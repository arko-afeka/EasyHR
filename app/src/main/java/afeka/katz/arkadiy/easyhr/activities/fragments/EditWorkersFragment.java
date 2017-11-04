package afeka.katz.arkadiy.easyhr.activities.fragments;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import afeka.katz.arkadiy.easyhr.EasyHRContext;
import afeka.katz.arkadiy.easyhr.R;
import afeka.katz.arkadiy.easyhr.data.UsersDatabase;
import afeka.katz.arkadiy.easyhr.model.User;
import afeka.katz.arkadiy.easyhr.views.WorkerConfiguration;

public class EditWorkersFragment extends Fragment implements View.OnClickListener {
    private OnFragmentInteractionListener mListener;
    private List<User> users;
    private List<String> managers;
    private ViewGroup currentView;

    public EditWorkersFragment() {
    }

    public static EditWorkersFragment newInstance(List<User> users, List<String> managers) {
        EditWorkersFragment fragment = new EditWorkersFragment();
        fragment.users = users;
        fragment.managers = managers;
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;

    }
    public static EditWorkersFragment newInstance(List<User> users, List<String> managers,
                                                  OnFragmentInteractionListener listener) {
        EditWorkersFragment fragment = newInstance(users, managers);
        fragment.mListener = listener;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        currentView = (ViewGroup)inflater.inflate(R.layout.fragment_edit_workers, container, false);

        currentView.findViewById(R.id.save).setOnClickListener(this);
        currentView.findViewById(R.id.add_worker).setOnClickListener(this);

        users.stream().filter(x ->
                !EasyHRContext.getInstance().getCurrentUser().getId().equals(x.getId())).
        forEach(this::addWorker);

        return currentView;
    }

    private void addWorker(User user) {
        WorkerConfiguration configuration = new WorkerConfiguration(getContext());

        if (user != null) {
            configuration.setUser(user.getEmail(), managers.contains(user.getId()));
        }

        ((ViewGroup)(currentView.findViewById(R.id.workers))).addView(configuration);
    }

    private void validate() {
        boolean accept = true;

        ViewGroup workersView = getView().findViewById(R.id.workers);
        List<CompletableFuture<Pair<User, Boolean>>> usersFuture = new ArrayList<>();

        for (int i = 0; i < workersView.getChildCount(); ++i) {
            WorkerConfiguration configuration = (WorkerConfiguration)workersView.getChildAt(i);

            boolean isManager = configuration.isManager();
            String email = configuration.getEmail();

            if (!EmailValidator.getInstance().isValid(email)) {
                accept = false;
                configuration.setError("EMail is invalid");
            } else {
                if (users.stream().anyMatch(x -> x.getEmail().equals(email))) {
                    continue;
                }

                usersFuture.add(UsersDatabase.getInstance().findByEMail(email).thenApply(
                        x -> {
                            if (x == null) configuration.setError("User doesn't exist");
                            return Pair.create(x, isManager);
                        }
                ));
            }
        }

        if (accept && usersFuture.isEmpty()) {
            mListener.workersUpdated(new ArrayList<>(), new ArrayList<>());
            return;
        }

        if (accept) {
            CompletableFuture.allOf(usersFuture.toArray(new CompletableFuture[usersFuture.size()])).
                    thenApply(
                            x -> usersFuture.stream().map(userData -> userData.join()).
                                    collect(Collectors.toList())
                    ).thenAccept(x -> {
                if (x.stream().anyMatch(pair -> pair.first == null)) {
                    return;
                } else {
                    List<User> workers = x.stream().map(pair -> pair.first).
                            collect(Collectors.toList());
                    List<String> managers = x.stream().filter(pair -> pair.second).
                            map(pair -> pair.first.getId()).collect(Collectors.toList());

                    mListener.workersUpdated(workers, managers);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:
                validate();
                break;
            case R.id.add_worker:
                addWorker(null);
                break;
        }
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
        void workersUpdated(List<User> workers, List<String> managers);
    }
}
