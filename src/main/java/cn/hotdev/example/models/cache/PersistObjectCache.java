package cn.hotdev.example.models.cache;


import cn.hotdev.example.constants.ConfigOption;
import cn.hotdev.example.utils.RedisTool;
import cn.hotdev.example.utils.StaticConfig;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ExecutionError;
import com.google.common.util.concurrent.UncheckedExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class PersistObjectCache extends ObjectCache {
    private static final AtomicReference<PersistObjectCache> instance = new AtomicReference<PersistObjectCache>();
    private static final Logger log = LoggerFactory.getLogger(PersistObjectCache.class);
    private static final StaticConfig config = StaticConfig.getInstance();
    private LoadingCache<String, String> cache;

    // redis 永久缓存DB
    private static final int redisDb = config.getInt(ConfigOption.cache_reObj_redis_db);
    // redis client
    private RedisTool redisTool;

    public static PersistObjectCache getInstance() {
        if (instance.get() == null)
            instance.compareAndSet(null, new PersistObjectCache());
        return instance.get();
    }

    private PersistObjectCache() {

        redisTool = new RedisTool("PersistObjectCache");

        cache = CacheBuilder.newBuilder()
                .concurrencyLevel(config.getInt(ConfigOption.cache_reObj_concurrencyLevel))
                .maximumSize(config.getInt(ConfigOption.cache_reObj_size))
                .expireAfterAccess(config.getInt(ConfigOption.cache_reObj_expiration), TimeUnit.SECONDS)
                .refreshAfterWrite(config.getInt(ConfigOption.cache_reObj_refresh), TimeUnit.SECONDS)
                .build(new PersistObjectCacheLoader(redisTool, redisDb));
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
            return null;

        try {
            return cache.getUnchecked(key);
        } catch (UncheckedExecutionException e) {
            log.warn("unchecked exception: key={}, db={}, err={}", key, redisDb, e.getMessage());
        }

        return null;
    }

    public Map<String, String> getAll(Iterable<? extends String> keys) {

        Map<String, String> map = new HashMap<String, String>();
        if (keys == null) {
            return map;
        }

        try {
            map = cache.getAll(keys);
        } catch (ExecutionException | UncheckedExecutionException | ExecutionError e) {
            log.warn("unchecked exception: keys={}, db={}, err={}", keys, redisDb, e.getMessage());
        }

        // fill absent objects
        if (map.isEmpty()) {
            for (String key : keys) {
                map.put(key, "");
            }
        }

        return map;
    }


    public void invalidate(String key) {
        cache.invalidate(key);
    }

    public void cleanUp() {
        cache.cleanUp();
        redisTool.close();
    }
}
