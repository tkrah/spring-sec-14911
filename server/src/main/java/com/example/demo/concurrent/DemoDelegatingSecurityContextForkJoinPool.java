package com.example.demo.concurrent;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.util.Assert;

public class DemoDelegatingSecurityContextForkJoinPool extends ForkJoinPool {

	private final ApplicationContext applicationContext;

	public DemoDelegatingSecurityContextForkJoinPool(int parallelism, ForkJoinWorkerThreadFactory factory,
													 UncaughtExceptionHandler handler, boolean asyncMode, ApplicationContext applicationContext) {
		super(parallelism, factory, handler, asyncMode);
		this.applicationContext = applicationContext;
	}

	@Override
	public <T> ForkJoinTask<T> submit(Callable<T> task) {
		return super.submit(wrap(task));
	}

	@Override
	public ForkJoinTask<?> submit(Runnable task) {
		return super.submit(wrap(task));
	}

	protected final Runnable wrap(Runnable delegate) {
		return create(delegate, null, SecurityContextHolder.getContextHolderStrategy(), this.applicationContext);
	}

	protected final <T> Callable<T> wrap(Callable<T> delegate) {
		return create(delegate, null, SecurityContextHolder.getContextHolderStrategy(), this.applicationContext);
	}

	static Runnable create(Runnable delegate, SecurityContext securityContext,
		SecurityContextHolderStrategy securityContextHolderStrategy, ApplicationContext applicationContext) {
		Assert.notNull(delegate, "delegate cannot be  null");
		Assert.notNull(securityContextHolderStrategy, "securityContextHolderStrategy cannot be null");
		DemoDelegatingSecurityContextRunnable runnable = securityContext != null
			? new DemoDelegatingSecurityContextRunnable(delegate, securityContext, applicationContext)
			: new DemoDelegatingSecurityContextRunnable(delegate, applicationContext);
		runnable.setSecurityContextHolderStrategy(securityContextHolderStrategy);
		return runnable;
	}

	static <V> Callable<V> create(Callable<V> delegate, SecurityContext securityContext,
		SecurityContextHolderStrategy securityContextHolderStrategy, ApplicationContext applicationContext) {
		Assert.notNull(delegate, "delegate cannot be null");
		Assert.notNull(securityContextHolderStrategy, "securityContextHolderStrategy cannot be null");
		DemoDelegatingSecurityContextCallable<V> callable = securityContext != null
			? new DemoDelegatingSecurityContextCallable<>(delegate, securityContext, applicationContext)
			: new DemoDelegatingSecurityContextCallable<>(delegate, applicationContext);
		callable.setSecurityContextHolderStrategy(securityContextHolderStrategy);
		return callable;
	}
}
