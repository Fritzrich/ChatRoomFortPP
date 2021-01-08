import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class InputThread extends Thread	{
	
	Client client;
	Socket socket;
	BufferedReader reader;
	boolean shouldRun = true;
	
	public InputThread(Socket socket, Client client) {
			this.socket = socket;
			this.client = client;
			try {
				InputStream input =socket.getInputStream();	
				reader = new BufferedReader(new InputStreamReader(input));
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	//Empf�ngt, gibt Nachrichten aus und bearbeitet falls n�tig
	public void run() {
		while(shouldRun) {
			try {
				String message = reader.readLine();
				System.out.println(message);
				
				//Login Ma�nahmen
				if(message.equals("[Server]: Sie haben bereits einen Account! Geben Sie das korrekte Passwort ein:  ")) {
					client.nameIsTaken = true;
					client.UI.postMessage(message);
				}
				else if(message.startsWith("[Server]: Sie sind eingeloggt als ")) {
					message = message.substring(34, message.length());
					String[] messageParts = message.split(" in Raum ");
					client.username = messageParts[0];
					client.room = messageParts[1];
					client.isLoggedIn = true;
					client.UI.setStatusUsername();
					client.UI.postMessage(message);
				}
				//Command Responses
				else if(message.equals("[Server]: Sie werden ausgeloggt!")) {
					client.quit();
				}
				else if(message.startsWith("[Server]: Es sind ")) {
					client.UI.setUsers(message.split(" :: "));
				}
				else if(message.startsWith("[Server]: Raeume ")) {
					client.UI.setRooms(message.split(" :: "));
				}
				else {
					client.UI.postMessage(message);
				}
			} catch(IOException e) {
				e.printStackTrace();
				shouldRun = false;
			}
		}
		try {
			socket.close();
			reader.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
