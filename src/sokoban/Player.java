package sokoban;

public class Player {
	//Coordinates
	private int x, y;			//X and Y coordinates relative to grid
	
	
	//Constructor
	public Player(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	
	//Getters
	public int getX() {return x;}
	public int getY() {return y;}
	public int[] getCoords() {return new int[] {x, y};}

	
	//Actions
	//Move player from initial cell to adjacent cell
	public void move(Direction direction) {
		x += direction.getX();
		y += direction.getY();
	}
}
