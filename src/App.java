import sim.*;


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

                    // Send job to first server that has required available cores

                    boolean found = false;
                    for (ServerConfig server : currentServerStates) {
                        if (server.core >= job.core) {
                            client.scheduleJob(job.jobID, server.type, server.id);
                            found = true;
                            break;
                        }
                    }

                    if (!found) {

                        // Send jobs to server with the lowest estimated time to complete scheduled jobs

                        ServerConfig bestServer = null;
                        int minRun = Integer.MAX_VALUE;
                        for (ServerConfig server : currentServerStates) {
                            int currentRun = client.getEstJobRuntime(server);
                            if ( currentRun < minRun) {
                                minRun = currentRun;
                                bestServer = server;
                            }
                        }

                        client.scheduleJob(job.jobID, bestServer.type, bestServer.id);
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
