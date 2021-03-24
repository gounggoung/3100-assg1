import sim.Client;
import sim.DSEvent;
import sim.LargestFirst;
import sim.ServerConfig;
import sim.Job;

import java.util.Arrays;

public class App {
    public static void main(String[] args) {

        Client client = new Client(); // Handshake is handled by constructor

        ServerConfig[] servers;
        ServerConfig largest = null;
        // SECTION 1 SPECIFIC:

        for (DSEvent event = client.getEvent(); event.eventType != DSEvent.EventType.NONE; event = client.getEvent()) {
            switch (event.eventType) {
            case JOB:
                Job job = (Job) event; // Downcasting
                // servers = client.getServers();
                if (largest == null) {

                    servers = client.getServers("Avail " + job.core + " " + job.memory + " " + job.disk);
                    Arrays.sort(servers, new LargestFirst());
                    largest = servers[0];
                    System.out.println(Arrays.toString(servers));
                }

                client.scheduleJob(job.jobID, largest.type, largest.id);

                break;
            case COMPLETE:
                break;
            case FAILURE:
                break;
            case RECOVERY:
                break;
            case NONE:
                break;
            }

        }

        client.close();
    }
}
