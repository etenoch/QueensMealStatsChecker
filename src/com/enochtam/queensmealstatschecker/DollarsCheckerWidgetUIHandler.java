package com.enochtam.queensmealstatschecker;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

public class DollarsCheckerWidgetUIHandler extends WidgetUIHandler{
	
	public DollarsCheckerWidgetUIHandler(Context context,RemoteViews remoteView, AppWidgetManager appWidgetManager){
		super(context, remoteView, appWidgetManager);
	}
	
	public void setDataFromSharedPrefs(){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String flexFunds = prefs.getString("flexFunds", "");
        String diningDollars = prefs.getString("diningDollars", "");
        long unixTime = prefs.getLong("lastUpdated", 0);
        String currentDateTime = Helper.getTime(unixTime);

        setWidgetFlexFunds(flexFunds);
        setWidgetDiningDollars(diningDollars);
        setWidgetLastUpdated(currentDateTime);

	}


	public void setWidgetLeftThisWeek(String text){

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
	    ComponentName mealCheckerWidget = new ComponentName(context,DollarsCheckerWidgetProvider.class);
	    appWidgetManager.updateAppWidget(mealCheckerWidget, remoteView);		
	}
}
