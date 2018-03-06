package sokoban;


public class Cell {
	//Coordinates
	private int x, y;			//X and Y coordinates relative to grid

	//Cell types (Used for board generation only)
	public static final char NONE = '_', EMPTY = ' ', WALL = '#', ENDPOINT = '.', BLOCK = '$', BLOCK_ENDPOINT = '*', PLAYER = '@', PLAYER_ENDPOINT = '+';
	
	//Properties				NOTE: Remember to update isEmpty() and toString() when adding new properties
	private boolean none;		//True if null block
	private boolean wall;		//True if cell is a wall
	private boolean endpoint;	//True if cell is a final block
	
	
	//Constructor for cells outside of map
	public Cell() {
		this.none = true;
	}
	
	//Constructor for regular cells
	public Cell(int x, int y, boolean wall, boolean endpoint) {
		this.x = x;
		this.y = y;
		this.wall = wall;
		this.endpoint = endpoint;
	}
	
	
	//Getters
	public int getX() {return x;}
	public int getY() {return y;}
	public int[] getCoords() {return new int[] {x, y};}
	
	//Boolean getters
	public boolean isNone() {
		return none;
	}
	public boolean isEmpty() {
		return (isWall() || isNone()) ? false : true;
	}
	public boolean isWall() {
		return wall;
	}	
	public boolean isEndpoint() {
		return endpoint;
	}
	
	//Setters
	public void setWall(boolean wall) {
		this.wall = wall;
	}

	public void setFinish(boolean endpoint) {
		this.endpoint = endpoint;
	}
	
	
	//toString
	@Override
	public String toString() {
		char output = '\0';
		
		if (isWall()) 						output = WALL;
		else if (isEndpoint()) 				output = ENDPOINT;
		else if (isNone()) 					output = NONE;
		else if (isEmpty())					output = EMPTY;
		
		return String.valueOf(output);
		
	}

	//toChar
	public char toChar() {
		char output = '\0';
		
		if (isWall()) 						output = WALL;
		else if (isEndpoint()) 				output = ENDPOINT;
		else if (isNone()) 					output = NONE;
		else if (isEmpty())					output = EMPTY;
		
		return output;
		
	}
	
	
	
}
