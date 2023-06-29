package com.softramen.constants;

import android.graphics.ColorMatrixColorFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Colors {
    public static final List<ColorMatrixColorFilter> worldMatrixColorFilterList = new ArrayList<>();

    public static void loadMatrixColorFilterList( final ColorMatrixColorFilter[] matrixColorFilterArray ) {
        worldMatrixColorFilterList.addAll( Arrays.asList( matrixColorFilterArray ) );
    }
}
