package com.example.sornanun.tukbard;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by SORNANUN on 5/6/2560.
 */

public class FirebaseController {

    public static ArrayList<Monk> monkArrayList = new ArrayList<Monk>();

    public ArrayList<Monk> getMonkArrayList() {
        return monkArrayList;
    }

    public interface FirebaseCallback {
        void firebaseReturnValue(ArrayList<Monk> monkList);
    }

    public interface FirebasePraDateCallback {
        void firebaseReturnValue(ArrayList<PraDate> praDateList);
    }

    private FirebaseCallback firebaseCallback;
    private FirebasePraDateCallback firebasePradateCallback;

    public FirebaseController(Activity activity, String activityName) {
        if (activityName.equals("map")) {
            firebaseCallback = (FirebaseCallback) activity;
        }
        else if (activityName.equals("calendar")) {
            firebasePradateCallback = (FirebasePraDateCallback) activity;
        }
    }

    public void getMonk() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("monkLocation");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        clearMonkArrayList();
                        addMonkToArrayList((Map<String, Object>) dataSnapshot.getValue());
                        firebaseCallback.firebaseReturnValue(monkArrayList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

    }

    private void addMonkToArrayList(Map<String, Object> monk) {
        for (Map.Entry<String, Object> entry : monk.entrySet()) {
            Map map = (Map) entry.getValue();
            String mLat = (String) map.get("monkLat");
            String mLong = (String) map.get("monkLong");
            String mAddress = (String) map.get("monkAddress");
            String mUpdate = (String) map.get("monkUpdateTime");
            String mUser = (String) map.get("monkUser");

            monkArrayList.add(new Monk(mLat, mLong, mAddress, mUpdate, mUser));
        }
        Log.i("Data", monkArrayList.toString());
    }

    private void clearMonkArrayList() {
        monkArrayList.clear();
    }

    public int getMonkArrayListSize() {
        return monkArrayList.size();
    }


    public static ArrayList<PraDate> praDateArrayList = new ArrayList<PraDate>();

    public void getPraDate() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("praDate");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        clearPraDateArrayList();
                        Log.d("Dataaa",dataSnapshot.getValue().toString());
                        addPraDateToArrayList((Map<String, Object>) dataSnapshot.getValue());
                        firebasePradateCallback.firebaseReturnValue(praDateArrayList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

    }

    private void addPraDateToArrayList(Map<String, Object> praDates) {

        for (Map.Entry<String, Object> entry : praDates.entrySet()) {
            Map map = (Map) entry.getValue();
            int date = Integer.valueOf(String.valueOf(map.get("date")));
            int month = Integer.valueOf(String.valueOf(map.get("month")));
            int year = Integer.valueOf(String.valueOf(map.get("year")));
            String detail = (String) map.get("detail");
            String type = (String) map.get("type");

            praDateArrayList.add(new PraDate(date, month, year, detail, type));
        }

        Log.i("SornanunCheck", praDateArrayList.toString());
    }

    private void clearPraDateArrayList() {
        praDateArrayList.clear();
    }


}
