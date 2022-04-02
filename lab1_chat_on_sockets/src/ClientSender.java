import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class ClientSender extends Thread{
    private final PrintWriter out;
    private final String nickname;
    private final DatagramSocket socket;
    private final InetAddress address;
    private final int udpServerPortNumber;
    public ClientSender(PrintWriter out, String nickname, DatagramSocket socket, InetAddress address, int udpServerPortNumber) {
        this.out = out;
        this.nickname = nickname;
        this.socket = socket;
        this.address = address;
        this.udpServerPortNumber = udpServerPortNumber;
    }

    @Override
    public void run(){
        Scanner scanner = new Scanner(System.in);
        String msg;
        String protocol;
        byte[] sendBuffer;
        while (true){
            protocol = "";
            while (!(protocol.equals("u") || protocol.equals("t"))){
                System.out.println("Type 't' for tcp msg or 'u' for udp msg");
                protocol = scanner.nextLine().toLowerCase();
            }
            String input = "";
            System.out.println("You have chosen " + protocol + " type ':q' to change protocol");
            while (!input.equals(":q")){
                input = scanner.nextLine();
                if(protocol.equals("u") && !input.equals(":q")){
                    try {
                        msg = nickname + ": " + input;
                        sendBuffer = msg.getBytes();
                        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, udpServerPortNumber);
                        socket.send(sendPacket);
                        System.out.println("Message sent");
                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }

                }
                else if(protocol.equals("t") && !input.equals(":q")){
                    msg = input;
                    out.println(nickname + ": " + msg);
                    out.flush();
                    System.out.println("Message sent");
                }
            }
        }
    }
}
