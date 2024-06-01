import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.net.Socket;

public class ClientChatter extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    public ClientChatter(String username) {
        setTitle("Chat Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 400);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(null, "Chat", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        contentPane.add(panel, BorderLayout.CENTER);
        panel.setLayout(new BorderLayout(0, 0));

        	try {
        	    Socket socket = new Socket("192.168.1.9", 12340);
        	    Chatpanel chatPanel = new Chatpanel(socket, username, "receiver");
        	    panel.add(chatPanel, BorderLayout.CENTER);
        	} catch (Exception e) {
        	    e.printStackTrace();
        	}

    }
}
