package afeka.katz.arkadiy.easyhr.data;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import afeka.katz.arkadiy.easyhr.model.Company;
import afeka.katz.arkadiy.easyhr.model.Shift;
import afeka.katz.arkadiy.easyhr.model.User;

/**
 * Created by arkokat on 11/4/2017.
 */

public class ShiftsDatabase {
    private static final String APPROVED_REF = "approved_shifts";
    private static final String PENDING_REF = "pending_shifts";

    private static ShiftsDatabase instance = new ShiftsDatabase();

    public static ShiftsDatabase getInstance() {
        return instance;
    }

    private DatabaseReference getRef(boolean nextWeek, Company company, String name) {
        DatabaseReference pendingReference =
                DatabaseConst.CONNECTION.getReference(name);

        GregorianCalendar calendar = new GregorianCalendar();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        int currentYear = calendar.get(Calendar.YEAR);
        int totalWeeks = calendar.getWeeksInWeekYear();

        if (nextWeek) {
            if (totalWeeks == currentWeek) {
                currentWeek = 1;
                currentYear += 1;
            } else {
                currentWeek++;
            }
        }

        return pendingReference.child(company.getId()).child(Integer.toString(currentYear)).
                child(Integer.toString(currentWeek));
    }

    public void addApprovedShift(User user, Company company, Shift shift) {
        getRef(true, company, APPROVED_REF).child(shift.getId()).updateChildren(
                Collections.singletonMap(user.getId(), true));
    }

    public void addPendingShift(User user, Company company, Shift shift) {
        getRef(true, company, PENDING_REF).child(shift.getId()).updateChildren(
                Collections.singletonMap(user.getId(), true));
    }

    public CompletableFuture<Map<String, List<Shift>>> getShiftsForNextWeek(Company company) {
        CompletableFuture<Map<String, List<Shift>>> result = new CompletableFuture<>();

        getRef(true, company, APPROVED_REF).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                result.complete(processShifts(dataSnapshot, company));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.complete(new HashMap<>());
            }
        });

        return result;

    }

    public CompletableFuture<Map<String, List<Shift>>> getShiftsForCurrentWeek(Company company) {
        CompletableFuture<Map<String, List<Shift>>> result = new CompletableFuture<>();

        getRef(false, company, APPROVED_REF).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                result.complete(processShifts(dataSnapshot, company));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.complete(new HashMap<>());
            }
        });

        return result;

    }

    private Map<String, List<Shift>> processShifts(DataSnapshot dataSnapshot, Company company) {
        if (!dataSnapshot.exists()) {
            return new HashMap<>();
        }

        Map<String, Shift> shiftToIdMapping = company.getShifts().stream().collect(Collectors.toMap(x -> (x.getId()), x -> x));
        Map<String, List<Shift>> shifts = new HashMap<>();

        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
            String shiftId = snapshot.getKey();

            for (DataSnapshot user: snapshot.getChildren()) {
                shifts.putIfAbsent(user.getKey(), new ArrayList<>());

                if (shiftToIdMapping.containsKey(shiftId)) {
                    shifts.get(user.getKey()).add(shiftToIdMapping.get(shiftId));
                }
            }
        }

        return shifts;
    }

    public CompletableFuture<Map<String, List<Shift>>> getPendingShifts(Company company) {
        CompletableFuture<Map<String, List<Shift>>> result = new CompletableFuture<>();

        getRef(true, company, PENDING_REF).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                result.complete(processShifts(dataSnapshot, company));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                result.complete(new HashMap<>());
            }
        });

        return result;
    }
}
