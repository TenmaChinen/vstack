package com.softramen.dialogs.utils;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.softramen.constants.Dialogs;
import com.softramen.constants.Screen;
import com.softramen.dialogs.R;
import com.softramen.utils.animator.AnimatorListener;

public class DialogBase extends DialogFragment {
    private int windowHeight = WindowManager.LayoutParams.MATCH_PARENT;
    private Animator windowEnterAnimator, windowExitAnimator;
    private boolean onStartExecuted = false;
    private boolean dismissible =true;
    private View inflatedView;

    @Override
    public void onCreate( @Nullable final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        setStyle( STYLE_NO_FRAME , R.style.DialogBaseStyle );

        final Context context = getContext();
        windowEnterAnimator = AnimatorInflater.loadAnimator( context , R.animator.animator_fade_in_dialog );
        windowExitAnimator = AnimatorInflater.loadAnimator( context , R.animator.animator_fade_out_dialog );

        final Bundle args = getArguments();
        if ( args != null ) {
            final int clipHeight = args.getInt( Dialogs.ARG_CLIP_HEIGHT , -1 );
            if ( clipHeight != -1 ) {
                windowHeight = Screen.HEIGHT - clipHeight;
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog( @Nullable final Bundle savedInstanceState ) {
        final Dialog dialog = super.onCreateDialog( savedInstanceState );
        dialog.setCancelable( false );
        dialog.setCanceledOnTouchOutside( false );

        final Window window = dialog.getWindow();
        window.setWindowAnimations( R.style.DialogNullWindowAnimationStyle );
        window.setFlags( WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE , WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE );
        window.setGravity( Gravity.BOTTOM );
        return dialog;
    }

    @Override
    public void onViewCreated( @NonNull final View view , @Nullable final Bundle savedInstanceState ) {
        super.onViewCreated( view , savedInstanceState );
        this.inflatedView = view;

        windowEnterAnimator.setTarget( inflatedView );
        windowExitAnimator.setTarget( inflatedView );

        windowEnterAnimator.addListener( ( AnimatorListener ) this::onWindowEnterAnimationEnd );
        windowExitAnimator.addListener( ( AnimatorListener ) () -> inflatedView.post( this::dismiss ) );
    }

    @Override
    public void onStart() {
        super.onStart();
        if ( onStartExecuted ) return;
        onStartExecuted = true;

        final Dialog dialog = getDialog();
        if ( dialog != null ) {
            final Window window = dialog.getWindow();
            window.setLayout( WindowManager.LayoutParams.MATCH_PARENT , windowHeight );
        }
        windowEnterAnimator.start();
        onWindowEnterAnimationStart();
    }

    /* This method is not used because of FLAG_NOT_FOCUSABLE */
    @Override
    public void onCancel( @NonNull final DialogInterface dialog ) {
        // super.onCancel( dialog );
    }

    public boolean isOnStartExecuted() {
        return onStartExecuted;
    }

    public void startDismissAnimation() {
        windowExitAnimator.start();
    }

    public void performClick() {
        if ( inflatedView != null ) inflatedView.performClick();
    }

    public void onWindowEnterAnimationStart() {
    }

    public void onWindowEnterAnimationEnd() {
    }

    public void sendResults( final String requestCode , final int callbackId , Bundle results ) {
        final FragmentActivity fragmentActivity = getActivity();
        if ( fragmentActivity != null ) {
            results = ( results != null ) ? results : new Bundle();
            results.putInt( Dialogs.METHOD_CODE , callbackId );
            final FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
            fragmentManager.setFragmentResult( requestCode , results );
        }
    }

    public void setDismissible(final boolean dismissible){
        this.dismissible=dismissible;
    }

    public boolean isDismissible(){
        return dismissible;
    }

    public int getWindowHeight(){
        return windowHeight;
    }
}
