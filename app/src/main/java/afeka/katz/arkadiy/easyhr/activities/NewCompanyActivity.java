package afeka.katz.arkadiy.easyhr.activities;

import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import afeka.katz.arkadiy.easyhr.R;
import afeka.katz.arkadiy.easyhr.activities.fragments.CompanyInfoFragment;

public class NewCompanyActivity extends AppCompatActivity implements CompanyInfoFragment.OnFragmentInteractionListener {

    @Override
    public void companyDataFilled(String name) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_company);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        CompanyInfoFragment fragment = CompanyInfoFragment.newInstance();
        transaction.add(R.id.contentPanel, fragment).commit();
        findViewById(R.id.loading).setVisibility(View.GONE);
    }
}
