package com.softramen.vstack.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.LifecycleOwner;
import com.softramen.admobManager.BannerAdManager;
import com.softramen.buttons.BiImageView;
import com.softramen.constants.Dialogs;
import com.softramen.dialogs.DialogSettings;
import com.softramen.dialogs.utils.DialogBase;
import com.softramen.introFactory.IntroManager;
import com.softramen.settingsManager.SettingsId;
import com.softramen.settingsManager.SettingsPreferences;
import com.softramen.sounds.Sounds;
import com.softramen.vstack.GameData;
import com.softramen.vstack.GamePreferences;
import com.softramen.vstack.R;
import com.softramen.vstack.databinding.ActivityMainBinding;
import com.softramen.vstack.mainScreen.MainScreenView;
import com.softramen.vstack.utils.GameDifficulty;

public class MainScreenActivity extends AppCompatActivity {
	private final Sounds sounds = Sounds.getInstance();
	private FragmentManager fragmentManager;
	private MainScreenView mainScreenView;
	private BiImageView btnDifficulty;
	private DialogBase dialogBase = null;
	private Activity activity;
	private Context context;

	private int adBannerHeight = 0;

	@Override
	protected void onCreate( final Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		context = this;
		activity = this;

		final ActivityMainBinding binding;
		binding = DataBindingUtil.setContentView( activity , R.layout.activity_main );
		binding.setClickListener( this::onClick );

		mainScreenView = new MainScreenView( context );
		binding.frameLayout.addView( mainScreenView );

		btnDifficulty = binding.btnDifficulty;
		updateBtnDifficultyState();

		fragmentManager = getSupportFragmentManager();
		final LifecycleOwner lifecycleOwner = this;
		fragmentManager.setFragmentResultListener( DialogSettings.REQUEST_CODE , lifecycleOwner , fragmentResultListener );

		adBannerHeight = BannerAdManager.loadAdBanner( activity );

		// AUTOMATE
		// showDialogSettings();
	}

	public void onClick( final View view ) {
		final int viewId = view.getId();
		final GameData gameData = GameData.getInstance();
		if ( viewId == R.id.btn_start_resume ) {
			final Intent intent = new Intent( activity , InGameActivity.class );
			final int lastUnlockedWorldIdx = gameData.getLastUnlockedWorldIdx();
			final int lastUnlockedLevelIdx = gameData.getLastUnlockedLevelIdx( lastUnlockedWorldIdx );
			intent.putExtra( InGameActivity.WORLD , lastUnlockedWorldIdx );
			intent.putExtra( InGameActivity.LEVEL , lastUnlockedLevelIdx );
			ContextCompat.startActivity( context , intent , null );
		} else if ( viewId == R.id.btn_levels ) {
			final int lastUnlockedWorldIdx = gameData.getLastUnlockedWorldIdx();
			gameData.setWorldByIdx( lastUnlockedWorldIdx );
			final Intent intent = new Intent( activity , LevelSelectorActivity.class );
			ContextCompat.startActivity( context , intent , null );
		} else if ( viewId == R.id.btn_difficulty ) {
			nextDifficultyState();
		} else if ( viewId == R.id.btn_settings ) {
			showDialogSettings();
		}

		sounds.playRePop();
	}

	private void updateBtnDifficultyState() {
		final int difficulty = GameData.getInstance().getDifficulty();
		final int drawableResourceId = GameDifficulty.getDrawableResourceId( difficulty );
		btnDifficulty.setTextImage( drawableResourceId );
	}

	private void nextDifficultyState() {
		final GameData gameData = GameData.getInstance();
		gameData.setDifficulty( GameDifficulty.getNext( gameData.getDifficulty() ) );
		updateBtnDifficultyState();
	}

	private void showDialogSettings() {
		final DialogSettings dialogSettings = new DialogSettings();
		final Bundle args = new Bundle();
		args.putInt( Dialogs.ARG_CLIP_HEIGHT , adBannerHeight );
		dialogSettings.setArguments( args );
		if ( !fragmentManager.isDestroyed() ) {
			dialogSettings.show( fragmentManager );
			dialogBase = dialogSettings;
		}
	}

	@Override
	public void onBackPressed() {
		if ( dialogBase == null ) {
			Sounds.getInstance().playCancel();
			activity.finish();
		} else dialogBase.dismiss();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mainScreenView.restartAnimation();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mainScreenView.stopAnimation();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mainScreenView.stopAnimation();
	}

	private final FragmentResultListener fragmentResultListener = new FragmentResultListener() {
		@Override
		public void onFragmentResult( @NonNull final String requestCode , @NonNull final Bundle result ) {
			dialogBase = null;
			if ( requestCode.equals( DialogSettings.REQUEST_CODE ) ) {
				final int methodCode = result.getInt( Dialogs.METHOD_CODE );
				if ( methodCode == Dialogs.ON_CLICK_SAVE ) {
					final SettingsPreferences settingsPreferences = SettingsPreferences.getInstance();
					final int[] changedIds = result.getIntArray( DialogSettings.RESULT_CHANGED_IDS );
					for ( final int changedId : changedIds ) {
						switch ( changedId ) {
							case SettingsId.SCREEN_ANIMATION:
								final boolean animationState = settingsPreferences.getScreenAnimationState();
								mainScreenView.setAnimationState( animationState );
								break;
							case SettingsId.NUM_TOTEMS:
								final int numTotems = settingsPreferences.getNumTotems();
								GameData.getInstance().setRemainingTotemsArray( numTotems );
							case SettingsId.RESET_PROGRESS:
								GamePreferences.getInstance().clearAll();
								updateBtnDifficultyState();
								break;
							case SettingsId.RESET_INTRO:
								IntroManager.getInstance().resetAll();
								break;
							case SettingsId.SOUND_PLAYER:
								sounds.setPlayerState( settingsPreferences.getSoundPlayerState() );
								break;
							case SettingsId.SOUND_INTERFACE:
								sounds.setInterfaceState( settingsPreferences.getSoundInterfaceState() );
						}
					}
				}
			}
		}
	};
}