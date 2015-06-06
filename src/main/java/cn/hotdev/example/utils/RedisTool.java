package cn.hotdev.example.utils;


import cn.hotdev.example.constants.ConfigOption;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class RedisTool {
    private static final Logger log = LoggerFactory.getLogger(RedisTool.class);
    private static final StaticConfig config = StaticConfig.getInstance();

    private static final JedisPool pool = new JedisPool(
            new JedisPoolConfig(),
            config.get(ConfigOption.global_redis_host),
            config.getInt(ConfigOption.global_redis_port));

    // 报警阈值
    private static final int retryWarnValue = 20;

    @Getter
    private String id;
    @Getter
    private Jedis client;
    @Getter
    private int db;
    @Getter
    private int retry = 0;

    public RedisTool(String id) {
        this.id = id;
        reconnect();
    }

    public RedisTool use(int db) {
        this.db = db > 0 ? db : 0;
        return this;
    }

    public String get(String key) {
        if (client == null)
            reconnect();

        String value = null;
        if (client != null) {

            log.info("redis({}): get {}, db={}", id, key, db);

            try {
                Pipeline p = client.pipelined();
                p.select(db);
                Response<String> response = p.get(key);
                p.sync();

                value = response.get();

            } catch (JedisException e) {
                client = null;
            }
        }
        return value;
    }

    public Map<String, String> getAll(Iterable<? extends String> keys) {
        if (client == null)
            reconnect();

        Map<String, Response<String>> futureMap = new HashMap<String, Response<String>>();
        Map<String, String> map = new LinkedHashMap<String, String>();

        if (client != null) {

            log.info("redis({}): getAll {}, db={}", id, keys, db);

            try {
                Pipeline p = client.pipelined();
                p.select(db);

                for (String key : keys) {
                    Response<String> response = p.get(key);
                    futureMap.put(key, response);
                }

                p.sync();

                if (!futureMap.isEmpty()) {
                    for (String key : futureMap.keySet()) {
                        Response<String> response = futureMap.get(key);
                        String value = response.get();
                        // never got 'null' in cache value map
                        map.put(key, value == null ? "" : value);
                    }
                }

            } catch (JedisException e) {
                client = null;
            }
        }

        return map;
    }

    public RedisTool set(String key, String value, int seconds) {
        if (client == null)
            reconnect();

        if (client != null) {

            log.info("redis({}): set {}, db={}", id, key, db);

            try {
                Pipeline p = client.pipelined();
                p.select(db);

                if (seconds > 0)
                    p.setex(key, seconds, value);
                else
                    p.set(key, value);

                p.sync();

            } catch (JedisException e) {
                client = null;
            }
        }

        return this;
    }

    private void reconnect() {

        close();

        // retry
        try {

            client = pool.getResource();

            log.info("redis({}) connected.", id);

        } catch (JedisConnectionException e) {
            retry++;
            if (retry > retryWarnValue) {
                log.warn("redis({}) maybe down: retry={}", id, retry);
            }
        }
    }

    public void close() {
        if (client != null) {
            client.close();
        }
    }
}
