package assignment7;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event.*;
import javafx.event.EventHandler;
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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.swing.*;



import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;

/**
 * Created by juanbravo on 11/28/16.
 */

public class Client extends Application{
    private TextArea incoming;
    private TextField outgoing;
    private BufferedReader reader;
    private PrintWriter writer;
    
    Thread connection;
    Socket sock;
    ObjectInputStream dataReceiver;
    ObjectOutputStream dataSender;
    
    GridPane paneForUserLogin = new GridPane();
    GridPane paneForChatSelect = new GridPane();
    GridPane paneForChatView = new GridPane();
    Stage theStage;
    TextArea chatText;
    
    /**
     * This inner class is helpful for bundling together
     * the user name and password of a new login
     */
    static class LoginInfo implements Serializable{
    	String username;
    	String password;
    	
    	LoginInfo(String username, String password){
    		this.username = username;
    		this.password = password;
    	}
    }
    
    /**
     * This inner class bundles together the user name and password
     * of a new user
     */
    static class NewUserInfo implements Serializable{
    	String username;
    	String password;
    	
    	NewUserInfo(String username, String password){
    		this.username = username;
    		this.password = password;
    	}
    }
    
    /**
     * The initial view for clients to see. It requires the
     * user inputs a userName and then a password
     * @param primaryStage
     */
    private void loginView(Stage primaryStage){
    	
		// 0.0 : Set UI attributes
    	paneForUserLogin = new GridPane();
		paneForUserLogin.setAlignment(Pos.CENTER);
		paneForUserLogin.setHgap(10);
		paneForUserLogin.setVgap(10);
		paneForUserLogin.setPadding(new Insets(25,25,25,25));	
		
		// 0.1: make lines visible
		paneForUserLogin.setGridLinesVisible(true);

		
		// 1.0: Add the user name fields
		
		// 1.1: Add the user name label
		Text userNameLabel = new Text("Username:");
		userNameLabel.setFill(Color.DARKBLUE);
		userNameLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 14));	
		paneForUserLogin.add(userNameLabel, 1, 1);
		
		// 1.2: Add the user name field
		TextField userNameField = new TextField("");
		paneForUserLogin.add(userNameField, 2, 1);
		
		
		// 2.0: Add the password fields
		
		// 2.1: Add the password label
		Text passwordLabel = new Text("Password:");
		passwordLabel.setFill(Color.DARKBLUE);
		passwordLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 14));	
		paneForUserLogin.add(passwordLabel, 1, 2);
		
		// 2.2: Add the password field
		PasswordField passwordField = new PasswordField();
		paneForUserLogin.add(passwordField, 2, 2);
		
		
		// 3.0: Add the login button
		
		// 3.1 Create and add button
		Button loginButton = new Button();
		loginButton.setText("Login!");
		loginButton.setTextFill(Color.RED);
		loginButton.setFont(Font.font("Comic Sans MS", FontWeight.NORMAL, 14));
		paneForUserLogin.add(loginButton, 1, 3);
		
		// 3.2: Add button handler
		loginButton.setOnAction( e->{
			try {
				// capture username and login 
				LoginInfo loginInfo = new LoginInfo(userNameField.getText(), passwordField.getText());
				
				//send login info to server
				if((userNameField.getText().length() > 0) && (passwordField.getText().length() > 0)){
					dataSender.writeObject(loginInfo);
				}
				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		
		
		// 3.0: Add the newUser button
		
		// 3.1 Create and add button
		Button newUserButton = new Button();
		newUserButton.setText("create new user!");
		newUserButton.setTextFill(Color.RED);
		newUserButton.setFont(Font.font("Comic Sans MS", FontWeight.NORMAL, 14));
		paneForUserLogin.add(newUserButton, 2, 3);
		
		// 3.2: Add button handler
		newUserButton.setOnAction( e->{
			try {
				// capture username and newUser 
				NewUserInfo newUserInfo = new NewUserInfo(userNameField.getText(), passwordField.getText());
				
				//send newUser info to server
				if((userNameField.getText().length() > 0) && (passwordField.getText().length() > 0)){
					dataSender.writeObject(newUserInfo);
				}
				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		

        // LAST: Create a scene and place it in the stage
        Scene scene = new Scene(paneForUserLogin, 500, 500);
        primaryStage.setTitle("Client Controller"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage
    }
    
    /**
     * This function sets the chat screen view
     */
    private void chatScreenView(Stage primaryStage){
    	// 0.0 : Set UI attributes
    	paneForChatView = new GridPane();
		paneForChatView.setAlignment(Pos.CENTER);
		paneForChatView.setHgap(10);
		paneForChatView.setVgap(10);
		paneForChatView.setPadding(new Insets(25,25,25,25));	
		
		// 0.1: make lines visible
		paneForChatView.setGridLinesVisible(true);
		
		// 1:0 Add Text Area
		chatText = new TextArea();
		chatText.setEditable(false);
		paneForChatView.add(chatText, 1, 1);
		
		// 2.0: Add input field
        TextField input = new TextField();
        input.setOnAction(event -> {
        	try {
        		String msgToSend = input.getText();
				dataSender.writeObject(msgToSend);
				input.clear();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// send message to server
        });
        paneForChatView.add(input, 1, 2);
        
       
        // Create a scene and place it in the stage
        Scene scene = new Scene(paneForChatView, 500, 500);
        primaryStage.setTitle("Chat Select"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage
    }

    /**
     * This is the view for sending and receiving chats
     * @param primaryStage
     */
    private void selectChatView(Stage primaryStage) {
    	// 0.0 : Set UI attributes
    	paneForChatSelect = new GridPane();
		paneForChatSelect.setAlignment(Pos.CENTER);
		paneForChatSelect.setHgap(10);
		paneForChatSelect.setVgap(10);
		paneForChatSelect.setPadding(new Insets(25,25,25,25));	
		
		// 0.1: make lines visible
		paneForChatSelect.setGridLinesVisible(true);
		
		
		// 1.0: Obtain list view of people available to chat
		
		
		
		// 3.0 Join the communal chat button
			// 3.1: Create and add button
			Button joinCommunalButton = new Button();
			joinCommunalButton.setText("Or ... Join the Communal Chat!");
			joinCommunalButton.setTextFill(Color.RED);
			joinCommunalButton.setFont(Font.font("Comic Sans MS", FontWeight.NORMAL, 14));
			paneForChatSelect.add(joinCommunalButton, 1, 5);
			
			// 3.2: Add button handler
			
			joinCommunalButton.setOnAction(e->{
				try {
					dataSender.writeObject("joinedCommunal");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				chatScreenView(primaryStage);
			});
		

        // Create a scene and place it in the stage
        Scene scene = new Scene(paneForChatSelect, 500, 500);
        primaryStage.setTitle("Chat Select"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage
    }

    /**
     * This function sets up a new connection with the 
     * server
     * @throws Exception
     */
    private void setUpNetworking() throws Exception {
        this.sock = new Socket("127.0.0.1", 4242);
        System.out.println("networking established");
        
        this.connection = new Thread(new IncomingReader());
        this.connection.setDaemon(true);
        this.connection.start();
    }
    
    /**
     * private class for seting up a connection with the server
     */
    private class IncomingReader implements Runnable {
    	boolean addedField = false;
        public void run() {
        	Text wrongField = new Text("Wrong Password!");
        	wrongField.setFill(Color.DARKBLUE);
        	wrongField.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 14));
        	
            	
				try {
					ObjectOutputStream getOutputStream = new ObjectOutputStream(sock.getOutputStream());
					ObjectInputStream getInputStream = new ObjectInputStream(sock.getInputStream());
					
					dataReceiver = getInputStream;
	            	dataSender = getOutputStream;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
           
            try {
                while (true) {
                	Serializable data = (Serializable) dataReceiver.readObject();
                	
                	// check for login related input
                	if(loginInput(data, wrongField)){
                		continue;
                	}
                	
                	// check for chat select related input
                	
                	// check for incoming chat message
                	if(data.toString().startsWith("chatMsg:")){
                		String chatMsg = data.toString().replaceFirst("chatMsg:", "");
                		Platform.runLater(() -> {
                			chatText.appendText(chatMsg + "\n");
                		}); 
                		
                	}
                	
                }
            } 
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        /**
         * This function deals with login related input
         * @return true if something related to input, false otherwise 
         */
        boolean loginInput(Serializable data, Text wrongField){
        	if(data.toString().equals("wrong password")){
        		Platform.runLater(() -> {
        			wrongField.setText("Wrong Password!");
        			if(!addedField){
        				paneForUserLogin.add(wrongField, 1, 5);
        				addedField = true;
        			}
        		}); 
        		return true;
        	}
        	else if(data.toString().equals("invalid user")){
        		Platform.runLater(() -> {
        			wrongField.setText("Invalid User!");
        			if(!addedField){
        				paneForUserLogin.add(wrongField, 1, 5);
        				addedField = true;
        			}
        		}); 
        		return true;
        	}
        	else if(data.toString().equals("good user")){
        		Platform.runLater(() -> {
        			wrongField.setText("New User Created!");
        			if(!addedField){
        				paneForUserLogin.add(wrongField, 1, 5);
        				addedField = true;
        			}
        		}); 
        		return true;
        	}
        	else if(data.toString().equals("good log")){
        		Platform.runLater(() -> {
        			wrongField.setText("Logging In!");
        			selectChatView(theStage);				// launch select chat view
        			
        			if(!addedField){
        				paneForUserLogin.add(wrongField, 1, 5);
        				addedField = true;
        			}
        		});
        		return true;
        	}
        	else if(data.toString().equals("already")){
        		Platform.runLater(() -> {
        			wrongField.setText("Already Logged In!");
        			if(!addedField){
        				paneForUserLogin.add(wrongField, 1, 5);
        				addedField = true;
        			}
        		});
        		return true;
        	}
        	else if(data.toString().equals("user already")){
        		Platform.runLater(() -> {
        			wrongField.setText("User already exists!");
        			if(!addedField){
        				paneForUserLogin.add(wrongField, 1, 5);
        				addedField = true;
        			}
        		});
        		return true;
        	}
        	return false;
        }
    }

    /**
     * called when application loads
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
    	theStage = primaryStage;
    	setUpNetworking();
    	loginView(primaryStage);
    }

    /**
     * dummy main function in javafx
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
