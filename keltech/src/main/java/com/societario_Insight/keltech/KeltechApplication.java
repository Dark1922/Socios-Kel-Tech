package com.societario_Insight.keltech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class KeltechApplication {

	public static void main(String[] args) {
		SpringApplication.run(KeltechApplication.class, args);
	}

}
