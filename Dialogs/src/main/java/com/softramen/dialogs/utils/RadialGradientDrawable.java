package com.softramen.dialogs.utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RadialGradientDrawable extends Drawable {
    private final Paint paint = new Paint();
    private final float radius;

    public RadialGradientDrawable( final float radius ) {
        final int[] colors = new int[]{ Color.TRANSPARENT , 0x8F64EE6A, 0x8F64EE6A , Color.TRANSPARENT };
        final float[] stops = new float[]{ 0 , 0.4f, 0.6f , 1 };
        final RadialGradient radialGradient;
        radialGradient = new RadialGradient( radius , radius , radius , colors , stops , Shader.TileMode.CLAMP );
        paint.setShader( radialGradient );
        this.radius = radius;
    }

    @Override
    public void draw( @NonNull final Canvas canvas ) {
        canvas.drawCircle( radius , radius , radius , paint );
    }

    @Override
    public void setAlpha( final int alpha ) {
        paint.setAlpha( alpha );
    }

    @Override
    public void setColorFilter( @Nullable final ColorFilter colorFilter ) {
        paint.setColorFilter( colorFilter );
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
