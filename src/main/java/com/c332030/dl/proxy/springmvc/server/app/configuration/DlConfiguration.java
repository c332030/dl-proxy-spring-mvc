package com.c332030.dl.proxy.springmvc.server.app.configuration;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

/**
 * <p>
 * Description: DlConfiguration
 * </p>
 *
 * @author c332030
 * @version 1.0
 */
@Configuration
public class DlConfiguration {

    @Bean
    public static OkHttpClient okHttpClient() {
        return new OkHttpClient().newBuilder()
            .retryOnConnectionFailure(false)
            .connectionPool(pool())
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .build();
    }

    @Bean
    public static ConnectionPool pool() {
        return new ConnectionPool(50, 5, TimeUnit.MINUTES);
    }

}
