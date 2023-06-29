package com.softramen.utils;

import java.util.Locale;

public class Converter {
    public static String timeToMinSec( final int totalSeconds ) {
        if ( totalSeconds == -1 ) return "--:--";
        final int minutes = totalSeconds / 60;
        final int seconds = totalSeconds % 60;
        return String.format( Locale.CANADA , "%02d:%02d" , minutes , seconds );
    }
}
