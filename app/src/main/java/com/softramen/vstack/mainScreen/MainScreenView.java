package com.softramen.vstack.mainScreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.widget.FrameLayout;

import com.softramen.settingsManager.SettingsPreferences;
import com.softramen.vstack.GameData;

import java.util.Arrays;

public class MainScreenView extends View {

    private final MainScreenBackground mainScreenBackground;
    private MainScreenThread animationThread;
    private boolean enabled = SettingsPreferences.getInstance().getScreenAnimationState();

    public MainScreenView( final Context context ) {
        super( context );
        setLayoutParams( new FrameLayout.LayoutParams( -1 , -1 ) );
        final Bitmap[] bitmapArray = Arrays.copyOfRange( GameData.getInstance().tileBitmapArray , 0 , 2 );
        mainScreenBackground = new MainScreenBackground( bitmapArray[ 0 ] , bitmapArray[ 1 ] );
    }

    @Override
    public void onDraw( final Canvas canvas ) {
        super.onDraw( canvas );
        mainScreenBackground.draw( canvas );
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimation();
    }

    public void updateAnimationLogic() {
        mainScreenBackground.updateAnimation();
    }

    public void stopAnimation() {
        if ( animationThread != null && animationThread.isRunning() ) animationThread.stopThread();
    }

    public void restartAnimation() {
        resetThread();
    }

    public void setAnimationState( final boolean enabled ) {
        this.enabled = enabled;
        if ( enabled ) resetThread();
        else {
            stopAnimation();
            post( () -> {
                mainScreenBackground.reset();
                invalidate();
            } );
        }
    }

    private void resetThread() {
        if ( !enabled ) return;
        if ( animationThread != null && animationThread.isRunning() ) animationThread.stopThread();

        animationThread = new MainScreenThread( this );
        mainScreenBackground.setMainScreenThread( animationThread );
        animationThread.start();
    }
}
