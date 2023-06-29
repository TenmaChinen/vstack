package com.softramen.settingsManager;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.softramen.constants.Dialogs;
import com.softramen.constants.TextSize;
import com.softramen.optionMenu.OptionMenu;
import com.softramen.sounds.Sounds;
import com.softramen.switchCom.SwitchCom;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SettingsListAdapter extends BaseAdapter {
    private final LayoutInflater layoutInflater;
    private final Map<Integer, Object> changesMap = new HashMap<>();
    private final Map<Integer, SettingsItem> settingsItemMap;
    private final int[] mapKeyArray;
    private final Sounds sounds = Sounds.getInstance();

    public SettingsListAdapter( @NonNull final Context context , @NonNull final Map<Integer, SettingsItem> settingsItemMap ) {
        layoutInflater = LayoutInflater.from( context );
        this.settingsItemMap = settingsItemMap;
        mapKeyArray = getMapKeys( settingsItemMap );
    }

    private int[] getMapKeys( final Map<Integer, SettingsItem> settingsItemMap ) {
        int idx = 0;
        final int[] mapKeyArray = new int[ settingsItemMap.size() ];
        for ( final int itemId : settingsItemMap.keySet() ) {
            mapKeyArray[ idx++ ] = itemId;
        }
        return mapKeyArray;
    }

    public void updateSettingsChanges() {
        for ( final Entry<Integer, Object> entrySet : changesMap.entrySet() ) {
            final int itemId = entrySet.getKey();
            final SettingsItem settingsItem = settingsItemMap.get( itemId );
            if ( settingsItem != null ) {
                switch ( settingsItem.getItemType() ) {
                    case SettingsItem.Type.SPINNER:
                        settingsItem.setOptionPosition( ( Integer ) entrySet.getValue() );
                        break;
                    case SettingsItem.Type.SWITCH:
                        settingsItem.setState( ( Boolean ) entrySet.getValue() );
                }

                switch ( itemId ) {
                    case SettingsId.RESET_PROGRESS:
                    case SettingsId.RESET_INTRO:
                        settingsItem.setState( false );
                }
            }
        }
    }

    public int[] getChangedSettingsId() {
        final Integer[] integerArray = changesMap.keySet().toArray( new Integer[ 0 ] );
        final int[] changedSettingIdArray = new int[ integerArray.length ];
        for ( int idx = 0 ; idx < changedSettingIdArray.length ; idx++ ) {
            changedSettingIdArray[ idx ] = integerArray[ idx ];
        }
        return changedSettingIdArray;
    }

    @Override
    public int getCount() {
        return settingsItemMap.size();
    }

    @Override
    public SettingsItem getItem( final int position ) {
        return settingsItemMap.get( mapKeyArray[ position ] );
    }

    @Override
    public long getItemId( final int position ) {
        return position;
    }

    @Override
    public int getItemViewType( final int position ) {
        return getItem( position ).getItemType();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @NonNull
    @Override
    public View getView( final int itemPosition , @Nullable View convertView , @NonNull final ViewGroup parent ) {

        /* Each view item is too complex to recycle */
        final TextView tvLabel;
        final SettingsItem listItem = getItem( itemPosition );

        switch ( getItemViewType( itemPosition ) ) {
            case SettingsItem.Type.SWITCH:
                convertView = layoutInflater.inflate( R.layout.item_settings_list_switch , parent , false );
                tvLabel = convertView.findViewById( R.id.tv_label_list_item );
                tvLabel.setText( listItem.getLabel() );
                tvLabel.setTextSize( TypedValue.COMPLEX_UNIT_PX , TextSize.DialogSettings.LABEL );

                final SwitchCom switchCom = convertView.findViewById( R.id.switch_settings_list_item );
                switchCom.setChecked( listItem.getState() );
                switchCom.setOnClickListener( v -> sounds.playPop() );
                switchCom.setOnCheckedChangeListener( ( compoundButton , state ) -> {
                    final int settingId = mapKeyArray[ itemPosition ];
                    if ( state != listItem.getState() ) changesMap.put( settingId , state );
                    else changesMap.remove( settingId );
                } );
                break;
            case SettingsItem.Type.SPINNER:
                convertView = layoutInflater.inflate( R.layout.item_settings_list_spinner , parent , false );
                tvLabel = convertView.findViewById( R.id.tv_label_list_item );
                tvLabel.setText( listItem.getLabel() );
                tvLabel.setTextSize( TypedValue.COMPLEX_UNIT_PX , TextSize.DialogSettings.LABEL );

                final OptionMenu optionMenu = convertView.findViewById( R.id.spinner_settings_list_item );
                optionMenu.setTextSize( TypedValue.COMPLEX_UNIT_PX , TextSize.DialogSettings.LABEL );
                optionMenu.setItems( listItem.getOptionStringArray() );
                optionMenu.setSelection( listItem.getOptionPosition() , false );
                optionMenu.setOnTouchListener( ( v , motionEvent ) -> {
                    if ( motionEvent.getAction() == MotionEvent.ACTION_UP ) sounds.playRePop();
                    return false;
                } );

                optionMenu.setOnItemSelectedListener( ( OnItemSelectedListener ) ( adapterView , view , optionPosition , l ) -> {
                    final int settingId = mapKeyArray[ itemPosition ];
                    if ( optionPosition != listItem.getOptionPosition() ) changesMap.put( settingId , optionPosition );
                    else changesMap.remove( settingId );
                    sounds.playPop();
                } );
                break;

            default:
                convertView = new Space( parent.getContext() );
        }

        convertView.setPadding( 0 , Dialogs.SETTINGS_DIVIDER_PAD , 0 , Dialogs.SETTINGS_DIVIDER_PAD );
        return convertView;
    }
}
