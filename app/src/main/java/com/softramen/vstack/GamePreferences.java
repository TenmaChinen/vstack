package com.softramen.vstack;

import android.content.Context;
import android.content.SharedPreferences;

import com.softramen.constants.InGame;
import com.softramen.vstack.utils.GameDifficulty;
import com.softramen.vstack.utils.JsonParser;
import com.softramen.vstack.utils.JsonSerializer;
import com.softramen.vstack.utils.LevelData;
import com.softramen.vstack.utils.WorldData;

import java.util.Arrays;

public class GamePreferences {

    private static final String KEY_PREFERENCES = "com.softramen.vstack.PREFERENCES";
    private static final String KEY_PLAYER_PROGRESS = "PLAYER_PROGRESS";
    private static final String KEY_REMAINING_TOTEMS = "REMAINING_TOTEMS";
    private static final String KEY_DIFFICULTY = "DIFFICULTY";

    private final SharedPreferences sharedPreferences;
    private static GamePreferences instance;

    private GamePreferences( final Context context ) {
        sharedPreferences = context.getSharedPreferences( KEY_PREFERENCES , Context.MODE_PRIVATE );
    }

    public static synchronized void init( final Context context ) {
        if ( instance == null ) {
            instance = new GamePreferences( context );
        }
    }

    public static synchronized GamePreferences getInstance() {
        return instance;
    }

    public void clearAll() {
        if ( sharedPreferences.edit().clear().commit() ) {
            final GameData gameData = GameData.getInstance();
            gameData.loadUserData();
            for ( final WorldData worldData : gameData.worldDataArray ) {
                for ( final LevelData levelData : worldData.levelDataArray ) {
                    levelData.setRecordTimes( new int[]{ -1 , -1 , -1 } );
                }
            }
        }
    }

     /*
        Player Progress Data Structure

        {
            "worldId" : {
                "levels" : {
                    "levelId" : {
                        "timeRecords" : [-1,-1,-1]
                    },
                }
            }
        }
    */

    public void loadPlayerProgress( final WorldData[] worldDataArray ) {
        final String jsonString = sharedPreferences.getString( KEY_PLAYER_PROGRESS , "{}" );
        JsonParser.loadPlayerProgress( jsonString , worldDataArray );
    }

    public void savePlayerProgress( final WorldData[] worldDataArray ) {
        final String jsonString = JsonSerializer.fromPlayerProgress( worldDataArray );
        if ( jsonString != null ) {
            sharedPreferences.edit().putString( KEY_PLAYER_PROGRESS , jsonString ).apply();
        }
    }

    public int getDifficulty() {
        return sharedPreferences.getInt( KEY_DIFFICULTY , GameDifficulty.EASY );
    }

    public void setDifficulty( final int difficulty ) {
        sharedPreferences.edit().putInt( KEY_DIFFICULTY , difficulty ).apply();
    }

    public int[] getRemainingTotems() {
        final String jsonArray = sharedPreferences.getString( KEY_REMAINING_TOTEMS , InGame.INITIAL_REMAINING_TOTEMS );
        return JsonParser.toIntArray( jsonArray );
    }

    public void setRemainingTotems( final int[] remainingTotems ) {
        final String jsonStringArray = Arrays.toString( remainingTotems );
        sharedPreferences.edit().putString( KEY_REMAINING_TOTEMS , jsonStringArray ).apply();
    }
}
