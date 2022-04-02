public class ServerMain {
    public static void main(String[] args){
        System.out.println("SERVER UP");
        int portNumber = 12345;
        int udpPortNumber = 12346;
        Server server = new Server(portNumber, udpPortNumber);
        server.start();

    }
}
