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
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
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
    public String read(){
        String response = "";
        try {
            byte[] inBytes = new byte[99]; // might need to increase this, need to do some testing
            int byteCount = in.read(inBytes);
            if(byteCount >= inBytes.length){
                System.out.println("WARNING: INPUT BUFFER FILLED!");
            }
            response = new String(inBytes).trim();
            System.out.println("RECV: " + response);
        } catch(IOException e){
            e.printStackTrace();
        }
        return response;
    }

    /**
     * Sends a payload to the connected server.
     * Shorthand for writeChars() and flush().
     * @param payload
     * The string to send to the server
     */
    public void write(String payload){
        try{
            out.writeBytes(payload);
            out.flush();
            System.out.println("SEND: " + payload);
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
        write("HELO");
        read();
        write("AUTH " + user);
        read();
    }

    /**
     * Handles exceptions for closing the socket
     */
    public void close(){
        try {
            write("REDY"); // quit won't work if handshake isn't complete
            read();
            write("QUIT");
            if(read().equals("QUIT")) {
                socket.close();
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }


}
