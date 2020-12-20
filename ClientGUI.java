import java.awt.*;

public class ClientGUI {

    private Client client;
    private Frame UI;
    private Panel p;

    private Button Send;
    private List Chat;
    private List Rooms;
    private List Users;
    private TextField Message;
    
    public ClientGUI (Client client) {
        this.client = client;

        initComponents();
        initListener();
    }

    private void initComponents () {
        UI = new Frame("Client-Chat");
    
        UI.setSize(1000, 800);
        UI.setVisible(true);
    }

    private void initListener () {
        UI.addWindowListener( new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent we) {
                client.out.writer.println(".quit");
                client.out.writer.flush();
            }
        } );
    }
}
