import sim.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.xml.sax.SAXException;

/**
 * The driver class for the program. This delegates communication to a Client
 * object and handles the scheduling logic.
 */
public class App {
    public static void main(String[] args) {

        Client client = new Client(); // Handshake is handled by constructor

        ServerConfig[] currentServerStates = null;
        XMLParse xml = null;
        HashMap<String, Float> costs = null;
        // Event loop
        for (DSEvent event = client.getEvent(); event.eventType != DSEvent.EventType.NONE; event = client.getEvent()) {
           // System.out.println(event.eventType);

            switch (event.eventType) {

                case JOB:
                    Job job = (Job) event;

                    // Can't do this outside loop, REDY needs to be called before GETS
                    currentServerStates = client.getServers("Capable " + job.core + " " + job.memory + " " + job.disk);

                    // if(xml == null){
                    //     try {
                    //         xml = new XMLParse(new File("src/ds-system.xml"));
                    //         costs = xml.getCosts();
                    //     } catch (SAXException e) {
                    //         // TODO Auto-generated catch block
                    //         e.printStackTrace();
                    //     } catch (IOException e) {
                    //         // TODO Auto-generated catch block
                    //         e.printStackTrace();
                    //     }

                        
                    // }
                   
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
                        // String type;
                        // float lowestCost = Integer.MAX_VALUE;
                        // ServerConfig bestServer = null;
                        // for(ServerConfig server : currentServerStates){
                        //     type = server.type;
                        //     if(costs.get(type) < lowestCost){
                        //         lowestCost = costs.get(type);
                        //         bestServer= server;
                        //     }
                        // }
                        
                        ServerConfig bestServer = null;
                        int maxStart = 0;
                        for(ServerConfig server : currentServerStates){
                            if(server.currentStartTime > maxStart){
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
