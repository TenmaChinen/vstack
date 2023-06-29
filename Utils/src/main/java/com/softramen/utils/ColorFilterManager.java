package com.softramen.utils;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;

public class ColorFilterManager {

    private static final double[] DELTA_INDEX = {
            0 , 0.01 , 0.02 , 0.04 , 0.05 , 0.06 , 0.07 , 0.08 , 0.1 , 0.11 ,
            0.12 , 0.14 , 0.15 , 0.16 , 0.17 , 0.18 , 0.20 , 0.21 , 0.22 , 0.24 ,
            0.25 , 0.27 , 0.28 , 0.30 , 0.32 , 0.34 , 0.36 , 0.38 , 0.40 , 0.42 ,
            0.44 , 0.46 , 0.48 , 0.5 , 0.53 , 0.56 , 0.59 , 0.62 , 0.65 , 0.68 ,
            0.71 , 0.74 , 0.77 , 0.80 , 0.83 , 0.86 , 0.89 , 0.92 , 0.95 , 0.98 ,
            1.0 , 1.06 , 1.12 , 1.18 , 1.24 , 1.30 , 1.36 , 1.42 , 1.48 , 1.54 ,
            1.60 , 1.66 , 1.72 , 1.78 , 1.84 , 1.90 , 1.96 , 2.0 , 2.12 , 2.25 ,
            2.37 , 2.50 , 2.62 , 2.75 , 2.87 , 3.0 , 3.2 , 3.4 , 3.6 , 3.8 ,
            4.0 , 4.3 , 4.7 , 4.9 , 5.0 , 5.5 , 6.0 , 6.5 , 6.8 , 7.0 ,
            7.3 , 7.5 , 7.8 , 8.0 , 8.4 , 8.7 , 9.0 , 9.4 , 9.6 , 9.8 ,
            10.0
    };

    public static void adjustHue( final ColorMatrix colorMatrix , final float degrees ) {
        final double radians = degrees * Math.PI / 180d;
        if ( radians == 0 ) {
            return;
        }

        final float cosVal = ( float ) Math.cos( radians );
        final float sinVal = ( float ) Math.sin( radians );
        final float lumR = 0.213f;
        final float lumG = 0.715f;
        final float lumB = 0.072f;
        final float[] matrix = new float[]{
                lumR + cosVal * ( 1 - lumR ) + sinVal * ( -lumR ) , lumG + cosVal * ( -lumG ) + sinVal * ( -lumG ) , lumB + cosVal * ( -lumB ) + sinVal * ( 1 - lumB ) , 0 , 0 ,
                lumR + cosVal * ( -lumR ) + sinVal * ( 0.143f ) , lumG + cosVal * ( 1 - lumG ) + sinVal * ( 0.140f ) , lumB + cosVal * ( -lumB ) + sinVal * ( -0.283f ) , 0 , 0 ,
                lumR + cosVal * ( -lumR ) + sinVal * ( -( 1 - lumR ) ) , lumG + cosVal * ( -lumG ) + sinVal * ( lumG ) , lumB + cosVal * ( 1 - lumB ) + sinVal * ( lumB ) , 0 , 0 ,
                0 , 0 , 0 , 1 , 0 };
        colorMatrix.postConcat( new ColorMatrix( matrix ) );
    }

    public static void adjustContrast( final ColorMatrix colorMatrix , final int value ) {
        final int contrast = ( int ) limitValue( value , 100 );
        if ( contrast == 0 ) {
            return;
        }
        float x;
        if ( contrast < 0 ) {
            x = 127 + ( float ) contrast / 100 * 127;
        } else {
            x = ( float ) DELTA_INDEX[ contrast ];
            x = x * 127 + 127;
        }

        final float[] matrix = new float[]{
                x / 127 , 0 , 0 , 0 , 0.5f * ( 127 - x ) ,
                0 , x / 127 , 0 , 0 , 0.5f * ( 127 - x ) ,
                0 , 0 , x / 127 , 0 , 0.5f * ( 127 - x ) ,
                0 , 0 , 0 , 1 , 0 ,
                0 , 0 , 0 , 0 , 1
        };
        colorMatrix.postConcat( new ColorMatrix( matrix ) );
    }

    protected static double limitValue( final float value , final double limit ) {
        return Math.min( limit , Math.max( -limit , value ) );
    }


    public static ColorMatrixColorFilter createMatrixColorFilter( final int hue , final int contrast ) {
        final ColorMatrix colorMatrix = new ColorMatrix();
        ColorFilterManager.adjustHue( colorMatrix , hue );
        ColorFilterManager.adjustContrast( colorMatrix , contrast );
        return new ColorMatrixColorFilter( colorMatrix );
    }
}

