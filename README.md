<h1> Euler Android Application </h1>

Euler is an open source toolkit for merging taxonomies and visualizing results.  Given initial input consisting of any number of taxonomies, the Euler Android Application allows one to modify the input and visualize the worlds that are possible after running Euler on the modified taxonomies on an Android device.

<h2> Overview </h2>
The Euler Android Application provides a very natural, intuitive, simple, yet powerful way to interact with the taxonomies where taxonomies are rendered as trees and articulations are connections.  Currently, there is virtually none/very little user interaction while running Euler.  In addition, there is no way to go back and change taxonomies once a determination is made that taxonomies are inconsistent.  With this application, taxonomies can be built and existing ones can be changed completely using user input and we can verify whether these modifications result in consistent taxonomies and generate possible worlds if they are consistent.  In the case that the taxonomies are not consistent, the user can make further changes and run Euler again!   This feature of the tool advances how the Euler toolkit is used and is a way to close the loop between the initial input and the final output.

<h2> Methodology </h2>
The first screen of the application allows the user to choose sample input to work with from a list of sample Yaml files (converted from CleanTax).   This Yaml file is parsed and the nodes and edges and related articulations are extracted. Then, using the DroidDiagramming library, these nodes and edges are arranged and displayed on the android device. A layered layout algorithm, provided as a function call in the library, is used to arrange the given nodes into a tree like structure.  Then using another series of the DroidDiagramming library function calls, the leaves of the different trees (taxonomies) are made to directly face each other in a manner like below:

![Image](https://github.com/HarikaSabbella/images/blob/master/graph.png?raw=true)

The taxonomies are displayed in this manner because generally new relations/articulations are defined between the leaves of the taxonomies rather than the other nodes of the taxonomies.  

The user can now do one of the following to modify the taxonomies:  delete edges, add new edges, change the way the articulations are displayed on the screen, define their own articulations, and remove, add, or delete articulations on currently present edges.  For ease of use, the user can also double tap on nodes to highlight (view thicker) all the incoming and outgoing edges from nodes and the articulations (if there are any defined for the edges) in enlarged font.   In addition, to guide users that may not be familiar with Euler, there are many error checking facilities built into the application (for example, if the user clicks on the red node when the diagnostic lattice is displayed, they are prompted to click on another node).

Once the user is finished making their modifications to the taxonomies, they can choose to run Euler on the modified taxonomies.   Once Euler is run, if the taxonomies are determined to be consistent by Euler, an aggregate view of the possible worlds is displayed (in SVG format) and the user has the option to choose to view individual worlds as well (also displayed in SVG format on a webpage).  If the taxonomies are determined to be inconsistent, then the diagnostic lattice is shown; at this point, the user can choose to view the log for the result of running Euler and fix the taxonomies themselves or they can click on the different option nodes on the diagnostic lattice to auto-fix the taxonomies accordingly, at which point, Euler is run again and a consistent, aggregate view is displayed (and once again the users can choose to view individual possible worlds on this screen). 

<h2> Demo Video </h2>
[Demo video](https://drive.google.com/file/d/0B5aE6-oQSXlfd3hCVGtUNmxtelE/edit?usp=sharing)

<h2> Getting started with the Euler Android Application </h2>
The remainder of this README provides instructions for getting started with the Euler Android Application, either as a user or as a developer.  For a detailed explanation of the working of the backend/server, please consult the README file located in the EulerServer repository (https://github.com/HarikaSabbella/EulerServer). 

<h1> Instructions for users of application </h1> 
1. Download the [APK](https://drive.google.com/file/d/0B5aE6-oQSXlfTGVXdm5YWno3c1E/view?usp=sharing) file onto your Android device
2. Open the downloaded APK file from the downloads folder on your device 
3. Begin playing around with the application!

<h1> Instructions for developers of application </h1>
1. Download and unzip the EulerAndroidApp-master.zip file
2. Eclipse set-up
    1. Download and install Eclipse Juno (https://eclipse.org/downloads/packages/eclipse-classic-422/junosr2)
    2. In Eclipse, Help -> Install New Software
    3. In the text box labeled “Work With” enter https://dl.google.com/eclipse/plugin/4.2, click “Add” and check the boxes labeled:
        1. Developer Tools
        2. Google App Engine Tools for Android 
        3. Google Plugin for Eclipse 
        4. SDKs 
        ![Image](https://github.com/HarikaSabbella/images/blob/master/screenshot1.png?raw=true)
    4. When prompted with a security warning, click “Ok”
        ![Image](https://github.com/HarikaSabbella/images/blob/master/screenshot2.png?raw=true)
    5. When prompted about restarting Eclipse, choose “Yes”
        ![Image](https://github.com/HarikaSabbella/images/blob/master/screenshot3.png?raw=true)
    6. Once Eclipse opens up again, choose the option to “Open SDK manager” and select the “Android SDK Build-tools 19” option and the “Android 4.4.2 (API 19)” option and uncheck everything else on the Android SDK Manager
        ![Image](https://github.com/HarikaSabbella/images/blob/master/screenshot4.png?raw=true)
3. Open EulerAndroidApp-master in Eclipse
    1. In Eclipse, File -> Import -> Android -> Existing Android Code Into Workspace
    2. Click the “Browse” button and open up the EulerAndroidApp-master folder and click “Finish”
        ![Image](https://github.com/HarikaSabbella/images/blob/master/screenshot5.png?raw=true)
    3. Begin playing around with the code!



