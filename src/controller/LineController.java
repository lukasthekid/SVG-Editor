package controller;

import Converter.ShapeConverter;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.shape.Line;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

import java.util.Stack;

public class LineController {

    @FXML
    private ToggleButton linebtn;
    @FXML
    private ToggleButton startMouseButton;
    @FXML
    private TextField startX;
    @FXML
    private TextField startY;
    @FXML
    private ToggleButton closeMouseButton;
    @FXML
    private TextField closeX;
    @FXML
    private TextField closeY;


    private SvgDrawboardController controller;
    private Stage stage;



    public void run(ToggleButton entryButton){
        stage.setAlwaysOnTop(true);
        Canvas canvas = controller.getCanvas();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        ColorPicker colorPicker = controller.getStrokeCP();
        Stack<Group> undoHistory = controller.undoHistory;
        ObservableList<SVGPath> paths = controller.getPaths();

        Line line = new Line();
        canvas.setOnMouseClicked(e ->{

            if(startMouseButton.isSelected()){
                resetPromtText();
                gc.setStroke(colorPicker.getValue());
                line.setStartX(e.getX() / controller.getTotalScale());
                line.setStartY(e.getY() / controller.getTotalScale());
                line.setStroke(gc.getStroke());
                startX.setPromptText(e.getX() + "");
                startY.setPromptText(e.getY() + "");
            }

            else if(closeMouseButton.isSelected()){
                line.setEndX(e.getX() / controller.getTotalScale());
                line.setEndY(e.getY() / controller.getTotalScale());
                line.setStrokeWidth(gc.getLineWidth());
                gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
                paths.add(ShapeConverter.shapeToSvgPath(line));
                Line push = new Line(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
                push.setStrokeWidth(gc.getLineWidth());
                push.setStroke(gc.getStroke());
                undoHistory.push(new Group(push));
                closeX.setPromptText(e.getX() + "");
                closeY.setPromptText(e.getY() + "");

            }

        });

        startY.setOnAction(e->{
            if(!startX.getText().isEmpty()){
                double x = Double.valueOf(startX.getText());
                double y = Double.valueOf(startY.getText());
                gc.setStroke(colorPicker.getValue());
                line.setStartX(x / controller.getTotalScale());
                line.setStartY(y / controller.getTotalScale());
            }
        });
        closeY.setOnAction(e->{
            if(!closeX.getText().isEmpty()){
                double x = line.getStartX() + Double.valueOf(closeX.getText());
                double y = line.getStartY() - Double.valueOf(closeY.getText());
                line.setEndX(x / controller.getTotalScale());
                line.setEndY(y / controller.getTotalScale());
                line.setStrokeWidth(gc.getLineWidth());
                line.setStroke(gc.getStroke());
                gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
                paths.add(ShapeConverter.shapeToSvgPath(line));
                Line push = new Line(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
                push.setStrokeWidth(gc.getLineWidth());
                push.setStroke(gc.getStroke());
                undoHistory.push(new Group(push));

            }
        });

        canvas.setOnMousePressed(e->{
            if(linebtn.isSelected()){
                gc.setStroke(colorPicker.getValue());
                line.setStartX(e.getX() / controller.getTotalScale());
                line.setStartY(e.getY() / controller.getTotalScale());
            }
        });
        canvas.setOnMouseReleased(e->{
            if(linebtn.isSelected()){
                line.setEndX(e.getX() / controller.getTotalScale());
                line.setEndY(e.getY() / controller.getTotalScale());
                line.setStroke(gc.getStroke());
                line.setStrokeWidth(gc.getLineWidth());
                gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
                paths.add(ShapeConverter.shapeToSvgPath(line));
                Line push = new Line(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
                push.setStrokeWidth(gc.getLineWidth());
                push.setStroke(gc.getStroke());
                undoHistory.push(new Group(push));
            }
        });

        stage.setOnCloseRequest(e->{
            controller.run();
            SvgDrawboardController.getStage().show();
            entryButton.setSelected(false);
            startMouseButton.setSelected(false);
            closeMouseButton.setSelected(false);
            stage.close();
        });



    }

    public void resetPromtText(){
        startX.setPromptText("X");
        startY.setPromptText("Y");
        closeX.setPromptText("X");
        closeY.setPromptText("Y");

    }

    public SvgDrawboardController getController() {
        return controller;
    }

    public void setController(SvgDrawboardController controller) {
        this.controller = controller;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
