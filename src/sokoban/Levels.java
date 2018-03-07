package sokoban;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Levels {
	private static List<String> levelList = new ArrayList<String>();
	private static int initMoveCount;
	private static int currentLevel;
	private static int numOfLevels = 200;
	
	// Load levels upon initialization
	static {
		try {
			loadLevels(Levels.class.getResource("../resources/Alberto_Garcia_Arranged.txt").getPath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	//Preloads 'numOfLevels' levels
	private static void loadLevels(String filepath) throws FileNotFoundException {
		//Local variables
		Scanner input = new Scanner(new FileReader(filepath));
		StringBuilder stringBuilder = new StringBuilder();
		
		//Read file to StringBuilder object
		while(input.hasNext())
			stringBuilder.append(input.nextLine() + System.lineSeparator());
		input.close();
		
		//Parse StringBuilder object to String
		String fileString = stringBuilder.toString();
		
		//Format level strings and append to levelList
		String[] levelArr = fileString.split(System.lineSeparator() + System.lineSeparator());
		
		for (int i = 0; i < numOfLevels; i++) {
			String levelString = levelArr[i].substring(0, levelArr[i].lastIndexOf(";") - 1);
			levelList.add(levelString);
		}
	}
	
	//Returns level count
	public static int getNumOfLevels() {
		return numOfLevels;
	}
	
	//Custom level getters
	public static int getInitMoveCount() {
		return initMoveCount;
	}
	public static int getLevel() {
		return currentLevel;
	}
	
	//Parse from level String to table of characters, to be used by game constructor
	public static char[][] parseStr(String level) {
		int height = 0;
		int width = 0;
		
		//Remove trailing whitespace and convert to array of strings
		String[] levelArr = level.replaceAll("\\s++$", "").split(System.lineSeparator());
		
		//Set board height
		height = levelArr.length;
		
		//Iterate through strings to set board width
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
	
	
	//Saves game state to specified file
	public static void saveLevel(File file, Game game) throws FileNotFoundException {
		// Create printstream
    	PrintStream printStream = new PrintStream(new FileOutputStream(file));
    	
    	// Print game to file
    	printStream.print(String.format("%s%n;Level:%s;Moves:%s", 
    			game, game.getLevel(), game.getMoveCount()));
    	
    	// Close stream
    	printStream.flush();
    	printStream.close();
	}
	
	//Parse from custom level file to table of characters, updates static fields
	public static char[][] loadLevel(File file) throws FileNotFoundException {
		StringBuilder levelStringBuilder = new StringBuilder();
		
		// Scan file
    	Scanner fileScanner = new Scanner(file);
    	while (fileScanner.hasNextLine())
    		levelStringBuilder.append(fileScanner.nextLine() + System.lineSeparator());
    	fileScanner.close();
    	
    	// Retrive level string
    	String rawString = levelStringBuilder.toString();
    	
    	// Separate level data 
    	String[] rawArray = rawString.split(";");
    	String levelString = rawArray[0];
    	String levelCount = rawArray[1].substring(rawArray[1].indexOf(':') + 1).trim();
    	String moveCount = rawArray[2].substring(rawArray[2].indexOf(':') + 1).trim();
    	
    	//Update static fields
    	currentLevel = Integer.valueOf(levelCount);
    	initMoveCount = Integer.valueOf(moveCount);
    	
    	// Return character array 
    	return parseStr(levelString);
	}
	
	//Returns requested level
	public static char[][] loadLevel(int level) {
		currentLevel = level;
		initMoveCount = 0;
		return parseStr(levelList.get(level - 1));
	}
}
