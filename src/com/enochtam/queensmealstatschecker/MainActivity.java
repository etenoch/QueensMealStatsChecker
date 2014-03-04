package com.enochtam.queensmealstatschecker;

import java.util.HashMap;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
    SharedPreferences prefs;

    View rootView;
	MainActivityUIHandler ui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        firstTimeRun();

        rootView = getWindow().getDecorView().findViewById(android.R.id.content);
    	ui = new MainActivityUIHandler(this, rootView);
        
        
        if(savedInstanceState==null){
            loadPreviousData();
            refreshData();
        	
        }

    }

    @Override
    public void onResume(){
        super.onResume();
        firstTimeRun();
        loadPreviousData();
        refreshData();
    }

    private boolean firstTimeRun(){
        String username = prefs.getString("username", "");
        String password = prefs.getString("password", "");
        if ( (username == null || username.isEmpty()) && (password == null || password.isEmpty())) {
            Intent i = new Intent(this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
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
            data.put("Login", " Log In ");
            data.put("j_username", username);
            data.put("j_password", password);
        }else{
        	ui.setStatus1TextView("Username and Password Not Provided",true);
            return false;
        }
        if(!Helper.isOnline(this)){
            ui.setStatus1TextView("No Internet Connection",true);
            loadPreviousData();
            return false;
        }

        //call the http request stuff here

        ui.setStatus1TextView("Loading Data",true);

        AsyncHttpPost asyncHttpPost = new AsyncHttpPost(data,this,rootView);
        asyncHttpPost.execute();

    	AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, MealCheckerWidgetProvider.class));
        if (appWidgetIds.length > 0) {
        	new MealCheckerWidgetProvider().onUpdate(this, appWidgetManager, appWidgetIds);
        }
        
        return true;
    }

    private void loadPreviousData(){
		ui.setDataFromSharedPrefs();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(MainActivity.this, PreferencesActivity.class);
            startActivity(i);
            return true;
        }else if(id == R.id.action_refresh){
            long currentUnixTime = System.currentTimeMillis() / 1000L;
            long lastUpdatedUnix = prefs.getLong("lastUpdated", 0);
        	if((currentUnixTime-lastUpdatedUnix)>30){
                refreshData();
                Toast.makeText(getApplicationContext(), "Refreshing Data", Toast.LENGTH_SHORT).show();
        	}else{
                Toast.makeText(getApplicationContext(), "Too Soon, Please wait a while", Toast.LENGTH_SHORT).show();
        	}
        	
            return true;
        }else if(id == R.id.action_about){
            Intent i = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
