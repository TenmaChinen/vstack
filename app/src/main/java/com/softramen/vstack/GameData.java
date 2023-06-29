package com.softramen.vstack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ColorMatrixColorFilter;

import com.softramen.constants.Colors;
import com.softramen.constants.Rewards;
import com.softramen.utils.BitmapFactory;
import com.softramen.utils.BitmapManager;
import com.softramen.vstack.utils.FileManager;
import com.softramen.vstack.utils.LevelData;
import com.softramen.vstack.utils.WorldData;

public class GameData {

    private static GameData instance;

    private final GamePreferences gamePreferences = GamePreferences.getInstance();
    public final Bitmap[] tileBitmapArray, tileTintedBitmapArray, bulletTileBitmapArray;
    public final WorldData[] worldDataArray;
    private final int lastWorldIdx;

    private int levelCursor = 0, worldCursor = 0;
    private int[] remainingTotemsArray;
    private int difficulty;

    private GameData( final Context context ) {
        final FileManager fileManager = new FileManager( context );
        worldDataArray = fileManager.getWorldDataArray();
        tileBitmapArray = fileManager.getTileBitmapArray();
        tileTintedBitmapArray = BitmapManager.makeCopy( tileBitmapArray );
        bulletTileBitmapArray = BitmapFactory.createDifficultyTileBitmapArray( fileManager.getTileIconBitmap() );
        lastWorldIdx = worldDataArray.length - 2;
        loadUserData();
    }

    public void loadUserData() {
        gamePreferences.loadPlayerProgress( worldDataArray );
        remainingTotemsArray = gamePreferences.getRemainingTotems();
        difficulty = gamePreferences.getDifficulty();
    }

    public static synchronized void init( final Context context ) {
        if ( instance == null ) {
            instance = new GameData( context );
        }
    }

    public static synchronized GameData getInstance() {
        return instance;
    }

    /*   S E T T E R S   */

    public void setWorldByIdx( final int worldIdx ) {
        worldCursor = worldIdx;
        if ( !getWorldData().isEmpty() ) {
            updateTintedTiles();
        }
    }

    public void setLevelByIdx( final int levelIdx ) {
        levelCursor = levelIdx;
    }

    public void setDifficulty( final int difficulty ) {
        this.difficulty = difficulty;
        gamePreferences.setDifficulty( difficulty );
    }

    public void setEmptyWorldCursor() {
        worldCursor = worldDataArray.length;
    }

    /*   G E T T E R S   */

    public int getWorldIdx() {
        return worldCursor;
    }

    public int getLevelIdx() {
        return levelCursor;
    }

    public int getNextWorldIdx() {
        return worldCursor + 1;
    }

    public int getNextLevelIdx() {
        return levelCursor + 1;
    }

    public String getWorldName() {
        return worldDataArray[ worldCursor ].name;
    }

    public String getWorldName( final int worldIdx ) {
        return worldDataArray[ worldIdx ].name;
    }

    public String[] getWorldNameArray() {
        final String[] worldNameArray = new String[ worldDataArray.length ];
        for ( int idx = 0 ; idx < worldNameArray.length ; idx++ ) worldNameArray[ idx ] = worldDataArray[ idx ].name;
        return worldNameArray;
    }

    public int getLevelNum() {
        return getLevelIdx() + 1;
    }

    public int getNextLevelNum() {
        return getLevelNum() + 1;
    }

    public String getWorldNameByIdx( final int idx ) {
        return worldDataArray[ idx ].name;
    }

    public int getLastWorldIdx() {
        return lastWorldIdx;
    }

    public int getLastLevelIdx() {
        return worldDataArray[ worldCursor ].getLastLevelIdx();
    }

    public WorldData getWorldData() {
        return worldDataArray[ worldCursor ];
    }

    public LevelData getLevelData() {
        return getWorldData().levelDataArray[ levelCursor ];
    }

    public WorldData getWorldDataByIdx( final int worldIdx ) {
        return worldDataArray[ worldIdx ];
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getRemainingTotemsArray() {
        return remainingTotemsArray[ difficulty ];
    }

    public int getLastUnlockedWorldIdx() {
        for ( int idx = 0 ; idx < lastWorldIdx ; idx++ ) {
            if ( !worldDataArray[ idx ].isBeaten( difficulty ) ) return idx;
        }
        return lastWorldIdx;
    }

    public int getLastUnlockedLevelIdx( final int worldIdx ) {
        // Case A : World is Beaten
        // Case B : Level 1 Beaten
        // Case C : Level 1 not beaten
        final WorldData worldData = getWorldDataByIdx( worldIdx );
        if ( worldData.isBeaten( difficulty ) ) return worldData.getLastLevelIdx();
        else if ( worldData.getFirstLevel().isBeaten( difficulty ) ) {
            final LevelData[] levelDataArray = worldData.levelDataArray;
            for ( int idx = 1 ; idx < levelDataArray.length ; idx++ ) {
                if ( !levelDataArray[ idx ].isBeaten( difficulty ) ) return idx;
            }
            return -1;
        } else if ( worldIdx == 0 ) return 0;
        else {
            final WorldData prevWorldData = getWorldDataByIdx( worldIdx - 1 );
            return prevWorldData.isBeaten( difficulty ) ? 0 : -1;
        }
    }

    public Bitmap getDifficultyBullet() {
        return bulletTileBitmapArray[ difficulty ];
    }

    /*   B O O L E A N S   */

    public boolean isLastWorld() {
        return ( worldCursor == lastWorldIdx );
    }

    public boolean isLastLevel() {
        return levelCursor == getWorldData().getLastLevelIdx();
    }

    public boolean isWorldBeaten() {
        return getWorldData().isBeaten( difficulty );
    }

    public boolean isLevelBeaten() {
        return getLevelData().isBeaten( difficulty );
    }

    /*public boolean isFirstWorldStarted() {
        return worldDataArray[ 0 ].getLastUnlockedLevelIdx() > 0;
    }*/

    public boolean isUnlockedLevel( final int worldIdx , final int levelIdx ) {
        if ( worldIdx == 0 && levelIdx == 0 ) return true;
        else if ( levelIdx == 0 ) {
            return worldDataArray[ worldIdx - 1 ].getLastLevel().isBeaten( difficulty );
        } else {
            return worldDataArray[ worldIdx ].levelDataArray[ levelIdx - 1 ].isBeaten( difficulty );
        }
    }

    /*   V O I D S   */

    public void loadNextLevel() {
        if ( !isLastLevel() ) {
            levelCursor++;
        }
    }

    public void loadNextWorld() {
        if ( !isLastWorld() ) {
            setWorldByIdx( worldCursor + 1 );
            levelCursor = 0;
        }
    }

    /*public void updateLastUnlockedLevel() {
        worldDataArray[ worldCursor ].incrementLastUnlockedLevel();
    }*/

    /*public void updateLastUnlockedLevel( final int worldIdx ) {
        worldDataArray[ worldIdx ].incrementLastUnlockedLevel();
    }*/

    public void updatePlayerProgress() {
        gamePreferences.savePlayerProgress( worldDataArray );
    }

    public void updateTintedTiles() {
        final ColorMatrixColorFilter matrixColorFilter = Colors.worldMatrixColorFilterList.get( worldCursor );
        BitmapFactory.colorFilterBitmapArray( tileBitmapArray , tileTintedBitmapArray , matrixColorFilter );
    }

    public void setRemainingTotemsArray( final int remainingTotemsArray ) {
        this.remainingTotemsArray[ difficulty ] = Math.max( 0 , remainingTotemsArray );
        gamePreferences.setRemainingTotems( this.remainingTotemsArray );
    }

    public int getBonusTotems( final boolean isWorldBeaten ) {
        int bonusTotems = Rewards.TOTEMS_PER_DIFFICULTY_ARRAY[ difficulty ];
        if ( isWorldBeaten ) bonusTotems += Rewards.EXTRA_TRIES_WORLD_BEAT_ARRAY[ difficulty ];
        return bonusTotems;
    }

    public void addRemainingTotems( final int totems ) {
        if ( remainingTotemsArray[ difficulty ] < 100 ) {
            remainingTotemsArray[ difficulty ] += totems;
            gamePreferences.setRemainingTotems( remainingTotemsArray );
        }
    }
}