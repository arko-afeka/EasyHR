package afeka.katz.arkadiy.easyhr.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import afeka.katz.arkadiy.easyhr.R;
import afeka.katz.arkadiy.easyhr.model.Shift;

/**
 * TODO: document your custom view class.
 */
public class ShiftView extends LinearLayout {
    private Spinner day;
    private Spinner startTime;
    private Spinner endTime;

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
        if (day.getSelectedItemId() == 0 ||
                startTime.getSelectedItemId() == 0 ||
                endTime.getSelectedItemId() == 0) return null;

        return new Shift(day.getSelectedItemId() - 1, (startTime.getSelectedItemId() - 1 )* 15, (endTime.getSelectedItemId() - 1) * 15);
    }

    public void setShift(Shift shift) {
        day.setSelection((int)shift.getDayInd() + 1);
        startTime.setSelection((int)shift.getStartTime() + 1);
        endTime.setSelection((int)shift.getEndTime() + 1);
        invalidate();
    }
}
