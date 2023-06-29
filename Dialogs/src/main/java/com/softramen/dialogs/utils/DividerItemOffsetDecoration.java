package com.softramen.dialogs.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DividerItemOffsetDecoration extends RecyclerView.ItemDecoration {
    private final int xOffset, yOffset;

    public DividerItemOffsetDecoration( final int xOffset , final int yOffset ) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    @Override
    public void getItemOffsets( @NonNull final Rect outRect , @NonNull final View view , @NonNull final RecyclerView parent , @NonNull final RecyclerView.State state ) {
        super.getItemOffsets( outRect , view , parent , state );
        outRect.set( xOffset , yOffset , xOffset , yOffset );
    }
}
