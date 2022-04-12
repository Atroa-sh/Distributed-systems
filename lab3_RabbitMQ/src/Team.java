import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.util.concurrent.TimeoutException;

public class Team {
    public static void main(String[] args) {
        // info
        System.out.println("Team");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));


        try {
            System.out.println("Give a name to the team");
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
            channel.queueBind(queueName, EXCHANGE_NAME, name);
            channel.queueBind(queueName, EXCHANGE_NAME, "all");
            channel.queueBind(queueName, EXCHANGE_NAME, "teams");
            System.out.println("created queue: " + queueName);

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, StandardCharsets.UTF_8);
                    System.out.println("Received: " + message);
                }
            };

            // start listening
            System.out.println("Waiting for messages...");
            channel.basicConsume(queueName, true, consumer);
            while (true) {
                System.out.println("What do you need? backpack, boots or oxygen? ':q' to quit");
                String request = br.readLine();
                if (":q".equals(request)) {
                    break;
                }
                String message = name + ":" + name + " requested " + request;

                // break condition


                // publish
                channel.basicPublish(EXCHANGE_NAME, request, null, message.getBytes(StandardCharsets.UTF_8));
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
