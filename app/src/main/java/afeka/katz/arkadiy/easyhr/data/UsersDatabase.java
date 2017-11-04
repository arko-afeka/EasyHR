package afeka.katz.arkadiy.easyhr.data;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import afeka.katz.arkadiy.easyhr.model.User;

/**
 * Created by arkokat on 10/28/2017.
 */

public class UsersDatabase {
    private static final String REF = "users";
    private static final String TAG = UsersDatabase.class.getCanonicalName();
    private static final UsersDatabase database = new UsersDatabase();
    private Map<String, OnUserUpdate> listeners;

    private UsersDatabase() {
        listeners = new HashMap<>();
    }

    public static UsersDatabase getInstance() {
        return database;
    }

    public CompletableFuture<User> getUser(final String id) {
        final CompletableFuture<User> result = new CompletableFuture<>();

        Query reference = DatabaseConst.CONNECTION.getReference(REF).
                orderByChild(User.ID_PROPERTY).equalTo(id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean exists = dataSnapshot.hasChild(id);

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

    public CompletableFuture<User> findByEMail(String email) {
        CompletableFuture<User> result = new CompletableFuture<>();
        DatabaseReference ref = DatabaseConst.CONNECTION.getReference(REF);
        ref.orderByChild(User.EMAIL_PROPERTY).equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    result.complete(dataSnapshot.getChildren().
                            iterator().next().getValue(User.class));
                } else {
                    result.complete(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.complete(null);
            }
        });

        return result;
    }

    public CompletableFuture<Boolean> updateUser(User user) {
        DatabaseReference ref = DatabaseConst.CONNECTION.getReference(REF).child(user.getId());
        final CompletableFuture<Boolean> result = new CompletableFuture<>();
        ref.updateChildren(user.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                result.complete(task.isSuccessful());
            }
        });

        return result;
    }

    public interface OnUserUpdate {
        void onUserUpdate(String id, User user);
    }
}
