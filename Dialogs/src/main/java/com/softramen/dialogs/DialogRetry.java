package com.softramen.dialogs;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.softramen.admobManager.RewardedAdManager;
import com.softramen.constants.Dialogs;
import com.softramen.constants.TextSize;
import com.softramen.dialogs.utils.DialogBase;
import com.softramen.dialogs.utils.RadialGradientDrawable;
import com.softramen.introFactory.IntroFactory;
import com.softramen.introFactory.IntroGroupId;
import com.softramen.introFactory.IntroManager;
import com.softramen.sounds.Sounds;
import com.softramen.utils.animator.AnimatorListener;

public class DialogRetry extends DialogBase {

    public static final String REQUEST_CODE = "DIALOG_RETRY";
    public static final String ARG_REMAINING_TOTEMS = "REMAINING_TOTEMS", ARG_DIFFICULTY= "DIFFICULTY";
    public static final String RESULT_REMAINING_TOTEMS = "RESULT_REMAINING_TOTEMS";

    private final Sounds sounds = Sounds.getInstance();
    private final Bundle results = new Bundle();

    private int remainingTotems, difficulty, callbackId = Dialogs.ON_CANCEL;

    private ImageView btnRetry;
    private Animator popFadeInAnimation;
    private Animation fadeInAnimation;
    private TextView tvTotemsCounter;

    @Override
    public void onCreate( @Nullable final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        popFadeInAnimation = AnimatorInflater.loadAnimator( getContext() , R.animator.animator_pop_fade_in );
        fadeInAnimation = AnimationUtils.loadAnimation( getContext() , android.R.anim.fade_in );

        final Bundle args = getArguments();
        if ( args != null ) {
            remainingTotems = args.getInt( ARG_REMAINING_TOTEMS );
            difficulty = args.getInt( ARG_DIFFICULTY );
        }
    }

    @Nullable
    @Override
    public View onCreateView( @NonNull final LayoutInflater inflater , @Nullable final ViewGroup container , @Nullable final Bundle savedInstanceState ) {
        final View inflatedView = inflater.inflate( R.layout.dialog_retry , container , false );
        final ImageView btnLevelSelector = inflatedView.findViewById( R.id.btn_level_selector );
        final TextView tvTryAgain = inflatedView.findViewById( R.id.tv_try_again );
        tvTotemsCounter = inflatedView.findViewById( R.id.tv_totems_counter );
        final ImageView btnRestart = inflatedView.findViewById( R.id.btn_restart );
        btnRetry = inflatedView.findViewById( R.id.btn_retry );

        final ViewTreeObserver viewTreeObserver = btnRetry.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener( new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final float radius = btnRetry.getMeasuredWidth() / 2.0f;
                final RadialGradientDrawable radialGradientDrawable = new RadialGradientDrawable( radius);
                btnRetry.setBackground( radialGradientDrawable );
                btnRetry.getViewTreeObserver().removeOnGlobalLayoutListener( this );
            }
        } );

        tvTryAgain.setTextSize( TypedValue.COMPLEX_UNIT_PX , TextSize.DialogRetry.TRY_AGAIN );
        tvTotemsCounter.setTextSize( TypedValue.COMPLEX_UNIT_PX , TextSize.DialogRetry.TOTEMS_COUNTER );

        tvTotemsCounter.setText( getString( R.string.remaining_totems , remainingTotems ) );


        final View.OnClickListener clickListener = view -> {
            btnRestart.setOnClickListener( null );
            btnRetry.setOnClickListener( null );
            btnLevelSelector.setOnClickListener( null );

            final int viewId = view.getId();
            sounds.playPress();

            if ( viewId == R.id.btn_restart ) callbackId = Dialogs.ON_CLICK_RESTART;
            else if ( viewId == R.id.btn_level_selector ) callbackId = Dialogs.ON_CLICK_LEVEL_SELECTOR;
            else if ( viewId == R.id.btn_retry ) {
                remainingTotems--;
                callbackId = Dialogs.ON_CLICK_RETRY;
            }
            startDismissAnimation();
        };

        final IntroManager introManager = IntroManager.getInstance();

        if ( remainingTotems == 0 ) {
            btnRetry.setColorFilter( Dialogs.disabledTint , PorterDuff.Mode.MULTIPLY );
            btnRetry.setOnClickListener( view -> sounds.playError() );

            final TextView tvBtnWatchVideo = inflatedView.findViewById( R.id.tv_btn_watch_video );
            final int rewardTotemsPerVideo = 3 * ( difficulty + 1 );
            tvBtnWatchVideo.setText( getString( R.string.watch_video, rewardTotemsPerVideo) );
            tvBtnWatchVideo.setTextSize( TypedValue.COMPLEX_UNIT_PX , TextSize.DialogRetry.WATCH_REWARDED_AD );
            tvBtnWatchVideo.setVisibility( View.VISIBLE );

            tvBtnWatchVideo.setOnClickListener( v -> {
                super.setDismissible( false );
                btnLevelSelector.setOnClickListener( null );
                tvBtnWatchVideo.setOnClickListener( null );
                btnRestart.setOnClickListener( null );
                btnRetry.setOnClickListener( null );

                tvBtnWatchVideo.setText( getString( R.string.loading_video_message ) );
                sounds.playRePop();

                final FragmentActivity fragmentActivity = getActivity();
                final RewardedAdManager rewardedAdManager = new RewardedAdManager( fragmentActivity );
                rewardedAdManager.setCallback( new RewardedAdManager.Callback() {
                    @Override
                    public void onAdShowed() {
                        tvBtnWatchVideo.setVisibility( View.GONE );
                    }

                    @Override
                    public void onReward( final int bonusTotems ) {
                        // TODO : Use the parameter in production mode
                        // remainingTotems = bonusTotems + ( difficulty + 1 );
                        remainingTotems = rewardTotemsPerVideo;
                    }

                    @Override
                    public void onAdDismissed() {
                        btnLevelSelector.setOnClickListener( clickListener );
                        btnRestart.setOnClickListener( clickListener );

                        final Animator animatorPop = AnimatorInflater.loadAnimator( getContext() , R.animator.animator_reward_pop );
                        animatorPop.setTarget( tvTotemsCounter );
                        tvTotemsCounter.postDelayed( () -> {
                            animatorPop.start();
                            tvTotemsCounter.setText( getString( R.string.remaining_totems , remainingTotems ) );
                            btnRetry.setOnClickListener( clickListener );
                            btnRetry.clearColorFilter();
                            sounds.playRePop();
                            setDismissible( true );
                        } , 600 );
                    }

                    @Override
                    public void onAdFailed() {
                        btnLevelSelector.setOnClickListener( clickListener );
                        btnRestart.setOnClickListener( clickListener );
                        btnRetry.setOnClickListener( view -> sounds.playError() );
                        tvBtnWatchVideo.setText( getString( R.string.loading_failed_message ) );
                        setDismissible( true );
                    }
                } );
                rewardedAdManager.showRewardedAd();
            } );

            // I N T R O   ( Reward )
            if ( !introManager.isGroupDisplayed( IntroGroupId.DIALOG_RETRY_REWARD ) ) {
                final IntroFactory introFactory = new IntroFactory( getActivity() , getDialog() );
                introFactory.showDialogRetryRewardIntro( tvBtnWatchVideo );
            }
        } else {
            btnRetry.setOnClickListener( clickListener );

            // I N T R O ( Restart - Retry )
            if ( !introManager.isGroupDisplayed( IntroGroupId.DIALOG_RETRY ) ) {
                final IntroFactory introFactory = new IntroFactory( getActivity() , getDialog() );
                introFactory.showDialogRetryIntro( btnRestart , btnRetry );
            }
        }

        btnLevelSelector.setOnClickListener( clickListener );
        btnRestart.setOnClickListener( clickListener );

        popFadeInAnimation.setTarget( btnRetry );
        popFadeInAnimation.addListener( ( AnimatorListener ) () ->

        {
            tvTotemsCounter.setVisibility( View.VISIBLE );
            tvTotemsCounter.startAnimation( fadeInAnimation );
        } );

        return inflatedView;
    }

    @Override
    public void onWindowEnterAnimationStart() {
        Sounds.getInstance().playAppear();
    }

    @Override
    public void onWindowEnterAnimationEnd() {
        btnRetry.setVisibility( View.VISIBLE );
        popFadeInAnimation.start();
    }

    @Override
    public void onDismiss( @NonNull final DialogInterface dialog ) {
        results.putInt( RESULT_REMAINING_TOTEMS , remainingTotems );
        super.sendResults( REQUEST_CODE , callbackId , results );
        super.onDismiss( dialog );
    }

    public void show( @NonNull final FragmentManager manager ) {
        super.show( manager , "DialogRetry" );
    }
}