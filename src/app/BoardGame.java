package app;

import java.util.ArrayList;
import java.util.Arrays;

//toConstructor() -> String ... for lagring :)

//Tile type must be specified when inheriting class
public class BoardGame<boardType> {
	// Instance variables
	private ArrayList<ArrayList<boardType>> board;

	
	// Constructors
	public BoardGame() {
		//Initializes an empty 3x3 board
		setBoard(3, 3);
	}
	
	public BoardGame(int rows, int columns) {
		setBoard(rows, columns);
	}
	
	public BoardGame(int rows, int columns, boardType defaultTile) {
		setBoard(rows, columns, defaultTile);
	}
	
	public BoardGame(Object[][] initArray) {
		setBoard(initArray);
	}


	// Validation
	private boolean isValidBoardGame(int rows, int columns) {
		// Validation of input attributes
		if (rows <= 0 || columns <= 0)
			return false;

		return true;
	}

	private boolean isValidElement(int row, int column) {
		// Indices must be within board span
		if (row >= board.size() || column >= board.get(0).size())
			return false;

		// Indices cannot be negative
		if (row < 0 || column < 0)
			return false;

		return true;
	}

	private boolean isValidRow(int row) {
		// Index must be within board span
		if (row < 0 || row >= board.size())
			return false;

		return true;
	}

	private boolean isValidColumn(int column) {
		// Index must be within board span
		if (column < 0 || column >= board.get(0).size())
			return false;

		return true;
	}

		
	// Getters
	public ArrayList<ArrayList<boardType>> getBoard() {
		return board;
	}
	
	public boardType getElement(int row, int column) throws IllegalArgumentException {
		// Validation of input attributes
		if (!isValidElement(row, column))
			throw new IllegalArgumentException("Indices must be within board span.");

		return board.get(row).get(column);
	}

	public ArrayList<boardType> getRow(int row) throws IllegalArgumentException {
		// Validation of input attributes
		if (!isValidRow(row))
			throw new IllegalArgumentException("Row index must be within board span");

		return board.get(row);
	}

	
	public ArrayList<boardType> getColumn(int column) throws IllegalArgumentException {
		// Validation of input attributes
		if (!isValidColumn(column))
			throw new IllegalArgumentException("Column index must be within board span");

		// Initialize empty ArrayList
		ArrayList<boardType> outputArrList = new ArrayList<boardType>();

		// Appends elements from specified column to output list
		for (ArrayList<boardType> arrList : board)
			outputArrList.add(arrList.get(column));

		return outputArrList;
	}
		
	public int getHeight() {
		return board.size();
	}

	public int getWidth() {
		return board.get(0).size();
	}

	
	//Setters
	public void set(int row, int column, boardType value) throws IllegalArgumentException {
		if (!isValidElement(row, column))
			throw new IllegalArgumentException("Indices must be within board span.");
		
		board.get(row).set(column, value);
	}
	
	@SuppressWarnings("unchecked")
	public void setRow(int row, ArrayList<boardType> newArrList) throws IllegalArgumentException {
		if (newArrList.size() != getWidth())
			throw new IllegalArgumentException("New row and board must have the same number of columns.");
		
		board.set(row, (ArrayList<boardType>) newArrList.clone());
	}
	
	public void setColumn(int column, ArrayList<boardType> newArrList) throws IllegalArgumentException {
		if (newArrList.size() != getHeight())
			throw new IllegalArgumentException("New column and board must have the same number of rows.");
		
		for (int i = 0; i != getHeight(); i++) {
			board.get(i).set(column, newArrList.get(i));
		}
	}

	public void setBoard() {
		//Initializes an empty 3x3 board
		setBoard(3, 3);
	}
	
	public void setBoard(int rows, int columns) {
		// Validation of input attributes
		if (!isValidBoardGame(rows, columns))
			throw new IllegalArgumentException("Number of rows and columns in board must be positive.");
		
		// Initialize empty 2D-array to build board
		Object[][] initArray = new Object[rows][columns];
		
		// Convert 2D-array to 2D ArrayList
		board = parseArrayTable(initArray);
	}
	
	public void setBoard(int rows, int columns, boardType defaultTile) {
		// Validation of input attributes
		if (!isValidBoardGame(rows, columns))
			throw new IllegalArgumentException("Number of rows and columns in board must be positive.");

		// Initialize empty 2D-array to build board
		Object[][] initArray = new Object[rows][columns];
		
		//Fills array with default object
		for (Object[] subArray : initArray)
			Arrays.fill(subArray, defaultTile);
				
		// Convert 2D-array to 2D ArrayList
		board = parseArrayTable(initArray);
	}
	
	public void setBoard(Object[][] initArray) {
		// Convert 2D-array to 2D ArrayList
		board = parseArrayTable(initArray);
	}
	
	
	// Other
	@Override
	public String toString() {
		return asTable();
	}
	
	public boolean isOccupied(int row, int column) throws IllegalArgumentException {
		// Validation of input attributes
		if (!isValidElement(row, column))
			throw new IllegalArgumentException("Indices must be within board span.");
		
		if (board.get(row).get(column) == null) return false;
		
		return true;
	}
	
	public String asTable() {
		String outputStr = "";
		for (ArrayList<boardType> currentRow : board)
			outputStr += currentRow.toString() + "\n";

		return outputStr.trim();
	}
	
	public static void main(String[] args) {
			BoardGame<String> board1 = new BoardGame<String>(2, 2, "Heiaa");
			
			String mystr = board1.getElement(1, 1);
			System.out.println(board1.asTable());			
			System.out.println(mystr);
						
			BoardGame<Double> board2 = new BoardGame<Double>(10, 12);
//			BoardGame<Double> board2 = new BoardGame<Double>(10, 12, 24.0);
			
			Double mydouble = board2.getElement(5, 6);
			System.out.println(board2.asTable());
			System.out.println(mydouble);
			
						
	}					
	
	
	// Parsers
	@SuppressWarnings("unchecked")
	public ArrayList<ArrayList<boardType>> parseArrayTable(Object[][] initArray) {
		ArrayList<ArrayList<boardType>> outputArrList = new ArrayList<ArrayList<boardType>>();

		for (Object[] array : initArray) {
			// Initialize temporary ArrayList to build matrix
			ArrayList<boardType> initArrList = new ArrayList<boardType>();
			for (Object element : array)
				initArrList.add((boardType) element);
			
//			initArrList.addAll(Arrays.asList(array));

			// Append temporary ArrayList to matrix
			outputArrList.add(initArrList);
		}

		return outputArrList;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<boardType> parseArrayList(Object[] initArray) {
		// Initialize temporary ArrayList to build output
		ArrayList<boardType> initArrList = new ArrayList<boardType>();
		for (Object element : initArray)
			initArrList.add((boardType) element);
		
		return initArrList;
	}
	
	
	
}
