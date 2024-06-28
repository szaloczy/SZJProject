package com.szj.demo.configuration;

import com.szj.demo.annotations.RequiredAuthenticationLevelAspect;
import com.szj.demo.repository.UserRepository;
import com.szj.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextListener;

@Configuration
@RequiredArgsConstructor
@EnableAspectJAutoProxy
public class ApplicationConfig {

    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

    @Bean
    public RequiredAuthenticationLevelAspect requiredAuthenticationLevelAspect(UserService userService){
        return new RequiredAuthenticationLevelAspect(userService);
    }

}
