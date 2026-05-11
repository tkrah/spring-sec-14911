package com.example.demo.concurrent;

import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.concurrent.DelegatingSecurityContextRunnable;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.task.DelegatingSecurityContextTaskExecutor;

public class DemoDelegatingSecurityContextTaskExecutor extends DemoDelegatingSecurityContextExecutor
	implements TaskExecutor {

	/**
	 * Creates a new {@link DelegatingSecurityContextTaskExecutor} that uses the specified {@link SecurityContext}.
	 *
	 * @param delegateTaskExecutor the {@link TaskExecutor} to delegate to. Cannot be null.
	 * @param securityContext the {@link SecurityContext} to use for each {@link DelegatingSecurityContextRunnable}
	 */
	public DemoDelegatingSecurityContextTaskExecutor(TaskExecutor delegateTaskExecutor,
													 SecurityContext securityContext, ApplicationContext applicationContext) {
		super(delegateTaskExecutor, securityContext, applicationContext);
	}

	/**
	 * Creates a new {@link DelegatingSecurityContextTaskExecutor} that uses the current {@link SecurityContext} from
	 * the {@link SecurityContextHolder}.
	 *
	 * @param delegate the {@link TaskExecutor} to delegate to. Cannot be null.
	 */
	public DemoDelegatingSecurityContextTaskExecutor(TaskExecutor delegate, ApplicationContext applicationContext) {
		this(delegate, null, applicationContext);
	}

}
