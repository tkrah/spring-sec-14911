package com.example.demo.scope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.context.annotation.Scope;

@Scope(value = "thread")
@Retention(RetentionPolicy.RUNTIME)
public @interface ThreadScope {
}
