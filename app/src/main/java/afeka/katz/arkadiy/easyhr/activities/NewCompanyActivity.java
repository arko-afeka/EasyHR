package afeka.katz.arkadiy.easyhr.activities;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.List;
import java.util.UUID;

import afeka.katz.arkadiy.easyhr.EasyHRContext;
import afeka.katz.arkadiy.easyhr.R;
import afeka.katz.arkadiy.easyhr.activities.fragments.CompanyInfoFragment;
import afeka.katz.arkadiy.easyhr.data.CompaniesDatabase;
import afeka.katz.arkadiy.easyhr.data.UsersDatabase;
import afeka.katz.arkadiy.easyhr.model.Company;
import afeka.katz.arkadiy.easyhr.model.Shift;
import afeka.katz.arkadiy.easyhr.model.User;

public class NewCompanyActivity extends AppCompatActivity implements CompanyInfoFragment.OnFragmentInteractionListener {

    @Override
    public void companyDataFilled(String name, List<Shift> shifts) {
        Company company = new Company(UUID.randomUUID().toString(), name, shifts);
        User currentUser = EasyHRContext.getInstance().getCurrentUser();
        currentUser.addRelatedCompany(company.getId());
        UsersDatabase.getInstance().updateUser(currentUser);
        company.addManager(currentUser.getId());
        CompaniesDatabase.getInstance().updateCompany(company);

        Intent mainActivity = new Intent(this, MainScreenActivity.class);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        CompanyInfoFragment fragment = CompanyInfoFragment.newInstance(null, this);
        transaction.add(R.id.content_panel, fragment).commit();
        findViewById(R.id.loading).setVisibility(View.GONE);
    }
}
