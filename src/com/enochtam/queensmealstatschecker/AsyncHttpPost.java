package com.enochtam.queensmealstatschecker;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;

public class AsyncHttpPost  extends AsyncTask<String, String, String> {
    public String loginLink = "https://qusw.housing.queensu.ca:4430/login.asp?action=login";
    public String mealStatsLink = "https://qusw.housing.queensu.ca:4430/common/mealstats.asp";
    public String dateFormatStr = "yyyy/MM/dd HH:mm";

    private HashMap<String, String> mData = null;// post data

    private Context mContext;
    private View rootView;
    private RemoteViews remoteView;
    private SharedPreferences prefs;
    private AppWidgetManager appWidgetManager;

    //instance variables for main activity
    private TextView flexFundsTextView;
    private TextView diningDollarsTextView;
    private TextView loginNumberTextView;
    private TextView status1TextView;
    private TextView status2TextView;
    private TextView leftThisWeekTextView;
    private LinearLayout mealPlanLinearLayout;


    private boolean inWidget = false;

    public AsyncHttpPost(HashMap<String, String> data,Context context,View rootView) {
        mData = data;
        this.mContext = context;
        this.rootView = rootView;

        prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

        flexFundsTextView = (TextView)rootView.findViewById(R.id.flexFundsTextView);
        diningDollarsTextView = (TextView)rootView.findViewById(R.id.diningDollarsTextView);
        loginNumberTextView = (TextView)rootView.findViewById(R.id.loginNumberTextView);
        status1TextView = (TextView)rootView.findViewById(R.id.status1TextView);
        status2TextView = (TextView)rootView.findViewById(R.id.status2TextView);
        mealPlanLinearLayout = (LinearLayout)rootView.findViewById(R.id.mealPlanLinearLayout);
        leftThisWeekTextView = (TextView)rootView.findViewById(R.id.leftThisWeekTextView);
    }

    public AsyncHttpPost(HashMap<String, String> data,Context context,RemoteViews remoteView,AppWidgetManager appWidgetManager) {
        mData = data;
        this.mContext = context;
        this.remoteView = remoteView;
        inWidget = true;

        this.appWidgetManager = appWidgetManager;

        prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

    }

    //background
    @Override
    protected String doInBackground(String... params) {
        byte[] result = null;
        String loginResultStr = "";
        String htmlResult = "";

        HttpClient client = new DefaultHttpClient();
        CookieStore cookieStore = new BasicCookieStore();
        HttpContext httpContext = new BasicHttpContext();

        HttpPost post = new HttpPost(loginLink);

        try {
            // set up post data
            ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
            Iterator<String> it = mData.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                nameValuePair.add(new BasicNameValuePair(key, mData.get(key)));
            }

            post.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
            HttpResponse response = client.execute(post,httpContext);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpURLConnection.HTTP_OK){
                result = EntityUtils.toByteArray(response.getEntity());
                loginResultStr = new String(result, "UTF-8");
            }else{
                return "unidentified";
            }
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }

        HttpGet httpget_scrape = new HttpGet(mealStatsLink);
        try{
            HttpResponse response2 = client.execute(httpget_scrape, httpContext);
            StatusLine statusLine2 = response2.getStatusLine();

            if(statusLine2.getStatusCode() == HttpURLConnection.HTTP_OK){
                String htmlString = EntityUtils.toString(response2.getEntity());
                htmlResult = htmlString;
            }else{
                return "unidentified";
            }
        }catch (IOException e){
            e.printStackTrace();
            return "unidentified";
        }
        //Log.e("MainActivity", "==========================================================================");
        //Log.e("MainActivity", htmlResult);
        //Log.e("MainActivity", "==========================================================================");

        return htmlResult;
    }

    @Override
    protected void onPostExecute(String result) {
        if(inWidget){
            onPostExecuteInWidget(result);
        }else{
            onPostExecuteNormal(result);
        }

    }
    protected void onPostExecuteNormal(String result){
        if((result.toLowerCase().contains("unidentified".toLowerCase()))){
            status1TextView.setTextColor(mContext.getResources().getColor(R.color.queensRed));
            status1TextView.setText("An Error Has Occurred");
            status2TextView.setTextColor(mContext.getResources().getColor(R.color.queensRed));
            status2TextView.setText("Check Internet Connection");
        }else if(result.toLowerCase().contains("error".toLowerCase())){
            status1TextView.setTextColor(mContext.getResources().getColor(R.color.queensRed));
            status1TextView.setText("An Error Has Occurred");
            status2TextView.setTextColor(mContext.getResources().getColor(R.color.queensRed));
            status2TextView.setText("Check Username and Password in Settings");
        }else{
            MealStats mealStats = new MealStats(result);
            mealStats.parseHtml();
            String totalFlex =  "$" +String.valueOf(mealStats.getTotalFlex());
            String totalDining = "$"+String.valueOf(mealStats.getTotalDining());
            if(flexFundsTextView!=null){
                flexFundsTextView.setText(totalFlex);
            }
            if(diningDollarsTextView!=null){
                diningDollarsTextView.setText(totalDining);
            }
            if(status1TextView!=null){
                status1TextView.setTextColor(mContext.getResources().getColor(R.color.black));
                status1TextView.setText("Data Loaded");
            }


            String leftThisWeek=null;
            final float scale = mContext.getResources().getDisplayMetrics().density;
            if(!mealStats.mealData.isEmpty()){
                if(mealPlanLinearLayout!=null){
                    mealPlanLinearLayout.removeAllViews();
                }
                for(NameValuePair pair : mealStats.mealData){
                    TextView textView = new TextView(mContext);

                    if(!pair.getName().contains("left for week")){
                        textView.setTextAppearance(mContext, android.R.style.TextAppearance_Medium);
                        textView.setPadding((int) (10 * scale + 0.5f), 5, 10, 5);

                        String text = "<font color=#000000>"+pair.getName()+"</font> <font color=#c30006>"+pair.getValue()+"</font>";
                        textView.setText(Html.fromHtml(text));
                        if(mealPlanLinearLayout!=null){
                            mealPlanLinearLayout.addView(textView);
                        }
                    }else{
                        leftThisWeek = pair.getValue();
                    }
                }
                if(leftThisWeek!=null&&leftThisWeekTextView!=null){
                    leftThisWeekTextView.setText(leftThisWeek);
                }
            }

            DateFormat dateFormat = new SimpleDateFormat(dateFormatStr);
            dateFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));
            long unixTime = System.currentTimeMillis() / 1000L;
            Date date = new Date ();
            date.setTime((long)unixTime*1000);
            String currentDateTime = dateFormat.format(date);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong("lastUpdated", unixTime);
            editor.putString("flexFunds", totalFlex);
            editor.putString("diningDollars", totalDining);
            editor.putString("leftThisWeek", leftThisWeek);
            editor.commit();
            if(status2TextView!=null){
                status2TextView.setTextColor(mContext.getResources().getColor(R.color.black));
                status2TextView.setText("Last Updated: "+currentDateTime+" EST");
            }

        }
    }
    protected void onPostExecuteInWidget(String result){
        if((result.toLowerCase().contains("unidentified".toLowerCase()))){

        }else if(result.toLowerCase().contains("error".toLowerCase())){

        }else{
            MealStats mealStats = new MealStats(result);
            mealStats.parseHtml();
            String totalFlex =  "$" +String.valueOf(mealStats.getTotalFlex());
            String totalDining = "$"+String.valueOf(mealStats.getTotalDining());

            remoteView.setTextViewText(R.id.widgetFlexFunds, totalFlex);
            remoteView.setTextViewText(R.id.widgetDiningDollars, totalDining);

            String leftThisWeek=null;
            if(!mealStats.mealData.isEmpty()){
                for(NameValuePair pair : mealStats.mealData){
                    if(pair.getName().contains("left for week")){
                        leftThisWeek = pair.getValue();
                    }
                }
                if(leftThisWeek!=null){
                    remoteView.setTextViewText(R.id.widgetLeftThisWeek, leftThisWeek);
                }
            }

            DateFormat dateFormat = new SimpleDateFormat(dateFormatStr);
            dateFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));
            long unixTime = System.currentTimeMillis() / 1000L;
            Date date = new Date ();
            date.setTime((long)unixTime*1000);
            String currentDateTime = dateFormat.format(date);

            remoteView.setTextViewText(R.id.widgetLastUpdated, "Last Updated: "+currentDateTime);


            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong("lastUpdated", unixTime);
            editor.putString("flexFunds", totalFlex);
            editor.putString("diningDollars", totalDining);
            editor.putString("leftThisWeek", leftThisWeek);
            editor.commit();


            Intent launchAppIntent = new Intent(mContext, MainActivity.class);
            PendingIntent launchAppPendingIntent = PendingIntent.getActivity(mContext,
                    0, launchAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteView.setOnClickPendingIntent(R.id.full_widget, launchAppPendingIntent);

            ComponentName tutListWidget = new ComponentName(mContext,
                    MealCheckerWidgetProvider.class);
            appWidgetManager.updateAppWidget(tutListWidget, remoteView);
        }
    }
}
