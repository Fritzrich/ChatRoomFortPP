/*import java.util.ArrayList;

public class LoginManager{

    private ArrayList<User> allUsers = new ArrayList<User>();

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

    public void changePassword(String username, String oldPassword, String newPassword){
        for(User user : allUsers){
            if(user.getUsername().equals(username) && user.getPassword().equals(oldPassword)){
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

}*/