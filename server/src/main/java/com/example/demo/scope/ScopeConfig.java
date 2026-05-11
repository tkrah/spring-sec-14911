package com.example.demo.scope;

import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ScopeConfig {

    @Bean
    public static CustomScopeConfigurer customScope() {
        CustomScopeConfigurer configurer = new CustomScopeConfigurer();
        Map<String, Object> threadScope = new HashMap<>();
        threadScope.put("thread", new ThreadScopeImpl());
        configurer.setScopes(threadScope);

        return configurer;
    }
}

