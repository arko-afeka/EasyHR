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
    public static final String SHIFTS_PROPERTY = "shifts";
    public static final String ID_PROPERTY = "id";

    private String name;
    private String id;
    private List<String> managers;
    private List<String> workers;
    private List<Shift> shifts;

    public Company() {

    }

    public Company(String id, String name, List<Shift> shifts) {
        this.id = id;
        this.name = name;
        this.shifts = shifts;
        this.managers = new ArrayList<>();
        this.workers = new ArrayList<>();
    }

    public Company(Company company) {
        this.name = new String(company.getName());
        this.shifts = new ArrayList<>(company.getShifts());
        this.managers = new ArrayList<>(company.getManagers());
        this.workers = new ArrayList<>(company.getWorkers());
        this.id = company.id;
    }

    public List<String> getManagers() {
        if (managers == null) managers = new ArrayList<>();

        return new ArrayList<>(managers);
    }

    public List<String> getWorkers() {
        if (workers == null) workers = new ArrayList<>();

        return new ArrayList<>(workers);
    }

    public void addManager(String managerUid) {
        if (managers == null) managers = new ArrayList<>();

        if (workers.contains(managerUid)) return;

        workers.add(managerUid);
        managers.add(managerUid);
    }

    public void addWorker(String worker) {
        if (workers == null) workers = new ArrayList<>();

        if (worker.contains(worker)) return;

        workers.add(worker);
    }

    public void updateManagers(List<String> managers) {
        this.managers = new ArrayList<>(managers);

        this.managers.forEach(this::addWorker);
    }
    public void updateWorkers(List<String> workers) {
        this.workers = new ArrayList<>(workers);
    }

    public String getName() {
        return name;
    }

    public List<Shift> getShifts() {
        if (shifts == null) shifts = new ArrayList<>();

        return shifts;
    }

    public String getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setShifts(List<Shift> shifts) {
        this.shifts = new ArrayList<>(shifts);
    }

    public void updateCompany(Company company) {
        this.name = company.getName();
        this.shifts = company.getShifts();
        this.workers = new ArrayList<>(company.getWorkers());
        this.managers = new ArrayList<>(company.getManagers());
    }

    public void removeManager(String id) {
        if (managers == null) managers = new ArrayList<>();
        this.managers.remove(id);
    }

    public void removeWorker(String id) {
        if (managers == null) managers = new ArrayList<>();
        if (workers == null) workers = new ArrayList<>();

        this.workers.remove(id);
        this.managers.remove(id);
    }

    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> data = new HashMap<>();

        data.put(NAME_PROPERTY, name);
        data.put(MANAGERS_PROPERTY, managers);
        data.put(WORKERS_PROPERTY, workers);
        data.put(SHIFTS_PROPERTY, shifts);
        data.put(ID_PROPERTY, id);

        return data;
    }
}
