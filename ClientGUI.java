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

    private Label Username = new Label();
    private Label Connection = new Label();
    private Button Send = new Button("Senden");
    private List Chat = new List();
    private List Rooms = new List();
    private List Users = new List();
    private TextField Message = new TextField(256);

    private Menu Options = new Menu("Optionen");
    private MenuBar Bar = new MenuBar();

    private Frame NewUsername = new Frame("Benutzernamen aendern");
    private Label NewUsernameError = new Label("");
    private TextField NewUsernameText = new TextField(32);
    private Button NewUsernameApply = new Button("Aendern");

    private Frame NewPassword = new Frame("Passwort aendern");
    private TextField NewPasswordText = new TextField(32);
    private Button NewPasswordApply = new Button("Aendern");
    
    public ClientGUI (Client client) {
        this.client = client;

        initComponents();
        initListener();
    }

    private void initComponents () {

        NewUsername.setLayout(new FlowLayout(FlowLayout.CENTER));
        NewUsername.setSize(610, 150);
            NewUsername.add(NewUsernameText);
            NewUsername.add(NewUsernameApply);
            NewUsername.add(NewUsernameError);
        
            NewPassword.setLayout(new FlowLayout(FlowLayout.LEFT));
            NewPassword.setSize(610, 150);
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

<<<<<<< HEAD
        Bar.add(Options);
            Options.add("neuer Nutzername");
            Options.add("neues Passwort");

=======
>>>>>>> c7cc036cee02c4e30bdeec1f1c3ed358248bc548
        UI = new Frame("Client-Chat");
        UI.setLayout(new BorderLayout(40, 40));

        UI.add("East", RoomsTab);
        UI.add("North", StatusPanel);
        UI.add("Center", ChatPanel);

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
                client.out.writer.println(".quit");
                client.out.writer.flush();
            }
        } );

        UI.addWindowFocusListener(new WindowAdapter() {
            public void windowGainedFocus(WindowEvent we) {
                Message.requestFocusInWindow();
            }
        });

        Send.addActionListener(this);
        Message.addActionListener(this);
        Rooms.addActionListener(this);

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

    public boolean isNewUsernameVisible () {
        return NewUsername.isVisible();
    }

    public void setNewUsernameInvisible() {
        NewUsernameText.setText("");
        NewUsername.setVisible(false);
    }

    public void setNewUsernameError() {
        NewUsernameError.setText("Dieser Benutzername ist bereits vergeben!");
    }
}
