package com.softramen.vstack.inGame.animatedtiles;

import com.softramen.constants.TileType;

import java.util.Arrays;
import java.util.HashMap;

public class IterationSequences {
    public static final int[] BROKEN_TILE_ARRAY = new int[]{ 200 , 1 , 1 , 1 , 1 , 1 , 10 , 1 , 1 , 1 , 2 , 15 , 1 , 5 , 1 , 15 , 1 , 15 , 1 , 1 , 1 , 10 , 1 , 1 , 1 , 10 , 1 , 1 , 1 , 20 , 1 , 1 , 1 , 1 , 150 , 1 , 80 , 60 , 1 , 5 , 40 , 1 };
    public static final int[] ARROW_TILE_ARRAY = new int[]{ 40 , 60 , 20 , 80 , 20 , 40 , 20 , 80 , 60 , 80 , 40 , 30 , 40 , 70 , 30 , 60 , 30 };
    public static final int[] PORTAL_TILE_ARRAY = new int[ 18 ];

    public static final HashMap<Integer, int[]> sequences = new HashMap<>();

    public static void init() {
        Arrays.fill( PORTAL_TILE_ARRAY , 2 );
        sequences.put( TileType.BROKEN_OFF , BROKEN_TILE_ARRAY );
        sequences.put( TileType.ARROW_LEFT , ARROW_TILE_ARRAY );
        sequences.put( TileType.PORTAL , PORTAL_TILE_ARRAY );
    }
}
