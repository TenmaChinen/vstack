package com.softramen.vstack.inGame;

import com.softramen.vstack.inGame.views.AnimatedItemsView;
import com.softramen.vstack.inGame.views.PlayerView;

public class SharedVar {

    private PlayerView playerView;
    private AnimatedItemsView animatedItemsView;

    public void setGameViews( final PlayerView playerView , final AnimatedItemsView animatedItemsView ) {
        this.playerView = playerView;
        this.animatedItemsView = animatedItemsView;
    }

    public void updatePlayerLogic() {
        playerView.updateLogic();
    }

    public void updateAnimatedItemsLogic() {
        animatedItemsView.updateLogic();
    }

    public void drawPlayer() {
        playerView.postInvalidate();
    }

    public void drawAnimatedItems() {
        animatedItemsView.postInvalidate();
    }

    public void disableAnimatedTile( final int row , final int col ) {
        animatedItemsView.disableTile( row , col );
    }

    public void destroyAnimatedItemsViewThread() {
        animatedItemsView.destroyThread();
    }
}
