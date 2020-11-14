import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class OutputThread extends Thread {

	Client client;
	Socket socket;
	PrintWriter writer;
	boolean shouldRun = true;

	public OutputThread(Socket socket, Client client) {
			this.socket = socket;
			this.client = client;
	}

	public void run() {
		Scanner scanner = new Scanner(System.in);
		try {
			OutputStream output = socket.getOutputStream();
			writer = new PrintWriter(output);
		} catch (IOException e) {
			e.printStackTrace();
		} 
			//Login-Start
			System.out.println("\nGeben Sie ihren Benutzernamen ein: ");
			String username = scanner.nextLine();
			client.setUsername(username);
			writer.println(username);
			writer.flush();
			if(client.nameIsTaken == true) {
				while(client.isLoggedIn == false) {
					String password = scanner.nextLine();
					writer.println(password);
					writer.flush();
				}
			} else {
				String password = scanner.nextLine();
				writer.println(password);
				writer.flush();
			} //Login-Ende
			//Nachrichten-Service
			String message;
			while(shouldRun) {
					message = scanner.nextLine();
					writer.println(message);
					writer.flush();
					if(message.equals(".quit")) shouldRun = false;
			}
			try {
				socket.close();
				writer.close();
				scanner.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
}