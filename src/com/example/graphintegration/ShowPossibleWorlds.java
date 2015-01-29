package com.example.graphintegration;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

public class ShowPossibleWorlds extends Activity {

	WebView myWebView2;
	WebView displayDiffWorldsWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_possible_worlds);



		myWebView2 = (WebView) findViewById(R.id.webview2);
		myWebView2.loadUrl("http://dilbert.cs.ucdavis.edu/Euler/file3_all.svg");  
		myWebView2.getSettings().setBuiltInZoomControls(true);

	}

	private class Thread3 extends AsyncTask<String, String, String>
	{
		@Override
		protected String doInBackground(String... args) 
		{
			try{
				HttpClient httpclient = new DefaultHttpClient();
				httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

				HttpPost httppost = new HttpPost("http://dilbert.cs.ucdavis.edu/Euler/movePossibleWorldsSVGFiles.php");

				HttpResponse response = httpclient.execute(httppost);
				HttpEntity resEntity = response.getEntity();
				System.out.println("RESPONSE LINE123"+response.getStatusLine());
				if (resEntity != null) {
					System.out.println("RESENTITY="+EntityUtils.toString(response.getEntity()));
					//resultFromServer.append(EntityUtils.toString(response.getEntity()));
				}
			}
			catch (Exception e) {}
			return null;
		}
	}

	@Override
	public void onBackPressed() {
		finishActivity();
	}
	public void finishActivity()
	{
		Intent returnIntent = new Intent();
		setResult(RESULT_OK, returnIntent);
		ShowPossibleWorlds.this.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_possible_worlds, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.help)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(ShowPossibleWorlds.this);
		

			builder.setTitle("Help");
			builder.setPositiveButton(R.string.ok, null);
			builder.setMessage("Click the \"See Individual Worlds\" button to individual images of possible worlds separately.");
			AlertDialog theAlertDialog = builder.create();
			theAlertDialog.show();
		}
		else if (item.getItemId() == R.id.individual_worlds)
		{
			showLinksForIndividualWorlds();
		}

		return false;
	}

	public void showLinksForIndividualWorlds()
	{
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View diffWorldsWebView = inflater.inflate(R.layout.show_links_different_worlds_webview, null);
		displayDiffWorldsWebView = (WebView) diffWorldsWebView.findViewById(R.id.list);
		new Thread3().execute("http://dilbert.cs.ucdavis.edu/Euler/movePossibleWorldsSVGFiles.php");
		myWebView2.loadUrl("http://dilbert.cs.ucdavis.edu/Euler/possibleWorldsSVGFiles");  
		myWebView2.getSettings().setBuiltInZoomControls(true);
	}
}
