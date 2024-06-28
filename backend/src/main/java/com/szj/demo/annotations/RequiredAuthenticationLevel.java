package com.szj.demo.annotations;

import com.szj.demo.enums.AuthenticationLevel;
import org.springframework.security.core.Authentication;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequiredAuthenticationLevel {
    AuthenticationLevel level();
}
