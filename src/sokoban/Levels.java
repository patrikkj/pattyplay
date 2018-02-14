package sokoban;

public class Levels {
	private static final String levelString1 = "  #####\r\n" + 
			"### . ##\r\n" + 
			"#   *  ##\r\n" + 
			"# $#* $ #\r\n" + 
			"#   * $@#\r\n" + 
			"#  #*  ##\r\n" + 
			"####. ##\r\n" + 
			"   #.##\r\n" + 
			"   ###";
	
	private static final char[][] level1 = new char[][] {
		{'W', 'W', 'W', 'W', 'W', 'W'},
		{'W', ' ', 'P', ' ', ' ', 'W'},
		{'W', 'B', '&', 'E', ' ', 'W'},
		{'W', ' ', ' ', ' ', ' ', 'W'},
		{'W', ' ', ' ', ' ', ' ', 'W'},
		{'W', 'W', 'W', 'W', 'W', 'W'}};

	private static final char[][] level2 = new char[][] {
		{'.', '.', '.', '.', '.', '.', '.', '.'},
		{'.', 'W', 'W', 'W', 'W', 'W', 'W', '.'},
		{'.', 'W', ' ', ' ', ' ', ' ', 'W', '.'},
		{'.', 'W', ' ', 'B', 'B', ' ', 'W', '.'},
		{'.', 'W', ' ', ' ', 'P', ' ', 'W', '.'},
		{'.', 'W', ' ', ' ', ' ', ' ', 'W', '.'},
		{'.', 'W', 'W', 'W', 'W', 'W', 'W', '.'},
		{'.', '.', '.', '.', '.', '.', '.', '.'}};
	
	
	public static char[][] parseStr(String level) {
		int height = 0;
		int width = 0;
		
		String[] levelArr = level.split("\r\n");
		
		//Set board height
		height = levelArr.length;
		
		//Iterate through strings to find width
		for (String str : levelArr)
			if (str.length() > width) 
				width = str.length();
		
		//Initialize output table
		char[][] output = new char[height][width];
		
		//Iterate through strings to define output table
		for (int row = 0; row < height; row++) {
			int firstWallIndex = levelArr[row].indexOf("#");
			for (int col = 0; col < width; col++) {
				if (col < levelArr[row].length()) {
					char currentChar = levelArr[row].charAt(col);
					if (currentChar == ' '  &&  col < firstWallIndex)
						output[row][col] = Cell.NONE;
					else
						output[row][col] = currentChar;
					
				}
			}
		}
		
		return output;
	}
		
	public static char[][] getLevel(int level) {
		switch (level) {
		case 3:
			return level1;
		case 2:
			return level2;
		case 1:
			return parseStr(levelString1);
		default:
			return new char[8][8];
		}
	}
}
