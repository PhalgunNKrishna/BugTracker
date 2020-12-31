package com.bugtracker.bug;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BugApplication {

	public static void main(String[] args) {
		System.out.println("start");
		SpringApplication.run(BugApplication.class, args);
	}

}
