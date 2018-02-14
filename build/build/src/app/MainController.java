package app;

import java.io.IOException;

import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainController {
	//Submodules
	private static int EMPTY_PANE = -1, TIC_ID = 0, SUDOKU_ID = 1;
	private static String ACTIVE_STYLE = "-fx-background-color: #252525", INACTIVE_STYLE = "";
	private int activeModule = EMPTY_PANE;
	private VBox tictactoeBox;
	private VBox sudokuBox;
	

	//Windows
    @FXML private AnchorPane rootPane;
    @FXML private AnchorPane subRootPane;
    
    
    //Main menu + buttons
    @FXML private GridPane menuGrid;
    @FXML private StackPane battleshipStackPane;
    @FXML private StackPane sudokuStackPane;
    @FXML private StackPane sokobanStackPane;
    @FXML private StackPane settingsStackPane;


    private void preloadModules() throws IOException {
    	tictactoeBox = FXMLLoader.load(getClass().getResource("../tictactoe/TicTacToe.fxml"));
    	sudokuBox = FXMLLoader.load(getClass().getResource("../sudoku/Sudoku.fxml"));
    }
    
    
    private void initializeModule(Region subModule, int GAME_ID) {
    	//Retrive stage to re-adjust window size
    	Scene rootScene = rootPane.getScene();
    	Stage rootStage = (Stage) rootScene.getWindow();
    	rootStage.setHeight(subRootPane.getLayoutY() + subModule.getMinHeight());
    	rootStage.setWidth(subModule.getMinWidth());
    	
    	//Inject submodule into main application
    	subRootPane.getChildren().setAll(subModule);
    	activeModule = GAME_ID;
    	
    	//Fit submodule constraits to parent
    	AnchorPane.setTopAnchor(subModule, 0.0);
    	AnchorPane.setBottomAnchor(subModule, 0.0);
    	AnchorPane.setLeftAnchor(subModule, 0.0);
    	AnchorPane.setRightAnchor(subModule, 0.0);
    }
    
    private void clearModules() {
		//Remove all submodules from subRootPane
    	subRootPane.getChildren().clear();
    	
    	// Iterate through menuPane to untrigger button
		for (Node node : menuGrid.getChildren()) {
			if (node.getClass().getSimpleName().equals("JFXButton")) {
				// Cast to JFXButton
				JFXButton button = (JFXButton) node;
				
				//Untrigger button
				button.setStyle(INACTIVE_STYLE);
			}
		}
		
		activeModule = EMPTY_PANE;
    }
    
    @FXML private void initialize() throws IOException {
    	preloadModules();
    }
    
    
    @FXML
    void handleBattleshipClick(ActionEvent event) throws IOException {
    	
    }

    @FXML
    void handleSettingsClick(ActionEvent event) {

    }

    @FXML
    void handleSokobanClick(ActionEvent event) {

    }

    @FXML
    void handleSudokuClick(ActionEvent event) {
    	JFXButton button = (JFXButton) event.getSource();
    	
        if (activeModule == EMPTY_PANE) {
    		//Inject submodule into main application
        	initializeModule(sudokuBox, SUDOKU_ID);
    
        	//Trigger button
        	button.setStyle(ACTIVE_STYLE);
        	
        } else if (activeModule == SUDOKU_ID) {
    		//Remove all submodules and untrigger buttons
    		clearModules();
  
    	//If another module was open, clear modules and open requested module
    	} else {
    		//Remove all submodules and untrigger buttons
    		clearModules();
    		
    		//Inject submodule into main application
        	initializeModule(sudokuBox, SUDOKU_ID);
    		
        	//Trigger button
        	button.setStyle(ACTIVE_STYLE);
    	}
    	
    }

    @FXML
    void handleTicTacToeClick(ActionEvent event) throws IOException {    
    	JFXButton button = (JFXButton) event.getSource();
    	
        if (activeModule == EMPTY_PANE) {
    		//Inject submodule into main application
        	initializeModule(tictactoeBox, TIC_ID);
    
        	//Trigger button
        	button.setStyle(ACTIVE_STYLE);
        	
        } else if (activeModule == TIC_ID) {
    		//Remove all submodules and untrigger buttons
    		clearModules();
  
    	//If another module was open, clear modules and open requested module
    	} else {
    		//Remove all submodules and untrigger buttons
    		clearModules();
    		
    		//Inject submodule into main application
        	initializeModule(tictactoeBox, TIC_ID);
    		
        	//Trigger button
        	button.setStyle(ACTIVE_STYLE);
    	}
    	
    }
}
