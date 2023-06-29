package com.softramen.admobManager;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class RewardedAdManager {

    private final AdRequest adRequest;
    private final Activity activity;
    private Callback callback;
    private int reloadNum = 2;

    public RewardedAdManager( final Activity activity ) {
        this.activity = activity;
        adRequest = new AdRequest.Builder().build();
    }

    public void showRewardedAd() {
        loadRewardedAd();
    }

    private void loadRewardedAd() {
        RewardedAd.load( activity , activity.getString( R.string.rewarded_ad_unit_id ) , adRequest , rewardedAdLoadCallback );
    }


    public interface Callback {
        void onAdShowed(); // To Hide Progress Bar
        void onReward( final int bonusTotems );
        void onAdFailed(); // To Hide Progress Bar & Notify User About Error
        void onAdDismissed();
    }

    public void setCallback( final Callback callback ) {
        this.callback = callback;
    }

    private final RewardedAdLoadCallback rewardedAdLoadCallback = new RewardedAdLoadCallback() {
        @Override
        public void onAdLoaded( @NonNull final RewardedAd rewardedAd ) {
            super.onAdLoaded( rewardedAd );
            rewardedAd.setFullScreenContentCallback( fullScreenContentCallback );
            if ( activity != null ) {
                rewardedAd.show( activity , rewardItem -> callback.onReward( rewardItem.getAmount() ) );
            }
        }

        @Override
        public void onAdFailedToLoad( @NonNull final LoadAdError loadAdError ) {
            super.onAdFailedToLoad( loadAdError );
            switch ( loadAdError.getCode() ) {
                case AdRequest.ERROR_CODE_NETWORK_ERROR:
                case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                case AdRequest.ERROR_CODE_NO_FILL:
                    if ( reloadNum == 0 && callback != null ) callback.onAdFailed();
                    else {
                        reloadNum--;
                        loadRewardedAd();
                    }
                    break;
                default:
                    if ( callback != null ) callback.onAdFailed();
            }
        }
    };

    private final FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
        @Override
        public void onAdShowedFullScreenContent() {
            if ( callback != null ) callback.onAdShowed();
        }

        @Override
        public void onAdDismissedFullScreenContent() {
            if ( callback != null ) callback.onAdDismissed();
        }

        @Override
        public void onAdFailedToShowFullScreenContent( @NonNull final AdError adError ) {
            // final int code = adError.getCode();
            // adError.getMessage()
            if ( callback != null ) callback.onAdFailed();
        }
    };
}
