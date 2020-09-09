package com.example.demo.kafka;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@SpringBootApplication
public class KafkaApplication {

	private static final Logger logger = Logger.getLogger(KafkaApplication.class);
	
    public static void main(String[] args) throws Exception {

    	logger.debug("KafkaApplication.main[START]==================================================================================");
        ConfigurableApplicationContext context = SpringApplication.run(KafkaApplication.class, args);

        MessageProducer producer = context.getBean(MessageProducer.class);
        MessageListener listener = context.getBean(MessageListener.class);
        /*
         * Sending a Hello World message to topic 'baeldung'. 
         * Must be received by both listeners with group foo
         * and bar with containerFactory fooKafkaListenerContainerFactory
         * and barKafkaListenerContainerFactory respectively.
         * It will also be received by the listener with
         * headersKafkaListenerContainerFactory as container factory.
         */
        producer.sendMessage("Hello, World!");
        listener.latch.await(10, TimeUnit.SECONDS);

        /*
         * Sending message to a topic with 5 partitions,
         * each message to a different partition. But as per
         * listener configuration, only the messages from
         * partition 0 and 3 will be consumed.
         */
        for (int i = 0; i < 5; i++) {
            producer.sendMessageToPartition("Hello To Partitioned Topic!", i);
        }
        listener.partitionLatch.await(10, TimeUnit.SECONDS);

        /*
         * Sending message to 'filtered' topic. As per listener
         * configuration,  all messages with char sequence
         * 'World' will be discarded.
         */
        producer.sendMessageToFiltered("Hello Baeldung!");
        producer.sendMessageToFiltered("Hello World!");
        listener.filterLatch.await(10, TimeUnit.SECONDS);

        /*
         * Sending message to 'greeting' topic. This will send
         * and received a java object with the help of
         * greetingKafkaListenerContainerFactory.
         */
        producer.sendGreetingMessage(new Greeting("Greetings", "World!"));
        listener.greetingLatch.await(10, TimeUnit.SECONDS);

        context.close();
        
    	logger.debug("KafkaApplication.main[END]==================================================================================");        
    }

    @Bean
    public MessageProducer messageProducer() {
    	logger.debug("");
    	
    	MessageProducer producer = new MessageProducer();
    	logger.debug(String.format("[return][producer][%s]", producer));
    	
    	return producer;
    }

    @Bean
    public MessageListener messageListener() {
    	logger.debug("");

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
    }

    public static class MessageListener {

    	private static final Logger logger = Logger.getLogger(MessageListener.class);
    	
        private CountDownLatch latch = new CountDownLatch(3);

        private CountDownLatch partitionLatch = new CountDownLatch(2);

        private CountDownLatch filterLatch = new CountDownLatch(2);

        private CountDownLatch greetingLatch = new CountDownLatch(1);

        @KafkaListener(topics = "${message.topic.name}")
        public void listen(String message) {
			
            logger.debug("Received Message : " + message);
            logger.debug("latch.getCount(): " + latch.getCount());
            latch.countDown();
            logger.debug("latch.getCount(): " + latch.getCount());
        }
        
        @KafkaListener(topics = "${message.topic.name}", groupId = "foo", containerFactory = "fooKafkaListenerContainerFactory")
        public void listenGroupFoo(String message) {
			/*
			 * logger.debug("Received Message in group 'foo': " + message);
			 * logger.debug("latch.getCount(): " + latch.getCount());
			 * latch.countDown(); logger.debug("latch.getCount(): " +
			 * latch.getCount());
			 */
            
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

    }

}
