

public class PrivateRoom {
	public Server server;
	public String name;
    public User user1;
    public User user2;

    public PrivateRoom(Server server, String name, User user1, User user2){
        this.server = server;
        this.name = name;
        this.user1 = user1;
        this.user2 = user2;
    }
}