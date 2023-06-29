package com.softramen.vstack.inGame.player;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.softramen.utils.animator.AnimatorListener;
import com.softramen.vstack.R;

public class PlayerAnimations {
    final Animator deadAnimator, winAnimator, portalShrinkAnimator, portalUnShrinkAnimator;
    private final ImageView imageView;
    private Callback callback;

    public interface Callback {
        void onWinAnimationEnd();
        void onDeadAnimationEnd();
        void onPortalShrinkAnimationEnd();
        void onPortalUnShrinkAnimationEnd();
    }

    public PlayerAnimations( final Context context ) {
        imageView = createImageView( context );

        deadAnimator = AnimatorInflater.loadAnimator( context , R.animator.animator_dead );
        winAnimator = AnimatorInflater.loadAnimator( context , R.animator.animator_win );
        portalShrinkAnimator = AnimatorInflater.loadAnimator( context , R.animator.animator_portal_shrink );
        portalUnShrinkAnimator = AnimatorInflater.loadAnimator( context , R.animator.animator_portal_un_shrink );

        deadAnimator.addListener( ( AnimatorListener ) this::onDedAnimationEnd );
        winAnimator.addListener( ( AnimatorListener ) this::onWinAnimationEnd );
        portalShrinkAnimator.addListener( ( AnimatorListener ) this::onPortalShrinkAnimationEnd );
        portalUnShrinkAnimator.addListener( ( AnimatorListener ) this::onPortalUnShrinkAnimationEnd );

        deadAnimator.setTarget( imageView );
        winAnimator.setTarget( imageView );
        portalShrinkAnimator.setTarget( imageView );
        portalUnShrinkAnimator.setTarget( imageView );
    }

    private void setPosition( final int x , final int y ) {
        imageView.setX( x );
        imageView.setY( y );
    }

    private void onWinAnimationEnd() {
        imageView.setVisibility( View.GONE );
        imageView.setScaleX( 1.0f );
        imageView.setScaleY( 1.0f );
        if ( callback != null ) callback.onWinAnimationEnd();
    }

    private void onDedAnimationEnd() {
        imageView.setVisibility( View.GONE );
        if ( callback != null ) callback.onDeadAnimationEnd();
    }

    private void onPortalShrinkAnimationEnd() {
        if ( callback != null ) callback.onPortalShrinkAnimationEnd();
    }

    private void onPortalUnShrinkAnimationEnd() {
        if ( callback != null ) callback.onPortalUnShrinkAnimationEnd();
        imageView.post( () -> imageView.setVisibility( View.GONE ) );
    }

    private ImageView createImageView( final Context context ) {
        final RelativeLayout.LayoutParams layoutParams;
        layoutParams = new RelativeLayout.LayoutParams( -2 , -2 );
        final ImageView imageView = new ImageView( context );
        imageView.setAdjustViewBounds( true );
        imageView.setLayoutParams( layoutParams );
        imageView.setVisibility( View.GONE );
        return imageView;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setBitmap( final Bitmap bitmap ) {
        imageView.setImageBitmap( bitmap );
    }

    public void startWinAnimation( final int x , final int y ) {
        setPosition( x , y );
        imageView.post( () -> {
            imageView.setVisibility( View.VISIBLE );
            winAnimator.start();
        } );
    }

    public void startDeadAnimation( final int x , final int y ) {
        setPosition( x , y );
        imageView.post( () -> {
            imageView.setVisibility( View.VISIBLE );
            deadAnimator.start();
        } );
    }

    public void startPortalShrinkAnimation( final int x , final int y ) {
        setPosition( x , y );
        imageView.post( () -> {
            imageView.setVisibility( View.VISIBLE );
            portalShrinkAnimator.start();
        } );
    }

    public void startPortalUnShrinkAnimation( final int x , final int y ) {
        setPosition( x , y );
        imageView.post( () -> {
            imageView.setVisibility( View.VISIBLE );
            portalUnShrinkAnimator.start();
        } );
    }


    public void setCallback( final Callback callback ) {
        this.callback = callback;
    }
}
