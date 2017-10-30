package afeka.katz.arkadiy.easyhr.data;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by arkokat on 10/28/2017.
 */

class DatabaseConst {

    public static final FirebaseDatabase CONNECTION;

    static {
        CONNECTION = FirebaseDatabase.getInstance();
    }
}
