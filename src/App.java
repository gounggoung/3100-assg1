import sim.Client;

public class App {
    public static void main(String[] args){

        Client client = new Client();

        // Select scheduling algorithm from command line
        // args[0] should be "-a"
        // args[1] should be <algorithm_name>
        if(args.length == 2){
            client.algorithm = args[1];
        }

        client.handshake();
        System.out.println("Using algorithm " + client.algorithm);

        // Start algorithms from here

        client.close();
    }
}
