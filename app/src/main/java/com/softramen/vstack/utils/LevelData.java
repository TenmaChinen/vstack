package com.softramen.vstack.utils;

import android.util.Log;

import com.softramen.constants.InGame;
import com.softramen.constants.Screen;
import com.softramen.constants.Tags;
import com.softramen.constants.TileType;

import java.util.Locale;

public class LevelData extends SurfaceData {

    public final String id;
    private final int[] speedArray;
    private final int[][] matrix;
    public final boolean isAnimated;

    public final int rows, cols;
    private final int[] posRowArray, posColArray;

    private final TimeRecords timeRecords = new TimeRecords( -1 , -1 , -1 );

    public LevelData( final String id , final int[] speedArray , final int[][] matrix , final boolean isAnimated ) {

        super( matrix.length , matrix[ 0 ].length , Screen.WIDTH , InGame.HEIGHT , InGame.MARGIN_X , InGame.MARGIN_Y );

        this.id = id;
        this.speedArray = speedArray;
        this.matrix = matrix;
        this.isAnimated = isAnimated;

        rows = matrix.length;
        cols = matrix[ 0 ].length;

        posRowArray = getPositions( rows , tileSize , marginY );
        posColArray = getPositions( cols , tileSize , marginX );
    }

    private int[] getPositions( final int length , final int size , final int margin ) {
        final int[] positionArray = new int[ length ];
        for ( int idx = 0 ; idx < length ; idx++ ) {
            positionArray[ idx ] = margin + idx * size;
        }
        return positionArray;
    }

    /*   G E T T E R S   */

    public int[][] getMatrix() {
        return matrix;
    }

    public int[][] getValidMatrix() {
        final int[][] validMatrix = new int[ rows ][ cols ];
        for ( int row = 0 ; row < rows ; row++ ) {
            for ( int col = 0 ; col < cols ; col++ ) {
                final int tileId = matrix[ row ][ col ];
                validMatrix[ row ][ col ] = ( tileId == 1 ) ? 0 : tileId;
            }
        }
        return validMatrix;
    }

    public int[][] getNonAnimatedMatrix() {
        final int[][] nonAnimatedMatrix = new int[ rows ][ cols ];
        for ( int row = 0 ; row < rows ; row++ ) {
            for ( int col = 0 ; col < cols ; col++ ) {
                final int tileId = matrix[ row ][ col ];
                switch ( tileId ) {
                    case TileType.ON:
                    case TileType.PORTAL:
                        nonAnimatedMatrix[ row ][ col ] = TileType.OFF;
                        break;
                    case TileType.OFF:
                    case TileType.WALL:
                        nonAnimatedMatrix[ row ][ col ] = tileId;
                        break;
                    default:
                        nonAnimatedMatrix[ row ][ col ] = TileType.VOID;
                }
            }
        }
        return nonAnimatedMatrix;
    }

    public int[] getSpeedArray() {
        return speedArray;
    }

    public int getCenter() {
        int tile, col;
        col = cols / 2;
        tile = matrix[ getFirstRow() ][ col ];
        while ( tile != TileType.OFF && tile != TileType.ON ) {
            col = ( int ) ( Math.random() * ( cols - 1 ) );
            tile = matrix[ getFirstRow() ][ col ];
            if ( tile == TileType.OFF ) {
                return col;
            }
        }
        return col;
    }

    public int getFirstRow() {
        return rows - 1;
    }

    public int getLastRow() {
        return 0;
    }

    public int getFirstCol() {
        return 0;
    }

    public int getLastCol() {
        return cols - 1;
    }

    public int[] getPosRowArray() {
        return posRowArray;
    }

    public int[] getPosColArray() {
        return posColArray;
    }

    public boolean isBeaten( final int difficulty ) {
        return timeRecords.getTimeRecord( difficulty ) != -1;
    }

    /*   S E T T E R S   */

    public void setRecordTimes( final int[] timeRecordArray ) {
        timeRecords.setTimeRecordArray( timeRecordArray );
    }

    public TimeRecords getTimeRecords() {
        return timeRecords;
    }

    public static class TimeRecords {
        private final int[] timeRecordArray;

        public TimeRecords( final int timeEasy , final int timeMedium , final int timeHard ) {
            this.timeRecordArray = new int[]{ timeEasy , timeMedium , timeHard };
        }

        public int getTimeRecord( final int difficulty ) {
            return timeRecordArray[ difficulty ];
        }

        public int[] getArray() {
            return timeRecordArray;
        }

        /*   S E T T E R S   */

        public void setTimeRecordArray( final int[] timeRecordArray ) {
            if ( timeRecordArray == null ) {
                Log.d( Tags.ERROR , "TimeRecords > setTimeRecordArray > timeRecordArray is NULL" );
            } else {
                System.arraycopy( timeRecordArray , 0 , this.timeRecordArray , 0 , timeRecordArray.length );
            }
        }

        public void setTimeRecord( final int difficulty , final int timeRecord ) {
            timeRecordArray[ difficulty ] = timeRecord;
        }

        public String[] getFormattedTimeRecords() {
            final String formattedTimeEasy = formatTime( timeRecordArray[ GameDifficulty.EASY ] );
            final String formattedTimeMedium = formatTime( timeRecordArray[ GameDifficulty.MEDIUM ] );
            final String formattedTimeHard = formatTime( timeRecordArray[ GameDifficulty.HARD ] );
            return new String[]{ formattedTimeEasy , formattedTimeMedium , formattedTimeHard };
        }

        public String formatTime( int totalSeconds ) {
            if ( totalSeconds >= 0 ) {
                totalSeconds = Math.min( totalSeconds , 3599 );
                final int minutes = totalSeconds / 60;
                final int seconds = totalSeconds % 60;
                return String.format( Locale.CANADA , "%02d:%02d" , minutes , seconds );
            } else {
                return "--:--";
            }
        }
    }
}
