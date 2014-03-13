package com.enochtam.queensmealstatschecker;

import java.util.Set;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

public class MealCheckerWidgetUIHandler {

	private Context context;
	private RemoteViews remoteView;
	private AppWidgetManager appWidgetManager;
	
    private SharedPreferences prefs;

	
	public MealCheckerWidgetUIHandler(Context context,RemoteViews remoteView, AppWidgetManager appWidgetManager){
		this.appWidgetManager = appWidgetManager;
		this.context = context;
		this.remoteView = remoteView;
	}
	
	public void setDataFromSharedPrefs(){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String flexFunds = prefs.getString("flexFunds", "");
        String diningDollars = prefs.getString("diningDollars", "");
        String leftThisWeek = prefs.getString("leftThisWeek", "");
        long unixTime = prefs.getLong("lastUpdated", 0);
        String currentDateTime = Helper.getTime(unixTime);

        setWidgetLeftThisWeek(leftThisWeek);
        setWidgetFlexFunds(flexFunds);
        setWidgetDiningDollars(diningDollars);
        setWidgetLastUpdated("Last Updated: "+currentDateTime);

	}
	
	public void setWidgetLeftThisWeek(String text){
        remoteView.setTextViewText(R.id.widgetLeftThisWeek, text);

	}
	public void setWidgetFlexFunds(String text){
        remoteView.setTextViewText(R.id.widgetFlexFunds, text);

	}
	public void setWidgetDiningDollars(String text){
        remoteView.setTextViewText(R.id.widgetDiningDollars, text);

	}
	public void setWidgetLastUpdated(String text){
        remoteView.setTextViewText(R.id.widgetLastUpdated, text);

	}
	
	public void updateWidget(){
	    ComponentName mealCheckerWidget = new ComponentName(context,MealCheckerWidgetProvider.class);
	    appWidgetManager.updateAppWidget(mealCheckerWidget, remoteView);		
	}
	

}
