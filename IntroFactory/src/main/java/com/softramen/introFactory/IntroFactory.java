package com.softramen.introFactory;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;

import com.softramen.constants.Intro;
import com.softramen.introView.IntroWidget;
import com.softramen.introView.shapes.ShapeType;

public class IntroFactory extends BaseIntroFactory {

    private final IntroManager introManager = IntroManager.getInstance();
    private final Activity activity;
    private Dialog dialog = null;

    public IntroFactory( final Activity activity ) {
        super( activity );
        this.activity = activity;
    }

    public IntroFactory( final Activity activity , final Dialog dialog ) {
        super( activity );
        this.activity = activity;
        this.dialog = dialog;
    }

    public interface Callback {
        default void onIntroEndA() {}
        default void onIntroEndB() {}
    }

    public void showLevelSelectorIntro( final View tvWorldName , final View ivRemainingTries ) {
        final String idA = IntroGroupId.LEVEL_SELECTOR + "_A";
        final String idB = IntroGroupId.LEVEL_SELECTOR + "_B";

        final String msgA = context.getString( R.string.intro_level_selector_world_selector );
        final IntroWidget introWidgetA = makeIntro( tvWorldName , msgA , ShapeType.RECTANGLE , idA );

        final String msgB = context.getString( R.string.intro_level_selector_remaining_totems );
        final IntroWidget introWidgetB = makeIntro( ivRemainingTries , msgB , ShapeType.RECTANGLE , idB );

        introWidgetA.setListener( id -> introWidgetB.show( activity ) );
        introWidgetB.setListener( id -> introManager.setGroupDisplayed( IntroGroupId.LEVEL_SELECTOR ) );

        startIntro( () -> introWidgetA.show( activity ) );
    }

    public void showInGameIntro( final View ivPlayer , final View areaBack , final Callback callback ) {
        final String idA = IntroGroupId.IN_GAME + "_A";
        final String idB = IntroGroupId.IN_GAME + "_B";

        final String msgA = context.getString( R.string.intro_in_game_tap_canvas );
        final IntroWidget introWidgetA = makeIntro( ivPlayer , msgA , ShapeType.RECTANGLE , Intro.IN_GAME_PLAYER_PAD , NULL , idA );

        final String msgB = activity.getString( R.string.intro_in_game_press_back );
        final IntroWidget introWidgetB = makeIntro( areaBack , msgB , ShapeType.RECTANGLE , idB );

        introWidgetA.setListener( id -> {
            introWidgetB.show( activity );
            callback.onIntroEndA();
        } );

        introWidgetB.setListener( id -> {
            introManager.setGroupDisplayed( IntroGroupId.IN_GAME );
            callback.onIntroEndB();
        } );

        startIntro( () -> introWidgetA.show( activity ) );
    }

    public void showDialogRetryIntro( final View btnRestart , final View btnRetry ) {
        final String idA = IntroGroupId.DIALOG_RETRY + "_A";
        final String idB = IntroGroupId.DIALOG_RETRY + "_B";

        final String msgA = context.getString( R.string.intro_dialog_retry_restart_button );
        final IntroWidget introWidgetA = makeIntro( btnRestart , msgA , ShapeType.CIRCLE , idA );

        final String msgB = context.getString( R.string.intro_dialog_retry_retry_button );
        final IntroWidget introWidgetB = makeIntro( btnRetry , msgB , ShapeType.CIRCLE , Intro.RETRY_PAD , NULL , idB );

        introWidgetA.setListener( id -> introWidgetB.show( dialog ) );
        introWidgetB.setListener( id -> introManager.setGroupDisplayed( IntroGroupId.DIALOG_RETRY ) );

        startIntro( () -> introWidgetA.show( dialog ) , 1000 );
    }

    public void showDialogRetryRewardIntro( final View tvBtnWatchVideo ) {
        final String idA = IntroGroupId.DIALOG_RETRY_REWARD + "_A";
        final String msgA = context.getString( R.string.intro_dialog_retry_reward_button );
        final IntroWidget introWidgetA = makeIntro( tvBtnWatchVideo , msgA , ShapeType.RECTANGLE , idA );

        introWidgetA.setListener( id -> introManager.setGroupDisplayed( IntroGroupId.DIALOG_RETRY_REWARD ) );

        startIntro( () -> introWidgetA.show( dialog ) );
    }

    public void showDialogWinnerIntro( final View areaExtraTriesLastRecord , final View areaTileExtraTriesNum ) {
        final String idA = IntroGroupId.DIALOG_WINNER + "_A";
        final String idB = IntroGroupId.DIALOG_WINNER + "_B";
        final String msgA = context.getString( R.string.intro_dialog_winner_new_record );
        final IntroWidget introWidgetA = makeIntro( areaExtraTriesLastRecord , msgA , ShapeType.RECTANGLE , idA );

        final String msgB = context.getString( R.string.intro_dialog_winner_extra_tries );
        final IntroWidget introWidgetB = makeIntro( areaTileExtraTriesNum , msgB , ShapeType.RECTANGLE , NULL , View.TEXT_ALIGNMENT_TEXT_START , idB );

        introWidgetA.setListener( id -> introWidgetB.show( dialog ) );
        introWidgetB.setListener( id -> introManager.setGroupDisplayed( IntroGroupId.DIALOG_WINNER ) );

        startIntro( () -> introWidgetA.show( dialog ) , 200 );
    }
}
