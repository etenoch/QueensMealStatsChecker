package com.enochtam.queensmealstatschecker;


import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

public class AsyncHttpPost  extends AsyncTask<String, String, String> {
	// constants for httprequests
    private final String loginLink = "https://qusw.housing.queensu.ca:4430/login.asp?action=login";
    private final String mealStatsLink = "https://qusw.housing.queensu.ca:4430/common/mealstats.asp";
    private final String statusLink = "http://enochtam.com/status.html";
    private final String msgLink = "http://enochtam.com/msg.html";

    private HashMap<String, String> mData = null;// post data

    // context related variables
    private Context mContext;
	private View rootView;
    private SharedPreferences prefs;
    //widget related
    private RemoteViews remoteView;
    private AppWidgetManager appWidgetManager;

    private boolean inWidget = false;//if only updating the widget
    private String widgetType;
    
    //http request variables
    private HttpClient client;
    private CookieStore cookieStore;
    private HttpContext httpContext;
    
    private String msg = null;// msg used if app is disabled
    
    
    public AsyncHttpPost(HashMap<String, String> data,Context context,View rootView) {
        this.mData = data;
        this.mContext = context;
        this.rootView = rootView;

        prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

    }

    public AsyncHttpPost(HashMap<String, String> data,Context context,RemoteViews remoteView,AppWidgetManager appWidgetManager, String widgetType) {
        this.mData = data;
        this.mContext = context;
        this.remoteView = remoteView;
        this.inWidget = true;

        this.appWidgetManager = appWidgetManager;

        this.widgetType = widgetType;
        
        prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

    }
        
    //background
    @SuppressWarnings("unused")
	@Override
    protected String doInBackground(String... params) {
        byte[] result = null;
        String loginResultStr = "";
        String htmlResult = "";

        client = new DefaultHttpClient();
        client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
        cookieStore = new BasicCookieStore();
        httpContext = new BasicHttpContext();

        try{
        	if (!checkAppStatus()){
        		return"override";
        	}
        	if(!loginRequest()){
        		return"unidentified login request";
        	}
        	htmlResult = scrapeRequest();
        }catch (IOException e){
            //e.printStackTrace();
        	
        	StringWriter errors = new StringWriter();
        	//e.printStackTrace(new PrintWriter(errors));
        	//Log.e("TAM_APP",errors.toString());
        	return "unidentified "+errors.toString();
        	
            //return "unidentified io exception";
        }catch (Exception e){
            //e.printStackTrace();
            return "unidentified exception";
        }
        
        return htmlResult;
    }

    protected boolean checkAppStatus() throws IOException{// returns false if app is disabled
        HttpGet check_status = new HttpGet(statusLink);

        HttpResponse response0 = client.execute(check_status, httpContext);
        StatusLine statusLine0 = response0.getStatusLine();

        if(statusLine0.getStatusCode() == HttpURLConnection.HTTP_OK){
            String string = EntityUtils.toString(response0.getEntity());
            if(string.toLowerCase().contains("false")){
                HttpGet get_msg = new HttpGet(msgLink);
               	HttpResponse msg_res = client.execute(get_msg, httpContext);
                StatusLine msg_statusLink = msg_res.getStatusLine();
                if(msg_statusLink.getStatusCode() == HttpURLConnection.HTTP_OK){
            		msg = EntityUtils.toString(msg_res.getEntity());
                }
            	return false;
            }

        }
    	return true;
    }
    
    protected boolean loginRequest() throws UnsupportedEncodingException, Exception{
        HttpPost post = new HttpPost(loginLink);
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
        if(statusLine.getStatusCode() != HttpURLConnection.HTTP_OK){
        	return false;
        }
    	return true;
    }
    
    protected String scrapeRequest() throws IOException{
        HttpGet httpget_scrape = new HttpGet(mealStatsLink);
        HttpResponse response2 = client.execute(httpget_scrape, httpContext);
        StatusLine statusLine2 = response2.getStatusLine();

        if(statusLine2.getStatusCode() == HttpURLConnection.HTTP_INTERNAL_ERROR){
        	return "possible login error";
        }else if(statusLine2.getStatusCode() != HttpURLConnection.HTTP_OK){
        	return "unidentified scrape request status line";
        }else{
        	String htmlString = EntityUtils.toString(response2.getEntity());
        	return htmlString;
        }

    }
    
    @Override
    protected void onPostExecute(String result) {
        if(inWidget){
            if( !(result.toLowerCase().contains("unidentified".toLowerCase())) 
            		&& !(result.toLowerCase().contains("error".toLowerCase())) 
            		&& !(result.toLowerCase().contains("override".toLowerCase())) ){
            	WidgetUIHandler widgetUIHandler = null;
            	if(widgetType=="main"){
            		 widgetUIHandler = new MealCheckerWidgetUIHandler(mContext, remoteView, appWidgetManager);
            	}else if(widgetType=="dollars"){
            		 widgetUIHandler = new DollarsCheckerWidgetUIHandler(mContext, remoteView, appWidgetManager);
            	}else if(widgetType=="meals"){
        			widgetUIHandler = new MealsLeftCheckerWidgetUIHandler(mContext, remoteView, appWidgetManager);
            	}
	            onPostExecuteInWidget(result, widgetUIHandler);
            }
        }else {
        	MainActivityUIHandler uiHandler = new MainActivityUIHandler(mContext, rootView);
        	if(result.toLowerCase().contains("error".toLowerCase())){  //login error
        		uiHandler.setStatus1TextView("An Error Has Occurred",true);
        		uiHandler.setStatus2TextView("Check Username and Password in Settings",true);
        	
        	}else if((result.toLowerCase().contains("unidentified".toLowerCase()))){  //http request error. most likely internet issue
        		uiHandler.setStatus1TextView("An Error Has Occurred",true);
        		uiHandler.setStatus2TextView("Check Internet Connection",true);
 
	        }else if(result.toLowerCase().contains("override".toLowerCase())) {  // app status overide
	        	
	        	new AlertDialog.Builder(mContext)
	            .setTitle("Important Message")
	            .setMessage(msg)
	            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) { 
	                
	                }
	             })
	            .setIcon(R.drawable.ic_action_warning)
	            .show();
	            
        		uiHandler.setStatus1TextView(msg,true);
	        }else{
	        	onPostExecuteNormal(result,uiHandler);
	        }
        	
        }
    	

    }
    
    protected void onPostExecuteNormal(String result, MainActivityUIHandler uiHandler){

            MealStats mealStats = new MealStats(result);
            mealStats.parseHtml();
            String totalFlex =  "$" +String.valueOf(mealStats.getTotalFlex());
            String totalDining = "$"+String.valueOf(mealStats.getTotalDining());

            ArrayList<TextView> linearLayoutData = new ArrayList<TextView>();

            String leftThisWeek=null;
            
            final float scale = mContext.getResources().getDisplayMetrics().density;
            if(!mealStats.mealData.isEmpty()){
                for(NameValuePair pair : mealStats.mealData){
                    TextView textView = new TextView(mContext);
                    if(!pair.getName().contains("left for week")){
                        textView.setTextAppearance(mContext, android.R.style.TextAppearance_Medium);
                        textView.setPadding((int) (10 * scale + 0.5f), 5, 10, 5);

                        String text = "<font color=#000000>"+pair.getName()+"</font> <font color=#c30006>"+pair.getValue()+"</font>";
                        textView.setText(Html.fromHtml(text));
                        linearLayoutData.add(textView);

                    }else{
                        leftThisWeek = pair.getValue();
                    }
                }
            }

            long unixTime = System.currentTimeMillis() / 1000L;
            String currentDateTime = Helper.getTime(unixTime);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong("lastUpdated", unixTime);
            editor.putString("flexFunds", totalFlex);
            editor.putString("diningDollars", totalDining);
            editor.putString("leftThisWeek", leftThisWeek);
            editor.commit();
            
            uiHandler.setFlexFundsTextView(totalFlex);
            uiHandler.setDiningDollarsTextView(totalDining);
            uiHandler.setStatus2TextView("Last Updated: "+currentDateTime);
            if(leftThisWeek!=null){
            	uiHandler.setLeftThisWeekTextView(leftThisWeek);            	
            }
            uiHandler.setMealPlanLinearLayout(linearLayoutData);

            uiHandler.setStatus1TextView("Data Loaded");
            
            
            // update widget
            RemoteViews temp_remoteView;
        	AppWidgetManager temp_appWidgetManager;
        	WidgetUIHandler widgetUIHandler;
        	temp_appWidgetManager = AppWidgetManager.getInstance(mContext);
        	
        	temp_remoteView = new RemoteViews(mContext.getPackageName(),R.layout.mealchecker_appwidget_layout);
        	widgetUIHandler = new MealCheckerWidgetUIHandler(mContext, temp_remoteView, temp_appWidgetManager);
    		widgetUIHandler.setWidgetLeftThisWeek(leftThisWeek);
    		widgetUIHandler.setWidgetFlexFunds(totalFlex);
    		widgetUIHandler.setWidgetDiningDollars(totalDining);
    		widgetUIHandler.setWidgetLastUpdated("Last Updated: "+currentDateTime);
			widgetUIHandler.updateWidget();
			
        	temp_remoteView = new RemoteViews(mContext.getPackageName(),R.layout.dollarschecker_appwidget_layout);
        	widgetUIHandler = new DollarsCheckerWidgetUIHandler(mContext, temp_remoteView, temp_appWidgetManager);
    		widgetUIHandler.setWidgetFlexFunds(totalFlex);
    		widgetUIHandler.setWidgetDiningDollars(totalDining);
    		widgetUIHandler.setWidgetLastUpdated(currentDateTime);
			widgetUIHandler.updateWidget();
			
        	temp_remoteView = new RemoteViews(mContext.getPackageName(),R.layout.mealsleftchecker_appwidget_layout);
        	widgetUIHandler = new MealsLeftCheckerWidgetUIHandler(mContext, temp_remoteView, temp_appWidgetManager);
    		widgetUIHandler.setWidgetLeftThisWeek(leftThisWeek);
    		widgetUIHandler.setWidgetLastUpdated(currentDateTime);
			widgetUIHandler.updateWidget();


    }
    protected void onPostExecuteInWidget(String result, WidgetUIHandler widgetUIHandler){
        MealStats mealStats = new MealStats(result);
        mealStats.parseHtml();
        String totalFlex =  "$" +String.valueOf(mealStats.getTotalFlex());
        String totalDining = "$"+String.valueOf(mealStats.getTotalDining());

        widgetUIHandler.setWidgetFlexFunds(totalFlex);
        widgetUIHandler.setWidgetDiningDollars(totalDining);
        
        String leftThisWeek=null;
        if(!mealStats.mealData.isEmpty()){
            for(NameValuePair pair : mealStats.mealData){
                if(pair.getName().contains("left for week")){
                    leftThisWeek = pair.getValue();
                }
            }

        }
        if(leftThisWeek!=null){
        	if(leftThisWeek.length() != 0 || !leftThisWeek.isEmpty()){
                widgetUIHandler.setWidgetLeftThisWeek(leftThisWeek);

        	}else{
                widgetUIHandler.setWidgetLeftThisWeek("-");

        	}
        }else{
            widgetUIHandler.setWidgetLeftThisWeek("-");

        }

        long unixTime = System.currentTimeMillis() / 1000L;
        String currentDateTime = Helper.getTime(unixTime);

        if(widgetType=="dollars" ||widgetType=="meals"){
            widgetUIHandler.setWidgetLastUpdated(currentDateTime);
        }else{
            widgetUIHandler.setWidgetLastUpdated("Last Updated: "+currentDateTime);
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("lastUpdated", unixTime);
        editor.putString("flexFunds", totalFlex);
        editor.putString("diningDollars", totalDining);
        editor.putString("leftThisWeek", leftThisWeek);
        editor.commit();

        widgetUIHandler.updateWidget();
        
    }
}
