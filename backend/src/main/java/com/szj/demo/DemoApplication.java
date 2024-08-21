package com.szj.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = {"com.szj.demo", "com.szj.demo.model", "com.szj.common.fileServer", "com.szj.common.fileServer.repository"})
@EnableJpaRepositories(basePackages = {"com.szj.demo.repository", "com.szj.common.fileServer.repository"})
@EntityScan(basePackages = {"com.szj.common.fileServer.model", "com.szj.demo.model"})
@EnableScheduling
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
