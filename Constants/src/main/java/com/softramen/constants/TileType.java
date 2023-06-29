package com.softramen.constants;

import java.util.HashMap;

public class TileType {
    public final static int VOID = -1;
    public final static int OFF = 0;
    public final static int ON = 1;
    public final static int WALL = 2;
    public final static int BROKEN_OFF = 3;
    public final static int BROKEN_ON = 4;
    public final static int ARROW_LEFT = 5;
    public final static int ARROW_RIGHT = 6;
    public final static int PORTAL = 7;

    public final static int[] animatedTilesId = { BROKEN_OFF , ARROW_LEFT , PORTAL };
    public final static HashMap<Integer, int[]> groups = new HashMap<Integer, int[]>() {};

    public static void init() {
        groups.put( BROKEN_OFF , new int[]{ BROKEN_OFF , BROKEN_ON } );
        groups.put( ARROW_LEFT , new int[]{ ARROW_LEFT , ARROW_RIGHT } );
    }

    public static boolean isFromGroup( final int tileId , final int tileGroup ) {
        if ( tileId == tileGroup ) return true;
        final int[] subTileIdArray = TileType.groups.get( tileGroup );
        if ( subTileIdArray != null ) {
            for ( final int subTileId : subTileIdArray ) {
                if ( tileId == subTileId ) return true;
            }
        }
        return false;
    }
}
