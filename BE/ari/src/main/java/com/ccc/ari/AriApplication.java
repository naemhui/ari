package com.ccc.ari;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication()
public class AriApplication {

	public static void main(String[] args) {
		SpringApplication.run(AriApplication.class, args);
	}

}
