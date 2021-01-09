
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

public class Server extends Thread{

	private int port;
	private ServerGUI UI;
    ArrayList<ServerThread> connections = new ArrayList<ServerThread>();
    ArrayList<User> allUsers = new ArrayList<User>();
    boolean shouldRun = true;
    ServerSocket serverSocket;
    ArrayList<Room> allRooms = new ArrayList<Room>();
    File file = new File("Serverlog.txt");
    File fileUserData = new File("ServerUserData.txt");

    public static void main(String[] args) {
        Server server = new Server(8000);
        new Room("public", server);
        server.readUserFile();
		server.start();
		server.startListening();
    }

    public Server(int port){
        this.port = port;
    }
    
	public void run() {
		UI = new ServerGUI(this);
		setUserAndRooms();
    	while(shouldRun) {
			try {
				Thread.sleep(1);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
		System.exit(0);
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
			UI.writeLog("[" + time + "]: " + data + "\n");
    	} catch(IOException e) {
    		e.printStackTrace();
    	}
    }
    
    public void updateServerUserData() {
    	try {
			BufferedWriter fw = new BufferedWriter( new FileWriter(fileUserData, false));
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
            	log("[Server]: Raum "+ room.getRoomName() +" ge�ndert zu " + roomName +"!");
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
    	log("[Server]: Raum "+ roomName + " geloescht!");
    }
    
    void addUserToRoom(Room room, String username) {
		room.addUser(getUser(username));
		getOnlineUsers();
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
        log("[Server]: User " + username + " hat sein Passwort ge�ndert!");
    }

    public void changeUsername(String oldUsername, String newUsername){
        for(User user : allUsers){
            if(user.getUsername().equals(oldUsername)){
               user.setUsername(newUsername);
            }
        }
        log("[Server]: User " + oldUsername + " hat seinen Namen zu " + newUsername + " geaendert");
    }
    
    public boolean userIsOnline(String username) {
    	for(User user : allUsers){
            if(user.getUsername().equals(username)){
               return user.isLoggedIn;
            }
        }
    	return false;
    }
    
    public void getOnlineUsers() {
		for (ServerThread st : connections) {
			st.sendMessageToClient("[Server]: Es sind " + st.room.userInRoom());
		}
		setUserAndRooms();			
	}
	
	public void getOnlineRooms() {
		String rooms = "";
    	for(Room room : allRooms) {
			rooms += " :: " + room.getRoomName();
    	}
    	for (ServerThread st : connections) {
			st.sendMessageToClient("[Server]: Raeume" + rooms);
		}
		setUserAndRooms();
	}
	
	public void setUserAndRooms() {
		UI.clearRooms();
		UI.clearUsers();

		for(Room room : allRooms) {
			UI.addRoom(room.getRoomName() + " (" + room.userCount() + " Nutzer) ");
		}
		UI.addRoom("+ neuer Raum");

		for (Room room : allRooms) {
			for (User user : room.userInRoom) {
				UI.addUser("[" + room.getRoomName() + "] " + user.getUsername());
			}
		}
		UI.addUser("+ Nutzer entbannen");
	}
}