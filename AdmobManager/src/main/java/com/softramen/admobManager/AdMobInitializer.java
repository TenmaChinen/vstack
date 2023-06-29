package com.softramen.admobManager;

import android.content.Context;

import com.google.android.gms.ads.MobileAds;

public class AdMobInitializer {
    public static void init( final Context context ) {
        MobileAds.initialize( context , initializationStatus -> {
        } );
    }
}
