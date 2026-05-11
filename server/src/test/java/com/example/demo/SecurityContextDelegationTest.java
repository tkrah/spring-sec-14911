package com.example.demo;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;

import com.example.demo.concurrent.DemoDelegatingSecurityContextAsyncTaskExecutor;
import com.example.demo.security.DemoAuthenticationToken;
import com.example.demo.util.DemoServiceSessionData;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SecurityContextDelegationTest {
	private final DemoDelegatingSecurityContextAsyncTaskExecutor demoTaskExecutor;

	private final UserDetailsService userDetailsService;

	private final ApplicationContext applicationContext;

	@Autowired
	public SecurityContextDelegationTest(UserDetailsService userDetailsService, ApplicationContext applicationContext) {
		final ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(1);
		taskExecutor.setMaxPoolSize(1);
		taskExecutor.initialize();
		this.demoTaskExecutor = new DemoDelegatingSecurityContextAsyncTaskExecutor(taskExecutor, applicationContext);
		this.userDetailsService = userDetailsService;
		this.applicationContext = applicationContext;
	}

	@Test
	void testDelegation() throws ExecutionException, InterruptedException {
		String userName = "admin";
		UserDetails principal = userDetailsService.loadUserByUsername(userName);
		Jwt jwt = Jwt.withTokenValue(UUID.randomUUID().toString())
			.header("alg", "none")
			.claim("sub", userName)
			.build();

		DemoAuthenticationToken authentication = new DemoAuthenticationToken(jwt, principal);
		authentication.setAuthenticated(true);

		SecurityContext sc = SecurityContextHolder.createEmptyContext();
		sc.setAuthentication(authentication);
		SecurityContextHolder.setContext(sc);

		final Future<String> submitAdmin = demoTaskExecutor.submit(() -> {
			final DemoServiceSessionData bean = applicationContext.getBean(DemoServiceSessionData.class);
			assertThat(bean.getCurrentUser()).isNotNull();
			return bean.getCurrentUser().getUsername();
		});

		// wait for completion
		String s = submitAdmin.get();
		assertThat(s).isEqualTo(userName);

		userName = "gast";
		principal = userDetailsService.loadUserByUsername(userName);
		jwt = Jwt.withTokenValue(UUID.randomUUID().toString())
			.header("alg", "none")
			.claim("sub", userName)
			.build();

		authentication = new DemoAuthenticationToken(jwt, principal);
		authentication.setAuthenticated(true);

		sc = SecurityContextHolder.createEmptyContext();
		sc.setAuthentication(authentication);
		SecurityContextHolder.setContext(sc);

		final Future<String> submitGast = demoTaskExecutor.submit(() -> {
			final DemoServiceSessionData bean = applicationContext.getBean(DemoServiceSessionData.class);
			assertThat(bean.getCurrentUser()).isNotNull();
			return bean.getCurrentUser().getUsername();
		});

		// wait for completion
		s = submitGast.get();
		assertThat(s).isEqualTo(userName);
	}
}
