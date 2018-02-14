package sudoku;

import java.io.IOException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SudokuApp extends Application {
	private double xOffset;
	private double yOffset;
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("Sudoku.fxml"));
		Scene scene = new Scene(root);
//		Image titleIcon = new Image("http://files.softicons.com/download/game-icons/brain-games-icons-by-quizanswers/png/512x512/Tic-Tac-Toe-Game-grey.png");

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
		primaryStage.setTitle("Sudoku");
//		primaryStage.getIcons().add(titleIcon);
		primaryStage.show();
		
	}

	public static void main(String[] args) {
		launch(args);
	}
}
