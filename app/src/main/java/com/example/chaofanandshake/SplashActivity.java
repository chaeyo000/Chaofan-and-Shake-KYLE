package com.example.chaofanandshake;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        MotionLayout motionLayout = findViewById(R.id.motionLayout);
        motionLayout.transitionToEnd();

        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, 1600);
        ImageView logo = findViewById(R.id.logo);
        ObjectAnimator animator = ObjectAnimator.ofFloat(logo, "rotation", 0f, 360f);
        animator.setDuration(1500);
        animator.start();

    }

}
