package afeka.katz.arkadiy.easyhr.data;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.CompletableFuture;

import afeka.katz.arkadiy.easyhr.model.Company;

/**
 * Created by arkokat on 10/28/2017.
 */

public class CompaniesDatabase {
    private static final String TAG = CompaniesDatabase.class.getCanonicalName();
    public static final String REF = "companies";

    public CompaniesDatabase() {

    }

    public CompletableFuture<Boolean> companyExists(final Company company) {
        final DatabaseReference ref = DatabaseConst.CONNECTION.getReference(REF);
        final CompletableFuture<Boolean> result = new CompletableFuture<>();

        final DatabaseReference companies = ref.child(company.getName());
        companies.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                result.complete(dataSnapshot.hasChild(company.getName()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.complete(null);
            }
        });

        return result;
    }

    public void updateCompany(final Company company) {
        DatabaseConst.CONNECTION.getReference(REF).
                child(company.getName()).updateChildren(company.toMap());
    }
}
