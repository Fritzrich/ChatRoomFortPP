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
	PrivateRoom pRoom = null;
	String chatBuddyName;
    
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
	
	public void sendMessageToChatPartner(String message) {
		server.getThread(chatBuddyName).sendMessageToClient(message);
	}
    
    public void serverShutdown() {
    	sendMessageToAllClients("[Server]: Der Server wird heruntergefahren!\nGood Bye!");
    	sendMessageToAllClients("[Server]: Sie werden ausgeloggt!");
    	shouldRun = false;
    }

    public void handleCommand(String message){	
    	if(message.equals(".help")) {															//Commands anzeigen lassen
    		sendMessageToClient(".quit - Sie werden ausgeloggt\n.changePassword - aendern Sie ihr Passwort\n.changeUsername - aendern Sie ihren Benutzernamen");
    	}
    	if(message.equals(".quit")) {															//Verbindung trennen
    		quit();
    	} else if(message.startsWith(".changePassword")) {										//Passwort aendern
				String newPassword = message.substring(15, message.length());
				server.changePassword(username, newPassword);
    			sendMessageToClient("[Server]: Sie haben ihr Passwort geaendert!");
    	} else if(message.startsWith(".changeUsername")) {										//Benutzernamen aendern
			String newUsername = message.substring(15, message.length());
			if (server.getUser(newUsername) != null) {
				sendMessageToClient("[Server]: Der Benutzername existiert bereits!");
			} else {
				server.changeUsername(username, newUsername);
				username = newUsername;
				sendMessageToClient("[Server]: Sie haben ihren Benutzernamen geaendert!");
				sendMessageToClient("[Server]: Sie sind eingeloggt als " + username + " in Raum " + room.getRoomName());
				server.getOnlineUsers();
			}
    	} else if(message.startsWith(".changeRoomTo")) {										//Raum wechseln
    		message = message.substring(13, message.length());
			room.removeUser(server.getUser(username));
			room = server.getRoom(message);
			server.addUserToRoom(server.getRoom(message), username);
			sendMessageToClient("[Server]: Sie sind eingeloggt als " + username + " in Raum " + room.getRoomName());
    		sendMessageToClient("Raum gewechselt zu"+ message);
    	} else if(message.startsWith(".closeDirectChat")) {
			sendMessageToChatPartner(".closeDirectChat");
			pRoom = null;
			server.getThread(chatBuddyName).pRoom = this.pRoom;
			server.getThread(chatBuddyName).chatBuddyName = null;
			chatBuddyName = null;
		} else if(message.startsWith(".chatWith")) {
			String tempchatBuddyName = message.substring(9, message.length());
			if(server.getThread(tempchatBuddyName).pRoom == null && !(tempchatBuddyName.equals(username))) {
			chatBuddyName = tempchatBuddyName;
			pRoom = new PrivateRoom(server, username + chatBuddyName, server.getUser(username), server.getUser(chatBuddyName));
			server.getThread(chatBuddyName).pRoom = this.pRoom;
			server.getThread(chatBuddyName).chatBuddyName = this.username;
			sendMessageToClient(".privateChatBuilt" + chatBuddyName);
			sendMessageToChatPartner(".privateChatBuilt" + username);
			} else {
				sendMessageToClient(".privateChatFailed");
			} 
			if(tempchatBuddyName.equals(username)) {
				sendMessageToClient(".privateChatFailed");
			}
		} else if(message.startsWith(".direct")){
			message = ".direct" + "[" + username + "]: " + message.substring(7, message.length());
			sendMessageToChatPartner(message);
		}
    }
    
    public void quit() {
		sendMessageToClient("[Server]: Sie werden ausgeloggt!");
		room.removeUser(server.getUser(username));
		server.setUserOffline(username);
		server.connections.remove(this);
		server.getOnlineUsers();
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
				else if (tempUsername.equals(".quit")) quit();
				else if (server.searchUser(tempUsername) == true) {			//User existiert
					boolean passwordIsDone = false;
					while (passwordIsDone == false) {
						sendMessageToClient("[Server]: Sie haben bereits einen Account! Geben Sie das korrekte Passwort ein:  ");
						password = reader.readLine();
						if (server.checkPassword(tempUsername, password) == true) {
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
					loginIsDone = true;
				}
			}
			username = tempUsername;
			// Login-Ende
			server.addUserToRoom(server.getRoom("public"), username);
			sendMessageToClient("[Server]: Sie sind eingeloggt als " + username + " in Raum " + room.getRoomName());
			server.getOnlineRooms();
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
				if(!(message.startsWith(".direct"))){
					server.log("[" + room.getRoomName() + "] " + finalMessage);
				}
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