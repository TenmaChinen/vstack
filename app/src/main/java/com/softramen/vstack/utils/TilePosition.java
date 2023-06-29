package com.softramen.vstack.utils;

public class TilePosition {
    public final int col, row, x, y;
    private int stateCursor = 0;

    public TilePosition( final int row , final int col , final int y , final int x ) {
        this.col = col;
        this.row = row;
        this.x = x;
        this.y = y;
    }

    public void setSateCursor( final int stateCursor ) {
        this.stateCursor = stateCursor;
    }

    public int getStateCursor() {
        return stateCursor;
    }
}
