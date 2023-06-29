package com.softramen.vstack.mainScreen;

import android.util.Log;

import com.softramen.constants.Tags;

public class MainScreenThread extends Thread {

    private final MainScreenView mainBackgroundView;
    private long maxSleepTime = 1000 / 10;
    private boolean isRunning;

    public MainScreenThread( final MainScreenView mainBackgroundView ) {
        this.mainBackgroundView = mainBackgroundView;
    }

    @Override
    public void run() {
        long startTime, sleepTime;
        isRunning = true;
        while ( isRunning ) {
            startTime = System.currentTimeMillis();
            mainBackgroundView.updateAnimationLogic();
            mainBackgroundView.postInvalidate();
            sleepTime = maxSleepTime - ( System.currentTimeMillis() - startTime );

            try {
                if ( sleepTime > 0 ) {
                    sleep( sleepTime );
                } else {
                    sleep( 10 );
                }
            } catch ( final InterruptedException interruptedException ) {
                Log.e( Tags.ERROR , "Run : " + interruptedException );
            }
        }
    }

    public void stopThread() {
        this.isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setFPS( final int fps ) {
        maxSleepTime = 1000 / fps;
    }
}
