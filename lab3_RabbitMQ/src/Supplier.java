import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class Supplier {
    private static final String[] supplies = {"oxygen", "backpack"};
    volatile int orderCounter = 0;
    public static void main(String[] args) {
        // info
        System.out.println("Supplier");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));


        try {
            System.out.println("Give a name to the Supplier");
            String name = br.readLine();

            // connection & channel
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();



            // exchange
            String EXCHANGE_NAME = "exchange";
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

            // queue & bind


            String queueName = name;
            channel.queueDeclare(queueName, false, false, false, null);
            channel.queueBind(queueName, EXCHANGE_NAME, "all");
            channel.queueBind(queueName, EXCHANGE_NAME, "suppliers");
            System.out.println("created queue: " + queueName);

//            channel.queueDeclare("oxygen", false, false, false, null);
//            channel.queueBind("oxygen", EXCHANGE_NAME, "oxygen");
//            System.out.println("created queue: " + "oxygen");
//
//
//            channel.queueDeclare("backpack", false, false, false, null);
//                channel.queueBind("backpack", EXCHANGE_NAME, "backpack");
//                System.out.println("created queue: " + "backpack");

            Consumer consumer = new DefaultConsumer(channel) {
                int orderCounter = 0;
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String received = new String(body, StandardCharsets.UTF_8);
                    System.out.println("Received: " + received);
                    String[] identify = received.split(":");
                    if(!identify[0].equals("ADMIN")){
                        orderCounter++;
                        String[] parts = received.split(" ");
                        String message = name + ":Supplier " + name + " sends " + parts[2] + " to " + identify[0] + ". Order nr. " + orderCounter;
                        channel.basicPublish(EXCHANGE_NAME, identify[0], null, message.getBytes(StandardCharsets.UTF_8));
                    }
                }
            };

            // start listening
            System.out.println("Waiting for messages...");
            channel.basicConsume(queueName, true, consumer);

            for(String queueType: supplies){
                channel.queueDeclare(queueType, false, false, false, null);
                channel.queueBind(queueType, EXCHANGE_NAME, queueType);
                channel.basicConsume(queueType, true, consumer);
                System.out.println("created queue: " + queueType);

            }

        }
        catch (IOException | TimeoutException e){
            e.printStackTrace();
        }

    }
}
