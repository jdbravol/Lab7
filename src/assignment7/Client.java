package assignment7;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
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

public class Client extends Application{
    private TextArea incoming;
    private TextField outgoing;
    private BufferedReader reader;
    private PrintWriter writer;

    private void initView(){
        //Initial Login page


        Stage loginStage = new Stage();
        GridPane mainPanel = new GridPane();
        Scene loginScene;
        //Username & Password input
        Label userName = new Label("User Name: ");
        TextField user = new TextField();
        Label pass = new Label("Password: ");
        PasswordField password = new PasswordField();
        mainPanel.add(userName, 0, 0);
        mainPanel.add(user, 0, 1);
        mainPanel.add(pass, 1, 0);
        mainPanel.add(password, 1, 1);


        //buttons
        Button newUser = new Button("Create User");
        Button forgotPass = new Button("forgot Password");
        Button login = new Button("Login");
        mainPanel.add(newUser, 2, 0);
        mainPanel.add(login, 2, 1);
        mainPanel.add(forgotPass, 3, 0);

        //set Handlers
        login.setOnAction(e ->{
            //TODO
            try {

            }
            catch (Exception ex){

            }
        });

        newUser.setOnAction(e ->{
            //TODO
            try {

            }
            catch (Exception ex){

            }
        });

        forgotPass.setOnAction(e ->{
            //TODO
            try {

            }
            catch (Exception ex){

            }
        });


        // Putting everything together
        loginStage.setTitle("Welcome to Messenger");

    }

    private void MessengerView() {


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

    @Override
    public void start(Stage primaryStage) throws Exception {
        initView();
        setUpNetworking();
        MessengerView();
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
        launch(args);
    }

    class IncomingReader implements Runnable {
        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {

                    //incoming.append(message + "\n");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private class loginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            writer.println(outgoing.getText());
            writer.flush();
            outgoing.setText("");
            outgoing.requestFocus();
            //TODO: 11/29/16
        }
    }
}
