package afeka.katz.arkadiy.easyhr.data;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;

import afeka.katz.arkadiy.easyhr.model.Company;

/**
 * Created by arkokat on 10/28/2017.
 */

public class ComapniesDatabase {
    private static final String TAG = ComapniesDatabase.class.getCanonicalName();
    public static final String REF = "companies";

    public ComapniesDatabase() {

    }

//    public CompletableFuture<Boolean> createNewCompany(final Company company) {
//        DatabaseReference ref = DatabaseConst.CONNECTION.getReference(REF);
//        final CompletableFuture<Boolean> result = new CompletableFuture<>();
//
//        DatabaseReference compaines = ref.child(company.getName());
//        compaines.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                result.complete(dataSnapshot.hasChild(company.getName()));
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                result.complete(false);
//
//            }
//        });
//
//        if (!waitListener.getResult()) {
//            ref.setValue(company.getName(), company);
//        }
//
//        return waitListener.getResult();
//    }
}
