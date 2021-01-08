import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.PanelUI;

public class ClientGUI implements ActionListener{

    private Client client;
    public boolean canPasswordBeSent = false;
    private JFrame UI;
    private Panel StatusPanel = new Panel();
    private Panel ChatPanel = new Panel();
    private Panel MessagePanel = new Panel();
    private JTabbedPane RoomsTab = new JTabbedPane(JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT );

    private Label Username = new Label();
    private Label Connection = new Label();
    private Button ToggleLists = new Button("Zeige Raeume");
    private Button Send = new Button("Senden");
    private List Chat = new List();
    private List Rooms = new List();
    private List Users = new List();
    private TextField Message = new TextField(256);

    private Frame logInFrame = new Frame();
    private TextField logInUsername = new TextField(32);
    private TextField logInPassword = new TextField(32);
    private Button logIn = new Button();
    private Label logInUsernameLabel = new Label();
    private Label logInPasswordLabel = new Label();
    private Panel infoPanel = new Panel();
    private Panel inputPanel = new Panel();
    private Panel logInButtonPanel = new Panel();
    
    public ClientGUI (Client client) {
        this.client = client;

        initComponents();
        initListener();
        logInFrame.setVisible(true);
    }

    private void initComponents () {
        StatusPanel.setLayout(new BorderLayout());
        StatusPanel.add("North", Username);
            Username.setText("Noch nicht angemeldet.");
            Username.setAlignment(Label.CENTER);
        StatusPanel.add("South", Connection);
            Connection.setText("Mit keinem Server verbunden.");
            Connection.setAlignment(Label.CENTER);

        ChatPanel.setLayout(new BorderLayout(0, 20));
        ChatPanel.add("Center", Chat);
        ChatPanel.add("South", MessagePanel);
            MessagePanel.setLayout(new BorderLayout(20, 0));
            MessagePanel.add("Center", Message);
            MessagePanel.add("East", Send);
        
        logInFrame.setSize(400,150);
        logInFrame.setLayout(new BorderLayout());
        infoPanel.setLayout(new BorderLayout());
        inputPanel.setLayout(new BorderLayout());
        logInButtonPanel.setLayout(new FlowLayout());
        inputPanel.add("North", logInUsername);
        inputPanel.add("South", logInPassword);
        logInButtonPanel.add(logIn);
        logInFrame.add("South", logInButtonPanel);
        infoPanel.add("North", logInUsernameLabel);
        infoPanel.add("South", logInPasswordLabel);
        logInUsernameLabel.setText("Benutzername");
        logInPasswordLabel.setText("Passwort");
        logInFrame.add("West", infoPanel);
        logInFrame.add("East", inputPanel);

            
        RoomsTab.add("Raeume", Rooms);
        RoomsTab.add("Nutzer", Users);

        UI = new JFrame("Client-Chat");
        UI.setLayout(new BorderLayout(40, 40));
        UI.add("East", RoomsTab);
        UI.add("North", StatusPanel);
        UI.add("Center", ChatPanel);
    
        UI.setSize(1000, 800);
        UI.setVisible(true);
    }

    private void initListener() {

        UI.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                client.out.writer.println(".quit");
                client.out.writer.flush();
            }
        } );

        UI.addWindowFocusListener(new WindowAdapter() {
            public void windowGainedFocus(WindowEvent we) {
                Message.requestFocusInWindow();
            }
        });

        logInFrame.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                logInFrame.setVisible(false);
                client.out.writer.println(".quit");
                client.out.writer.flush();
            }
        });

        Send.addActionListener(this);

        Message.addActionListener(this);

        Rooms.addActionListener(this);

        ToggleLists.addActionListener(this);

        logIn.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ev) {
        if (ev.getSource() == Send) {             //sende Texteingabe über OutputThread
            client.out.sendMessage(Message.getText());
            Chat.add(Message.getText());
            Message.setText("");
        }

        else if (ev.getSource() == Message) {
            client.out.sendMessage(Message.getText());
            Chat.add(Message.getText());
            Message.setText("");
        }
        else if (ev.getSource() == Rooms) {
            client.out.sendMessage(".changeRoomTo" + ev.getActionCommand());
        }
        else if (ev.getSource() == ToggleLists) {
            if (ToggleLists.getLabel().equals("Zeige Nutzer")) {
                ToggleLists.setLabel("Zeige Raeume");
            } else {
                ToggleLists.setLabel("Zeige Nutzer");
            }          
        }
        else if (ev.getSource() == logIn) {
            if(!client.nameIsTaken) client.out.sendMessage(logInUsername.getText());
            while(!canPasswordBeSent){
            }
            client.out.sendMessage(logInPassword.getText());
            logInFrame.setVisible(false);
        }
    }
        

    public void setStatusConnection() {
        Connection.setText("Verbunden mit Server auf: " + client.socket.getRemoteSocketAddress());
    }

    public void setStatusUsername() {
        Username.setText("Eingeloggt als: " + client.username + " in Raum " + client.room);
    }

    public void postMessage(String message) {
        Chat.add(message);
    }

    public void setUsers(String[] UserList) {
        Users.removeAll();
        for (int i = 1; i < UserList.length; ++i) {
            Users.add(UserList[i]);
        }
    }

    public void setRooms(String[] RoomList) {
        Rooms.removeAll();
        for (int i = 1; i < RoomList.length; ++i) {
            Rooms.add(RoomList[i]);
        }
    }
}
