package com.softramen.vstack.inGame.animatedtiles;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.softramen.constants.TileType;
import com.softramen.vstack.utils.TilePosition;

import java.util.Arrays;

public class TileAnimations {
    private final AnimationSchedule[] animationScheduleArray;
    private final TilePosition[] tilePositionArray;
    private final boolean[] tileAvailabilityArray;
    private final boolean noTileGroupStatesId;
    private final Bitmap[] stateBitmapArray;
    private final int[] tileStateIdArray;
    private final int[][] dynamicMatrix;

    public TileAnimations( final int tileGroupId , final TilePosition[] tilePositionArray , final int[][] dynamicMatrix , final Bitmap[] stateBitmapArray ) {
        this.tilePositionArray = tilePositionArray;
        this.dynamicMatrix = dynamicMatrix;
        this.stateBitmapArray = stateBitmapArray;

        final int[] tileStateIdArray = TileType.groups.get( tileGroupId );
        noTileGroupStatesId = ( tileStateIdArray == null );

        if ( noTileGroupStatesId ) {
            // Use range to define the animation frame
            this.tileStateIdArray = new int[ stateBitmapArray.length ];
            for ( int idx = 0 ; idx < stateBitmapArray.length ; idx++ ) this.tileStateIdArray[ idx ] = idx;
        } else {
            this.tileStateIdArray = tileStateIdArray;
        }

        final int[] iterationSequenceArray = IterationSequences.sequences.get( tileGroupId );
        animationScheduleArray = createAnimationScheduleArray( iterationSequenceArray );
        tileAvailabilityArray = createTilesAvailabilityArray();
    }

    public void update() {
        for ( int idx = 0 ; idx < animationScheduleArray.length ; idx++ ) {
            if ( tileAvailabilityArray[ idx ] ) {
                if ( animationScheduleArray[ idx ].update() ) {
                    final TilePosition tilePos = tilePositionArray[ idx ];
                    final int cursorState = animationScheduleArray[ idx ].getCursorState();
                    tilePos.setSateCursor( cursorState );
                    if ( !noTileGroupStatesId ) {
                        dynamicMatrix[ tilePos.row ][ tilePos.col ] = tileStateIdArray[ cursorState ];
                    }
                }
            }
        }
    }

    public void draw( final Canvas canvas ) {
        for ( int idx = 0 ; idx < tilePositionArray.length ; idx++ ) {
            if ( tileAvailabilityArray[ idx ] ) {
                final TilePosition tilePos = tilePositionArray[ idx ];
                canvas.drawBitmap( stateBitmapArray[ tilePos.getStateCursor() ] , tilePos.x , tilePos.y , null );
            }
        }
    }

    public boolean disableTile( final int row , final int col ) {
        for ( int idx = 0 ; idx < tileAvailabilityArray.length ; idx++ ) {
            if ( tileAvailabilityArray[ idx ] ) {
                if ( tilePositionArray[ idx ].row == row && tilePositionArray[ idx ].col == col ) {
                    tileAvailabilityArray[ idx ] = false;
                    return true;
                }
            }
        }
        return false;
    }

    public void reset() {
        Arrays.fill( tileAvailabilityArray , true );
    }

    private AnimationSchedule[] createAnimationScheduleArray( final int[] iterationSequenceArray ) {
        final AnimationSchedule[] animationScheduleArray;
        animationScheduleArray = new AnimationSchedule[ tilePositionArray.length ];
        for ( int idx = 0 ; idx < animationScheduleArray.length ; idx++ ) {
            animationScheduleArray[ idx ] = new AnimationSchedule( iterationSequenceArray , tileStateIdArray.length );
        }
        return animationScheduleArray;
    }

    private boolean[] createTilesAvailabilityArray() {
        final boolean[] visibilityArray = new boolean[ tilePositionArray.length ];
        Arrays.fill( visibilityArray , true );
        return visibilityArray;
    }
}
