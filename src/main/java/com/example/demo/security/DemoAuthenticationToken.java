package com.example.demo.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.Transient;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Transient
public class DemoAuthenticationToken extends JwtAuthenticationToken {

	private final transient UserDetails principal;

	public DemoAuthenticationToken(Jwt jwt, UserDetails principal) {
		super(jwt);
		this.principal = principal;
	}

	public DemoAuthenticationToken(Jwt jwt, Collection<? extends GrantedAuthority> authorities, UserDetails principal) {
		super(jwt, authorities);
		this.principal = principal;
	}

	public DemoAuthenticationToken(Jwt jwt, Collection<? extends GrantedAuthority> authorities, String name,
		UserDetails principal) {
		super(jwt, authorities, name);
		this.principal = principal;
	}

	@Override
	public Object getPrincipal() {
		return this.principal;
	}
}
