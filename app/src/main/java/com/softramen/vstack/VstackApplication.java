package com.softramen.vstack;

import android.app.Application;
import android.content.Context;

import com.softramen.admobManager.AdMobInitializer;
import com.softramen.constants.TileType;
import com.softramen.introFactory.IntroManager;
import com.softramen.settingsManager.SettingsPreferences;
import com.softramen.sounds.Sounds;
import com.softramen.vstack.inGame.animatedtiles.IterationSequences;
import com.softramen.vstack.utils.UpdateManager;

public class VstackApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        final Context context = this;

        AdMobInitializer.init( context );

        SettingsPreferences.init( context );
        GamePreferences.init( context );
        Sounds.init( context );

        final SettingsPreferences settingsPreferences = SettingsPreferences.getInstance();
        final Sounds sounds = Sounds.getInstance();
        sounds.setPlayerState( settingsPreferences.getSoundPlayerState() );
        sounds.setInterfaceState( settingsPreferences.getSoundInterfaceState() );

        IntroManager.init( context );

        TileType.init();
        IterationSequences.init();

        GameTimer.init();

        GameData.init( context );
        UpdateManager.init();
    }
}
