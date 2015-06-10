package cn.hotdev.example.models.cache;


import cn.hotdev.example.constants.DefaultConfigOption;
import cn.hotdev.example.tools.ObjectTool;
import cn.hotdev.example.tools.RedisTool;
import cn.hotdev.example.tools.StaticConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class TempObjectCache implements ObjectCache {
    private static final Logger log = LoggerFactory.getLogger(TempObjectCache.class);
    private static final AtomicReference<TempObjectCache> instance = new AtomicReference<TempObjectCache>();
    private static final StaticConfig config = StaticConfig.getInstance();
    private Cache<String, Optional<Object>> cache;

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

    @Override
    public int getDb() {
        return redisDb;
    }

    @Override
    public Set<String> keys() {
        return cache.asMap().keySet();
    }

    @Override
    public long count() {
        return cache.size();
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        if (key == null || key.isEmpty())
            return null;

        Optional<Object> opt = cache.getIfPresent(key);

        if (opt == null)
            return null;

        if (!opt.isPresent()) {

            // try to read from backup redis
            log.info("read from redis: key={}, db={}", key, redisDb);

            String redisVal = redisTool.use(redisDb).get(key);

            // write in memory
            if (redisVal != null && !redisVal.isEmpty()) {

                // unserialize
                try {
                    T t = ObjectTool.unserialize(redisVal, type);

                    // write in memory
                    cache.put(key, Optional.fromNullable(t));

                    return t;

                } catch (IOException e) {
                    log.warn("object unserialize failed: key={}, db={}, err={}", key, redisDb, e.getMessage());
                }

            }

            return null;

        } else {
            return (T) opt.get();
        }
    }

    @Override
    public <T> Map<String, T> getAll(Iterable<? extends String> keys, Class<T> type) {
        Map<String, T> map = new HashMap<String, T>();
        if (keys == null) {
            return map;
        }

        ImmutableMap<String, Optional<Object>> allPresent = cache.getAllPresent(keys);

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


                    if (redisVal != null && !redisVal.isEmpty()) {

                        // unserialize
                        try {
                            T t = ObjectTool.unserialize(redisVal, type);
                            map.put(key, t);

                            // write in memory
                            cache.put(key, Optional.fromNullable(t));

                        } catch (IOException e) {
                            log.warn("object unserialize failed: key={}, db={}, err={}", key, redisDb, e.getMessage());
                        }

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

            // write in memory
            cache.put(key, Optional.fromNullable(obj));

            String value = ObjectTool.serialize(obj);

            if (value != null && !value.isEmpty()) {
                // write to redis for backup
                log.info("write to redis: key={}, db={}", key, redisDb);

                redisTool.use(redisDb).set(key, value, redisExpire);
            }

        } catch (JsonProcessingException e) {
            log.warn("object serialize failed: key={}, db={}, err={}", key, redisDb, e.getMessage());
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
