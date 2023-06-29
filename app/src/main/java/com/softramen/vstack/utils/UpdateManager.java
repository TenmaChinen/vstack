package com.softramen.vstack.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateManager {
    public final Map<Integer, ArrayList<Integer>> hashMap;

    private static UpdateManager instance;

    public UpdateManager() {
        hashMap = new HashMap<>();
    }

    public static synchronized void init() {
        if ( instance == null ) {
            instance = new UpdateManager();
        }
    }

    public static synchronized UpdateManager getInstance() {
        return instance;
    }

    public void addPendingItem( final int worldIdx , final int levelIdx ) {
        if ( !hashMap.containsKey( worldIdx ) ) {
            hashMap.put( worldIdx , new ArrayList<>() );
        }
        final ArrayList<Integer> arrayList = hashMap.get( worldIdx );
        if ( arrayList != null && !arrayList.contains( levelIdx ) ) {
            arrayList.add( levelIdx );
        }
    }

    public Map<Integer, ArrayList<Integer>> getHashMap() {
        return hashMap;
    }

    public void clear() {
        hashMap.clear();
    }
}
