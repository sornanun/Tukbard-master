package com.example.sornanun.tukbard;

import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class SplashScreen extends AppCompatActivity {

    private boolean mIsBackButtonPressed;
    private static final int SPLASH_DURATION = 3000; // 3 seconds

    ImageView logoImage;
    TextView textName;
    TextView textSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        logoImage = (ImageView) findViewById(R.id.logoImage);
        textName = (TextView) findViewById(R.id.txName);
        textSlogan = (TextView) findViewById(R.id.txSlogan);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

//        logoImage.setVisibility(View.INVISIBLE);
//        textName.setVisibility(View.INVISIBLE);
//        textSlogan.setVisibility(View.INVISIBLE);

//        logoImage.setAlpha(0.0f);
//        textName.setAlpha(0.0f);
//        textSlogan.setAlpha(0.0f);

        textName.setTranslationY(textName.getHeight());

        YoYo.with(Techniques.BounceInDown)
                .duration(500)
                .delay(100)
                .playOn(findViewById(R.id.logoImage));

        YoYo.with(Techniques.FadeInUp)
                .duration(500)
                .delay(1000)
                .playOn(findViewById(R.id.txName));

        YoYo.with(Techniques.FadeInUp)
                .duration(800)
                .delay(1300)
                .playOn(findViewById(R.id.txSlogan));

        Handler handler = new Handler();

        // run a thread after 3 seconds to start the home screen
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {

                // make sure we close the splash screen so the user won't come back when it presses back key
                finish();

                if (!mIsBackButtonPressed) {
                    // start the home screen if the back button wasn't pressed already
                    Intent intent = new Intent(SplashScreen.this, HomeActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }

            }
        }, SPLASH_DURATION); // time in milliseconds (1 second = 1000 milliseconds) until the run() method will be called
    }
}
