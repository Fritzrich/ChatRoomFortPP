
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    Socket socket;
    boolean isLoggedIn = false;
    boolean nameIsTaken = false;
    String username = "";
    String room = "";
    OutputThread out;
    InputThread in;
    ClientGUI UI;
   
    public static void main(String[] args){
        new Client("localhost", 8000);
    }

    public Client(String host, int port){
        UI = new ClientGUI(this);

    	try{
            socket = new Socket(host, port);
            System.out.println("Mit Server verbunden");
            UI.setStatusConnection();
            
            out = new OutputThread(socket, this);
            in = new InputThread(socket, this);
            out.start();
            in.start();
        } catch(UnknownHostException e){
            UI.postMessage("Verbindung zum Server fehlgeschlagen. Bitte schliessen Sie das Programm und versuchen Sie es später erneut!");
            e.printStackTrace();
        } catch(IOException e){
            UI.postMessage("Verbindung zum Server fehlgeschlagen. Bitte schliessen Sie das Programm und versuchen Sie es später erneut!");
            e.printStackTrace();
        }
    }
   
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getUsername() {
    	return username;
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
