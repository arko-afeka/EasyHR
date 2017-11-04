package afeka.katz.arkadiy.easyhr.model;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by arkokat on 10/28/2017.
 */

@IgnoreExtraProperties
public class User {
    public static final String NAME_PROPERTY = "name";
    public static final String FAMILY_PROPERTY = "family";
    public static final String EMAIL_PROPERTY = "email";
    public static final String ID_PROPERTY = "id";
    public static final String COMPANIES_PROPERTY = "companies";
    public static final String IS_VERIFIED = "isVerified";

    private String name;
    private String family;

    private String email;
    private String id;

    private Boolean isVerified;

    private List<String> companies;

    public User() {
    }

    public User(FirebaseUser user) {
//        this.getIsVerified = user.isEmailVerified();
        this.id = user.getUid();
        this.companies = new ArrayList<>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return MessageFormat.format("{0} {1}", family, name);
    }

    public String getName() {
        return name;
    }

    public String getFamily() {
        return family;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public void addRelatedCompany(String company) {
        if (companies == null) {
            companies = new ArrayList<>();
        }

        if (companies.contains(company)) return;

        companies.add(company);
    }

    public void resignFromCompany(String company) {
        companies.remove(company);
    }

    public List<String> getCompanies() {
        if (companies == null) {
            companies = new ArrayList<>();
        }
        return companies;
    }

    public void setVerified() {
        this.isVerified = true;
    }

    public boolean getIsVerified() {
        return isVerified;
    }

    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> data = new HashMap<>();

        data.put(IS_VERIFIED, isVerified);
        data.put(NAME_PROPERTY, name);
        data.put(FAMILY_PROPERTY, family);
        data.put(EMAIL_PROPERTY, email);
        data.put(ID_PROPERTY, id);
        data.put(COMPANIES_PROPERTY, companies);

        return data;
    }
}
