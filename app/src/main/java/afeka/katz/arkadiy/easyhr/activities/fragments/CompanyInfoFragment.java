package afeka.katz.arkadiy.easyhr.activities.fragments;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import afeka.katz.arkadiy.easyhr.R;
import afeka.katz.arkadiy.easyhr.data.CompaniesDatabase;
import afeka.katz.arkadiy.easyhr.model.Company;
import afeka.katz.arkadiy.easyhr.model.Shift;
import afeka.katz.arkadiy.easyhr.views.ShiftConfiguration;

public class CompanyInfoFragment extends Fragment implements View.OnClickListener {
    private OnFragmentInteractionListener mListener;
    private List<ShiftConfiguration> shifts = new ArrayList<>();
    private Company company;
    private Context cx;
    private ViewGroup currentView;

    public CompanyInfoFragment() {
    }

    public static CompanyInfoFragment newInstance(Company company, Context cx) {
        CompanyInfoFragment fragment = new CompanyInfoFragment();
        fragment.company = company;
        fragment.cx = cx;
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static CompanyInfoFragment newInstance(Company company, Context cx,
                                                        CompanyInfoFragment.OnFragmentInteractionListener listener) {
        CompanyInfoFragment fragment = newInstance(company, cx);
        fragment.company = company;
        fragment.cx = cx;
        fragment.mListener = listener;
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        currentView = (ViewGroup)inflater.inflate(R.layout.fragment_company_add, container, false);

        currentView.findViewById(R.id.company_data_add).setOnClickListener(this);
        currentView.findViewById(R.id.add_shift).setOnClickListener(this);

        if (company != null) {
            ((EditText)currentView.findViewById(R.id.company_data_name)).setText(company.getName());

            for (Shift shift: company.getShifts()) {
                addShiftInternal(shift);
            }
        }

        return currentView;
    }

    private void addShiftInternal(Shift shift) {
        ShiftConfiguration configuration = new ShiftConfiguration(cx);
        configuration.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ViewGroup companyAddForm = currentView.findViewById(R.id.company_data_form);

        if (shift != null) {
            configuration.setShift(shift);
        }

        companyAddForm.addView(configuration, companyAddForm.getChildCount() - 2);
    }

    public void addShift(View v) {
        addShiftInternal(null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mListener != null) {
            return;
        }

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_shift) {
            addShift(v);
            return;
        }

        final EditText companyName = getView().findViewById(R.id.company_data_name);

        boolean accept = true;

        if (companyName.getText().toString().length() == 0) {
            companyName.setError("Company name cannot be empty");
            accept = false;
        }

        ViewGroup form = getView().findViewById(R.id.company_data_form);

        final List<Shift> shifts = new ArrayList<>();

        for (int i = 0; i < form.getChildCount(); ++i) {
            View child = form.getChildAt(i);

            if (!(child instanceof ShiftConfiguration)) {
                continue;
            }

            Shift shift = ((ShiftConfiguration)child).getShift();

            if (shift != null) {
                shifts.add(shift);
            }
        }

        if (!accept) return;

        CompaniesDatabase.getInstance().companyExists(companyName.getText().
                toString()).thenAccept(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {
                if (aBoolean && (company == null || !companyName.getText().toString().equals(CompanyInfoFragment.this.company.getId()))) {
                    companyName.setError("Company exists");
                } else {
                    mListener.companyDataFilled(companyName.getText().toString(), shifts);
                }
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void companyDataFilled(String name, List<Shift> shifts);
    }
}
