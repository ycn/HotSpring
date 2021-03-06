package cn.hotdev.example.constants;


import cn.hotdev.example.models.enums.DefaultValueEnum;

public enum DefaultConfigOption implements DefaultValueEnum {

    global_redis_host("localhost"),
    global_redis_port("6379"),
    cache_obj_size("1000"),
    cache_obj_concurrencyLevel("10"),
    cache_obj_expiration("60"),
    cache_obj_redis_db("22"),
    cache_obj_redis_expiration("3600"),
    cache_reObj_size("1000"),
    cache_reObj_concurrencyLevel("10"),
    cache_reObj_expiration("600"),
    cache_reObj_refresh("60"),
    cache_reObj_executorPoolSize("10"),
    cache_reObj_redis_db("20"),
    app_name("app");

    private String defaultValue;

    private DefaultConfigOption(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }
}
