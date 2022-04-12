import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Admin {
    public static void main(String[] args) {
        // info
        System.out.println("Team");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));


        try {
            String personalKey = "#";

            // connection & channel
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();


            // exchange
            String EXCHANGE_NAME = "exchange";
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

            // queue & bind
            String queueName = "admin";
            channel.queueDeclare(queueName, false, false, false, null);
            channel.queueBind(queueName, EXCHANGE_NAME, personalKey);
            System.out.println("created queue: " + queueName);

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, StandardCharsets.UTF_8);
                    String[] identify = message.split(":");
                    if(!identify[0].equals("ADMIN")){
                        System.out.println(message);
                    }

                }
            };

            // start listening
            System.out.println("Waiting for messages...");
            channel.basicConsume(queueName, true, consumer);
            String message = "";
            String choice;
            while (true) {
                choice = "";
                while (!choice.equals("teams") && !choice.equals("suppliers") && !choice.equals("all") && !choice.equals(":q")){
                    System.out.println("""
                            1. Send message to teams: type 'teams'
                            2. Send message to suppliers: type 'suppliers'
                            3. Send message to all: type 'all'
                            4. type :q to exit""");
                    choice = br.readLine();
                }
                if (":q".equals(choice)) break;
                System.out.println("Enter message: ");
                message = "ADMIN: " + br.readLine();
                channel.basicPublish(EXCHANGE_NAME, choice, null, message.getBytes(StandardCharsets.UTF_8));
                System.out.println("Sent: " + message);
            }
            channel.close();
            connection.close();
        }
        catch (IOException | TimeoutException e){
            e.printStackTrace();
        }

    }
}
