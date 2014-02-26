package com.enochtam.queensmealstatschecker;

import android.os.Bundle;
import android.preference.PreferenceFragment;


public class PrefsFragment extends PreferenceFragment {

    //TODO Trigger Reset when user/pass is changed
    //TODO "Reset ALl" button

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
