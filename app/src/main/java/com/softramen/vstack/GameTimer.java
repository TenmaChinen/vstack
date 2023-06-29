package com.softramen.vstack;

public class GameTimer {

    public static GameTimer instance;

    private long elapsedTime = 0;
    private long startTime = 0;

    public static synchronized void init() {
        if ( instance == null ) {
            instance = new GameTimer();
        }
    }

    public static synchronized GameTimer getInstance() {
        return instance;
    }

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public void stop() {
        elapsedTime += ( System.currentTimeMillis() - startTime );
    }

    public void reset() {
        elapsedTime = 0;
    }

    public int getElapsedTime() {
        return ( int ) ( elapsedTime / 1000 );
    }
}
