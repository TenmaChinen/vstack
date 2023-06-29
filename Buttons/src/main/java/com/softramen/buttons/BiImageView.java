package com.softramen.buttons;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

public class BiImageView extends androidx.appcompat.widget.AppCompatImageView {
    private Drawable textImage;
    private float textImageHeightPercent;


    public BiImageView( @NonNull final Context context ) {
        super( context );
    }

    public BiImageView( @NonNull final Context context , @Nullable final AttributeSet attrs ) {
        super( context , attrs );

        final TypedArray typedArray = context.getTheme().obtainStyledAttributes( attrs , R.styleable.layered_image_view , 0 , 0 );
        try {
            textImage = typedArray.getDrawable( R.styleable.layered_image_view_text_image );
            textImageHeightPercent = typedArray.getFloat( R.styleable.layered_image_view_text_image_height_percent , 1.0f );
        } finally {
            typedArray.recycle();
        }
    }

    public void setTextImage( final int resourceId ) {
        this.textImage = ResourcesCompat.getDrawable( getResources() , resourceId , null );
        invalidate();
        requestLayout();
    }

    @Override
    protected void onDraw( final Canvas canvas ) {
        super.onDraw( canvas );

        if ( textImage != null ) {
            final int width = getWidth(), height = getHeight();
            final float intrinsicRatio = ( float ) textImage.getIntrinsicWidth() / ( float ) textImage.getIntrinsicHeight();
            final int newTextImageHeight = ( int ) ( height * textImageHeightPercent );
            final int newTextImageWidth = ( int ) ( newTextImageHeight * intrinsicRatio );

            final int posX = ( width - newTextImageWidth ) / 2;
            final int posY = ( height - newTextImageHeight ) / 2;
            textImage.setBounds( posX , posY , posX + newTextImageWidth , posY + newTextImageHeight );
            textImage.draw( canvas );
        }
    }
}
