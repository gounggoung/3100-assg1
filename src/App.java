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
        ArrayList<Job> waitingJobs = new ArrayList<>();

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
                            if(job.jobID == 93){
                                int test = 3;
                            }
                            client.scheduleJob(job.jobID, server.type, server.id);
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        client.scheduleJob(job.jobID, currentServerStates[0].type, currentServerStates[0].id);
                    }

                    break;
                case COMPLETE:
                    for (Job waitingJob : waitingJobs) {
                        currentServerStates = client.getServers("Capable " + waitingJob.core + " " + waitingJob.memory + " " + waitingJob.disk);
                        for (ServerConfig server : currentServerStates) {
                            if (server.core >= waitingJob.core) {
    
                                client.scheduleJob(waitingJob.jobID, server.type, server.id);
                                waitingJobs.remove(waitingJob);
                                break;
                            }
                        }
                    }
                    break;

                case FAILURE:
                case RECOVERY:
                    break;
            }
        }

        client.close();
    }
}
