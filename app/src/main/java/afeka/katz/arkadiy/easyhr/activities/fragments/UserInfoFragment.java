package afeka.katz.arkadiy.easyhr.activities.fragments;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.apache.commons.lang3.StringUtils;

import afeka.katz.arkadiy.easyhr.R;
import afeka.katz.arkadiy.easyhr.model.User;

public class UserInfoFragment extends Fragment implements View.OnClickListener {
    private OnFragmentInteractionListener mListener;
    private User user;

    public UserInfoFragment() {
    }

    public static UserInfoFragment newInstance(User user) {
        UserInfoFragment fragment = new UserInfoFragment();
        fragment.user = user;
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onClick(View v) {
        boolean accept = true;
        EditText name = getView().findViewById(R.id.user_data_first_name);

        if (name.getText().toString().length() == 0) {
            accept = false;
            name.setError("Cannot be blank");
        } else if (!StringUtils.isAlpha(name.getText().toString())) {
            accept = false;
            name.setError("May contain only letters (a-Z)");
        }

        EditText lastName = getView().findViewById(R.id.user_data_last_name);

        if (lastName.getText().toString().length() == 0) {
            accept = false;
            lastName.setError("Last name cannot be blank");
        }else if (!StringUtils.isAlpha(lastName.getText().toString())) {
            accept = false;
            lastName.setError("May contain only letters (a-Z)");
        }

        if (accept) {
            mListener.onUserDataFilled(name.getText().toString(), lastName.getText().toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup fragmentView = (ViewGroup)inflater.inflate(R.layout.fragment_user_data, container, false);

        fragmentView.findViewById(R.id.sign_in).setOnClickListener(this);

        if (user != null) {
            ((EditText)fragmentView.findViewById(R.id.user_data_first_name)).setText(user.getName());
            ((EditText)fragmentView.findViewById(R.id.user_data_last_name)).setText(user.getFamily());
        }

        return fragmentView;
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onUserDataFilled(String name, String familyName);
    }
}
