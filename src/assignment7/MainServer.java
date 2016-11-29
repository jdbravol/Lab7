package assignment7;
/**
 * Created by juanbravo on 11/28/16.
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import assignment7.Client.LoginInfo;

public class MainServer extends Application {

    private TextArea ta = new TextArea();
    private HashMap<String, String> logInDataBase = new HashMap<String, String>();
    private HashSet<String> usersAvailableToChat = new HashSet<String>(0);
    private ArrayList<Chat> chats = new ArrayList<Chat>(0);
    private HashSet<String> usersLoggedIn = new HashSet<String>(0);
    
    private HashSet<String> usersInCommunal = new HashSet<String>(0);
    private HashMap<String,Socket> usersToSockets = new HashMap<String,Socket>(0);
    private HashMap<String,ObjectOutputStream> usersToOutputs = new HashMap<String,ObjectOutputStream>(0);
    private HashMap<Socket,String> socketsToUsers = new HashMap<Socket,String>(0);

    // Number a client
    private int clientNo = 0;
    
    /**
     * This inner class is for chats
     */
    private static class Chat{
    	ArrayList<String> usersInThisChat;
    	
    	Chat(){
    		this.usersInThisChat = new ArrayList<String>(0);
    	}
    	Chat(ArrayList<String> usersInThisChat){
    		this.usersInThisChat = usersInThisChat;
    	}
    }

    @Override // Override the start method in the Application class
    public void start(Stage primaryStage) {
        // Create a scene and place it in the stage
        Scene scene = new Scene(new ScrollPane(ta), 450, 200);
        primaryStage.setTitle("MultiThreadServer"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage

        Thread serverThread = new Thread( () -> {
            try {  // Create a server socket
                ServerSocket serverSocket = new ServerSocket(4242);
                ta.appendText("MultiThreadServer started at "
                        + new Date() + '\n');


                while (true) {
                    // Listen for a new connection request
                    Socket socket = serverSocket.accept();

                    // Increment clientNo
                    clientNo++;

                    Platform.runLater( () -> {
                        // Display the client number
                        ta.appendText("Starting thread for client " + clientNo +
                                " at " + new Date() + '\n');

                        // Find the client's host name, and IP address
                        InetAddress inetAddress = socket.getInetAddress();
                        ta.appendText("Client " + clientNo + "'s host name is "
                                + inetAddress.getHostName() + "\n");
                        ta.appendText("Client " + clientNo + "'s IP Address is "
                                + inetAddress.getHostAddress() + "\n");	});


                    // Create and start a new thread for the connection
                    Thread clientThread = new Thread(new HandleAClient(socket));
                    clientThread.setDaemon(true);
                    clientThread.start();
                }
            }
            catch(IOException ex) {
                System.err.println(ex);
            }
        });
        
        serverThread.setDaemon(true);
        serverThread.start();
    }


    // Define the thread class for handling
    class HandleAClient implements Runnable {
        private Socket socket; // A connected socket
        /** Construct a thread */
        public HandleAClient(Socket socket) {
            this.socket = socket;
        }
        /** Run a thread */
        public void run() {
            try {
                // Create data input and output streams
            	ObjectOutputStream outputToClient = new ObjectOutputStream( socket.getOutputStream());
                ObjectInputStream inputFromClient = new ObjectInputStream( socket.getInputStream());
                // Continuously serve the client
                while (true) {
                	// get data
                	Serializable data = (Serializable) inputFromClient.readObject();
                	
                	// check login stuff
                	if(loginCheck(data, outputToClient, socket)){
                		continue;
                	}
                	
                	// check for indivial chat DESPUESSSS
                	
                	// check for communal
                	if(data.toString().equals("joinedCommunal")){
                		usersInCommunal.add(socketsToUsers.get(socket));			// meter esta personal al comunal
                		usersAvailableToChat.remove(socketsToUsers.get(socket));	// sacarlo de available to chat
                		continue;
                	}
                	
                	// if left communal
                	
                	// if left individual
                	
                	// if log out
                	
                	if(data instanceof String){
                		// check if communal or individual
                		String nameOfUser = socketsToUsers.get(socket);
                		if(usersInCommunal.contains(nameOfUser)){
                			// send the message to everyone
                			String message = data.toString();
                			sendToCommunal(message, nameOfUser);
                		}
                		
                	}

                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * sends inputs message to all people in communal
     */
    private void sendToCommunal(String message, String nameOfUser){
    	for(String userInComm : usersInCommunal){
    		try {
				ObjectOutputStream commSender = usersToOutputs.get(userInComm);
				commSender.writeObject("chatMsg:" + nameOfUser + ": " + message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
    	}
    }
    
    
    
    /**
     * Checks status of login
     * @param data
     * @param outputToClient
     * @param socket
     * @return
     */
    private boolean loginCheck(Serializable data, ObjectOutputStream outputToClient, Socket socket){
    	try{
	    	// deal with Log in attempt
	        if(data instanceof Client.LoginInfo){
	        	Client.LoginInfo tryLogin = (Client.LoginInfo) data;
	        	// check if user name exists
	        	if(!logInDataBase.containsKey(((Client.LoginInfo)data).username)){
	        		outputToClient.writeObject("invalid user");
	        	}
	        	// check if password is correct
	        	else if(!logInDataBase.get(((Client.LoginInfo) data).username).equals(((Client.LoginInfo)data).password)){
	        		outputToClient.writeObject("wrong password");
	        	}
	        	else if(usersLoggedIn.contains(tryLogin.username)){
	        		outputToClient.writeObject("already");
	        	}
	        	// signal successfull login
	        	else{
	        		outputToClient.writeObject("good log");
	        		usersLoggedIn.add(tryLogin.username);
	        		
	        		usersAvailableToChat.add(tryLogin.username);	// signal this user is now available to chat
	        		usersToSockets.put(tryLogin.username, socket);
	        		socketsToUsers.put(socket, tryLogin.username);
	        		usersToOutputs.put(tryLogin.username, outputToClient);
	        		
	        		
	        	}
	        	return true;
	        }
	        
	        // deal with new user
	        if(data instanceof Client.NewUserInfo){
	        	Client.NewUserInfo newUserInfo = (Client.NewUserInfo)data;
	        	if(!logInDataBase.containsKey(newUserInfo.username)){
		        	logInDataBase.put(newUserInfo.username, newUserInfo.password);
		        	outputToClient.writeObject("good user");
		        	return true;
	        	}
	        	else{
	        		outputToClient.writeObject("user already");
		        	return true;
	        	}
	        }
	        return false;
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
		return false;
    }

    public static void main(String[] args) throws Exception {
        launch(args);
        
        Socket testSocket = new Socket("127.0.0.1", 4242);
    }
}
