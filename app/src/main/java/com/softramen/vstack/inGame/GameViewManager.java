package com.softramen.vstack.inGame;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.FrameLayout;

import com.softramen.constants.TileType;
import com.softramen.utils.BitmapManager;
import com.softramen.vstack.GameData;
import com.softramen.vstack.inGame.views.AnimatedItemsView;
import com.softramen.vstack.inGame.views.BackgroundView;
import com.softramen.vstack.inGame.views.PlayerView;
import com.softramen.vstack.inGame.views.StackPlayerView;
import com.softramen.vstack.utils.LevelData;

public class GameViewManager implements PlayerView.Callback {

    private final GameData gameData = GameData.getInstance();
    private final StackPlayerView stackPlayerView;
    private final AnimatedItemsView animatedItemsView;
    private final BackgroundView backgroundView;
    private final PlayerView playerView;
    private final SharedVar sharedVar;
    private boolean gameIsOver = true;

    private LevelData levelData;
    private Callback callback;

    public interface Callback {
        void onPlayerDead();
        void onPlayerWins();
    }

    public GameViewManager( final Context context ) {
        sharedVar = new SharedVar();
        backgroundView = new BackgroundView( context );
        playerView = new PlayerView( context , sharedVar );
        playerView.setCallback( this );
        animatedItemsView = new AnimatedItemsView( context , sharedVar );
        stackPlayerView = new StackPlayerView( context );
    }

    public void setCallback( final Callback callback ) {
        this.callback = callback;
    }

    public void addViews( final FrameLayout parent ) {
        parent.addView( backgroundView );
        parent.addView( animatedItemsView );
        parent.addView( playerView );
        parent.addView( playerView.getPlayerImageView() );
        parent.addView( stackPlayerView );
    }

    public void loadLevel() {
        levelData = gameData.getLevelData();

        final Bitmap[] tileBitmapArray = BitmapManager.scaleTileBitmapArray( gameData.tileTintedBitmapArray , levelData.tileSize );
        final DynamicBackground dynamicBackground = new DynamicBackground( levelData );
        final Bitmap playerBitmap = tileBitmapArray[ TileType.ON ];
        final int difficulty = gameData.getDifficulty();

        animatedItemsView.clearData();
        if ( levelData.isAnimated ) {
            animatedItemsView.setData( levelData , dynamicBackground.matrix , tileBitmapArray );
        }

        backgroundView.setData( tileBitmapArray , levelData );
        stackPlayerView.setData( tileBitmapArray , levelData );
        playerView.setData( playerBitmap , levelData , dynamicBackground , stackPlayerView , difficulty );

        sharedVar.setGameViews( playerView , ( levelData.isAnimated ) ? animatedItemsView : null );
    }

    public void startGame() {
        gameIsOver = false;
        playerView.resetThread();
        playerView.resetTimer();

        if ( levelData.isAnimated ) {
            animatedItemsView.resetThread();
        }

        // AUTOMATE
        // playerView.post( playerView::performTouch ); // First Touch
        // playerView.postDelayed( playerView::performTouch, 1000 ); // Second Touch
    }

    public void resumeGame() {
        if ( !gameIsOver ) {
            playerView.resume();
            if ( levelData.isAnimated ) {
                animatedItemsView.resume();
            }
        }
    }

    public void pauseGame() {
        playerView.pause();
        if ( levelData.isAnimated ) {
            animatedItemsView.pause();
        }
    }

    public void restartGame() {
        gameIsOver = false;
        resetGame();
        resumeGame();
    }

    public void retryGame() {
        gameIsOver = false;
        playerView.retry();
        resumeGame();
    }

    public void updateDraws() {
        backgroundView.postInvalidate();
        playerView.postInvalidate();
        stackPlayerView.postInvalidate();
        if ( levelData.isAnimated ) {
            animatedItemsView.postInvalidate();
        }
    }

    private void resetGame() {
        stackPlayerView.reset();
        stackPlayerView.postInvalidate();
        playerView.reset();
        playerView.postInvalidate();
        if ( levelData.isAnimated ) {
            animatedItemsView.reset();
            animatedItemsView.postInvalidate();
        }
    }

    public PlayerView getPlayerView() {
        return playerView;
    }

    // C A L L B A C K S

    @Override
    public void onPlayerDead() {
        gameIsOver = true;
        if ( callback != null ) {
            callback.onPlayerDead();
        }
    }

    @Override
    public void onPlayerWins() {
        gameIsOver = true;
        if ( callback != null ) {
            callback.onPlayerWins();
        }
    }
}
