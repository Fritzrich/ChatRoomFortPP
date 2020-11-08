import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class Server{

    private int port;
    ArrayList<MulServerThread> connections = new ArrayList<MulServerThread>();
    LoginManager loginManager = new LoginManager();
    boolean shouldRun = true;

    public static void main(String[] args) {
        Server server = new Server(1234);
        server.startListening();
    }

    public Server(int port){
        this.port = port;
    }

    public void startListening() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while(shouldRun){
                //verbinden mit Client und zuweisung zu Thread
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client verbunden");
                MulServerThread mst = new MulServerThread(clientSocket, this);
                mst.start();        //Thread starten
                connections.add(mst);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        


        
    }
}