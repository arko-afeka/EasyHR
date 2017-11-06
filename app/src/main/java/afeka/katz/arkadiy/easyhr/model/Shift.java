package afeka.katz.arkadiy.easyhr.model;

/**
 * Created by arkokat on 11/4/2017.
 */

public class Shift {
    private long dayInd;
    private long startTime;
    private long endTime;
    private String id;

    public Shift() {

    }

    public Shift(String id, long dayInd, long startTime, long endTime) {
        this.id = id;
        this.dayInd = dayInd;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public long getDayInd() {
        return dayInd;
    }

    public String getId() {
        return id;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }
}
