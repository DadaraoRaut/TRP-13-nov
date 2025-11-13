package com.erp.billing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@EnableDiscoveryClient
@SpringBootApplication
public class BillingModuleApplication {

	public static void main(String[] args) {

        SpringApplication.run(BillingModuleApplication.class, args);
	}

}

