package com.example.demo.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class DemoServiceSessionHelper {

	private final ApplicationContext applicationContext;
	private final UserDetailsService userService;

	@Autowired
	public DemoServiceSessionHelper(ApplicationContext applicationContext, UserDetailsService userService) {
		this.applicationContext = applicationContext;
		this.userService = userService;
	}

	public void setUser(String username) {
		DemoServiceSessionData serviceSessionData = applicationContext.getBean(DemoServiceSessionData.class);
		serviceSessionData.setCurrentUser(userService.loadUserByUsername(username));
	}

	public void setUser(UserDetails user) {
		DemoServiceSessionData serviceSessionData = applicationContext.getBean(DemoServiceSessionData.class);
		serviceSessionData.setCurrentUser(user);
	}

	public void resetUser() {
		DemoServiceSessionData serviceSessionData = applicationContext.getBean(DemoServiceSessionData.class);
		serviceSessionData.setCurrentUser(null);
	}

	public void performCleanup() {
		DemoServiceSessionData serviceSessionData = applicationContext.getBean(DemoServiceSessionData.class);
		serviceSessionData.cleanup();
	}
}
