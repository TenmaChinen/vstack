package com.softramen.vstack.activities;

import android.os.Bundle;

import com.softramen.splashActivity.SplashActivity;

public class BrandActivity extends SplashActivity {

    @Override
    protected void onCreate( final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setActivityToLaunch( MainScreenActivity.class );
    }
}