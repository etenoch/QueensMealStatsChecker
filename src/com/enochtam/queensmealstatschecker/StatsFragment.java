package com.enochtam.queensmealstatschecker;

import java.util.HashMap;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class StatsFragment extends Fragment{
	
	MainActivityUIHandler ui;
    View rootView;
    SharedPreferences prefs;
    View fragView;
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		
		fragView = inflater.inflate(R.layout.activity_main, container, false); 
		
		rootView = fragView;
		
		ui = new MainActivityUIHandler(getActivity(), rootView);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		
	    setHasOptionsMenu(true);

		return fragView;
    }
    
    public void onCreate(Bundle savedInstanceState) {
    	 super.onCreate(savedInstanceState);

	}
    
    public void onResume(){
        super.onResume();
        firstTimeRun();
        loadPreviousData();
        refreshData();
    	Log.e("TAM_APP", "from on resume stats fragment");

    }
    
    private boolean firstTimeRun(){
        String username = prefs.getString("username", "");
        String password = prefs.getString("password", "");
        if ( (username == null || username.isEmpty()) && (password == null || password.isEmpty())) {
            Intent i = new Intent(getActivity(), LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        return false;
    }

    private boolean refreshData(){
        HashMap<String, String> data = new HashMap<String, String>();
        
        String username = prefs.getString("username", "");
        String password = prefs.getString("password", "");
        
        
        if(Helper.checkUserAndPass(username, password)){

        	ui.setloginNumberTextView(username);
            data.put("username", username);
            data.put("password", password);
        }else{
        	ui.setStatus1TextView("Username and Password Not Provided",true);
            return false;
        }
        if(!Helper.isOnline(getActivity())){
            ui.setStatus1TextView("No Internet Connection",true);
            loadPreviousData();
            return false;
        }

        //call the http request stuff here

        ui.setStatus1TextView("Loading Data",true);

        AsyncHttpPost asyncHttpPost = new AsyncHttpPost(data,getActivity(),rootView);
        asyncHttpPost.execute();

    	AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getActivity());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(getActivity(), MealCheckerWidgetProvider.class));
        if (appWidgetIds.length > 0) {
        	//new MealCheckerWidgetProvider().onUpdate(this, appWidgetManager, appWidgetIds);
        }
        
        return true;
    }

    private void loadPreviousData(){
		ui.setDataFromSharedPrefs();
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(getActivity(), PreferencesActivity.class);
            startActivity(i);
            return true;
        }else if(id == R.id.action_refresh){
        	refreshData();
        	return true;
        }else if(id == R.id.action_about){
            Intent i = new Intent(getActivity(), AboutActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	
}
