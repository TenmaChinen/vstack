package com.softramen.vstack.mainScreen;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.softramen.utils.BitmapManager;
import com.softramen.vstack.utils.SurfaceData;

public class MainScreenBackground {
    private final BackgroundAnimations backgroundAnimations;
    private final BackgroundMatrix backgroundMatrix;
    private final Bitmap bmpBlockOff, bmpBlockOn;
    private final int[] posRowArray, posColArray;
    private final int ROWS, COLS = 7;

    public MainScreenBackground( final Bitmap bmpBlockOff , final Bitmap bmpBlockOn ) {

        final SurfaceData surfaceData = new SurfaceData( COLS );
        ROWS = surfaceData.rows;

        posRowArray = getPositionArray( ROWS , surfaceData.tileSize , surfaceData.marginY );
        posColArray = getPositionArray( COLS , surfaceData.tileSize , surfaceData.marginX );

        this.bmpBlockOff = BitmapManager.scaleTileBitmap( bmpBlockOff , surfaceData.tileSize );
        this.bmpBlockOn = BitmapManager.scaleTileBitmap( bmpBlockOn , surfaceData.tileSize );

        backgroundMatrix = new BackgroundMatrix( ROWS , COLS );
        backgroundAnimations = new BackgroundAnimations( backgroundMatrix );
    }

    private int[] getPositionArray( final int length , final int size , final int margin ) {
        final int[] positionArray = new int[ length ];
        for ( int idx = 0 ; idx < length ; idx++ ) {
            positionArray[ idx ] = margin + idx * size;
        }
        return positionArray;
    }

    public void setMainScreenThread( final MainScreenThread mainScreenThread ) {
        backgroundAnimations.setMainScreenThread( mainScreenThread );
    }

    public void reset() {
        backgroundMatrix.fillAll( false );
    }

    public void draw( final Canvas canvas ) {
        for ( int row = 0 ; row < ROWS ; row++ ) {
            final int posY = posRowArray[ row ];
            for ( int col = 0 ; col < COLS ; col++ ) {
                final Bitmap bitmap = backgroundMatrix.matrix[ row ][ col ] ? bmpBlockOn : bmpBlockOff;
                canvas.drawBitmap( bitmap , posColArray[ col ] , posY , null );
            }
        }
    }

    public void updateAnimation() {
        backgroundAnimations.updateAnimation();
    }
}