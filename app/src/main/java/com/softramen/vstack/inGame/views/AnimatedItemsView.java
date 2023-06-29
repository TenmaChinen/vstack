package com.softramen.vstack.inGame.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

import com.softramen.constants.Tags;
import com.softramen.constants.TileType;
import com.softramen.utils.BitmapFactory;
import com.softramen.vstack.inGame.GameThread;
import com.softramen.vstack.inGame.SharedVar;
import com.softramen.vstack.inGame.animatedtiles.TileAnimations;
import com.softramen.vstack.utils.LevelData;
import com.softramen.vstack.utils.TilePosition;

import java.util.ArrayList;

@SuppressLint ( "ViewConstructor" )
public class AnimatedItemsView extends View {

    private final SharedVar sharedVar;
    private TileAnimations[] tileAnimationsArray;
    private GameThread animationsThread;

    public AnimatedItemsView( final Context context , final SharedVar sharedVar ) {
        super( context );
        this.sharedVar = sharedVar;
    }

    @Override
    public void onDraw( final Canvas canvas ) {
        super.onDraw( canvas );
        if ( tileAnimationsArray != null ) {
            for ( final TileAnimations tileAnimations : tileAnimationsArray ) {
                tileAnimations.draw( canvas );
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if ( animationsThread != null ) {
            animationsThread.stopThread();
        }
    }

    public void setData( final LevelData levelData , final int[][] dynamicMatrix , final Bitmap[] tileBitmapArray ) {
        tileAnimationsArray = createTileAnimationsArray( levelData , tileBitmapArray , dynamicMatrix );
    }

    public void clearData() {
        tileAnimationsArray = null;
        animationsThread = null;
    }

    public void updateLogic() {
        for ( final TileAnimations tileAnimations : this.tileAnimationsArray ) {
            tileAnimations.update();
        }
    }

    public void resume() {
        if ( animationsThread != null && !animationsThread.isRunning() ) {
            resetThread();
        }
    }

    public void pause() {
        if ( animationsThread != null && animationsThread.isRunning() ) {
            animationsThread.stopThread();
        }
    }

    public void reset() {
        for ( final TileAnimations tileAnimations : tileAnimationsArray ) {
            tileAnimations.reset();
        }
    }

    public void resetThread() {
        destroyThread();
        animationsThread = new GameThread( sharedVar , GameThread.Type.ANIMATIONS );
        animationsThread.setFPS( 20 );
        animationsThread.start();
    }

    public void destroyThread() {
        if ( animationsThread != null && animationsThread.isRunning() ) {
            animationsThread.stopThread();
        }
    }

    public void disableTile( final int row , final int col ) {
        for ( final TileAnimations tileAnimations : tileAnimationsArray ) {
            if ( tileAnimations.disableTile( row , col ) ) {
                return;
            }
        }
    }

    /*   U T I L S   */

    private TileAnimations[] createTileAnimationsArray( final LevelData levelData , final Bitmap[] tilesBitmap , final int[][] dynamicMatrix ) {

        final ArrayList<TileAnimations> tileAnimationsArrayList = new ArrayList<>();
        for ( final int tileGroupId : TileType.animatedTilesId ) {
            final TilePosition[] tilePositionArray = createTilePositionArray( tileGroupId , levelData , dynamicMatrix );
            if ( tilePositionArray.length > 0 ) {
                final Bitmap[] groupTileBitmapArray;
                // For non statesId tileGroup create the Bitmap array here
                if ( tileGroupId == TileType.PORTAL ) {
                    groupTileBitmapArray = BitmapFactory.createPortalRotationBitmapArray( tilesBitmap[ TileType.PORTAL ] );
                } else {
                    groupTileBitmapArray = getGroupTileBitmapArray( tileGroupId , tilesBitmap );
                }
                final TileAnimations tileAnimations = new TileAnimations( tileGroupId , tilePositionArray , dynamicMatrix , groupTileBitmapArray );
                tileAnimationsArrayList.add( tileAnimations );
            }
        }
        return tileAnimationsArrayList.toArray( new TileAnimations[ 0 ] );
    }

    private TilePosition[] createTilePositionArray( final int tileGroupId , final LevelData levelData , final int[][] dynamicMatrix ) {
        final int[] posRows = levelData.getPosRowArray();
        final int[] posCols = levelData.getPosColArray();
        final ArrayList<TilePosition> tilePositionArrayList = new ArrayList<>();
        for ( int row = 0 ; row < levelData.rows ; row++ ) {
            for ( int col = 0 ; col < levelData.cols ; col++ ) {
                if ( TileType.isFromGroup( dynamicMatrix[ row ][ col ] , tileGroupId ) ) {
                    tilePositionArrayList.add( new TilePosition( row , col , posRows[ row ] , posCols[ col ] ) );
                }
            }
        }
        return tilePositionArrayList.toArray( new TilePosition[ 0 ] );
    }

    private Bitmap[] getGroupTileBitmapArray( final int tileGroupId , final Bitmap[] tileBitmapArray ) {
        final int[] groupTilesId = TileType.groups.get( tileGroupId );
        if ( groupTilesId != null ) {
            final Bitmap[] groupTileBitmapArray = new Bitmap[ groupTilesId.length ];
            for ( int idx = 0 ; idx < groupTilesId.length ; idx++ ) {
                groupTileBitmapArray[ idx ] = tileBitmapArray[ groupTilesId[ idx ] ];
            }
            return groupTileBitmapArray;
        } else {
            Log.e( Tags.ERROR , "getGroupTilesBitmap > groupTilesId is Null" );
        }
        return new Bitmap[ 0 ];
    }
}
