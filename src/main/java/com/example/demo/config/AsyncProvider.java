package com.example.demo.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.example.demo.concurrent.DemoDelegatingSecurityContextAsyncTaskExecutor;

@Configuration
public class AsyncProvider {
	@Bean
	@Primary
	public AsyncTaskExecutor demoTaskExecutor(@Qualifier("asyncTaskExecutor") final ThreadPoolTaskExecutor delegate,
		final ApplicationContext applicationContext) {
		return new DemoDelegatingSecurityContextAsyncTaskExecutor(delegate, applicationContext);
	}
}
