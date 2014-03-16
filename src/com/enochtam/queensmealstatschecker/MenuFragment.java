package com.enochtam.queensmealstatschecker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MenuFragment extends Fragment{
	
	MenuUIHandler ui;
    View rootView;
    SharedPreferences prefs;
    View fragView;
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		fragView = inflater.inflate(R.layout.activity_menu, container, false); 
		
		rootView = fragView;
		
		ui = new MenuUIHandler(getActivity(), rootView);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		
	    setHasOptionsMenu(true);

		return fragView;
    }
	
}

