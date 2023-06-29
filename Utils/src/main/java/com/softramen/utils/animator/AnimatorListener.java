package com.softramen.utils.animator;

import android.animation.Animator;

import androidx.annotation.NonNull;

public interface AnimatorListener extends Animator.AnimatorListener {
    @Override
    default void onAnimationEnd( @NonNull final Animator animation ) {
        onAnimationEndOrCancel();
    }

    @Override
    default void onAnimationCancel( @NonNull final Animator animation ) {
        onAnimationEndOrCancel();
    }

    @Override
    default void onAnimationStart( @NonNull final Animator animation ) {}
    @Override
    default void onAnimationRepeat( @NonNull final Animator animation ) {}

    void onAnimationEndOrCancel();
}
