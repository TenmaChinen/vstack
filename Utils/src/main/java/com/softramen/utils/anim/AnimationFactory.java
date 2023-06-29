package com.softramen.utils.anim;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.softramen.utils.R;

public class AnimationFactory {
    public static Animation makeToastAnimation( final View target ) {
        final Animation animation = AnimationUtils.loadAnimation( target.getContext() , R.anim.anim_toast );
        animation.setAnimationListener( ( AnimationListener ) animation1 -> target.setVisibility( View.GONE ) );
        return animation;
    }
}
