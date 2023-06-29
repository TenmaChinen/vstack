package com.softramen.utils.anim;

import android.view.animation.Animation;

public interface AnimationListener extends Animation.AnimationListener {
    @Override
    default void onAnimationStart( final Animation animation ) {}
    @Override
    void onAnimationEnd( Animation animation );
    @Override
    default void onAnimationRepeat( final Animation animation ) {}
}
