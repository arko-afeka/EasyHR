package afeka.katz.arkadiy.easyhr.model;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by arkokat on 10/28/2017.
 */

public class Company {
    public static final String NAME_PROPERTY = "name";
    public static final String MANAGERS_PROPERTY = "managers";
    public static final String WORKERS_PROPERTY = "workers";

    private String name;
    private Set<String> managers;
    private Set<String> workers;

    public Company() {

    }

    public Company(String name) {
        this.name = name;
        this.managers = new HashSet<>();
        this.workers = new HashSet<>();
    }

    public Set<String> getManagers() {
        return managers;
    }

    public Set<String> getWorkers() {
        return workers;
    }

    public void addManager(String managerUid) {
        managers.add(managerUid);
    }

    public void addWorker(String worker) {
        workers.add(worker);
    }

    public String getName() {
        return name;
    }

    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> data = new HashMap<>();

        data.put(NAME_PROPERTY, name);
        data.put(MANAGERS_PROPERTY, managers);
        data.put(WORKERS_PROPERTY, workers);

        return data;
    }
}
