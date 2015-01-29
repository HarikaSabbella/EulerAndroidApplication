package com.example.graphintegration;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	WebView myWebView;
	Button chooseGraphButton1;
	Button showGraphsButton;
	TextView textView1;
	TextView urlButton1TextView;
	static String URLButton1 = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(null);
		setContentView(R.layout.activity_main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);	
		chooseGraphButton1 = (Button) findViewById(R.id.chooseGraphButton1);
		showGraphsButton = (Button) findViewById(R.id.showGraphsButton);
		textView1 = (TextView) findViewById(R.id.textView1);

		setUpButton1(chooseGraphButton1);
		drawGraphsLaunchActivty();
	}
	
	private void drawGraphsLaunchActivty()
	{
		showGraphsButton.setOnTouchListener(new OnTouchListener () {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				Intent newIntent = new Intent(MainActivity.this, ShowGraphsActivity.class);
				newIntent.putExtra("url1", URLButton1);
				newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(newIntent);
				return false;
			}		
		});		
	}
	
	private void setUpButton1(Button button) {
		button.setOnTouchListener(new OnTouchListener () {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				Intent newIntent = new Intent(MainActivity.this, WebViewActivity1.class);
				newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(newIntent);
				return true;
			}
		});		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
}
