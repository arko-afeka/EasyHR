package afeka.katz.arkadiy.easyhr.data;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import afeka.katz.arkadiy.easyhr.model.User;

/**
 * Created by arkokat on 10/28/2017.
 */

public class UsersDatabase {
    public static final String REF = "users";
    private static final String TAG = UsersDatabase.class.getCanonicalName();

    public CompletableFuture<User> getUser(final String id) {
        final CompletableFuture<User> result = new CompletableFuture<>();

        Query reference = DatabaseConst.CONNECTION.getReference(REF).limitToFirst(1);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean exists = dataSnapshot.hasChild(User.NAME_PROPERTY);

                if (exists) {
                    DataSnapshot data = dataSnapshot.child(id);

                    result.complete(data.getValue(User.class));
                }


                result.complete(null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.completeExceptionally(databaseError.toException());
            }
        });

        return result;
    }

    public void addUser(User user) {
        DatabaseReference ref = DatabaseConst.CONNECTION.getReference(REF).child(user.getId());
        ref.updateChildren(user.toMap());
    }
}
