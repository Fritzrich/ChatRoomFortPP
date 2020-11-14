import java.net.*;
import java.util.ArrayList;

public class Server{

    private int port;
    ArrayList<ServerThread> connections = new ArrayList<ServerThread>();
    ArrayList<User> allUsers = new ArrayList<User>();
    boolean shouldRun = true;

    public static void main(String[] args) {
        Server server = new Server(8000);
        server.startListening();
    }

    public Server(int port){
        this.port = port;
    }

    public void startListening() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while(shouldRun){
                //verbinden mit Client und zuweisung zu Thread
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client verbunden");
                ServerThread st = new ServerThread(clientSocket, this);
                st.start();        //Thread starten
                connections.add(st);
            }
            serverSocket.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    //Account-bezogene Methoden
    
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

    public void changeUsername(String oldUsername, String password, String newUsername){
        for(User user : allUsers){
            if(user.getUsername().equals(oldUsername) && user.getPassword().equals(password)){
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