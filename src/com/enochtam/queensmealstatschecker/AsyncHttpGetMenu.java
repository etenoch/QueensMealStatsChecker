package com.enochtam.queensmealstatschecker;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class AsyncHttpGetMenu  extends AsyncTask<String, String, String> {
	// constants for httprequests
    private final String homepageLink = "http://dining.queensu.ca/where-to-dine/todays-menus/";


    // context related variables
    private Context mContext;
	private View rootView;


    //http request variables
    private HttpClient client;
    private CookieStore cookieStore;
    private HttpContext httpContext;
    
    
    private ArrayList<MenuPage> mealPageDataArrayList = new ArrayList<MenuPage>();
    private ListView lv;

    public AsyncHttpGetMenu(Context context,View rootView) {
        this.mContext = context;
        this.rootView = rootView;
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
        	htmlResult = getLinks();
            Document doc = Jsoup.parse(htmlResult);
        	Elements links = doc.select(".post_content").select("a");
        	for (Element link : links) {
        		if(link.attr("href").contains(".htm")||link.attr("href").contains(".html")){
        			mealPageDataArrayList.add(new MenuPage(link.text(), link.attr("href"), getHtml(link.attr("href"))));
        		}
        		  
        	}
        	
        }catch (Exception e){
            return "unidentified";
        }
        
        return htmlResult;
    }

    protected String getLinks() throws IOException, Exception{
        HttpGet getlinks = new HttpGet(homepageLink);

        HttpResponse response = client.execute(getlinks, httpContext);

        if(response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK){
            String string = EntityUtils.toString(response.getEntity());
        	return string;
        }else{
        	throw new Exception();
        }
    }

    protected String getHtml(String link) throws IOException, Exception{
    	
        HttpGet gethtml = new HttpGet(link);
        HttpResponse response = client.execute(gethtml, httpContext);

        if(response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK){
            String string = EntityUtils.toString(response.getEntity());
        	return string;
        }else{
        	throw new Exception();
        }
    }
    
    @Override
    protected void onPostExecute(String result) {
    	lv = (ListView) rootView.findViewById(R.id.menuListView);
    	ArrayList<String> justStrings = new ArrayList<String>();
    	for (MenuPage m : mealPageDataArrayList){
    		justStrings.add(m.title);
    	}
    	ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext,android.R.layout.simple_list_item_1 ,justStrings);

    	lv.setAdapter(arrayAdapter);
    	
    	TextView loadingIndicator=  (TextView) rootView.findViewById(R.id.loadingIndicator);
    	loadingIndicator.setText("");
    	
    	lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
                
                //Toast.makeText(mContext, mealPageDataArrayList.get((int)id).link, Toast.LENGTH_SHORT).show();
            	
                Intent i = new Intent(mContext, MenuDisplayActivity.class);
                i.putExtra("title",mealPageDataArrayList.get((int)id).title);
                i.putExtra("link",mealPageDataArrayList.get((int)id).link);

                mContext.startActivity(i);
            }
        });
    	
    }
    


}
