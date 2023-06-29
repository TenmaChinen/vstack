package com.softramen.vstack.inGame.animatedtiles;

class AnimationSchedule {
    private final int[] iterationSequenceArray;
    private int iteration, iterations, cursorSequence, cursorState;
    private final int statesLength;

    public AnimationSchedule( final int[] iterationSequenceArray , final int statesLength ) {
        this.iterationSequenceArray = iterationSequenceArray;
        this.statesLength = statesLength;
        reset();
    }

    public void reset() {
        cursorState = 0;
        cursorSequence = 2 * ( int ) ( Math.random() * iterationSequenceArray.length / 2 );
        //cursorSequence = 0;
        iterations = iterationSequenceArray[ cursorSequence ];
        iteration = 0;
    }

    private void increaseSequenceCursor() {
        cursorSequence = ( cursorSequence + 1 ) % iterationSequenceArray.length;
        iterations = iterationSequenceArray[ cursorSequence ];
    }

    private void increaseStateCursor() {
        cursorState = ( cursorState + 1 ) % statesLength;
    }

    public boolean update() {
        iteration = ( iteration + 1 ) % iterations;
        if ( iteration == 0 ) {
            increaseSequenceCursor();
            increaseStateCursor();
            return true;
        }
        return false;
    }

    public int getCursorState() {
        return cursorState;
    }
}
