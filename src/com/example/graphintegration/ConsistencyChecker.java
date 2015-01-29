package com.example.graphintegration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;

import com.mindfusion.diagramming.ArrowHeads;
import com.mindfusion.diagramming.Behavior;
import com.mindfusion.diagramming.Diagram;
import com.mindfusion.diagramming.DiagramAdapter;
import com.mindfusion.diagramming.DiagramLink;
import com.mindfusion.diagramming.DiagramNode;
import com.mindfusion.diagramming.DiagramView;
import com.mindfusion.diagramming.FitSize;
import com.mindfusion.diagramming.HandlesStyle;
import com.mindfusion.diagramming.LayeredLayout;
import com.mindfusion.diagramming.LayeredLayoutLinkType;
import com.mindfusion.diagramming.NodeEvent;
import com.mindfusion.diagramming.Pen;
import com.mindfusion.diagramming.Shape;
import com.mindfusion.diagramming.ShapeNode;
import com.mindfusion.diagramming.SolidBrush;
import com.mindfusion.diagramming.jlayout.Direction;
import com.mindfusion.diagramming.jlayout.Orientation;
import com.mindfusion.drawing.Color;

public class ConsistencyChecker extends Activity {
	File file = new File(Environment.getExternalStorageDirectory().getPath()+"/test1.txt");

	static final int REQUEST = 1;
	Button cleanTaxButton;
	Map <String, DiagramNode> nodesMapGraph = new HashMap<String,DiagramNode>();

	StringBuffer clickedNode = new StringBuffer();

	StringBuffer nodeList = new StringBuffer();
	StringBuffer edgeList = new StringBuffer();
	DiagramView touchPad1;
	Diagram diagram; 
	SolidBrush whiteBrush = new SolidBrush(Color.white);
	SolidBrush lightGrayBrush = new SolidBrush(Color.lightGray);

	SolidBrush redBrush = new SolidBrush(Color.red);
	SolidBrush greenBrush = new SolidBrush(Color.green);
	SolidBrush blueBrush = new SolidBrush(Color.blue);

	StringBuffer file3Text = new StringBuffer();
	
	Pen grayPen = new Pen(0.3f, Color.gray);
	String result; 
	LayeredLayout layout = new LayeredLayout();

	private void writeToFile(String data) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory().getPath()+"/test1.txt",true));
			out.write(data);
			out.close();
		} 
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	private void eraseFile() throws IOException {
		FileOutputStream writer = new FileOutputStream(Environment.getExternalStorageDirectory().getPath()+"/test1.txt");
		writer.write((new String()).getBytes());
		writer.close();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_possible_worlds);
		touchPad1 = (DiagramView) findViewById(R.id.touchpad1);
		
		cleanTaxButton = (Button) findViewById(R.id.button123);
		cleanTaxButton.setOnClickListener(cleanTaxButtonListener);
		touchPad1.setBehavior(Behavior.Pan);
		diagram = touchPad1.getDiagram();
		diagram.addDiagramListener(diagramListener);
		diagram.setBackBrush(lightGrayBrush);
		//RectF rect = new RectF(-50, -50, 1000, 1000);
		//diagram.setBounds(rect);

		Bundle extras = getIntent().getExtras();
		result= extras.getString("resultFromServer");
			showLattice();
	}
	
	public OnClickListener cleanTaxButtonListener = new OnClickListener()
	{
		@Override
		public void onClick(View v) {
			new Thread4().execute("http://dilbert.cs.ucdavis.edu/Euler/file3.txt");
		}	
	};
	
	public void showLattice()
	{
		new Thread3().execute("http://dilbert.cs.ucdavis.edu/Euler/displayLattice.php");
		new Thread2().execute("http://dilbert.cs.ucdavis.edu/Euler/file3_lat.dot");
	}

	private class Thread4 extends AsyncTask<String, String, String>
	{
		@Override
		protected String doInBackground(String... args) 
		{
			file3Text.setLength(0);
			BufferedReader in;
			try {
				in = new BufferedReader(new InputStreamReader(new URL(args[0]).openStream()));
				String str;
				while ((str = in.readLine()) != null) {
					System.out.println(str);
					file3Text.append(str+"\n");
				}
			} 
			catch (Exception e) {}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) 
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(ConsistencyChecker.this);
			builder.setTitle("CleanTax File");
			builder.setPositiveButton(R.string.ok, null);
			builder.setMessage(file3Text.toString());
			AlertDialog theAlertDialog = builder.create();
			theAlertDialog.show();
		}
	}
	
	private class Thread3 extends AsyncTask<String, String, String>
	{
		@Override
		protected String doInBackground(String... args) 
		{
			try{
			HttpClient httpclient = new DefaultHttpClient();
			httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

			HttpPost httppost = new HttpPost("http://dilbert.cs.ucdavis.edu/Euler/displayLattice.php");

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
	
	
	private class Thread2 extends AsyncTask<String, String, String>
	{
		
		@Override
		protected String doInBackground(String... args) 
		{
			System.out.println("gogog");
			try 
			{
				URL url = new URL(args[0]);
				URLConnection connection;
				connection = url.openConnection();
				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				int responseCode = httpConnection.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_OK) {
				
					BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
					String str;
					while ((str = in.readLine()) != null) {
						if(str.contains("shape"))
						{	
							StringTokenizer nodes = new StringTokenizer(str, "[");
							if(str.contains("color=\"#FF0000\""))
							{
								nodeList.append(nodes.nextElement().toString().replace("\"", "")+"RRed*");
							}
							else if(str.contains("color=\"#00FF00\""))
							{
								nodeList.append(nodes.nextElement().toString().replace("\"", "")+"GGreen*");
							}
						}
						else if (str.contains("->"))
						{
							StringTokenizer nodes = new StringTokenizer(str, "[");
							edgeList.append(nodes.nextElement().toString().replace("\"", "")+"*");
						}
					}
				}
			}
			catch (MalformedURLException e) {}
			catch (IOException e) {}
			return null;
		}

		@Override
		protected void onPostExecute(String result) 
		{
			System.out.println("XXX"+nodeList.toString());
			try {
				StringTokenizer nodes = new StringTokenizer(nodeList.toString(), "*");
				while(nodes.hasMoreElements())
				{
					String node = nodes.nextElement().toString().trim();
					ShapeNode diagramNode = diagram.getFactory().createShapeNode(0, 0, 8, 8, Shape.fromId("Square"));

					if(node.contains("RRed"))
					{
						diagramNode.setBrush(redBrush);
					}
					else if (node.contains("GGreen"))
					{
						diagramNode.setBrush(greenBrush);
					}
					diagramNode.setTextBrush(whiteBrush);
					diagramNode.setShadowBrush(lightGrayBrush);
					diagramNode.setHandlesStyle(HandlesStyle.HatchFrame);
					diagramNode.setText(node.replace(" RRed", "").replace(" GGreen", "").trim());
					diagramNode.resizeToFitText(FitSize.KeepRatio);
					nodesMapGraph.put(node.replace(" RRed", "").replace(" GGreen", "").trim(), diagramNode);
				}
				StringTokenizer nodes1 = new StringTokenizer(edgeList.toString(), "*");
				while(nodes1.hasMoreElements())
				{
					String node = nodes1.nextElement().toString().trim();
					StringTokenizer nodes2 = new StringTokenizer(node, "->");
					String from = nodes2.nextElement().toString().replace(" RRed", "").trim();
					String to = nodes2.nextElement().toString().replace(" GGreen", "").trim();
					//System.out.println("FROM"+from+"TO"+to+"\n");
					DiagramLink l = diagram.getFactory().createDiagramLink(nodesMapGraph.get(from), nodesMapGraph.get(to));
					l.setPen(grayPen);
					l.setShadowBrush(lightGrayBrush);
					l.setHeadShape(ArrowHeads.Arrow);
					l.setHeadPen(grayPen);
					l.setHeadShapeSize(2f);
				}
				arrangeDiagram();
			}

			catch (Exception e) {}
		}
	}

	public DiagramAdapter diagramListener = new DiagramAdapter()
	{
		@Override
		public void nodeClicked(NodeEvent arg0) 
		{
			try {
				eraseFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (arg0.getNode().getBrush().equals(redBrush) || 
					(((ShapeNode) arg0.getNode()).getText().toString().contains("AllOther")))
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(ConsistencyChecker.this);
				builder.setTitle("ERROR!");
				builder.setPositiveButton(R.string.ok, null);
				builder.setMessage("Please choose another option from the lattice!");
				AlertDialog theAlertDialog = builder.create();
				theAlertDialog.show();
			}
			else
			{
			arg0.getNode().setBrush(blueBrush);
			arg0.getNode().setSelected(true);
			clickedNode.setLength(0);
			clickedNode.append(((ShapeNode) arg0.getNode()).getText().toString());
			new Thread1().execute("http://dilbert.cs.ucdavis.edu/Euler/file3.txt");
		
			}
		}
	};
	private class Thread1 extends AsyncTask<String, String, String>
	{
		@Override
		protected String doInBackground(String... args) 
		{	
			try 
			{
				int counter = 1;
				String strLineNumbers[] = clickedNode.toString().split(",");
				URL url = new URL(args[0]);
				URLConnection connection;
				connection = url.openConnection();
				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				int responseCode = httpConnection.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_OK) {
					BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
					String str;
					while ((str = in.readLine()) != null && !str.contains("[")) {
						writeToFile(str+"\n");
					}
					while(str!=null)
					{
						System.out.println("HERE"+str);
						for(int x = 0; x<strLineNumbers.length; x++)
						{
							if(counter == Integer.parseInt(strLineNumbers[x].trim()))
							{	
								writeToFile(str+"\n");
							}
						}
						System.out.println("COUNTER="+counter);
						counter++;
						str = in.readLine();	
					}
				}
			}
			catch (Exception e) {}
			return result;
		}

		@Override
		protected void onPostExecute(String result) 
		{
			load2();
		}
	}

	public void load2() {
		new Thread(new Runnable() {
			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {}
				});                     
				try {
					load1();
				} 
				catch (Exception e) {}
			}
		}).start();       
	}

	public void load1() throws Exception
	{
		HttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost httppost = new HttpPost("http://dilbert.cs.ucdavis.edu/Euler/changeFile3.php");

		MultipartEntity mpEntity = new MultipartEntity();
		ContentBody cbFile = new FileBody(file);
		mpEntity.addPart("userfile", cbFile);

		httppost.setEntity(mpEntity);
		System.out.println("executing request " + httppost.getRequestLine());

		HttpResponse response = httpclient.execute(httppost);
		HttpEntity resEntity = response.getEntity();
		System.out.println("RESPONSE LINE"+response.getStatusLine());
		if (resEntity != null) {
			System.out.println("RESENTITY="+EntityUtils.toString(response.getEntity()));
			//resultFromServer.append(EntityUtils.toString(response.getEntity()));
		}
		if (resEntity != null) {
			resEntity.consumeContent();
		}
		System.out.println("GOT HERE");
		httpclient.getConnectionManager().shutdown();

		Intent newIntent = new Intent(ConsistencyChecker.this, ShowPossibleWorlds.class);
		startActivityForResult(newIntent, REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Check which request we're responding to
		if (requestCode == REQUEST) {
			if (resultCode == RESULT_OK) {
				ConsistencyChecker.this.finish();
			}
		}
	}
	
	private void arrangeDiagram()
	{
		layout.setNodeDistance(20f);
		layout.setDirection(Direction.Straight);
		layout.setOrientation(Orientation.Vertical);	
		layout.setLinkType(LayeredLayoutLinkType.Straight);
		layout.setLayerDistance(20f);
		layout.arrange(diagram);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_possible_worlds, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	
		if (item.getItemId() == R.id.consistency_button)
		{
			runEulerConsistencyButtonListener();
		}
		if (item.getItemId() == R.id.help_button)
		{
			helpButtonListener();
		}
		if (item.getItemId() == R.id.fix_consistency_button)
		{
			finishActivity();
		}

		return false;
	}

	public void finishActivity()
	{
		Intent returnIntent = new Intent();
		setResult(RESULT_OK, returnIntent);
		ConsistencyChecker.this.finish();
	}

	@Override
	public void onBackPressed() {
		finishActivity();
	}

	public void helpButtonListener()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(ConsistencyChecker.this);
		builder.setTitle("Help");
		builder.setPositiveButton(R.string.ok, null);
		builder.setMessage("Click the \"Consistency Check\" button to find out if your taxonomies are consistent. " +
				" In the case that they are not consistent, use the \"Fix Taxonomies\" button to go back to previous screen and " +
				"fix your taxonomies/articulations.");
		AlertDialog theAlertDialog = builder.create();
		theAlertDialog.show();
	}

	public void runEulerConsistencyButtonListener()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(ConsistencyChecker.this);
		builder.setTitle("Consistency Check");
		builder.setPositiveButton(R.string.ok, null);
		builder.setMessage(result);
		AlertDialog theAlertDialog = builder.create();
		theAlertDialog.show();
	}


}
