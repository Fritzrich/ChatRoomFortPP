
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
//import java.io.*;
import java.util.Scanner;

public class Client {

    Socket socket;
    ClientConnection cc;
    boolean shouldRun = true;

    public static void main(String[] args){
        new Client("localhost", 1234);
    }

    public Client(String host, int port){
        try{
            socket = new Socket(host, port);
            cc = new ClientConnection(socket, this);
            cc.start();
        } catch(UnknownHostException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void listenForInput(){
        Scanner scanner = new Scanner(System.in);
        while(shouldRun) {
            while(!scanner.hasNextLine()){
                try{
                    Thread.sleep(1);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
            String input = scanner.nextLine();
            cc.sendMessageToServer(input);
        }
        scanner.close();
    }

    /*public void connect(){
        try{
            socket = new Socket(host, port);
            socket.close();
        } catch (Exception e){
            e.printStackTrace();
        }
       
        
    }*/
}
