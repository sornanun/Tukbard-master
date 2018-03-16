package com.example.sornanun.tukbard;

import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by sornanun on 23/1/2559.
 */
public class Preferences extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.preference);

        setContentView(R.layout.toolbar_preference);

        Toolbar actionbar = (Toolbar) findViewById(R.id.actionbar);
        actionbar.setTitle("ตั้งค่า");
        actionbar.setTitleTextColor(Color.WHITE);
        actionbar.setNavigationIcon(getResources().getDrawable(R.drawable.abc_ic_ab_back_material));
        actionbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
            }
        });
    }

}
