package com.enochtam.queensmealstatschecker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

public class MenuDisplayActivity extends ActionBarActivity {

	WebView webView;
	TextView textView;
    String link;
    String title;
	boolean clicked = false;
    
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_menu_display);
        
        Intent intent = getIntent();
        link = intent.getStringExtra("link");
        title = intent.getStringExtra("title");

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setInitialScale(120);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
            	clicked = true;
            	return false;
            }  
        });
        
    	webView.loadUrl(link);
    	
    	textView = (TextView) findViewById(R.id.titleText);
    	textView.setText(title);
        
    }
	

	public void onBackPressed(){
    	if (clicked){
    		webView.loadUrl(link);
    		clicked = false;
    	}else{
    		super.onBackPressed();
    	}
    	
	}
	
    public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
            case android.R.id.home:
            	super.onBackPressed();
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
}