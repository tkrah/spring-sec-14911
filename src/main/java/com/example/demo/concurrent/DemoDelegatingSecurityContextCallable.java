package com.example.demo.concurrent;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.concurrent.DelegatingSecurityContextCallable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import com.example.demo.scope.ThreadScopeImpl;
import com.example.demo.util.DemoServiceSessionHelper;

public class DemoDelegatingSecurityContextCallable<V> implements Callable<V> {
	private final Callable<V> delegate;

	private final boolean explicitSecurityContextProvided;

	/**
	 * The {@link SecurityContext} that the delegate {@link Callable} will be ran as.
	 */
	private SecurityContext delegateSecurityContext;

	private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
		.getContextHolderStrategy();

	/**
	 * The {@link SecurityContext} that was on the {@link SecurityContextHolder} prior to being set to the
	 * delegateSecurityContext.
	 */
	private SecurityContext originalSecurityContext;

	private final ApplicationContext applicationContext;

	/**
	 * Creates a new {@link DelegatingSecurityContextCallable} with a specific {@link SecurityContext}.
	 *
	 * @param delegate the delegate {@link DelegatingSecurityContextCallable} to run with the specified
	 *            {@link SecurityContext}. Cannot be null.
	 * @param securityContext the {@link SecurityContext} to establish for the delegate {@link Callable}. Cannot be
	 *            null.
	 */
	public DemoDelegatingSecurityContextCallable(Callable<V> delegate, SecurityContext securityContext,
		final ApplicationContext applicationContext) {
		this(delegate, securityContext, true, applicationContext);
	}

	/**
	 * Creates a new {@link DelegatingSecurityContextCallable} with the {@link SecurityContext} from the
	 * {@link SecurityContextHolder}.
	 *
	 * @param delegate the delegate {@link Callable} to run under the current {@link SecurityContext}. Cannot be null.
	 */
	public DemoDelegatingSecurityContextCallable(Callable<V> delegate, ApplicationContext applicationContext) {
		this(delegate, SecurityContextHolder.getContext(), false, applicationContext);
	}

	private DemoDelegatingSecurityContextCallable(Callable<V> delegate, SecurityContext securityContext,
		boolean explicitSecurityContextProvided, ApplicationContext applicationContext) {
		Assert.notNull(delegate, "delegate cannot be null");
		Assert.notNull(securityContext, "securityContext cannot be null");
		this.delegate = delegate;
		this.delegateSecurityContext = securityContext;
		this.explicitSecurityContextProvided = explicitSecurityContextProvided;
		this.applicationContext = applicationContext;
	}

	@Override
	public V call() throws Exception {
		this.originalSecurityContext = this.securityContextHolderStrategy.getContext();
		try {
			this.securityContextHolderStrategy.setContext(this.delegateSecurityContext);
			// ISSUE-14911
			prepare();
			return this.delegate.call();
		} finally {
			// ISSUE-14911
			cleanup();
			SecurityContext emptyContext = this.securityContextHolderStrategy.createEmptyContext();
			if (emptyContext.equals(this.originalSecurityContext)) {
				this.securityContextHolderStrategy.clearContext();
			} else {
				this.securityContextHolderStrategy.setContext(this.originalSecurityContext);
			}
			this.originalSecurityContext = null;
		}
	}

	private void prepare() {
		if (this.delegateSecurityContext != null) {
			final DemoServiceSessionHelper helper =
				getApplicationContext().getBean(DemoServiceSessionHelper.class);
			final Authentication authentication = this.delegateSecurityContext.getAuthentication();
			if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
				if (authentication.getPrincipal() instanceof UserDetails userDetails) {
					helper.setUser(userDetails);
				} else {
					helper.setUser(authentication.getName());
				}
			}
		}
	}

	private void cleanup() {
		clearScope();
	}

	/**
	 * Sets the {@link SecurityContextHolderStrategy} to use. The default action is to use the
	 * {@link SecurityContextHolderStrategy} stored in {@link SecurityContextHolder}.
	 *
	 * @since 5.8
	 */
	public void setSecurityContextHolderStrategy(SecurityContextHolderStrategy securityContextHolderStrategy) {
		Assert.notNull(securityContextHolderStrategy, "securityContextHolderStrategy cannot be null");
		this.securityContextHolderStrategy = securityContextHolderStrategy;
		if (!this.explicitSecurityContextProvided) {
			this.delegateSecurityContext = securityContextHolderStrategy.getContext();
		}
	}

	@Override
	public String toString() {
		return this.delegate.toString();
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	private void clearScope() {
		ConfigurableListableBeanFactory factory =
			(ConfigurableListableBeanFactory) getApplicationContext().getAutowireCapableBeanFactory();
		ThreadScopeImpl scope = (ThreadScopeImpl) factory.getRegisteredScope("thread");
		if (scope != null) {
			scope.clearScope();
		}
	}

}
