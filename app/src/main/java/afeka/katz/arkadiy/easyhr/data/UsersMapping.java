package afeka.katz.arkadiy.easyhr.data;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.CompletableFuture;

import afeka.katz.arkadiy.easyhr.model.User;

/**
 * Created by arkokat on 10/30/2017.
 */

public class UsersMapping {
    public static final String REF = "users";
    private static final String TAG = UsersDatabase.class.getCanonicalName();

    public void addUser(User user) {
        DatabaseReference ref = DatabaseConst.CONNECTION.getReference(REF);
        ref.setValue(user.getId(), user);
    }
}
