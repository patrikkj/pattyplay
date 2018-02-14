package battleship;

import app.BoardGame;

public class BattleshipGame extends BoardGame<BattleshipCell> {

	public BattleshipGame() {
		super();
	}

	public BattleshipGame(int rows, int columns, BattleshipCell defaultTile) {
		super(rows, columns, defaultTile);
	}

	public BattleshipGame(int rows, int columns) {
		super(rows, columns);
	}

	public BattleshipGame(Object[][] initArray) {
		super(initArray);
	}

}
