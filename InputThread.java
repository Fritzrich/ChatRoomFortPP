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
	
	//Empfängt, gibt Nachrichten aus und bearbeitet falls nötig
	public void run() {
		while(shouldRun) {
			try {
				String message = reader.readLine();
				System.out.println(message);
				//Login Maßnahmen
				if(message.equals("\n[Server]: Sie haben bereits einen Account! Geben Sie das korrekte Passwort ein:  ")) {
					client.nameIsTaken = true;
				}
				if(message.equals("\n[Server]: Sie sind eingeloggt!")) {
					client.isLoggedIn = true;
				}
			} catch(IOException e) {
				System.out.println("HIER liegt der Fehler INPUT");
				e.printStackTrace();
				shouldRun = false;
			}
		}
		try {
			socket.close();
			reader.close();
			shouldRun = false;
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}