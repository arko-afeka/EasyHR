package afeka.katz.arkadiy.easyhr.activities.fragments;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import afeka.katz.arkadiy.easyhr.R;

public class CompanyInfoFragment extends Fragment implements View.OnClickListener {
    private OnFragmentInteractionListener mListener;

    public CompanyInfoFragment() {
    }

    public static CompanyInfoFragment newInstance() {
        CompanyInfoFragment fragment = new CompanyInfoFragment();
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
        ViewGroup viewGroup = (ViewGroup)inflater.inflate(R.layout.fragment_company_add, container, false);

        viewGroup.findViewById(R.id.company_data_add).setOnClickListener(this);

        return viewGroup;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onClick(View v) {
        EditText companyName = getView().findViewById(R.id.company_data_name);

        boolean accept = true;

        if (companyName.getText().toString().length() == 0) {
            companyName.setError("Company name cannot be empty");
            accept = false;
        }

        if (accept) {
            mListener.companyDataFilled(companyName.getText().toString());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void companyDataFilled(String name);
    }
}
