import sim.*;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The driver class for the program. This delegates communication to a Client
 * object and handles the scheduling logic.
 */
public class App {
    public static void main(String[] args) {

        Client client = new Client(); // Handshake is handled by constructor

        ServerConfig[] currentServerStates = null;
        

        // Event loop
        for (DSEvent event = client.getEvent(); event.eventType != DSEvent.EventType.NONE; event = client.getEvent()) {
           // System.out.println(event.eventType);

            switch (event.eventType) {

                case JOB:
                    Job job = (Job) event;

                    // Can't do this outside loop, REDY needs to be called before GETS
                    currentServerStates = client.getServers("Capable " + job.core + " " + job.memory + " " + job.disk);

                    // System.out.println(job.toString());

                    boolean found = false;
                    for (ServerConfig server : currentServerStates) {
                        if (server.core >= job.core) {
                            client.scheduleJob(job.jobID, server.type, server.id);
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        int chosenServer = (int)(Math.random()*currentServerStates.length);
                        System.out.println(chosenServer);
                        client.scheduleJob(job.jobID, currentServerStates[chosenServer].type, currentServerStates[chosenServer].id);
                    }

                    break;
                case COMPLETE:
                    break;

                case FAILURE:
                case RECOVERY:
                    break;
            }
        }

        client.close();
    }
}
