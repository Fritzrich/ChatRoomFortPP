import java.net.*;
import java.io.*;

public class Server{

    private int port;
    public static void main(String[] args) {
        Server server = new Server(1234);
        LoginManager loginManager = new LoginManager();
        server.startListening();
    }

    public Server(int port){
        this.port = port;
    }

    public void startListening() {
        try {
            //verbinden mit Client
            ServerSocket serverSocket = new ServerSocket(port);
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client verbunden");

            clientSocket.close();
            serverSocket.close();
        } catch(Exception e){
            e.printStackTrace();
        }
        


        
    }
}