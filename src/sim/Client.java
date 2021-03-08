package sim;

import java.net.*;
import java.io.*;

public class Client {
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    public String algorithm = "allToLargest";

    // Constructors handle the socket initialization
    public Client(String address, int port){
        try{
            // Connect to the server
            socket = new Socket(address, port);
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(socket.getOutputStream());
        }
        catch(IOException e){
            e.printStackTrace();
        }

    }
    public Client(){
        this("localhost", 50000);
    }

    /**
     * Waits for a response from the server and returns it as a string.
     * @return
     * Response from server as a string
     */
    public String getResponse(){
        StringBuilder currentString = new StringBuilder();
        try {
            while(in.available() == 0){
                // waiting for response
                // TODO: find a way to get rid of this busy wait
            }

            for(int i = in.available(); i > 0; i--){
                char c = (char)in.readByte();
                currentString.append(c);
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        return currentString.toString();
    }

    /**
     * Sends a payload to the connected server.
     * Shorthand for writeChars() and flush().
     * @param payload
     * The string to send to the server
     */
    public void send(String payload){
        try{
            out.write(payload.getBytes());
            out.flush();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Initializes the connection to the server simulator
     */
    public void handshake() {
        System.out.println("Beginning handshake...");
        String user = System.getProperty("user.name");
        send("HELO");
        System.out.println(getResponse());
        send("AUTH " + user);
        System.out.println(getResponse());
    }

    /**
     * Handles exceptions for closing the socket
     */
    public void close(){
        try {
            send("REDY"); // quit won't work if handshake isn't complete
            send("QUIT");
            socket.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }


}
