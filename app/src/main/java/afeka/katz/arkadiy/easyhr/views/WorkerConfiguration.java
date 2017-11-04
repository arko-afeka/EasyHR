package afeka.katz.arkadiy.easyhr.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.apache.commons.validator.routines.EmailValidator;

import afeka.katz.arkadiy.easyhr.R;
import afeka.katz.arkadiy.easyhr.model.Shift;
import afeka.katz.arkadiy.easyhr.model.User;

public class WorkerConfiguration extends LinearLayout implements View.OnClickListener {
    private EditText email;
    private CheckBox manager;
    private Button remove;

    public WorkerConfiguration(Context context) {
        super(context);
        init(context, null);
    }

    public WorkerConfiguration(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context cx, AttributeSet attrs) {
        setOrientation(HORIZONTAL);
        LayoutInflater inflater = (LayoutInflater) cx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.worker_configuration, this);

        email = findViewById(R.id.email);
        manager = findViewById(R.id.manager);
        remove = findViewById(R.id.remove_worker);

        remove.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        ((ViewGroup)getParent()).removeView(this);
    }

    public void setUser(String email, boolean isManager) {
        this.manager.setChecked(isManager);
        this.email.setText(email);
    }

    public boolean isManager() {
        return manager.isChecked();
    }

    public String getEmail() {
        if (!EmailValidator.getInstance().isValid(email.getText().toString())) {
            return null;
        }

        return email.getText().toString();
    }

    public void setError(String error) {
        email.setError(error);
    }
}
