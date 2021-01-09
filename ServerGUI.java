import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ServerGUI implements ActionListener{
    private Server server;

    private Frame UI = new Frame("Chatserver");
    private List Serverlog = new List();
    private List Users = new List();
    private List Rooms = new List();
    private JTabbedPane Status = new JTabbedPane(JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT );

    private Frame RoomManager = new Frame();
    private Choice RoomCommandType = new Choice();
    private TextField RoomCommand = new TextField(32);
    private Button RoomCommandApply = new Button("Anwenden");

    private Frame UserManager = new Frame();
    private Choice UserCommandType = new Choice();
    private TextField UserCommand = new TextField(32);
    private Button UserCommandApply = new Button("Anwenden");

    public ServerGUI(Server server) {
        this.server = server;

        initComponents();
        initListener();
    }

    private void initComponents() {
        Status.add("Raeume", Rooms);
        Status.add("Nutzer", Users);
            Users.add("+ entbanne Nutzer");

        UI.setLayout(new BorderLayout());
        UI.add("Center", Serverlog);
        UI.add("East", Status);
        UI.setSize(1000, 800);
        UI.setVisible(true);

        UserManager.setSize(600,150);
        UserManager.setLayout(new FlowLayout(FlowLayout.CENTER));
        UserManager.add(UserCommandType);
            UserCommandType.add("Folgenden Nutzer verwarnen:");
            UserCommandType.add("Folgenden Nutzer kicken:");
            UserCommandType.add("Folgenden Nutzer bannen:");
            UserCommandType.add("Folgenden Nutzer entbannen:");
        UserManager.add(UserCommand);
        UserManager.add(UserCommandApply);

        RoomManager.setSize(600,100);
        RoomManager.setLayout(new FlowLayout(FlowLayout.CENTER));
        RoomManager.add(RoomCommandType);
            RoomCommandType.add("neuer Raum");
            RoomCommandType.add("Raum umbenennen in");
            RoomCommandType.add("Raum loeschen");
        RoomManager.add(RoomCommand);
        RoomManager.add(RoomCommandApply);
    }

    private void initListener() {

        UI.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                for (ServerThread serverThread : server.connections) {
                    serverThread.serverShutdown();
                    server.log("[Server]: Server wurde heruntergefahren!");
                }
                server.updateServerUserData();
                server.shouldRun = false;
            }
        } );

        RoomManager.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                RoomManager.setVisible(false);
            }
        });

        UserManager.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                UserManager.setVisible(false);
            }
        });

        Rooms.addActionListener(this);
        Users.addActionListener(this);

        RoomCommandApply.addActionListener(this);
        RoomCommand.addActionListener(this);

        UserCommandApply.addActionListener(this);
        UserCommand.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ev) {
        if (ev.getSource() == Rooms) {
            RoomManager.setVisible(true);
            if (Rooms.getSelectedItem() != "+ neuer Raum") {
                RoomCommand.setText(Rooms.getSelectedItem().split(" ")[0]);
                RoomCommandType.select("Raum umbenennen in");
            }
        } else if (ev.getSource() == Users) {
            UserManager.setVisible(true);

            if (Users.getSelectedItem() == "+ entbanne Nutzer") {
                UserCommandType.select("Folgenden Nutzer entbannen:");
            } else {
                UserCommand.setText(Users.getSelectedItem().split("] ")[1]);
            }
        } else if (ev.getSource() == RoomCommand || ev.getSource() == RoomCommandApply) {   // Raum-Management
            String command = RoomCommand.getText();
            String selectedRoom = Rooms.getSelectedItem().split(" ")[0];
            
            switch (RoomCommandType.getSelectedItem()) {
                case "neuer Raum":
                    new Room(command, server);
                    server.getOnlineRooms();
                    server.log("[Server]: Raum " + command + " wurde erstellt!");
                    break;
                case "Raum umbenennen in":                                          
                    server.log("[Server]: Raum " + selectedRoom + " wurde umbenannt zu" + command);
                    server.getRoom(selectedRoom).setRoomName(command);
                    server.getOnlineRooms();
                    break;
                case "Raum loeschen":
                    if (command != "public") {
                        server.deleteRoom(command);
                        server.getOnlineRooms();
                    }
                    break;
            }
            RoomCommand.setText("");
            RoomManager.setVisible(false);

        } else if (ev.getSource() == UserCommand || ev.getSource() == UserCommandApply) {   // Nutzer-Management
            String command = UserCommand.getText();

            switch (UserCommandType.getSelectedItem()) {
                case "Folgenden Nutzer verwarnen:":
                    for(ServerThread st : server.connections) {
                        if(st.username.equals(command)) {
                            st.sendMessageToClient("[Server]: Bitte halten Sie die Spielregeln ein! Sonst kick!!!");
                            server.log("[Server]: User " + command + " wurde verwarnt!");
                        }
                    }
                    break;
                case "Folgenden Nutzer kicken:":
                    for(ServerThread st : server.connections) {
                        if(st.username.equals(command)) {
                            st.room.userInRoom.remove(server.getUser(command));
                            st.sendMessageToClient("[Server]: Sie werden gekickt!");
                            st.sendMessageToClient("[Server]: Sie werden ausgeloggt!");
                            server.log("[Server]: User " + command + " wurde gekickt!");
                            server.setUserOffline(command);
                            st.quit();
                        }
                    }
                    break;
                case "Folgenden Nutzer bannen:":
                    for(User user : server.allUsers) {
                        if(user.getUsername().equals(command)) {
                            server.getUser(command).ban();
                            server.log("[Server]: User " + command + " wurde gebannt!");
                            if(user.isLoggedIn) {
                                for(ServerThread st : server.connections) {
                                    if(st.username.equals(command)) {
                                        st.room.userInRoom.remove(server.getUser(command));
                                        st.sendMessageToClient("[Server]: Sie werden gebannt!");
                                        st.sendMessageToClient("[Server]: Sie werden ausgeloggt!");
                                    server.getUser(command).ban();
                                    server.setUserOffline(command);
                                    st.quit();
                                    }
                                }
                            }
                        }
                    }
                    server.updateServerUserData();
                    break;
                case "Folgenden Nutzer entbannen:":
                    server.getUser(command).pardon();
                    server.log("[Server]: User " + command + " wurde entbannt!");
                    server.updateServerUserData();
                    break;
            }
            UserCommand.setText("");
            UserManager.setVisible(false);
        }
    }

    public void clearRooms() {
        Rooms.removeAll();
    }

    public void addRoom(String room) {
        Rooms.add(room);
    }

    public void clearUsers() {
        Users.removeAll();
    }

    public void addUser(String user) {
        Users.add(user);
    }

    public void writeLog(String message) {
        Serverlog.add(message);
    }
}