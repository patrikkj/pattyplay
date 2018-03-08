package app;

import java.io.File;
import java.io.FileNotFoundException;

public interface Saveable {
	/**
	 * Method for saving game state to file.
	 * @throws FileNotFoundException 
	 */
	public void save(File file) throws FileNotFoundException;
	
	/**
	 * Method for loading a previous game state to current instance.
	 * @throws FileNotFoundException 
	 */
	public void load(File file) throws FileNotFoundException;
}
