package Converter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.regex.Pattern;

public class SvgCsvConverter {
    private ObservableList<SVGPath> svg;

    public SvgCsvConverter(ObservableList<SVGPath> svg) {
        this.svg = svg;
    }



    public static void csvFileSaver(Stage currentStage, ObservableList<SVGPath> paths) {
            String csvString = csvGenerator(paths);

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extensionFilter);

        File file = fileChooser.showSaveDialog(currentStage);
        if (file != null) {
            try {
                //writes the CSV text code in a File
                Files.write(file.toPath(), csvString.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
            } catch (Exception e) {
                System.out.println("Error");
            }
        }
    }


    public static String csvGenerator(ObservableList<SVGPath> list){
        StringBuilder csvString = new StringBuilder();

            csvString.append("id,fill_color,stroke_color,stroke-width,path\r\n");

            for (int i = 0; i < list.size(); i++) {
                SVGPath c = list.get(i);
                String fillcoror = c.getFill()!=null? "#" + c.getFill().toString().substring(2): "#00000000";
                String strokecolor = c.getStroke()!=null? "#" + c.getStroke().toString().substring(2) : "#00000000";
                double stroke_width = c.getStrokeWidth();
                csvString.append(i + "," + fillcoror +
                        "," + strokecolor +
                        "," + stroke_width +
                        "," + c.getContent() + "\r\n"
                );
            }

        return csvString.toString();
    }

    public static ObservableList<SVGPath> cvsToSvgGenerator(File file) {
        ObservableList<SVGPath> svgPaths = FXCollections.observableArrayList();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String l;
            while ((l = br.readLine()) != null) {
                SVGPath p = new SVGPath();
                String[] values = l.split(",");

                if(Pattern.matches("#([0-9a-f]{3}|[0-9a-f]{6}|[0-9a-f]{8})",values[1]))
                    p.setFill(Color.web(values[1]));
                if(Pattern.matches("#([0-9a-f]{3}|[0-9a-f]{6}|[0-9a-f]{8})",values[2]))
                    p.setStroke(Color.web(values[2]));
                if(Pattern.matches("([0-9]*)\\.([0-9]*)", values[3]))
                    p.setStrokeWidth(Double.valueOf(values[3]));
                try {
                    p.setContent(values[4]);
                }catch(Exception exception){

                }

                svgPaths.add(p);


            }
        } catch (Exception e) {
            System.out.println("CSV File cannot be converted");
            e.printStackTrace();
        }
        return svgPaths;

    }

    public static void svgFileSaver(Stage currentStage, Canvas canvas, ObservableList<SVGPath> paths){
        StringBuilder svg = new StringBuilder();
        //create SVG Header
        svg.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n" +
                "<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\"" + canvas.getWidth() + "\" height=\"" + canvas.getHeight() + "\">\n");

        for (SVGPath path : paths) {
            //append every SVG Path to the String
            svg.append(svgToString(path));

        }
        svg.append(
                "</svg>");

        String svgString = svg.toString();
        //after saving the history is cleared
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("SVG files (*.svg)", "*.svg");
        fileChooser.getExtensionFilters().add(extensionFilter);

        File file = fileChooser.showSaveDialog(currentStage);
        if (file != null) {
            try {
                //writes the SVG text code in a File
                Files.write(file.toPath(), svgString.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

            } catch (Exception e) {
                System.out.println("Error");
            }
        }
    }
    private static String svgToString(SVGPath path) {
        String head = "<path";
        String fill = path.getFill()!=null?"#" + path.getFill().toString().substring(2):"#00000000";
        String stroke = path.getStroke()!=null?"#" + path.getStroke().toString().substring(2):"#00000000";
        double strokeWidth = path.getStrokeWidth();
        String content = path.getContent();
        String foot = "/>\n";

        return head + " fill=\"" + fill + "\"" + " stroke=\"" + stroke + "\"" + " stroke-width=\"" + strokeWidth + "\"" + " d=\"" + content + "\"" + foot;
    }
    public static ObservableList<SVGPath> fileOpener(Stage currentStage) throws IOException {
        //before opening the history gets cleared


        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        FileChooser.ExtensionFilter ef = new FileChooser.ExtensionFilter("SVG files (*svg)", "*.svg");
        fileChooser.getExtensionFilters().addAll(extensionFilter, ef);
        File file = fileChooser.showOpenDialog(currentStage);

        ObservableList<SVGPath> svgList;

        if(getFileExtension(file).equals(".csv")) {
            svgList = SvgCsvConverter.cvsToSvgGenerator(file);

        } else{
            SVGMetaPost converter = new SVGMetaPost(file.toURI().toString());
            svgList = converter.runPath();
        }
       return svgList;
    }

    private static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf);
    }

}




