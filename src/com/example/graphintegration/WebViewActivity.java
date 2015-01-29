package com.example.graphintegration;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends Activity {
	WebView myWebView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view);
		displayHelpDialog();
		myWebView = (WebView) findViewById(R.id.webview);
		myWebView.getSettings().setBuiltInZoomControls(true);
	    myWebView.setWebViewClient(new MyWebViewClient());
	    
	    myWebView.loadUrl("https://sites.google.com/site/graphmergeapplication/");
	}
	
	public class MyWebViewClient extends WebViewClient {
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {  	
	    	MainActivity.URLButton1 = url;  
	    	WebViewActivity.this.finish();
	        return true; 
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.web_view, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item )
	{
		displayHelpDialog();
		return false;
	}
	public void displayHelpDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(WebViewActivity.this, android.R.style.Theme_Holo_Light_DialogWhenLarge);
		builder.setTitle("Help");
		builder.setPositiveButton(R.string.ok, null);
		builder.setMessage("Click on a link to choose a taxonomy.  After clicking on the link, you will" +
				" be redirected to the home page and you can choose to visualize your chosen taxonomy there.");
		AlertDialog theAlertDialog = builder.create();
		theAlertDialog.show();
	}
}
