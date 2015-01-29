package com.example.graphintegration;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity {

	
	Spinner modify_diagram_spinner;
	
	WebView myWebView;
	Button chooseGraphButton1;
	Button showGraphsButton;
	TextView textView1;
	TextView urlButton1TextView;
	static String URLButton1 = "";
	SharedPreferences articulationsPresent;
	String[] articulations = {"==", "><", "<", ">", "!"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(null);
		setContentView(R.layout.activity_main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);	
		articulationsPresent = getSharedPreferences("ArticulationsList", MODE_PRIVATE);
		SharedPreferences.Editor preferencesEditor = articulationsPresent.edit();
		preferencesEditor.clear();
		for(int h = 0; h<articulations.length; h++)
		{
			preferencesEditor.putString(articulations[h], articulations[h]);	
		}
		preferencesEditor.apply();

		chooseGraphButton1 = (Button) findViewById(R.id.chooseGraphButton1);
		showGraphsButton = (Button) findViewById(R.id.showGraphsButton);
		textView1 = (TextView) findViewById(R.id.textView1);

		articulationsPresent = getSharedPreferences("ArticulationsList", MODE_PRIVATE);

		setUpButton1(chooseGraphButton1);
		drawGraphsLaunchActivty();
	}

	private void drawGraphsLaunchActivty()
	{
		showGraphsButton.setOnClickListener(new OnClickListener () {
			@Override
			public void onClick(View v) {
				Intent newIntent = new Intent(MainActivity.this, ShowGraphsActivity.class);
				newIntent.putExtra("url1", URLButton1);
				newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(newIntent);		
			}		
		});		
	}

	private void setUpButton1(Button button) {
		button.setOnClickListener(new OnClickListener () {

			@Override
			public void onClick(View v) {
				Intent newIntent = new Intent(MainActivity.this, WebViewActivity.class);
				newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(newIntent);
			}
		});		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item )
	{
	    if (item.getItemId() == R.id.help)
	    {
	        helpButtonClicked();
	    }
	    else if (item.getItemId() == R.id.about)
	    {
	    	aboutButtonClicked();
	    }
	    return false;
	}

	private void aboutButtonClicked()
	{	
			AlertDialog.Builder builder = createBuilder();
			builder.setTitle("About");
			builder.setPositiveButton(R.string.ok, null);
			builder.setMessage("This tool allows you to delete and create relationships between nodes of taxonomies and allows you to visualize the different worlds that are possible based on your input!");
			AlertDialog theAlertDialog = builder.create();
			theAlertDialog.show();
		}

	private void helpButtonClicked()
	{
			AlertDialog.Builder builder1 = createBuilder();
			builder1.setTitle("Help");
			builder1.setPositiveButton(R.string.ok, null);
			builder1.setMessage("Choose a taxonomy first using the \"Choose Taxonomies\" button and use the \"Display Taxonomies\" button to view the choosen taxonomy!");
			AlertDialog theAlertDialog1 = builder1.create();
			theAlertDialog1.show();
	}
	private AlertDialog.Builder createBuilder()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Holo_Light_DialogWhenLarge);
		return builder;
	}
}
