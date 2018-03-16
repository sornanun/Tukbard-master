package com.example.sornanun.tukbard;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tyczj.extendedcalendarview.CalendarProvider;
import com.tyczj.extendedcalendarview.Day;
import com.tyczj.extendedcalendarview.Event;
import com.tyczj.extendedcalendarview.ExtendedCalendarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;

public class CalendarActivity extends AppCompatActivity implements FirebaseController.FirebasePraDateCallback {

    ExtendedCalendarView extendedCalendarView;
    RelativeLayout event_detail;
    RelativeLayout event_click_box;
    TextView textDate;
    TextView textType;
    TextView textDescription;
    TextView eventClick;
    AlertDialog dialog;
    FirebaseController firebaseController;
    ArrayList<PraDate> praDateArrayList = new ArrayList<PraDate>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        this.extendedCalendarView = (ExtendedCalendarView) findViewById(R.id.extendedCalendarView_addLocationSiteCalendar_CALENDAR);
        this.extendedCalendarView.setGesture(ExtendedCalendarView.LEFT_RIGHT_GESTURE);

        event_detail = (RelativeLayout) findViewById(R.id.event_detail);
        event_click_box = (RelativeLayout) findViewById(R.id.event_click_box);

        textDate = (TextView) findViewById(R.id.txDate);
        textType = (TextView) findViewById(R.id.txType);
        textDescription = (TextView) findViewById(R.id.txDetail);

        eventClick = (TextView) findViewById(R.id.event_click);

        firebaseController = new FirebaseController(this,"calendar");

        extendedCalendarView.setGesture(1);
        extendedCalendarView.setOnDayClickListener(new ExtendedCalendarView.OnDayClickListener() {
            @Override
            public void onDayClicked(AdapterView<?> adapter, View view, int position, long id, Day day) {
                if (!day.getEvents().isEmpty()) {
                    event_click_box.setVisibility(View.INVISIBLE);
                    event_detail.setVisibility(View.VISIBLE);
                    for (Event e : day.getEvents()) {
                        textDate.setText(e.getStartDate("dd MMMM yyyy"));
                        textType.setText(e.getTitle());
                        textDescription.setText(e.getDescription());
                        //Toast.makeText(getBaseContext(), e.getStartDate("dd/MMMM/yy ") + e.getTitle() +" "+e.getDescription() , Toast.LENGTH_LONG).show();
                    }
                } else {
                    event_click_box.setVisibility(View.VISIBLE);
                    event_detail.setVisibility(View.INVISIBLE);
                    eventClick.setText(R.string.event_click_not_found);
                }

            }

        });

        if (isInternetConnected() == true) {
            dialog = new SpotsDialog(CalendarActivity.this, "กรุณารอสักครู่...");
            dialog.show();
            getContentResolver().delete(CalendarProvider.CONTENT_URI, null, null); // remove old table
            firebaseController.getPraDate();
        }
    }

    private boolean isInternetConnected() {
        if (isNetworkAvailable(this.getApplicationContext())) {
            return true;
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("ตรวจสอบการเชื่อมต่อ")
                    .setMessage("แอพพลิเคชั่นต้องการเชื่อมต่ออินเทอร์เน็ต กรุณาตรวจสอบการเชื่อมต่ออินเทอร์เน็ตเพื่อดึงข้อมูลวันพระ")
                    .setPositiveButton("รับทราบ", null).show();
        }
        return false;
    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public void addPraDateEvent(int date, int month, int year, String detail, String type) {
        // Adding events
        ContentValues values = new ContentValues();
        values.put(CalendarProvider.COLOR, Event.COLOR_RED);
        values.put(CalendarProvider.DESCRIPTION, detail);
        //values.put(CalendarProvider.LOCATION, "Sample Location");
        values.put(CalendarProvider.EVENT, type);

        Calendar cal = Calendar.getInstance();
        TimeZone tz = TimeZone.getDefault();

        month = month - 1;
        if (month < 0) month = 12;
        cal.set(year, month, date, 6, 0);
        int julianDay = Time.getJulianDay(cal.getTimeInMillis(), TimeUnit.MILLISECONDS.toSeconds(tz.getOffset(cal.getTimeInMillis())));

        values.put(CalendarProvider.START, cal.getTimeInMillis());
        values.put(CalendarProvider.START_DAY, julianDay);

        cal.set(year, month, date, 8, 0);
        int endDayJulian = Time.getJulianDay(cal.getTimeInMillis(), TimeUnit.MILLISECONDS.toSeconds(tz.getOffset(cal.getTimeInMillis())));

        values.put(CalendarProvider.END, cal.getTimeInMillis());
        values.put(CalendarProvider.END_DAY, endDayJulian);

        // store value to sqlite database
        Uri uri = getContentResolver().insert(CalendarProvider.CONTENT_URI, values);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    @Override
    public void firebaseReturnValue(ArrayList<PraDate> praDateList) {
        this.praDateArrayList = praDateList;
        for (PraDate praDate : praDateList) {
            int date = praDate.getDate();
            int month = praDate.getMonth();
            int year = praDate.getYear();
            String detail = praDate.getDetail();
            String type = praDate.getType();
            addPraDateEvent(date, month, year, detail, type);
        }
        dialog.dismiss();
        extendedCalendarView.refreshCalendar();
    }
}
