package assignment7;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
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

    private void initView(Stage primaryStage){
        //Initial Login page
        BorderPane paneForUser = new BorderPane();
        paneForUser.setPadding(new Insets(5, 5, 5, 5));
        paneForUser.setLeft(new Label("User Name: "));

        TextField username = new TextField();
        username.setAlignment(Pos.BOTTOM_RIGHT);
        paneForUser.setCenter(username);

        BorderPane paneForPass = new BorderPane();
        paneForPass.setPadding(new Insets(5, 5, 5, 5));
        paneForPass.setLeft(new Label("Password: "));

        TextField password = new TextField();
        password.setAlignment(Pos.BOTTOM_RIGHT);
        paneForUser.setCenter(password);

        BorderPane mainPane = new BorderPane();
        // Text area to display contents

        mainPane.setTop(paneForUser);
        mainPane.setCenter(paneForPass);


        // Create a scene and place it in the stage
        Scene scene = new Scene(mainPane, 450, 200);
        primaryStage.setTitle("Client"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage

    }

    private void MessengerView(Stage primaryStage) {
        BorderPane paneForTextField = new BorderPane();
        paneForTextField.setPadding(new Insets(5, 5, 5, 5));
        paneForTextField.setStyle("-fx-border-color: green");
        paneForTextField.setLeft(new Label("Enter a radius: "));

        TextField tf = new TextField();
        tf.setAlignment(Pos.BOTTOM_RIGHT);
        paneForTextField.setCenter(tf);

        BorderPane mainPane = new BorderPane();
        // Text area to display contents

        TextArea ta = new TextArea();
        mainPane.setCenter(new ScrollPane(ta));
        mainPane.setTop(paneForTextField);


        // Create a scene and place it in the stage
        Scene scene = new Scene(mainPane, 450, 200);
        primaryStage.setTitle("Client"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage


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
        initView(primaryStage);
        setUpNetworking();
        MessengerView(primaryStage);
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
