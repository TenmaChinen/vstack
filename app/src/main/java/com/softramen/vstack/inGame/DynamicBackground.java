package com.softramen.vstack.inGame;

import com.softramen.constants.TileType;
import com.softramen.vstack.utils.LevelData;

public class DynamicBackground {

    public final int[] posRowArray, posColArray;
    private final int[][] originalMatrix;
    private final int cols, rows;
    public final int[][] matrix;

    public DynamicBackground( final LevelData levelData ) {
        originalMatrix = levelData.getValidMatrix();
        rows = levelData.rows;
        cols = levelData.cols;

        posRowArray = levelData.getPosRowArray();
        posColArray = levelData.getPosColArray();

        matrix = new int[ rows ][ cols ];
        reset();
    }

    public void reset() {
        for ( int row = 0 ; row < rows ; row++ ) {
            if ( cols >= 0 ) System.arraycopy( originalMatrix[ row ] , 0 , matrix[ row ] , 0 , cols );
        }
    }

    public void setTile( final int row , final int col , final int code ) {
        matrix[ row ][ col ] = code;
    }

    public int getTile( final int row , final int col ) {
        if ( ( col == cols ) || ( col == -1 ) ) return -1;
        else return matrix[ row ][ col ];
    }

    public int getRandomAvailableCol( final int row ) {
        while ( true ) {
            final int col = ( int ) ( Math.random() * ( cols - 1 ) );
            final int tile = getTile( row , col );
            if ( tile == TileType.OFF ) {
                return col;
            }
        }
    }

    public int getRandomAvailableCol( final int row , final int excludedCol ) {
        while ( true ) {
            final int col = ( int ) ( Math.random() * ( cols - 1 ) );
            if ( col == excludedCol ) continue;
            final int tile = getTile( row , col );
            if ( tile == TileType.OFF ) {
                return col;
            }
        }
    }

    public boolean isAnimatedTile( final int row , final int col ) {
        switch ( matrix[ row ][ col ] ) {
            case TileType.ARROW_LEFT:
            case TileType.ARROW_RIGHT:
            case TileType.BROKEN_OFF:
            case TileType.BROKEN_ON:
            case TileType.PORTAL:
                return true;
            default:
                return false;
        }
    }
}
