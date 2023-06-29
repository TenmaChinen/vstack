package com.softramen.vstack.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewpager2.widget.ViewPager2;

import com.softramen.admobManager.BannerAdManager;
import com.softramen.constants.Dialogs;
import com.softramen.constants.LevelSelector;
import com.softramen.constants.TextSize;
import com.softramen.dialogs.DialogWorldSelector;
import com.softramen.dialogs.utils.DialogBase;
import com.softramen.introFactory.IntroFactory;
import com.softramen.introFactory.IntroGroupId;
import com.softramen.introFactory.IntroManager;
import com.softramen.sounds.Sounds;
import com.softramen.utils.anim.AnimationFactory;
import com.softramen.vstack.GameData;
import com.softramen.vstack.R;

import com.softramen.vstack.databinding.ActivityLevelSelectorBinding;
import com.softramen.vstack.levelSelector.RecyclerViewAdapter;
import com.softramen.vstack.levelSelector.ViewPagerAdapter;
import com.softramen.vstack.utils.UpdateManager;

import java.util.ArrayList;
import java.util.Map;


public class LevelSelectorActivity extends AppCompatActivity {

    private final Sounds sounds = Sounds.getInstance();
    private final GameData gameData = GameData.getInstance();

    private TextView tvWorldNum, tvWorldName, tvRemainingTotems;
    private ViewPagerAdapter viewPagerAdapter;
    private FragmentManager fragmentManager;
    private DialogBase dialogBase = null;
    private ViewPager2 viewPager;
    private Activity activity;
    private Context context;

    private ImageView ivBtnPrev, ivBtnNext;
    private Animation toastAnimation;
    private TextView tvToast;

    private int adBannerHeight = 0;
    private boolean isViewPagerSoundEnabled = true;

    @Override
    protected void onCreate( final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        context = this;
        activity = this;

        final ActivityLevelSelectorBinding binding;
        binding = DataBindingUtil.setContentView( activity , R.layout.activity_level_selector );
        binding.setClickListener( this::onClick );

        tvWorldNum = binding.tvWorldNum;
        tvWorldNum.setTextSize( TypedValue.COMPLEX_UNIT_PX , TextSize.LevelSelector.TITLE );

        tvWorldName = binding.tvWorldName;
        tvWorldName.setTextSize( TypedValue.COMPLEX_UNIT_PX , TextSize.LevelSelector.WORLD_NAME );

        tvRemainingTotems = binding.tvRemainingTotems;
        tvRemainingTotems.setTextSize( TypedValue.COMPLEX_UNIT_PX , TextSize.LevelSelector.REMAINING_TOTEMS );

        binding.ivRemainingTotems.setImageBitmap( gameData.getDifficultyBullet() );
        binding.btnBack.setPadding( LevelSelector.BTN_BACK_PAD , 0 , LevelSelector.BTN_BACK_PAD , 0 );

        ivBtnPrev = binding.btnPrev;
        ivBtnNext = binding.btnNext;

        viewPager = binding.viewPager;
        viewPagerAdapter = new ViewPagerAdapter( context );
        viewPagerAdapter.setCallback( levelThumbnailCallback );

        viewPager.setAdapter( viewPagerAdapter );
        viewPager.registerOnPageChangeCallback( onPageChangeCallback );

        adBannerHeight = BannerAdManager.loadAdBanner( activity );

        fragmentManager = getSupportFragmentManager();
        final LifecycleOwner lifecycleOwner = this;
        fragmentManager.setFragmentResultListener( DialogWorldSelector.REQUEST_CODE , lifecycleOwner , fragmentResultListener );

        tvToast = binding.tvToast;
        toastAnimation = AnimationFactory.makeToastAnimation( tvToast );

        if ( !IntroManager.getInstance().isGroupDisplayed( IntroGroupId.LEVEL_SELECTOR ) ) {
            final IntroFactory introFactory = new IntroFactory( activity );
            introFactory.setLayoutMargin( adBannerHeight , Gravity.TOP );
            introFactory.showLevelSelectorIntro( binding.btnWorldNumName , binding.areaRemainingTotems );
        }

        // AUTOMATE
        // showDialogWorldSelector();
    }

    @Override
    public void onBackPressed() {
        if ( dialogBase == null ) {
            sounds.playCancel();
            finishActivity();
        } else dialogBase.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Updates RecyclerView items related to each unlocked level while app is running

        final UpdateManager updateManager = UpdateManager.getInstance();
        final Map<Integer, ArrayList<Integer>> itemsHashMap = updateManager.getHashMap();
        for ( final Integer worldIndex : itemsHashMap.keySet() ) {
            viewPagerAdapter.notifyRecyclerViewItemsChanged( worldIndex , itemsHashMap.get( worldIndex ) );
        }
        updateManager.clear();

        tvRemainingTotems.setText( getString( R.string.remaining_totems , gameData.getRemainingTotemsArray() ) );
        viewPager.setCurrentItem( gameData.getWorldIdx() , false );
    }


    public final ViewPager2.OnPageChangeCallback onPageChangeCallback = new ViewPager2.OnPageChangeCallback() {

        @Override
        public void onPageSelected( final int position ) {

            final int itemCount = viewPagerAdapter.getItemCount();
            final int lastIndex = itemCount - 1;

            gameData.setWorldByIdx( position );
            tvWorldNum.setText( getString( R.string.world_number , position + 1 ) );
            tvWorldName.setText( gameData.getWorldName() );

            if ( position == 0 || itemCount == 1 ) {
                ivBtnPrev.setVisibility( View.GONE );
            } else if ( ivBtnPrev.getVisibility() == View.GONE ) {
                ivBtnPrev.setVisibility( View.VISIBLE );
            }

            if ( position == lastIndex || itemCount == 1 ) {
                ivBtnNext.setVisibility( View.GONE );
            } else if ( ivBtnNext.getVisibility() == View.GONE ) {
                ivBtnNext.setVisibility( View.VISIBLE );
            }
        }

        @Override
        public void onPageScrollStateChanged( final int state ) {
            if ( state == ViewPager2.SCROLL_STATE_SETTLING && isViewPagerSoundEnabled ) {
                sounds.playPop();
            }
            isViewPagerSoundEnabled = true;
        }
    };

    private final RecyclerViewAdapter.Callback levelThumbnailCallback = ( levelIdx , isUnlocked ) -> {
        if ( isUnlocked ) {
            sounds.playRepress();
            final Intent intent = new Intent( activity , InGameActivity.class );
            intent.putExtra( InGameActivity.WORLD , viewPager.getCurrentItem() );
            intent.putExtra( InGameActivity.LEVEL , levelIdx );
            ContextCompat.startActivity( context , intent , null );
        } else {
            sounds.playError();

            final String message;
            final int pageIdx = viewPager.getCurrentItem();
            int lastUnlockedLevelIdx = gameData.getLastUnlockedLevelIdx( pageIdx );
            if ( lastUnlockedLevelIdx == -1 ) {
                final String prevWorldName = gameData.getWorldName( pageIdx - 1 );
                message = getString( R.string.beat_world_message , prevWorldName );
            } else {
                final int prevLevelNum = lastUnlockedLevelIdx + 1;
                message = getString( R.string.beat_level_message , prevLevelNum );
            }

            if ( toastAnimation.hasStarted() ) toastAnimation.cancel();
            tvToast.post( () -> {
                tvToast.setText( message );
                tvToast.setVisibility( View.VISIBLE );
                tvToast.startAnimation( toastAnimation );
            } );
        }
    };

    public void onClick( final View view ) {
        final int viewId = view.getId();

        if ( viewId == R.id.btn_back ) {
            sounds.playPop();
            finishActivity();
        } else if ( viewId == R.id.btn_prev ) viewPager.setCurrentItem( viewPager.getCurrentItem() - 1 );
        else if ( viewId == R.id.btn_next ) viewPager.setCurrentItem( viewPager.getCurrentItem() + 1 );
        else if ( viewId == R.id.btn_world_num_name ) {
            sounds.playAppear();
            showDialogWorldSelector();
        }
    }

    private final FragmentResultListener fragmentResultListener = new FragmentResultListener() {
        @Override
        public void onFragmentResult( @NonNull final String requestCode , @NonNull final Bundle result ) {
            dialogBase = null;
            if ( requestCode.equals( DialogWorldSelector.REQUEST_CODE ) ) {
                final int targetPosition = result.getInt( DialogWorldSelector.RESULT_POSITION , -1 );
                if ( targetPosition != -1 ) {
                    final boolean smoothScroll = Math.abs( viewPager.getCurrentItem() - targetPosition ) == 1;
                    if ( smoothScroll ) isViewPagerSoundEnabled = false;
                    viewPager.setCurrentItem( targetPosition , smoothScroll );
                }
            }
        }
    };

    private void showDialogWorldSelector() {
        final DialogWorldSelector dialogWorldSelector = new DialogWorldSelector();
        final Bundle args = new Bundle();
        args.putInt( Dialogs.ARG_CLIP_HEIGHT , adBannerHeight );
        args.putStringArray( DialogWorldSelector.ARG_WORLD_NAMES , gameData.getWorldNameArray() );
        args.putInt( DialogWorldSelector.ARG_SELECTED_WORLD_POSITION , gameData.getWorldIdx() );
        dialogWorldSelector.setArguments( args );
        if ( !fragmentManager.isDestroyed() ) {
            dialogWorldSelector.show( fragmentManager );
            dialogBase = dialogWorldSelector;
        }
    }

    private void finishActivity() {
        if ( activity.isTaskRoot() ) {
            final Intent intent = new Intent( activity , MainScreenActivity.class );
            startActivity( intent );
        }
        activity.finish();
    }
}