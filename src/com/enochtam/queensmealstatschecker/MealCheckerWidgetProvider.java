package com.enochtam.queensmealstatschecker;

import java.util.HashMap;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

public class MealCheckerWidgetProvider extends AppWidgetProvider {
    SharedPreferences prefs;
    AppWidgetManager appWidgetManager;
    Context context;
    RemoteViews remoteView;
    MealCheckerWidgetUIHandler widgetUIHandler;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,int[] appWidgetIds) {
    	updateWidget(context,appWidgetManager);
        loadPreviousDataWidget();
        refreshDataWidget();
    }
    
    public void updateWidget(Context context,AppWidgetManager appWidgetManager){
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
        this.remoteView = new RemoteViews(context.getPackageName(),R.layout.mealchecker_appwidget_layout);
        this.appWidgetManager = appWidgetManager;
        this.context=context;
        
        widgetUIHandler = new MealCheckerWidgetUIHandler(context, remoteView, appWidgetManager);

        Intent launchAppIntent = new Intent(context, MainActivity.class);
        PendingIntent launchAppPendingIntent = PendingIntent.getActivity(context,
                0, launchAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteView.setOnClickPendingIntent(R.id.full_widget, launchAppPendingIntent);
		
        loadPreviousDataWidget();
    }
    public void loadPreviousDataWidget(){
		widgetUIHandler.setDataFromSharedPrefs();
		widgetUIHandler.updateWidget();

    }
    public void refreshDataWidget(){
        HashMap<String, String> data = new HashMap<String, String>();

        String username = prefs.getString("username", "");
        String password = prefs.getString("password", "");
        
        if(Helper.checkUserAndPass(username, password) && Helper.isOnline(context)){
            data.put("username", username);
            data.put("password", password);
	        AsyncHttpPost asyncHttpPost = new AsyncHttpPost(data,context,remoteView,appWidgetManager);
	        asyncHttpPost.execute();
            
        }
    
    }

}
