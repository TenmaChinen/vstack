package com.softramen.vstack.utils;

import android.graphics.ColorMatrixColorFilter;
import android.util.Log;

import com.softramen.constants.Colors;
import com.softramen.constants.Tags;
import com.softramen.utils.ColorFilterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonParser {

    public static WorldData[] toWorldDataArray( final JSONArray jsonArrayWorldData ) {
        final WorldData[] worldsData = new WorldData[ jsonArrayWorldData.length() ];
        try {
            final ColorMatrixColorFilter[] colorMatrixColorFilters = new ColorMatrixColorFilter[ worldsData.length ];

            for ( int worldIdx = 0 ; worldIdx < worldsData.length ; worldIdx++ ) {
                final JSONObject jsonObjectWorldData = jsonArrayWorldData.getJSONObject( worldIdx );
                final JSONArray jsonArrayLevelData = jsonObjectWorldData.getJSONArray( "levels" );
                final LevelData[] levelDataArray = new LevelData[ jsonArrayLevelData.length() ];

                for ( int levelIdx = 0 ; levelIdx < levelDataArray.length ; levelIdx++ ) {
                    levelDataArray[ levelIdx ] = toLevelData( jsonArrayLevelData.getJSONObject( levelIdx ) );
                }

                final String worldId = jsonObjectWorldData.getString( "id" );
                final String worldName = jsonObjectWorldData.getString( "name" );

                final JSONObject jsonObjectColorFilter = jsonObjectWorldData.getJSONObject( "colorFilter" );
                final ColorMatrixColorFilter matrixColorFilter = toMatrixColorFilter( jsonObjectColorFilter );
                colorMatrixColorFilters[ worldIdx ] = matrixColorFilter;

                worldsData[ worldIdx ] = new WorldData( worldId , worldName , levelDataArray );
            }

            Colors.loadMatrixColorFilterList( colorMatrixColorFilters );

            return worldsData;
        } catch ( final JSONException jsonException ) {
            Log.e( Tags.ERROR , "fromJsonWorldsData > Error : " + jsonException );
            return null;
        }
    }

    private static LevelData toLevelData( final JSONObject jsonLevelData ) {
        try {
            final String id = jsonLevelData.getString( "id" );
            final int[] speeds = toIntArray( jsonLevelData.getJSONArray( "speeds" ) );
            final int[][] matrix = toIntMatrix( jsonLevelData.getJSONArray( "matrix" ) );
            final boolean isAnimated = jsonLevelData.getBoolean( "isAnimated" );
            return new LevelData( id , speeds , matrix , isAnimated );
        } catch ( final JSONException jsonException ) {
            Log.e( Tags.ERROR , "fromJsonLevelData > Error : " + jsonException );
            return null;
        }
    }

    public static void loadPlayerProgress( final String jsonString , final WorldData[] worldDataArray ) {
        try {
            final JSONObject jsonObjectPlayerProgress = new JSONObject( jsonString );
            for ( final WorldData worldData : worldDataArray ) {
                if ( jsonObjectPlayerProgress.isNull( worldData.id ) ) continue;
                final JSONObject jsonObjectWorld = jsonObjectPlayerProgress.getJSONObject( worldData.id );
                if ( jsonObjectWorld.isNull( "levels" ) ) continue;
                final JSONObject jsonObjectLevels = jsonObjectWorld.getJSONObject( "levels" );
                loadTimeRecordArray( jsonObjectLevels , worldData.levelDataArray );
            }
        } catch ( final JSONException jsonException ) {
            Log.e( Tags.ERROR , "loadJsonStringPlayerProgress > jsonException : " + jsonException );
        }
    }

    private static void loadTimeRecordArray( final JSONObject jsonObjectLevels , final LevelData[] levelDataArray ) throws JSONException {
        for ( final LevelData levelData : levelDataArray ) {
            if ( jsonObjectLevels.isNull( levelData.id ) ) continue;
            final JSONObject jsonObjectLevel = jsonObjectLevels.getJSONObject( levelData.id );
            if ( jsonObjectLevel.isNull( "timeRecords" ) ) continue;
            final JSONArray jsonArrayTimeRecords = jsonObjectLevel.getJSONArray( "timeRecords" );
            levelData.setRecordTimes( toIntArray( jsonArrayTimeRecords ) );
        }
    }

    /*   P R I M I T I V E   */

    public static int[] toIntArray( final JSONArray jsonArray ) {
        try {
            final int[] intArray = new int[ jsonArray.length() ];
            for ( int idx = 0 ; idx < intArray.length ; idx++ ) {
                intArray[ idx ] = jsonArray.getInt( idx );
            }
            return intArray;
        } catch ( final JSONException jsonException ) {
            Log.e( Tags.ERROR , "jsonArrayToIntArray > jsonException : " + jsonException );
            return null;
        }
    }

    public static int[] toIntArray( final String jsonStringArray ) {
        try {
            final JSONArray jsonArray = new JSONArray( jsonStringArray );
            return toIntArray( jsonArray );
        } catch ( final Exception error ) {
            Log.d( Tags.ERROR , "Error : " + error );
            return null;
        }
    }

    public static int[][] toIntMatrix( final JSONArray jsonMatrix ) {
        int[][] intMatrix = null;
        try {
            final int rows = jsonMatrix.length();
            final int cols = jsonMatrix.getJSONArray( 0 ).length();
            intMatrix = new int[ rows ][ cols ];
            for ( int row = 0 ; row < rows ; row++ ) {
                final JSONArray jsonArray = jsonMatrix.getJSONArray( row );
                for ( int col = 0 ; col < cols ; col++ ) {
                    intMatrix[ row ][ col ] = jsonArray.getInt( col );
                }
            }
        } catch ( final JSONException jsonException ) {
            Log.e( Tags.ERROR , "jsonMatrixToIntMatrix > jsonException : " + jsonException );
        }
        return intMatrix;
    }

    private static ColorMatrixColorFilter toMatrixColorFilter( final JSONObject jsonObjectMatrixColorFilter ) throws JSONException {
        final int hue = jsonObjectMatrixColorFilter.getInt( "hue" );
        final int contrast = jsonObjectMatrixColorFilter.getInt( "contrast" );
        if ( hue == 0 && contrast == 0 ) return null;
        return ColorFilterManager.createMatrixColorFilter( hue , contrast );
    }
}
