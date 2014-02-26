package com.enochtam.queensmealstatschecker;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class MealCheckerWidgetProvider extends AppWidgetProvider {
    SharedPreferences prefs;
    public String dateFormatStr = "yyyy/MM/dd HH:mm";
    AppWidgetManager appWidgetManager;
    Context context;

    private RemoteViews remoteView;


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,int[] appWidgetIds) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        this.remoteView = new RemoteViews(context.getPackageName(),R.layout.mealchecker_appwidget_layout);
        this.appWidgetManager = appWidgetManager;
        this.context=context;


        loadPreviousDataWidget();
        refreshDataWidget();
        finish();


    }

    public void loadPreviousDataWidget(){
        String flexFunds = prefs.getString("flexFunds", "");
        String diningDollars = prefs.getString("diningDollars", "");
        String leftThisWeek = prefs.getString("leftThisWeek", "");
        long unixTime = prefs.getLong("lastUpdated", 0);

        remoteView.setTextViewText(R.id.widgetFlexFunds, flexFunds);
        remoteView.setTextViewText(R.id.widgetDiningDollars, diningDollars);
        remoteView.setTextViewText(R.id.widgetLeftThisWeek, leftThisWeek);

        DateFormat dateFormat = new SimpleDateFormat(dateFormatStr);
        dateFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        Date date = new Date ();
        date.setTime((long)unixTime*1000);
        String currentDateTime = dateFormat.format(date);

        remoteView.setTextViewText(R.id.widgetLastUpdated, "Last Updated: "+currentDateTime);

    }
    public void refreshDataWidget(){
        HashMap<String, String> data = new HashMap<String, String>();
        //TODO check all the things
        String username = prefs.getString("username", "");
        String password = prefs.getString("password", "");
        data.put("username", username);
        data.put("password", password);

        AsyncHttpPost asyncHttpPost = new AsyncHttpPost(data,context,remoteView,appWidgetManager);
        asyncHttpPost.execute();
    }
    public void finish(){
        Intent launchAppIntent = new Intent(context, MainActivity.class);
        PendingIntent launchAppPendingIntent = PendingIntent.getActivity(context,
                0, launchAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteView.setOnClickPendingIntent(R.id.full_widget, launchAppPendingIntent);

        ComponentName tutListWidget = new ComponentName(context,
                MealCheckerWidgetProvider.class);
        appWidgetManager.updateAppWidget(tutListWidget, remoteView);
    }

}
