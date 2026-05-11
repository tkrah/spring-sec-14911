package com.example.demo.concurrent;

import java.util.concurrent.Callable;

import org.springframework.context.ApplicationContext;
import org.springframework.security.concurrent.DelegatingSecurityContextCallable;
import org.springframework.security.concurrent.DelegatingSecurityContextRunnable;
import org.springframework.security.core.context.SecurityContext;

public abstract class DemoAbstractDelegatingSecurityContextSupport {
	private final SecurityContext securityContext;

	private final ApplicationContext applicationContext;

	/**
	 * Creates a new {@link DemoAbstractDelegatingSecurityContextSupport} that uses the specified
	 * {@link SecurityContext}.
	 *
	 * @param securityContext the {@link SecurityContext} to use for each {@link DelegatingSecurityContextRunnable} and
	 *            each {@link DelegatingSecurityContextCallable} or null to default to the current
	 *            {@link SecurityContext}.
	 */
	DemoAbstractDelegatingSecurityContextSupport(SecurityContext securityContext,
												 ApplicationContext applicationContext) {
		this.securityContext = securityContext;
		this.applicationContext = applicationContext;
	}

	protected final Runnable wrap(Runnable delegate) {
		if (this.securityContext != null) {
			return new DemoDelegatingSecurityContextRunnable(delegate, this.securityContext, this.applicationContext);
		} else {
			return new DemoDelegatingSecurityContextRunnable(delegate, this.applicationContext);
		}
	}

	protected final <T> Callable<T> wrap(Callable<T> delegate) {
		if (this.securityContext != null) {
			return new DemoDelegatingSecurityContextCallable<>(delegate, this.securityContext,
				this.applicationContext);
		} else {
			return new DemoDelegatingSecurityContextCallable<>(delegate, this.applicationContext);
		}
	}
}
