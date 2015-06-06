package cn.hotdev.example.models.cache;


import cn.hotdev.example.utils.ObjectTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ObjectCache {

    protected static final Logger log = LoggerFactory.getLogger(ObjectCache.class);

    abstract public int getDb();

    abstract public String get(String key);

    abstract public Map<String, String> getAll(Iterable<? extends String> keys);


    public <T> T get(String key, Class<T> type) {

        if (key == null || key.isEmpty() || type == null)
            return null;

        String value = get(key);
        T t = null;
        try {
            t = ObjectTool.unserialize(value, type);
        } catch (IOException e) {
            log.error("object unserialize failed: key={}, db={}, err={}", key, getDb(), e.getMessage());
        }
        return t;
    }

    public <T> Map<String, T> getAll(Iterable<? extends String> keys, Class<T> type) {

        Map<String, T> map = new HashMap<String, T>();
        if (keys == null || type == null) {
            return map;
        }

        Map<String, String> strMap = getAll(keys);
        if (strMap.isEmpty()) {
            return map;
        }

        List<String> errKeys = new ArrayList<String>();

        for (String key : strMap.keySet()) {
            String value = strMap.get(key);

            try {
                T t = ObjectTool.unserialize(value, type);
                map.put(key, t);
            } catch (IOException e) {
                errKeys.add(key);
            }
        }

        if (!errKeys.isEmpty()) {
            log.error("object unserialize failed: keys={}, db={}", errKeys, getDb());
        }

        return map;
    }
}
