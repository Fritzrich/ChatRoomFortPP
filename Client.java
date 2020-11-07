import java.net.Socket;
//import java.io.*;

public class Client {

    private String host;
    private int port;

    public static void main(String[] args){
        Client client = new Client("localhost", 1234);
        client.connect();
    }

    public Client(String host, int port){
        this.host = host;
        this.port = port;
    }

    public void connect(){
        try{
            Socket socket = new Socket(host, port);

            socket.close();
        } catch (Exception e){
            e.printStackTrace();
        }
       
        
    }
}
