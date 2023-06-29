package com.softramen.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Paint;
import android.graphics.RectF;
import com.softramen.constants.LevelSelector;
import com.softramen.constants.TileType;

public class BitmapFactory {

	public static void colorFilterBitmapArray( final Bitmap[] srcBitmapArray , final Bitmap[] dstBitmapArray ,
			final ColorMatrixColorFilter matrixColorFilter ) {

		if ( matrixColorFilter == null ) {
			for ( int idx = 0 ; idx < srcBitmapArray.length ; idx++ ) {
				dstBitmapArray[ idx ] = srcBitmapArray[ idx ].copy( Bitmap.Config.ARGB_8888 , false );
			}
		}

		final Paint paint = new Paint();
		paint.setColorFilter( matrixColorFilter );
		for ( int idx = 0 ; idx < srcBitmapArray.length ; idx++ ) {
			if ( idx == TileType.WALL ) {
				dstBitmapArray[ idx ] = srcBitmapArray[ idx ].copy( Bitmap.Config.ARGB_8888 , false );
			} else {
				final Bitmap bitmapCopy = dstBitmapArray[ idx ].copy( Bitmap.Config.ARGB_8888 , true );
				final Canvas canvas = new Canvas( bitmapCopy );
				canvas.drawBitmap( srcBitmapArray[ idx ] , 0 , 0 , paint );
				dstBitmapArray[ idx ] = bitmapCopy;
			}
		}
	}

	public static Bitmap[] createColorFilteredBitmapArray( final Bitmap[] bitmapArray ,
			final ColorMatrixColorFilter matrixColorFilter ) {
		if ( matrixColorFilter == null ) return bitmapArray;

		final Bitmap[] bitmapFilteredArray = new Bitmap[ bitmapArray.length ];
		final int tileSize = bitmapArray[ 0 ].getWidth(); // 142

		final Paint paint = new Paint();
		final Matrix matrix = new Matrix();
		final RectF rectF = new RectF( 0 , 0 , tileSize , tileSize );
		matrix.setRectToRect( rectF , rectF , ScaleToFit.START );
		paint.setColorFilter( matrixColorFilter );
		for ( int idx = 0 ; idx < bitmapArray.length ; idx++ ) {
			final Bitmap bitmapFiltered =
					Bitmap.createBitmap( tileSize , tileSize , Bitmap.Config.ARGB_8888 ); // ARGB_8888
			final Canvas canvas = new Canvas( bitmapFiltered );
			canvas.drawBitmap( bitmapArray[ idx ] , matrix , paint );
			bitmapFilteredArray[ idx ] = bitmapFiltered;
		}

		return bitmapFilteredArray;
	}


	public static Bitmap createColorFilteredBitmap( final Bitmap bitmap ,
			final ColorMatrixColorFilter matrixColorFilter ) {
		if ( matrixColorFilter == null ) return Bitmap.createBitmap( bitmap );

		final Paint paint = new Paint();
		paint.setColorFilter( matrixColorFilter );

		final Bitmap bitmapFiltered =
				Bitmap.createBitmap( bitmap.getWidth() , bitmap.getHeight() , Bitmap.Config.ARGB_8888 );
		final Canvas canvas = new Canvas( bitmapFiltered );
		canvas.drawBitmap( bitmap , 0 , 0 , paint );

		return bitmapFiltered;
	}

	public static Bitmap[] createDifficultyTileBitmapArray( final Bitmap redTileBitmap ) {

		final Bitmap[] bulletTileBitmapArray = new Bitmap[ 3 ];
		final int[] hueArray = { 140 , 50 , 0 };
		final int[] contrastArray = { 20 , 40 , 0 };

		for ( int idx = 0 ; idx < 3 ; idx++ ) {
			final Bitmap scaledBitmap = BitmapManager.scaleTileBitmap( redTileBitmap , LevelSelector.BULLET_SIZE );
			final ColorMatrixColorFilter matrixColorFilter =
					ColorFilterManager.createMatrixColorFilter( hueArray[ idx ] , contrastArray[ idx ] );
			bulletTileBitmapArray[ idx ] = BitmapFactory.createColorFilteredBitmap( scaledBitmap , matrixColorFilter );
		}
		return bulletTileBitmapArray;
	}


	public static Bitmap[] createPortalRotationBitmapArray( final Bitmap bitmapPortal ) {
		final Bitmap[] bitmapArray = new Bitmap[ 20 ]; // 360 / 20 = 18 Degrees per frame
		final int size = bitmapPortal.getWidth();
		final int pivot = size / 2;

		final Matrix matrix = new Matrix();
		for ( int idx = 0 ; idx < bitmapArray.length ; idx++ ) {
			final int degrees = idx * 18;
			matrix.setRotate( degrees , pivot , pivot );
			final Bitmap bitmapRotated = Bitmap.createBitmap( bitmapPortal , 0 , 0 , size , size , matrix , false );
			final int offset = ( bitmapRotated.getWidth() - size ) / 2;
			bitmapArray[ idx ] = Bitmap.createBitmap( bitmapRotated , offset , offset , size , size );
		}
		return bitmapArray;
	}


}