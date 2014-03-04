package com.enochtam.queensmealstatschecker;

import java.util.ArrayList;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivityUIHandler {
	private Context context;
    private SharedPreferences prefs;

    //instance variables for main activity
    private TextView flexFundsTextView;
    private TextView diningDollarsTextView;
    private TextView status1TextView;
    private TextView loginNumberTextView;
    private TextView status2TextView;
    private TextView leftThisWeekTextView;
    private LinearLayout mealPlanLinearLayout;
	
	public MainActivityUIHandler(Context context,View rootView){
		this.context= context;
		
        flexFundsTextView = (TextView)rootView.findViewById(R.id.flexFundsTextView);
        diningDollarsTextView = (TextView)rootView.findViewById(R.id.diningDollarsTextView);
        status1TextView = (TextView)rootView.findViewById(R.id.status1TextView);
        status2TextView = (TextView)rootView.findViewById(R.id.status2TextView);
        mealPlanLinearLayout = (LinearLayout)rootView.findViewById(R.id.mealPlanLinearLayout);
        leftThisWeekTextView = (TextView)rootView.findViewById(R.id.leftThisWeekTextView);
        loginNumberTextView = (TextView)rootView.findViewById(R.id.loginNumberTextView);

	}
	public void setDataFromSharedPrefs(){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String flexFunds = prefs.getString("flexFunds", "");
        String diningDollars = prefs.getString("diningDollars", "");
        String leftThisWeek = prefs.getString("leftThisWeek", "");
        long lastUpdatedUnix = prefs.getLong("lastUpdated", 0);
        String lastUpdated = Helper.getTime(lastUpdatedUnix);
        
        if (flexFunds == null || flexFunds.isEmpty()) {
        	setFlexFundsTextView("-");
        }else{
        	setFlexFundsTextView(flexFunds);
        }
        if (diningDollars == null || diningDollars.isEmpty()) {
        	setDiningDollarsTextView("-");
        }else{
        	setDiningDollarsTextView(diningDollars);
        }
        if (lastUpdated == null || lastUpdated.isEmpty()) {
        	setStatus2TextView("Last Updated: never");
        }else{
        	setStatus2TextView("Last Updated: "+ lastUpdated);
        }
        if (leftThisWeek == null || leftThisWeek.isEmpty()) {
        	setLeftThisWeekTextView("-");
        }else{
        	setLeftThisWeekTextView(leftThisWeek);

        }
        
	}
    public void setFlexFundsTextView(String text){
    	flexFundsTextView.setText(text);
    }
    public void setDiningDollarsTextView(String text){
        diningDollarsTextView.setText(text);
    }
    public void setloginNumberTextView(String text){
    	loginNumberTextView.setText(text);
    }
    public void setStatus1TextView(String text){
        status1TextView.setTextColor(context.getResources().getColor(R.color.black));
        status1TextView.setText(text);
    }
    public void setStatus1TextView(String text,boolean red){
        status1TextView.setTextColor(context.getResources().getColor(R.color.queensRed));
        status1TextView.setText(text);
    }
    public void setStatus2TextView(String text){
        status1TextView.setTextColor(context.getResources().getColor(R.color.black));
        status2TextView.setText(text);
    }
    public void setStatus2TextView(String text,boolean red){
        status1TextView.setTextColor(context.getResources().getColor(R.color.queensRed));
        status2TextView.setText(text);
    }
    public void setLeftThisWeekTextView(String text){
        leftThisWeekTextView.setText(text);
    }
    public void setMealPlanLinearLayout(ArrayList<TextView> data){
        mealPlanLinearLayout.removeAllViews();
        for(TextView textView : data){
        	mealPlanLinearLayout.addView(textView);
        }


        
    }
	
}
