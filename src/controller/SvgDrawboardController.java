package controller;

import Converter.SvgCsvConverter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import Converter.ShapeConverter;
import javafx.stage.StageStyle;

import java.util.Stack;

public class SvgDrawboardController {


    //store all Actions in order to undo them
     Stack<Group> undoHistory = new Stack<>();
     Group drawing = new Group();
     private static Stage stage;
     //store every Shape as a SVG Path in a List
     private ObservableList<SVGPath> paths;
     private GraphicsContext gc;
     //store the scale of canvas and
     private double scale = 1.0;
     private double totalScale = 1.0;
     //store X Y Offset for moving the stage
     private double xOffset = 0;
     private double yOffset = 0;


    //FXML for Drawboard
    @FXML
    private TextField strokeWidthText;
    @FXML
    private ToggleGroup tools;
    @FXML
    private ToggleButton circlebtn;
    @FXML
    private ToggleButton rectbtn;
    @FXML
    private ToggleButton drowbtn;
    @FXML
    private StackPane zoomPane;
    @FXML
    private Canvas canvas;
    @FXML
    private ColorPicker fillCP;
    @FXML
    private ColorPicker strokeCP;
    @FXML
    private HBox stageHead;
    @FXML
    private Button undo;
    @FXML
    private RadioButton isolate;
    @FXML
    private Button save;
    @FXML
    private Button open;
    @FXML
    private RadioButton editLines;

     //FXML for LineWindow
     @FXML
     private ToggleButton extendLine;

     public void setUp(){
         //setting up default values
         stage.initStyle(StageStyle.TRANSPARENT);
         gc = canvas.getGraphicsContext2D();
         paths = FXCollections.observableArrayList();
         zoomPane.setMinSize(canvas.getWidth(), canvas.getHeight());
         fillCP.setValue(Color.TRANSPARENT);
         strokeCP.setValue(Color.BLACK);
         gc.setLineWidth(1);

         run();
     }
    public void run() {

          //initialize possible shapes
          Line draw = new Line();
          Line position = new Line();
          Rectangle rect = new Rectangle();
          Circle circ = new Circle();


          //change stroke-width
          strokeWidthText.setOnAction(e -> {
               try {
                    gc.setLineWidth(Double.valueOf(strokeWidthText.getText()));

               } catch (Exception exc) {
                    System.out.println("stroke-width invalide");
               }
          });

          //drawing shapes
          canvas.setOnMousePressed(e -> {

              double mouseX = e.getX() / totalScale;
              double mouseY = e.getY() / totalScale;
               if (drowbtn.isSelected()) {
                    gc.setStroke(strokeCP.getValue());
                    gc.lineTo(mouseX, mouseY);
                    draw.setStartX(mouseX);
                    draw.setStartY(mouseY);
               } else if (rectbtn.isSelected()) {
                    gc.setStroke(strokeCP.getValue());
                    gc.setFill(fillCP.getValue());
                    rect.setX(mouseX);
                    rect.setY(mouseY);
               } else if (circlebtn.isSelected()) {
                    gc.setStroke(strokeCP.getValue());
                    gc.setFill(fillCP.getValue());
                    circ.setCenterX(mouseX);
                    circ.setCenterY(mouseY);
               }
               else if(editLines.isSelected()){
                   //found = findLine(mouseX,mouseY);
                   ObservableList<Node> nodes = FXCollections.observableArrayList(paths);
                   Node l = findNearestNode(nodes,mouseX,mouseY);
                   System.out.println((SVGPath)l);
                   deleteAndDraw((SVGPath)l);

               }
               else{
                   position.setStartX(mouseX);
                   position.setStartY(mouseY);
               }
          });

          canvas.setOnMouseDragged(e -> {
              double mouseX = e.getX() / totalScale;
              double mouseY = e.getY() / totalScale;
               if (drowbtn.isSelected()) {
                    //drawing many lines
                    draw.setEndX(mouseX);
                    draw.setEndY(mouseY);
                    draw.setStroke(gc.getStroke());
                    draw.setStrokeWidth(gc.getLineWidth());
                    gc.strokeLine(draw.getStartX(), draw.getStartY(), draw.getEndX(), draw.getEndY());
                    //finished Shapes needs to be add to the Path-List
                    paths.add(ShapeConverter.shapeToSvgPath(draw));
                    Line push = new Line(draw.getStartX(),draw.getStartY(),draw.getEndX(),draw.getEndY());
                    push.setStroke(gc.getStroke());
                    push.setStrokeWidth(gc.getLineWidth());
                    drawing.getChildren().add(push);
                    draw.setStartX(mouseX);
                    draw.setStartY(mouseY);

               }
               //drag the canvas
               else if(!extendLine.isSelected() && !rectbtn.isSelected() && !circlebtn.isSelected() && !editLines.isSelected()){
                   position.setEndX(mouseX);
                   position.setEndY(mouseY);
                   //if zoomed into the canvas should move slower
                   double deltaX = totalScale>=1?Math.abs(position.getEndX() - position.getStartX()) / totalScale:Math.abs(position.getEndX() - position.getStartX()) * totalScale;
                   double deltaY = totalScale>=1?Math.abs(position.getEndY() - position.getStartY()) / totalScale:Math.abs(position.getEndY() - position.getStartY()) * totalScale;
                   canvas.setTranslateX(position.getStartX() < position.getEndX() ? canvas.getTranslateX() + deltaX : canvas.getTranslateX() - deltaX);
                   canvas.setTranslateY(position.getStartY() < position.getEndY() ? canvas.getTranslateY() + deltaY : canvas.getTranslateY() - deltaY);
               }
          });

          canvas.setOnMouseReleased(e -> {
              double mouseX = e.getX() / totalScale;
              double mouseY = e.getY() / totalScale;

               if (drowbtn.isSelected()) {
                    draw.setEndX(mouseX);
                    draw.setEndY(mouseY);
                    draw.setStroke(gc.getStroke());
                    draw.setStrokeWidth(gc.getLineWidth());
                    gc.strokeLine(draw.getStartX(), draw.getStartY(), draw.getEndX(), draw.getEndY());
                    //add Shape to the SVG path List
                    paths.add(ShapeConverter.shapeToSvgPath(draw));
                    Line push = new Line(draw.getStartX(),draw.getStartY(),draw.getEndX(),draw.getEndY());
                    push.setStroke(gc.getStroke());
                    push.setStrokeWidth(gc.getLineWidth());
                    drawing.getChildren().add(push);
                    undoHistory.push(drawing);
                    drawing = new Group();

               } else if (rectbtn.isSelected()) {

                    //calculating the width trough the mouse-coordinates
                    rect.setWidth(Math.abs((mouseX - rect.getX())));
                    rect.setHeight(Math.abs((mouseY - rect.getY())));

                    //if mouse got dragged in the opposite direction (startCoordinates > closeCoordinates)
                    if (rect.getX() > mouseX) {
                         rect.setX(mouseX);
                    }
                    if (rect.getY() > mouseY) {
                         rect.setY(e.getY() / totalScale);
                    }

                    rect.setStroke(gc.getStroke());
                    rect.setStrokeWidth(gc.getLineWidth());
                    rect.setFill(gc.getFill());
                    gc.fillRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
                    gc.strokeRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());

                    paths.add(ShapeConverter.shapeToSvgPath(rect));
                    Rectangle push = new Rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
                    push.setFill(rect.getFill());
                    push.setStroke(rect.getStroke());
                    undoHistory.push(new Group(push));

               } else if (circlebtn.isSelected()) {
                    circ.setRadius((Math.abs(mouseX - circ.getCenterX()) + Math.abs(mouseY - circ.getCenterY())) / 2);
                    if (circ.getCenterX() > mouseX) {
                        circ.setCenterX(mouseX);
                    }
                    if (circ.getCenterY() > mouseY) {
                        circ.setCenterY(mouseY);
                    }

                    circ.setStroke(gc.getStroke());
                    circ.setStrokeWidth(gc.getLineWidth());
                    circ.setFill(gc.getFill());
                    gc.fillOval(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), circ.getRadius());
                    gc.strokeOval(circ.getCenterX(), circ.getCenterY(), circ.getRadius(), circ.getRadius());

                    //add shape to the SVG-Path List
                    paths.add(ShapeConverter.shapeToSvgPath(circ));
                   Circle push = new Circle(circ.getCenterX(), circ.getCenterY(), circ.getRadius());
                   push.setStroke(circ.getStroke());
                   push.setFill(circ.getFill());
                   undoHistory.push(new Group(push));

               }
               else if(!editLines.isSelected()){
                   //close the canvas track path
                   position.setEndY(mouseX);
                   position.setEndX(mouseY);
               }
          });
          //zoom in and zoom out
        canvas.setOnScroll(e->{
            //check if zoomed in or out
            if (e.getDeltaY() == 0) {
                return;
            }

            double scaleFactor =
                    (e.getDeltaY() > 0)
                            ? 1.1
                            : 1/1.1;

            //from the basic view zoomed out is not allowed, that one is locked
            if (canvas.getWidth() >= zoomPane.getMinWidth() || scaleFactor > 1){

                    //action should be only done with every second scrool (perfomance based)
                    scale = scale == 1 ? scale * scaleFactor : 1;
                    gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                    gc.scale(scale, scale);

                    //after scaling we need to draw the content again on a bigger canvas in order not to cut something out
                    for (SVGPath path : paths) {
                        draw(path);
                    }
                    canvas.setWidth(canvas.getWidth() * scale);
                    canvas.setHeight(canvas.getHeight() * scale);
                    //in order to zoom in from the center
                    canvas.setTranslateX(canvas.getTranslateX() * scale);
                    canvas.setTranslateY(canvas.getTranslateY() * scale);

                    //calc the total sclaing done
                    totalScale = totalScale * scale;

            }
        });

        isolate.setOnAction(e->{
            paths.clear();
            undoHistory.clear();
        });

        //in order to make the Stage movable
        stageHead.setOnMousePressed(e->{
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });

        stageHead.setOnMouseDragged(e->{
            stage.setX(e.getScreenX() - xOffset);
            stage.setY(e.getScreenY() - yOffset);
            if(e.getScreenX() == 0.0 && e.getScreenY() == 0.0) {
                stage.setX(0);
                stage.setY(0);
                stage.setMaximized(true);
            }
            stage.setOpacity(0.8f);
        });
        stageHead.setOnMouseReleased(e->{
            stage.setOpacity(1.0f);
        });
     }

    private void deleteAndDraw(SVGPath l) {
        paths.remove(l);
        gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
        for (SVGPath s : paths)
            draw(s);
    }

    private Node findNearestNode(ObservableList<Node> nodes, double x, double y) {
        Point2D pClick = new Point2D(x, y);
        Node nearestNode = null;
        double closestDistance = Double.POSITIVE_INFINITY;

        for (Node node : nodes) {
            Bounds bounds = node.getBoundsInParent();
            Point2D[] corners = new Point2D[] {
                    new Point2D(bounds.getMinX(), bounds.getMinY()),
                    new Point2D(bounds.getMaxX(), bounds.getMinY()),
                    new Point2D(bounds.getMaxX(), bounds.getMaxY()),
                    new Point2D(bounds.getMinX(), bounds.getMaxY()),
            };

            for (Point2D pCompare: corners) {
                double nextDist = pClick.distance(pCompare);
                if (nextDist < closestDistance) {
                    closestDistance = nextDist;
                    nearestNode = node;
                }
            }
        }

        return nearestNode;
    }

    //load the LineWindow stage
    public void extend(ActionEvent actionEvent){
        if(extendLine.isSelected()) {
            FXMLLoader sLoader = new FXMLLoader(getClass().getResource("/source/lineController.fxml"));
            try {
                AnchorPane sRoot = sLoader.load();
                LineController lineController = (LineController) sLoader.getController();
                lineController.setController(this);
                Stage lineWindow = new Stage();
                lineWindow.initStyle(StageStyle.UTILITY);
                lineWindow.setTitle("");
                lineWindow.setScene(new Scene(sRoot));
                lineWindow.setResizable(false);
                lineController.setStage(lineWindow);
                lineController.run(extendLine);
                lineWindow.setX(100);
                lineWindow.setY(100);
                lineWindow.show();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }


     //draw SVG path
     public void draw (SVGPath path){
          gc.beginPath();
          gc.setFill(path.getFill());
          gc.setStroke(path.getStroke());
          gc.setLineWidth(path.getStrokeWidth());
          gc.appendSVGPath(path.getContent());
          gc.stroke();
          gc.fill();
     }


     public void setStage(Stage stage) {
          SvgDrawboardController.stage = stage;
     }


     public void undo(ActionEvent actionEvent) {

          if (!undoHistory.empty()) {
               //clear canvas and List
               gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
               paths.clear();
               Group removeGroup = undoHistory.lastElement();
              //draw every Shape except for the deleted one
               undoHistory.pop();

              for (int i = 0; i < undoHistory.size(); i++) {
                  Group g = undoHistory.elementAt(i);
                  for (int j = 0; j < g.getChildren().size(); j++) {
                      Shape shape = (Shape) g.getChildren().get(j);
                      paths.add(ShapeConverter.shapeToSvgPath(shape));
                      if (shape.getClass() == Line.class) {
                          Line temp = (Line) shape;
                          gc.setLineWidth(temp.getStrokeWidth());
                          gc.setStroke(temp.getStroke());
                          gc.setFill(temp.getFill());
                          gc.strokeLine(temp.getStartX(), temp.getStartY(), temp.getEndX(), temp.getEndY());
                      } else if (shape.getClass() == Rectangle.class) {
                          Rectangle temp = (Rectangle) shape;
                          gc.setLineWidth(temp.getStrokeWidth());
                          gc.setStroke(temp.getStroke());
                          gc.setFill(temp.getFill());
                          gc.fillRect(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight());
                          gc.strokeRect(temp.getX(), temp.getY(), temp.getWidth(), temp.getHeight());
                      } else if (shape.getClass() == Circle.class) {
                          Circle temp = (Circle) shape;
                          gc.setLineWidth(temp.getStrokeWidth());
                          gc.setStroke(temp.getStroke());
                          gc.setFill(temp.getFill());
                          gc.fillOval(temp.getCenterX(), temp.getCenterY(), temp.getRadius(), temp.getRadius());
                          gc.strokeOval(temp.getCenterX(), temp.getCenterY(), temp.getRadius(), temp.getRadius());
                      } else if (shape.getClass() == Ellipse.class) {
                          Ellipse temp = (Ellipse) shape;
                          gc.setLineWidth(temp.getStrokeWidth());
                          gc.setStroke(temp.getStroke());
                          gc.setFill(temp.getFill());
                          gc.fillOval(temp.getCenterX(), temp.getCenterY(), temp.getRadiusX(), temp.getRadiusY());
                          gc.strokeOval(temp.getCenterX(), temp.getCenterY(), temp.getRadiusX(), temp.getRadiusY());
                      }
                      else if (shape.getClass() == SVGPath.class){
                          SVGPath temp = (SVGPath) shape;
                          draw(temp);
                      }
                  }
              }
          } else {
               System.out.println("there is no action to undo");
          }

     }

     public void saveDrawing(MouseEvent mouseEvent) {
         SvgCsvConverter.svgFileSaver(stage, canvas, paths);
         undoHistory.clear();
         paths.clear();
     }

     public void open(ActionEvent actionEvent) throws Exception {
         undoHistory.clear();
         paths.clear();
         gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

         ObservableList<SVGPath> svgList = SvgCsvConverter.fileOpener(stage);
         System.out.println("svglist " + svgList.size());

         //redraw after we got our SVGPaths
         Group svgShape = new Group();
         for (SVGPath c : svgList) {
             c.setId("test");
             draw(c);
             svgShape.getChildren().add(c);
             paths.add(c);
         }
         undoHistory.push(svgShape);
     }

     public void saveCSV(MouseEvent mouseEvent){
          SvgCsvConverter.csvFileSaver(stage,paths);
          //after saving history gets cleared
          paths.clear();
          undoHistory.clear();

     }

    public void reset(MouseEvent mouseEvent) {

        fillCP.setValue(Color.TRANSPARENT);
        strokeCP.setValue(Color.BLACK);
        gc.setLineWidth(1);
        paths.clear();
        undoHistory.clear();
        double resetScale = 1.0/totalScale;
        gc.scale(resetScale,resetScale);
        totalScale = 1.0;
        canvas.setWidth(zoomPane.getMinWidth());
        canvas.setHeight(zoomPane.getMinHeight());
        canvas.setTranslateX(0);
        canvas.setTranslateY(0);
        gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
        
    }

    public static Stage getStage() {
        return stage;
    }

    public ObservableList<SVGPath> getPaths() {
        return paths;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public ColorPicker getFillCP() {
        return fillCP;
    }

    public void setFillCP(ColorPicker fillCP) {
        this.fillCP = fillCP;
    }

    public ColorPicker getStrokeCP() {
        return strokeCP;
    }

    public void setStrokeCP(ColorPicker strokeCP) {
        this.strokeCP = strokeCP;
    }

    public StackPane getZoomPane() {
        return zoomPane;
    }

    public void setZoomPane(StackPane zoomPane) {
        this.zoomPane = zoomPane;
    }
    public double getScale() {
        return scale;
    }

    public double getTotalScale() {
        return totalScale;
    }


    public void minimize(MouseEvent mouseEvent) {
         stage.setIconified(!stage.isIconified());
    }

    public void maximize(MouseEvent mouseEvent) { stage.setMaximized(!stage.isMaximized()); }

    public void close(MouseEvent mouseEvent) {
         stage.close();
    }


}

