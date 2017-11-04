package afeka.katz.arkadiy.easyhr.data;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import afeka.katz.arkadiy.easyhr.model.Company;

/**
 * Created by arkokat on 10/28/2017.
 */

public class CompaniesDatabase {
    private static final String TAG = CompaniesDatabase.class.getCanonicalName();
    private static final String REF = "companies";
    private static CompaniesDatabase companiesDatabase = new CompaniesDatabase();
    private Map<String, ValueEventListener> listeners;

    private CompaniesDatabase() {
        listeners = new HashMap<>();
    }

    public static CompaniesDatabase getInstance() {
        return companiesDatabase;
    }

    public CompletableFuture<Boolean> companyExists(final String company) {
        final DatabaseReference ref = DatabaseConst.CONNECTION.getReference(REF);
        final CompletableFuture<Boolean> result = new CompletableFuture<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                result.complete(dataSnapshot.hasChild(company));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.complete(true);
            }
        });

        return result;
    }

    public void updateCompany(final Company company) {
        DatabaseConst.CONNECTION.getReference(REF).
                child(company.getId()).updateChildren(company.toMap());


    }

    public void removeCompany(String id) {
        DatabaseConst.CONNECTION.getReference(REF).child(id).removeValue();
    }

    public CompletableFuture<Company> getCompany(final String id) {
        final DatabaseReference ref = DatabaseConst.CONNECTION.getReference(REF).child(id);
        final CompletableFuture<Company> result = new CompletableFuture<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                result.complete(dataSnapshot.getValue(Company.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.complete(null);
            }
        });

        return result;
    }

    public void addOnUpdateListener(final String id, final OnCompanyUpdate listener) {
        this.listeners.put(id, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.companyUpdated(id, dataSnapshot.exists() ? dataSnapshot.getValue(Company.class) : null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                return;
            }
        });
        DatabaseConst.CONNECTION.getReference(REF).child(id).addValueEventListener(this.listeners.get(id));
    }

    public void removeOnUpdateListener(final String id) {
        if (listeners.containsKey(id)) {
            DatabaseConst.CONNECTION.getReference(REF).child(id).removeEventListener(listeners.get(id));
            listeners.remove(id);
        }
    }

    public interface OnCompanyUpdate {
        void companyUpdated(String id, Company newData);
    }
}
