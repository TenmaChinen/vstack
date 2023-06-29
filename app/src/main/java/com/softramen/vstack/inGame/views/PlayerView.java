package com.softramen.vstack.inGame.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.softramen.constants.TileType;
import com.softramen.sounds.Sounds;
import com.softramen.vstack.GameTimer;
import com.softramen.vstack.inGame.DynamicBackground;
import com.softramen.vstack.inGame.GameThread;
import com.softramen.vstack.inGame.SharedVar;
import com.softramen.vstack.inGame.player.Player;
import com.softramen.vstack.inGame.player.PlayerAnimations;
import com.softramen.vstack.utils.GameDifficulty;
import com.softramen.vstack.utils.LevelData;

@SuppressLint ( "ViewConstructor" )
public class PlayerView extends View implements PlayerAnimations.Callback {

    private final Sounds sounds = Sounds.getInstance();
    private final GameTimer gameTimer = GameTimer.getInstance();
    private final PlayerAnimations playerAnimations;
    private final SharedVar sharedVar;

    private boolean isTap = false, isLevelAnimated = false;
    private DynamicBackground dynamicBackground;
    private StackPlayerView stackPlayerView;
    private GameThread playerThread;
    private Callback callback;
    private Player player;

    public interface Callback {
        void onPlayerDead();
        void onPlayerWins();
    }

    public PlayerView( final Context context , final SharedVar sharedVar ) {
        super( context );
        playerAnimations = new PlayerAnimations( context );
        playerAnimations.setCallback( this );
        this.sharedVar = sharedVar;
    }

    public void setData( final Bitmap playerBitmap , final LevelData levelData , final DynamicBackground dynamicBackground , final StackPlayerView stackPlayerView , final int difficulty ) {
        final float speedMultiplier = GameDifficulty.getSpeedMultiplier( difficulty );
        player = new Player( playerBitmap , levelData , speedMultiplier );
        playerAnimations.setBitmap( playerBitmap );
        this.dynamicBackground = dynamicBackground;
        this.stackPlayerView = stackPlayerView;
        isLevelAnimated = levelData.isAnimated;
    }

    public void setCallback( final Callback callback ) {
        this.callback = callback;
    }

    public ImageView getPlayerImageView() {
        return playerAnimations.getImageView();
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void onDraw( final Canvas canvas ) {
        super.onDraw( canvas );
        player.draw( canvas );
    }

    @Override
    public boolean onTouchEvent( final MotionEvent motionEvent ) {
        if ( playerThread != null && playerThread.isRunning() && ( motionEvent.getAction() & MotionEvent.ACTION_MASK ) == MotionEvent.ACTION_DOWN ) {
            isTap = true;
            playerThread.interrupt(); // To skip sleep & directly check the logic
        }
        return true;
    }

    // Automation Purposes
    public void performTouch() {
        final MotionEvent event = MotionEvent.obtain( SystemClock.uptimeMillis() , SystemClock.uptimeMillis() , MotionEvent.ACTION_DOWN , getX() , getY() , 0 );
        dispatchTouchEvent( event );
        event.recycle();
    }

    // Needed in case Activity is not able to stop the Thread
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if ( playerThread != null ) {
            playerThread.stopThread();
        }
    }

    public void onTapLogic() {
        if ( playerThread != null && playerThread.isRunning() ) {
            if ( player.isValidCol() ) {

                if ( isLevelAnimated ) {
                    if ( dynamicBackground.isAnimatedTile( player.getRow() , player.getCol() ) ) {
                        sharedVar.disableAnimatedTile( player.getRow() , player.getCol() );
                    }
                }

                if ( player.isPlayerLastRow() ) { // W I N
                    playerThread.stopThread();
                    gameTimer.stop();
                    if ( isLevelAnimated ) sharedVar.destroyAnimatedItemsViewThread();

                    stackPlayerView.setTile( player.getRow() , player.getCol() , TileType.ON );
                    playerAnimations.startWinAnimation( player.getPosX() , player.getPosY() );
                    sounds.playWin();
                } else { // S T A C K
                    final int tileId = dynamicBackground.getTile( player.getRow() , player.getCol() );
                    stackPlayerView.setTile( player.getRow() , player.getCol() , tileId );

                    player.increaseRow();
                    player.setCol( dynamicBackground.getRandomAvailableCol( player.getRow() ) );
                    player.setRandomDirection();
                    postInvalidate();

                    playerThread.setFPS( player.getSpeedFPS() );

                    sounds.playStack();
                }
            } else { // D E A D
                if ( isLevelAnimated ) {
                    sharedVar.destroyAnimatedItemsViewThread();
                }
                gameTimer.stop();
                playerThread.stopThread();
                player.setVisibility( false );
                playerAnimations.startDeadAnimation( player.getPosX() , player.getPosY() );
                sounds.playFail();
            }
        }
        isTap = false;
    }

    public void updateLogic() {
        if ( playerThread.isRunning() ) {
            if ( isTap ) onTapLogic();
            else {
                final int currentTile = dynamicBackground.getTile( player.getRow() , player.getCol() );
                final int nextTile = dynamicBackground.getTile( player.getRow() , player.getNextCol() );

                player.setVisibility( true );

                // Current Tile Behaviour
                switch ( currentTile ) {
                    case TileType.ARROW_LEFT:
                        if ( player.isDirectionRight() ) player.toggleDirection();
                        break;
                    case TileType.ARROW_RIGHT:
                        if ( player.isDirectionLeft() ) player.toggleDirection();
                        break;
                    case TileType.PORTAL:
                        player.setVisibility( false );
                        playerThread.setMustWait();
                        playerAnimations.startPortalShrinkAnimation( player.getPosX() , player.getPosY() );
                        final int randomCol = dynamicBackground.getRandomAvailableCol( player.getRow() , player.getCol() );
                        player.setCol( randomCol );
                        player.setRandomDirection();
                        sounds.playPortal();
                        return;
                }

                // Next Tile Behaviour
                switch ( nextTile ) {
                    case TileType.OFF:
                    case TileType.ARROW_LEFT:
                    case TileType.ARROW_RIGHT:
                    case TileType.PORTAL:
                        player.step();
                        break;
                    case TileType.WALL:
                        player.toggleDirection();
                        player.step();
                        break;
                    case TileType.VOID:
                        if ( player.isDirectionRight() ) {
                            player.stepToFirstCol();
                        } else if ( player.isDirectionLeft() ) {
                            player.stepToLastCol();
                        }
                        break;
                    case TileType.BROKEN_OFF:
                    case TileType.BROKEN_ON:
                        player.setVisibility( false );
                        player.step();
                        break;
                }

                post( sounds::playStep );
            }
        }
    }

    public void resume() {
        if ( playerThread != null && !playerThread.isRunning() ) {
            resetThread();
            gameTimer.start();
        }
    }

    public void reset() {
        gameTimer.reset();
        player.reset();
    }

    public void retry() {
        player.retry();
    }

    public void pause() {
        if ( playerThread != null && playerThread.isRunning() ) {
            playerThread.stopThread();
        }
    }

    public void resetThread() {
        if ( playerThread != null && playerThread.isRunning() ) {
            playerThread.stopThread();
        }
        playerThread = new GameThread( sharedVar , GameThread.Type.PLAYER );
        playerThread.setFPS( player.getSpeedFPS() );
        playerThread.setSleepOneCycle();
        playerThread.start();
    }

    public void resetTimer() {
        gameTimer.reset();
        gameTimer.start();
    }

    @Override
    public void onWinAnimationEnd() {
        if ( callback != null ) {
            callback.onPlayerWins();
        }
    }

    @Override
    public void onDeadAnimationEnd() {
        player.setVisibility( true );
        invalidate();
        if ( callback != null ) {
            callback.onPlayerDead();
        }
    }

    @Override
    public void onPortalShrinkAnimationEnd() {
        playerAnimations.startPortalUnShrinkAnimation( player.getPosX() , player.getPosY() );
    }

    @Override
    public void onPortalUnShrinkAnimationEnd() {
        player.setVisibility( true );
        postInvalidate();
        playerThread.setNotify();
    }
}