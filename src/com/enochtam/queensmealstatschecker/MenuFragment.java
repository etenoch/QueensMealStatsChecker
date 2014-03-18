package com.enochtam.queensmealstatschecker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MenuFragment extends Fragment{
	
	MenuUIHandler ui;
    View rootView;
    SharedPreferences prefs;
    View fragView;
	TextView tv;
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		fragView = inflater.inflate(R.layout.activity_menu, container, false); 
		
		rootView = fragView;
		
		ui = new MenuUIHandler(getActivity(), rootView);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		
	    setHasOptionsMenu(true);
    	refreshData();

		return fragView;
    }
    
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

	}
    

    
    public void onResume(){
    	super.onResume();

    }
    private boolean refreshData(){
    	//TextView loadingIndicator=  (TextView) rootView.findViewById(R.id.loadingIndicator);
    	//loadingIndicator.setText("Loading...");
    	    	
    	 if(!Helper.isOnline(getActivity())){
		 	tv = (TextView) rootView.findViewById(R.id.menuListStatus);
    		tv.setText("An Error has occured.\nPlease check your internet connection.");
		 	return false;
         }
    	
        AsyncHttpGetMenu asyncHttpPost = new AsyncHttpGetMenu(getActivity(),rootView);
        asyncHttpPost.execute();
        
        return true;
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

