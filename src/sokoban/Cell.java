package sokoban;


public class Cell {
	//Coordinates
	private int x, y;			//X and Y coordinates relative to grid

	//Cell types
	public static final char NONE = '_', EMPTY = ' ', PLAYER = '@', WALL = '#', BLOCK = '$', ENDPOINT = '.', BLOCK_ENDPOINT = '*';
	
	//Properties				NOTE: Remember to update isEmpty() and toString() when adding new properties
	private boolean none;		//True if null block
	private boolean wall;		//True if cell is a wall
	private boolean block;		//True if cell contains block
	private boolean endpoint;		//True if cell is a final block
	
	
	//Constructor for cells outside of map
	public Cell() {
		this.none = true;
	}
	
	//Constructor for regular cells
	public Cell(int x, int y, boolean wall, boolean block, boolean endpoint) {
		this.x = x;
		this.y = y;
		this.wall = wall;
		this.block = block;
		this.endpoint = endpoint;
	}
	
	
	//Getters
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	
	//Boolean getters
	public boolean isNone() {
		return none;
	}
	public boolean isEmpty() {
		return (isWall() || isBlock() || isNone()) ? false : true;
	}
	public boolean isWall() {
		return wall;
	}	
	public boolean isBlock() {
		return block;
	}
	public boolean isEndpoint() {
		return endpoint;
	}
	
	//Setters
	public void setWall(boolean wall) {
		this.wall = wall;
	}
	public void setBlock(boolean block) {
		this.block = block;
	}
	public void setFinish(boolean endpoint) {
		this.endpoint = endpoint;
	}
	
	
	//toString
	@Override
	public String toString() {
		char output = '\0';
		
		if (isWall()) 						output = WALL;
		else if (isBlock() && isEndpoint())	output = BLOCK_ENDPOINT;
		else if (isBlock()) 				output = BLOCK;
		else if (isEndpoint()) 				output = ENDPOINT;
		else if (isNone()) 					output = NONE;
		else if (isEmpty())					output = EMPTY;
		
		return String.valueOf(output);
		
	}

	//toChar
	public char toChar() {
		char output = '\0';
		
		if (isWall()) 						output = WALL;
		else if (isBlock() && isEndpoint())	output = BLOCK_ENDPOINT;
		else if (isBlock()) 				output = BLOCK;
		else if (isEndpoint()) 				output = ENDPOINT;
		else if (isNone()) 					output = NONE;
		else if (isEmpty())					output = EMPTY;
		
		return output;
		
	}
	
	
	
}
