package cn.hotdev.example.models.cache;

import cn.hotdev.example.constants.ConfigOption;
import cn.hotdev.example.utils.RedisTool;
import cn.hotdev.example.utils.StaticConfig;
import com.google.common.cache.CacheLoader;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.common.base.Preconditions.checkNotNull;


public class PersistObjectCacheLoader extends CacheLoader<String, String> {
    private static final StaticConfig config = StaticConfig.getInstance();
    private static final ExecutorService executor = Executors.newFixedThreadPool(config.getInt(ConfigOption.cache_reObj_executorPoolSize));

    // redis 永久缓存DB
    private int redisDb;
    // redis client
    private RedisTool redisTool;

    public PersistObjectCacheLoader(RedisTool redisTool, int redisDb) {
        this.redisTool = redisTool;
        this.redisDb = redisDb;
    }

    @Override
    public String load(String key) throws Exception {
        return loadOne(key, null, true);
    }

    @Override
    public ListenableFuture<String> reload(final String key, final String oldValue) throws Exception {
        checkNotNull(key);
        checkNotNull(oldValue);

        ListenableFutureTask<String> task = ListenableFutureTask.create(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return loadOne(key, oldValue, false);
            }
        });

        executor.execute(task);
        return task;
    }

    private String loadOne(String key, String oldValue, boolean sync) {

        if (key == null || key.isEmpty())
            return "";

        if (oldValue == null)
            oldValue = "";

        String value = redisTool.use(redisDb).get(key);

        if (value == null || value.isEmpty()) {
            value = oldValue;
        }

        return value;
    }

    @Override
    public Map<String, String> loadAll(Iterable<? extends String> keys) throws Exception {

        Map<String, String> map = new HashMap<String, String>();
        if (keys == null) {
            return map;
        }

        return redisTool.use(redisDb).getAll(keys);
    }
}
