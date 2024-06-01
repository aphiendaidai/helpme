import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.util.Base64;

public class Chatpanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private Socket socket;
    private BufferedReader reader;
    private DataOutputStream writer;
    private JTextArea txtMessages;
    private String sender;
    private String receiver;

    public Chatpanel(Socket socket, String sender, String receiver) {
        this.socket = socket;
        this.sender = sender;
        this.receiver = receiver;

        setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(null, "Message", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        add(panel, BorderLayout.SOUTH);
        panel.setLayout(new BorderLayout());

        JTextArea txtMessage = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(txtMessage);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton btnSend = new JButton("Send");
        btnSend.addActionListener(e -> sendMessage(txtMessage));
        panel.add(btnSend, BorderLayout.EAST);

        txtMessages = new JTextArea();
        txtMessages.setEditable(false);
        JScrollPane scrollPane1 = new JScrollPane(txtMessages);
        add(scrollPane1, BorderLayout.CENTER);

        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new DataOutputStream(socket.getOutputStream());
            new OutputThread(socket, txtMessages, reader, sender, receiver).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void sendMessage(JTextArea txtMessage) {
        String message = txtMessage.getText().trim();
        if (message.isEmpty()) return;
        try {
            if (socket != null && !socket.isClosed()) {
                System.out.println("Sending message: " + message);
                String encodedMessage = Base64.getEncoder().encodeToString(message.getBytes());
                writer.writeBytes(encodedMessage + "\n");
                writer.flush();
                txtMessages.append("\n" + sender + ": " + message);
                txtMessage.setText("");
            } else {
                System.out.println("Socket is closed, unable to send message.");
            }
        } catch (SocketException e) {
            System.out.println("SocketException: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
