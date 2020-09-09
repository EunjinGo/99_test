package com.example.demo;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	private final Logger logger = Logger.getLogger(this.getClass());
	
    public static void main(String[] args) throws Exception {

    	System.out.println("DemoApplication.main[START]==================================================================================");
        SpringApplication.run(DemoApplication.class, args);
    	System.out.println("DemoApplication.main[END]==================================================================================");

    }
}
