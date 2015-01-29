package com.example.graphintegration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.mindfusion.diagramming.Align;
import com.mindfusion.diagramming.ArrowHeads;
import com.mindfusion.diagramming.Behavior;
import com.mindfusion.diagramming.Diagram;
import com.mindfusion.diagramming.DiagramAdapter;
import com.mindfusion.diagramming.DiagramItem;
import com.mindfusion.diagramming.DiagramLink;
import com.mindfusion.diagramming.DiagramLinkList;
import com.mindfusion.diagramming.DiagramNode;
import com.mindfusion.diagramming.DiagramView;
import com.mindfusion.diagramming.FitSize;
import com.mindfusion.diagramming.Granularity;
import com.mindfusion.diagramming.HandlesStyle;
import com.mindfusion.diagramming.LayeredLayout;
import com.mindfusion.diagramming.LayeredLayoutLinkType;
import com.mindfusion.diagramming.LinkEvent;
import com.mindfusion.diagramming.LinkLabel;
import com.mindfusion.diagramming.LinkShape;
import com.mindfusion.diagramming.NodeEvent;
import com.mindfusion.diagramming.Pen;
import com.mindfusion.diagramming.QuickRouter;
import com.mindfusion.diagramming.Shape;
import com.mindfusion.diagramming.ShapeNode;
import com.mindfusion.diagramming.SolidBrush;
import com.mindfusion.diagramming.jlayout.Direction;
import com.mindfusion.diagramming.jlayout.Orientation;
import com.mindfusion.drawing.Color;
import com.mindfusion.drawing.Font;

public class ShowGraphsActivity extends Activity {
	File file = new File(Environment.getExternalStorageDirectory().getPath()+"/test.txt");
	/*contains each group and the isa edges*/
	Map <String, Map<String, StringBuffer>> groups = new HashMap<String, Map<String, StringBuffer>>();
	/*number of nodes per group*/
	Map <String, Integer> groupNodecount = new HashMap<String, Integer>();
	Map <String, DiagramNode> nodesMapGraph = new HashMap<String,DiagramNode>();
	Map <String, Integer> colorIndexForTaxonomy = new HashMap<String, Integer>();
	Map <String, Integer> shapeIndexForTaxonomy = new HashMap<String, Integer>();
	/*odd/even number assigned to group*/
	Map <String, Integer> groupNames = new HashMap<String, Integer>();

	Map <String, StringBuffer> groupIsaEdges = new HashMap<String, StringBuffer>();

	String[] groupNamesOrder = new String[100];
	SharedPreferences articulationsPresent;

	int groupNamesOrderCount = 0;
	int groupCount = 0;

	Pen redPen = new Pen(0.3f, Color.red);
	Pen grayPen = new Pen(0.6f, Color.gray);
	Pen blackPen = new Pen(0.3f, Color.black);
	Pen lightGrayPen = new Pen(0.3f, Color.LIGHT_GRAY);
	Pen darkGrayPen = new Pen(0.3f, Color.DARK_GRAY);
	Pen thickBluePen = new Pen(0.6f, Color.blue);	
	Pen thinBluePen = new Pen(0.3f, Color.blue);

	Pen bluePen = new Pen(0.3f, Color.blue);

	Pen thickRedPen = new Pen(1.5f, Color.red);
	Pen thickGrayPen = new Pen(2.1f, Color.gray);
	Pen thickBlackPen = new Pen(1.5f, Color.black);

	Pen thickBluePen1 = new Pen(2.1f, Color.blue);
	Pen thickBluePen2 = new Pen(1.5f, Color.blue);

	Pen thickLightGrayPen = new Pen(1.5f, Color.LIGHT_GRAY);

	SolidBrush blackBrush = new SolidBrush(Color.black);
	SolidBrush redBrush = new SolidBrush(Color.red);

	SolidBrush grayBrush = new SolidBrush(Color.gray);
	SolidBrush lightGrayBrush = new SolidBrush(Color.LIGHT_GRAY);
	SolidBrush darkGrayBrush = new SolidBrush(Color.DARK_GRAY);
	SolidBrush whiteBrush = new SolidBrush(Color.white);
	SolidBrush blueBrush = new SolidBrush(Color.blue);
	SolidBrush greenBrush = new SolidBrush(Color.green);


	String[] articulations;
	String[] shapes = {"Heptagon", "Ellipse", "Rectangle", "Pentagon", "ConeUp", "FramedRectangle"};
	SolidBrush[] brushColors = {greenBrush, darkGrayBrush};
	ListView listView;
	ArrayAdapter<String> articulationsAdapter;
	Button addEdgeButton;
	Button checkConsistencyButton;
	Button displayDiffWorldsButton;
	Button modifyAvailArticulationsButton;
	Button highlightNodeEdgesButton;
	Button nodeCountButton;
	Button routeEdgesButton;
	Button addNewEdgeButton;


	DiagramView touchPad;
	Diagram diagram; 
	Context context;

	AlertDialog modifyArticulationAlertDialog;
	AlertDialog highlightAlertDialog;
	AlertDialog addNewEdgeAlertDialog;

	DiagramLink linkClicked; 
	AlertDialog modifyLinkArticulationAlertDialog;

	AlertDialog.Builder builder;
	Button doneButton;
	TableLayout articulationsScrollView;
	EditText articulationEditText;
	Button addArticulationButton;

	int counter = -1;


	TextView mCustomTitle;

	static final int REQUEST = 1;
	StringBuffer resultFromServer = new StringBuffer();
	String savedDiagram;

	Spinner toSpinner;
	Spinner fromSpinner;
	Spinner nodesSpinner;
	ArrayAdapter<String> nodesAdapter;
	ArrayAdapter<String> nodesAdapter1;

	LayeredLayout layout = new LayeredLayout();

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.show_graphs, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item )
	{
		if (item.getItemId() == R.id.newEdge)
		{
			addNewEdgeButtonListener();
		}
		else if (item.getItemId() == R.id.modifyAvailableArticulations)
		{
			modifyArticulationsButtonListener();
		}
		else if (item.getItemId() == R.id.highlightEdges)
		{
			highlightEdgesButtonListener();
		}
		else if (item.getItemId() == R.id.diagramInfo)
		{
			nodeCountButtonListener();
		}
		else if (item.getItemId() == R.id.help)
		{
			helpButtonListener();
		}
		else if (item.getItemId() == R.id.runEuler)
		{
			try {
				drawWorldsButtonListener();
			} 
			catch (IOException e) 
			{}
		}

		return false;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_graphs);

		touchPad = (DiagramView) findViewById(R.id.touchpad);
		touchPad.setBehavior(Behavior.Pan);

		diagram = touchPad.getDiagram();
		diagram.setLinkHeadShapeSize(2);
		diagram.setBackBrush(whiteBrush);

		RectF rect = new RectF(-50, -50, 1000, 1000);
		diagram.setBounds(rect);

		nodesAdapter = new ArrayAdapter<String>(ShowGraphsActivity.this, android.R.layout.simple_spinner_item);
		nodesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		nodesAdapter.add(" ");
		nodesAdapter1 = new ArrayAdapter<String>(ShowGraphsActivity.this, android.R.layout.simple_spinner_item);
		nodesAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);	

		articulationsPresent = getSharedPreferences("ArticulationsList", MODE_PRIVATE);

		String[] articulations2 = articulationsPresent.getAll().keySet().toArray(new String[0]);
		String[] articulations1 = new String[articulations2.length];
		for(int g = 0; g<articulations2.length; g++)
		{
			articulations1[g]=articulationsPresent.getAll().get(articulations2[g]).toString();
		}
		for(String group : groupNames.keySet())
		{
			groupNames.put(group, null);
			groupCount = 0;
		}
		for(int f = 0; f<100; f++)
		{
			groupNamesOrder[f] = null;
			groupNamesOrderCount = 0;
		}
		ArrayList<String> lst = new ArrayList<String>();
		lst.addAll(Arrays.asList(articulations1));
		articulationsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, lst); 

		builder = createBuilder();
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View addNewEdgeAlert = inflater.inflate(R.layout.add_edge, null);
		listView = (ListView) addNewEdgeAlert.findViewById(R.id.list);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setAdapter(articulationsAdapter);


		addEdgeButton = (Button) addNewEdgeAlert.findViewById(R.id.addNewButton);
		fromSpinner = (Spinner) addNewEdgeAlert.findViewById(R.id.spinner1);
		toSpinner = (Spinner) addNewEdgeAlert.findViewById(R.id.spinner2);
		fromSpinner.setAdapter(nodesAdapter);
		toSpinner.setAdapter(nodesAdapter);
		setUpSpinners();

		addEdgesSelectedAfterAddEdgeButtonClicked();
		builder.setView(addNewEdgeAlert);
		addNewEdgeAlertDialog = builder.create();

		addNewEdgeAlertDialog.setTitle("Draw Edge");

		procedureForSelectionEvents();

		String url1 = getIntent().getExtras().getString("url1");
		context = this.getApplicationContext();
		new Thread2().execute(url1);

	}
	private AlertDialog.Builder createBuilder()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(ShowGraphsActivity.this, android.R.style.Theme_Holo_Light_DialogWhenLarge);
		return builder;
	}

	public void drawWorldsButtonListener() throws IOException
	{
		eraseFile();
		/*convertDiagramToCleanTax() generates hashMap which contains isa edges and a stringbuffer which contains all the other articulations*/
		String articulationList = convertDiagramToCleanTax();
		for(String key : groupIsaEdges.keySet())
		{
			if(!key.contains("none"))
			{
				writeToFile(groupIsaEdges.get(key).toString());
			}
		}
		writeToFile("\narticulation tw1 tw1");
		load2();
		writeToFile("\n"+articulationList);
	}
	public String convertDiagramToCleanTax()
	{

		for(DiagramLink link : diagram.getLinks())
		{
			link.setTag(null);
		}
		touchPad.setDiagram(diagram);

		for(DiagramNode node : diagram.getNodes())
		{
			System.out.print(((ShapeNode) node).getText().toString()+"nodes\n");
			for(DiagramLink link : node.getOutgoingLinks())
			{	
				if(link.getVisible())
					System.out.print(((ShapeNode) link.getDestination()).getText().toString()+"links\n");
			}
		}

		StringBuffer articulationGeneration = new StringBuffer();

		for(int p = groupNamesOrderCount-1; p>=0; p--)
		{	
			String group = groupNamesOrder[p];
			StringBuffer taxonomy = new StringBuffer();
			if(!group.contains("none"))
				taxonomy.append("taxonomy "+group+" Taxonomy"+group+"\n");
			for(String node: groups.get(group).keySet())
			{
				if(!group.contains("none"))
				{
					DiagramNode dNode = nodesMapGraph.get(group+"."+node);
					StringBuffer childrenOfdNode = new StringBuffer();
					int flowIndicator = 0;
					for(DiagramLink link: dNode.getAllOutgoingLinks())
					{
						if(link.getPen().equals(thickBluePen) || link.getPen().equals(thickBluePen1) || link.getPen().equals(thickBluePen2))
						{
							flowIndicator=1;
							//break;
						}
						else if (link.getPen().equals(grayPen) || link.getPen().equals(thickGrayPen))
						{
							flowIndicator = 0;
							//break;
						}					
					}
					if(flowIndicator == 1)
					{
						childrenOfdNode.append("("+node);
						for(DiagramLink link: dNode.getAllOutgoingLinks())
						{
							if(link.getPen().equals(thickBluePen) || link.getPen().equals(thickBluePen1) || link.getPen().equals(thickBluePen2))
							{
								ShapeNode destination = ((ShapeNode) link.getDestination());
								if(destination!=null)
								{
									childrenOfdNode.append(" "+destination.getText().toString().replace(group+".", ""));
									//dNode.getOutgoingLinks().remove(link);
									DiagramItem item = link;
									item.setTag("gone");
								}
							}
						}
						childrenOfdNode.append(")\n");
						taxonomy.append(childrenOfdNode.toString());
					}
					else if (flowIndicator == 0)
					{
						childrenOfdNode.append("("+node);
						for(DiagramLink link: dNode.getAllIncomingLinks())
						{
							if(link.getPen().equals(grayPen) || link.getPen().equals(thickGrayPen))
							{
								ShapeNode origin = ((ShapeNode) link.getOrigin());
								if(origin != null)
								{
									childrenOfdNode.append(" "+origin.getText().toString().replace(group+".", ""));
									//dNode.getOutgoingLinks().remove(link);
									DiagramItem item = link;
									item.setTag("gone");
								}	
							}
						}
						childrenOfdNode.append(")\n");
						taxonomy.append(childrenOfdNode.toString());
					}		
				} 
				else if (group.contains("none"))
				{
					DiagramNode dNode = nodesMapGraph.get(node);
					boolean indicator1 = true;
					StringBuffer part1 = new StringBuffer();
					StringBuffer part2 = new StringBuffer();
					for(DiagramLink link: dNode.getAllIncomingLinks())
					{
						if(link.getTag()==null && link.getVisible())
						{
							ShapeNode origin = ((ShapeNode) link.getOrigin());
							part1.append(origin.getText().toString()+" ");
							//dNode.getOutgoingLinks().remove(link);
							DiagramItem item = link;
							item.setTag("gone");
						}
					}
					for(DiagramLink link: dNode.getAllOutgoingLinks())
					{
						if(link.getTag()==null && link.getVisible())
						{
							ShapeNode destination = ((ShapeNode) link.getDestination());
							part2.append(destination.getText().toString()+" ");
							//dNode.getOutgoingLinks().remove(link);
							DiagramItem item = link;
							item.setTag("gone");
						}
					}
					if(part2.toString().equals("") || part2.toString().equals(null) || part1.toString().equals("") || part1.toString().equals(null) )
						indicator1 = false;

					if(indicator1)
					{
						String[] part12 = part1.toString().split(" ");
						String[] part22 = part2.toString().split(" ");
						/*for(int g = 0; g<part12.length; g++)
					{
						System.out.println("left side"+part12[g]);
					}
					for(int g = 0; g<part22.length; g++)
					{
						System.out.println("right side"+part22[g]);
					}
						 */
						StringTokenizer groupNode12 = new StringTokenizer(part12[0], ".");
						StringTokenizer groupNode22 = new StringTokenizer(part22[0], ".");

						//System.out.println(part12.length+"ADASDA"+part22.length);
						//System.out.println(part12[0].toString()+"ghjg\n");
						//System.out.println(groupNames.get(groupNode1.nextElement())+"tht\n");
						//System.out.println(groupNames.get(groupNode2.nextElement())+"tht\n");
						int group1 = groupNames.get(groupNode12.nextElement());
						int group2 = groupNames.get(groupNode22.nextElement());

						if(group1 < group2)
						{
							if(part12.length > 2)
							{
								articulationGeneration.append("["+part1.toString().trim()+" l"+part12.length+"sum"+" "+part2.toString().trim()+"]\n");
							}
							else
							{
								articulationGeneration.append("["+part1.toString().trim()+" lsum"+" "+part2.toString().trim()+"]\n");
							}
						}
						else if(group1 > group2)
						{
							if(part12.length > 2)
							{
								articulationGeneration.append("["+part2.toString().trim()+" r"+part12.length+"sum"+" "+part1.toString().trim()+"]\n");
							}
							else
							{
								articulationGeneration.append("["+part2.toString().trim()+" rsum"+" "+part1.toString().trim()+"]\n");
							}
						}
					}
				}				
			}
			groupIsaEdges.put(group, taxonomy);
		}	



		for(DiagramLink link : diagram.getLinks())
		{
			if(link.getLabels()!=null)
			{
				if(link.getLabels().get(0).getText().equals("") && link.getTag() == null && link.getVisible())
				{
					ShapeNode origin = (ShapeNode) link.getOrigin();
					ShapeNode destination = (ShapeNode) link.getDestination();
					StringTokenizer groupNode1 = new StringTokenizer(origin.getText(), ".");
					StringTokenizer groupNode2 = new StringTokenizer(destination.getText(), ".");
					String groupOfOriginNode = groupNode1.nextToken();
					/*to get just node part of destination node*/ 
					groupNode2.nextElement();
					StringBuffer presentArticulations = new StringBuffer();
					presentArticulations.append(groupIsaEdges.get(groupOfOriginNode));
					presentArticulations.append("("+groupNode1.nextElement()+" "+groupNode2.nextElement()+")\n");
					groupIsaEdges.remove(groupOfOriginNode);
					groupIsaEdges.put(groupOfOriginNode, presentArticulations);
					//origin.getOutgoingLinks().remove(link);
					DiagramItem item = link;
					item.setTag("gone");	
				}
				else if(link.getTag() == null && link.getVisible())
				{
					if(link.getPen().equals(bluePen))
					{
						articulationGeneration.append("["+((ShapeNode) (link.getDestination())).getText().toString()+
								parseLabel(link.getLabels().get(0).getText().toString()) + 
								((ShapeNode) (link.getOrigin())).getText().toString()+"]\n");	
					}
					else
					{	
						articulationGeneration.append("["+((ShapeNode) (link.getOrigin())).getText().toString()+
								parseLabel(link.getLabels().get(0).getText().toString()) + 
								((ShapeNode) (link.getDestination())).getText().toString()+"]\n");
					}
					//link.getOrigin().getOutgoingLinks().remove(link);
					DiagramItem item = link;
					item.setTag("gone");
				}			
			}
		}
		//System.out.println(groupIsaEdges.entrySet());
		return(articulationGeneration.toString().trim());
	}

	private String parseLabel(String label)
	{
		StringBuffer formatedLabel =new StringBuffer();
		StringBuffer formatedLabel1 =new StringBuffer();
		StringTokenizer articulationsOnLabel = new StringTokenizer(label, ",");
		if(articulationsOnLabel.countTokens()==1)
		{
			formatedLabel.append(" "+convertDisplayArticulationToInternalArticulation(articulationsOnLabel.nextToken())+" ");
			return formatedLabel.toString();
		}
		else
		{
			while(articulationsOnLabel.hasMoreTokens())
			{
				formatedLabel.append(convertDisplayArticulationToInternalArticulation(articulationsOnLabel.nextToken())+" ");
			}
			return formatedLabel1.append(" {"+formatedLabel.toString().trim()+"} ").toString();
		}

	}

	private String convertDisplayArticulationToInternalArticulation(String articulation)
	{
		String[] articulationList = articulationsPresent.getAll().keySet().toArray(new String[0]);
		for(int i = 0; i < articulationList.length; ++i){	
			if(articulation.contains(articulationList[i]))
			{
				return(convertArticulationToEnglish(articulationsPresent.getAll().get(articulationList[i]).toString()));
			}
		}
		return null;
	}

	private String convertArticulationToEnglish(String articulation)
	{
		if(articulation.contains("><"))
			return ("overlaps");
		else if(articulation.contains("<"))
			return ("is_included_in");	
		else if(articulation.contains(">"))
			return ("includes");
		else if(articulation.contains("=="))
			return ("equals");
		else if(articulation.contains("!"))
			return ("disjoint");
		return null;
	}

	public void load2() {
		new Thread(new Runnable() {
			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {}
				});                     
				try {
					load1();
					if(resultFromServer.toString().contains("inconsistent"))
					{
					Intent newIntent = new Intent(ShowGraphsActivity.this, ConsistencyChecker.class);
					newIntent.putExtra("resultFromServer", resultFromServer.toString());
					newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivityForResult(newIntent, REQUEST);
					}
					else
					{
						Intent newIntent = new Intent(ShowGraphsActivity.this, ShowPossibleWorlds.class);
						startActivity(newIntent);
					}
				} 
				catch (Exception e) {}
			}

		}).start();       
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Check which request we're responding to
		if (requestCode == REQUEST) {
			if (resultCode == RESULT_OK) {
				//diagram.loadFromString(savedDiagram);
				for(DiagramLink link : diagram.getLinks())
				{
					link.setTag(null);
				}
			}
		}
	}

	private void eraseFile() throws IOException {
		FileOutputStream writer = new FileOutputStream(Environment.getExternalStorageDirectory().getPath()+"/test.txt");
		writer.write((new String()).getBytes());
		writer.close();
	}

	private void writeToFile(String data) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory().getPath()+"/test.txt",true));
			out.write(data);
			out.close();
		} 
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void load1() throws Exception
	{
		
		resultFromServer.setLength(0);
		//eraseFile();
		HttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost httppost = new HttpPost("http://dilbert.cs.ucdavis.edu/Euler/generateSVG.php");

		MultipartEntity mpEntity = new MultipartEntity();
		ContentBody cbFile = new FileBody(file);
		mpEntity.addPart("userfile", cbFile);

		httppost.setEntity(mpEntity);
		System.out.println("executing request " + httppost.getRequestLine());

		HttpResponse response = httpclient.execute(httppost);

		HttpEntity resEntity = response.getEntity();
		System.out.println("RESPONSE LINE"+response.getStatusLine());
		if (resEntity != null) {
			//System.out.println("RESENTITY="+EntityUtils.toString(response.getEntity()));
			resultFromServer.append(EntityUtils.toString(response.getEntity()));
		}
		if (resEntity != null) {
			resEntity.consumeContent();
		}
		System.out.println("GOT HERE");
		httpclient.getConnectionManager().shutdown();

	}

	public void helpButtonListener()
	{
		AlertDialog.Builder builder = createBuilder();
		builder.setTitle("Help");
		builder.setPositiveButton(R.string.ok, null);
		builder.setMessage("Modify the taxonomies displayed by deleting edges, adding new edges, changing the display of the articulations, " +
				"defining your own articulations, and removing, adding, or deleting articulations on currently present edges\n\n" +
				"consistencies of the taxonomies given your changes or you can choose to visualize the possible worlds.\n " +
				"\n\nHelpful Information:\n" +
				"1) Double tap on a node to see all the incoming and outgoing edges for that node highlighted--double " +
				"tapping the node again causes lowlighting of the edges\n\n" +
				"2) Tap on an edge to modify the articulations present on the edge or to delete the edge\n\n" +
				"3) Use the \'Add New Edges\' menu bar selection under \'Modify Diagram\' to put in a new edge between the two nodes\n\n" +
				"4) Use the \'Modify Available Articulations\' menu bar selection under \'Modify Diagram\' to change the display of articulations\n\n" +
				"5) Use the \'Diagram Information\' menu bar selection under \'Modify Diagram\' to view the number of nodes each taxonomy contains\n\n" +
				"6) Use the \'Run Euler\' menu bar selection to see if possible worlds can be generated given" +
				"your modifications to the taxonomies");
		AlertDialog theAlertDialog = builder.create();
		theAlertDialog.show();
	}

	public void addNewEdgeButtonListener(){
		addNewEdgeAlertDialog.show();
	}

	public void highlightEdgesButtonListener()
	{
		builder = createBuilder();
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View highlightingAlert = inflater.inflate(R.layout.highlight_edges, null);

		nodesSpinner = (Spinner) highlightingAlert.findViewById(R.id.nodeSpinner);
		nodesSpinner.setAdapter(nodesAdapter1);

		Button highlightButton = (Button) highlightingAlert.findViewById(R.id.highlightButton);
		Button dehighlightButton = (Button) highlightingAlert.findViewById(R.id.dehighlightButton);

		highlightButton.setOnClickListener(highlightButtonListener);
		dehighlightButton.setOnClickListener(dehighlightButtonListener);

		builder.setView(highlightingAlert);
		highlightAlertDialog = builder.create();
		highlightAlertDialog.show();

	}

	public void nodeCountButtonListener(){

		StringBuffer taxonomyCount = new StringBuffer();
		for(String x: groupNodecount.keySet())
		{
			if(!x.contains("none"))
				taxonomyCount.append("Taxonomy "+x+" has "+ groupNodecount.get(x) + " nodes.\n");
		}
		newAlertDialog("Node Count For Each Taxonomy", taxonomyCount.toString());
	}

	public OnClickListener highlightButtonListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			DiagramNode node = nodesMapGraph.get(nodesSpinner.getSelectedItem().toString());
			node.setTag("true");
			highlightEdges(node);
			highlightAlertDialog.dismiss();
		}
	};

	public OnClickListener dehighlightButtonListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			DiagramNode node = nodesMapGraph.get(nodesSpinner.getSelectedItem().toString());
			node.setTag("false");
			dehighlightEdges(node);
			highlightAlertDialog.dismiss();
		}
	};

	private void setLinkLabelHighlight(DiagramLink link, String label, float position)
	{
		LinkLabel linkLabel = new LinkLabel(link, label);
		linkLabel.setVerticalAlign(Align.Center);
		linkLabel.setHorizontalAlign(Align.Center);
		linkLabel.setLinkLengthPosition(position);
		Font font = new Font("Times New Roman", 3, 5f); 
		linkLabel.setFont(font);
		linkLabel.setTextColor(Color.red);
		link.addLabel(linkLabel);
	}

	private void setLinkLabelRegular(DiagramLink link, String label, float position)
	{
		LinkLabel linkLabel = new LinkLabel(link, label);
		linkLabel.setVerticalAlign(Align.Center);
		linkLabel.setHorizontalAlign(Align.Center);
		linkLabel.setLinkLengthPosition(position);
		Font font = new Font("Times New Roman", 3, 2.3f); 
		linkLabel.setFont(font);
		if(link.getPen().equals(lightGrayPen))
			linkLabel.setTextColor(Color.lightGray);
		if(link.getPen().equals(grayPen))
			linkLabel.setTextColor(Color.gray);
		if(link.getPen().equals(redPen))
			linkLabel.setTextColor(Color.black);
		if(link.getPen().equals(thinBluePen))
			linkLabel.setTextColor(Color.black);
		if(link.getPen().equals(thickBluePen))
			linkLabel.setTextColor(Color.black);
		if(link.getPen().equals(blackPen))
			linkLabel.setTextColor(Color.black);
		link.addLabel(linkLabel);
	}

	public void highlightEdges(DiagramNode node)
	{
		for(DiagramLink link : node.getAllLinks())
		{
			link.setHeadShapeSize(4f);
			link.setBaseShapeSize(4f);
			if(link.getLabels() != null)
			{
				String s = link.getLabels().get(0).getText().toString();
				link.removeLabel(link.getLabels().get(0));
				setLinkLabelHighlight(link, s, 0.08f);
			}
			if(link.getPen().equals(lightGrayPen))
				link.setPen(thickLightGrayPen);
			if(link.getPen().equals(grayPen))
				link.setPen(thickGrayPen);
			if(link.getPen().equals(redPen))
				link.setPen(thickRedPen);
			if(link.getPen().equals(thinBluePen))
				link.setPen(thickBluePen2);
			if(link.getPen().equals(thickBluePen))
				link.setPen(thickBluePen1);
			if(link.getPen().equals(blackPen))
				link.setPen(thickBlackPen);
			changeLabelPositions(link, 1);
		}

	}
	public void dehighlightEdges(DiagramNode node)
	{
		for(DiagramLink link : node.getAllLinks())
		{
			link.setHeadShapeSize(2f);
			link.setBaseShapeSize(2f);
			if(link.getLabels() != null)
			{
				String s = link.getLabels().get(0).getText().toString();
				link.removeLabel(link.getLabels().get(0));
				setLinkLabelRegular(link, s, 0.08f);
			}
			if(link.getPen().equals(thickLightGrayPen))
				setPenForLink(link,lightGrayPen, lightGrayBrush);
			if(link.getPen().equals(thickGrayPen))
				setPenForLink(link,grayPen, grayBrush);
			if(link.getPen().equals(thickRedPen))
				setPenForLink(link,redPen, redBrush);
			if(link.getPen().equals(thickBluePen1))
				setPenForLink(link,thickBluePen, blueBrush);
			if(link.getPen().equals(thickBluePen2))
				setPenForLink(link,thinBluePen, blueBrush);
			if(link.getPen().equals(thickBlackPen))
				setPenForLink(link,blackPen, blackBrush);
			changeLabelPositions(link, 0);
		}
	}

	public void modifyArticulationsButtonListener ()
	{
		builder = createBuilder();
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View changeArticulations = inflater.inflate(R.layout.articulation_settings, null);

		doneButton = (Button) changeArticulations.findViewById(R.id.doneButton);
		articulationsScrollView = (TableLayout) changeArticulations.findViewById(R.id.articulationsTableView);
		articulationEditText = (EditText) changeArticulations.findViewById(R.id.articulationEditText);
		addArticulationButton = (Button) changeArticulations.findViewById(R.id.addArticulationButton);

		addArticulationButton.setOnClickListener(addAriculationButtonListener);
		updateSavedArticulationsList();

		setUpDoneButton();
		builder.setView(changeArticulations);
		modifyArticulationAlertDialog = builder.create();
		modifyArticulationAlertDialog.show();
	}

	private void updateSavedArticulationsList(){
		articulationsScrollView.removeAllViews();
		String[] articulations = articulationsPresent.getAll().keySet().toArray(new String[0]);
		for(int i = 0; i < articulations.length; ++i){		
			insertNewArticulationView(articulations[i], i);
		}		
	}

	public int saveNewArticulation(String newArticulation, String displayArticulation){
		SharedPreferences.Editor preferencesEditor = articulationsPresent.edit();
		String[] articulations = articulationsPresent.getAll().keySet().toArray(new String[0]);
		int indicator = 0;
		for(int i = 0; i < articulations.length; ++i){
			if (articulationsPresent.getAll().get(articulations[i]).equals(newArticulation) || 
					articulationsPresent.getAll().get(articulations[i]).equals(displayArticulation))
				indicator = 100;
		}

		if(indicator==0 || newArticulation.equals(displayArticulation))
		{
			if(displayArticulation.equals(""))
			{
				preferencesEditor.putString(newArticulation, newArticulation);
				preferencesEditor.apply();
				insertNewArticulationView(newArticulation, 0);
			}
			else
			{
				preferencesEditor.putString(newArticulation, displayArticulation);
				preferencesEditor.apply();	
			}
		}
		else
		{
			String invalidArticulation = "Invalid Articulation";
			String alreadyPresent = "Entered articulation is already present as a display or internal articulation!";
			newAlertDialog(invalidArticulation, alreadyPresent);
			return 1;
		}
		return 0;
	}

	private void removeArticulation(String articulation) {
		SharedPreferences.Editor preferencesEditor = articulationsPresent.edit();
		preferencesEditor.remove(articulation);
		preferencesEditor.apply();
	}

	public OnClickListener addAriculationButtonListener = new OnClickListener(){

		@Override
		public void onClick(View theView) {
			String newArticulation = articulationEditText.getText().toString();
			if(!newArticulation.equals(""))
				saveNewArticulation(newArticulation, "");
			else
			{
				String invalidArticulation = "Invalid Articulation";
				String emptyArticulation = "Empty articulations are not allowed!";
				newAlertDialog(invalidArticulation, emptyArticulation);
			}
			articulationEditText.setText(""); 
			// Forcing keyboard to close
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(articulationEditText.getWindowToken(), 0);
		}
	};

	private void insertNewArticulationView(String articulation, int arrayIndex){
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View newArticulationRow = inflater.inflate(R.layout.articulation_row, null);

		TextView internalArticulationTextView= (TextView) newArticulationRow.findViewById(R.id.internalArticulationTextView);
		EditText displayArticulationEditText = (EditText) newArticulationRow.findViewById(R.id.displayArticulationEditText);
		Button updateButton = (Button) newArticulationRow.findViewById(R.id.updateButton);
		updateButton.setOnClickListener(updateButtonListener);

		internalArticulationTextView.setText(articulation);
		displayArticulationEditText.setText((CharSequence) articulationsPresent.getAll().get(articulation));
		articulationsScrollView.addView(newArticulationRow, arrayIndex);
	}

	public OnClickListener updateButtonListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			TableRow tableRow = (TableRow) v.getParent();
			TextView internalArticulationTextView = (TextView) tableRow.findViewById(R.id.internalArticulationTextView);
			EditText displayArticulationEditText = (EditText) tableRow.findViewById(R.id.displayArticulationEditText);

			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(displayArticulationEditText.getWindowToken(), 0);

			if(displayArticulationEditText.getText().toString().equals(""))
			{
				String invalidArticulation = "Invalid Articulation";
				String emptyArticulation = "Empty articulations are not allowed!";
				newAlertDialog(invalidArticulation, emptyArticulation);
				displayArticulationEditText.setText((CharSequence) articulationsPresent.getAll().get(internalArticulationTextView.getText()).toString());
			}
			else 
			{
				String internalArticulation = internalArticulationTextView.getText().toString();
				String displayArticulation1 = displayArticulationEditText.getText().toString();
				String displayArticulation = articulationsPresent.getAll().get(internalArticulation).toString();
				removeArticulation(internalArticulationTextView.getText().toString());
				int result= saveNewArticulation(internalArticulation, displayArticulation1);
				if(result == 1)
				{
					displayArticulationEditText.setText((CharSequence) displayArticulation);
					SharedPreferences.Editor preferencesEditor = articulationsPresent.edit();
					preferencesEditor.putString(internalArticulation, displayArticulation);
					preferencesEditor.apply();	
				}
				else
				{
					changeDiagramLabels(displayArticulation, articulationsPresent.getAll().get(internalArticulation).toString());
					internalArticulationTextView.setText(internalArticulation);
					displayArticulationEditText.setText((CharSequence) articulationsPresent.getAll().get(internalArticulation));

				}
			}
		}
	};

	public void changeDiagramLabels(String currentLabel, String newLabel) 
	{
		for(DiagramLink link:diagram.getLinks())
		{
			if(link.getLabels() != null)
			{
				if(link.getLabels().get(0).getText().contains(currentLabel))
				{
					String newLabel1 = link.getLabels().get(0).getText().replace(currentLabel, newLabel);
					link.removeLabel(link.getLabels().get(0));
					setLinkLabelRegular(link, newLabel1, 0.08f);	
				}
			}
			changeLabelPositions(link, 0);			
		}
	}
	public void checkLinkLabels()
	{
		for(DiagramLink link:diagram.getLinks())
		{
			if(link.getLabels() != null)
			{
				String[] articulations2 = articulationsPresent.getAll().keySet().toArray(new String[0]);
				for(int g = 0; g<articulations2.length; g++)
				{
					if(link.getLabels().get(0).getText().contains(articulations2[g]))
					{
						String newLabel1 = link.getLabels().get(0).getText().replace(articulations2[g], articulationsPresent.getAll().get(articulations2[g]).toString());
						link.removeLabel(link.getLabels().get(0));
						setLinkLabelRegular(link, newLabel1, 0.08f);	
					}
				}
			}
		}
	}

	public void setUpDoneButton()
	{
		doneButton.setOnClickListener(new OnClickListener () 
		{
			@Override
			public void onClick(View arg0) {
				articulationsAdapter.clear();
				String[] articulations2 = articulationsPresent.getAll().keySet().toArray(new String[0]);
				String[] articulations1 = new String[articulations2.length];
				for(int g = 0; g<articulations2.length; g++)
				{
					articulations1[g]=articulationsPresent.getAll().get(articulations2[g]).toString();
				}

				ArrayList<String> lst = new ArrayList<String>();
				lst.addAll(Arrays.asList(articulations1));
				articulationsAdapter.addAll(lst);
				modifyArticulationAlertDialog.dismiss();
			}
		});
	}

	private void resetNodeColors()
	{
		for(DiagramNode node: diagram.getNodes())
		{
			int indicator = 0;
			String nodeText = ((ShapeNode) node).getText().toString();
			char[] buffer = nodeText.toCharArray();
			StringBuffer group = new StringBuffer();
			for(int h = 0; h<buffer.length; h++)
			{
				if(buffer[h]=='.')
				{	
					indicator=100;
					break;
				}
				group.append(buffer[h]);
			}
			if(indicator==100)
			{
				nodesMapGraph.get(((ShapeNode) node).getText()).setBrush(brushColors[colorIndexForTaxonomy.get(group.toString())]);
				nodesMapGraph.get(((ShapeNode) node).getText()).setSelected(false);
			}
			else
			{
				nodesMapGraph.get(((ShapeNode) node).getText()).setBrush(brushColors[colorIndexForTaxonomy.get("none")]);
				nodesMapGraph.get(((ShapeNode) node).getText()).setSelected(false);
			}
		}
	}
	private void setPenForLink(DiagramLink link, Pen pen, SolidBrush brush)
	{
		link.setHeadBrush(brush);
		link.setPen(pen);
		link.setHeadPen(pen);
		link.setTextBrush(brush);
	}

	private void arrangeDiagram()
	{
		layout.setNodeDistance(20f);
		layout.setDirection(Direction.Straight);
		layout.setOrientation(Orientation.Horizontal);	
		layout.setLinkType(LayeredLayoutLinkType.Straight);
		layout.setLayerDistance(20f);

		layout.arrange(diagram);
	}

	private void setUpSpinners()
	{
		fromSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if(arg0.getSelectedItem()==" ") {}
				else 
				{
					counter = 0;
					resetNodeColors();
					setNodeColors();
				}	
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}	
		});

		toSpinner.setOnItemSelectedListener(new OnItemSelectedListener() 
		{
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if(arg0.getSelectedItem()==" ") {}
				else 
				{
					counter = 1;
					resetNodeColors();
					setNodeColors();
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}	
		});
	}

	private void procedureForSelectionEvents()
	{
		diagram.addDiagramListener(new DiagramAdapter () 
		{
			@Override
			public void nodeDoubleClicked(NodeEvent arg0)
			{
				if(arg0.getNode().getTag().equals("false"))
				{
					arg0.getNode().setTag("true");
					highlightEdges(arg0.getNode());
				}
				else if(arg0.getNode().getTag().equals("true"))
				{
					arg0.getNode().setTag("false");
					dehighlightEdges(arg0.getNode());
				}
			}

			@Override
			public void nodeClicked(NodeEvent arg0) 
			{
				if(counter == -1)
				{			
					arg0.getNode().setBrush(redBrush);
					arg0.getNode().setSelected(true);
					fromSpinner.setSelection(nodesAdapter.getPosition(((ShapeNode) arg0.getNode()).getText()));

					counter = 0;
				}
				else if(counter == 0)
				{	
					toSpinner.setSelection(nodesAdapter.getPosition(((ShapeNode) arg0.getNode()).getText()));
					resetNodeColors();
					setNodeColors();
					counter = 1;				
				}
				else if (counter == 1)
				{
					fromSpinner.setSelection(nodesAdapter.getPosition(((ShapeNode) arg0.getNode()).getText()));
					resetNodeColors();
					setNodeColors();
					counter = 0;
				}
			}


			@Override
			public void linkClicked(LinkEvent e)
			{
				linkClicked = e.getLink();
				setPenForLink(linkClicked, redPen, redBrush);
				AlertDialog.Builder builder = createBuilder();
				builder.setTitle("Change 'OR' Articulations");

				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View modifyLinkSelectedAlert = inflater.inflate(R.layout.link_selected, null);

				TextView fromTextView = (TextView) modifyLinkSelectedAlert.findViewById(R.id.spinner1);
				TextView toTextView = (TextView) modifyLinkSelectedAlert.findViewById(R.id.spinner2);
				ListView listViewForLinkSelected = (ListView) modifyLinkSelectedAlert.findViewById(R.id.list);
				listViewForLinkSelected.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
				listViewForLinkSelected.setAdapter(articulationsAdapter);
				Button doneButton = (Button) modifyLinkSelectedAlert.findViewById(R.id.doneButton);
				Button deleteEdgeButton = (Button) modifyLinkSelectedAlert.findViewById(R.id.button1);
				deleteEdgeButton.setOnClickListener(deleteEdgeButtonlistener);
				doneButton.setOnClickListener(doneButtonlistener);

				builder.setView(modifyLinkSelectedAlert);
				modifyLinkArticulationAlertDialog = builder.create();
				modifyLinkArticulationAlertDialog.show();

				ShapeNode source = (ShapeNode) e.getLink().getOrigin();
				ShapeNode destination = (ShapeNode) e.getLink().getDestination();
				fromTextView.setText(source.getText());
				toTextView.setText(destination.getText());	
				listViewForLinkSelected.clearChoices();
				if(e.getLink().getLabels() == null) {}
				else
				{
					String[] singleArticulation = e.getLink().getLabels().get(0).getText().split(",");
					for (int h=0; h<singleArticulation.length  && singleArticulation[h]!=null; h++)
					{
						listViewForLinkSelected.setItemChecked(articulationsAdapter.getPosition(singleArticulation[h].trim()), true);
					}
				}
			}

		});
	}
	public OnClickListener deleteEdgeButtonlistener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			DiagramNode node = linkClicked.getOrigin();
			node.getOutgoingLinks().remove(linkClicked);
			DiagramItem item = linkClicked;
			item.setVisible(false);
			modifyLinkArticulationAlertDialog.dismiss();
		}
	};

	public OnClickListener doneButtonlistener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			RelativeLayout linkSelectedAlert = (RelativeLayout) v.getParent();
			ListView listViewForLinkSelected = (ListView) linkSelectedAlert.findViewById(R.id.list);

			SparseBooleanArray sp= listViewForLinkSelected.getCheckedItemPositions();
			String selected = "";
			int selectedItemsCount = listViewForLinkSelected.getCount();
			for(int i = 0; i < selectedItemsCount; i++){
				if(sp.get(i)) {
					selected += listViewForLinkSelected.getItemAtPosition(i).toString() + ",";
				}
			}
			String articulation="";
			if(selected.endsWith(","))
				articulation += selected.substring(0,selected.length()-1);
			else
				articulation += selected;

			if(linkClicked.getLabels()==null)
			{
				setPenForLink(linkClicked, redPen, redBrush);
				linkClicked.setHeadBrush(redBrush);
				setLinkLabelRegular(linkClicked, articulation.toString(), 0.08f);
			}
			else
			{
				linkClicked.removeLabel(linkClicked.getLabels().get(0));
				setPenForLink(linkClicked, redPen, redBrush);
				linkClicked.setHeadBrush(redBrush);
				setLinkLabelRegular(linkClicked, articulation.toString(), 0.08f);
			}
			changeLabelPositions(linkClicked, 0);
			modifyLinkArticulationAlertDialog.dismiss();
		}
	};

	private void setNodeColors()
	{
		if(!fromSpinner.getSelectedItem().toString().equals(" ") && !toSpinner.getSelectedItem().toString().equals(" "))
		{
			String from = fromSpinner.getSelectedItem().toString();
			String to = toSpinner.getSelectedItem().toString();
			nodesMapGraph.get(from).setBrush(redBrush);
			nodesMapGraph.get(to).setBrush(redBrush);
			nodesMapGraph.get(from).setSelected(true);
			nodesMapGraph.get(to).setSelected(true);

			listView.clearChoices();


		}
		else if (fromSpinner.getSelectedItem().toString().equals(" ") && !toSpinner.getSelectedItem().toString().equals(" "))
		{
			nodesMapGraph.get(toSpinner.getSelectedItem().toString()).setBrush(redBrush);
			nodesMapGraph.get(toSpinner.getSelectedItem().toString()).setSelected(true);
		}
		else if (!fromSpinner.getSelectedItem().toString().equals(" ") && toSpinner.getSelectedItem().toString().equals(" "))
		{
			nodesMapGraph.get(fromSpinner.getSelectedItem().toString()).setBrush(redBrush);
			nodesMapGraph.get(fromSpinner.getSelectedItem().toString()).setSelected(true);
		}

	}


	private void addEdgesSelectedAfterAddEdgeButtonClicked() 
	{
		addEdgeButton.setOnClickListener(new OnClickListener () 
		{
			@Override
			public void onClick(View arg0) 
			{
				String from1 = null;
				String to1 = null;
				if(fromSpinner.getSelectedItem() != " " && toSpinner.getSelectedItem() != " ")
				{
					from1 = fromSpinner.getSelectedItem().toString();
					to1 = toSpinner.getSelectedItem().toString();
					changeNodeColorOfSelectedNodes(to1, from1);
					addNewEdgeAlertDialog.dismiss();

				}
				else if (toSpinner.getSelectedItem() == " " && fromSpinner.getSelectedItem() == " ")
				{
					newAlertDialog("ERROR", "Please choose a source node and a destination node for the edge you would like to add (fill in the to and from fields)!");
				}
				else if (toSpinner.getSelectedItem() == " ")
				{
					newAlertDialog("ERROR", "Please choose a node to draw the edge to (fill in the to field)!");
				}

				else if (fromSpinner.getSelectedItem() == " ")
				{
					newAlertDialog("ERROR", "Please choose a node to draw the edge from (fill in the from field)!");
				}
			}
		});
		diagram.arrangeLinkLabels();
	}
	public void newAlertDialog(String title, String message)
	{	
		AlertDialog.Builder builder = createBuilder();
		builder.setTitle(title);
		builder.setPositiveButton(R.string.ok, null);
		builder.setMessage(message);
		AlertDialog theAlertDialog = builder.create();
		theAlertDialog.show();
	}

	public void changeNodeColorOfSelectedNodes(String to, String from)
	{
		SparseBooleanArray sp= listView.getCheckedItemPositions();
		String selected = "";
		int selectedItemsCount = listView.getCount();
		for(int i = 0; i < selectedItemsCount; i++){
			if(sp.get(i)) {
				selected += listView.getItemAtPosition(i).toString() + ",";
			}
		}
		String articulation="";
		if(selected.endsWith(","))
			articulation += selected.substring(0,selected.length()-1);
		else
			articulation += selected;

		DiagramLink l = diagram.getFactory().createDiagramLink(nodesMapGraph.get(from), nodesMapGraph.get(to));		
		l.setShadowBrush(whiteBrush);
		setPenForLink(l, redPen, redBrush);
		l.setHeadBrush(redBrush);
		l.setHeadShape(ArrowHeads.BowArrow);
		listView.clearChoices();
		setLinkLabelRegular(l, articulation, 0.08f);
		changeLabelPositions(l, 0);
		routeLinks(l);
	}

	private void changeLabelPositions(DiagramLink l, int highlightIndicator)
	{
		DiagramLinkList linksRoutingNeeded = new DiagramLinkList();
		float labelPosition = 0.5f;
		for(DiagramLink link : l.getOrigin().getOutgoingLinks()) {
			if(link.getDestination().equals(l.getDestination()))
			{
				linksRoutingNeeded.add(link);
				if(link.getLabels() != null)
				{
					String label = link.getLabels().get(0).getText();
					link.removeLabel(link.getLabels().get(0));
					if(highlightIndicator == 0)
						setLinkLabelRegular(link,label, labelPosition+=0.10f);
					if(highlightIndicator == 1)
						setLinkLabelHighlight(link,label, labelPosition+=0.15f);
				}
			}	
		}
	}

	private void routeLinks(DiagramLink l)
	{
		DiagramLinkList linksRoutingNeeded = new DiagramLinkList();
		QuickRouter linkRouter = new QuickRouter(diagram);
		for(DiagramLink link : l.getOrigin().getOutgoingLinks()) {
			/*if the link given has self loops don't add it into the list to be rerouted*/
			/*if there are multiple edges between the same origin and destination of the given link*/
			if(link.getDestination().equals(l.getDestination()) && !(l.getOrigin().equals(l.getDestination())))
			{
				linksRoutingNeeded.add(link);
			}
		}
		linkRouter.setGranularity(Granularity.CoarseGrained);
		linkRouter.RouteLinks(linksRoutingNeeded);
		diagram.getRoutingOptions().setStartOrientation(Orientation.Horizontal);
		diagram.setLinkRouter(linkRouter);
	}

	private class Thread2 extends AsyncTask<String, String, String>
	{
		@Override
		protected String doInBackground(String... args) 
		{
			try 
			{
				URL url = new URL(args[0]);
				URLConnection connection;
				connection = url.openConnection();
				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				int responseCode = httpConnection.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_OK) {
					InputStream inputStream = httpConnection.getInputStream();
					FileOutputStream out = openFileOutput("Graph.txt", Context.MODE_PRIVATE);
					byte[] str = new byte[2048];
					int strLen = 0;
					while ((strLen = inputStream.read(str)) > 0) {
						out.write(str, 0, strLen);
					}
					out.close();
				} 
			}
			catch (MalformedURLException e) {}
			catch (IOException e) {}
			return null;
		}

		@Override
		protected void onPostExecute(String result) 
		{
			try
			{
				parseInputFile("Graph.txt", context);		
				System.out.println("HELLO"+groups.size()+" "+groups.entrySet()+"here"+groups.keySet()+"\n");
				drawNodes();
				drawEdgesWithOutArticulations();
				drawEdgesWithArticulations("Graph.txt", context);
				//System.out.println("HELLO11"+nodesMapGraph.size()+" "+nodesMapGraph.keySet()+"\n");
			}
			catch (Exception e) {}
		}
	}

	void drawNodes()
	{		
		int colorCount=0;
		int shapeCount=0;
		for(String group1 : groups.keySet())
		{
			groupNamesOrder[groupNamesOrderCount++] = group1;
		}

		for(int p = groupNamesOrderCount-1; p>=0; p--)
		{	
			String group = groupNamesOrder[p];
			if(groupNames.get(group) == null && !group.contains("none"))
			{
				groupNames.put(group, groupCount++);
			}
			else if(groupNames.get(group) == null && group.contains("none"))
			{
				groupCount++;
				groupNames.put(group, groupCount++);
			}

			int nodeCount = 0; 
			if(colorCount < brushColors.length)
				colorIndexForTaxonomy.put(group, colorCount++);
			else
			{
				colorCount = 0;
				colorIndexForTaxonomy.put(group, colorCount++);
			}
			if(shapeCount < shapes.length)
				shapeIndexForTaxonomy.put(group, shapeCount++);
			else
			{
				shapeCount = 0;
				shapeIndexForTaxonomy.put(group, shapeCount++);
			}

			String randomNodeShape = shapes[shapeIndexForTaxonomy.get(group)];
			SolidBrush randomBrush = brushColors[colorIndexForTaxonomy.get(group)];
			for(String node: groups.get(group).keySet())
			{
				ShapeNode diagramNode = diagram.getFactory().createShapeNode(0, 0, 8, 8, Shape.fromId(randomNodeShape));
				diagramNode.setTag("false");
				diagramNode.setBrush(randomBrush); 
				diagramNode.setTextBrush(whiteBrush);
				diagramNode.setHandlesStyle(HandlesStyle.Invisible);
				diagramNode.setShadowBrush(whiteBrush);
				if(group.contains("none") || group.contains("(+)"))
				{
					diagramNode.setText(node);
					nodesMapGraph.put(node, diagramNode);
				}
				else
				{
					diagramNode.setText(group+"."+node);
					nodesMapGraph.put(group+"."+node, diagramNode);				
				}
				diagramNode.resizeToFitText(FitSize.KeepRatio);
				nodesAdapter.add(diagramNode.getText());
				nodesAdapter1.add(diagramNode.getText());
				nodeCount++;
			}
			groupNodecount.put(group, nodeCount);
		}
	}

	void drawEdgesWithOutArticulations()
	{	
		for(String group : groupNames.keySet())
		{	
			for(String node1: groups.get(group).keySet())
			{
				if(!groups.get(group).get(node1).equals(""))
				{
					String[] edge = groups.get(group).get(node1).toString().split(" ");
					for(int j= 0; j<edge.length && edge[j] !=" " && edge[j]!=""; j++)
					{	
						DiagramLink l = null; 
						if((groupNames.get(group)%2) == 0 && !group.contains("none"))
						{
							l = diagram.getFactory().createDiagramLink(nodesMapGraph.get(group+"."+edge[j]), nodesMapGraph.get(group+"."+node1));
							setPenForLink(l, thickBluePen, blueBrush);
							l.setBaseShape(ArrowHeads.DoubleArrow);
							l.setBaseShapeSize(2f);
							l.setHeadShape(ArrowHeads.None);
						}
						else if((groupNames.get(group)%2) == 1 && !group.contains("none"))
						{
							l = diagram.getFactory().createDiagramLink(nodesMapGraph.get(group+"."+node1), nodesMapGraph.get(group+"."+edge[j]));
							setPenForLink(l, grayPen, grayBrush);
							l.setHeadShape(ArrowHeads.BowArrow);
						}
						l.setShape(LinkShape.Cascading);
						diagram.setShadowsStyle(0);
					}
				}
			}
		}
		arrangeDiagram();
	}

	private String getGroup(String s)
	{
		if(s.contains("."))
		{
			StringTokenizer s1 = new StringTokenizer(s, ".");
			return s1.nextToken();
		}
		return "none";
	}

	void drawEdgesWithArticulations(String filename, Context c) throws IOException
	{	

		DiagramLinkList linksRoutingNeeded = new DiagramLinkList();

		FileInputStream fis = c.openFileInput(filename);
		BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
		String Read;
		if (fis!=null) 
		{ 
			while ((Read = reader.readLine()) != null)
			{    
				if (Read.contains("label: ==") || Read.contains("label: '=='") || Read.contains("label: '>'") || Read.contains("label: '!'") || Read.contains("label: !") || Read.contains("label: >")|| Read.contains("label: ><") || Read.contains("label: '><'") || Read.contains("label: <")|| Read.contains("label: '<'"))
				{
					String label = Read.substring(9).trim().replace("OR", ",").replace("\'", "").replace("\\", "").trim();
					Read = reader.readLine();
					if(Read.contains("s: "))
					{
						String node1 = Read.substring(5).trim().replace("\'", "").replace("\\", "").trim();
						Read = reader.readLine();
						if(Read.contains("t: "))
						{
							String node2 = Read.substring(5).trim().replace("\'", "").replace("\\", "").trim();
							String group1 = getGroup(node1);
							String group2 = getGroup(node2);
							//System.out.println("B++"+node1+"G++"+node2);
							DiagramLink l = null;

							if(groupNames.get(group1) > groupNames.get(group2))
							{
								l = diagram.getFactory().createDiagramLink(nodesMapGraph.get(node2), nodesMapGraph.get(node1));							
								setPenForLink(l,thinBluePen, blueBrush);
								l.setHeadBrush(blueBrush);
							}	
							else
							{
								l = diagram.getFactory().createDiagramLink(nodesMapGraph.get(node1), nodesMapGraph.get(node2));	
								setPenForLink(l, lightGrayPen, lightGrayBrush);
								l.setHeadBrush(lightGrayBrush);
							}
							routeLinks(l);
							setLinkLabelRegular(l,label, 0.08f);		
							l.setShape(LinkShape.Bezier);
							linksRoutingNeeded.add(l);
						}
					}
				}
				else if(Read.contains("label: in") || Read.contains("label: out"))
				{
					Read = reader.readLine();
					if(Read.contains("s: "))
					{
						String node1 = Read.substring(5).trim().replace("\'", "").replace("\\", "").trim();
						Read = reader.readLine();
						if(Read.contains("t: "))
						{
							String node2 = Read.substring(5).trim().replace("\'", "").replace("\\", "").trim();
							//String group1 = getGroup(node1);
							//String group2 = getGroup(node2);
							DiagramLink l = null;
							/*if(groupNames.get(group1) < groupNames.get(group2))
							{
								l = diagram.getFactory().createDiagramLink(nodesMapGraph.get(node2), nodesMapGraph.get(node1));	
								setPenForLink(l,thinBluePen, blueBrush);
								l.setBaseShape(ArrowHeads.DoubleArrow);
								l.setBaseShapeSize(2f);
								l.setHeadShape(ArrowHeads.None);
								l.setHeadBrush(blueBrush);
							}
							else
							{*/
							l = diagram.getFactory().createDiagramLink(nodesMapGraph.get(node1), nodesMapGraph.get(node2));							
							setPenForLink(l, lightGrayPen, lightGrayBrush);
							l.setHeadShape(ArrowHeads.BowArrow);
							l.setHeadBrush(lightGrayBrush);
							//}		

							l.setShape(LinkShape.Bezier);
							linksRoutingNeeded.add(l);
						}
					}	
				}
				else
				{}
			}
		}
		diagram.arrangeLinkLabels();
		checkLinkLabels();
		//linkRouter.RouteLinks(linksRoutingNeeded);
		//diagram.setLinkRouter(linkRouter);
	}


	private void parseInputFile(String filename, Context c) throws IOException
	{
		FileInputStream fis = c.openFileInput(filename);
		BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
		String Read;
		if (fis!=null) 
		{ 
			while ((Read = reader.readLine()) != null)
			{    
				if(Read.contains("concept: "))
				{	
					String concept = Read.substring(11).replace("\'", "").replace("\\", "").trim();
					StringBuffer z = new StringBuffer();
					Map <String, StringBuffer> groupNodes = new HashMap<String, StringBuffer>();
					groupNodes.put(concept, z);
					Read = reader.readLine();
					if(Read.contains("group: "))
					{	
						String next = Read.substring(9).replace("\'", "").replace("\\", "").trim().replace("(+)", "none");
						if(groups.get(next)==null)
							groups.put(next, groupNodes);
						else
						{
							Map <String, StringBuffer> groupNodes1 = groups.get(next);
							StringBuffer z1 = new StringBuffer();
							groupNodes1.put(concept, z1);
						}
					}
				}
				if(Read.contains("label: isa"))
				{
					Read = reader.readLine();
					if(Read.contains("s: "))
					{
						String edgeNode1 = Read.substring(5).replace("\'", "").replace("\\", "").trim();
						StringTokenizer groupConcept = new StringTokenizer(edgeNode1, ".");	
						String group = groupConcept.nextToken();
						String concept = groupConcept.nextToken();
						Read = reader.readLine();
						if(Read.contains("t: "))
						{
							String edgeNode2 = Read.substring(5).replace("\'", "").replace("\\", "").replace("}", "").trim();
							StringTokenizer groupConcept1 = new StringTokenizer(edgeNode2, ".");	
							groupConcept1.nextToken();
							String concept1 = groupConcept1.nextToken();
							StringBuffer concept2 = new StringBuffer();
							concept2.append(concept1);
							groups.get(group).get(concept).append(concept1+" ");
						}
					}	
				}
			}
		}
	}
}  
