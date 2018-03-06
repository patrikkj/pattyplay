package sokoban;

public class Block {
	//Coordinates
	private int x, y;			//X and Y coordinates relative to grid
	
	
	//Constructor
	public Block(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	
	//Getters
	public int getX() {return x;}
	public int getY() {return y;}
	public int[] getCoords() {return new int[] {x, y};}
	
	//Actions
	//Push block from initial cell to adjacent cell
	public void push(Direction direction) {
		x += direction.getX();
		y += direction.getY();
	}
}
