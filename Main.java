
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("MemoryCard/MemoryCard.fxml")));
        Scene scene = new Scene(root, 410, 500);
        String css = Objects.requireNonNull(getClass().getResource("/Memory.css")).toExternalForm();
        scene.getStylesheets().add(css);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args){

        launch(args);
    }
}
