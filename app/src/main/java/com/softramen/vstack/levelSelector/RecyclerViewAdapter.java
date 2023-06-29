package com.softramen.vstack.levelSelector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.softramen.constants.LevelSelector;
import com.softramen.constants.TextSize;
import com.softramen.settingsManager.SettingsPreferences;
import com.softramen.vstack.BuildConfig;
import com.softramen.vstack.GameData;
import com.softramen.vstack.R;
import com.softramen.vstack.utils.LevelData;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private final SettingsPreferences settingsPreferences = SettingsPreferences.getInstance();
    private final GameData gameData = GameData.getInstance();
    private final LevelData[] levelDataArray;
    private final Bitmap[] tileBitmapArray;
    private final Context context;
    private final int worldIdx;
    private Callback callback;
    private final LayoutInflater layoutInflater;

    public RecyclerViewAdapter( final Context context , final int worldIdx , final Bitmap[] tileBitmapArray ) {
        this.context = context;
        this.worldIdx = worldIdx;
        this.tileBitmapArray = tileBitmapArray;
        levelDataArray = gameData.worldDataArray[ worldIdx ].levelDataArray;
        layoutInflater = LayoutInflater.from( context );
    }

    public interface Callback {
        void onClick( final int levelIdx , final boolean isLocked );
    }

    public void setCallback( final Callback callback ) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder( @NonNull final ViewGroup viewGroup , final int viewType ) {
        final View itemView = layoutInflater.inflate( R.layout.recycler_view_item_level , viewGroup , false );
        itemView.setLayoutParams( new ViewGroup.LayoutParams( -1 , LevelSelector.ITEM_HEIGHT ) );
        return new ViewHolder( itemView );
    }

    @Override
    public void onBindViewHolder( @NonNull final ViewHolder viewHolder , final int position ) {
        boolean isUnlocked = gameData.isUnlockedLevel( worldIdx , position );
        if ( BuildConfig.DEBUG ) isUnlocked = isUnlocked | settingsPreferences.getUnlockLevelsState();
        viewHolder.bindData( levelDataArray[ position ] , isUnlocked );
    }

    @Override
    public int getItemCount() {
        return levelDataArray.length;
    }

    @Override
    public long getItemId( final int position ) {
        return position;
    }

    @Override
    public int getItemViewType( final int position ) {
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final FrameLayout frameLayoutCanvas;
        private final GridLayout gridLayout;
        private final TextView tvLevel;
        private final ImageView ivLock;
        private boolean gridLayoutIsInflated = false;
        private boolean isInitialized = false;

        public ViewHolder( final View itemView ) {
            super( itemView );
            itemView.setOnClickListener( this::onClick );
            tvLevel = getLevelTextView( itemView );
            frameLayoutCanvas = itemView.findViewById( R.id.frame_layout_canvas );
            gridLayout = itemView.findViewById( R.id.grid_layout_time_records );
            ivLock = itemView.findViewById( R.id.iv_lock );
            ivLock.setMaxWidth( LevelSelector.LOCK_WIDTH );
        }

        private TextView getLevelTextView( final View itemView ) {
            final TextView tvLevel = itemView.findViewById( R.id.tv_level );
            final int pad = LevelSelector.LEVEL_TITLE_PAD;
            tvLevel.setPadding( pad , pad , pad , ( int ) ( pad * 0.65f ) );
            tvLevel.setTextSize( TypedValue.COMPLEX_UNIT_PX , TextSize.LevelSelector.LEVEL_TITLE );
            return tvLevel;
        }

        public void bindData( final LevelData levelData , final boolean isUnlocked ) {

            final String levelTitle = context.getString( R.string.level_title , getBindingAdapterPosition() + 1 );
            tvLevel.setText( levelTitle );

            if ( frameLayoutCanvas.getChildCount() > 0 ) frameLayoutCanvas.removeAllViews();
            frameLayoutCanvas.addView( new LevelThumbnailView( context , levelData , tileBitmapArray , LevelSelector.ITEM_WIDTH , LevelSelector.ITEM_HEIGHT , isUnlocked ) );

            if ( isUnlocked ) {

                if ( !gridLayoutIsInflated ) {
                    // Prevent from inflate if ViewHolder is recycled
                    layoutInflater.inflate( R.layout.time_records_item , gridLayout , true );
                    gridLayout.setVisibility( View.VISIBLE );
                    gridLayoutIsInflated = true;
                }

                final String[] formattedTimeRecordArray = levelData.getTimeRecords().getFormattedTimeRecords();
                setTimeRecordsLayout( formattedTimeRecordArray );
            }

            ivLock.setVisibility( isUnlocked ? View.GONE : View.VISIBLE );
        }

        private void setTimeRecordsLayout( final String[] timeRecordArray ) {
            final int[] ivResourceIdArray = { R.id.iv_easy , R.id.iv_medium , R.id.iv_hard };
            final int[] tvResourceIdArray = { R.id.tv_easy , R.id.tv_medium , R.id.tv_hard };

            for ( int idx = 0 ; idx < 3 ; idx++ ) {
                final ImageView ivRecord = gridLayout.findViewById( ivResourceIdArray[ idx ] );
                final TextView tvRecord = gridLayout.findViewById( tvResourceIdArray[ idx ] );

                if ( !isInitialized ) {
                    ivRecord.setImageBitmap( gameData.bulletTileBitmapArray[ idx ] );
                    tvRecord.setTextSize( TypedValue.COMPLEX_UNIT_PX , TextSize.LevelSelector.RECORD );
                }

                if ( timeRecordArray[ idx ].equals( "--:--" ) ) {
                    ivRecord.setColorFilter( Color.GRAY , PorterDuff.Mode.MULTIPLY );
                    tvRecord.setTextColor( Color.LTGRAY );
                } else {
                    ivRecord.clearColorFilter();
                    tvRecord.setTextColor( Color.WHITE );
                }
                tvRecord.setText( timeRecordArray[ idx ] );
            }
            isInitialized = true;
        }

        private boolean isLocked() {
            return ivLock.getVisibility() == View.GONE;
        }

        @Override
        public void onClick( final View view ) {
            if ( callback != null ) {
                callback.onClick( getBindingAdapterPosition() , isLocked() );
            }
        }
    }
}
