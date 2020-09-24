

import controller.SvgDrawboardController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.logging.Logger;

public class Main extends Application {
    private static Logger logger = Logger.getLogger(SvgDrawboardController.class.getName());
    private SvgDrawboardController mainController;

    @Override
    public void start(Stage stage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/source/svgDrawboard.fxml"));
        BorderPane root = loader.load();
        mainController = (SvgDrawboardController) loader.getController();
        mainController.setStage(stage);
        stage.setTitle("SVG EDITOR");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.getIcons().add(new Image(String.valueOf(getClass().getResource("/source/icon.png"))));
        stage.setResizable(true);
        mainController.setUp();
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

//https://www.indiepedia.de/images/2/22/Heckert_GNU_white.svg