
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    Socket socket;
    boolean isLoggedIn = false;
    User user;
    boolean nameIsTaken = false;
    String username = "";
    OutputThread out;
    InputThread in;
    ClientGUI UI;
   
    public static void main(String[] args){
        new Client("localhost", 8000);
    }

    public Client(String host, int port){
    	try{
            socket = new Socket(host, port);
            System.out.println("Mit Server verbunden");
            UI = new ClientGUI(this);
            UI.setStatusConnection();
            
            out = new OutputThread(socket, this);
            in = new InputThread(socket, this);
            out.start();
            in.start();
        } catch(UnknownHostException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
   
    public void setUsername(String username) {
        this.username = username;
        this.user.setUsername(username);
    }
    
    public String getUsername() {
    	return user.getUsername();
    }
    
    public void quit() {
    	try {
    		out.shouldRun = false;
    		in.shouldRun = false;
            socket.close();
            System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
