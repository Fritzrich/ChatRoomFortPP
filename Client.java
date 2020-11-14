
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    Socket socket;
    boolean isLoggedIn = false;
    User user;
    boolean nameIsTaken = false;
    String username;
   
    public static void main(String[] args){
        new Client("localhost", 8000);
    }

    public Client(String host, int port){
    	try{
            socket = new Socket(host, port);
            System.out.println("Mit Server verbunden");
            new OutputThread(socket, this).start();
            new InputThread(socket, this).start();
        } catch(UnknownHostException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public void setUsername(String username) {
    	this.username = username;
    }
    
    public String getUsername() {
    	return user.getUsername();
    }
}
