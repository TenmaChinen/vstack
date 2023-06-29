package com.softramen.introFactory;

import android.content.Context;

import com.softramen.introView.IntroPreferenceManager;

import java.util.HashMap;
import java.util.Map;

public class IntroManager {
    private final IntroPreferenceManager preferenceManager;
    private static IntroManager instance;

    private final Map<String, Boolean> map = new HashMap<>();

    private IntroManager( final Context context ) {
        preferenceManager = new IntroPreferenceManager( context );
        loadStates();
    }

    public static synchronized void init( final Context context ) {
        if ( instance == null ) {
            instance = new IntroManager( context );
        }
    }

    public static IntroManager getInstance() {
        return instance;
    }

    private void loadStates() {
        map.put( IntroGroupId.LEVEL_SELECTOR , preferenceManager.isDisplayed( IntroGroupId.LEVEL_SELECTOR ) );
        map.put( IntroGroupId.IN_GAME , preferenceManager.isDisplayed( IntroGroupId.IN_GAME ) );
        map.put( IntroGroupId.DIALOG_RETRY , preferenceManager.isDisplayed( IntroGroupId.DIALOG_RETRY ) );
        map.put( IntroGroupId.DIALOG_RETRY_REWARD , preferenceManager.isDisplayed( IntroGroupId.DIALOG_RETRY_REWARD ) );
        map.put( IntroGroupId.DIALOG_WINNER , preferenceManager.isDisplayed( IntroGroupId.DIALOG_WINNER ) );
    }

    public boolean isGroupDisplayed( final String groupId ) {
        return map.get( groupId );
    }

    public void setGroupDisplayed( final String groupId ) {
        map.put( groupId , true );
        preferenceManager.setDisplayed( groupId );
    }

    public void resetAll() {
        for ( final String key : map.keySet() ) map.put( key , false );
        preferenceManager.resetAll();
    }
}
