import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class OutputThread extends Thread {

	Client client;
	Socket socket;
	BufferedWriter writer;
	boolean shouldRun;

	public OutputThread(Socket socket, Client client) {
			this.socket = socket;
			this.client = client;
	}

	public void run() {
		Scanner scanner = new Scanner(System.in);
		try {
			OutputStream output = socket.getOutputStream();
			writer = new BufferedWriter(new PrintWriter(output));
			//Login-Start
			System.out.println("\nGeben Sie ihren Benutzernamen ein: ");
			String username = scanner.nextLine();
			client.setUsername(username);
			writer.write(username);
			writer.flush();
			if(client.nameIsTaken == true) {
				while(client.isLoggedIn == false) {
					System.out.println("\nGeben Sie ihr Passwort ein: ");
					String password = scanner.nextLine();
					writer.write(password);
					writer.flush();
				}
			} else {
				System.out.println("\nGeben Sie ihr Passwort ein: ");
				String password = scanner.nextLine();
				writer.write(password);
			} //Login-Ende
			//Nachrichten-Service
			String message;
			while(shouldRun) {
				try{
					message = scanner.nextLine();
					writer.write(message);
					writer.flush();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			socket.close();
			writer.close();
			scanner.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}