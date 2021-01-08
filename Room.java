import java.util.ArrayList;

public class Room {
	public Server server;
	public String name;
	public ArrayList<User> userInRoom = new ArrayList<User>();
	
	public Room(String name, Server server) {
		this.name = name;
		this.server = server;
		server.allRooms.add(this);
	}
	
	public String getRoomName() {
		return name;
	}
	
	public void setRoomName(String name) {
		this.name = name;
	}
	
	public void	addUser(User newUser) {
		userInRoom.add(newUser);
	}

	public void removeUser(User oldUser) {
		userInRoom.remove(oldUser);
	}

	public String userInRoom() {
		String userList = "";
		
		for (User user : userInRoom) {
			userList += " :: " + user.getUsername();
		}
		return userList;
	}
	
}
