package com.softramen.dialogs;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.softramen.constants.Dialogs;
import com.softramen.constants.TextSize;
import com.softramen.dialogs.utils.DialogBase;
import com.softramen.utils.animator.AnimatorListener;

public class DialogAnnounce extends DialogBase {

    public static final String REQUEST_CODE = "DIALOG_ANNOUNCE";
    public static final String ARG_MESSAGE = "MESSAGE";

    private int callbackId = Dialogs.ON_CANCEL;
    private AnimatorSet announceAnimatorSet;
    private TextView tvAnnounce;
    private String message;

    @Override
    public void onCreate( @Nullable final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        final Bundle args = getArguments();
        if ( args != null ) {
            message = args.getString( ARG_MESSAGE );
        }
    }

    @Nullable
    @Override
    public View onCreateView( @NonNull final LayoutInflater inflater , @Nullable final ViewGroup container , @Nullable final Bundle savedInstanceState ) {
        final View inflatedView = inflater.inflate( R.layout.dialog_announce , container , false );
        tvAnnounce = inflatedView.findViewById( R.id.tv_announce );
        tvAnnounce.setTextSize( TypedValue.COMPLEX_UNIT_PX , TextSize.ANNOUNCE );
        tvAnnounce.setText( message );

        inflatedView.setOnClickListener( v -> {
            tvAnnounce.setVisibility( View.GONE );
            callbackId = Dialogs.ON_FINISH_ANNOUNCE;
            startDismissAnimation();
        } );

        final int windowHeight = getWindowHeight();
        final int posA = ( int ) ( windowHeight * 0.45f );
        final int posB = ( int ) ( windowHeight * 0.50f );

        final ObjectAnimator partA = ObjectAnimator.ofFloat( tvAnnounce , View.ALPHA , 0 , 1 ).setDuration( 400 );
        final ObjectAnimator partB = ObjectAnimator.ofFloat( tvAnnounce , View.ALPHA , 1 , 0 ).setDuration( 400 );
        partB.setStartDelay( 1200 );

        final AnimatorSet alphaAnimation = new AnimatorSet();
        alphaAnimation.playSequentially( partA , partB );

        announceAnimatorSet = new AnimatorSet();
        announceAnimatorSet.setInterpolator( new AccelerateDecelerateInterpolator() );
        final ObjectAnimator objectAnimator = ObjectAnimator.ofFloat( tvAnnounce , "translationY" , posA , posB ).setDuration( 2000 );
        announceAnimatorSet.playTogether( alphaAnimation , objectAnimator );

        announceAnimatorSet.addListener( ( AnimatorListener ) () -> {
            tvAnnounce.setVisibility( View.GONE );
            callbackId = Dialogs.ON_FINISH_ANNOUNCE;
            startDismissAnimation();
        } );


        announceAnimatorSet.setTarget( tvAnnounce );
        return inflatedView;
    }


    @Override
    public void onStart() {
        super.onStart();
        // super.performClick(); // AUTOMATE
    }

    @Override
    public void onWindowEnterAnimationEnd() {
        tvAnnounce.setVisibility( View.VISIBLE );
        announceAnimatorSet.start();
    }

    @Override
    public void onDismiss( @NonNull final DialogInterface dialog ) {
        super.sendResults( REQUEST_CODE , callbackId , null );
        super.onDismiss( dialog );
    }

    @Override
    public void startDismissAnimation() {
        announceAnimatorSet.removeAllListeners();
        tvAnnounce.clearAnimation();
        super.startDismissAnimation();
    }

    public void show( @NonNull final FragmentManager manager ) {
        super.show( manager , "DialogAnnounce" );
    }
}