import sim.*;

import java.util.Arrays;

/**
 * The driver class for the program. This delegates communication to a Client
 * object and handles the scheduling logic.
 */
public class App {
    public static void main(String[] args) {

        Client client = new Client(); // Handshake is handled by constructor

        ServerConfig[] currentServerStates = null;
        ServerConfig[] servers = null;
        

        // Event loop
        for (DSEvent event = client.getEvent(); event.eventType != DSEvent.EventType.NONE; event = client.getEvent()) {
            switch (event.eventType) {
                case JOB:
                    Job job = (Job) event;
                    // Can't do this outside loop, REDY needs to be called before GETS

                    currentServerStates = client.getServers("Capable " + job.core + " " + job.memory + " " + job.disk);
                    if (servers == null) {

                        servers = currentServerStates;
                        Arrays.sort(servers, new SmallestFirst());
                        
                       // System.out.println(Arrays.toString(currentServerStates) + "\n\n\n");

                    }
                    System.out.println(job.toString());
                    // if (servers[servers.length - 1].core >= job.core) {
                        Boolean found = false;
                        for (ServerConfig server : currentServerStates) {
                            if (server.core >= job.core) {
                                client.scheduleJob(job.jobID, server.type, server.id);
                                found = true;
                                break;
                            }
                        }

                        if(!found){
                            for(ServerConfig server : servers){
                                if (server.core >= job.core) {
                                    client.scheduleJob(job.jobID, server.type, server.id);
                                    break;
                                }
                            }
                        }
                    // }

                    break;
                case COMPLETE:
                case FAILURE:
                case RECOVERY:
                    break;
            }
        }

        client.close();
    }
}
