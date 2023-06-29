package com.softramen.utils;

import android.graphics.Bitmap;

public class BitmapManager {
    public static Bitmap[] scaleBitmapArray( final Bitmap[] bitmapArray , final int width , final int height ) {
        final Bitmap[] scaledBitmaps = new Bitmap[ bitmapArray.length ];
        for ( int idx = 0 ; idx < bitmapArray.length ; idx++ ) {
            scaledBitmaps[ idx ] = scaleBitmap( bitmapArray[ idx ] , width , height );
        }
        return scaledBitmaps;
    }

    public static Bitmap scaleBitmap( final Bitmap bitmap , final int width , final int height ) {
        return Bitmap.createScaledBitmap( bitmap , width , height , false );
    }

    public static Bitmap scaleTileBitmap( final Bitmap tileBitmap , final int tileSize ) {
        return scaleBitmap( tileBitmap , tileSize , tileSize );
    }

    public static Bitmap[] scaleTileBitmapArray( final Bitmap[] tilesBitmap , final int tileSize ) {
        return scaleBitmapArray( tilesBitmap , tileSize , tileSize );
    }

    public static Bitmap[] makeCopy( final Bitmap[] bitmaps ) {
        final Bitmap[] bitmapsClone = new Bitmap[ bitmaps.length ];
        for ( int idx = 0 ; idx < bitmaps.length ; idx++ ) {
            bitmapsClone[ idx ] = Bitmap.createBitmap( bitmaps[ idx ] );
        }
        return bitmapsClone;
    }
}
