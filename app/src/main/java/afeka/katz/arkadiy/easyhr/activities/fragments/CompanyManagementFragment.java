package afeka.katz.arkadiy.easyhr.activities.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import afeka.katz.arkadiy.easyhr.EasyHRContext;
import afeka.katz.arkadiy.easyhr.R;
import afeka.katz.arkadiy.easyhr.data.CompaniesDatabase;
import afeka.katz.arkadiy.easyhr.data.ShiftsDatabase;
import afeka.katz.arkadiy.easyhr.data.UsersDatabase;
import afeka.katz.arkadiy.easyhr.model.Company;
import afeka.katz.arkadiy.easyhr.model.Shift;
import afeka.katz.arkadiy.easyhr.model.User;

/**
 * Created by arkokat on 11/4/2017.
 */

public class CompanyManagementFragment extends Fragment implements View.OnClickListener,
        CompanyInfoFragment.OnFragmentInteractionListener, CompaniesDatabase.OnCompanyUpdate,
        EditWorkersFragment.OnFragmentInteractionListener, AssignShifts.OnFragmentInteractionListener {
    private Company company;
    private User currentUser;
    private ViewGroup currentView;

    public CompanyManagementFragment() {
        this.currentUser = EasyHRContext.getInstance().getCurrentUser();
    }

    public static CompanyManagementFragment newInstance(Company company) {
        CompanyManagementFragment fragment = new CompanyManagementFragment();
        fragment.company = company;
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
        if (!company.getManagers().contains(currentUser.getId())) {
            currentView = (ViewGroup) inflater.inflate(R.layout.fragment_company_management_worker, container, false);
        } else {
            currentView = (ViewGroup) inflater.inflate(R.layout.fragment_company_management_manager, container, false);
        }

        ((TextView)currentView.findViewById(R.id.company_name)).setText(company.getName());

        ViewGroup companyManagementOptions = currentView.findViewById(R.id.company_management_options);

        for (int i = 0; i < companyManagementOptions.getChildCount(); ++i) {
            View child = companyManagementOptions.getChildAt(i);

            child.setOnClickListener(this);
        }

        CompaniesDatabase.getInstance().addOnUpdateListener(company.getId(), this);

        return currentView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        CompaniesDatabase.getInstance().removeOnUpdateListener(company.getId());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void companyDataFilled(String name, List<Shift> shifts) {
        Company editedCompany = new Company(company);

        editedCompany.setName(name);
        editedCompany.setShifts(shifts);

        CompaniesDatabase.getInstance().updateCompany(editedCompany);

        getFragmentManager().popBackStack();
    }

    @Override
    public void onResume() {
        super.onResume();
//        ((TextView)getView().findViewById(R.id.company_name)).setText(company.getName());
//        getView().findViewById(R.id.company_name).invalidate();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_company:
                Fragment editCompany = CompanyInfoFragment.newInstance(company, this);
                getActivity().getFragmentManager().beginTransaction().
                    replace(R.id.content_panel, editCompany).addToBackStack(null).commit();
                break;
            case R.id.edit_workers:
                getActivity().findViewById(R.id.loading).setVisibility(View.VISIBLE);
                List<CompletableFuture<User>> users = new ArrayList<>();

                for (String userId: company.getWorkers()) {
                    users.add(UsersDatabase.getInstance().getUser(userId));
                }

                CompletableFuture.allOf(users.toArray(new CompletableFuture[users.size()])).
                        thenApply(x ->
                            users.stream().
                                map(CompletableFuture::join).collect(Collectors.toList())
                        ).thenAccept(usersList -> {
                    getActivity().findViewById(R.id.loading).setVisibility(View.INVISIBLE);

                    Fragment editWorkers = EditWorkersFragment.newInstance(usersList,
                            company.getManagers(), this);
                    getActivity().getFragmentManager().beginTransaction().
                            replace(R.id.content_panel, editWorkers).addToBackStack(null).commit();
                });
                break;
            case R.id.assign_shifts:
                CompletableFuture<Map<String, List<Shift>>> approved =
                        ShiftsDatabase.getInstance().getShiftsForNextWeek(company);
                CompletableFuture<Map<String, List<Shift>>> pending =
                        ShiftsDatabase.getInstance().getPendingShifts(company);
                if (company.getManagers().contains(currentUser.getId())) {
                    getActivity().findViewById(R.id.loading).setVisibility(View.VISIBLE);
                    List<CompletableFuture<User>> companyUsers = new ArrayList<>();

                    for (String userId: company.getWorkers()) {
                        companyUsers.add(UsersDatabase.getInstance().getUser(userId));
                    }

                    CompletableFuture.allOf(companyUsers.toArray(new CompletableFuture[companyUsers.size()])).
                            thenApply(x ->
                                    companyUsers.stream().
                                            map(CompletableFuture::join).collect(Collectors.toList())
                            ).thenAccept(usersList -> {

                        CompletableFuture.allOf(pending, approved).thenAccept(data -> {
                            Map<String, List<Shift>> pendingResult = pending.join();
                            Map<String, List<Shift>> approvedResult = approved.join();


                            Map<String, User> userIdMapping =
                                    usersList.stream().collect(Collectors.toMap(User::getId, x -> x));

                            Map<User, List<Shift>> pendingShifts = pendingResult.entrySet().stream().
                                    filter(x -> userIdMapping.containsKey(x.getKey())).collect(
                                            Collectors.toMap(x ->
                                                    userIdMapping.get(x.getKey()),
                                                    Map.Entry::getValue));
                            Map<User, List<Shift>> approvedShifts = approvedResult.entrySet().stream().
                                    filter(x -> userIdMapping.containsKey(x.getKey())).collect(
                                    Collectors.toMap(x ->
                                                    userIdMapping.get(x.getKey()),
                                            Map.Entry::getValue));

                            Fragment editWorkers =
                                    AssignShifts.newInstance(company, usersList, approvedShifts,
                                            pendingShifts, CompanyManagementFragment.this);
                            getActivity().getFragmentManager().beginTransaction().
                                    replace(R.id.content_panel, editWorkers).addToBackStack(null).commit();
                            getActivity().findViewById(R.id.loading).setVisibility(View.INVISIBLE);

                        });
                    });

                } else {
                    CompletableFuture.allOf(pending, approved).thenAccept(data -> {
                        Map<String, List<Shift>> pendingResult = pending.join();
                        Map<String, List<Shift>> approvedResult = approved.join();

                        Fragment assignShifts =
                                AssignShifts.newInstance(currentUser, company,
                                        pendingResult.getOrDefault(currentUser.getId(), new ArrayList<>()),
                                        approvedResult.getOrDefault(currentUser.getId(), new ArrayList<>()),
                                         CompanyManagementFragment.this);
                        getActivity().getFragmentManager().beginTransaction().
                                replace(R.id.content_panel, assignShifts).addToBackStack(null).commit();
                        getActivity().findViewById(R.id.loading).setVisibility(View.INVISIBLE);

                    });
                }
                break;
            case R.id.resign:
                company.removeWorker(currentUser.getId());
                CompaniesDatabase.getInstance().updateCompany(company);
                currentUser.resignFromCompany(company.getId());
                UsersDatabase.getInstance().updateUser(currentUser);
                break;
        }
    }

    @Override
    public void companyUpdated(String id, Company newData) {
        company = newData;

        if (!company.getWorkers().contains(currentUser.getId())) {
            getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        CompaniesDatabase.getInstance().updateCompany(newData);
    }

    @Override
    public void workersUpdated(List<User> workers, List<String> managers) {
        workers.forEach(x -> {
            x.addRelatedCompany(company.getId());
            UsersDatabase.getInstance().updateUser(x);
        });

        List<String> workerUids = workers.stream().map(User::getId).
                collect(Collectors.toList());

        workerUids.add(currentUser.getId());
        managers.add(currentUser.getId());

        company.updateManagers(managers);
        company.updateWorkers(workerUids);

        CompaniesDatabase.getInstance().updateCompany(company);

        getFragmentManager().popBackStack();
    }

    private void setShifts(Map<User, List<Shift>> assignedShifts) {
        for (Map.Entry<User, List<Shift>> userShift: assignedShifts.entrySet()) {
            for (Shift shift: userShift.getValue()) {
                ShiftsDatabase.getInstance().addApprovedShift(userShift.getKey(), company, shift);
            }
        }
    }

    private void requestForShifts(List<Shift> selectedShifts) {
        for (Shift shift: selectedShifts) {
            ShiftsDatabase.getInstance().addPendingShift(currentUser, company, shift);
        }
    }

    @Override
    public void onShiftsAssigned(Map<User, List<Shift>> selectedShifts) {
        if (company.getManagers().contains(currentUser.getId())) {
            setShifts(selectedShifts);
        } else if (company.getWorkers().contains(currentUser.getId())) {
            requestForShifts(selectedShifts.values().isEmpty() ? Collections.EMPTY_LIST :
                    selectedShifts.get(0));
        }

        getFragmentManager().popBackStack();
    }
}
