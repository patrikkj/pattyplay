package sokoban;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class Game {
	// Instance vars
	private Cell[][] grid;			// Game board
	private int height, width;		// Grid dimensions
	
	// Dynamic entries
	private Player player;			// Game character
	private Set<Block> blocks;		// List of blocks
	
	// Move History
	public Stack<Move> undoStack;
	public Stack<Move> redoStack;
	private boolean isRedo;
	private Move previousRedo; 		// Used to clear redoStack
	
	// Constants
	public static final int ILLEGAL_MOVE = 0, PLAYER_MOVE = 1, PLAYER_BLOCK_MOVE = 2;
	
	
	// Constructors
	/**
	 * Constructs empty grid with specified dimensions.
	 */
	public Game(int width, int height) {
		// Initialize Collections
		blocks = new HashSet<>();
		undoStack = new Stack<>();
		redoStack = new Stack<>();
		
		// Retrive grid dimensions
		this.width = width;
		this.height = height;
		
		// Initialize empty grid
		grid = new Cell[height][width];
		
		// Fill grid with default cell
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
				grid[y][x] = new Cell();
	}
	
	/**
	 * Constructs grid from input table of characters.
	 * @param charGrid - grid of characters representing given level.
	 * @see sokoban.Levels
	 */
	public Game(char[][] charGrid) {
		// Initialize Collections
		blocks = new HashSet<>();
		undoStack = new Stack<>();
		redoStack = new Stack<>();
		
		// Retrive grid dimensions
		height = charGrid.length;
		width = charGrid[0].length;
		
		// Initialize empty grid
		grid = new Cell[height][width];
		
		// Fill grid with corresponding cell
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++) {
				
				switch (charGrid[y][x]) {
				case Cell.NONE:
					grid[y][x] = new Cell();
					break;
				case Cell.EMPTY:
					grid[y][x] = new Cell(x, y, false, false);
					break;
				case Cell.WALL:
					grid[y][x] = new Cell(x, y, true, false);
					break;
				case Cell.ENDPOINT:
					grid[y][x] = new Cell(x, y, false, true);
					break;
				case Cell.BLOCK:
					grid[y][x] = new Cell(x, y, false, false);
					blocks.add(new Block(x, y));
					break;
				case Cell.BLOCK_ENDPOINT:
					grid[y][x] = new Cell(x, y, false, true);
					blocks.add(new Block(x, y));
					break;
				case Cell.PLAYER:
					grid[y][x] = new Cell(x, y, false, false);
					player = new Player(x, y);
					break;
				case Cell.PLAYER_ENDPOINT:
					grid[y][x] = new Cell(x, y, false, true);
					player = new Player(x, y);
					break;
					
				// If no specifications, set noneCell
				default:
					grid[y][x] = new Cell();
				}
			}
	}
	
	
	// Validation
	/**
	 * Checks if game is finished.
	 * @return {@code true} if all endpoint cells are covered, else {@code false}.
	 */
	public boolean isFinished() {
		// Iterate through grid and look for uncovered endpoint tiles
		for (Cell[] cellRow : grid)
			for (Cell cell : cellRow)
				if (cell.isEndpoint()  &&  !hasBlock(cell))
					return false;
		
		return true;
	}
	
	/**
	 * Returns whether requested move is legal.
	 */
	private boolean isValidMove(Direction direction) {
		// Check indices
		int newX = player.getX() + direction.getX();
		int newY = player.getY() + direction.getY();
		if ((newX < 0)  ||  (newX >= width)) return false;
		if ((newY < 0)  ||  (newY >= height)) return false;
		
		// Check adjacent cell
		if (isFree(getAdjacent(direction)))
			return true;
		
		// If cell contains block, check if block can be pushed
		if (hasBlock(getAdjacent(direction)))
			if (isFree(getAdjacent2(direction)))
				return true;
			
		return false;
	}
	
	
	// Actions
	/**
	 * Attempts to make a move in given direction.
	 * @return {@code true} if requested move was legal, else {@code false}. 
	 */
	public int move(Direction direction) {
		// Used to mess around when finished, have fun :)
		if (isFinished()) {
			player.move(direction, 0);
			return PLAYER_MOVE;
		}
		
		// Break if move is invalid
		if (!isValidMove(direction)) 
			return ILLEGAL_MOVE;
		
		// Clear redo stack if required
		if (!redoStack.isEmpty()  &&  (direction != previousRedo.getDirection())  && !isRedo)
			redoStack.clear();
		else if (!redoStack.isEmpty() && !isRedo)
			redoStack.pop();
		
		// If adjacent cell is a block, push block and move player
		if (hasBlock(getAdjacent(direction))) {
			// Retrive block at given cell
			Block block = getBlock(getAdjacent(direction));
			
			// Push selected block
			block.push(direction);
			
			// Move player
			player.move(direction, 1);
			
			// Push move onto undo stack
			undoStack.push(direction.toMove(true));
			
			return PLAYER_BLOCK_MOVE;
		}
		
		// If adjacent cell is free, move player
		else if (isFree(getAdjacent(direction))) {
			// Move player
			player.move(direction, 1);
			
			// Push move onto undo stack
			undoStack.push(direction.toMove(false));
			
			return PLAYER_MOVE;
		}
		
		return ILLEGAL_MOVE;
	}
	
	
	// Cached moves
	/**
	 * Redo action if possible.
	 */
	public Move redo() {
		// Breaks if stack is empty
		if (redoStack.isEmpty()) return null;
		
		// Move to be executed
		Move move = redoStack.pop();
		
		// Set current move to redo move
		isRedo = true;
		
		// Perform move
		move(move.getDirection());
		
		// Unset current move
		isRedo = false;
		
		// Return move
		return move;
	}

	/**
	 * Undo action if possible.
	 */
	public Move undo() {
		// Breaks if stack is empty
		if (undoStack.isEmpty()) return null;
		
		// Move to be executed in reverse
		Move move = undoStack.pop();
		
		// Get direction of move to perform
		Direction undoDirection = move.getDirection().getInverse();
		
		// Undo push
		if (move.isPush()) 
			getBlock(move.getDirection()).push(undoDirection);
		
		// Move player and decrement move count
		player.move(undoDirection, -1);
		
		// Add original move to redo stack
		redoStack.push(move);
		
		// Update last redo operation
		previousRedo = move;
		
		// Return original move
		return move;
	}
	
	
	// Getters
	/**
	 * Returns whether a block is located on given cell.
	 */
	public boolean hasBlock(Cell cell) {
		// Check if any block satisfies given predicate
		return blocks.stream().anyMatch(block -> Arrays.equals(block.getCoords(), cell.getCoords()));
	}
	
	/**
	 * Returns whether a player is located on given cell.
	 */
	public boolean hasPlayer(Cell cell) {
		// Check if any block satisfies given predicate
		return blocks.stream().anyMatch(block -> Arrays.equals(player.getCoords(), cell.getCoords()));
	}
	
	
	/**
	 * Returns true if cell is empty and does not contain a block.
	 */
	public boolean isFree(Cell cell) {
		return (cell.isEmpty() && !hasBlock(cell));
	}
	
	/**
	 * Returns cell at (x, y).
	 */
	public Cell get(int x, int y) {
		return grid[y][x];
	}
	
	/**
	 * Returns player.
	 */
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * Returns block at given cell if any.
	 */
	public Block getBlock(Cell cell) {
		// Return block at given cell if any (filter uses lazy evaluation)
		return blocks.stream().filter(block ->  Arrays.equals(block.getCoords(), cell.getCoords())).findFirst().get();
	}
	
	/**
	 * Returns block at distance 1 in given direction relative to player coordinates.
	 */
	public Block getBlock(Direction direction) {
		// Cell at distance 1 relative to player
		Cell adjacentCell = getAdjacent(direction);
		
		// Return block at given cell if any (filter uses lazy evaluation)
		return blocks.stream().filter(block ->  Arrays.equals(block.getCoords(), adjacentCell.getCoords())).findFirst().get();
	}
	
	
	
	/**
	 * Returns a set containing all blocks in grid.
	 */
	public Set<Block> getBlocks() {
		return blocks;
	}
	
	/**
	 * Returns cell at distance 1 in given direction relative to player coordinates.
	 */
	public Cell getAdjacent(Direction direction) {
		return get(player.getX() + direction.getX(), player.getY() + direction.getY());
	}
	
	/**
	 * Returns cell at distance 2 in given direction relative to player coordinates.
	 */
	public Cell getAdjacent2(Direction direction) {
		return get(player.getX() + 2*direction.getX(), player.getY() + 2*direction.getY());
	}
	

	// Getters for board dimensions
	public int getWidth() {return width;}
	public int getHeight() {return height;}
	
	
	// Deprecated
	/**
	 * Prints grid as table.
	 */
	@Deprecated
	public void printGrid() {
		System.out.println(toString());
	}
	
	/**
	 * Returns string generated by asTable().
	 */
	@Override
	public String toString() {
		String outputStr = "";
		for (Cell[] cellArray : grid) {
			for (Cell cell : cellArray)
				outputStr += toChar(cell);
			outputStr += "\r\n";
		}
		return outputStr;
	}
	
	private char toChar(Cell cell) {
		if (hasPlayer(cell) && cell.isEndpoint()) 
			return Cell.PLAYER_ENDPOINT;
		if (hasBlock(cell) && cell.isEndpoint())		
			return Cell.BLOCK_ENDPOINT;
		if (cell.isEndpoint())
			return Cell.ENDPOINT;
		if (cell.isWall()) 	
			return Cell.WALL;
		if (cell.isNone()) 	
			return Cell.EMPTY;
		if (cell.isEmpty() && !hasPlayer(cell) && !hasBlock(cell)) 	
			return Cell.EMPTY;
		if (hasPlayer(cell)) 
			return Cell.PLAYER;
		if (hasBlock(cell)) 
			return Cell.BLOCK;
		
		return '\0';
	}
	
	// Main
	public static void main(String[] args) {
		Game game1 = new Game(Levels.getLevel(1));
		
		game1.printGrid();
	}
}


/**
 * Constants used for player / block movement.
 */
enum Direction {
	UP 		(0, -1),
	DOWN 	(0, 1),
	LEFT 	(-1, 0),
	RIGHT 	(1, 0);
	
	// Fields
	private final int x, y;
	
	// Constructor
	private Direction(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	// Returns respective X and Y coordinates
	public int getX() {return x;}
	public int getY() {return y;}
	
	// Returns the inverse Direction object
	public Direction getInverse() {
		switch (this) {
		case UP:
			return DOWN;
		case DOWN:
			return UP;
		case LEFT:
			return RIGHT;
		case RIGHT:
			return LEFT;
		}
		
		// Never gonna happen bruh
		return null;
	}
	
	// Returns corresponding Move enumeration
	public Move toMove(boolean push) {
		switch(this) {
		case UP:
			return (push) ? Move.UP_PUSH : Move.UP;
		case DOWN:
			return (push) ? Move.DOWN_PUSH : Move.DOWN;
		case LEFT:
			return (push) ? Move.LEFT_PUSH : Move.LEFT;
		case RIGHT:
			return (push) ? Move.RIGHT_PUSH : Move.RIGHT;
		default:
			return null;
		}
	}
}

enum Move {
	UP			(Direction.UP, false),
	DOWN		(Direction.DOWN, false),
	LEFT		(Direction.LEFT, false),
	RIGHT		(Direction.RIGHT, false),
	UP_PUSH 	(Direction.UP, true),
	DOWN_PUSH 	(Direction.DOWN, true),
	LEFT_PUSH 	(Direction.LEFT, true),
	RIGHT_PUSH 	(Direction.RIGHT, true);
	
	// Fields
	private final Direction direction;
	private final boolean push;
	
	// Constructor
	private Move(Direction direction, boolean push) {
		this.direction = direction;
		this.push = push;
	}
	
	// Getters
	public Direction getDirection() {return direction;}
	public boolean isPush() {return push;}
}
