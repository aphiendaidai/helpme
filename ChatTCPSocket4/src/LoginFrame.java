import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

public class LoginFrame extends JFrame {
    private JTextField txtLoginUsername;
    private JPasswordField txtLoginPassword;
    private JTextField txtRegisterUsername;
    private JPasswordField txtRegisterPassword;

    public LoginFrame() {
        setTitle("Login and Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 500, 400);
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new GridLayout(2, 1));

        // Panel for Login
        JPanel loginPanel = new JPanel();
        loginPanel.setBorder(BorderFactory.createTitledBorder("Login"));
        loginPanel.setLayout(new GridLayout(3, 2, 10, 10));
        contentPane.add(loginPanel);

        JLabel lblLoginUsername = new JLabel("Username:");
        loginPanel.add(lblLoginUsername);

        txtLoginUsername = new JTextField();
        loginPanel.add(txtLoginUsername);
        txtLoginUsername.setColumns(10);

        JLabel lblLoginPassword = new JLabel("Password:");
        loginPanel.add(lblLoginPassword);

        txtLoginPassword = new JPasswordField();
        loginPanel.add(txtLoginPassword);
        txtLoginPassword.setColumns(10);

        JButton btnLogin = new JButton("Đăng nhập");
        loginPanel.add(btnLogin);
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = txtLoginUsername.getText();
                String password = new String(txtLoginPassword.getPassword());
                if (authenticate(username, password)) {
                    JOptionPane.showMessageDialog(null, "Đăng nhập thành công");
                    EventQueue.invokeLater(() -> {
                        try {
                            ClientChatter clientChatter = new ClientChatter(username);
                            clientChatter.setVisible(true);
                            dispose(); // Close login frame
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                } else {
                    JOptionPane.showMessageDialog(null, "Tài khoản không khả dụng");
                }
            }
        });

        // Panel for Registration
        JPanel registerPanel = new JPanel();
        registerPanel.setBorder(BorderFactory.createTitledBorder("Đăng Ký"));
        registerPanel.setLayout(new GridLayout(3, 2, 10, 10));
        contentPane.add(registerPanel);

        JLabel lblRegisterUsername = new JLabel("Username:");
        registerPanel.add(lblRegisterUsername);

        txtRegisterUsername = new JTextField();
        registerPanel.add(txtRegisterUsername);
        txtRegisterUsername.setColumns(10);

        JLabel lblRegisterPassword = new JLabel("Password:");
        registerPanel.add(lblRegisterPassword);

        txtRegisterPassword = new JPasswordField();
        registerPanel.add(txtRegisterPassword);
        txtRegisterPassword.setColumns(10);

        JButton btnRegister = new JButton("Đăng ký");
        registerPanel.add(btnRegister);
        btnRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = txtRegisterUsername.getText();
                String password = new String(txtRegisterPassword.getPassword());
                if (register(username, password)) {
                    JOptionPane.showMessageDialog(null, "Tạo tài khoản thành công");
                    reset();
                } else {
                    JOptionPane.showMessageDialog(null, "Tạo tài khoản thất bại");
                }
            }
        });
    }

    private boolean authenticate(String username, String password) {
        try (Socket socket = new Socket("192.168.1.9", 12340);
             DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            writer.writeBytes("AUTH:" + username + ":" + password + "\n");
            writer.flush();

            String response = reader.readLine();
            return "SUCCESS".equals(response);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private boolean register(String username, String password) {
        try (Socket socket = new Socket("192.168.1.9", 12340);
             DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            writer.writeBytes("REGISTER:" + username + ":" + password + "\n");
            writer.flush();

            String response = reader.readLine();
            return "SUCCESS".equals(response);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private void reset() {
        txtRegisterUsername.setText("");
        txtRegisterPassword.setText("");
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                LoginFrame frame = new LoginFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
