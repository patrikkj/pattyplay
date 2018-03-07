package sokoban;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Levels {
	private static List<String> levelList = new ArrayList<String>();
	private static int numOfLevels = 200;
	private static boolean isLoaded;
	
	private static final String levelString1 = "  #####\r\n" + 
			"### . ##\r\n" + 
			"#   *  ##\r\n" + 
			"# $#* $ #\r\n" + 
			"#   * $+#\r\n" + 
			"#  #*  ##\r\n" + 
			"####. ##\r\n" + 
			"   #.##\r\n" + 
			"   ###";
	
	//Returns requested level
	public static char[][] getLevel(int level) {
		//Load levels
		if (!isLoaded) {
			try {
				loadLevels("C:\\Users\\Patrik\\git\\Patrik-Forked\\PattyPlay\\src\\resources\\Alberto_Garcia_Arranged.txt");
				isLoaded = true;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		//Return requested level
		return parseStr(levelList.get(level - 1));
	}
	
	
	//Preloads 'numOfLevels' levels
	private static void loadLevels(String filepath) throws FileNotFoundException {
		//Local variables
		Scanner input = new Scanner(new FileReader(filepath));
		StringBuilder stringBuilder = new StringBuilder();
		
		//Read file to StringBuilder object
		while(input.hasNext())
			stringBuilder.append(input.nextLine() + "\n");
		input.close();
		
		//Parse StringBuilder object to String
		String fileString = stringBuilder.toString();
		
		//Format level strings and append to levelList
		String[] levelArr = fileString.split("\n\n");
		for (int i = 0; i < numOfLevels; i++) {
			String levelString = levelArr[i].substring(0, levelArr[i].lastIndexOf(";") - 1);
			levelList.add(levelString);
		}
	}
	
	
	//Parse from level String to table of characters, to be used by game constructor
	public static char[][] parseStr(String level) {
		int height = 0;
		int width = 0;
		
		String[] levelArr = level.replaceAll("\\s++$", "").split("\n");
		
		Arrays.stream(levelArr).map(str -> str.replaceAll("\\s++$", ""));
		
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
			int lastWallIndex = levelArr[row].lastIndexOf("#");
			for (int col = 0; col < width; col++) {
				if (col < levelArr[row].length()) {
					char currentChar = levelArr[row].charAt(col);
					if (currentChar == ' '  &&  ((col < firstWallIndex)  || (col > lastWallIndex)))
						output[row][col] = Cell.NONE;
					else
						output[row][col] = currentChar;
					
				}
			}
		}
		
		return output;
	}
		
	
}
