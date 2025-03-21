package com.example.demo.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.springframework.context.ApplicationContext;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.security.concurrent.DelegatingSecurityContextCallable;
import org.springframework.security.concurrent.DelegatingSecurityContextRunnable;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

public class DemoDelegatingSecurityContextAsyncTaskExecutor extends DemoDelegatingSecurityContextTaskExecutor
	implements AsyncTaskExecutor {

	/**
	 * Creates a new {@link DelegatingSecurityContextAsyncTaskExecutor} that uses the specified {@link SecurityContext}.
	 *
	 * @param delegateAsyncTaskExecutor the {@link AsyncTaskExecutor} to delegate to. Cannot be null.
	 * @param securityContext the {@link SecurityContext} to use for each {@link DelegatingSecurityContextRunnable} and
	 *            {@link DelegatingSecurityContextCallable}
	 */
	public DemoDelegatingSecurityContextAsyncTaskExecutor(AsyncTaskExecutor delegateAsyncTaskExecutor,
														  SecurityContext securityContext, ApplicationContext applicationContext) {
		super(delegateAsyncTaskExecutor, securityContext, applicationContext);
	}

	/**
	 * Creates a new {@link DelegatingSecurityContextAsyncTaskExecutor} that uses the current {@link SecurityContext}.
	 *
	 * @param delegateAsyncTaskExecutor the {@link AsyncTaskExecutor} to delegate to. Cannot be null.
	 */
	public DemoDelegatingSecurityContextAsyncTaskExecutor(AsyncTaskExecutor delegateAsyncTaskExecutor,
														  ApplicationContext applicationContext) {
		this(delegateAsyncTaskExecutor, null, applicationContext);
	}

	@Override
	@SuppressWarnings("deprecation")
	public final void execute(Runnable task, long startTimeout) {
		getDelegate().execute(wrap(task));
	}

	@Override
	public final Future<?> submit(Runnable task) {
		return getDelegate().submit(wrap(task));
	}

	@Override
	public final <T> Future<T> submit(Callable<T> task) {
		return getDelegate().submit(wrap(task));
	}

	private AsyncTaskExecutor getDelegate() {
		return (AsyncTaskExecutor) getDelegateExecutor();
	}
}
