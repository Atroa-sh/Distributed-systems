import java.io.BufferedReader;

public class ClientReceiver extends Thread {
    private BufferedReader in;
    public ClientReceiver(BufferedReader in) {
        this.in = in;
    }

    @Override
    public void run(){
        try {
            String msg = in.readLine();
            while (msg != null){
                System.out.println(msg);
                msg = in.readLine();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
