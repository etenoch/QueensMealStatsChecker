package com.enochtam.queensmealstatschecker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;


public class PrefsFragment extends PreferenceFragment {

    //TODO Trigger Reset when user/pass is changed

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        
        Preference myPref = (Preference) findPreference("reset_application");
        myPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                     public boolean onPreferenceClick(Preference preference) {
                         SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(getActivity());
                         Editor editor = prefs.edit();
                         editor.clear();
                         editor.commit();
                         Intent i = new Intent(getActivity(), LoginActivity.class);
                         i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                         startActivity(i);
                         return true;
                     }
                 });
    }
}
