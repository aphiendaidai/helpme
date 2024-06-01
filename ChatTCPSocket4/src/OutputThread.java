import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Base64;

public class OutputThread extends Thread {
        private final Socket socket;
        private final JTextArea txt;
        private final BufferedReader reader;
        private final String sender;
        private final String receiver;

        public OutputThread(Socket socket, JTextArea txt, BufferedReader reader, String sender, String receiver) {
            this.socket = socket;
            this.txt = txt;
            this.reader = reader;
            this.sender = sender;
            this.receiver = receiver;
        }

        @Override
        public void run() {
            try {
                String msg;
                while ((msg = reader.readLine()) != null) {
                    if (!msg.isEmpty()) {
                        System.out.println("Received message: " + msg);
                        String decryptedMessage = new String(Base64.getDecoder().decode(msg));
                        SwingUtilities.invokeLater(() -> txt.append("\n" + receiver + ": " + decryptedMessage));
                    }
                }
            } catch (SocketException e) {
                if ("Socket closed".equals(e.getMessage())) {
                    System.out.println("Socket has been closed.");
                } else {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeSocket();
            }
        }


        private void closeSocket() {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


