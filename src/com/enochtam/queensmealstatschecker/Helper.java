package com.enochtam.queensmealstatschecker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Helper {
    public static String dateFormatStr = "yyyy/MM/dd hh:mm a";
	public static String timeZone = "America/New_York";
	
	
	public static String getTime(long unixTime){
		Date date = new Date ();
        date.setTime((long)unixTime*1000);
        DateFormat dateFormat = new SimpleDateFormat(dateFormatStr);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
		return dateFormat.format(date);
	}
	
	public static boolean isOnline(Context ctx){
        ConnectivityManager cm =
                (ConnectivityManager) ctx.getSystemService(ctx.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
	}
	public static boolean checkUserAndPass(String username,String password){
		boolean error = false;
		if (username == null || username.isEmpty()) {
			error = true;
		}
		if (password == null || password.isEmpty()) {
			error = true;
		}
		return !error;
	}
}
