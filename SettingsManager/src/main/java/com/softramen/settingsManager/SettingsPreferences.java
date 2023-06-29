package com.softramen.settingsManager;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SettingsPreferences {

    private static final String KEY_PREFERENCES = "com.softramen.settingsPreferences.PREFERENCES";

    private static final String KEY_SOUND_PLAYER = "SOUND_PLAYER";
    private static final String KEY_SOUND_INTERFACE = "SOUND_INTERFACE";
    private static final String KEY_SCREEN_ANIMATION = "SCREEN_ANIMATION";
    private static final String KEY_PLAYER_START_ROW = "PLAYER_START_ROW";
    private static final String KEY_UNLOCK_LEVELS = "UNLOCK_LEVELS";

    private static SettingsPreferences instance;

    private final Map<Integer, String> settingIdKeyMap = new HashMap<>();
    private final Map<Integer, SettingsItem> settingsItemMap;
    private final SharedPreferences sharedPreferences;

    private SettingsPreferences( final Context context ) {
        sharedPreferences = context.getSharedPreferences( KEY_PREFERENCES , Context.MODE_PRIVATE );
        settingsItemMap = createSettingsItem();
        setSettingsIdToKeyMap();
    }

    public static synchronized void init( final Context context ) {
        if ( instance == null ) {
            instance = new SettingsPreferences( context );
        }
    }

    private Map<Integer, SettingsItem> createSettingsItem() {
        final Map<Integer, SettingsItem> settingsItemMap = new LinkedHashMap<>();
        final String dot = "\\.";
        settingsItemMap.put( SettingsId.SOUND_PLAYER , new SettingsItem( "Sound PLayer" , getSoundPlayerState() ) );
        settingsItemMap.put( SettingsId.SOUND_INTERFACE , new SettingsItem( "Sound Interface" , getSoundInterfaceState() ) );
        settingsItemMap.put( SettingsId.SCREEN_ANIMATION , new SettingsItem( "Background Animation" , getScreenAnimationState() ) );
        if ( BuildConfig.DEBUG ) {
            settingsItemMap.put( SettingsId.PLAYER_START_ROW , new SettingsItem( "Start Row" , "First.Prev.Last".split( dot ) , new int[]{ -1 , 1 , 0 } , getPlayerStartRowPosition() ) );
            settingsItemMap.put( SettingsId.UNLOCK_LEVELS , new SettingsItem( "Unlock Levels" , getUnlockLevelsState() ) );
            settingsItemMap.put( SettingsId.NUM_TOTEMS , new SettingsItem( "Num Totems" , "Keep.Reset.5".split( dot ) , new int[]{ -1 , 0 , 5 } , 0 ) );
            settingsItemMap.put( SettingsId.RESET_PROGRESS , new SettingsItem( "Reset progress" , false ) );
            settingsItemMap.put( SettingsId.RESET_INTRO , new SettingsItem( "Reset Intro" , false ) );
        }
        return settingsItemMap;
    }

    private void setSettingsIdToKeyMap() {
        settingIdKeyMap.put( SettingsId.SOUND_PLAYER , KEY_SOUND_PLAYER );
        settingIdKeyMap.put( SettingsId.SOUND_INTERFACE , KEY_SOUND_INTERFACE );
        settingIdKeyMap.put( SettingsId.SCREEN_ANIMATION , KEY_SCREEN_ANIMATION );
        if ( BuildConfig.DEBUG ) {
            settingIdKeyMap.put( SettingsId.PLAYER_START_ROW , KEY_PLAYER_START_ROW );
            settingIdKeyMap.put( SettingsId.UNLOCK_LEVELS , KEY_UNLOCK_LEVELS );
        }
    }

    public static synchronized SettingsPreferences getInstance() {
        return instance;
    }

    public Map<Integer, SettingsItem> getSettingsItemMap() {
        return settingsItemMap;
    }

    public boolean getSoundPlayerState() {
        return sharedPreferences.getBoolean( KEY_SOUND_PLAYER , true );
    }

    public boolean getSoundInterfaceState() {
        return sharedPreferences.getBoolean( KEY_SOUND_INTERFACE , true );
    }

    public boolean getScreenAnimationState() {
        return sharedPreferences.getBoolean( KEY_SCREEN_ANIMATION , true );
    }

    public int getPlayerStartRowPosition() {
        return sharedPreferences.getInt( KEY_PLAYER_START_ROW , 0 );
    }

    public int getNumTotems() {
        final SettingsItem settingsItem = settingsItemMap.get( SettingsId.NUM_TOTEMS );
        final int numTotems = settingsItem.getOptionInt();
        settingsItem.setOptionPosition( 0 );
        return numTotems;
    }

    public int getPlayerStartRow() {
        // First - Prev - Last
        final SettingsItem settingsItem = settingsItemMap.get( SettingsId.PLAYER_START_ROW );
        return settingsItem.getOptionInt();
    }

    public boolean getUnlockLevelsState() {
        return sharedPreferences.getBoolean( KEY_UNLOCK_LEVELS , false );
    }

    // SAVE
    public void saveSettings( final int[] settingIdArray ) {
        for ( final int settingId : settingIdArray ) {
            final String key = settingIdKeyMap.get( settingId );
            final SettingsItem settingsItem = settingsItemMap.get( settingId );
            switch ( settingsItem.getItemType() ) {
                case SettingsItem.Type.SPINNER:
                    sharedPreferences.edit().putInt( key , settingsItem.getOptionPosition() ).apply();
                    break;
                case SettingsItem.Type.SWITCH:
                    sharedPreferences.edit().putBoolean( key , settingsItem.getState() ).apply();
                    break;
            }
        }
    }

}
