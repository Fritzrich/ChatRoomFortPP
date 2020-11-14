import java.io.*;
import java.net.*;

public class ServerThread extends Thread {

    Socket client;
    Server server;
    PrintWriter writer;
    BufferedReader reader;
    boolean shouldRun = true;
    String username;
    
    public ServerThread(Socket client, Server server) {
        this.client = client;
        this.server = server;
    }

    public void sendMessageToClient(String message) {
		writer.println(message);
		writer.flush();
    }
    
    public void sendMessageExcept(String message, ServerThread thread) {
    	for(ServerThread serverThread : server.connections) {
    		if(!(thread.equals(serverThread))) {
    			sendMessageToClient(message);
    		}
    	}
    }
    
    public void sendMessageToAllClients(String message) {
        for (ServerThread thread : server.connections){
            thread.sendMessageToClient(message);
        }
    }

    public void handleCommand(String message){
    	if(message.equals(".quit")) sendMessageToClient("[Server]: Sie werden ausgeloggt!");	//Verbindung trennen
    	else if(message.equals(".changePassword")) {											//Passwort ändern
    			sendMessageToClient("[Server]: Geben Sie ein neues Passwort ein: ");
    			String newPassword = "";
    			try {
    				newPassword = reader.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
    			server.changePassword(username, newPassword);
    	}
    }

    public void run() {
        try {
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			writer = new PrintWriter(client.getOutputStream());
			// Login-Start
			String tempUsername = reader.readLine();
			if (server.searchUser(tempUsername) == true) { // User existiert
				while (server.userIsOnline(tempUsername) == false) {
					sendMessageToClient("[Server]: Sie haben bereits einen Account! Geben Sie das korrekte Passwort ein:  ");
					String password = reader.readLine();
					if (server.checkPassword(tempUsername, password) == true) {
						sendMessageToClient("[Server]: Sie sind eingeloggt!");
					}
				}
			} else { // User existiert nicht
				sendMessageToClient("[Server]: Geben Sie ein Passwort ein: ");
				String password = reader.readLine();
				server.addUser(tempUsername, password);
				sendMessageToClient("[Server]: Sie sind eingeloggt!");
			}
			username = tempUsername;
			sendMessageToClient("[Server]: Es sind " + server.getOnlineUsers() + " online!");
			sendMessageExcept("[Server]: " + username + " hat sich gerade eingeloggt!", this);
			// Login-Ende
			while (shouldRun) {
				while (client.getInputStream().available() == 0) {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				String message = reader.readLine();
				String finalMessage = "[" + username + "]" + message;
				if(message.startsWith(".")){ //Filtere Kommando-Anfragen
					handleCommand(message);
				} else {
					sendMessageExcept(finalMessage, this);
				}
			}
			try {
				reader.close();
				writer.close();
				client.close();
				shouldRun = false;
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}

