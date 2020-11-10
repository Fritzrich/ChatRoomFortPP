/*import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientConnection extends Thread{
    
    Socket socket;
    DataInputStream din;
    DataOutputStream dout;
    boolean shouldRun = true;
    Client client;

    public ClientConnection(Socket socket, Client client){
        this.socket = socket;
        this.client = client;
    }

    public void sendMessageToServer(String text){
        try {
            dout.writeUTF(text);
            dout.flush();
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
    }

    public void close(){
        try{
            din.close();
            dout.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void run(){
        try{
            din = new DataInputStream(socket.getInputStream());
            dout = new DataOutputStream(socket.getOutputStream());
            while(shouldRun){
                try {
                    while (din.available() == 0) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    String reply = din.readUTF();
                    System.out.println(reply);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch(IOException e){
            e.printStackTrace();
            close();
        }
    }
}*/
