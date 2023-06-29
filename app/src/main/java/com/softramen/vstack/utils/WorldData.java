package com.softramen.vstack.utils;

public class WorldData {

    public final String id;
    public final String name;
    public final LevelData[] levelDataArray;

    public WorldData( final String id , final String name , final LevelData[] levelDataArray ) {
        this.id = id;
        this.name = name;
        this.levelDataArray = levelDataArray;
    }

    public LevelData getFirstLevel() {
        return levelDataArray[ 0 ];
    }

    public int getLastLevelIdx() {
        return levelDataArray.length - 1;
    }

    public LevelData getLastLevel() {
        return levelDataArray[ getLastLevelIdx() ];
    }

    public boolean isEmpty() {
        return levelDataArray.length == 0;
    }

    public boolean isBeaten( final int difficulty ) {
        return getLastLevel().isBeaten( difficulty );
    }
}
