package com.enochtam.queensmealstatschecker;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

public abstract class WidgetUIHandler {
	
	protected Context context;
	protected RemoteViews remoteView;
	protected AppWidgetManager appWidgetManager;
	protected SharedPreferences prefs;
	
	public WidgetUIHandler(Context context,RemoteViews remoteView, AppWidgetManager appWidgetManager){
		this.appWidgetManager = appWidgetManager;
		this.context = context;
		this.remoteView = remoteView;
	}
	
	public abstract void setWidgetLeftThisWeek(String text);
	public abstract void setWidgetFlexFunds(String text);
	public abstract void setWidgetDiningDollars(String text);
	public abstract void setWidgetLastUpdated(String text);
	public abstract void setDataFromSharedPrefs();
	public abstract void updateWidget();
    
    
}
