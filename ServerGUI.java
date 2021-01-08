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
        Rooms.add("Test");
        Users.add("Testuser");
    }

    private void initComponents() {
        Status.add("Raeume", Rooms);
        Status.add("Nutzer", Users);

        UI.setLayout(new BorderLayout());
        UI.add("Center", Serverlog);
        UI.add("East", Status);
        UI.setSize(1000, 800);
        UI.setVisible(true);

        UserManager.setSize(600,100);
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
            RoomCommandType.add("Raum löschen");
        RoomManager.add(RoomCommand);
        RoomManager.add(RoomCommandApply);
    }

    private void initListener() {

        UI.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                server.handleCommand(".quit");
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
    }

    public void actionPerformed(ActionEvent ev) {
        if (ev.getSource() == Rooms) {
            RoomManager.setVisible(true);
        }
        else if (ev.getSource() == Users) {
            UserManager.setVisible(true);
        }
        /*
        *TODO: 
        -bei Raumerstellung, aenderung & del muss sowohl Raumliste als auch userliste mit raumprefix aktualisiert werden,
            dasselbe gilt, falls user raum wechselt oder user ausgeloggt wird
                choice{neuer Raum}:
                        createRoom-Funktionalität kopieren
                choice{Namen aendern in }:
                        Rooms.selected() mit *.getRooms(this.Rooms.selected()) in server<Rooms> ausfindig machen und *.setRoomName(Inhalt von TextField nehmen)
                choice{loeschen}:
                        deleteRoom-Funktionalität kpieren & zu löschenden raumnamen noch einmal in textfeld hineinschreiben
                choice{user*}: analog zu neuer Raum -Choices 1 & 3 JEDOCH: Nutzername wird bei Choices 1,2,3 bereits in das Feld geschrieben durch *.setText(*.selected())
        */
    }

    public void writeLog(String message) {
        Serverlog.add(message);
    }
}