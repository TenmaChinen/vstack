package com.softramen.introFactory;

import android.content.Context;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import com.softramen.constants.TextSize;
import com.softramen.introView.IntroConfig;
import com.softramen.introView.IntroWidget;
import com.softramen.introView.shapes.FocusGravity;
import com.softramen.introView.shapes.FocusType;
import com.softramen.introView.shapes.ShapeType;


public class BaseIntroFactory {
    private final IntroConfig introConfig;
    protected final int NULL = -1;
    protected final Context context;

    public BaseIntroFactory( final Context context ) {
        this.context = context;
        introConfig = setIntroConfig();
    }

    private IntroConfig setIntroConfig() {
        final IntroConfig introConfig = new IntroConfig();
        introConfig.setTextInfoSize( TypedValue.COMPLEX_UNIT_PX , TextSize.IntroWidget.INFO );
        introConfig.setFocusGravity( FocusGravity.CENTER );
        introConfig.setMaskColor( context.getResources().getColor( R.color.intro_mask ) );
        introConfig.setFocusType( FocusType.NORMAL );
        introConfig.setFocusPadding( 20 );
        introConfig.setFadeAnimationEnabled( true );
        introConfig.setStartDelayMillis( 200 );
        return introConfig;
    }

    public void setLayoutMargin( final int margin , final int gravity ) {
        introConfig.setLayoutMargin( margin );
        introConfig.setLayoutGravity( gravity );
    }

    public IntroWidget makeIntro( final View target , final String message , final ShapeType shapeType , final String usageId ) {
        final IntroWidget.Builder builder = new IntroWidget.Builder( context , R.style.IntroWidgetCustomTheme );
        builder.setTarget( target )
               // .performClick( performClick )
               .setTargetShapeType( shapeType )
               .setUsageId( usageId )
               .setInfoText( message )
               .setConfig( introConfig );
        return builder.build();
    }

    public IntroWidget makeIntro( final View target , final String message , final ShapeType shapeType , final int padding , final int alignment , final String usageId ) {
        final IntroWidget.Builder builder = new IntroWidget.Builder( context , R.style.IntroWidgetCustomTheme );
        builder.setTarget( target )
               // .performClick( performClick )
               .setTargetShapeType( shapeType )
               .setUsageId( usageId )
               .setInfoText( message )
               .setConfig( introConfig );

        if ( padding != -1 ) builder.setTargetPadding( padding );
        if ( alignment != NULL ) builder.setTextViewInfoAlignment( alignment );
        return builder.build();
    }

    public void startIntro( final Runnable runnable ) {
        final Handler handler = new Handler();
        handler.post( runnable );
    }

    public void startIntro( final Runnable runnable , final int delayMillis ) {
        final Handler handler = new Handler();
        handler.postDelayed( runnable , delayMillis );
    }
}
