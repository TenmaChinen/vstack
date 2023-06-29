package com.softramen.vstack.utils;

import com.softramen.constants.Screen;

public class SurfaceData {
    private final String TAG = "SURFACE_DATA";

    public final int surfaceWidth;
    public final int surfaceHeight;
    public final int marginX;
    public final int marginY;
    public final int tileSize;
    public final int rows;

    public SurfaceData( final int rows , final int cols , final int frameWidth , final int frameHeight , int marginX , int marginY ) {

        final int tileSizeA = ( frameWidth - 2 * marginX ) / cols;
        final int tileSizeB = ( frameHeight - 2 * marginY ) / rows;
        final int tileSize;

        if ( tileSizeA < tileSizeB ) {
            tileSize = tileSizeA;
            marginY = ( frameHeight - rows * tileSize ) / 2;
        } else {
            tileSize = tileSizeB;
            marginX = ( frameWidth - cols * tileSize ) / 2;
        }
        this.tileSize = tileSize;
        this.marginX = marginX;
        this.marginY = marginY;
        surfaceHeight = rows * tileSize;
        surfaceWidth = cols * tileSize;
        this.rows = rows;
    }

    public SurfaceData( final int cols ) {
        final float screenWidth = ( float ) Screen.WIDTH;
        final float screenHeight = ( float ) Screen.HEIGHT;
        tileSize = Math.round( screenWidth / ( cols - 0.8f ) );
        rows = ( int ) Math.ceil( screenHeight / tileSize );

        marginX = Math.round( ( screenWidth - cols * tileSize ) / 2 );
        marginY = Math.round( ( screenHeight - rows * tileSize ) / 2 );

        surfaceHeight = rows * tileSize;
        surfaceWidth = cols * tileSize;
    }
}
