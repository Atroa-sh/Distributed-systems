import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.*;

public class Server extends Thread{
    private final int portNumber;
    private final int udpServerPort;
    private ServerSocket serverSocket;
    private HashMap<Integer, PrintWriter> clients = new HashMap<>();
    private ArrayList<ClientHandler> handlers = new ArrayList<>();
    private ArrayList<Integer> udpPorts = new ArrayList<>();
    public Server(int portNumber, int udpServerPort) {
        this.portNumber = portNumber;
        this.udpServerPort = udpServerPort;
    }

    @Override
    public void run(){
        try {
            // create socket
            serverSocket = new ServerSocket(portNumber);
            ServerUdp serverUdp = new ServerUdp(udpServerPort);
            serverUdp.start();
            while(true){
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String nickname = in.readLine();
                int port = Integer.parseInt(in.readLine());
                udpPorts.add(port);
                ClientHandler handler = new ClientHandler(in, nickname, clientSocket.getPort());
                System.out.println("new client: " + nickname + " connected");
                int portNumber = clientSocket.getPort();
                handlers.add(handler);
                clients.put(portNumber, out);
                handler.start();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally{
            try {
                for(int i = 0; i < handlers.size(); i++){
                    handlers.get(i).interrupt();
                }
                for(int i = 0; i < handlers.size(); i++){
                    handlers.get(i).join();
                }
                if (serverSocket != null){
                    serverSocket.close();
                    }
            }
            catch (Exception e) {
            e.printStackTrace();
        }

        }
    }
    private class ServerUdp extends Thread{
        private final int portNumber;
        private DatagramSocket serverSocket;
        private byte[] receiveBuffer = new byte[1024];
        private byte[] sendBuffer = new byte[1024];
        private InetAddress address;
        public ServerUdp(int portNumber) {
            this.portNumber = portNumber;
        }

        @Override
        public void run(){
            try {
                serverSocket = new DatagramSocket(portNumber);
                address = InetAddress.getByName("localhost");
                while(true) {
                    Arrays.fill(receiveBuffer, (byte) 0);
                    DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                    serverSocket.receive(receivePacket);
                    String msg = new String(receivePacket.getData());
                    Integer sender = receivePacket.getPort();
                    String udpMsg = "UDP " + msg;
                    byte[] sendBuffer = udpMsg.getBytes();
                    for(int i = 0 ; i < udpPorts.size() ; i++){
                        Integer port = udpPorts.get(i);
                        if(!port.equals(sender)){
                            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, port);
                            serverSocket.send(sendPacket);
                        }
                    }

                }
            }catch (Exception e){
                e.printStackTrace();
            }
            finally {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            }
        }
    }
    private class ClientHandler extends Thread{
        private BufferedReader in;
        private String nickname;
        private Integer portNumber;

        private ClientHandler(BufferedReader bufferedReader, String nickname, Integer portNumber) {
            this.in = bufferedReader;
            this.nickname = nickname;
            this.portNumber = portNumber;
        }

        @Override
        public void run(){
            try {
                String msg;
                while (true){
                    msg = in.readLine();
                    System.out.println(nickname + " handler: received " + msg + " sending to others");
                    HashMap<Integer, PrintWriter> tmp = new HashMap<>(clients);
                    for(Map.Entry<Integer, PrintWriter> entry : tmp.entrySet()){
                        if(!entry.getKey().equals(portNumber)){
                            entry.getValue().println(msg);
                        }
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            finally {
                System.out.println(nickname + " disconnected");
            }
        }
    }
}
