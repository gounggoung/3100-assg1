package sim;

import java.net.*;
import java.io.*;

/**
 * Handles connection to the server and provides methods to issue commands.
 */
public class Client {
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private String lastOutInstruction = "";

    /**
     * Initialise connection to server and handshake.
     * @param address The address of the server.
     * @param port The port of the server.
     */
    public Client(String address, int port) {
        try {
            // Connect to the server
            socket = new Socket(address, port);
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(socket.getOutputStream());
            handshake();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Initialise connection to server and handshake. Default connection: localhost:50000
     */
    public Client() {
        this("localhost", 50000);
    }

    /**
     * Waits for a response from the server and returns it as a string.
     *
     * @return Response from server as a string
     */
    private String read(int bufferLength) throws IOException {
        String response = "";
        try {
            byte[] inBytes = new byte[bufferLength]; // might need to increase this, need to do some testing
            int byteCount = in.read(inBytes);
            if (byteCount >= inBytes.length) {
                System.out.println("WARNING: Input buffer full");
            }
            response = new String(inBytes).trim();
            System.out.println("RECV: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response.equals("ERR")) {
            throw new IOException("ERR received on instruction: " + lastOutInstruction);
        }
        return response;
    }


    /**
     * Sends a payload to the connected server and waits for a response.
     * @param payload The string to send to the server.
     * @param bufferLength The size of the buffer to store the server's response.
     * @return The server's response to the payload command.
     */
    public String command(String payload, int bufferLength) {
        try {
            out.write(payload.getBytes());
            lastOutInstruction = payload;
            System.out.println("SEND: " + payload);
            return read(bufferLength);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sends a payload to the connected server and waits for a response.
     *
     * @param payload The string to send to the server.
     * @return The server's response to the payload command.
     */
    public String command(String payload){
        return command(payload, 1024);
    }

    /**
     * Initializes the connection to the server simulator
     */
    private void handshake() {
        String user = System.getProperty("user.name");
        command("HELO");
        command("AUTH " + user);
}

    /**
     * Request server configurations.
     * @param arg All | Type [serverType] | Avail [core] [mem] [disk] | Capable [core] [mem] [disk]
     * @return An array of strings representing each server configuration.
     */
    public ServerConfig[] getServers(String arg){
        // TODO: generate array of server objects

        String[] data = command("GETS " + arg).split(" ");
        int messageCount = Integer.parseInt(data[1]);
        int messageLength = Integer.parseInt(data[2]);
        int totalLength = messageLength * messageCount;
        String serverInfo = command("OK", totalLength);
        command("OK");
        String[] serverStrings = serverInfo.split("\n");
        ServerConfig[] serverConfigs = new ServerConfig[serverStrings.length];
        for(int i = 0; i < serverStrings.length; i++){
            ServerConfig thisServerConfig;

            String[] args = serverStrings[i].split(" ");
            String type = args[0];
            int id = Integer.parseInt(args[1]);
            ServerConfig.State state = ServerConfig.State.valueOf(args[2].toUpperCase());
            int currentStartTime = Integer.parseInt(args[3]);
            int core = Integer.parseInt(args[4]);
            int memory = Integer.parseInt(args[5]);
            int disk = Integer.parseInt(args[6]);
            int waitingJobs = Integer.parseInt(args[7]);
            int runningJobs = Integer.parseInt(args[8]);

            // if resource failures are enabled
            if(args.length == 15){
                int failures = Integer.parseInt(args[9]);
                int totalFailureTime = Integer.parseInt(args[10]);
                int meanFailureTime = Integer.parseInt(args[11]);
                int meanRecoveryTime = Integer.parseInt(args[12]);
                int meanAbsDeviationOfFailure = Integer.parseInt(args[13]);
                int lastStartTime = Integer.parseInt(args[14]);
                thisServerConfig = new ServerConfig(type, id, state, currentStartTime, core, memory, disk, waitingJobs, runningJobs, failures, totalFailureTime, meanFailureTime, meanRecoveryTime, meanAbsDeviationOfFailure, lastStartTime);
            }
            else{
                thisServerConfig = new ServerConfig(type, id, state, currentStartTime, core, memory, disk, waitingJobs, runningJobs);
            }
            serverConfigs[i] = thisServerConfig;
        }
        return serverConfigs;
    }
    /**
     * Request all server configurations.
     * @return An array of strings representing each server configuration.
     */
    public ServerConfig[] getServers(){
        return getServers("All");
    }

    /**
     * Requests the next available job from the server
     * @return A Job object if a job is available, otherwise null.
     */
    public Job getEvent() {
        String[] response = command("REDY").split(" ");
        Job job = null;
        switch (response[0]) {
            case "JOBP":
                // TODO
                // Might need to remove previous instance of job or update
                // Falls through to JOBN for now
            case "JOBN":
                // Normal job for scheduling for the first time

                int submitTime = Integer.parseInt(response[1]);
                int jobID = Integer.parseInt(response[2]);
                int estRuntime = Integer.parseInt(response[3]);
                int core = Integer.parseInt(response[4]);
                int memory = Integer.parseInt(response[5]);
                int disk = Integer.parseInt(response[6]);

                job = new Job(submitTime, jobID, estRuntime, core, memory, disk);
                break;
            case "JCPL":
                // TODO
                // Information on latest job completion
                break;
            case "RESF":
                // TODO
                // Information on latest server failure
                break;
            case "RESR":
                // TODO
                // Information on latest server recovery
                break;
            case "NONE":
                // No jobs left to schedule
                close();
                break;
            default:
                System.out.println("Unrecognised response from server: " + response[0]);
                return null;
        }
        return job;
    }

    /**
     * Attempts to gracefully close the connection to the server.
     */
    public void close() {
        try {
            if (command("QUIT").equals("QUIT")) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
