package sokoban;

import app.BoardGame;

public class SokobanGame extends BoardGame<SokobanCell> {

	public SokobanGame() {
		super();
	}

	public SokobanGame(int rows, int columns, SokobanCell defaultTile) {
		super(rows, columns, defaultTile);
	}

	public SokobanGame(int rows, int columns) {
		super(rows, columns);
	}

	public SokobanGame(Object[][] initArray) {
		super(initArray);
	}

}
