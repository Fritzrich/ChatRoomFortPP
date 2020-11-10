import java.io.*;
import java.net.*;

public class MulServerThread extends Thread {

    Socket client;
    Server server;
    PrintWriter writer;
    BufferedReader reader;
    boolean shouldRun = true;

    public MulServerThread(Socket client, Server server) {
        super("MulServerThread");
        this.client = client;
        this.server = server;
    }

    public void sendMessageToClient(String message) {
        writer.write(message);
		writer.flush();
    }
    
    public void sendMessageExcept(String message, MulServerThread thread) {
    	for(MulServerThread serverThread : server.connections) {
    		if(!(serverThread == thread)) {
    			sendMessageToClient(message);
    		}
    	}
    }
    
    public void sendMessageToAllClients(String message) {
        for (MulServerThread thread : server.connections){
            thread.sendMessageToClient(message);
        }
    }

    public void handleCommand(String textIn){
    	
    }

    public void run() {
        try {
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            writer = new PrintWriter(client.getOutputStream());
            	//Logins-Start
                String username = reader.readLine();
                if(server.searchUser(username) == true) {
                	while(server.userIsOnline(username) == false) {
                		sendMessageToClient("\n[Server]: Sie haben bereits einen Account! Geben Sie das korrekte Passwort ein:  ");
                		String password = reader.readLine();
                		if(server.checkPassword(username, password) == true){
                			sendMessageToClient("\n[Server]: Sie sind eingeloggt!");
                		}
                	}
                } else {
                	sendMessageToClient("\n[Server]: Geben Sie ein Passwort ein: ");
                	String password = reader.readLine();
                	server.addUser(username, password);
                	sendMessageToClient("\n[Server]: Sie sind eingeloggt!");
                }
                sendMessageToClient("\n[Server]: Es sind " + server.getOnlineUsers() + " online!");
                sendMessageToAllClients("\n[Server]: " + username + " hat sich gerade eingeloggt!");
                //Login-Ende
                while(shouldRun) {
                	while (client.getInputStream().available() == 0) {
                		try {
                		    Thread.sleep(1);
                		} catch (InterruptedException e) {
                		    e.printStackTrace();
                		}
                	}
                	String message = reader.readLine();
                	String finalMessage = "[" + username + "]" + message;
                	sendMessageExcept(finalMessage, this);
                	/*if(textIn.startsWith(".")){            //Filtere Kommando-Anfragen
                    	handleCommand(textIn);
                    }
                    else sendMessageToAllClients(textIn);*/
                }
                reader.close();
                writer.close();
                client.close();
                shouldRun = false;
            }catch(IOException e){
                e.printStackTrace();
            }
    }
}



/*while (din.available() == 0) {
try {
    Thread.sleep(1);
} catch (InterruptedException e) {
    e.printStackTrace();
}
}*/

