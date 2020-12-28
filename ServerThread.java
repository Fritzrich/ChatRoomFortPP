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
    		sendMessageToClient(".quit - Sie werden ausgeloggt\n.changePassword - �ndern Sie ihr Passwort\n.changeUsername - �ndern Sie ihren Benutzernamen");
    	}
    	if(message.equals(".quit")) {															//Verbindung trennen
    		quit();
    	} else if(message.equals(".changePassword")) {											//Passwort �ndern
    			sendMessageToClient("[Server]: Geben Sie ein neues Passwort ein: ");
    			String newPassword = "";
    			try {
    				newPassword = reader.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
    			server.changePassword(username, newPassword);
    			sendMessageToClient("[Server]: Sie haben ihr Passwort ge�ndert!");
    	} else if(message.equals(".changeUsername")) {											//Benutzernamen �ndern
    		sendMessageToClient("[Server]: Geben Sie einen neuen Benutzernamen ein: ");
			String newUsername = "";
			try {
				newUsername = reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			server.changeUsername(username, newUsername);
			username = newUsername;
			sendMessageToClient("[Server]: Sie haben ihren Benutzernamen ge�ndert!");
    	} else if(message.startsWith(".changeRoomTo")) {										//Raum wechseln
    		message = message.substring(13, message.length());
    		server.addUserToRoom(server.getRoom(message), username);
    		room = server.getRoom(message);
    		sendMessageToClient("Raum gewechselt zu"+ message);
    	} else if(message.startsWith(".changeRoomName")){										//Raumnamen �ndern
    		message = message.substring(15, message.length());
			server.changeRoomName(room, message);
		}
    }
    
    public void quit() {
    	sendMessageToClient("[Server]: Sie werden ausgeloggt!");
		server.setUserOffline(username);
		sendMessageExcept("[Server]: Es sind" + server.getOnlineUsers() + " | online!", this);
		shouldRun = false;
    }

    public void run() {
        try {
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			writer = new PrintWriter(client.getOutputStream());
			room = server.getRoom("public");
			boolean loginIsDone  = false;
			String tempUsername = "";
			// Login-Start
			while(!loginIsDone) {
				sendMessageToClient("[Server]: Geben Sie ihren Benutzernamen ein:");
				tempUsername = reader.readLine();
				String password;
				if(server.searchUser(tempUsername) == true && server.getUser(tempUsername).isBanned == true) {
					sendMessageToClient("[Server]: Der Nutzer " + tempUsername + " ist gebannt!");
				}
				else if (tempUsername.equals(".quit")) {
					quit();
					loginIsDone = true;
				}
				else if (server.searchUser(tempUsername) == true) {			//User existiert
					boolean passwordIsDone = false;
					while (passwordIsDone == false) {
						sendMessageToClient("[Server]: Sie haben bereits einen Account! Geben Sie das korrekte Passwort ein:  ");
						password = reader.readLine();
						if (server.checkPassword(tempUsername, password) == true) {
							sendMessageToClient("[Server]: Sie sind eingeloggt als " + tempUsername);
							passwordIsDone = true;
						}
					}
					loginIsDone = true;
				} else {								//User existiert nicht
					sendMessageToClient("[Server]: Geben Sie ein Passwort ein: ");
					password = reader.readLine();
					if (password.equals(".quit")) quit();
					server.addUser(tempUsername, password);
					server.registerToFile(tempUsername, password);
					sendMessageToClient("[Server]: Sie sind eingeloggt!");
					loginIsDone = true;
				}
			}
			username = tempUsername;
			// Login-Ende
			server.addUserToRoom(server.getRoom("public"), username);
			sendMessageToAllClients("[Server]: Es sind" + server.getOnlineUsers() + " :: online!");
			sendMessageToClient("[Server]: Raeume" + server.getOnlineRooms() + " :: vorhanden!");
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
				server.log("[" + room.getRoomName() + "] " + finalMessage);
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
				server.connections.remove(this);
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}