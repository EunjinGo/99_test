package com.example.demo.web;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.kafka.Greeting;
import com.example.demo.kafka.KafkaApplication.MessageListener;
import com.example.demo.kafka.KafkaApplication.MessageProducer;

@Controller
public class WebController {
	
	private final Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private ApplicationContext appContext;
	
	@Autowired
	private MessageProducer producerAuto;
	@RequestMapping("/test")
	public String test(HttpServletRequest request) {
	
		logger.debug("[START]================================================================");
		logger.debug(request.toString());
		
		Map map = new HashMap<String,Object>();
		map.put("getDisplayName", appContext.getDisplayName());
		map.put("getStartupDate", appContext.getStartupDate());
		logger.debug(map.toString());
		
		long seed = Calendar.getInstance().getTimeInMillis();
		Random random = new Random();
		random.setSeed(seed);
		
		long randomKey = random.nextLong();
		
		map.clear();
		map.put("randomKey",  randomKey);
		map.put("id", request.getRequestedSessionId());
		logger.debug(map.toString());
		
		
		//MessageProducer producer = appContext.getBean(MessageProducer.class);
		producerAuto.sendMessageToFiltered(map.toString());
        
        //logger.debug(String.format("[producer][%s]",  producer));
        
        logger.debug(String.format("[producerAuto][%s]",producerAuto));
		
        logger.debug("[END]================================================================");
		return "test";
	}
}
