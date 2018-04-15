package com.crisanto.kevin.picstant;

import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class LoginActivity extends AppCompatActivity {

    LinearLayout mLoginContainer;
    AnimationDrawable mAnimationDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Configure animation
        mLoginContainer = (LinearLayout) findViewById(R.id.login_container);
        mAnimationDrawable = (AnimationDrawable) mLoginContainer.getBackground();
        mAnimationDrawable.setEnterFadeDuration(5000);
        mAnimationDrawable.setExitFadeDuration(2000);

    }

    // Start animation


    @Override
    protected void onResume() {
        super.onResume();

        if(mAnimationDrawable != null && !mAnimationDrawable.isRunning()) {
            mAnimationDrawable.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mAnimationDrawable != null && !mAnimationDrawable.isRunning()) {
            mAnimationDrawable.stop();
        }
    }
}
