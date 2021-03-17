package sim;

/**
 * An object that holds the configuration of a single server.
 */
public class ServerConfig {
    enum State{
        INACTIVE,
        BOOTING,
        IDLE,
        ACTIVE,
        UNAVAILABLE
    }
    public String type;
    public int id;
    public State state;
    public int currentStartTime;
    public int core;
    public int memory;
    public int disk;
    public int waitingJobs;
    public int runningJobs;

    // The following are only used if failures are simulated
    public int failures;
    public int totalFailureTime;
    public int meanFailureTime;
    public int meanRecoveryTime;
    public int meanAbsDeviationOfFailure;
    public int lastStartTime;


    /**
     * Server information constructor (failures not simulated)
     * @param type Name of the server type.
     * @param id ID of the server. Unique to this server type.
     * @param state Enum of possible server states: INACTIVE | BOOTING | IDLE | ACTIVE | UNAVAILABLE
     * @param currentStartTime Start time of current use.
     * @param core Core count available to server.
     * @param memory Memory available to server.
     * @param disk Disk space available to server.
     * @param waitingJobs Number of jobs waiting to run on this server. Details retrieved with LSTJ {@linkplain Client#command(String) command}.
     * @param runningJobs Number of jobs currently running on this server. Details retrieved with LSTJ {@linkplain Client#command(String) command}
     */
    public ServerConfig(String type, int id, State state, int currentStartTime, int core, int memory, int disk, int waitingJobs, int runningJobs){
        this.type = type;
        this.id = id;
        this.state = state;
        this.currentStartTime = currentStartTime;
        this.core = core;
        this.memory = memory;
        this.disk = disk;
        this.waitingJobs = waitingJobs;
        this.runningJobs = runningJobs;
    }

    /**
     * Server information constructor (failures simulated)
     * @param type Name of the server type.
     * @param id ID of the server. Unique to this server type.
     * @param state Enum of possible server states: INACTIVE | BOOTING | IDLE | ACTIVE | UNAVAILABLE
     * @param currentStartTime Start time of current use.
     * @param core Core count available to server.
     * @param memory Memory available to server.
     * @param disk Disk space available to server.
     * @param waitingJobs Number of jobs waiting to run on this server. Details retrieved with LSTJ {@linkplain Client#command(String) command}.
     * @param runningJobs Number of jobs currently running on this server. Details retrieved with LSTJ {@linkplain Client#command(String) command}
     * @param failures Number of failures
     * @param totalFailureTime Total of the failure times
     * @param meanFailureTime Mean of the time to failure for each failure
     * @param meanRecoveryTime Mean of the time to recovery for each failure
     * @param meanAbsDeviationOfFailure Mean absolute deviation of failures
     * @param lastStartTime Either the initial start time or the start after latest recovery
     */
    public ServerConfig(String type, int id, State state, int currentStartTime, int core, int memory, int disk, int waitingJobs, int runningJobs, int failures, int totalFailureTime, int meanFailureTime, int meanRecoveryTime, int meanAbsDeviationOfFailure, int lastStartTime){
        this.type = type;
        this.id = id;
        this.state = state;
        this.currentStartTime = currentStartTime;
        this.core = core;
        this.memory = memory;
        this.disk = disk;
        this.waitingJobs = waitingJobs;
        this.runningJobs = runningJobs;
        this.failures = failures;
        this.totalFailureTime = totalFailureTime;
        this.meanFailureTime = meanFailureTime;
        this.meanRecoveryTime = meanRecoveryTime;
        this.meanAbsDeviationOfFailure = meanAbsDeviationOfFailure;
        this.lastStartTime = lastStartTime;
    }

    @Override
    public String toString() {
        return "type:" + type + " id:" + id + " state:" + state + " start:" + currentStartTime + " core:" + core + " mem:" + memory + " disk:" + disk + " wait:" + waitingJobs + " run:" + runningJobs + "\n";
    }
}
