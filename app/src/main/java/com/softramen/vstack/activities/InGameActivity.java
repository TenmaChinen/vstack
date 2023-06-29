package com.softramen.vstack.activities;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.LifecycleOwner;

import com.softramen.admobManager.BannerAdManager;
import com.softramen.constants.Dialogs;
import com.softramen.constants.InGame;
import com.softramen.dialogs.DialogAnnounce;
import com.softramen.dialogs.DialogRetry;
import com.softramen.dialogs.DialogWinner;
import com.softramen.dialogs.utils.DialogBase;
import com.softramen.introFactory.IntroFactory;
import com.softramen.introFactory.IntroGroupId;
import com.softramen.introFactory.IntroManager;
import com.softramen.sounds.Sounds;
import com.softramen.utils.animator.AnimatorListener;
import com.softramen.vstack.GameData;
import com.softramen.vstack.GameTimer;
import com.softramen.vstack.R;
import com.softramen.vstack.inGame.GameViewManager;
import com.softramen.vstack.inGame.player.Player;
import com.softramen.vstack.inGame.views.PlayerView;
import com.softramen.vstack.utils.LevelData;
import com.softramen.vstack.utils.UpdateManager;

public class InGameActivity extends AppCompatActivity {
    public final static String WORLD = "WORLD", LEVEL = "LEVEL";

    private final GameData gameData = GameData.getInstance();

    private FragmentManager fragmentManager;
    private GameViewManager gameViewManager;
    private DialogBase dialogBase = null;
    private int adBannerHeight = 0;
    private Activity activity;
    private Context context;

    @Override
    protected void onCreate( final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_in_game );

        context = this;
        activity = this;
        final LifecycleOwner lifecycleOwner = this;
        fragmentManager = getSupportFragmentManager();
        fragmentManager.setFragmentResultListener( DialogAnnounce.REQUEST_CODE , lifecycleOwner , fragmentResultListener );
        fragmentManager.setFragmentResultListener( DialogWinner.REQUEST_CODE , lifecycleOwner , fragmentResultListener );
        fragmentManager.setFragmentResultListener( DialogRetry.REQUEST_CODE , lifecycleOwner , fragmentResultListener );

        adBannerHeight = BannerAdManager.loadAdBanner( activity );

        loadWorldAndLevel();

        final FrameLayout frameLayout = findViewById( R.id.frame_layout );
        gameViewManager = new GameViewManager( context );
        gameViewManager.setCallback( gameViewManagerCallback );
        gameViewManager.loadLevel();
        gameViewManager.addViews( frameLayout );

        showLevelNameDialog();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameViewManager.pauseGame();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameViewManager.resumeGame();
    }

    @Override
    public void onBackPressed() {
        if ( dialogBase == null ) {
            Sounds.getInstance().playCancel();
            finishActivity();
        } else if ( dialogBase.isDismissible() ) dialogBase.dismiss();
    }

    private void loadWorldAndLevel() {
        final Intent intent = getIntent();
        final int worldIdx = intent.getIntExtra( WORLD , 3 );
        final int levelIdx = intent.getIntExtra( LEVEL , 0 );
        gameData.setWorldByIdx( worldIdx );
        gameData.setLevelByIdx( levelIdx );
    }

    private final GameViewManager.Callback gameViewManagerCallback = new GameViewManager.Callback() {
        @Override
        public void onPlayerDead() {
            showDialogRetry();
        }

        @Override
        public void onPlayerWins() {
            final UpdateManager updateManager = UpdateManager.getInstance();
            boolean updatePlayerProgress = false;
            boolean isWorldBeatReward = false;
            final String message;

            if ( gameData.isLastLevel() ) {
                if ( !gameData.isWorldBeaten() ) isWorldBeatReward = true;
                if ( gameData.isLastWorld() || gameData.isWorldBeaten() ) {
                    message = getString( R.string.world_clear , gameData.getWorldName() );
                } else {
                    final int nextWorldIdx = gameData.getNextWorldIdx();
                    message = getString( R.string.world_unlocked , gameData.getWorldName( nextWorldIdx ) );
                    updateManager.addPendingItem( nextWorldIdx , 0 );
                    updatePlayerProgress = true;
                }

            } else if ( gameData.isLevelBeaten() ) {
                message = getString( R.string.level_clear , gameData.getLevelNum() );
            } else {
                message = getString( R.string.next_level_unlocked , gameData.getNextLevelNum() );
                updateManager.addPendingItem( gameData.getWorldIdx() , gameData.getNextLevelIdx() );
                updatePlayerProgress = true;
            }

            final int difficulty = gameData.getDifficulty();
            final LevelData.TimeRecords timeRecordArray = gameData.getLevelData().getTimeRecords();
            final int lastTimeRecord = timeRecordArray.getTimeRecord( difficulty );
            final int currentTimeRecord = GameTimer.getInstance().getElapsedTime();

            int bonusTotems = 0;
            if ( lastTimeRecord == -1 || currentTimeRecord < lastTimeRecord ) {
                timeRecordArray.setTimeRecord( difficulty , currentTimeRecord );
                updateManager.addPendingItem( gameData.getWorldIdx() , gameData.getLevelIdx() );
                updatePlayerProgress = true;
                bonusTotems = gameData.getBonusTotems( isWorldBeatReward );
                gameData.addRemainingTotems( bonusTotems );
            }

            if ( updatePlayerProgress ) gameData.updatePlayerProgress();

            showDialogWinner( message , lastTimeRecord , currentTimeRecord , bonusTotems );
        }
    };

    private void showDialogRetry() {
        final DialogRetry dialogRetry = new DialogRetry();
        final Bundle args = new Bundle();
        args.putInt( DialogRetry.ARG_REMAINING_TOTEMS , gameData.getRemainingTotemsArray() );
        args.putInt( DialogRetry.ARG_DIFFICULTY , gameData.getDifficulty() );
        args.putInt( Dialogs.ARG_CLIP_HEIGHT , adBannerHeight );
        dialogRetry.setArguments( args );
        if ( !fragmentManager.isDestroyed() ) {
            dialogRetry.show( fragmentManager );
            dialogBase = dialogRetry;
        }
    }

    private void showDialogWinner( final String message , final int lastTimeRecord , final int currentTimeRecord , final int bonusTotems ) {
        final DialogWinner dialogWinner = new DialogWinner();
        final Bundle args = new Bundle();
        args.putString( DialogWinner.ARG_MESSAGE , message );
        args.putInt( DialogWinner.ARG_LAST_TIME_RECORD , lastTimeRecord );
        args.putInt( DialogWinner.ARG_CURRENT_TIME_RECORD , currentTimeRecord );
        args.putInt( DialogWinner.ARG_BONUS_TOTEMS , bonusTotems );
        args.putInt( Dialogs.ARG_CLIP_HEIGHT , adBannerHeight );
        dialogWinner.setArguments( args );
        if ( !fragmentManager.isDestroyed() ) {
            dialogWinner.show( fragmentManager );
            dialogBase = dialogWinner;
        }
    }

    public void showLevelNameDialog() {
        final String message = getString( R.string.level_title , gameData.getLevelNum() );
        final DialogAnnounce dialogAnnounce = new DialogAnnounce();
        final Bundle args = new Bundle();
        args.putString( DialogAnnounce.ARG_MESSAGE , message );
        args.putInt( Dialogs.ARG_CLIP_HEIGHT , adBannerHeight );
        dialogAnnounce.setArguments( args );
        if ( !fragmentManager.isDestroyed() ) {
            dialogAnnounce.show( fragmentManager );
            dialogBase = dialogAnnounce;
        }
    }

    private final FragmentResultListener fragmentResultListener = new FragmentResultListener() {
        @Override
        public void onFragmentResult( @NonNull final String requestCode , @NonNull final Bundle result ) {
            dialogBase = null;
            final int methodCode = result.getInt( Dialogs.METHOD_CODE );

            switch ( requestCode ) {
                case DialogAnnounce.REQUEST_CODE:
                    if ( methodCode == Dialogs.ON_CANCEL ) finishActivity();
                    else if ( methodCode == Dialogs.ON_FINISH_ANNOUNCE ) {
                        if ( !IntroManager.getInstance().isGroupDisplayed( IntroGroupId.IN_GAME ) ) showIntro();
                        else gameViewManager.startGame();
                    }
                    break;
                case DialogWinner.REQUEST_CODE:
                    if ( methodCode == Dialogs.ON_CLICK_LEVEL_SELECTOR ) startLevelSelectorActivity();
                    else if ( methodCode == Dialogs.ON_CLICK_RESTART ) gameViewManager.restartGame();
                    else if ( methodCode == Dialogs.ON_CANCEL ) finishActivity();
                    else if ( methodCode == Dialogs.ON_CLICK_NEXT_LEVEL ) {
                        if ( gameData.isLastWorld() && gameData.isLastLevel() ) {
                            gameData.setEmptyWorldCursor();
                            startLevelSelectorActivity();
                        } else {
                            if ( gameData.isLastLevel() ) gameData.loadNextWorld();
                            else gameData.loadNextLevel();

                            gameViewManager.loadLevel();
                            gameViewManager.updateDraws();
                            showLevelNameDialog();
                        }
                    }
                    break;
                case DialogRetry.REQUEST_CODE:

                    if ( result.containsKey( DialogRetry.RESULT_REMAINING_TOTEMS ) ) {
                        gameData.setRemainingTotemsArray( result.getInt( DialogRetry.RESULT_REMAINING_TOTEMS ) );
                    }

                    if ( methodCode == Dialogs.ON_CLICK_RESTART ) gameViewManager.restartGame();
                    else if ( methodCode == Dialogs.ON_CLICK_LEVEL_SELECTOR ) startLevelSelectorActivity();
                    else if ( methodCode == Dialogs.ON_CANCEL ) finishActivity();
                    else if ( methodCode == Dialogs.ON_CLICK_RETRY ) {
                        gameViewManager.retryGame();
                    }
            }
        }
    };

    private void showIntro() {
        final PlayerView playerView = gameViewManager.getPlayerView();
        final ImageView ivPlayer = playerView.getPlayerImageView();
        final Player player = playerView.getPlayer();
        ivPlayer.setVisibility( View.INVISIBLE );
        ivPlayer.setX( player.getPosX() );
        ivPlayer.setY( player.getPosY() );

        final RelativeLayout.LayoutParams layoutParams;
        layoutParams = new RelativeLayout.LayoutParams( InGame.INTRO_PHONE_BACK_BTN , -2 );
        layoutParams.addRule( RelativeLayout.CENTER_IN_PARENT );

        final ImageView ivPhoneBackButton = new ImageView( context );
        ivPhoneBackButton.setLayoutParams( layoutParams );
        ivPhoneBackButton.setVisibility( View.GONE );
        ivPhoneBackButton.setImageResource( R.drawable.drawable_phone_back_button );
        ivPhoneBackButton.setAdjustViewBounds( true );

        final RelativeLayout relativeLayout = activity.findViewById( R.id.relative_layout );
        relativeLayout.addView( ivPhoneBackButton );

        final IntroFactory introFactory = new IntroFactory( activity );
        introFactory.setLayoutMargin( adBannerHeight , Gravity.BOTTOM );
        introFactory.showInGameIntro( ivPlayer , ivPhoneBackButton , new IntroFactory.Callback() {
            @Override
            public void onIntroEndA() {
                final Animator fadeInAnimator = AnimatorInflater.loadAnimator( context , android.R.animator.fade_in );
                fadeInAnimator.setTarget( ivPhoneBackButton );
                fadeInAnimator.start();
                ivPhoneBackButton.setVisibility( View.VISIBLE );
            }

            @Override
            public void onIntroEndB() {
                final Animator fadeOutAnimator = AnimatorInflater.loadAnimator( context , android.R.animator.fade_out );
                fadeOutAnimator.setTarget( ivPhoneBackButton );
                fadeOutAnimator.addListener( ( AnimatorListener ) () -> {
                    relativeLayout.removeView( ivPhoneBackButton );
                    gameViewManager.startGame();
                } );
                fadeOutAnimator.start();
            }
        } );
    }

    private void startLevelSelectorActivity() {
        final Intent intent = new Intent( activity , LevelSelectorActivity.class );
        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
        ContextCompat.startActivity( context , intent , null );
        overridePendingTransition( android.R.anim.fade_in , android.R.anim.fade_out );
        activity.finish();
    }

    private void finishActivity() {
        if ( activity.isTaskRoot() ) {
            final Intent intent = new Intent( activity , MainScreenActivity.class );
            ContextCompat.startActivity( context , intent , null );
            overridePendingTransition( android.R.anim.fade_in , android.R.anim.fade_out );
        }
        activity.finish();
    }
}