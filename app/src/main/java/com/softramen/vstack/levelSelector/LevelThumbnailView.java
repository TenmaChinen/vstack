package com.softramen.vstack.levelSelector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.View;

import com.softramen.constants.TileType;
import com.softramen.utils.BitmapManager;
import com.softramen.vstack.utils.LevelData;
import com.softramen.vstack.utils.SurfaceData;

class LevelThumbnailView extends View {

    private final Bitmap[] tileScaledBitmapArray;
    private final Paint paint = new Paint();
    private final int rows, cols, tileSize;
    private final int[][] levelMatrix;

    private final SurfaceData surfaceData;

    public LevelThumbnailView( final Context context , final LevelData levelData , final Bitmap[] tileBitmapArray , final int width , final int height , final boolean isUnlocked ) {
        super( context );

        surfaceData = new SurfaceData( levelData.rows , levelData.cols , width , height , 0 , 0 );

        rows = levelData.rows;
        cols = levelData.cols;
        levelMatrix = levelData.getMatrix();
        tileSize = surfaceData.tileSize;
        tileScaledBitmapArray = BitmapManager.scaleTileBitmapArray( tileBitmapArray , surfaceData.tileSize );

        if ( !isUnlocked ) {
            paint.setColorFilter( new PorterDuffColorFilter( Color.GRAY , PorterDuff.Mode.MULTIPLY ) );
        }

    }

    @Override
    protected void onMeasure( final int widthMeasureSpec , final int heightMeasureSpec ) {
        super.onMeasure( widthMeasureSpec , heightMeasureSpec );
        setMeasuredDimension(  surfaceData.surfaceWidth , surfaceData.surfaceHeight  );
    }

    @Override
    protected void onDraw( final Canvas canvas ) {
        super.onDraw( canvas );

        for ( int row = 0 ; row < rows ; row++ ) {
            for ( int col = 0 ; col < cols ; col++ ) {
                final int tileCode = levelMatrix[ row ][ col ];
                final Bitmap bitmap = tileScaledBitmapArray[ tileCode ];
                if ( tileCode == TileType.PORTAL ) {
                    canvas.drawBitmap( tileScaledBitmapArray[ TileType.OFF ] , col * tileSize , row * tileSize , paint );
                }
                canvas.drawBitmap( bitmap , col * tileSize , row * tileSize , paint );
            }
        }
    }
}