
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class OutputThread extends Thread {

	Client client;
	Socket socket;
	PrintWriter writer;
	boolean shouldRun = true;

	public OutputThread(Socket socket, Client client) {
			this.socket = socket;
			this.client = client;
	}

	public void sendMessage(String message) {
		//Nachrichten-Service
			writer.println(message);
			writer.flush();
	}

	public void run() {
		try {
			OutputStream output = socket.getOutputStream();
			writer = new PrintWriter(output);
		} catch (IOException e) {
			e.printStackTrace();
		} 			
			while(shouldRun) {
				//erhalte verbindung am Leben
			}
			try {
				socket.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
}
