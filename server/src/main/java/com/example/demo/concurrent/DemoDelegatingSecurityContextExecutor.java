package com.example.demo.concurrent;

import java.util.concurrent.Executor;

import org.springframework.context.ApplicationContext;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutor;
import org.springframework.security.concurrent.DelegatingSecurityContextRunnable;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

public class DemoDelegatingSecurityContextExecutor extends DemoAbstractDelegatingSecurityContextSupport
	implements Executor {

	private final Executor delegate;

	/**
	 * Creates a new {@link DelegatingSecurityContextExecutor} that uses the specified {@link SecurityContext}.
	 *
	 * @param delegateExecutor the {@link Executor} to delegate to. Cannot be null.
	 * @param securityContext the {@link SecurityContext} to use for each {@link DelegatingSecurityContextRunnable} or
	 *            null to default to the current {@link SecurityContext}
	 */
	public DemoDelegatingSecurityContextExecutor(Executor delegateExecutor, SecurityContext securityContext,
												 ApplicationContext applicationContext) {
		super(securityContext, applicationContext);
		Assert.notNull(delegateExecutor, "delegateExecutor cannot be null");
		this.delegate = delegateExecutor;
	}

	/**
	 * Creates a new {@link DelegatingSecurityContextExecutor} that uses the current {@link SecurityContext} from the
	 * {@link SecurityContextHolder} at the time the task is submitted.
	 *
	 * @param delegate the {@link Executor} to delegate to. Cannot be null.
	 */
	public DemoDelegatingSecurityContextExecutor(Executor delegate, ApplicationContext applicationContext) {
		this(delegate, null, applicationContext);
	}

	@Override
	public final void execute(Runnable task) {
		this.delegate.execute(wrap(task));
	}

	protected final Executor getDelegateExecutor() {
		return this.delegate;
	}
}
