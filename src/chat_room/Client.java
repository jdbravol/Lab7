package chat_room;

import javafx.application.Application;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by juanbravo on 11/28/16.
 */

public class Client{
    private JTextArea incoming;
    private JTextField outgoing;
    private BufferedReader reader;
    private PrintWriter writer;


    public void run() throws Exception {
        initView();
        setUpNetworking();
    }

    private void initView() {
        // general frame
        JFrame frame = new JFrame("Messenger");
        JPanel mainPanel = new JPanel();

        // Incoming text TextArea
        incoming = new JTextArea(15, 40);
        incoming.setLineWrap(true);
        incoming.setWrapStyleWord(true);
        incoming.setEditable(false); // cant be edited
        JScrollPane qScroller = new JScrollPane(incoming);
        qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        // Outgoing text field
        outgoing = new JTextField(50);

        // Buttons
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new SendButtonListener()); //action of the button

        // Putting everything together
        mainPanel.add(qScroller);
        mainPanel.add(outgoing);
        mainPanel.add(sendButton);
        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        frame.setSize(500, 350);
        frame.setVisible(true);

    }

    private void setUpNetworking() throws Exception {
        @SuppressWarnings("resource")



        Socket sock = new Socket("127.0.0.1", 4242);
        InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
        reader = new BufferedReader(streamReader);
        writer = new PrintWriter(sock.getOutputStream());
        System.out.println("networking established");
        Thread readerThread = new Thread(new IncomingReader());
        readerThread.start();
    }

    class SendButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            writer.println(outgoing.getText());
            writer.flush();
            outgoing.setText("");
            outgoing.requestFocus();
        }
    }

    public static void main(String[] args) {
        try {
            new Client().run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class IncomingReader implements Runnable {
        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {

                    incoming.append(message + "\n");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
