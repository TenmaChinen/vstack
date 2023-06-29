package com.softramen.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.softramen.constants.Dialogs;
import com.softramen.dialogs.utils.DialogBase;
import com.softramen.dialogs.utils.DividerItemOffsetDecoration;
import com.softramen.sounds.Sounds;

import org.jetbrains.annotations.Nullable;


public class DialogWorldSelector extends DialogBase {
    public static final String REQUEST_CODE = "DIALOG_WINNER";
    public static final String ARG_WORLD_NAMES = "WORLD_NAMES", ARG_SELECTED_WORLD_POSITION = "ARG_SELECTED_WORLD_POSITION";
    public static final String RESULT_POSITION = "RESULT_POSITION";

    private final Sounds sounds = Sounds.getInstance();
    private final Bundle results = new Bundle();

    private String[] worldNameArray;
    private int selectedWorldPosition;

    @Override
    public void onCreate( @Nullable final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        final Bundle args = getArguments();
        if ( args != null ) {
            worldNameArray = args.getStringArray( ARG_WORLD_NAMES );
            selectedWorldPosition = args.getInt( ARG_SELECTED_WORLD_POSITION );
        }
    }

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
        final View inflatedView = inflater.inflate( R.layout.dialog_world_names , container , false );
        final RecyclerView recyclerView = inflatedView.findViewById( R.id.recycler_view );

        final WorldNamesAdapter worldNamesAdapter = new WorldNamesAdapter( worldNameArray , selectedWorldPosition , getContext() );

        worldNamesAdapter.setCallback( position -> {
            results.putInt( RESULT_POSITION , position );
            sounds.playRePop();
            startDismissAnimation();
        } );

        final DividerItemOffsetDecoration dividerItemOffsetDecoration = new DividerItemOffsetDecoration( 0 , Dialogs.WORLD_SELECTOR_DIVIDER_PAD );
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager( getContext() );
        linearLayoutManager.setOrientation( RecyclerView.VERTICAL );
        recyclerView.setLayoutManager( linearLayoutManager );
        recyclerView.setAdapter( worldNamesAdapter );
        recyclerView.addItemDecoration( dividerItemOffsetDecoration );

        inflatedView.setOnClickListener( v -> startDismissAnimation() );

        return inflatedView;
    }

    @Override
    public void onDismiss( @NonNull final DialogInterface dialog ) {
        sendResults( REQUEST_CODE , -1 , results );
        super.onDismiss( dialog );
    }

    public void show( @NonNull final FragmentManager manager ) {
        super.show( manager , "DialogWorldSelector" );
    }
}