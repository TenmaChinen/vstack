package com.softramen.sounds;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import com.softramen.sounds.R;

public class Sounds {

    private final int PRIORITY = 1, RATE = 1, LOOP = 0;
    private final float LEFT_SOUND = 1.0f, RIGHT_SOUND = 1.0f;

    private boolean isPlayerActive = false;
    private boolean isInterfaceActive = false;

    private final SoundPool soundPool;
    private final int POP, RE_POP, PORTAL_SHRINK;
    private final int STACK, FAIL, WIN, PRESS, REPRESS, ERROR, CANCEL, APPEAR;

    private static Sounds instance;

    private Sounds( final Context context ) {
        // Deprecated in API 21
        // soundPool = new SoundPool( MAX_STREAMS, AudioManager.STREAM_MUSIC, 0 );
        final AudioAttributes audioAttributes;

        final AudioAttributes.Builder audioAttributesBuilder = new AudioAttributes.Builder();
        audioAttributesBuilder.setContentType( AudioAttributes.CONTENT_TYPE_SONIFICATION );
        audioAttributesBuilder.setUsage( AudioAttributes.USAGE_GAME );
        audioAttributes = audioAttributesBuilder.build();

        final int MAX_STREAMS = 2;
        final SoundPool.Builder soundPoolBuilder = new SoundPool.Builder();
        soundPoolBuilder.setMaxStreams( MAX_STREAMS );
        soundPoolBuilder.setAudioAttributes( audioAttributes );
        soundPool = soundPoolBuilder.build();

        POP = soundPool.load( context , R.raw.raw_pop , PRIORITY );
        STACK = soundPool.load( context , R.raw.raw_stack , PRIORITY );
        FAIL = soundPool.load( context , R.raw.raw_fail , PRIORITY );
        WIN = soundPool.load( context , R.raw.raw_win , PRIORITY );
        PRESS = soundPool.load( context , R.raw.raw_press , PRIORITY );
        REPRESS = soundPool.load( context , R.raw.raw_repress , PRIORITY );
        ERROR = soundPool.load( context , R.raw.raw_error , PRIORITY );
        CANCEL = soundPool.load( context , R.raw.raw_cancel , PRIORITY );
        APPEAR = soundPool.load( context , R.raw.raw_appear , PRIORITY );
        RE_POP = soundPool.load( context , R.raw.raw_re_pop , PRIORITY );
        PORTAL_SHRINK = soundPool.load( context , R.raw.raw_portal , PRIORITY );
    }

    public static synchronized void init( final Context context ) {
        if ( instance == null ) {
            instance = new Sounds( context );
        }
    }

    public static synchronized Sounds getInstance() {
        return instance;
    }

    //   I N   G A M E   S O U N D S

    public void playStep() {
        playInGameSound( POP );
    }

    public void playStack() {
        playInGameSound( STACK );
    }

    public void playFail() {
        playInGameSound( FAIL );
    }

    public void playWin() {
        playInGameSound( WIN );
    }

    public void playPortal() {
        playInGameSound( PORTAL_SHRINK );
    }

    //   I N T E R F A C E   S O U N D S

    public void playPress() {
        playInterfaceSound( PRESS );
    }

    public void playError() {
        playInterfaceSound( ERROR );
    }

    public void playCancel() {
        playInterfaceSound( CANCEL );
    }

    public void playRepress() {
        playInterfaceSound( REPRESS );
    }

    public void playAppear() {
        playInterfaceSound( APPEAR );
    }

    public void playPop() {
        playInterfaceSound( POP );
    }

    public void playRePop() {
        playInterfaceSound( RE_POP );
    }

    //

    private void playInGameSound( final int soundId ) {
        if ( isPlayerActive ) {
            soundPool.play( soundId , LEFT_SOUND , RIGHT_SOUND , PRIORITY , LOOP , RATE );
        }
    }

    private void playInterfaceSound( final int soundId ) {
        if ( isInterfaceActive ) {
            soundPool.play( soundId , LEFT_SOUND , RIGHT_SOUND , PRIORITY , LOOP , RATE );
        }
    }

    //   S E T T E R S

    public void setPlayerState( final boolean state ) {
        isPlayerActive = state;
    }

    public void setInterfaceState( final boolean state ) {
        isInterfaceActive = state;
    }

    //   G E T T E R S

    public boolean isPlayerActive() {
        return isPlayerActive;
    }

    public boolean isInterfaceActive() {
        return isInterfaceActive;
    }
}
