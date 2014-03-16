package com.enochtam.queensmealstatschecker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends ActionBarActivity {
    SharedPreferences prefs;

    View rootView;
	MainActivityUIHandler ui;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_view);

        //prefs = PreferenceManager.getDefaultSharedPreferences(this);

        //firstTimeRun();

        //rootView = getWindow().getDecorView().findViewById(android.R.id.content);
    	//ui = new MainActivityUIHandler(this, rootView);
        
    	
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(true);

        Tab tab1 = actionBar
            .newTab()
            .setText("Meal Stats")
            .setTag("MealStatsFragment")
            .setTabListener(new SupportFragmentTabListener<StatsFragment>(R.id.content_view, this,"stats", StatsFragment.class));
        actionBar.addTab(tab1);
        actionBar.selectTab(tab1);
    	
        Tab tab2 = actionBar
                .newTab()
                .setText("Caf Menu")
                .setTag("CafMenuFragment")
                .setTabListener(new SupportFragmentTabListener<MenuFragment>(R.id.content_view,this,"menu", MenuFragment.class));
        actionBar.addTab(tab2);
        

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
        }else if(id == R.id.action_about){
            Intent i = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
