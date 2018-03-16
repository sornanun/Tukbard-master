package com.example.sornanun.tukbard;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.viewpagerindicator.CirclePageIndicator;

public class TutorialActivity extends AppCompatActivity {

    ViewPager viewPager;
    tutorialAdapter tutorialAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.ViewPager);
        tutorialAdapter = new tutorialAdapter(this);
        viewPager.setAdapter(tutorialAdapter);

        //Bind the title indicator to the adapter
        CirclePageIndicator cirtleIndicator = (CirclePageIndicator)findViewById(R.id.circles);
        cirtleIndicator.setViewPager(viewPager);
        cirtleIndicator.setRadius(15);
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
}
