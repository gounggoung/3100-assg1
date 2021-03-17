import sim.Client;

import java.util.Arrays;

public class App {
    public static void main(String[] args){

        Client client = new Client();

        // Select scheduling algorithm from command line
        // args[0] should be "-a"
        // args[1] should be <algorithm_name>

        client.getEvent();
        System.out.println(Arrays.toString(client.getServers()));
        System.out.println(client.getEvent());

        // Start algorithms from here

        client.close();
    }
}
