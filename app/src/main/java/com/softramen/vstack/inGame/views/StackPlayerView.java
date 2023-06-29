package com.softramen.vstack.inGame.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

import com.softramen.constants.TileType;
import com.softramen.vstack.utils.LevelData;

public class StackPlayerView extends View {
    private int[] posRowArray, posColArray;
    private Bitmap[] tileBitmapArray;
    private int rows, cols;
    private int[][] matrix;

    public StackPlayerView( final Context context ) {
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
        rows = levelData.rows;
        cols = levelData.cols;
        posRowArray = levelData.getPosRowArray();
        posColArray = levelData.getPosColArray();
        matrix = new int[ rows ][ cols ];
        reset();
    }

    public void reset() {
        for ( int row = 0 ; row < rows ; row++ ) {
            for ( int col = 0 ; col < cols ; col++ ) {
                matrix[ row ][ col ] = TileType.VOID;
            }
        }
    }

    public void setTile( final int row , final int col , final int tileId ) {
        switch ( tileId ) {
            case TileType.BROKEN_OFF:
            case TileType.BROKEN_ON:
                matrix[ row ][ col ] = TileType.BROKEN_ON;
                break;
            default:
                matrix[ row ][ col ] = TileType.ON;
        }
        postInvalidate();
    }
}
