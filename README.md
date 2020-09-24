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
