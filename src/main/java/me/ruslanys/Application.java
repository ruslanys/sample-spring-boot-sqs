package me.ruslanys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static final String MANAGER_QUEUE = "mQueue";
    public static final String PROCESSOR_QUEUE = "cQueue";

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
