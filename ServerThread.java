import java.io.*;
import java.net.*;

public class ServerThread extends Thread {

    Socket client;
    Server server;
    PrintWriter writer;
    BufferedReader reader;
    boolean shouldRun = true;
    String username;
    Room room;
    
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
    		if(!(thread.equals(serverThread)) && serverThread.room.equals(room)) {
    			if(server.userIsOnline(serverThread.username)) {
    				serverThread.sendMessageToClient(message);
    			}
    		}
    	}
    }
    
    public void sendMessageToAllClients(String message) {
        for (ServerThread serverThread : server.connections){
        	if(server.userIsOnline(serverThread.username)) {
        		serverThread.sendMessageToClient(message);
        	}
        }
    }
    
    public void serverShutdown() {
    	sendMessageToAllClients("[Server]: Der Server wird heruntergefahren!\nGood Bye!");
    	sendMessageToAllClients("[Server]: Sie werden ausgeloggt!");
    	shouldRun = false;
    }

    public void handleCommand(String message){	
    	if(message.equals(".help")) {															//Commands anzeigen lassen
    		sendMessageToClient(".quit - Sie werden ausgeloggt\n.changePassword - Ändern Sie ihr Passwort\n.changeUsername - Ändern Sie ihren Benutzernamen");
    	}
    	if(message.equals(".quit")) {															//Verbindung trennen
    		sendMessageToClient("[Server]: Sie werden ausgeloggt!");
    		server.setUserOffline(username);
			shouldRun = false;
    	} else if(message.equals(".changePassword")) {											//Passwort ändern
    			sendMessageToClient("[Server]: Geben Sie ein neues Passwort ein: ");
    			String newPassword = "";
    			try {
    				newPassword = reader.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
    			server.changePassword(username, newPassword);
    			sendMessageToClient("[Server]: Sie haben ihr Passwort geändert!");
    	} else if(message.equals(".changeUsername")) {											//Benutzernamen ändern
    		sendMessageToClient("[Server]: Geben Sie einen neuen Benutzernamen ein: ");
			String newUsername = "";
			try {
				newUsername = reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			server.changeUsername(username, newUsername);
			username = newUsername;
			sendMessageToClient("[Server]: Sie haben ihren Benutzernamen geändert!");
    	} else if(message.startsWith(".createRoom")) {											//Kreiert Raum
    		message = message.substring(11, message.length());
    		System.out.println(message);
    		server.allRooms.add(new Room(message, server));
    		sendMessageToClient("Neue Raum"+ message);
    	} else if(message.startsWith(".changeRoomTo")) {										//Raum wechseln
    		message = message.substring(13, message.length());
    		server.addUserToRoom(server.getRoom(message), username);
    		room = server.getRoom(message);
    		sendMessageToClient("Raum gewechselt zu"+ message);
    	}
    }

    public void run() {
        try {
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			writer = new PrintWriter(client.getOutputStream());
			// Login-Start
			sendMessageToClient("[Server]: Geben Sie ihren Benutzernamen ein:");
			String tempUsername = reader.readLine();
			if (server.searchUser(tempUsername) == true) {//User existiert
				boolean loginIsDone = false;
				while (loginIsDone == false) {
					sendMessageToClient("[Server]: Sie haben bereits einen Account! Geben Sie das korrekte Passwort ein:  ");
					String password = reader.readLine();
					if (server.checkPassword(tempUsername, password) == true) {
						sendMessageToClient("[Server]: Sie sind eingeloggt!");
						loginIsDone = true;
					}
				}
			} else { //User existiert nicht
				sendMessageToClient("[Server]: Geben Sie ein Passwort ein: ");
				String password = reader.readLine();
				server.addUser(tempUsername, password);
				sendMessageToClient("[Server]: Sie sind eingeloggt!");
			}
			username = tempUsername;
			// Login-Ende
			server.addUserToRoom(server.getRoom("public"), username);
			room = server.getRoom("public");
			sendMessageToClient("[Server]: Es sind " + server.getOnlineUsers() + " online!");
			sendMessageExcept("[Server]: " + username + " hat sich gerade eingeloggt!", this);
			//Nachrichtendienst
			while (shouldRun) {
				while (client.getInputStream().available() == 0) {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				String message = reader.readLine();
				String finalMessage = "[" + username + "]: " + message;
				if(message.startsWith(".")){ 									//Filtere Kommando-Anfragen
					handleCommand(message);
				} else {
					sendMessageExcept(finalMessage, this);
				}
			}
			try {
				reader.close();
				writer.close();
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
