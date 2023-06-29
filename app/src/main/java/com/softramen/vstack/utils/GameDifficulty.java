package com.softramen.vstack.utils;

public class GameDifficulty {
    public static final int EASY = 0;
    public static final int MEDIUM = 1;
    public static final int HARD = 2;

    public static int getDrawableResourceId( final int difficulty ) {
        switch ( difficulty ) {
            case EASY:
                return com.softramen.buttons.R.drawable.lbl_easy;
            case MEDIUM:
                return com.softramen.buttons.R.drawable.lbl_medium;
            case HARD:
                return com.softramen.buttons.R.drawable.lbl_hard;
            default:
                return -1;
        }
    }

    public static int getNext( final int difficulty ) {
        return ( difficulty + 1 ) % 3;
    }

    public static float getSpeedMultiplier( final int difficulty ) {
        switch ( difficulty ) {
            case MEDIUM:
                return 1.5f;
            case HARD:
                return 2.0f;
            default:
                return 1.0f;
        }
    }
}
