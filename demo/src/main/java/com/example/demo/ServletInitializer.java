package com.example.demo;


import org.apache.log4j.Logger;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import com.example.demo.kafka.KafkaApplication;
import com.example.demo.kafka.KafkaApplication.MessageListener;
import com.example.demo.kafka.KafkaApplication.MessageProducer;

public class ServletInitializer extends SpringBootServletInitializer {

	private final Logger logger = Logger.getLogger(this.getClass());
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		
		logger.debug( String.format("[aplication][?]",application) );
		
		/*
		 * ConfigurableApplicationContext context = application.context();
		 * //SpringApplication.run(KafkaApplication.class, args);
		 * 
		 * MessageProducer producer = context.getBean(MessageProducer.class);
		 * MessageListener listener = context.getBean(MessageListener.class);
		 * 
		 * logger.debug( String.format("{}",1) );
		 */
		
        //return application.sources(DemoApplication.class);
        return application.sources(DemoApplication.class);
	}

}
