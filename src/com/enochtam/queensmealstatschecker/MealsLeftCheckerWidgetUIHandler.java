package com.enochtam.queensmealstatschecker;

import java.util.Set;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

public class MealsLeftCheckerWidgetUIHandler extends WidgetUIHandler{
	
	public MealsLeftCheckerWidgetUIHandler(Context context,RemoteViews remoteView, AppWidgetManager appWidgetManager){
		super(context, remoteView, appWidgetManager);

	}
	
	public void setDataFromSharedPrefs(){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String leftThisWeek = prefs.getString("leftThisWeek", "");
        long unixTime = prefs.getLong("lastUpdated", 0);
        String currentDateTime = Helper.getTime(unixTime);

        setWidgetLeftThisWeek(leftThisWeek);
        setWidgetLastUpdated(currentDateTime);

	}
	
	public void setWidgetLeftThisWeek(String text){
        remoteView.setTextViewText(R.id.widgetLeftThisWeek, text);

	}
	public void setWidgetFlexFunds(String text){
        //remoteView.setTextViewText(R.id.widgetFlexFunds, text);

	}
	public void setWidgetDiningDollars(String text){
        //remoteView.setTextViewText(R.id.widgetDiningDollars, text);

	}
	public void setWidgetLastUpdated(String text){
        remoteView.setTextViewText(R.id.widgetLastUpdated, text);

	}
	
	public void updateWidget(){
	    ComponentName mealCheckerWidget = new ComponentName(context,MealsLeftCheckerWidgetProvider.class);
	    appWidgetManager.updateAppWidget(mealCheckerWidget, remoteView);		
	}
	

}
