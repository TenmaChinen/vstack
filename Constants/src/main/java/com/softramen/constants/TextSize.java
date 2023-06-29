package com.softramen.constants;

public class TextSize {
    private final static float BASE_SIZE = Screen.WIDTH * 0.1f;

    public static class LevelSelector{
        public static final int TITLE = ( int )  ( BASE_SIZE * 0.65f );
        public static final int WORLD_NAME = ( int ) ( BASE_SIZE * 0.58f );
        public static final int REMAINING_TOTEMS = ( int ) ( BASE_SIZE * 0.65f );
        public static final int LEVEL_TITLE = ( int ) ( BASE_SIZE * 0.55f );
        public static final int RECORD = ( int ) ( BASE_SIZE * 0.4f );
        // public static final int SOON_MESSAGE = ( int ) ( BASE_SIZE * 0.32f );
        public static final int SOON_MESSAGE = ( int ) ( BASE_SIZE * 0.75f );
    }

    public static final int ANNOUNCE = ( int ) ( BASE_SIZE * 0.75f );

    public static class DialogRetry {
        public static final int TRY_AGAIN = ( int ) ( BASE_SIZE * 0.9f );
        public static final int TOTEMS_COUNTER = ( int ) ( BASE_SIZE * 0.7f );
        public static final int WATCH_REWARDED_AD = ( int ) ( BASE_SIZE * 0.65f );
    }

    public static class DialogWinner {
        public static final int BONUS_TOTEMS = ( int ) ( BASE_SIZE * 0.7f );
        public static final int BONUS_TOTEMS_PLUS = ( int ) ( BASE_SIZE * 0.75f );
        public static final int WINNER_MESSAGE = ( int ) ( BASE_SIZE * 0.8f );
        public static final int NEW_RECORD_MESSAGE = ( int ) ( BASE_SIZE * 0.95f );
        public static final int CURRENT_RECORD_TIME = ( int ) ( BASE_SIZE * 0.9f );
        public static final int LAST_RECORD_TIME = ( int ) ( BASE_SIZE * 0.65f );
    }

    public static class DialogSettings {
        public static final int LABEL = ( int ) ( BASE_SIZE * 0.5f );
        public static final int BUTTON = ( int ) ( BASE_SIZE * 0.6f );
    }

    public static class DialogWorldSelector {
        public static final int WORLD_NUM = ( int ) ( BASE_SIZE * 0.6f );
        public static final int WORLD_NAME = ( int ) ( BASE_SIZE * 0.55f );
    }

    public static class IntroWidget{
        public static final int INFO = ( int ) ( BASE_SIZE * 0.6f );
    }
}
