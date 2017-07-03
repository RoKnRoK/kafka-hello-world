package com.rok.helloworld;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.Scanner;

/**
 * Created by roman.kulikov on 6/30/2017.
 * All rights reserved =D
 */
public class KafkaProducerHW {

    public static void main(String[] argv)throws Exception {
        if (argv.length != 1) {
            System.err.println("Please specify 1 parameters ");
            System.exit(-1);
        }
        String topicName = argv[0];
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter message(type exit to quit)");

        //Configure the Producer
        Properties configProperties = new Properties();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getCanonicalName());
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getCanonicalName());

        Producer<String, String> producer = new KafkaProducer<>(configProperties);
        String line = scanner.nextLine();
        while(!line.equals("exit")) {
            ProducerRecord<String, String> rec = new ProducerRecord<>(topicName, line);
            producer.send(rec);
            line = scanner.nextLine();
        }
        scanner.close();
        producer.close();
    }
}
