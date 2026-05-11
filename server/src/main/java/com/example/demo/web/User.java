package com.example.demo.web;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class User {

	public String getName() {
		return "Hello World";
	}

}
