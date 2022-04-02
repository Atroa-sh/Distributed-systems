import java.io.BufferedReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public class ClientUdpReceiver extends Thread {
    private final DatagramSocket socket;
    private final byte[] receiveBuffer = new byte[1024];
    private final DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
    public ClientUdpReceiver(DatagramSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run(){
        try {
            while (true){
                Arrays.fill(receiveBuffer, (byte)0);
                socket.receive(receivePacket);
                String msg = new String(receivePacket.getData());
                System.out.println(msg.substring(0, msg.indexOf('\u0000')));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
