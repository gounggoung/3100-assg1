package sim;

import java.util.Comparator;

public class LargestFirst implements Comparator<ServerConfig> {

    @Override
    public int compare(ServerConfig a, ServerConfig b) {
        if(a.core != b.core){
            return b.core - a.core; // core count descending
        }
        else return a.id - b.id; // id ascending
    }
}
