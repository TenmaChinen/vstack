package com.softramen.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.softramen.constants.Dialogs;
import com.softramen.constants.TextSize;

import com.softramen.dialogs.utils.DialogBase;
import com.softramen.settingsManager.SettingsItem;
import com.softramen.settingsManager.SettingsListAdapter;
import com.softramen.settingsManager.SettingsPreferences;
import com.softramen.sounds.Sounds;

import org.jetbrains.annotations.Nullable;

import java.util.Map;


public class DialogSettings extends DialogBase {
    private final String TAG = "DIALOG_SETTINGS";

    public static final String REQUEST_CODE = "DIALOG_SETTINGS";
    public static final String RESULT_CHANGED_IDS = "RESULT_CHANGED_IDS";

    private final SettingsPreferences settingsPreferences = SettingsPreferences.getInstance();
    private final Sounds sounds = Sounds.getInstance();
    private int callbackId = Dialogs.ON_CANCEL;

    private int[] changedSettingsId = null;


    @NonNull
    @Override
    public Dialog onCreateDialog( @Nullable final Bundle savedInstanceState ) {
        final Dialog dialog = super.onCreateDialog( savedInstanceState );
        final Window window = dialog.getWindow();
        window.setGravity( Gravity.TOP );
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView( @NonNull final LayoutInflater inflater , @Nullable final ViewGroup container , @Nullable final Bundle savedInstanceState ) {
        final View inflatedView = inflater.inflate( R.layout.dialog_settings , container , false );
        final ListView listView = inflatedView.findViewById( R.id.list_view );
        final TextView btnSave = inflatedView.findViewById( R.id.btn_save );
        final TextView btnCancel = inflatedView.findViewById( R.id.btn_cancel );

        btnSave.setTextSize( TypedValue.COMPLEX_UNIT_PX , TextSize.DialogSettings.BUTTON );
        btnCancel.setTextSize( TypedValue.COMPLEX_UNIT_PX , TextSize.DialogSettings.BUTTON );

        final Map<Integer, SettingsItem> settingsItemMap = settingsPreferences.getSettingsItemMap();
        final SettingsListAdapter settingsListAdapter = new SettingsListAdapter( inflater.getContext() , settingsItemMap );
        listView.setAdapter( settingsListAdapter );

        final View.OnClickListener clickListener = view -> {
            btnSave.setOnClickListener( null );
            btnCancel.setOnClickListener( null );
            final int viewId = view.getId();
            if ( viewId == R.id.btn_save ) {
                settingsListAdapter.updateSettingsChanges();
                changedSettingsId = settingsListAdapter.getChangedSettingsId();
                settingsPreferences.saveSettings( changedSettingsId );
                callbackId = Dialogs.ON_CLICK_SAVE;
                sounds.playRePop();
            } else if ( viewId == R.id.btn_cancel ){
                callbackId = Dialogs.ON_CANCEL;
                sounds.playPop();
            }
            startDismissAnimation();
        };

        btnSave.setOnClickListener( clickListener );
        btnCancel.setOnClickListener( clickListener );
        return inflatedView;
    }

    @Override
    public void onWindowEnterAnimationStart() {
        Sounds.getInstance().playAppear();
    }

    @Override
    public void onCancel( @NonNull final DialogInterface dialogInterface ) {
        // super.onCancel( dialogInterface );
    }

    @Override
    public void onDismiss( @NonNull final DialogInterface dialog ) {
        final Bundle bundle = new Bundle();
        bundle.putIntArray( RESULT_CHANGED_IDS , changedSettingsId );
        sendResults( REQUEST_CODE , callbackId , bundle );
        super.onDismiss( dialog );
    }

    public void show( @NonNull final FragmentManager manager ) {
        super.show( manager , "DialogSettings" );
    }
}