import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) throws IOException {

        System.out.println("JAVA TCP CLIENT");
        String hostName = "localhost";
        String nickname;
        Scanner scanner = new Scanner(System.in);
        int serverPortNumber = 12345;
        int udpServerPortNumber = 12346;
        Socket socket;
        DatagramSocket udpSocket;

        try {
            // create socket
            socket = new Socket(hostName, serverPortNumber);
            udpSocket = new DatagramSocket();
            // in & out streams
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Podaj nickname");
            nickname = scanner.nextLine();
            out.println(nickname);
            out.flush();
            out.println(udpSocket.getLocalPort());
            out.flush();
            ClientReceiver printer = new ClientReceiver(in);
            ClientSender writer = new ClientSender(out, nickname, udpSocket, InetAddress.getByName("localhost"), udpServerPortNumber);
            ClientUdpReceiver udpReceiver = new ClientUdpReceiver(udpSocket);
            System.out.println("Connected");
            printer.start();
            writer.start();
            udpReceiver.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
//            if (socket != null){
//                socket.close();
//            }
        }
    }
}
