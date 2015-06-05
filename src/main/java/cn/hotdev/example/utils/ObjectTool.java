package cn.hotdev.example.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class ObjectTool {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> String serialize(T t) throws JsonProcessingException {
        if (t == null)
            return null;
        return mapper.writeValueAsString(t);
    }

    public static <T> T unserialize(String value, Class<T> type) throws IOException {
        if (value == null)
            return null;
        return mapper.readValue(value, type);
    }
}
