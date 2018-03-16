package sudoku;

import java.util.ArrayList;
import java.util.List;

import app.BoardGame;

public class SudokuGame extends BoardGame<SudokuCell> {
	protected static char EMPTY_CELL = ' ';
	
	//Constructors
	public SudokuGame() {
		super(9, 9);
		
		for (int row = 0; row != getHeight(); row++)
			for (int column = 0; column != getWidth(); column++)
				set(row, column, new SudokuCell(row, column, EMPTY_CELL));
	}
	
	public SudokuGame(int difficulty) {
		super(9, 9);
		int[] diffArray = new int[] { 40, 32, 24, 17 }; 
		
		int[][] sudokuGrid = new int[9][9];

		Generator generator = new Generator();
		
		Grid grid = generator.generate(81 - diffArray[difficulty % 4]);
		
		for (int row = 0; row < 9; row++)
			for (int col = 0; col < 9; col++)
				sudokuGrid[row][col] = grid.getCell(row, col).getValue(); 
		
		setBoard(sudokuGrid);
	}
	
	
	
	//Gets char value of SudokuCell at specified index
	public char getValue(int row, int column) {
		return getElement(row, column).getValue();
	}

	
	//Assigns value to cell if permitted and update alerts
	public void set(int row, int column, char value) {
		//Breaks if cell is predefined
		if (!getElement(row, column).isEditable()) return;
		
		//If character is 0, clear cell
		if (value == '0'  ||  value == EMPTY_CELL) getElement(row, column).setValue(EMPTY_CELL);
		
		//Assigns value to cell
		else getElement(row, column).setValue(value);
		
		//Update alerts
		validateEntry(row, column);
	}
	
	public void setBoard(int[][] initTable) {
		//Set board based on integer array
		for (int row = 0; row != getHeight(); row++)
			for (int column = 0; column != getWidth(); column++) {
				if (initTable[row][column] == 0)
					set(row, column, new SudokuCell(row, column, EMPTY_CELL));
				else
					set(row, column, new SudokuCell(row, column, Character.forDigit(initTable[row][column], 10)));
			}
				
	}
	
	public void setBoard(char[][] initTable) {
		//Set board based on integer array
		for (int row = 0; row != getHeight(); row++)
			for (int column = 0; column != getWidth(); column++)
				set(row, column, new SudokuCell(row, column, initTable[row][column]));
	}
	
	
	//Validates row, column and update cell alerts
	public void validateEntry(int row, int column) {
		validateRow(row);
		validateColumn(column);
		validateBlock(row, column);
	}
	
	//Validates row and update cell alert
	public void validateRow(int row) {
		//Retrive corresponding Array
		SudokuCell[] rowArray = getRowArray(row);
		
		//Retrive corresponding ArrayList 
		List<Character> compareList = new ArrayList<Character>();
		
		//Append character values to compareList
		for (SudokuCell cell : rowArray) {
			compareList.add(cell.getValue());
			cell.setRowAlert(false);
		}

		for (SudokuCell cell : rowArray) {
			char currentChar = cell.getValue();
			int firstIndex = compareList.indexOf(currentChar);
			int lastIndex = compareList.lastIndexOf(currentChar);
			
			if ((firstIndex != lastIndex) &&  currentChar != EMPTY_CELL)
				cell.setRowAlert(true);
		}
	}
	
	//Validates column and update cell alert
	public void validateColumn(int column) {
		//Retrive corresponding Array
		SudokuCell[] columnArray = getColumnArray(column);
		
		//Retrive corresponding ArrayList 
		List<Character> compareList = new ArrayList<Character>();
		
		//Append character values to compareList
		for (SudokuCell cell : columnArray) {
			compareList.add(cell.getValue());
			cell.setColumnAlert(false);
		}
		
		for (SudokuCell cell : columnArray) {
			char currentChar = cell.getValue();
			int firstIndex = compareList.indexOf(currentChar);
			int lastIndex = compareList.lastIndexOf(currentChar);
			
			if ((firstIndex != lastIndex)  &&  currentChar != EMPTY_CELL)
				cell.setColumnAlert(true);
		}
	}
	
	//Validates block and update cell alert
	public void validateBlock(int row, int column) {
		//Retrive block
		int rowBlock = Math.floorDiv(row, 3);
		int columnBlock = Math.floorDiv(column, 3);
		
		//Temporary list for comparison
		List<Character> compareList = new ArrayList<Character>();
		
		//Append character values to compareList and array
		for (int row_i = (rowBlock*3); row_i < (rowBlock*3 + 3); row_i++)
			for (int col_i = (columnBlock*3); col_i < (columnBlock*3 + 3); col_i++) 
				compareList.add(getElement(row_i, col_i).getValue());

		//Iterate through cells to check for duplicate
		for (int row_i = (rowBlock*3); row_i < (rowBlock*3 + 3); row_i++)
			for (int col_i = (columnBlock*3); col_i < (columnBlock*3 + 3); col_i++) {
				SudokuCell cell = getElement(row_i, col_i);
				cell.setBlockAlert(false);
				
				char currentChar = cell.getValue();
				int firstIndex = compareList.indexOf(currentChar);
				int lastIndex = compareList.lastIndexOf(currentChar);
				
				if ((firstIndex != lastIndex)  &&  currentChar != EMPTY_CELL)
					cell.setBlockAlert(true);
			}
	}
	
	//Validates board and update cell alerts
	public void validateBoard() {
		//Row validation
		for (int row = 0; row < getHeight(); row++)
			validateRow(row);

		//Column validation
		for (int column = 0; column < getWidth(); column++) 
			validateColumn(column);

	}

	
	
	//Overrides
	public SudokuCell[] getRowArray(int row) throws IllegalArgumentException {
		ArrayList<SudokuCell> tempArrList = getRow(row);
		
		SudokuCell[] sudokuArr = new SudokuCell[getHeight()];
		
		for (int i = 0; i < getHeight(); i++)
			sudokuArr[i] = tempArrList.get(i);
		
		return sudokuArr;
	}
	
	public SudokuCell[] getColumnArray(int column) throws IllegalArgumentException {
		ArrayList<SudokuCell> tempArrList = getColumn(column);
		
		SudokuCell[] sudokuArr = new SudokuCell[getWidth()];
		
		for (int i = 0; i < getWidth(); i++)
			sudokuArr[i] = tempArrList.get(i);
		
		return sudokuArr;
	}
	
	
	
	//Other
	public static void main(String[] args) {
		SudokuGame game = new SudokuGame(0);
		System.out.println(game.asTable());
	}
	
	
	
	
	
}
