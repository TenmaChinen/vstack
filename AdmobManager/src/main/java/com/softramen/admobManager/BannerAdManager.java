package com.softramen.admobManager;

import android.app.Activity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class BannerAdManager {
    private final String TAG = "BANNER_AD_MANAGER" ;

    public static int loadAdBanner( final Activity activity ) {
        final AdView adView = activity.findViewById( R.id.ad_view );
        final AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd( adRequest );

        final AdSize adSize = adView.getAdSize();
        if ( adSize != null ) {
            final int adHeightInPixels = adSize.getHeightInPixels( activity );
            adView.getLayoutParams().height = adHeightInPixels;
            return adHeightInPixels;
        }
        return 0;
    }
}
