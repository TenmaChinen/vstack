package com.softramen.vstack.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.softramen.constants.Tags;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;

public class FileManager {
    private final AssetManager assetManager;
    private final Context context;

    public FileManager( final Context context ) {
        this.context = context;
        assetManager = context.getAssets();
    }

    public WorldData[] getWorldDataArray() {

        try {
            final InputStream inputStream = assetManager.open( "worlds/worlds.json" );
            final byte[] buffer = new byte[ inputStream.available() ];
            inputStream.read( buffer );
            inputStream.close();
            final JSONArray jsonArrayWorld = new JSONArray( new String( buffer ) );
            return JsonParser.toWorldDataArray( jsonArrayWorld );
        } catch ( final IOException ioException ) {
            Log.e( Tags.ERROR , "getLevelsData > Error : " + ioException );
            return null;
        } catch ( final JSONException jsonException ) {
            Log.e( Tags.ERROR , "getWorldsData > jsonException : " + jsonException );
            return null;
        }
    }

    public Bitmap[] getTileBitmapArray() {
        final String[] tileNameArray = new String[]{ "off" , "on" , "wall" , "broken_off" , "broken_on" , "arrow_left" , "arrow_right" , "portal" };
        final Bitmap[] bitmapArray = new Bitmap[ tileNameArray.length ];

        for ( int idx = 0 ; idx < tileNameArray.length ; idx++ ) {
            try {
                final InputStream inputStream = assetManager.open( "tiles/" + tileNameArray[ idx ] + ".png" );
                bitmapArray[ idx ] = BitmapFactory.decodeStream( inputStream );
            } catch ( final IOException ioException ) {
                Log.e( Tags.ERROR , "getTilesBitmap > ioException : " + ioException );
            }
        }
        return bitmapArray;
    }

    public Bitmap getTileIconBitmap() {
        try {
            final InputStream inputStream = assetManager.open( "other/icon.png" );
            return BitmapFactory.decodeStream( inputStream );
        } catch ( final IOException ioException ) {
            Log.e( Tags.ERROR , "getTilesBitmap > ioException : " + ioException );
            return null;
        }
    }
}
