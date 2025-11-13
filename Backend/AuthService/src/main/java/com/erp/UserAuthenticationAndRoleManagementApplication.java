package com.erp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class UserAuthenticationAndRoleManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserAuthenticationAndRoleManagementApplication.class, args);
	}

}
