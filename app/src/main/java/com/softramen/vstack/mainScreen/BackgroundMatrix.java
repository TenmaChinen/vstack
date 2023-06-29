package com.softramen.vstack.mainScreen;

public class BackgroundMatrix {
    public final int ROWS, COLS;
    public final boolean[][] matrix;

    public BackgroundMatrix( final int rows , final int cols ) {
        ROWS = rows;
        COLS = cols;
        matrix = createMatrix();
    }

    private boolean[][] createMatrix() {
        final boolean[][] m = new boolean[ ROWS ][ COLS ];
        for ( int row = 0 ; row < ROWS ; row++ ) {
            for ( int col = 0 ; col < COLS ; col++ ) {
                m[ row ][ col ] = false;
            }
        }
        return m;
    }

    public void fillAll( final boolean value ) {
        for ( int row = 0 ; row < ROWS ; row++ ) {
            for ( int col = 0 ; col < COLS ; col++ ) {
                matrix[ row ][ col ] = value;
            }
        }
    }

    public void fillRow( final int row , final boolean value ) {
        for ( int col = 0 ; col < COLS ; col++ ) {
            matrix[ row ][ col ] = value;
        }
    }

    public void fillCol( final int col , final boolean value ) {
        for ( int row = 0 ; row < ROWS ; row++ ) {
            matrix[ row ][ col ] = value;
        }
    }

    public void fillAltRows( final boolean odd , final boolean value ) {
        for ( int row = odd ? 0 : 1 ; row < ROWS ; row += 2 ) {
            fillRow( row , value );
        }
    }

    public void fillAltCols( final boolean odd , final boolean value ) {
        for ( int col = odd ? 0 : 1 ; col < COLS ; col++ ) {
            fillCol( col , value );
        }
    }

    public void fillAltRow( final int col , final boolean odd , final boolean value ) {
        for ( int row = odd ? 0 : 1 ; row < ROWS ; row += 2 ) {
            matrix[ row ][ col ] = value;
        }
    }

    public void fillAltCol( final int row , final boolean odd , final boolean value ) {
        for ( int col = odd ? 0 : 1 ; col < COLS ; col += 2 ) {
            matrix[ row ][ col ] = value;
        }
    }
}
