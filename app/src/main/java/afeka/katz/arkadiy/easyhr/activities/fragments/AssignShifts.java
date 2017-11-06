package afeka.katz.arkadiy.easyhr.activities.fragments;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import afeka.katz.arkadiy.easyhr.R;
import afeka.katz.arkadiy.easyhr.model.Company;
import afeka.katz.arkadiy.easyhr.model.Shift;
import afeka.katz.arkadiy.easyhr.model.User;
import afeka.katz.arkadiy.easyhr.views.ShiftView;

public class AssignShifts extends Fragment implements View.OnClickListener {
    private OnFragmentInteractionListener mListener;
    private boolean isManager;
    private List<User> workers;
    private Company company;
    private Map<User, List<Shift>> pendingShitfs;
    private Map<User, List<Shift>> approvedShifts;

    public AssignShifts() {
        // Required empty public constructor
    }

    public static AssignShifts newInstance(User worker, Company company, List<Shift> approvedShifts,
                                           List<Shift> pendingShifts, OnFragmentInteractionListener mListener) {
        AssignShifts fragment = new AssignShifts();
        fragment.isManager = false;
        fragment.workers = Collections.singletonList(worker);
        fragment.pendingShitfs = Collections.singletonMap(worker, pendingShifts);
        fragment.approvedShifts = Collections.singletonMap(worker, approvedShifts);
        fragment.mListener = mListener;
        fragment.company = company;
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;

    }

    public static AssignShifts newInstance(Company company, List<User> workers, Map<User, List<Shift>> approvedShifts,
                                           Map<User, List<Shift>> pendingShitfs, OnFragmentInteractionListener mListener) {
        AssignShifts fragment = new AssignShifts();
        fragment.isManager = true;
        fragment.workers = workers;
        fragment.mListener = mListener;
        fragment.company = company;
        fragment.approvedShifts = approvedShifts;
        fragment.pendingShitfs = pendingShitfs;
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void addManagerShift(ViewGroup shifts, Shift shift) {
        LinearLayout shiftLayout = new LinearLayout(getContext());

        shiftLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        shiftLayout.setOrientation(LinearLayout.VERTICAL);
        ShiftView shiftView = new ShiftView(getContext());
        shiftView.setShift(shift);
        shiftLayout.addView(shiftView);
        shiftLayout.setTag(shift);

        for (User user : workers) {
            LinearLayout workerLayout = new LinearLayout(getContext());
            workerLayout.setOrientation(LinearLayout.HORIZONTAL);
            workerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            workerLayout.setPadding(20, 20, 20, 20);

            TextView userName = new TextView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight = 1;
            userName.setLayoutParams(params);
            userName.setText(user.getFullName());

            CheckBox selectedWorker = new CheckBox(getContext());

            if (approvedShifts.entrySet().stream().anyMatch(
                    x -> x.getKey().equals(user.getId()) && x.getValue().stream().anyMatch(y -> y.getId().equals(shift.getId()))
                )) {
                selectedWorker.setChecked(true);
            } else if (pendingShitfs.entrySet().stream().anyMatch(
                    x -> x.getKey().equals(user.getId()) && x.getValue().stream().anyMatch(y -> y.getId().equals(shift.getId()))
            )) {
                workerLayout.setBackgroundColor(getResources().getColor(R.color.yellow));
            }

            workerLayout.setTag(user);
            workerLayout.addView(userName);
            workerLayout.addView(selectedWorker);
            shiftLayout.addView(workerLayout);
        }

        shifts.addView(shiftLayout);
    }

    private void addWorkerShift(ViewGroup shifts, Shift shift) {
        LinearLayout layout = new LinearLayout(getContext());

        layout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        layout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        ShiftView shiftView = new ShiftView(getContext());
        shiftView.setLayoutParams(params);
        CheckBox selectedShift = new CheckBox(getContext());
        shiftView.setShift(shift);
        selectedShift.setId(getContext().getResources().getIdentifier(
                "selected_shift", "id",
                getContext().getPackageName()));

        List<Shift> userShifts = pendingShitfs.get(workers.get(0));

        if (userShifts.stream().anyMatch(x -> x.getId().equals(shift.getId()))) {
            selectedShift.setChecked(true);
        }

        userShifts = approvedShifts.get(workers.get(0));

        if (userShifts.stream().anyMatch(x -> x.getId().equals(shift.getId()))) {
            layout.setBackgroundColor(getResources().getColor(R.color.green));
        }


        layout.setTag(shift);
        layout.addView(shiftView);
        layout.addView(selectedShift);

        shifts.addView(layout);
    }

    @Override
    public void onClick(View v) {
        ViewGroup shitfsView = getView().findViewById(R.id.shifts);
        Map<User, List<Shift>> result = new HashMap<>();

        for (int i = 0; i < shitfsView.getChildCount(); ++i) {
            ViewGroup shiftView = (ViewGroup)shitfsView.getChildAt(i);
            Shift shift = (Shift)shiftView.getTag();

            if (!isManager) {
                result.putIfAbsent(workers.get(0), new ArrayList<>());
                CheckBox selected = shiftView.findViewById(getContext().getResources().getIdentifier(
                        "selected_shift", "id",
                        getContext().getPackageName()));

                if (!selected.isChecked()) {
                    continue;
                }

                result.get(workers.get(0)).add(shift);
            } else {
                for (int j = 1; j < shiftView.getChildCount(); ++j) {
                    ViewGroup userView = (ViewGroup)shiftView.getChildAt(j);

                    User user = (User)userView.getTag();

                    CheckBox selected = (CheckBox)userView.getChildAt(1);

                    if (!selected.isChecked()) {
                        continue;
                    }

                    result.putIfAbsent(user, new ArrayList<>());
                    result.get(user).add(shift);
                }
            }
        }

        mListener.onShiftsAssigned(result);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup currentView =
                (ViewGroup)inflater.inflate(R.layout.fragment_assign_shifts, container, false);

        ViewGroup shiftsView = currentView.findViewById(R.id.shifts);

        for (Shift shift : company.getShifts()) {
            if (isManager) {
                addManagerShift(shiftsView, shift);
            } else {
                addWorkerShift(shiftsView, shift);
            }
        }

        currentView.findViewById(R.id.save).setOnClickListener(this);

        return currentView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (mListener != null) return;
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
        void onShiftsAssigned(Map<User, List<Shift>> selectedShifts);
    }
}
