# SVG-Editor
Similar to Inkscape this SVG-Editor in an intuitive Application in order to load,save and edit Vectorfiles. This Project is done in JavaFx and allows you to save your drawings as SVG or CSV, moverover you have the opportunity to load in local SVG Files, display them on the Canvas, zoom in/out and edit the File by adding your drawings to the existing file. In the following sections i will explain every feature in detail.

## How to start SVG-Editor
- in order to start this Project you will need JavaFX with a suitable JDK installed (Project has been developed with JavaFX 11 and JDK 8 [https://gluonhq.com/products/javafx/] [https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html]
- in addition to the standard JavaFX libary you will need the BATIK library: https://xmlgraphics.apache.org/batik/download.html
- you will also need the JFXTRAS library: https://jfxtras.org/
- if you need help -> check: https://www.genuitec.com/docs/archives/adding-libraries-java-project-build-path-myeclipse/
- now you are good to go! Open the Project in your IDE and start Main Method

## Features explained
- the Editor looks like this. You can change the Theme in CSS File if there is a need to. I chose a nice modern bluish dark theme with custom Stage-Header
![alt text](https://github.com/lukasthekid/SVG-Editor/blob/master/Screenshots/Overview.PNG)
- as you can see you have the opportunity to draw different shapes like rectangles or lines like Microsoft's Paint, but every Shape you draw will be convertet to SVG-Paths, therefore you can scale your drawings as you want (see: https://searchwindevelopment.techtarget.com/definition/vector-graphics#:~:text=Vector%20graphics%20is%20the%20creation,direction%20at%20the%20same%20time.)
- one of the main features is the open funktion, in which you can load in loacally stored SVG or CSV files into the editor. The SVG code will be extracted and converted into JavaFX's class: SVGPath, thus you will be able to egt hand on every single path and change its content. The file opener works very performant and Vector-Files with more than 10k paths to extract and draw on canvas should not be a big deal. (Addition: the Canvas is set on 600 x 800, therefore bigger fill be croped)
(public transport of Vienna, 12k paths)
![alt text](https://github.com/lukasthekid/SVG-Editor/blob/master/Screenshots/OpenSvg.PNG)
- if none of the tools is selected, you can drag the content on the canvas by mouse-dragging
- with mouse scrolling you can zoom in. Because we work with vectorgraphics the content wont get blurry. with every scroll the content gets redrawn.
- if you select the line tool a new window gets displayed, here you have even more features. by clicking "line" you can draw lines by dragging. Moreover you can click on canvas for start and end point for the line ("startMouse" "closeMouse"). You can also type in your start coordinates and how much you want to move on from that point. Surely you can combinate both features.
![alt text](https://github.com/lukasthekid/SVG-Editor/blob/master/Screenshots/LineController.PNG)
- Now i want to display the CSV feature. By saving a drawing to CSV the content will be extract and saved with fill,stroke,stroke-whith and path. With this feature you will be able to load the SVG file into a database, change the color or delete selected path and so on... You can reopen the CSV file and edit again.

