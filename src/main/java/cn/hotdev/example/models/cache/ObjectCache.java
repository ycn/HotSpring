package cn.hotdev.example.models.cache;


import java.util.Map;
import java.util.Set;

public interface ObjectCache {

    public int getDb();

    public Set<String> keys();

    public long count();

    public <T> T get(String key, Class<T> type);

    public <T> Map<String, T> getAll(Iterable<? extends String> keys, Class<T> type);
}
