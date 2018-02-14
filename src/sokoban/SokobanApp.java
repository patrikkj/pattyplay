package sokoban;

import java.io.IOException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SokobanApp extends Application {
	private double xOffset;
	private double yOffset;
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Sokoban.fxml"));
		Parent root = loader.load();
		
		//Controller used for later reference
		SokobanController controller = loader.getController();
		
		//Create scene based on FXML file
		Scene scene = new Scene(root);
		
		//Transparency settings
		scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		
		//Set mouse pressed
        root.setOnMousePressed(new EventHandler<MouseEvent>() {
           @Override
           public void handle(MouseEvent event) {
               xOffset = event.getSceneX();
               yOffset = event.getSceneY();
           }});
        
        //Set mouse drag
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	if (yOffset < 55) {
	                primaryStage.setX(event.getScreenX() - xOffset);
	                primaryStage.setY(event.getScreenY() - yOffset);
            	}
            }
        });
        
		// Initialize application window
		primaryStage.setScene(scene);
		primaryStage.setTitle("Sokoban");
		primaryStage.show();
		
		//Set key listener
		controller.postInitialize();
	}

	public static void main(String[] args) {
		launch(args);
	}
}