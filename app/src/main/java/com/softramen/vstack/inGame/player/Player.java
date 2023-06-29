package com.softramen.vstack.inGame.player;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.softramen.settingsManager.SettingsPreferences;
import com.softramen.vstack.BuildConfig;
import com.softramen.vstack.utils.LevelData;

public class Player {
    public static final int LEFT = -1;
    public static final int RIGHT = 1;

    private final int firstRow, lastCol, center;
    private final int[] levelSpeedArray;
    private final Bitmap bitmap;

    private int posX = 0, posY = 0, direction = 1;
    private int row, col, startCol = -2;
    private boolean isVisible = true;
    private final int[] posRowArray, posColArray;
    private final float speedMultiplier;

    public Player( final Bitmap bitmap , final LevelData levelData , final float speedMultiplier ) {
        firstRow = levelData.getFirstRow();
        lastCol = levelData.getLastCol();

        center = levelData.getCenter();
        levelSpeedArray = levelData.getSpeedArray();
        row = firstRow;
        col = center;
        this.speedMultiplier = speedMultiplier;

        this.bitmap = bitmap;
        posRowArray = getPositions( levelData.tileSize , levelData.marginY );
        posColArray = getPositions( levelData.tileSize , levelData.marginX );
        reset();
    }

    private int[] getPositions( final int size , final int margin ) {
        final int[] positions = new int[ size ];
        for ( int idx = 0 ; idx < size ; idx++ ) {
            positions[ idx ] = margin + idx * size;
        }
        return positions;
    }

    public void reset() {
        startCol = -2;
        setCol( center );
        if ( BuildConfig.DEBUG ) setRow( SettingsPreferences.getInstance().getPlayerStartRow() );
        else setRow( firstRow );
        setRandomDirection();
    }

    public void retry() {
        setRandomDirection();
    }

    /*   S E T T E R S   */

    public void setDirection( final int direction ) {
        this.direction = direction;
    }

    public void setRow( final int row ) {
        this.row = ( row != -1 ) ? row : firstRow;
        posY = posRowArray[ this.row ];
    }

    public void setCol( final int col ) {
        this.col = col;
        posX = posColArray[ col ];
    }

    /*   G E T T E R S   */

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getFirstCol() {
        return 0;
    }

    public int getLastCol() {
        return lastCol;
    }

    public int getFirstRow() {
        return firstRow;
    }

    public int getRightCol() {
        return col + 1;
    }

    public int getLeftCol() {
        return col - 1;
    }

    public int getNextCol() {
        return ( direction == 1 ) ? getRightCol() : getLeftCol();
    }

    /*  V O I D S   */

    public void toggleDirection() {
        direction = -direction;
    }

    public void increaseRow() {
        row--;
        posY = posRowArray[ row ];
        isVisible = true;
    }

    public void step() {
        col = col + direction;
        posX = posColArray[ col ];
    }

    public void stepToFirstCol() {
        setCol( 0 );
    }

    public void stepToLastCol() {
        setCol( lastCol );
    }

    public void setRandomDirection() {
        direction = ( Math.random() > 0.5 ) ? 1 : -1;
    }

    public boolean isDirectionRight() {
        return ( direction == Player.RIGHT );
    }

    public boolean isDirectionLeft() {
        return ( direction == Player.LEFT );
    }

    public boolean isValidCol() {
        if ( startCol == -2 ) {
            startCol = col;
            return true;
        }
        return col == startCol;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public boolean isFirstRow() {
        return row == firstRow;
    }

    public boolean isPlayerLastRow() {
        return row == 0;
    }

    public float getSpeedFPS() {
        /*
            iterations = ( int ) ( SLOPE * speeds[ cursor ] + OFFSET );
            ( Max Iterations - One Iteration ) / ( Speed One - Max Speed )
            private final double SLOPE = ( 20.0 - 1.0 ) / ( 1.0 - 10.0 );
            private final double OFFSET = 1.0 - SLOPE * 10.0;
        */
        return 0.4736842f * levelSpeedArray[ firstRow - row ] * speedMultiplier + 0.5263157f;
    }

    public void setVisibility( final boolean visible ) {
        isVisible = visible;
    }

    public void draw( final Canvas canvas ) {
        if ( isVisible ) {
            canvas.drawBitmap( bitmap , posX , posY , null );
        }
    }
}