package sudoku;

public class SudokuCell {
	//instance variables
	private char value;
	private int row;
	private int column;

	private boolean editable;
	private boolean rowAlert;		//Alert if value is a duplicate in set row
	private boolean columnAlert;	//Alert if value is a duplicate in set column
	private boolean blockAlert;		//Alert if value is a duplicate in set block
	
	
	
	
	//Constructors
	public SudokuCell() {
		this.editable = true;
		this.row = -1;
		this.column = -1;
	}
	
	public SudokuCell(int row, int column) {
		this.editable = true;
		this.row = row;
		this.column = column;
	}
	
	public SudokuCell(int row, int column, char value) {
		this.editable = (value == SudokuGame.EMPTY_CELL) ? true : false;
		this.value = value;
		this.row = row;
		this.column = column;
	}
	
	
	//Getters
	public char getValue() {return value;}
	public int getRow() {return row;}
	public int getColumn() {return column;}
	public int[] getCoords() {return new int[] {row, column};}

	public boolean isEditable() {return editable;}
	public boolean isRowAlert() {return rowAlert;}
	public boolean isColumnAlert() {return columnAlert;}
	public boolean isBlockAlert() {return blockAlert;}
	public boolean isAlerted() {
		if (value == SudokuGame.EMPTY_CELL) {
			return false;
		}
		return (rowAlert || columnAlert || blockAlert) ? true : false;
	}
	
	//Setters
	public void setValue(char value) {this.value = value;}
	public void setRow(int row) {this.row = row;}
	public void setColumn(int column) {this.column = column;}
	public void setCoords(int row, int column) {this.row = row; this.column = column;}
	public void setEditable(boolean editable) {this.editable = editable;}
	
	public void setRowAlert(boolean rowAlert) {this.rowAlert = rowAlert;}
	public void setColumnAlert(boolean columnAlert) {this.columnAlert = columnAlert;}
	public void setBlockAlert(boolean blockAlert) {this.blockAlert = blockAlert;}
	
	
	//Other
	@Override
	public String toString() {return String.valueOf(value);}
	
}
