package com.softramen.vstack.inGame;

public class GameThread extends Thread {

    private boolean running = false, mustWait = false, isSleepOneCycle = false;
    private final SharedVar sharedVar;
    private long waitMillis;
    private final int type;

    public GameThread( final SharedVar sharedVar , final int type ) {
        this.sharedVar = sharedVar;
        this.type = type;
    }

    public void stopThread() {
        running = false;
    }

    public void setFPS( final float fps ) {
        waitMillis = ( long ) ( 1000 / fps );
    }

    public void setMustWait() {
        mustWait = true;
    }

    public void setNotify() {
        mustWait = false;
        synchronized( sharedVar ) {
            sharedVar.notify();
        }
    }

    public void setSleepOneCycle() {
        isSleepOneCycle = true;
    }

    @Override
    public void run() {
        long startTime, sleepTime;
        running = true;
        while ( running ) {

            if ( isSleepOneCycle ) sleepOneCycle();

            startTime = System.currentTimeMillis();

            try {
                synchronized( sharedVar ) {
                    if ( type == Type.PLAYER ) {
                        if ( mustWait ) {
                            sharedVar.wait();
                            continue;
                        }
                        sharedVar.updatePlayerLogic();
                        sharedVar.drawPlayer();
                    } else if ( type == Type.ANIMATIONS ) {
                        sharedVar.updateAnimatedItemsLogic();
                        sharedVar.drawAnimatedItems();
                    }
                }
            } catch ( final InterruptedException interruptedExceptionA ) {
                // Log.d( TAG , "run > Synchronized : " + interruptedExceptionA );
            }

            sleepTime = waitMillis - ( System.currentTimeMillis() - startTime );

            if ( interrupted() ) continue;
            try {
                sleep( ( sleepTime > 0 ) ? sleepTime : 10 );
            } catch ( final InterruptedException interruptedExceptionB ) {
                // Log.d( TAG , "run > Sleep : " + interruptedExceptionB + " | Type : " + type );
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    private void sleepOneCycle() {
        try {
            isSleepOneCycle = false;
            sleep( waitMillis );
        } catch ( final InterruptedException ignored ) {
        }
    }

    public static class Type {
        public final static int PLAYER = 0;
        public final static int ANIMATIONS = 1;
    }
}
