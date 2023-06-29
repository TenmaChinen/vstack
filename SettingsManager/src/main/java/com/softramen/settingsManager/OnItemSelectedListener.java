package com.softramen.settingsManager;

import android.view.View;
import android.widget.AdapterView;

public interface OnItemSelectedListener extends AdapterView.OnItemSelectedListener {
	@Override
	void onItemSelected( final AdapterView<?> adapterView , View view , int position , long l );

	@Override
	default void onNothingSelected( final AdapterView<?> adapterView ) {

	}
}
