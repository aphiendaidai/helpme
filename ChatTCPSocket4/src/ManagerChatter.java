import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ManagerChatter extends JFrame implements Runnable {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtServerPort;
    private JTabbedPane tabbedPane;
    private ServerSocket srvSocket;
    private Thread serverThread;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ManagerChatter frame = new ManagerChatter();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public ManagerChatter() {
        initialize();
    }

    private void initialize() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        contentPane.add(panel, BorderLayout.NORTH);
        panel.setLayout(new GridLayout(1, 2, 0, 0));

        JLabel lblManagerPort = new JLabel("Manager Port:");
        lblManagerPort.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lblManagerPort);

        txtServerPort = new JTextField();
        txtServerPort.setText("12340");
        panel.add(txtServerPort);
        txtServerPort.setColumns(10);

        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        contentPane.add(tabbedPane, BorderLayout.CENTER);

        try {
            int serverPort = Integer.parseInt(txtServerPort.getText());
            srvSocket = new ServerSocket(serverPort);
            serverThread = new Thread(this);
            serverThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
        	
            try {
            	
                Socket clientSocket = srvSocket.accept();
                if (clientSocket != null) {
                    handleClientRequest(clientSocket);
                }
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleClientRequest(Socket clientSocket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             DataOutputStream writer = new DataOutputStream(clientSocket.getOutputStream())) {

            String request = reader.readLine();
            String[] parts = request.split(":");

            if (parts.length < 3) {
                writer.writeBytes("FAILURE\n");
                writer.flush();
                return;
            }

            String command = parts[0];
            String username = parts[1];
            String password = parts[2];

            if ("AUTH".equals(command)) {
                if (authenticate(username, password)) {
                    writer.writeBytes("SUCCESS\n");
                    writer.flush();

                    // Add the chat panel for this client
                    Chatpanel chatPanel = new Chatpanel(clientSocket, "Manager", username);
                    SwingUtilities.invokeLater(() -> tabbedPane.add(username, chatPanel));
                } else {
                    writer.writeBytes("FAILURE\n");
                    writer.flush();
                }
            } else if ("REGISTER".equals(command)) {
                if (register(username, password)) {
                    writer.writeBytes("SUCCESS\n");
                } else {
                    writer.writeBytes("FAILURE\n");
                }
                writer.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private boolean authenticate(String username, String password) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/chat_application", "root", "");
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username=? AND password=?")) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean register(String username, String password) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/chat_application", "root", "");
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)")) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
