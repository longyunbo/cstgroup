package com.drag.cstgroup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@ComponentScan(basePackages = { "com.drag.cstgroup"})
@EnableJpaRepositories
@SpringBootApplication
@EnableScheduling
public class CstgroupApplication {

	public static void main(String[] args) {
		SpringApplication.run(CstgroupApplication.class, args);
	}
}
