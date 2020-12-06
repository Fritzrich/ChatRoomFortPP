import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Server extends Thread{

    private int port;
    ArrayList<ServerThread> connections = new ArrayList<ServerThread>();
    ArrayList<User> allUsers = new ArrayList<User>();
    boolean shouldRun = true;
    ServerSocket serverSocket;
    ArrayList<Room> allRooms = new ArrayList<Room>();

    public static void main(String[] args) {
        Server server = new Server(8000);
        server.allRooms.add(new Room("public", server));
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
    		if(command.equals(".quit")) {
    			for (ServerThread serverThread : connections) {
    				serverThread.serverShutdown();
    			}
    			shouldRun = false;
				scanner.close();
    		}
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
    
    
    
    void addUserToRoom(Room room, String username) {
    	room.addUser(getUser(username));
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
    }
    
    void setUserOffline(String username) {
    	for(User user : allUsers){
            if(user.getUsername().equals(username)){
                user.logOff();
            }
        }
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
    }

    public void changeUsername(String oldUsername, String newUsername){
        for(User user : allUsers){
            if(user.getUsername().equals(oldUsername)){
               user.setUsername(newUsername);
            }
        }
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