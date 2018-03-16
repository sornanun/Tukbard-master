package com.example.sornanun.tukbard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

public class HomeActivity extends AppCompatActivity {

    RelativeLayout openMap;
    RelativeLayout openCalendar;
    RelativeLayout openTutorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        openMap = (RelativeLayout) findViewById(R.id.openMap);
        openMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, MapsActivity.class));
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        openCalendar = (RelativeLayout) findViewById(R.id.openCalendar);
        openCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, CalendarActivity.class));
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });

        openTutorial = (RelativeLayout) findViewById(R.id.openTutorial);
        openTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, TutorialActivity.class));
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_logo);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(HomeActivity.this, Preferences.class));
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
