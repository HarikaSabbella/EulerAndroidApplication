package com.example.graphintegration;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity1 extends Activity {
	WebView myWebView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view);
		
		myWebView = (WebView) findViewById(R.id.webview);
		myWebView.getSettings().setBuiltInZoomControls(true);
	    myWebView.setWebViewClient(new MyWebViewClient());
	    
	    myWebView.loadUrl("https://sites.google.com/site/graphmergeapplication/");
	}
	
	public class MyWebViewClient extends WebViewClient {
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {  	
	    	MainActivity.URLButton1 = url;  
	    	WebViewActivity1.this.finish();
	        return true; 
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.web_view, menu);
		return true;
	}
}
