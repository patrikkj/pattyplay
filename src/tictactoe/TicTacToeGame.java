package tictactoe;

import java.util.ArrayList;

import app.BoardGame;

public class TicTacToeGame extends BoardGame<Character> {
	//Instance Variables
	private int player1Score, player2Score, draws;
	private char currentTurn;
	private char startingPlayer;
	private char gameStatus;
	private Character[] rawWinSeq1 = new Character[] { PLAYER_1, PLAYER_1, PLAYER_1 };
	private Character[] rawWinSeq2 = new Character[] { PLAYER_2, PLAYER_2, PLAYER_2 };
	
	
	//Status codes
	private static char PLAYER_1 = 'x', PLAYER_2 = 'o', FREE_CELL = '\0', DRAW = 'D', UNDECIDED = 'U';
	
	public String toString() {
		return String.format("%s: [gameStatus = %s, currentTurn = %s, startingPlayer = %s, player1Score = %s, player2Score = %s, draws = %s]\n%s",
				this.getClass().getSimpleName(), gameStatus, currentTurn, startingPlayer, player1Score, player2Score, draws, asTable());
	}
	
	//Contructors
	public TicTacToeGame() {
		super(3, 3, FREE_CELL);		
		startingPlayer = PLAYER_1;
		currentTurn = startingPlayer;
		gameStatus = UNDECIDED;
	}
	
	public TicTacToeGame(Character defaultTile) {
		super(3, 3, defaultTile);
		startingPlayer = PLAYER_1;
		currentTurn = startingPlayer;
		gameStatus = UNDECIDED;
	}

	public TicTacToeGame(Object[][] initArray) {
		super(initArray);
		startingPlayer = PLAYER_1;
		currentTurn = startingPlayer;
		gameStatus = UNDECIDED;
	}

	
	//Getters
	public char getStatus() {
		ArrayList<Character> winSeqX = parseArrayList(rawWinSeq1);
		ArrayList<Character> winSeqO = parseArrayList(rawWinSeq2);
		
		//Check rows
		for (int row = 0; row < getHeight(); row++) {
			if (getRow(row).equals(winSeqX)) return PLAYER_1;
			if (getRow(row).equals(winSeqO)) return PLAYER_2;
		}
		
		//Check columns
		for (int column = 0; column < getWidth(); column++) {
			if (getColumn(column).equals(winSeqX)) return PLAYER_1;
			if (getColumn(column).equals(winSeqO)) return PLAYER_2;
		}
		
		//Check diagonals
		if ((getElement(0, 0) == PLAYER_1)  &&  (getElement(1, 1) == PLAYER_1)  &&  (getElement(2, 2) == PLAYER_1)) return PLAYER_1;
		if ((getElement(0, 2) == PLAYER_1)  &&  (getElement(1, 1) == PLAYER_1)  &&  (getElement(2, 0) == PLAYER_1)) return PLAYER_1;

		if ((getElement(0, 0) == PLAYER_2)  &&  (getElement(1, 1) == PLAYER_2)  &&  (getElement(2, 2) == PLAYER_2)) return PLAYER_2;
		if ((getElement(0, 2) == PLAYER_2)  &&  (getElement(1, 1) == PLAYER_2)  &&  (getElement(2, 0) == PLAYER_2)) return PLAYER_2;
		
		if (isFull()) return DRAW;
		
		return UNDECIDED;
	}
	
	public boolean isFull() {
		//Checks for empty cells
		for (int row = 0; row != getWidth(); row++)
			for (int column = 0; column != getHeight(); column++)
				if (getElement(row, column) == FREE_CELL) return false;
		
		return true;
	}
	
	public boolean isFinished() {
		//Checks finished variable
		if (gameStatus != UNDECIDED) return true;
		
		ArrayList<Character> winSeqX = parseArrayList(rawWinSeq1);
		ArrayList<Character> winSeqO = parseArrayList(rawWinSeq2);
		
		//Check rows
		for (int row = 0; row < getHeight(); row++) {
			if (getRow(row).equals(winSeqX)  ||  getRow(row).equals(winSeqO))
				return true;
		}
		
		//Check columns
		for (int column = 0; column < getWidth(); column++)
			if (getColumn(column).equals(winSeqX)  ||  getColumn(column).equals(winSeqO))
				return true;
		
		//Check diagonals
		if ((getElement(0, 0) == PLAYER_1)  &&  (getElement(1, 1) == PLAYER_1)  &&  (getElement(2, 2) == PLAYER_1)) return true;
		if ((getElement(0, 2) == PLAYER_1)  &&  (getElement(1, 1) == PLAYER_1)  &&  (getElement(2, 0) == PLAYER_1)) return true;

		if ((getElement(0, 0) == PLAYER_2)  &&  (getElement(1, 1) == PLAYER_2)  &&  (getElement(2, 2) == PLAYER_2)) return true;
		if ((getElement(0, 2) == PLAYER_2)  &&  (getElement(1, 1) == PLAYER_2)  &&  (getElement(2, 0) == PLAYER_2)) return true;
		
		//Check if board is full
		if (isFull()) return true;
		
		return false;
	}
	
	public int getPlayer1Score() { return player1Score; }
	
	public int getPlayer2Score() { return player2Score; }
	
	public int getDraws() { return draws; }
	
	public char getCurrentTurn() { return currentTurn; }
	
	public static char getPLAYER_1() { return PLAYER_1; }
	
	public static char getPLAYER_2() { return PLAYER_2; }
	
	public static char getUNDECIDED() { return UNDECIDED; }
	
	public static char getDRAW() { return DRAW; }
	
	public char getStartingPlayer() { return startingPlayer; }
	
	public char getGameStatus() { return gameStatus; }
	
	
	//Actions
	//Moves if game is not finished
	public void move(int row, int column) {
		//Breaks if game is finished
		if (gameStatus != UNDECIDED) return;
		
		//Breaks if cell is occupied
		if (getElement(row, column) != FREE_CELL) return;
		
		//Add piece to board
		set(row, column, currentTurn);
		
		//Updates gameStatus and checks if game is finished
		gameStatus = getStatus();
		if (gameStatus != UNDECIDED)
			onGameFinish();
		
		//Change turn
		currentTurn = (currentTurn == PLAYER_1) ? PLAYER_2 : PLAYER_1;
	}
	
	//Swaps starting player
	public void swapPlayers() {
		startingPlayer = (startingPlayer == PLAYER_1) ? PLAYER_2 : PLAYER_1;
		softReset();
	}
	
	//Resets board and player turn
	public void softReset() {
		setBoard(3, 3, FREE_CELL);
		currentTurn = startingPlayer;
		gameStatus = UNDECIDED;
	}
	
	//Resets board, player turn and scores
	public void hardReset() {
		//Reset variables
		player1Score = 0; 
		player2Score = 0;
		draws = 0;
		PLAYER_1 = 'x';
		PLAYER_2 = 'o';
		startingPlayer = PLAYER_1;
		currentTurn = startingPlayer;
		gameStatus = UNDECIDED;
		
		//Reset board
		setBoard(3, 3, FREE_CELL);
		
	}
	
	//Updates game scores
	public void onGameFinish() {
		if (gameStatus == DRAW) draws++;
		else if (gameStatus == PLAYER_1) player1Score++;
		else if (gameStatus == PLAYER_2) player2Score++;
	}
	
	
	//Other
	public static void main(String[] args) {
		TicTacToeGame newGame = new TicTacToeGame();
		
		newGame.move(0, 0);
		newGame.move(1, 1);
		newGame.move(0, 1);
		newGame.move(2, 0);
		newGame.move(0, 2);
		newGame.move(2, 2);
		
		System.out.println(newGame);
		
	}
	
}
