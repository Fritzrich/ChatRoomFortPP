import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ClientGUI implements ActionListener{

    private Client client;

    private Frame UI;
    private Panel StatusPanel = new Panel();
    private Panel ChatPanel = new Panel();
    private Panel MessagePanel = new Panel();
    private JTabbedPane RoomsTab = new JTabbedPane(JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT );

    private Label Username = new Label();                   //Info oben im Client
    private Label Connection = new Label();

    private Button Send = new Button("Senden");
    private List Chat = new List();
    private List Rooms = new List();
    private List Users = new List();
    private TextField Message = new TextField(256);

    private Menu Options = new Menu("Optionen");            //Optionen Namen, Passwort aendern
    private MenuBar Bar = new MenuBar();

    private Frame NewUsername = new Frame("Benutzernamen aendern");         //Namen aendern + Error falls bereits vorhanden
    private Label NewUsernameError = new Label("");
    private TextField NewUsernameText = new TextField(32);
    private Button NewUsernameApply = new Button("Aendern");

    private Frame NewPassword = new Frame("Passwort aendern");              //Passwort aendern
    private TextField NewPasswordText = new TextField(32);
    private Button NewPasswordApply = new Button("Aendern");

    private Frame privateChat = new Frame("Direkter Chat");
    private Label chatPartner = new Label("");
    private Button sendDirect = new Button("Senden");
    private TextField writeDirect = new TextField(256);
    private List directChat = new List();
    private Panel directPanel = new Panel();
    
    public ClientGUI (Client client) {
        this.client = client;

        initComponents();
        initListener();
    }

    private void initComponents () {

        privateChat.setLayout(new BorderLayout());
        privateChat.setSize(750, 600);
            directPanel.setLayout(new BorderLayout());
            directPanel.add("Center", writeDirect);
            directPanel.add("East", sendDirect);
        privateChat.add("North", chatPartner);
        privateChat.add("Center", directChat);
        privateChat.add("South", directPanel);

        NewUsername.setLayout(new FlowLayout(FlowLayout.CENTER));
        NewUsername.setSize(550, 150);
            NewUsername.add(NewUsernameText);
            NewUsername.add(NewUsernameApply);
            NewUsername.add(NewUsernameError);
        
            NewPassword.setLayout(new FlowLayout(FlowLayout.LEFT));
            NewPassword.setSize(550, 150);
                NewPassword.add(NewPasswordText);
                NewPassword.add(NewPasswordApply);

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
            
        RoomsTab.add("Raeume", Rooms);
        RoomsTab.add("Nutzer", Users);

        Bar.add(Options);
            Options.add("neuer Nutzername");
            Options.add("neues Passwort");

        UI = new Frame("Client-Chat");
        UI.setLayout(new BorderLayout(40, 40));

        UI.add("East", RoomsTab);
        UI.add("North", StatusPanel);
        UI.add("Center", ChatPanel);

        UI.setMenuBar(Bar);

        UI.setBackground(Color.lightGray);              //Colors
        StatusPanel.setBackground(Color.lightGray);
        ChatPanel.setBackground(Color.lightGray);
        MessagePanel.setBackground(Color.lightGray);

        UI.setSize(1000, 800);
        UI.setVisible(true);
    }

    private void initListener() {

        UI.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (client.out == null) {
                    System.exit(0);
                } else {
                    client.out.writer.println(".quit");
                    client.out.writer.flush();
                }
            }
        } );

        UI.addWindowFocusListener(new WindowAdapter() {
            public void windowGainedFocus(WindowEvent we) {
                Message.requestFocusInWindow();
            }
        });

        NewUsername.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                NewUsernameText.setText("");
                NewUsernameError.setText("");
                NewUsername.setVisible(false);
            }
        });

        NewPassword.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                NewPasswordText.setText("");
                NewPassword.setVisible(false);
            }
        });

        privateChat.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                client.out.sendMessage(".closeDirectChat");
                exitDirectChat();
            }
        });

        Send.addActionListener(this);
        Message.addActionListener(this);
        Rooms.addActionListener(this);
        Options.addActionListener(this);
        NewUsernameText.addActionListener(this);
        NewUsernameApply.addActionListener(this);
        NewPasswordText.addActionListener(this);
        NewPasswordApply.addActionListener(this);
        Users.addActionListener(this);
        writeDirect.addActionListener(this);
        sendDirect.addActionListener(this);

    }

    public void actionPerformed(ActionEvent ev) {
        if (ev.getSource() == Send && client.out != null) {             //sende Texteingabe über OutputThread
            client.out.sendMessage(Message.getText());
            Chat.add(Message.getText());
            Message.setText("");
        }
        else if (ev.getSource() == Message && client.out != null) {
            client.out.sendMessage(Message.getText());
            Chat.add(Message.getText());
            Message.setText("");
        }
        else if (ev.getSource() == Rooms) {
            client.out.sendMessage(".changeRoomTo" + ev.getActionCommand());
        }
        else if (ev.getActionCommand() == "neuer Nutzername") {
            if(client.isLoggedIn) NewUsername.setVisible(true);       
        } else if (ev.getActionCommand() == "neues Passwort") {
            if(client.isLoggedIn) NewPassword.setVisible(true);
        } else if ((ev.getSource() == NewUsernameText || ev.getSource() == NewUsernameApply) && client.out != null) {
        client.out.sendMessage(".changeUsername" + NewUsernameText.getText());
        } else if ((ev.getSource() == NewPasswordText || ev.getSource() == NewPasswordApply)&& client.out != null) {
            client.out.sendMessage(".changePassword" + NewPasswordText.getText());
            NewPassword.setVisible(false);
        } else if (ev.getSource() == Users) {
            if(!privateChat.isVisible()){
                privateChat.setVisible(true);
                client.out.sendMessage(".chatWith" + Users.getSelectedItem().split(" ")[0]);
            }
        } else if (ev.getSource() == writeDirect || ev.getSource() == sendDirect) {
            client.out.sendPrivateMessage(writeDirect.getText());
            directChat.add(writeDirect.getText());
            writeDirect.setText("");
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

    public void setNewUsernameInvisible() {
        NewUsernameText.setText("");
        NewUsernameError.setText("");
        NewUsername.setVisible(false);
    }

    public void setNewUsernameError() {
        NewUsernameError.setText("Dieser Benutzername ist bereits vergeben!");
    }

    public void postDirectMessage(String message){
        directChat.add(message);
    }

    public void exitDirectChat(){
        directChat.removeAll();
        writeDirect.setText("");
        privateChat.setVisible(false);
    }

    public void setChatPartner(String username) {
        chatPartner.setText("Privater Chat mit " + username);
        privateChat.setVisible(true);
    }
}
