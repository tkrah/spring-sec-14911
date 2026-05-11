package com.example.demo.util;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.demo.scope.ThreadScope;

@Component
@ThreadScope
public class DemoServiceSessionData {
	private UserDetails currentUser;

	public UserDetails getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(UserDetails currentUser) {
		this.currentUser = currentUser;
	}

	public void cleanup() {
		currentUser = null;
	}
}
