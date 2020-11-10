import java.io.*;
import java.net.*;

public class MulServerThread extends Thread {

    Socket client;
    Server server;
    DataInputStream din;
    DataOutputStream dout;
    boolean shouldRun = true;

    public MulServerThread(Socket client, Server server) {
        super("MulServerThread");
        this.client = client;
        this.server = server;
    }

    /*
     * public void logIn(){ try{ Scanner s = new Scanner(new BufferedReader(new
     * InputStreamReader(client.getInputStream()))); PrintWriter pw = new
     * PrintWriter(new OutputStreamWriter(client.getOutputStream())); pw.
     * println("Bitte geben Sie ihre Daten in folgender Form ein: *Benutzername*, *Passwort*"
     * ); String[] userInfo = s.nextLine().split(", "); String tempUsername =
     * userInfo[0]; String tempPassword = userInfo[1];
     * 
     * } } }
     */

    public void sendMessageToClient(String message) {
        try{
            dout.writeUTF(message);
            dout.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
        
    }

    public void sendMessageToAllClients(String message) {
        for (MulServerThread thread : server.connections){
            thread.sendMessageToClient(message);
        }
    }

    public void handleCommand(String textIn){

    }

    public void run() {
        try {
            din = new DataInputStream(client.getInputStream());
            dout = new DataOutputStream(client.getOutputStream());
            while (shouldRun) {
                while (din.available() == 0) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            String textIn = din.readUTF();
            if(textIn.startsWith("/")){            //Filtere Kommando-Anfragen
                handleCommand(textIn);
            }
            else sendMessageToAllClients(textIn);
            }
            din.close();
            dout.close();
            client.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
