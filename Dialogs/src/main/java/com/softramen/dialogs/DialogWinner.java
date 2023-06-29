package com.softramen.dialogs;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.softramen.constants.Dialogs;
import com.softramen.constants.TextSize;
import com.softramen.dialogs.utils.DialogBase;
import com.softramen.introFactory.IntroFactory;
import com.softramen.introFactory.IntroGroupId;
import com.softramen.introFactory.IntroManager;
import com.softramen.sounds.Sounds;
import com.softramen.utils.Converter;
import com.softramen.utils.animator.AnimatorListener;

public class DialogWinner extends DialogBase {
    public static final String REQUEST_CODE = "DIALOG_WINNER";
    public static final String ARG_MESSAGE = "MESSAGE", ARG_LAST_TIME_RECORD = "ARG_LAST_TIME_RECORD";
    public static final String ARG_CURRENT_TIME_RECORD = "CURRENT_TIME_RECORD", ARG_BONUS_TOTEMS = "BONUS_TOTEMS";

    private final Sounds sounds = Sounds.getInstance();
    private Animator animatorTvNewRecordMessage;
    private TextView tvNewRecordMessage;

    private int lastTimeRecord, currentTimeRecord, bonusTotems, callbackId = Dialogs.ON_CANCEL;
    private String message;

    @Override
    public void onCreate( @Nullable final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        final Bundle args = getArguments();
        if ( args != null ) {
            message = args.getString( ARG_MESSAGE );
            lastTimeRecord = args.getInt( ARG_LAST_TIME_RECORD );
            currentTimeRecord = args.getInt( ARG_CURRENT_TIME_RECORD );
            bonusTotems = args.getInt( ARG_BONUS_TOTEMS );
        }
    }

    @Nullable
    @Override
    public View onCreateView( @NonNull final LayoutInflater inflater , @Nullable final ViewGroup container , @Nullable final Bundle savedInstanceState ) {
        final View inflatedView = inflater.inflate( R.layout.dialog_winner , container , false );
        final TextView tvMessage = inflatedView.findViewById( R.id.tv_message );
        final TextView tvCurrentRecordTime = inflatedView.findViewById( R.id.tv_current_record_time );
        final TextView tvLastRecordTime = inflatedView.findViewById( R.id.tv_last_record_time );
        final ImageView btnLevelSelector = inflatedView.findViewById( R.id.btn_level_selector );
        final ImageView btnNextLevel = inflatedView.findViewById( R.id.btn_next_level );
        final ImageView btnRestart = inflatedView.findViewById( R.id.btn_restart );
        tvNewRecordMessage = inflatedView.findViewById( R.id.tv_new_record_message );

        tvMessage.setTextSize( TypedValue.COMPLEX_UNIT_PX , TextSize.DialogWinner.WINNER_MESSAGE );
        tvNewRecordMessage.setTextSize( TypedValue.COMPLEX_UNIT_PX , TextSize.DialogWinner.NEW_RECORD_MESSAGE );
        tvCurrentRecordTime.setTextSize( TypedValue.COMPLEX_UNIT_PX , TextSize.DialogWinner.CURRENT_RECORD_TIME );
        tvLastRecordTime.setTextSize( TypedValue.COMPLEX_UNIT_PX , TextSize.DialogWinner.LAST_RECORD_TIME );

        tvMessage.setText( message );
        tvLastRecordTime.setText( getString( R.string.record_time , Converter.timeToMinSec( lastTimeRecord ) ) );
        tvCurrentRecordTime.setText( getString( R.string.elapsed_time , Converter.timeToMinSec( currentTimeRecord ) ) );

        // DEBUG
        // if ( true || ( lastTimeRecord == -1 || currentTimeRecord < lastTimeRecord ) ) {
        if ( lastTimeRecord == -1 || currentTimeRecord < lastTimeRecord ) {

            final ImageView ivSquare = inflatedView.findViewById( R.id.iv_square );
            final TextView tvBonusTotems = inflatedView.findViewById( R.id.tv_bonus_totems );
            final TextView tvPlus = inflatedView.findViewById( R.id.tv_plus );

            tvBonusTotems.setTextSize( TypedValue.COMPLEX_UNIT_PX , TextSize.DialogWinner.BONUS_TOTEMS );
            tvPlus.setTextSize( TypedValue.COMPLEX_UNIT_PX , TextSize.DialogWinner.BONUS_TOTEMS_PLUS );
            tvBonusTotems.setText( getString( R.string.extra_tries , bonusTotems ) );

            animatorTvNewRecordMessage = AnimatorInflater.loadAnimator( getContext() , R.animator.animator_pop_fade_in );
            final Animator animatorTvExtraTries = AnimatorInflater.loadAnimator( getContext() , R.animator.animator_pop_fade_in );

            animatorTvNewRecordMessage.setTarget( tvNewRecordMessage );
            animatorTvExtraTries.setTarget( tvBonusTotems );
            animatorTvExtraTries.setTarget( tvPlus );

            animatorTvNewRecordMessage.addListener( ( AnimatorListener ) () -> {
                ivSquare.setVisibility( View.VISIBLE );
                tvPlus.setVisibility( View.VISIBLE );
                tvBonusTotems.setVisibility( View.VISIBLE );
                animatorTvExtraTries.start();
            } );

            // I N T R O
            if ( !IntroManager.getInstance().isGroupDisplayed( IntroGroupId.DIALOG_WINNER ) ) {
                final IntroFactory introFactory = new IntroFactory( getActivity() , getDialog() );
                final View areaExtraTriesLastRecord = inflatedView.findViewById( R.id.area_bonus_totems_last_record_time );
                final View areaTileExtraTriesNum = inflatedView.findViewById( R.id.area_tile_bonus_totems_num );
                introFactory.showDialogWinnerIntro( areaExtraTriesLastRecord , areaTileExtraTriesNum );
            }
        }

        final View.OnClickListener onClickListener = view -> {
            btnLevelSelector.setOnClickListener( null );
            btnNextLevel.setOnClickListener( null );
            btnRestart.setOnClickListener( null );
            final int viewId = view.getId();
            sounds.playRepress();
            if ( viewId == R.id.btn_level_selector ) callbackId = Dialogs.ON_CLICK_LEVEL_SELECTOR;
            else if ( viewId == R.id.btn_next_level ) callbackId = Dialogs.ON_CLICK_NEXT_LEVEL;
            else if ( viewId == R.id.btn_restart ) callbackId = Dialogs.ON_CLICK_RESTART;
            else if ( viewId == R.id.btn_retry ) callbackId = Dialogs.ON_CLICK_RETRY;
            startDismissAnimation();
        };

        btnNextLevel.setOnClickListener( onClickListener );
        btnLevelSelector.setOnClickListener( onClickListener );
        btnRestart.setOnClickListener( onClickListener );

        return inflatedView;
    }

    @Override
    public void onDismiss( @NonNull final DialogInterface dialog ) {
        sendResults( REQUEST_CODE , callbackId , null );
        super.onDismiss( dialog );
    }

    @Override
    public void onWindowEnterAnimationEnd() {
        if ( animatorTvNewRecordMessage != null ) {
            animatorTvNewRecordMessage.start();
            tvNewRecordMessage.setVisibility( View.VISIBLE );
        }
    }

    public void show( @NonNull final FragmentManager manager ) {
        super.show( manager , "DialogWinner" );
    }
}