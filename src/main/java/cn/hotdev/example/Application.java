package cn.hotdev.example;

import cn.hotdev.example.models.cache.PersistObjectCache;
import cn.hotdev.example.models.cache.TempObjectCache;
import cn.hotdev.example.tools.RedisTool;
import com.mashape.unirest.http.Unirest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PreDestroy;
import java.io.IOException;


@EnableCaching
@SpringBootApplication // same as @Configuration @EnableAutoConfiguration @ComponentScan
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    @Bean
    public CacheManager cacheManager() {
        return new EhCacheCacheManager(ehCacheCacheManager().getObject());
    }

    @Bean
    public EhCacheManagerFactoryBean ehCacheCacheManager() {
        EhCacheManagerFactoryBean cmfb = new EhCacheManagerFactoryBean();
        cmfb.setConfigLocation(new ClassPathResource("ehcache.xml"));
        cmfb.setShared(true);
        return cmfb;
    }

    @PreDestroy
    public void destroy() {
        // release all temp caches & temp cache redis client
        TempObjectCache tempObjectCache = TempObjectCache.getInstance();
        tempObjectCache.cleanUp();

        // release all persist caches & persist cache redis client
        PersistObjectCache persistObjectCache = PersistObjectCache.getInstance();
        persistObjectCache.cleanUp();

        // destroy redis client pool
        RedisTool.destroy();

        // Shutdown UniRest
        try {
            Unirest.shutdown();
        } catch (IOException ignore) {
        }
    }
}
