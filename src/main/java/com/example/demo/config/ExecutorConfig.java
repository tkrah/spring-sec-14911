package com.example.demo.config;

import java.util.concurrent.ForkJoinPool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.example.demo.concurrent.DemoDelegatingSecurityContextForkJoinPool;

@Configuration
public class ExecutorConfig {

	@Bean
	public ThreadPoolTaskExecutor asyncTaskExecutor(@Value("${demo.max.parallel.workers}") int maxParallelWorkers) {
		final ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(maxParallelWorkers);
		taskExecutor.setMaxPoolSize(maxParallelWorkers * 2);
		return taskExecutor;
	}

	@Bean
	public ForkJoinPool parallelWorkExecutor(@Value("${demo.max.parallel.workers}") int maxParallelWorkers,
		ApplicationContext context) {
		final ForkJoinWorkerFactory factory = new ForkJoinWorkerFactory("demo-forkjoin-worker-");
		return new DemoDelegatingSecurityContextForkJoinPool(maxParallelWorkers, factory, null, false, context);
	}

}
