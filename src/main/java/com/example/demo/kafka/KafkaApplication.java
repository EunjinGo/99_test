package com.example.demo.kafka;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
public class KafkaApplication {

	private static final Logger logger = Logger.getLogger(KafkaApplication.class);

	private static final List list = new ArrayList<String>();
	
	public KafkaApplication() {
    	logger.debug("################################################################################################");
    	logger.debug("###KafkaApplication                                                                          ###");
    	logger.debug("################################################################################################");
		
	}
	
    @Bean
    public MessageProducer messageProducer() {
    	logger.debug("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    	logger.debug("<<<messageProducer                                                                           <<<");
    	logger.debug("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    	
    	MessageProducer producer = new MessageProducer();
    	logger.debug(String.format("[return][producer][%s]", producer));
    	
    	return producer;
    }

    @Bean
    public MessageListener messageListener() {
    	logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    	logger.debug(">>>messageListener                                                                           >>>");
    	logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

    	MessageListener listner = new MessageListener();
    	logger.debug(String.format("[return][listner][%s]", listner));

    	return listner;
    }

    public static class MessageProducer {

    	private final Logger logger = Logger.getLogger(MessageProducer.class);
    	
        @Autowired
        private KafkaTemplate<String, String> kafkaTemplate;

        @Autowired
        private KafkaTemplate<String, Greeting> greetingKafkaTemplate;

        @Value(value = "${message.topic.name}")
        private String topicName;

        @Value(value = "${partitioned.topic.name}")
        private String partitionedTopicName;

        @Value(value = "${filtered.topic.name}")
        private String filteredTopicName;

        @Value(value = "${greeting.topic.name}")
        private String greetingTopicName;

        @Value(value = "${eunjin.topic.name}")
        private String eunjinTopicName;
        
        public void sendMessageEunjin(String message) {
        	logger.debug(message);
        	logger.debug(String.format("[getDefaultTopic][%s]", kafkaTemplate.getDefaultTopic()));
        	
            kafkaTemplate.send(eunjinTopicName, message);
        }
        /*
        public void sendMessage(String message) {

        	logger.debug(message);
        	
            ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topicName, message);

            future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

                @Override
                public void onSuccess(SendResult<String, String> result) {
                    logger.debug("Sent message=[" + message + "] with offset=[" + result.getRecordMetadata()
                        .offset() + "]");
                }

                @Override
                public void onFailure(Throwable ex) {
                    logger.debug("Unable to send message=[" + message + "] due to : " + ex.getMessage());
                }
            });
        }

        public void sendMessageToPartition(String message, int partition) {
            kafkaTemplate.send(partitionedTopicName, partition, null, message);
        }

        public void sendMessageToFiltered(String message) {
            kafkaTemplate.send(filteredTopicName, message);
        }

        public void sendGreetingMessage(Greeting greeting) {
            greetingKafkaTemplate.send(greetingTopicName, greeting);
        }
        */
    }

    public static class MessageListener {

    	private static final Logger logger = Logger.getLogger(MessageListener.class);
    	
    	
    	@KafkaListener(topics = "${eunjin.topic.name}", containerFactory = "eunjinKafkaListenerContainerFactory")
        public void listenEunjin(String message) {
	 		logger.debug(String.format("[message][%s]",  message));
	        list.add(message);
	        logger.debug(String.format("[list][%d]", list.size()));
        }
    	 
    	/*
        private CountDownLatch latch = new CountDownLatch(3);

        private CountDownLatch partitionLatch = new CountDownLatch(2);

        private CountDownLatch filterLatch = new CountDownLatch(2);

        private CountDownLatch greetingLatch = new CountDownLatch(1);
        
        
        @KafkaListener(topics = "${message.topic.name}", groupId = "foo", containerFactory = "fooKafkaListenerContainerFactory")
        public void listenGroupFoo(String message) {
            logger.debug("Received Message in group 'foo': " + message);
            logger.debug("latch.getCount(): " + latch.getCount());
            latch.countDown();
            logger.debug("latch.getCount(): " + latch.getCount());
        }

        @KafkaListener(topics = "${message.topic.name}", groupId = "bar", containerFactory = "barKafkaListenerContainerFactory")
        public void listenGroupBar(String message) {
            logger.debug("Received Message in group 'bar': " + message);
            logger.debug("latch.getCount(): " + latch.getCount());
            latch.countDown();
            logger.debug("latch.getCount(): " + latch.getCount());
        }

        @KafkaListener(topics = "${message.topic.name}", containerFactory = "headersKafkaListenerContainerFactory")
        public void listenWithHeaders(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
            logger.debug("Received Message: " + message + " from partition: " + partition);
            logger.debug("latch.getCount(): " + latch.getCount());
            latch.countDown();
            logger.debug("latch.getCount(): " + latch.getCount());
        }

        @KafkaListener(topicPartitions = @TopicPartition(topic = "${partitioned.topic.name}", partitions = { "0", "3" }), containerFactory = "partitionsKafkaListenerContainerFactory")
        public void listenToPartition(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
            logger.debug("Received Message: " + message + " from partition: " + partition);
            logger.debug("partitionLatch.getCount(): " + latch.getCount());
            this.partitionLatch.countDown();
            logger.debug("partitionLatch.getCount(): " + latch.getCount());
        }

        @KafkaListener(topics = "${filtered.topic.name}", containerFactory = "filterKafkaListenerContainerFactory")
        public void listenWithFilter(String message) {
            logger.debug("Received Message in filtered listener: " + message);
            logger.debug("filterLatch.getCount(): " + filterLatch.getCount());
            this.filterLatch.countDown();
            logger.debug("filterLatch.getCount(): " + filterLatch.getCount());
        }

        @KafkaListener(topics = "${greeting.topic.name}", containerFactory = "greetingKafkaListenerContainerFactory")
        public void greetingListener(Greeting greeting) {
            logger.debug("Received greeting message: " + greeting);
            logger.debug("greetingLatch.getCount(): " + greetingLatch.getCount());
            this.greetingLatch.countDown();
            logger.debug("greetingLatch.getCount(): " + greetingLatch.getCount());
        }
        */
        
    }

}
