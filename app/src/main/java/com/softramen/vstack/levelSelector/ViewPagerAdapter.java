package com.softramen.vstack.levelSelector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ColorMatrixColorFilter;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.softramen.constants.Colors;
import com.softramen.constants.LevelSelector;
import com.softramen.constants.TextSize;
import com.softramen.utils.BitmapFactory;
import com.softramen.vstack.GameData;
import com.softramen.vstack.R;
import com.softramen.vstack.utils.WorldData;

import java.util.ArrayList;

public class ViewPagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_WORLD = 1, VIEW_TYPE_SOON_MESSAGE = 2;

    private final SparseArray<RecyclerViewAdapter> loadedRecyclerViewAdapters = new SparseArray<>();
    private final WorldData[] worldDataArray = GameData.getInstance().worldDataArray;
    private final LayoutInflater layoutInflater;

    private RecyclerViewAdapter.Callback recyclerViewItemCallback;

    public ViewPagerAdapter( final Context context ) {
        layoutInflater = LayoutInflater.from( context );
    }

    public void setCallback( final RecyclerViewAdapter.Callback recyclerViewItemCallback ) {
        this.recyclerViewItemCallback = recyclerViewItemCallback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder( @NonNull final ViewGroup viewGroup , final int viewType ) {
        final View itemView;
        if ( viewType == VIEW_TYPE_WORLD ) {
            itemView = layoutInflater.inflate( R.layout.view_pager_item_world , viewGroup , false );
            return new WorldViewHolder( itemView );
        } else {
            itemView = layoutInflater.inflate( R.layout.view_pager_item_soon_message , viewGroup , false );
            return new SoonMessageViewHolder( itemView );
        }
    }

    @Override
    public void onBindViewHolder( @NonNull final RecyclerView.ViewHolder viewHolder , final int position ) {
        if ( viewHolder instanceof WorldViewHolder ) {
            final WorldViewHolder worldViewHolder = ( WorldViewHolder ) viewHolder;
            worldViewHolder.bind( position );
            loadedRecyclerViewAdapters.append( position , worldViewHolder.getRecyclerViewAdapter() );
        }
    }

    @Override
    public int getItemCount() {
        return worldDataArray.length;
    }

    @Override
    public long getItemId( final int position ) {
        return position;
    }

    @Override
    public int getItemViewType( final int position ) {
        return ( position == worldDataArray.length - 1 ) ? VIEW_TYPE_SOON_MESSAGE : VIEW_TYPE_WORLD;
    }

    @Override
    public void onViewRecycled( @NonNull final RecyclerView.ViewHolder viewHolder ) {
        super.onViewRecycled( viewHolder );
        loadedRecyclerViewAdapters.delete( viewHolder.getBindingAdapterPosition() );
    }

    public void notifyRecyclerViewItemsChanged( final int pageIndex , final ArrayList<Integer> recyclerViewItemsIndex ) {
        if ( recyclerViewItemsIndex != null ) {
            final RecyclerViewAdapter recyclerViewAdapter = loadedRecyclerViewAdapters.get( pageIndex );
            if ( recyclerViewAdapter != null ) {
                for ( final Integer itemIndex : recyclerViewItemsIndex ) {
                    recyclerViewAdapter.notifyItemChanged( itemIndex );
                }
            }
        }
    }

    public class WorldViewHolder extends RecyclerView.ViewHolder {
        private RecyclerViewAdapter recyclerViewAdapter;
        private final RecyclerView recyclerView;

        public WorldViewHolder( @NonNull final View itemView ) {
            super( itemView );
            recyclerView = itemView.findViewById( R.id.recycler_view );
        }

        public void bind( final int worldIdx ) {
            final Context context = itemView.getContext();

            final GridLayoutManager gridLayoutManager = new GridLayoutManager( context , 2 );
            final ItemOffsetDecoration itemOffsetDecoration = new ItemOffsetDecoration( LevelSelector.ITEM_OFFSET_X , LevelSelector.ITEM_OFFSET_Y );
            final Bitmap[] tilesBitmap = GameData.getInstance().tileBitmapArray;
            final ColorMatrixColorFilter matrixColorFilter = Colors.worldMatrixColorFilterList.get( worldIdx );
            final Bitmap[] tintedBitmaps = BitmapFactory.createColorFilteredBitmapArray( tilesBitmap , matrixColorFilter );

            recyclerViewAdapter = new RecyclerViewAdapter( context , worldIdx , tintedBitmaps );
            recyclerViewAdapter.setCallback( recyclerViewItemCallback );
            recyclerView.addItemDecoration( itemOffsetDecoration );
            recyclerView.setLayoutManager( gridLayoutManager );
            recyclerView.setAdapter( recyclerViewAdapter );
        }

        public RecyclerViewAdapter getRecyclerViewAdapter() {
            return recyclerViewAdapter;
        }
    }

    private static class SoonMessageViewHolder extends RecyclerView.ViewHolder {

        public SoonMessageViewHolder( final View itemView ) {
            super( itemView );
            final TextView tvSoonMessage = itemView.findViewById( R.id.tv_soon_message );
            tvSoonMessage.setTextSize( TypedValue.COMPLEX_UNIT_PX , TextSize.LevelSelector.SOON_MESSAGE );
            tvSoonMessage.setY( LevelSelector.SOON_MESSAGE_Y );
        }
    }
}