import sim.*;

import java.util.Arrays;

/**
 * The driver class for the program. This delegates communication to a Client object
 * and handles the scheduling logic.
 */
public class App {
    public static void main(String[] args) {

        Client client = new Client(); // Handshake is handled by constructor

        ServerConfig[] servers;
        ServerConfig largest = null;

        // Event loop
        for (DSEvent event = client.getEvent(); event.eventType != DSEvent.EventType.NONE; event = client.getEvent()) {
            switch (event.eventType) {
            case JOB:
                Job job = (Job) event;
                // Can't do this outside loop, REDY needs to be called before GETS
                if (largest == null) {
                    servers = client.getServers("Avail " + job.core + " " + job.memory + " " + job.disk);
                    Arrays.sort(servers, new LargestFirst());
                    largest = servers[0];
                }

                client.scheduleJob(job.jobID, largest.type, largest.id);

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
