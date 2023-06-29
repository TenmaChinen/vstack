package com.softramen.dialogs;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.softramen.constants.Colors;
import com.softramen.constants.Dialogs;
import com.softramen.constants.TextSize;

public class WorldNamesAdapter extends RecyclerView.Adapter<WorldNamesAdapter.ViewHolder> {

    private final ColorMatrixColorFilter[] worldMatrixColorFilterArray;
    private final String[] worldNameArray;
    final LayoutInflater layoutInflater;
    private final Context context;

    private final int selectedWorldPosition;
    private Callback callback;


    public WorldNamesAdapter( final String[] worldNameArray , final int selectedWorldPosition , final Context context ) {
        this.context = context;
        this.worldNameArray = worldNameArray;
        layoutInflater = LayoutInflater.from( context );
        this.selectedWorldPosition = selectedWorldPosition;

        worldMatrixColorFilterArray = new ColorMatrixColorFilter[ worldNameArray.length ];
        for ( int idx = 0 ; idx < worldNameArray.length ; idx++ ) {
            worldMatrixColorFilterArray[ idx ] = Colors.worldMatrixColorFilterList.get( idx );
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder( @NonNull final ViewGroup viewGroup , final int viewType ) {
        final View viewItem = layoutInflater.inflate( R.layout.world_names_adapter_item , viewGroup , false );
        viewItem.setLayoutParams( new FrameLayout.LayoutParams( Dialogs.WORLD_SELECTOR_WIDTH , -2 ) );
        return new ViewHolder( viewItem );
    }

    @Override
    public void onBindViewHolder( @NonNull final ViewHolder viewHolder , final int position ) {
        final String worldName = worldNameArray[ position ];
        final ColorMatrixColorFilter matrixColorFilter = worldMatrixColorFilterArray[ position ];
        viewHolder.bindData( worldName , matrixColorFilter );
    }

    @Override
    public int getItemCount() {
        return worldNameArray.length;
    }

    @Override
    public long getItemId( final int position ) {
        return position;
    }

    @Override
    public int getItemViewType( final int position ) {
        return 0;
    }

    public void setCallback( final Callback callback ) {
        this.callback = callback;
    }

    public interface Callback {
        void onItemClick( final int position );
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView ivBackground;
        private final TextView tvWorldNumber, tvWorldName;

        public ViewHolder( @NonNull final View itemView ) {
            super( itemView );
            ivBackground = itemView.findViewById( R.id.iv_background );
            tvWorldNumber = itemView.findViewById( R.id.tv_world_number );
            tvWorldName = itemView.findViewById( R.id.tv_world_name );
            itemView.setOnClickListener( this );
            initTextViews();
        }

        private void initTextViews() {
            tvWorldNumber.setTextSize( TypedValue.COMPLEX_UNIT_PX , TextSize.DialogWorldSelector.WORLD_NUM );
            tvWorldName.setTextSize( TypedValue.COMPLEX_UNIT_PX , TextSize.DialogWorldSelector.WORLD_NAME );
        }

        public void bindData( final String worldName , final ColorMatrixColorFilter matrixColorFilter ) {
            final int position = getBindingAdapterPosition();
            final int number = position + 1;
            tvWorldNumber.setText( context.getString( R.string.world_number , number ) );
            tvWorldName.setText( worldName );
            ivBackground.setColorFilter( matrixColorFilter );
            if ( position == selectedWorldPosition ) {
                final ConstraintLayout constraintLayout = itemView.findViewById( R.id.constraint_layout );
                constraintLayout.setBackgroundColor( Color.WHITE );
            }
        }

        @Override
        public void onClick( final View view ) {
            if ( callback != null ) callback.onItemClick( getBindingAdapterPosition() );
        }
    }
}