package com.enochtam.queensmealstatschecker;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class MainActivity extends ActionBarActivity {
    SharedPreferences prefs;

    TextView flexFundsTextView;
    TextView diningDollarsTextView;
    TextView loginNumberTextView;
    TextView status1TextView;
    TextView status2TextView;
    TextView leftThisWeekTextView;
    LinearLayout mealPlanLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        firstTimeRun();

        flexFundsTextView = (TextView)findViewById(R.id.flexFundsTextView);
        diningDollarsTextView = (TextView)findViewById(R.id.diningDollarsTextView);
        loginNumberTextView = (TextView)findViewById(R.id.loginNumberTextView);
        status1TextView = (TextView)findViewById(R.id.status1TextView);
        status2TextView = (TextView)findViewById(R.id.status2TextView);
        mealPlanLinearLayout = (LinearLayout)findViewById(R.id.mealPlanLinearLayout);
        leftThisWeekTextView = (TextView)findViewById(R.id.leftThisWeekTextView);

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

            loginNumberTextView.setText(username);
            data.put("username", username);
            data.put("password", password);
        }else{
            status1TextView.setTextColor(getResources().getColor(R.color.queensRed));
            status1TextView.setText("Username and Password Not Provided");
            return false;
        }
        if(!Helper.isOnline(this)){
            status1TextView.setTextColor(getResources().getColor(R.color.queensRed));
            status1TextView.setText("No Internet Connection");
            loadPreviousData();
            return false;
        }

        //call the http request stuff here

        status1TextView.setTextColor(getResources().getColor(R.color.queensRed));
        status1TextView.setText("Loading Data");

        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);

        AsyncHttpPost asyncHttpPost = new AsyncHttpPost(data,this,rootView);
        asyncHttpPost.execute();

    	AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, MealCheckerWidgetProvider.class));
        if (appWidgetIds.length > 0) {
            new MealCheckerWidgetProvider().onUpdate(this, appWidgetManager, appWidgetIds);
            //new MealCheckerWidgetProvider().updateWidget(this, appWidgetManager);
            //Log.e("widget - ", "******* Widget Updated **********");
        }
        
        return true;
    }

    private void loadPreviousData(){
        String flexFunds = prefs.getString("flexFunds", "");
        String diningDollars = prefs.getString("diningDollars", "");
        String leftThisWeek = prefs.getString("leftThisWeek", "");
        
        long lastUpdatedUnix = prefs.getLong("lastUpdated", 0);
        String lastUpdated = Helper.getTime(lastUpdatedUnix);

        if (flexFunds == null || flexFunds.isEmpty()) {
            flexFundsTextView.setText("No Data");
        }else{
            flexFundsTextView.setText(flexFunds);
        }
        if (diningDollars == null || diningDollars.isEmpty()) {
            diningDollarsTextView.setText("No Data");
        }else{
            diningDollarsTextView.setText(diningDollars);
        }
        if (lastUpdated == null || lastUpdated.isEmpty()) {
            status2TextView.setText("Last Updated: never");
        }else{
            status2TextView.setText("Last Updated: "+ lastUpdated);
        }
        if (leftThisWeek == null || leftThisWeek.isEmpty()) {
            leftThisWeekTextView.setText("No Data");
        }else{
            leftThisWeekTextView.setText(leftThisWeek);

        }

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
            refreshData();
            Toast.makeText(getApplicationContext(), "Refreshing Data", Toast.LENGTH_SHORT).show();
            return true;
        }else if(id == R.id.action_about){
            Intent i = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
