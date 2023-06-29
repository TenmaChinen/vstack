package com.softramen.vstack.utils;

import android.util.Log;

import com.softramen.constants.Tags;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class JsonSerializer {

    public static String fromPlayerProgress( final WorldData[] worldDataArray ) {
        final JSONObject jsonObjectPlayerProgress = new JSONObject();
        try {
            for ( final WorldData worldData : worldDataArray ) {
                final JSONObject jsonObjectWorld = new JSONObject();
                final JSONObject jsonObjectLevels = new JSONObject();
                for ( final LevelData levelData : worldData.levelDataArray ) {
                    final JSONObject jsonObjectLevel = new JSONObject();
                    jsonObjectLevel.put( "timeRecords" , fromTimeRecords( levelData.getTimeRecords() ) );
                    jsonObjectLevels.put( levelData.id , jsonObjectLevel );
                }
                jsonObjectWorld.put( "levels" , jsonObjectLevels );
                jsonObjectPlayerProgress.put( worldData.id , jsonObjectWorld );
            }
            return jsonObjectPlayerProgress.toString();
        } catch ( final JSONException jsonException ) {
            Log.e( Tags.ERROR , "toJsonStringPlayerProgress > jsonException : " + jsonException );
            return null;
        }
    }

    private static JSONArray fromTimeRecords( final LevelData.TimeRecords timeRecords ) throws JSONException {
        final String arrayString = Arrays.toString( timeRecords.getArray() );
        return new JSONArray( arrayString );
    }
}
