package afeka.katz.arkadiy.easyhr.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import afeka.katz.arkadiy.easyhr.R;
import afeka.katz.arkadiy.easyhr.model.Shift;

/**
 * TODO: document your custom view class.
 */
public class ShiftView extends LinearLayout {
    private TextView day;
    private TextView startTime;
    private TextView endTime;
    private Shift shift;

    public ShiftView(Context context) {
        super(context);
        init(context, null);
    }

    public ShiftView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context cx, AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) cx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.shift_view, this);

        day = findViewById(R.id.week_day);
        startTime = findViewById(R.id.start_time);
        endTime = findViewById(R.id.end_time);
    }


    public Shift getShift() {
        return shift;
    }

    private String getTimeByInd(long time) {
        return String.format("%02d:%02d", time / 60, time  % 60);
    }

    public void setShift(Shift shift) {
        this.shift = shift;

        this.day.setText(getResources().getStringArray(R.array.week_days)[(int)shift.getDayInd()]);
        this.startTime.setText(getTimeByInd(shift.getStartTime()));
        this.endTime.setText(getTimeByInd(shift.getEndTime()));
        invalidate();
    }
}
