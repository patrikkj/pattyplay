package app;

import java.io.IOException;

import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import sokoban.SokobanController;

public class MainController {
	//Submodules
	private static int EMPTY_PANE = -1, TIC_ID = 0, SUDOKU_ID = 1, SOKOBAN_ID = 2;
	private static String ACTIVE_STYLE = "-fx-background-color: #252525", INACTIVE_STYLE = "";
	private int activeModule = EMPTY_PANE;
	private StackPane tictactoeModule;
	private StackPane sudokuModule;
	private StackPane sokobanModule;
	
	//Controllers
	private SokobanController controller;

	//Windows
    @FXML private AnchorPane rootPane;
    @FXML private AnchorPane subRootPane;
    
    @FXML private Label label;
    
    //Main menu + buttons
    @FXML private GridPane menuGrid;
    @FXML private StackPane battleshipStackPane;
    @FXML private StackPane sudokuStackPane;
    @FXML private StackPane sokobanStackPane;
    @FXML private StackPane settingsStackPane;


    private void preloadModules() throws IOException {
    	tictactoeModule = FXMLLoader.load(getClass().getResource("../tictactoe/TicTacToe.fxml"));
    	sudokuModule = FXMLLoader.load(getClass().getResource("../sudoku/Sudoku.fxml"));
    	
    	//Sokoban - Special treatment for key listener :)
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("../sokoban/Sokoban.fxml"));
		sokobanModule = loader.load();
		
		//Need reference to run key listener method after initialization
		controller = loader.getController();
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
    	
    	//Initialize key listener for sokoban
    	if (GAME_ID == SOKOBAN_ID) controller.postInitialize();
    	
    	//Remove key listener for sokoban if loading new module
    	if (GAME_ID != SOKOBAN_ID) removeKeyListener();
    	
    	//Fit submodule constraits to parent
    	AnchorPane.setTopAnchor(subModule, 0.0);
    	AnchorPane.setBottomAnchor(subModule, 0.0);
    	AnchorPane.setLeftAnchor(subModule, 0.0);
    	AnchorPane.setRightAnchor(subModule, 0.0);
    }
    
    private void removeKeyListener() {
    	rootPane.getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {}
		});
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
    	JFXButton button = (JFXButton) event.getSource();
    	
        if (activeModule == EMPTY_PANE) {
    		//Inject submodule into main application
        	initializeModule(sokobanModule, SOKOBAN_ID);
    
        	//Trigger button
        	button.setStyle(ACTIVE_STYLE);
        	
        } else if (activeModule == SOKOBAN_ID) {
    		//Remove all submodules and untrigger buttons
    		clearModules();
  
    	//If another module was open, clear modules and open requested module
    	} else {
    		//Remove all submodules and untrigger buttons
    		clearModules();
    		
    		//Inject submodule into main application
        	initializeModule(sokobanModule, SOKOBAN_ID);
    		
        	//Trigger button
        	button.setStyle(ACTIVE_STYLE);
    	}
    	
    }

    @FXML
    void handleSudokuClick(ActionEvent event) {
    	JFXButton button = (JFXButton) event.getSource();
    	
        if (activeModule == EMPTY_PANE) {
    		//Inject submodule into main application
        	initializeModule(sudokuModule, SUDOKU_ID);
    
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
        	initializeModule(sudokuModule, SUDOKU_ID);
    		
        	//Trigger button
        	button.setStyle(ACTIVE_STYLE);
    	}
    	
    }

    @FXML
    void handleTicTacToeClick(ActionEvent event) throws IOException {    
    	JFXButton button = (JFXButton) event.getSource();
    	
        if (activeModule == EMPTY_PANE) {
    		//Inject submodule into main application
        	initializeModule(tictactoeModule, TIC_ID);
    
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
        	initializeModule(tictactoeModule, TIC_ID);
    		
        	//Trigger button
        	button.setStyle(ACTIVE_STYLE);
    	}
    	
    }
}
