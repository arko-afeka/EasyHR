package afeka.katz.arkadiy.easyhr.model;

/**
 * Created by arkokat on 11/4/2017.
 */

public class Shift {
    private long dayInd;
    private long startTime;
    private long endTime;

    public Shift() {

    }

    public Shift(long dayInd, long startTime, long endTime) {
        this.dayInd = dayInd;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public long getDayInd() {
        return dayInd;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }
}
