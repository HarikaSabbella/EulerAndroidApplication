package com.example.graphintegration;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.mindfusion.diagramming.Behavior;
import com.mindfusion.diagramming.DashStyle;
import com.mindfusion.diagramming.Diagram;
import com.mindfusion.diagramming.DiagramAdapter;
import com.mindfusion.diagramming.DiagramLink;
import com.mindfusion.diagramming.DiagramLinkList;
import com.mindfusion.diagramming.DiagramNode;
import com.mindfusion.diagramming.DiagramView;
import com.mindfusion.diagramming.LayeredLayout;
import com.mindfusion.diagramming.LayeredLayoutLinkType;
import com.mindfusion.diagramming.NodeEvent;
import com.mindfusion.diagramming.Pen;
import com.mindfusion.diagramming.Shape;
import com.mindfusion.diagramming.ShapeNode;
import com.mindfusion.diagramming.SolidBrush;
import com.mindfusion.diagramming.SpringLayout;
import com.mindfusion.diagramming.TreeLayout;
import com.mindfusion.diagramming.TreeLayoutBalance;
import com.mindfusion.diagramming.TreeLayoutType;
import com.mindfusion.diagramming.jlayout.Direction;
import com.mindfusion.diagramming.jlayout.Orientation;
import com.mindfusion.drawing.Color;

public class ShowGraphsActivity extends Activity {
	Map <String, Map<String, StringBuffer>> groups = new HashMap<String, Map<String, StringBuffer>>();
	Map <String, DiagramNode> nodesMapGraph = new HashMap<String,DiagramNode>();

	String[] articulations = {"==", "<>", "<", ">"};
	ListView listView;
	ArrayAdapter<String> adapter;
	Button addEdgeButton;
	DiagramView touchPad;
	Diagram diagram; 
	Color red;
	Context context;
	

	Pen redPen = new Pen(0.3f, Color.red);
	Pen grayPen = new Pen(0.3f, Color.gray);
	Pen blackPen = new Pen(0.6f, Color.black);
	Pen whitePen = new Pen(0.3f, Color.white);
	
	SolidBrush blackBrush = new SolidBrush(Color.black);
	SolidBrush redBrush = new SolidBrush(Color.red);
	SolidBrush grayBrush = new SolidBrush(Color.gray);
	SolidBrush lightGrayBrush = new SolidBrush(Color.LIGHT_GRAY);
	SolidBrush whiteBrush = new SolidBrush(Color.white);

	Spinner toSpinner;
	Spinner fromSpinner;
	ArrayAdapter<String> nodesAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_graphs);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		
		listView = (ListView) findViewById(R.id.list);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, articulations);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setAdapter(adapter);

		addEdgeButton = (Button) findViewById(R.id.button1);
		
		touchPad = (DiagramView) findViewById(R.id.touchpad);
		touchPad.setBehavior(Behavior.SelectOnly);
		diagram = touchPad.getDiagram();
		diagram.setLinkHeadShapeSize(2);
		diagram.setBackBrush(lightGrayBrush);

		fromSpinner = (Spinner) findViewById(R.id.spinner1);
		toSpinner = (Spinner) findViewById(R.id.spinner2);
		nodesAdapter = new ArrayAdapter<String>(ShowGraphsActivity.this, android.R.layout.simple_spinner_item);
		nodesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		nodesAdapter.add(" ");
		fromSpinner.setAdapter(nodesAdapter);
		toSpinner.setAdapter(nodesAdapter);
		setUpSpinners();

		String url1 = getIntent().getExtras().getString("url1");
		context = this.getApplicationContext();
		new Thread2().execute(url1);

		setUpNodesForSelection();
		addEdgesSelectedAfterAddEdgeButtonClicked();	
	}
	
	private void setPenForLink(DiagramLink link, Pen pen, SolidBrush brush)
	{
		link.setPen(pen);
		link.setHeadPen(pen);
		link.setTextBrush(brush);
	}
	
	private void arrangeDiagram()
	{
		/*Spring or Fractal or Layered*/
		LayeredLayout layout = new LayeredLayout();
		layout.setNodeDistance(20f);
		layout.setDirection(Direction.Reversed);
		layout.setOrientation(Orientation.Vertical);	
		layout.setLinkType(LayeredLayoutLinkType.Straight);
		layout.setLayerDistance(20f);
		layout.setMultipleGraphsPlacement(0);
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
					resetNodeColors();
					setNodeColors();
				}	
				//nodesMapGraph.get(arg0.getSelectedItem()).setBrush(redBrush);	
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
					resetNodeColors();
					setNodeColors();
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}	
		});
	}
	private void setUpNodesForSelection()
	{
		diagram.addDiagramListener(new DiagramAdapter () 
		{
			int counter = -1;
			@Override
			public void nodeSelected(NodeEvent arg0) 
			{
				if(counter == -1)
				{
					arg0.getNode().setBrush(redBrush);
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
				else{}
			}	
		});
	}
	private void resetNodeColors()
	{
		for(DiagramNode node: diagram.getNodes())
		{
			nodesMapGraph.get(((ShapeNode) node).getText()).setBrush(blackBrush);
		}
		
	}
	private void setNodeColors()
	{
		if(!fromSpinner.getSelectedItem().toString().equals(" ") && !toSpinner.getSelectedItem().toString().equals(" "))
		{
			nodesMapGraph.get(fromSpinner.getSelectedItem().toString()).setBrush(redBrush);
			nodesMapGraph.get(toSpinner.getSelectedItem().toString()).setBrush(redBrush);
		}
		else if (fromSpinner.getSelectedItem().toString().equals(" ") && !toSpinner.getSelectedItem().toString().equals(" "))
		{
			nodesMapGraph.get(toSpinner.getSelectedItem().toString()).setBrush(redBrush);
		}
		else if (!fromSpinner.getSelectedItem().toString().equals(" ") && toSpinner.getSelectedItem().toString().equals(" "))
		{
			nodesMapGraph.get(fromSpinner.getSelectedItem().toString()).setBrush(redBrush);
		}
	}

	
	private void addEdgesSelectedAfterAddEdgeButtonClicked() 
	{
		addEdgeButton.setOnClickListener(new OnClickListener () 
		{
			@Override
			public void onClick(View arg0) 
			{
				String from = null;
				String to = null;
				if(fromSpinner.getSelectedItem() != " ")
					from = fromSpinner.getSelectedItem().toString();
				if(toSpinner.getSelectedItem() != " ")
					to = toSpinner.getSelectedItem().toString();

				SparseBooleanArray sp= listView.getCheckedItemPositions();
				/*append first thing in sp so that 'OR' can be put in between articulation 'keys' after first*/
				StringBuffer articulation= new StringBuffer(); 
				articulation.append(articulations[sp.keyAt(0)]);
				for(int i=1;i<sp.size();i++)
				{
						articulation.append(" OR "+articulations[sp.keyAt(i)]);
				}
				int indicator = 0;
				DiagramLinkList s = nodesMapGraph.get(from).getAllOutgoingLinks();
				for (DiagramLink link: s)
				{
					ShapeNode destinationsOfFromNode = (ShapeNode) link.getDestination();
					ShapeNode destinationFromUserSelection = (ShapeNode) nodesMapGraph.get(to);
					if(destinationsOfFromNode.getText().equals(destinationFromUserSelection.getText()))
					{
						if(link.getLabels() == null)
						{
							setPenForLink(link, redPen, redBrush);
							link.addLabel(articulation.toString());	
						}
						else
						{
							String newLabel = link.getLabels().get(0).getText();
							link.removeLabel(link.getLabels().get(0));
							setPenForLink(link, redPen, redBrush);
							link.addLabel(articulation.toString()+" OR "+newLabel);	
						}
						indicator = 100;
						break;
					}
				}

				if(indicator == 0)
				{
					DiagramLink l = diagram.getFactory().createDiagramLink(nodesMapGraph.get(from), nodesMapGraph.get(to));			
					l.addLabel(articulation.toString());						
					setPenForLink(l, redPen, redBrush);
				}
				resetNodeColors();
				articulation.setLength(0);
				listView.clearChoices();
				//arrangeDiagram();
			}
		});
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
				//System.out.println("HELLO"+groups.size()+" "+groups.entrySet()+"\n");
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
		for (String group: groups.keySet())
		{	
			for(String node: groups.get(group).keySet())
			{
				ShapeNode diagramNode = diagram.getFactory().createShapeNode(0, 0, 8, 8, Shape.fromId("Ellipse"));
				diagramNode.setBrush(blackBrush); 
				diagramNode.setTextBrush(whiteBrush);
				if(group.contains("none"))
				{
					diagramNode.setText(node);
					nodesMapGraph.put(node, diagramNode);
				}
				else
				{
					diagramNode.setText(group+"."+node);
					nodesMapGraph.put(group+"."+node, diagramNode);				
				}
				nodesAdapter.add(diagramNode.getText());
			}
		}
	}

	void drawEdgesWithOutArticulations()
	{
		for(String group: groups.keySet())
		{
			for(String node1: groups.get(group).keySet())
			{
				if(!groups.get(group).get(node1).equals(""))
				{
					String[] edge = groups.get(group).get(node1).toString().split(" ");
					for(int j= 0; j<edge.length && edge[j] !=" " && edge[j]!=""; j++)
					{	
						System.out.println("group "+group+" node "+node1+" "+edge[j]+"\n");
						DiagramLink l = diagram.getFactory().createDiagramLink(nodesMapGraph.get(group+"."+node1), nodesMapGraph.get(group+"."+edge[j]));
						setPenForLink(l, blackPen, blackBrush);
						diagram.setShadowsStyle(0);
					}
				}
			}
		}
		arrangeDiagram();
	}
	void drawEdgesWithArticulations(String filename, Context c) throws IOException
	{
		FileInputStream fis = c.openFileInput(filename);
		BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
		String Read;
		if (fis!=null) 
		{ 
			while ((Read = reader.readLine()) != null)
			{    
				if (Read.contains("label: ==") || Read.contains("label: '>'") ||  Read.contains("label: >")|| Read.contains("label: ><") || Read.contains("label: <"))
				{
					String label = Read.substring(9).trim();
					Read = reader.readLine();
					if(Read.contains("s: "))
					{
						String node1 = Read.substring(5).trim();
						Read = reader.readLine();
						if(Read.contains("t: "))
						{
							String node2 = Read.substring(5).trim();
							DiagramLink l = diagram.getFactory().createDiagramLink(nodesMapGraph.get(node1), nodesMapGraph.get(node2));							
							setPenForLink(l,grayPen, grayBrush);
							l.addLabel(label);
							//arrangeDiagram();
						}
					}
				}
				else if(Read.contains("label: in") || Read.contains("label: out"))
				{
					Read = reader.readLine();
					if(Read.contains("s: "))
					{
						String node1 = Read.substring(5).trim();
						Read = reader.readLine();
						if(Read.contains("t: "))
						{
							String node2 = Read.substring(5).trim();
							DiagramLink l = diagram.getFactory().createDiagramLink(nodesMapGraph.get(node1), nodesMapGraph.get(node2));							
							setPenForLink(l,whitePen, lightGrayBrush);
							//arrangeDiagram();
						}
					}	
				}
				else
				{}
			}
		}		
	}

	void parseInputFile(String filename, Context c) throws IOException
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
					String concept = Read.substring(11);
					StringBuffer z = new StringBuffer();
					Map <String, StringBuffer> groupNodes = new HashMap<String, StringBuffer>();
					groupNodes.put(concept, z);
					Read = reader.readLine();
					if(Read.contains("group: "))
					{	
						String next = Read.substring(9).replace("\'", "");
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
						String edgeNode1 = Read.substring(5);
						StringTokenizer groupConcept = new StringTokenizer(edgeNode1, ".");	
						String group = groupConcept.nextToken();
						String concept = groupConcept.nextToken();
						Read = reader.readLine();
						if(Read.contains("t: "))
						{
							String edgeNode2 = Read.substring(5);
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
