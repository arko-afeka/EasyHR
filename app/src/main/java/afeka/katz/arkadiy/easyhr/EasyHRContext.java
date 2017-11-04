package afeka.katz.arkadiy.easyhr;

import afeka.katz.arkadiy.easyhr.model.User;

/**
 * Created by arkokat on 11/4/2017.
 */

public class EasyHRContext {
    private static final EasyHRContext ourInstance = new EasyHRContext();

    private User currentUser;

    public static EasyHRContext getInstance() {
        return ourInstance;
    }

    private EasyHRContext() {
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public int defaultToastDuration() { return 10; }
}
