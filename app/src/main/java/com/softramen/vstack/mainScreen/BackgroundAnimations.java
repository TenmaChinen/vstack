package com.softramen.vstack.mainScreen;

public class BackgroundAnimations {
    private final BackgroundMatrix backgroundMatrix;
    private final FullOff fullOff;
    private final FullFlash fullFlash;
    private final RowSwipe rowSwipe;
    private final ColSwipe colSwipe;
    private final RowsAlternate rowsAlternate;
    private final ColsAlternate colsAlternate;
    private final ColsAlternateSwipe colsAlternateSwipe;
    private final RowsAlternateSwipe rowsAlternateSwipe;


    private MainScreenThread mainScreenThread;
    private int currentAnimation = 0;

    public BackgroundAnimations( final BackgroundMatrix backgroundMatrix ) {
        this.backgroundMatrix = backgroundMatrix;

        fullOff = new FullOff( 2 , 1 , 1 );
        fullFlash = new FullFlash( 2 , 4 , 6 );
        rowSwipe = new RowSwipe( backgroundMatrix.ROWS , 3 , 16 );
        colSwipe = new ColSwipe( backgroundMatrix.COLS , 3 , 16 );
        rowsAlternate = new RowsAlternate( 2 , 4 , 4 );
        colsAlternate = new ColsAlternate( 2 , 4 , 4 );

        colsAlternateSwipe = new ColsAlternateSwipe( backgroundMatrix.ROWS , 4 , 16 );
        rowsAlternateSwipe = new RowsAlternateSwipe( backgroundMatrix.COLS , 4 , 16 );
    }

    public void setMainScreenThread( final MainScreenThread mainScreenThread ) {
        this.mainScreenThread = mainScreenThread;
        currentAnimation = 0;
    }

    public void updateAnimation() {
        final boolean isFinished;
        switch ( currentAnimation ) {
            case AnimationType.FULL_OFF:
                isFinished = fullOff.update();
                break;
            case AnimationType.FULL_FLASH:
                isFinished = fullFlash.update();
                break;
            case AnimationType.ROW_SWIPE:
                isFinished = rowSwipe.update();
                break;
            case AnimationType.COL_SWIPE:
                isFinished = colSwipe.update();
                break;
            case AnimationType.ROWS_ALTERNATE:
                isFinished = rowsAlternate.update();
                break;
            case AnimationType.COLS_ALTERNATE:
                isFinished = colsAlternate.update();
                break;
            case AnimationType.ROWS_ALTERNATE_SWIPE:
                isFinished = rowsAlternateSwipe.update();
                break;
            case AnimationType.COLS_ALTERNATE_SWIPE:
                isFinished = colsAlternateSwipe.update();
                break;

            default:
                isFinished = false;
        }
        if ( isFinished ) {
            setNextAnimation();
        }
    }

    private void setNextAnimation() {
        currentAnimation = ( int ) ( Math.random() * 7 );

        final int fps;
        switch ( currentAnimation ) {
            case AnimationType.FULL_OFF:
                fps = fullOff.fps;
                break;
            case AnimationType.FULL_FLASH:
                fps = fullFlash.fps;
                break;
            case AnimationType.ROW_SWIPE:
                fps = rowSwipe.fps;
                break;
            case AnimationType.COL_SWIPE:
                fps = colSwipe.fps;
                break;
            case AnimationType.ROWS_ALTERNATE:
                fps = rowsAlternate.fps;
                break;
            case AnimationType.COLS_ALTERNATE:
                fps = colsAlternate.fps;
                break;
            case AnimationType.ROWS_ALTERNATE_SWIPE:
                fps = rowsAlternateSwipe.fps;
                break;
            case AnimationType.COLS_ALTERNATE_SWIPE:
                fps = colsAlternateSwipe.fps;
                break;
            default:
                fps = 1;
        }
        mainScreenThread.setFPS( fps );
    }

    class AnimationType {
        final static int FULL_OFF = 0;
        final static int FULL_FLASH = 1;
        final static int ROW_SWIPE = 2;
        final static int COL_SWIPE = 3;
        final static int ROWS_ALTERNATE = 4;
        final static int COLS_ALTERNATE = 5;

        final static int ROWS_ALTERNATE_SWIPE = 6;
        final static int COLS_ALTERNATE_SWIPE = 7;
    }

    class FullOff extends IterationScheduler {
        public FullOff( final int subIterations , final int maxReps , final int fps ) {
            super( subIterations , maxReps , fps );
        }

        @Override
        public boolean update() {
            backgroundMatrix.fillAll( false );
            return super.update();
        }
    }

    class FullFlash extends IterationScheduler {
        public FullFlash( final int subIterations , final int maxReps , final int fps ) {
            super( subIterations , maxReps , fps );
        }

        @Override
        public boolean update() {
            backgroundMatrix.fillAll( subIteration == 1 );
            return super.update();
        }
    }

    class RowsAlternate extends IterationScheduler {
        public RowsAlternate( final int subIterations , final int maxReps , final int fps ) {
            super( subIterations , maxReps , fps );
        }

        @Override
        public boolean update() {
            final boolean state = getIndex() % 2 == 0;
            backgroundMatrix.fillAltRows( false , !state );
            backgroundMatrix.fillAltRows( true , state );
            return super.update();
        }
    }

    class ColsAlternate extends IterationScheduler {
        public ColsAlternate( final int subIterations , final int maxReps , final int fps ) {
            super( subIterations , maxReps , fps );
        }

        @Override
        public boolean update() {
            final boolean state = getIndex() % 2 == 0;
            backgroundMatrix.fillAltCols( false , !state );
            backgroundMatrix.fillAltCols( true , state );
            return super.update();
        }
    }

    class ColsAlternateSwipe extends IterationScheduler {
        private boolean state, side;

        public ColsAlternateSwipe( final int subIterations , final int maxReps , final int fps ) {
            super( subIterations , maxReps , fps );
            state = true;
            side = false;
        }

        @Override
        public boolean update() {
            if ( subIteration == 0 ) {
                final boolean newSide = Math.random() > 0.5;
                if ( newSide == side ) state = !state;
                side = newSide;
            }

            backgroundMatrix.fillAltCol( getIndex() , side , state );
            return super.update();
        }
    }

    class RowsAlternateSwipe extends IterationScheduler {
        private boolean state, side;

        public RowsAlternateSwipe( final int subIterations , final int maxReps , final int fps ) {
            super( subIterations , maxReps , fps );
            state = true;
            side = false;
        }

        @Override
        public boolean update() {
            if ( subIteration == 0 ) {
                final boolean newSide = Math.random() > 0.5;
                if ( newSide == side ) state = !state;
                side = newSide;
            }

            backgroundMatrix.fillAltRow( getIndex() , side , state );
            return super.update();
        }
    }

    class RowSwipe extends IterationScheduler {
        public RowSwipe( final int subIterations , final int maxReps , final int fps ) {
            super( subIterations , maxReps , fps );
        }

        @Override
        public boolean update() {
            backgroundMatrix.fillRow( getIndex() , iteration % 2 == 0 );
            return super.update();
        }
    }

    class ColSwipe extends IterationScheduler {
        public ColSwipe( final int subIterations , final int maxReps , final int fps ) {
            super( subIterations , maxReps , fps );
        }

        @Override
        public boolean update() {
            backgroundMatrix.fillCol( getIndex() , iteration % 2 == 0 );
            return super.update();
        }
    }

    /* ------------------------------------------------------- */

    class IterationScheduler {
        private final int subIterations;
        protected int iterations;
        protected int iteration, subIteration;
        protected boolean direction;
        protected int fps;
        private final int maxReps;

        public IterationScheduler( final int subIterations , final int maxReps , final int fps ) {
            this.subIterations = subIterations;
            this.maxReps = maxReps;
            this.fps = fps;
            reset();
        }

        public void reset() {
            iteration = 0;
            subIteration = 0;
            iterations = 1 + ( int ) ( Math.random() * maxReps );
            direction = Math.random() > 0.5;
        }

        public int getIndex() {
            return direction ? subIteration : ( subIterations - 1 - subIteration );
        }

        public boolean update() {
            subIteration = ( subIteration + 1 ) % subIterations;
            if ( subIteration == 0 ) {
                iteration = ( iteration + 1 ) % iterations;
                if ( iteration == 0 ) {
                    reset();
                    return true;
                }
            }
            return false;
        }
    }
}
