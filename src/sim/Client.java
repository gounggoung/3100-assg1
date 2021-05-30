package sim;

import sim.DSEvent.EventType;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Handles connection to the server and provides methods to issue commands.
 */
public class Client {
    private Socket socket;
    private DataOutputStream out;
    private BufferedReader in;
    private String lastOutInstruction = "";
    public boolean verbose = false;


    /**
     * Initialise connection to server and handshake.
     * 
     * @param address The address of the server.
     * @param port    The port of the server.
     */
    public Client(String address, int port) {
        try {
            // Connect to the server
            socket = new Socket(address, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
            out = new DataOutputStream(socket.getOutputStream());
            handshake();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * Initialise connection to server and handshake. Default connection:
     * localhost:50000
     */
    public Client() {
        this("localhost", 50000);
    }


    /**
     * Attempts to gracefully close the connection to the server.
     */
    public void close() {
        try {
            if (!command("QUIT").equals("QUIT")) {
                command("REDY");
                command("QUIT");
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Sends a payload to the connected server and waits for a response.
     * 
     * @param payload      The string to send to the server.
     * @return The server's response to the payload command.
     */
    public String command(String payload) {
        try {
            payload += '\n';
            out.write(payload.getBytes());
            lastOutInstruction = payload; // Used for debugging. Private class variable
            if (verbose)
                System.out.print("SEND: " + payload);
            return read();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
     * Requests the next available job from the server
     * 
     * @return A Job object if a job is available, otherwise null.
     */
    public DSEvent getEvent() {
        String[] response = command("REDY").split(" "); // Send REDY to server and await reponse to process
        DSEvent event = null;
        switch (response[0]) {
        case "JOBP":
            // TODO Stage 2

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

            event = new Job(submitTime, jobID, estRuntime, core, memory, disk);

            break;
        case "JCPL":
            // Information on latest job completion
            event = new DSEvent(EventType.COMPLETE);
            break;
        case "RESF":
            // TODO Stage 2
            // Information on latest server failure
            break;
        case "RESR":
            // TODO Stage 2
            // Information on latest server recovery
            break;
        case "NONE":
            // No jobs left to schedule
            event = new DSEvent(EventType.NONE);
            break;
        default:
            System.out.println("Unrecognised response from server: " + response[0]);
            return null;
        }
        return event;
    }


    /**
     * Request server configurations.
     * 
     * @param arg All | Type [serverType] | Avail [core] [mem] [disk] | Capable
     *            [core] [mem] [disk]
     * @return An array of strings representing each server configuration.
     */
    public ServerConfig[] getServers(String arg) {
        // TODO: generate array of server objects
        ServerConfig[] serverConfigs = null;
        try {
            // Following commands process 'DATA' from Server
            String[] data = command("GETS " + arg).split(" ");
            int messageCount = Integer.parseInt(data[1]);

            // Client ready for received servers
            out.write("OK\n".getBytes());

            String[] serverStrings = new String[messageCount];
            for (int i = 0; i < messageCount; i++) {
                serverStrings[i] = in.readLine();
            }

            // Acknowledgement of received servers
            command("OK");

            // Parse server info into ServerConfig objects
            serverConfigs = new ServerConfig[serverStrings.length];
            for (int i = 0; i < serverStrings.length; i++) {
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
                if (args.length == 15) {
                    int failures = Integer.parseInt(args[9]);
                    int totalFailureTime = Integer.parseInt(args[10]);
                    int meanFailureTime = Integer.parseInt(args[11]);
                    int meanRecoveryTime = Integer.parseInt(args[12]);
                    int meanAbsDeviationOfFailure = Integer.parseInt(args[13]);
                    int lastStartTime = Integer.parseInt(args[14]);
                    thisServerConfig = new ServerConfig(type, id, state, currentStartTime, core, memory, disk,
                            waitingJobs, runningJobs, failures, totalFailureTime, meanFailureTime, meanRecoveryTime,
                            meanAbsDeviationOfFailure, lastStartTime);
                } else {
                    thisServerConfig = new ServerConfig(type, id, state, currentStartTime, core, memory, disk,
                            waitingJobs, runningJobs);
                }
                serverConfigs[i] = thisServerConfig;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverConfigs;
    }

    public String[] getWaitingTimes(ServerConfig server){
        String[] response = new String[3];
        response[0] = server.type;
        response[1] = String.valueOf(server.id);
        response[2] = command("EJWT " + server.type + " " + server.id);
        return response;
    }

    public int getJobListing(ServerConfig server){
        int jobCount = 0;
        command("LSTJ " + server.type + " " + server.id);

        try {
            out.write("OK\n".getBytes());
            String[] response;
            for(response = in.readLine().split(" "); response.length > 1; response = in.readLine().split(" ")){
                jobCount++;
                out.write("OK\n".getBytes());
            }
            
            return jobCount;
        } catch (IOException e) {
           
            e.printStackTrace();
        }

       
        return 0;
        
    }

    public int queryCount(ServerConfig server){
        int total = 0;

        total += Integer.parseInt(command("CNTJ " + server.type + " " + server.id + " " + "1"));
        //total += Integer.parseInt(command("CNTJ " + server.type + " " + server.id + " " + "2"));
        return total;
    }

    /**
     * Waits for a response from the server and returns it as a string.
     *
     * @return Response from server as a string
     */
    private String read() throws IOException {
        String response = "";
        try {
            response = in.readLine();
            if (verbose)
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
     * Schedules a job on a server
     * 
     * @param jobID ID of the job to schedule
     * @param serverType Server type to run the job
     * @param serverID  Server ID (unique to server type) to run the job
     */
    public void scheduleJob(int jobID, String serverType, int serverID) {
        command("SCHD " + jobID + " " + serverType + " " + serverID);
    }
}
