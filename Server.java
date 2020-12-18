
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class Server extends Thread{

    private int port;
    ArrayList<ServerThread> connections = new ArrayList<ServerThread>();
    ArrayList<User> allUsers = new ArrayList<User>();
    boolean shouldRun = true;
    ServerSocket serverSocket;
    ArrayList<Room> allRooms = new ArrayList<Room>();
    File file = new File("Serverlog.txt");
    File fileUserData = new File("ServerUserData.txt");

    public static void main(String[] args) {
        Server server = new Server(8000);
        server.allRooms.add(new Room("public", server));
        server.readUserFile();
        server.start();
        server.startListening();
    }
    
    public Server(int port){
        this.port = port;
    }
    
    public void run() { //zum manuellen Schließen (type ".quit" in Console)
    	Scanner scanner = new Scanner(System.in);
    	while(shouldRun) {
    		String command = scanner.nextLine();
    		handleCommand(command);
    	}
    	scanner.close();
    }
    
    public void readUserFile() {
    	String line;
		try {
			BufferedReader readbuffer = new BufferedReader( new FileReader("ServerUserData.txt"));
			line = readbuffer.readLine();
			while(line != null){
				String username = line;
				line = readbuffer.readLine();
				String password = line;
				line = readbuffer.readLine();
				boolean IsBanned = Boolean.parseBoolean(line);
				User newUser = new User(username, password , IsBanned);
				allUsers.add(newUser);
				line = readbuffer.readLine();
			}
			readbuffer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void registerToFile(String username, String password) {
    	try {
    		BufferedWriter writer = new BufferedWriter(new FileWriter(fileUserData, true));
    		writer.write(username + "\n" + password + "\n" + "false\n");
    		writer.close();
    	} catch(IOException e) {
    		e.printStackTrace();
    	}
    }
    
    public void log(String data) {
    	try {
    		SimpleDateFormat date = new SimpleDateFormat("HH:mm");
    	    String time = date.format(new Date());
    		BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
    		writer.write("[" + time + "]: " + data + "\n");
    		writer.close();
    	} catch(IOException e) {
    		e.printStackTrace();
    	}
    }
    
    public void handleCommand(String command) {
    	if(command.equals(".quit")) {
			for (ServerThread serverThread : connections) {
				serverThread.serverShutdown();
				log("[Server]: Server wurde heruntergefahren!");
			}
			shouldRun = false;
		} else if(command.startsWith(".kickUser")) {
			command = command.substring(9, command.length());
			for(ServerThread st : connections) {
				if(st.username.equals(command)) {
					st.room.userInRoom.remove(getUser(command));
					st.sendMessageToClient("[Server]: Sie werden gekickt!");
					st.sendMessageToClient("[Server]: Sie werden ausgeloggt!");
					log("[Server]: User " + command + " wurde gekickt!");
					setUserOffline(command);
					st.quit();
				}
			}
		} else if(command.startsWith(".warnUser")) {
			command = command.substring(9, command.length());
			for(ServerThread st : connections) {
				if(st.username.equals(command)) {
					st.sendMessageToClient("[Server]: Bitte halten Sie die Spielregeln ein! Sonst kick!!!");
					log("[Server]: User " + command + " wurde verwarnt!");
				}
			} 
		} else if(command.startsWith(".banUser")) {
			command = command.substring(8, command.length());
			for(ServerThread st : connections) {
				if(st.username.equals(command)) {
					st.room.userInRoom.remove(getUser(command));
					st.sendMessageToClient("[Server]: Sie werden gebannt!");
					st.sendMessageToClient("[Server]: Sie werden ausgeloggt!");
					getUser(command).ban();
					log("[Server]: User " + command + " wurde gebannt!");
					setUserOffline(command);
					st.quit();
				}
			}
			updateServerUserData();
		} else if (command.startsWith(".pardon")) {
			command = command.substring(7, command.length());
			getUser(command).pardon();
			log("[Server]: User " + command + " wurde entbannt!");
			updateServerUserData();
		} else if (command.startsWith(".createRoom")) { 				// Kreiert Raum
			command = command.substring(11, command.length());
			allRooms.add(new Room(command, this));
			log("[Server]: Raum " + command + " wurde erstellt!");
		} else if (command.startsWith(".changeRoomName")) { 			// Raumnamen ändern
			//command = command.substring(15, command.length());
			//changeRoomName(room, command);
		} else if (command.startsWith(".deleteRoom")) { 				// Raum löschen
			command = command.substring(11, command.length());
			deleteRoom(command);
		}
    }
    
    public void updateServerUserData() {
    	try {
			FileWriter fw = new FileWriter("serverUserData.txt");
			String updatedText = "";
			for(User user : allUsers) {
				updatedText += user.getUsername() + "\n" + user.getPassword() + "\n" + Boolean.toString(user.isBanned) + "\n";
			}
			fw.write(updatedText);
			fw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void startListening() {
        try {
            serverSocket = new ServerSocket(port);
            while(shouldRun){
                //verbinden mit Client und Zuweisung zu Thread
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client verbunden");
                ServerThread st = new ServerThread(clientSocket, this);
                st.start();        //Thread starten
                connections.add(st);
            }
            serverSocket.close();
            return;
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    //Room-Management
    
    void changeRoomName(Room room, String roomName) {
    	for(Room temproom : allRooms){
            if(temproom.equals(room)){
            	log("[Server]: Raum "+ room.getRoomName() +" geändert zu " + roomName +"!");
                temproom.setRoomName(roomName);
            }
        }
    }
    
    void deleteRoom(String roomName) {
    	for(Room room : allRooms){
            if(room.getRoomName().equals(roomName)){
            	for(User user : room.userInRoom) {
            		addUserToRoom(getRoom("public"), user.getUsername());
            		for(ServerThread st : connections) {
            			if(st.username.equals(user.getUsername())){
            				st.room = getRoom("public");
            			}
            		}
            	}
            	allRooms.remove(room);
            }
        }
    	log("[Server]: Raum "+ roomName + " gelöscht!");
    }
    
    void addUserToRoom(Room room, String username) {
    	room.addUser(getUser(username));
    	log("[Server]: User " + username + " hat den Raum "+ room.getRoomName() + " betreten!");
    }
    
    Room getRoom(String roomName) {
    	for(Room room : allRooms){
            if(room.getRoomName().equals(roomName)){
                return room;
            }
        }
    	return null;
    }
    
    //Account-Management
    
    User getUser(String username) {
    	for(User user : allUsers){
            if(user.getUsername().equals(username)){
                return user;
            }
        }
    	return null;
    }
    
    void addUser(String username, String password){
        User user = new User(username, password);
        allUsers.add(user);
        user.isLoggedIn = true;
        log("[Server]: User " + username + " hat sich registriert!");
    }

    boolean searchUser(String username){
        for(User user : allUsers){
            if(user.getUsername().equals(username)){
                return true;
            }
        }
        return false;
    }
    
    void setUserOnline(String username) {
    	for(User user : allUsers){
            if(user.getUsername().equals(username)){
                user.logIn();
            }
        }
    	log("[Server]: User " + username + " hat sich eingeloggt!");
    }
    
    void setUserOffline(String username) {
    	for(User user : allUsers){
            if(user.getUsername().equals(username)){
                user.logOff();
            }
        }
    	log("[Server]: User " + username + " hat sich ausgeloggt!");
    }

    public boolean checkPassword(String username, String password){
        for(User user : allUsers){
            if(user.getUsername().equals(username)){
                if(user.getPassword().equals(password)){
                	user.isLoggedIn = true;
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public void changePassword(String username, String newPassword){
        for(User user : allUsers){
            if(user.getUsername().equals(username) ){
               user.setPassword(newPassword);
            }
        }
        log("[Server]: User " + username + " hat sein Passwort geändert!");
    }

    public void changeUsername(String oldUsername, String newUsername){
        for(User user : allUsers){
            if(user.getUsername().equals(oldUsername)){
               user.setUsername(newUsername);
            }
        }
        log("[Server]: User " + oldUsername + " hat seinen Namen zu " + newUsername + " geändert");
    }
    
    public boolean userIsOnline(String username) {
    	for(User user : allUsers){
            if(user.getUsername().equals(username)){
               return user.isLoggedIn;
            }
        }
    	return false;
    }
    
    public String getOnlineUsers() {
    	String users = "";
    	for(User user : allUsers) {
    		if(user.isLoggedIn)	users += " | " + user.getUsername();
    	}
    	return users;
    }
}