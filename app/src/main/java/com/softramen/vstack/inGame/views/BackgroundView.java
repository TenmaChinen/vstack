package com.softramen.vstack.inGame.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

import com.softramen.constants.TileType;
import com.softramen.vstack.utils.LevelData;

public class BackgroundView extends View {
    private int[] posRowArray, posColArray;
    private Bitmap[] tileBitmapArray;
    private int[][] matrix;
    private int rows, cols;

    public BackgroundView( final Context context ) {
        super( context );
    }

    @Override
    public void onDraw( final Canvas canvas ) {
        super.onDraw( canvas );
        if ( matrix == null ) return;

        for ( int row = 0 ; row < rows ; row++ ) {
            final int posY = posRowArray[ row ];
            for ( int col = 0 ; col < cols ; col++ ) {
                final int tileCode = matrix[ row ][ col ];
                if ( tileCode == TileType.VOID ) continue;
                canvas.drawBitmap( tileBitmapArray[ tileCode ] , posColArray[ col ] , posY , null );
            }
        }
    }

    public void setData( final Bitmap[] tileBitmapArray , final LevelData levelData ) {
        this.tileBitmapArray = tileBitmapArray;
        matrix = levelData.getNonAnimatedMatrix();

        rows = levelData.rows;
        cols = levelData.cols;

        posRowArray = levelData.getPosRowArray();
        posColArray = levelData.getPosColArray();
    }
}
