public class User {
    
    private String username;
    private String password;
    public boolean isLoggedIn;

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }
    
    public User(String username) {
    	this.username = username;
    }

    void setUsername(String username){
        this.username = username;
    }

    String getUsername(){
        return username;
    }

    void setPassword(String password){
        this.password = password;
    }

    String getPassword(){
        return password;
    }
}
