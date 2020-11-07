import java.io.*;
import java.net.*;
import java.util.Scanner; 

public class MulServerThread extends Thread{
    
    private Socket client;

    public MulServerThread(Socket client){
        this.client = client;
    }

    public void logIn(){
        try{
            Scanner s = new Scanner(new BufferedReader(new InputStreamReader(client.getInputStream())));
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
            pw.println("Bitte geben Sie ihre Daten in folgender Form ein: *Benutzername*, *Passwort*");
            String[] userInfo = s.nextLine().split(", ");
            String tempUsername = userInfo[0];
            String tempPassword = userInfo[1];
            
            }
        }
    }

    public void run(){
        try{
            
        }
    }
}
