package sudoku;

import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public class SudokuController {
	// Instance variables
	private SudokuGame game;
	private int difficulty;
	
	@FXML private GridPane gridPane;
	@FXML private JFXButton diffButton;

	
	//Initialize
	@FXML private void initialize() {
		game = new SudokuGame(difficulty);

		//Generate buttons in gridPane
		generateGrid();

		update();
	}

	//Update GUI
	private void update() {
		// Iterate through gridPane to update values
		for (Node node : gridPane.getChildren()) {
			if (node.getClass().getSimpleName().equals("JFXButton")) {
				// Cast to JFXButton
				JFXButton button = (JFXButton) node;

				// Locate node
				int rowIndex = GridPane.getRowIndex(button);
				int columnIndex = GridPane.getColumnIndex(button);

				// Set button value to board value
				button.setText(String.valueOf(game.getValue(rowIndex, columnIndex)));
				button.setId("sudokuTile");
				
				if (game.getElement(rowIndex, columnIndex).isAlerted())
					button.setId("sudokuTileMarked");
				
				if (!game.getElement(rowIndex, columnIndex).isEditable())
					button.setId("sudokuTilePermanent");

			}
		}
		
		//Update difficulty
		switch (difficulty % 4) {
		case 0:
			diffButton.setText("Toggle difficulty:  Easy");
			diffButton.setId("diff0");
			break;
		case 1:
			diffButton.setText("Toggle difficulty:  Medium");
			diffButton.setId("diff1");
			break;
		case 2:
			diffButton.setText("Toggle difficulty:  Hard");
			diffButton.setId("diff2");
			break;
		case 3:
			diffButton.setText("Toggle difficulty:  Extreme");
			diffButton.setId("diff3");
			break;
		}
	}
	
	
	//Generate buttons in gridPane
	private void generateGrid() {
		for (int row = 0; row != 9; row++) {
			for (int column = 0; column != 9; column++) {
				// Generate new JFXButton
				JFXButton button = new JFXButton();

				// Set properties
				button.setFocusTraversable(false);
				button.setMaxWidth(Double.POSITIVE_INFINITY);
				button.setMaxHeight(Double.POSITIVE_INFINITY);
				button.setViewOrder(1.0);

				// Set click trigger
				button.setOnMousePressed(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						if (!button.isFocused())
							button.requestFocus();
						else
							gridPane.requestFocus();
						
					}});
				
				// Set key trigger
				button.setOnKeyPressed(new EventHandler<KeyEvent>() {
					@Override
					public void handle(KeyEvent event) {
						String key = event.getCode().getName();
												
						//Get object in focus 
						int rowIndex = GridPane.getRowIndex(button.getScene().focusOwnerProperty().get());
						int columnIndex = GridPane.getColumnIndex(button.getScene().focusOwnerProperty().get());
						
						//Assert value if digit is pressed
						if (Character.isDigit(key.charAt(0)))
							game.set(rowIndex, columnIndex, key.charAt(0));
												
						//Corresponding numpad digit
						if (Character.isDigit(key.charAt(key.length() - 1)))
							game.set(rowIndex, columnIndex, key.charAt(key.length() - 1));
							
						//Update incase of digit press
						update();
							
						
						//Coordinates of new focus cell
						int requestRow = rowIndex;
						int requestColumn = columnIndex;
						
						//Check directions
						switch (key) {
						case "Up":
							if (rowIndex > 0) requestRow--;
							break;
						case "Down":
							if (rowIndex < 8) requestRow++;
							break;
						case "Left":
							if (columnIndex > 0) requestColumn--;
							break;
						case "Right":
							if (columnIndex < 8) requestColumn++;
							break;
						default:
							//Break out of method if no cases match
							return;
						}
						
						int nodeCounter = 0;
						for (Node node : gridPane.getChildren()) 
							if (!node.getClass().getSimpleName().equals("JFXButton")) nodeCounter++;
						
						
						gridPane.getChildren().get(nodeCounter + 9*requestRow + requestColumn ).requestFocus();
						return;
						}
				});

				// Set style
				button.getStylesheets().add("/resources/stylesheet.css");
				button.setId("sudokuTile");
				button.setAccessibleText(String.format("%s,%s", row, column));

				// Append button to grid
				gridPane.add(button, column, row);
			}
		}
	}

	
	//Button handlers
	@FXML void handleGenerateBoardClick(ActionEvent event) {
		game = new SudokuGame(difficulty);
		gridPane.requestFocus();
		
		update();
	}

	@FXML private void handleToggleDifficultyClick(ActionEvent event) {
		difficulty++;
		
		update();
	}

	@FXML private void handleSaveClick() {}

	@FXML private void handleLoadClick() {}
	
	

}
