package tictactoe;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRippler;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Bloom;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;

public class TicTacToeController {
	//Instance Variables
	private TicTacToeGame game;
	private Paint tileRipplerFill = Paint.valueOf("#9E9E9E");
	
	@FXML private Label currentTurnLabel;
	@FXML private Label scoreLabel1;
	@FXML private Label scoreLabel2;
	@FXML private Label drawLabel;
	@FXML private Label playerCharLabel1;
	@FXML private Label playerCharLabel2;
	@FXML private Label startingLabel;
	@FXML private JFXButton resetBoardButton;
	@FXML private JFXButton swapButton;
	@FXML private Button mybutton;
	@FXML private GridPane gridPane;
	
	//Initialization
	@FXML private void initialize() {
		//Initialize game
		game = new TicTacToeGame();
		
		//Initialize tile ripple effect
		initTileRipple();
		
		update();
	}
	
	@FXML private void update() {
		//Label updates
		updateCurrentTurnLabel();  //Update label and "Reset Board" button
		scoreLabel1.setText(String.valueOf(game.getPlayer1Score()));
		scoreLabel2.setText(String.valueOf(game.getPlayer2Score()));
		drawLabel.setText(String.valueOf(game.getDraws()));
		playerCharLabel1.setText(String.valueOf(TicTacToeGame.getPLAYER_1()).toUpperCase());
		playerCharLabel2.setText(String.valueOf(TicTacToeGame.getPLAYER_2()).toUpperCase());
		startingLabel.setText(String.valueOf(game.getStartingPlayer()).toUpperCase());
		
		//Iterate through gridPane submodules for grid label update
		for (Node stackNode : gridPane.getChildren()) {
			//Locate stackPanes
			if (stackNode.getTypeSelector().equals("StackPane")) {
				//Nasty digging to find label within hirarchy
				StackPane stackPane = (StackPane) stackNode;
				JFXRippler rippler = (JFXRippler) stackPane.getChildren().get(0);
				Label tileLabel = (Label) rippler.getChildren().get(0);
				
				//Retrive board value
				int[] tileCoords = gridCoordinates(tileLabel);
				char tileValue = game.getElement(tileCoords[0], tileCoords[1]);

				//Set GUI value 
				tileLabel.setText(String.valueOf(tileValue).toUpperCase());
			}
		}
	}
	
	private void initTileRipple() {
		//Iterate through gridPane submodules
		for (Node stackNode : gridPane.getChildren()) {
			
			//Locate stackPanes
			if (stackNode.getTypeSelector().equals("StackPane")) {
				
				//Casting to instance class
				StackPane stackPane = (StackPane) stackNode;
				Label tileLabel = (Label) stackPane.getChildren().get(0);
				
				//Append ripple effect to parent
				JFXRippler tileRippler = new JFXRippler(tileLabel);
				tileRippler.setRipplerFill(tileRipplerFill);
				stackPane.getChildren().add(tileRippler);
			}
		}
	}
	
	private int[] gridCoordinates(Label tileLabel) {
		//Retrive coordinates as text "row_column"
		String[] tileCoords = tileLabel.getAccessibleText().split("_");
		
		//Retrive row and column from 
		int rowIndex = Integer.valueOf(tileCoords[0]);
		int columnIndex = Integer.valueOf(tileCoords[1]);
		
		return new int[] { rowIndex, columnIndex }; 
		
	}
	
	private void updateCurrentTurnLabel() {
		
		if (game.getGameStatus() == TicTacToeGame.getUNDECIDED()) {
			resetBoardButton.setEffect(swapButton.getEffect());
			
			if (game.getCurrentTurn() == TicTacToeGame.getPLAYER_1())
				currentTurnLabel.setText("Player 1's turn");
			else 
				currentTurnLabel.setText("Player 2's turn");
		} else {
			resetBoardButton.setEffect(new Bloom(0));
			if (game.getGameStatus() == TicTacToeGame.getPLAYER_1())
				currentTurnLabel.setText("Player 1 Won!");
			else if (game.getGameStatus() == TicTacToeGame.getPLAYER_2())
				currentTurnLabel.setText("Player 2 Won!");
			else if (game.getGameStatus() == TicTacToeGame.getDRAW())
				currentTurnLabel.setText("Draw!");
		}
	}
	
	
	//Menu buttons
	@FXML private void handleNewGameClick() {
		game.hardReset();
		update();
	}
	
	@FXML private void handleSaveClick() {
		System.out.println("Saving.. :)");
	}
	
	@FXML private void handleLoadClick() {
		System.out.println("Loading.. :)");
	}
	
	
	//In-window buttons
	@FXML private void handleGridClick(MouseEvent event) {
		//Label Accessible Text format: "row_column"
		Label tileLabel = (Label) event.getSource();
		
		int[] coordArr = gridCoordinates(tileLabel);
		
		//Update game state
		game.move(coordArr[0], coordArr[1]);
		
		//Update GUI
		update();
	}
	
	@FXML private void handleResetBoardClick() {
		game.softReset();
		update();
	}
	
	@FXML private void handleSwapClick() {
		game.swapPlayers();
		update();
	}
	
	
	
}
