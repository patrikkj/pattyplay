package sokoban;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import app.Saveable;

public class Game implements Saveable {
	// Instance vars
	private Cell[][] grid;			// Game board
	private int height, width;		// Grid dimensions
	private int level;				// Current level
	private int moveCount;			// Number of moves

	// Dynamic entries
	private Player player;			// Game character
	private Set<Block> blocks;		// List of blocks
	
	// Move History
	public Stack<Move> undoStack;
	public Stack<Move> redoStack;	
	
	
	// Constructor
	/**
	 * Default constructor, loads level 1.
	 * @see sokoban.Levels
	 */
	public Game() {
		char[][] charGrid = Levels.loadLevel(1);
		
		//Set level data
		level = 1;
		moveCount = 0;
		
		initializeCollections();
		initializeGrid(charGrid);
	}
	
	/**
	 * Constructs grid from input table of characters.
	 * @param charGrid - grid of characters representing given level.
	 * @see sokoban.Levels
	 */
	public Game(char[][] charGrid) {
		initializeCollections();
		initializeGrid(charGrid);
	}
	
	/**
	 * Initializes collections used to keep track of game state.
	 */
	private void initializeCollections() {
		// Initialize Collections
		blocks = new HashSet<>();
		undoStack = new Stack<>();
		redoStack = new Stack<>();
	}
	
	/**
	 * Initializes cell grid based on input character table.
	 */
	private void initializeGrid(char[][] charGrid) {
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
	 * @return {@code Move} enumeration representing the move performed. 
	 */
	public Move move(Direction direction) {
		// Used to mess around when finished, have fun :)
		if (isFinished()) {
			// Move player
			player.move(direction);
			
			return direction.toMove(false);
		}
		
		// Break if move is invalid
		if (!isValidMove(direction)) 
			return Move.ILLEGAL;
		
		// Pop if move performed is equal to the top move in redo stack
		if (!redoStack.isEmpty()  &&  (direction == redoStack.peek().getDirection()))
			redoStack.pop();
		else  // If move pattern breaks with redo stack move pattern, clear history
			redoStack.clear();
		
		// Target cell
		Cell targetCell = getAdjacent(direction);
		
		// Case: Targeted cell contains a block
		if (hasBlock(targetCell)) {
			// Retrive block at given cell
			Block block = getBlock(targetCell);
			
			// Move block
			block.push(direction);
			
			// Move player and increment move count
			player.move(direction);
			moveCount++;
			
			// Push move onto undo stack
			undoStack.push(direction.toMove(true));
			
			// Return executed move
			return direction.toMove(true);
		}
		
		// Case: Targeted cell is free
		else if (isFree(targetCell)) {
			// Move player and increment move count
			player.move(direction);
			moveCount++;
			
			// Push move onto undo stack
			undoStack.push(direction.toMove(false));
			
			// Return executed move
			return direction.toMove(false);
		}
		
		return Move.ILLEGAL;
	}

	/**
	 * Undo action if possible.
	 */
	public Move undo() {
		// Breaks if stack is empty
		if (undoStack.isEmpty()) return Move.ILLEGAL;
		
		// Move to be executed in reverse
		Move move = undoStack.pop();
		
		// Get direction of movement
		Direction direction = move.getDirection().getInverse();
		
		// Move block 
		if (move.isPush()) 
			getBlock(move.getDirection()).push(direction);
		
		// Move player and decrement move count
		player.move(direction);
		moveCount--;
		
		// Add move to redo stack
		redoStack.push(move);
		
		// Return original move
		return move;
	}
	
	/**
	 * Redo action if possible.
	 */
	public Move redo() {
		// Breaks if stack is empty
		if (redoStack.isEmpty()) return Move.ILLEGAL;
		
		// Move to be executed
		Move move = redoStack.pop();
		
		// Get direction of movement
		Direction direction = move.getDirection();
		
		// Move block
		if (move.isPush())
			getBlock(direction).push(direction);
			
		// Move player and increment move count
		player.move(direction);
		moveCount++;
		
		// Add move to undo stack
		undoStack.push(move);

		// Return move
		return move;
	}
	
	
	// Getters
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
	
	// Boolean getters
	/**
	 * Returns true if cell is empty and does not contain a block.
	 */
	public boolean isFree(Cell cell) {
		return (cell.isEmpty() && !hasBlock(cell));
	}
	
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
	
	// Getters for game details
	public int getWidth() {return width;}
	public int getHeight() {return height;}
	public int getLevel() {return level;}
	public int getMoveCount() {return moveCount;}
	
	
	// Other
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
	
	/**
	 * Converts cell to character using standard Sokoban formatting.
	 */
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

	
	// Save - Load
	/**
	 * Loads level given by {@code level} from predefined list.
	 */
	public void load(int level) {
		// Parse level to grid of characters, this also updates Levels' static fields
		char[][] charGrid = Levels.loadLevel(level);
		
		// Set game details
		this.level = Levels.getLevel();
		moveCount = Levels.getInitMoveCount();
		
		// Rerun initializers
		initializeCollections();
		initializeGrid(charGrid);
	}

	/**
	 * Loads level represented by {@code file}. Support standard Sokoban level format.
	 */
	public void load(File file) throws FileNotFoundException {
		// Parse level to grid of characters, this also updates Levels' static fields
		char[][] charGrid = Levels.loadLevel(file);
		
		// Set game details
		level = Levels.getLevel();
		moveCount = Levels.getInitMoveCount();
		
		// Rerun initializers
		initializeCollections();
		initializeGrid(charGrid);
	}

	/**
	 * Saves current level to given file using Sokobans' standard level format.
	 */
	public void save(File file) throws FileNotFoundException {
		Levels.saveLevel(file, this);
	}
}




///////////// DIRECTION ////////////////

/**
 * Constants for describing directions.
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
	
	// Convert direction to Move enumeration
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




///////////// MOVE ////////////////

/**
 * Constants used for player / block movement.
 */
enum Move {
	//Player 
	UP			(Direction.UP, false),
	DOWN		(Direction.DOWN, false),
	LEFT		(Direction.LEFT, false),
	RIGHT		(Direction.RIGHT, false),
	
	// Player and block
	UP_PUSH 	(Direction.UP, true),
	DOWN_PUSH 	(Direction.DOWN, true),
	LEFT_PUSH 	(Direction.LEFT, true),
	RIGHT_PUSH 	(Direction.RIGHT, true),
	
	// Illegal 
	ILLEGAL		(null, false);
	
	
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
