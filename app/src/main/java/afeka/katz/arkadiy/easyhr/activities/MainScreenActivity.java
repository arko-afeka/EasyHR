package afeka.katz.arkadiy.easyhr.activities;

import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.UserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import afeka.katz.arkadiy.easyhr.EasyHRContext;
import afeka.katz.arkadiy.easyhr.R;
import afeka.katz.arkadiy.easyhr.activities.fragments.CompanyInfoFragment;
import afeka.katz.arkadiy.easyhr.activities.fragments.UserInfoFragment;
import afeka.katz.arkadiy.easyhr.data.CompaniesDatabase;
import afeka.katz.arkadiy.easyhr.data.UsersDatabase;
import afeka.katz.arkadiy.easyhr.model.Company;
import afeka.katz.arkadiy.easyhr.model.Shift;
import afeka.katz.arkadiy.easyhr.model.User;
import afeka.katz.arkadiy.easyhr.activities.listeners.MenuListener;

public class MainScreenActivity extends AppCompatActivity implements CompaniesDatabase.OnCompanyUpdate,
        CompanyInfoFragment.OnFragmentInteractionListener, UserInfoFragment.OnFragmentInteractionListener {
    private Map<String, Company> userCompanies = new HashMap<>();

    @Override
    public void onUserDataFilled(String name, String familyName) {
        User currentUser = EasyHRContext.getInstance().getCurrentUser();

        currentUser.setName(name);
        currentUser.setFamily(familyName);

        UsersDatabase.getInstance().updateUser(currentUser);

        Toast.makeText(this, "User data has been updated", EasyHRContext.getInstance().defaultToastDuration()).show();
        getFragmentManager().popBackStack();
    }

    @Override
    public void companyDataFilled(String name, List<Shift> shifts) {
        Company newCompany = new Company(UUID.randomUUID().toString(), name, shifts);
        newCompany.addManager(EasyHRContext.getInstance().getCurrentUser().getId());
        CompaniesDatabase.getInstance().updateCompany(newCompany);
        userCompanies.put(newCompany.getId(), newCompany);
        UsersDatabase.getInstance().updateUser(EasyHRContext.getInstance().getCurrentUser());
        this.invalidateOptionsMenu();

        Toast.makeText(this, "New company has been created", EasyHRContext.getInstance().defaultToastDuration()).show();
        getFragmentManager().popBackStack();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout);

        User curentUser = EasyHRContext.getInstance().getCurrentUser();

        List<CompletableFuture<Company>> companies = new ArrayList<>();
        for (String companyId: curentUser.getCompanies()) {
            companies.add(CompaniesDatabase.getInstance().getCompany(companyId));
        }

        CompletableFuture.allOf(companies.toArray(new CompletableFuture[companies.size()])).
                thenApply(x -> companies.stream().
                    map(future -> future.join()).collect(Collectors.toList())).thenAccept(list -> {
            list.forEach(x -> userCompanies.put(x.getId(), x));
            Toolbar myToolbar = findViewById(R.id.toolbar);
            setSupportActionBar(myToolbar);
            myToolbar.setOnMenuItemClickListener(new MenuListener(userCompanies, this));
            findViewById(R.id.loading).setVisibility(View.GONE);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);

        for (Company company: userCompanies.values()) {
            menu.add(company.getName());
            CompaniesDatabase.getInstance().addOnUpdateListener(company.getId(), this);
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        FragmentManager manager = getFragmentManager();

        if (manager.getBackStackEntryCount() > 0) {
            manager.popBackStackImmediate();
        } else {
            finish();
        }
    }

    @Override
    public void companyUpdated(String id, Company newData) {
        if (!userCompanies.containsKey(id)) return;

        User currentUser = EasyHRContext.getInstance().getCurrentUser();

        if (!newData.getWorkers().contains(currentUser.getId()) &&
                !newData.getManagers().contains(currentUser.getId())) {
            userCompanies.remove(id);
        }

        userCompanies.put(id, newData);

        this.invalidateOptionsMenu();
    }
}
