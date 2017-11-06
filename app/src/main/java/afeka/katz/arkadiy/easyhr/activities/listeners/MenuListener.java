package afeka.katz.arkadiy.easyhr.activities.listeners;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import afeka.katz.arkadiy.easyhr.EasyHRContext;
import afeka.katz.arkadiy.easyhr.R;
import afeka.katz.arkadiy.easyhr.activities.StartUpActivity;
import afeka.katz.arkadiy.easyhr.activities.fragments.CompanyInfoFragment;
import afeka.katz.arkadiy.easyhr.activities.fragments.CompanyManagementFragment;
import afeka.katz.arkadiy.easyhr.activities.fragments.UserInfoFragment;
import afeka.katz.arkadiy.easyhr.data.CompaniesDatabase;
import afeka.katz.arkadiy.easyhr.model.Company;


public class MenuListener implements Toolbar.OnMenuItemClickListener {
    private final AppCompatActivity cx;
    private Map<String, Company> userCompanies;

    public MenuListener(Map<String, Company> userCompanies, AppCompatActivity cx) {
        this.cx = cx;
        this.userCompanies = userCompanies;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        final FragmentManager manager = cx.getFragmentManager();

        switch (item.getItemId()) {
            case R.id.create_company:
                FragmentTransaction createCompanyTransaction = manager.beginTransaction();
                createCompanyTransaction.replace(R.id.content_panel, CompanyInfoFragment.newInstance(null));
                createCompanyTransaction.addToBackStack(null).commit();
                break;
            case R.id.edit_info:
                FragmentTransaction editUserTransaction = manager.beginTransaction();
                editUserTransaction.replace(R.id.content_panel, UserInfoFragment.newInstance(EasyHRContext.getInstance().getCurrentUser()));
                editUserTransaction.addToBackStack(null).commit();
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent startup = new Intent(cx, StartUpActivity.class);
                startup.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                cx.startActivity(startup);
                break;
            default:
                String name = item.getTitle().toString();
                Optional<Company> company = userCompanies.values().stream().filter(x -> x.getName().equals(name)).findFirst();

                if (!company.isPresent()) return false;

                CompanyManagementFragment companyManagementFragment = CompanyManagementFragment.newInstance(company.get());
                FragmentTransaction companyManagementTransaction = manager.beginTransaction();
                companyManagementTransaction.replace(R.id.content_panel, companyManagementFragment).addToBackStack(null).
                        commit();
        }

        return true;
    }
}
