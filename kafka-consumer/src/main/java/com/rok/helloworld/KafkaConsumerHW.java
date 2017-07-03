package com.rok.helloworld;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Collections;
import java.util.Properties;
import java.util.Scanner;

/**
 * Created by roman.kulikov on 6/30/2017.
 * All rights reserved =D
 */
public class KafkaConsumerHW {
    private static boolean stop = false;

    public static void main(String[] argv)throws Exception{
        if (argv.length != 2) {
            System.err.printf("Usage: %s <topicName> <groupId>\n", KafkaConsumerHW.class.getSimpleName());
            System.exit(-1);
        }
        Scanner scanner = new Scanner(System.in);
        String topicName = argv[0];
        String groupId = argv[1];

        ConsumerThread consumerRunnable = new ConsumerThread(topicName,groupId);
        consumerRunnable.start();
        String line = "";
        while (!line.equals("exit")) {
            line = scanner.next();
        }
        stop = true;
        System.out.println("Stopping consumer .....");
        consumerRunnable.join();
    }

    private static class ConsumerThread extends Thread{
        private String topicName;
        private String groupId;
        private KafkaConsumer<String,String> kafkaConsumer;

        ConsumerThread(String topicName, String groupId){
            this.topicName = topicName;
            this.groupId = groupId;
        }
        public void run() {
            Properties configProperties = new Properties();
            configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());
            configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());
            configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, "simple");

            //Figure out where to start processing messages from
            kafkaConsumer = new KafkaConsumer<>(configProperties);
            kafkaConsumer.subscribe(Collections.singletonList(topicName));
            //Start processing messages
            try {
                while (!stop) {
                    ConsumerRecords<String, String> records = kafkaConsumer.poll(100);
                    for (ConsumerRecord<String, String> record : records)
                        System.out.println(record.value());
                }
            }finally{
                kafkaConsumer.close();
                System.out.println("After closing KafkaConsumer");
            }
        }
        void wakeup(){
            this.kafkaConsumer.wakeup();
        }
    }
}
