package cn.hotdev.example.models.cache;


import cn.hotdev.example.constants.DefaultConfigOption;
import cn.hotdev.example.utils.ObjectTool;
import cn.hotdev.example.utils.RedisTool;
import cn.hotdev.example.utils.StaticConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class TempObjectCache extends ObjectCache {
    private static final Logger log = LoggerFactory.getLogger(TempObjectCache.class);
    private static final AtomicReference<TempObjectCache> instance = new AtomicReference<TempObjectCache>();
    private static final StaticConfig config = StaticConfig.getInstance();
    private Cache<String, String> cache;

    // redis 临时缓存DB
    private static final int redisDb = config.getInt(DefaultConfigOption.cache_obj_redis_db);
    // redis 临时缓存过期时间(秒)
    private static final int redisExpire = config.getInt(DefaultConfigOption.cache_obj_redis_expiration);
    // redis client
    private RedisTool redisTool;

    public static TempObjectCache getInstance() {
        if (instance.get() == null)
            instance.compareAndSet(null, new TempObjectCache());
        return instance.get();
    }

    private TempObjectCache() {
        cache = CacheBuilder.newBuilder()
                .concurrencyLevel(config.getInt(DefaultConfigOption.cache_obj_concurrencyLevel))
                .maximumSize(config.getInt(DefaultConfigOption.cache_obj_size))
                .expireAfterWrite(config.getInt(DefaultConfigOption.cache_obj_expiration), TimeUnit.SECONDS)
                .build();

        redisTool = new RedisTool("TempObjectCache");
    }

    public Set<String> keys() {
        return cache.asMap().keySet();
    }

    @Override
    public int getDb() {
        return redisDb;
    }

    public String get(String key) {

        if (key == null || key.isEmpty())
            return "";

        String value = cache.getIfPresent(key);

        if (value != null && !value.isEmpty()) {

            // try to read from backup redis
            log.info("read from redis: key={}, db={}", key, redisDb);

            String redisVal = redisTool.use(redisDb).get(key);

            // write in memory
            if (redisVal != null && !redisVal.isEmpty()) {
                cache.put(key, redisVal);
            }

            return redisVal;
        } else {
            return null;
        }
    }

    public Map<String, String> getAll(Iterable<? extends String> keys) {

        Map<String, String> map = new HashMap<String, String>();
        if (keys == null) {
            return map;
        }

        ImmutableMap<String, String> allPresent = cache.getAllPresent(keys);

        List<String> missingKeys = new ArrayList<String>();
        for (String key : keys) {
            if (!allPresent.containsKey(key)) {
                missingKeys.add(key);
            }
        }

        // read missing from backup redis
        if (!missingKeys.isEmpty()) {
            log.info("read from redis: keys={}, db={}", missingKeys, redisDb);

            Map<String, String> missingMap = redisTool.use(redisDb).getAll(missingKeys);

            if (!missingMap.isEmpty()) {
                for (String key : missingMap.keySet()) {
                    String redisVal = missingMap.get(key);

                    // write in memory
                    if (redisVal != null && !redisVal.isEmpty()) {
                        cache.put(key, redisVal);
                        map.put(key, redisVal);
                    }
                }
            }
        }

        return map;
    }

    public void set(String key, Object obj) {

        if (key == null || key.isEmpty() || obj == null)
            return;

        try {

            String value;

            if (obj instanceof String) {
                value = (String) obj;
                if (value.isEmpty())
                    return;
            } else {
                value = ObjectTool.serialize(obj);
            }

            cache.put(key, value);

            // write to redis for backup
            log.info("write to redis: key={}, db={}", key, redisDb);

            redisTool.use(redisDb).set(key, value, redisExpire);

        } catch (JsonProcessingException e) {
            log.error("object serialize failed: key={}, db={}, err={}", key, redisDb, e.getMessage());
        }
    }

    public void invalidate(String key) {
        cache.invalidate(key);
    }

    public void cleanUp() {
        cache.cleanUp();
        redisTool.close();
    }
}
