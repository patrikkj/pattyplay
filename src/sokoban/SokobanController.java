package sokoban;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Scanner;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;

public class SokobanController {
	//Instance variables
	private Game game;
	private Scene scene;
	private GridPane gridPane;
	private boolean customLevel;
	private double duration;
	private double tileWidth;
	private double tileHeight;
	private double tileSize;
	private int tilesX;
	private int tilesY;
	private int level;
	private StackPane charPane;
	private ImageView charImageView;
	
	//Block container
	private HashMap<Block, StackPane> blockMap;
	
	//FXML References
	@FXML StackPane rootPane;
	@FXML AnchorPane subRootPane;
	@FXML Label levelLabel;
	@FXML Label movesLabel;
	
	
	//Initialize Game
	public void initialize() {
		//Set default level
		level = 1;
		
		//Initialize GUI
		initializeGrid();
		
		//Initialize character
		renderCharacter();
		
		//Initialize block map
		blockMap = new HashMap<>();
		
		//Initialize blocks
		renderBlocks();
		
		//Set labels
		levelLabel.setText(String.format("Level: %s", level));
		movesLabel.setText(String.format("Moves: %s", game.getPlayer().getMoveCount()));
	}
	
	//Initialize GUI
	private void initializeGrid() {
		//Load specified level
		if (!customLevel)
			game = new Game(Levels.getLevel(level));
		
		//Create Grid Pane to encapsulate tiles
		gridPane = new GridPane();
		gridPane.setAlignment(Pos.CENTER);
		
		//Board dimensions
		tilesX = game.getWidth();
		tilesY = game.getHeight();
		
		//Calculate tile dimensions
		tileWidth = subRootPane.getMinWidth() / tilesX;
		tileHeight = subRootPane.getMinHeight() / tilesY;
		tileSize = Math.round(Math.min(tileWidth, tileHeight));
		
		//Explicitly fix rounding errors :(
		switch ((int) tileSize) {
		case 21:
			tileSize--;
			break;
		case 22:
			tileSize++;
			break;
		case 25:
			tileSize--;
			break;
		case 29:
			tileSize--;
			break;
		case 33:
			tileSize--;
			break;
		case 50:
			tileSize++;
			break;
		}
		
		//Generate tiles
		for (int y = 0; y < tilesY; y++)
			for (int x = 0; x < tilesX; x++) {
				//Initialize new tile
				StackPane tile = new StackPane();
					
				//Set identifier (scene.lookup("#id"))
				tile.setId(String.format("Tile_%s_%s", x, y));

				//Apply size specification
				tile.setPrefSize(tileSize, tileSize);
				
				//Add image to imageView
				updateImage(x, y, tile);

				//Append tile to gridPane
				gridPane.add(tile, x, y);
			}

		//Insert Grid Pane into subRootPane
		subRootPane.getChildren().setAll(gridPane);
		AnchorPane.setBottomAnchor(gridPane, 0d);
		AnchorPane.setTopAnchor(gridPane, 0d);
		AnchorPane.setLeftAnchor(gridPane, 0d);
		AnchorPane.setRightAnchor(gridPane, 0d);
	}
	
	//Set key listener (Note: Do not call prior to scene loading)
	public void postInitialize() {
		//Create reference to current scene
		scene = rootPane.getScene();
		
		//Set key listener
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				//Local variables
				Move move = null;
				String key = event.getCode().getName();
				System.out.println(key);
				//Check directions
				switch (key) {
				case "Up":
					switch (game.move(Direction.UP)) {
					case Game.PLAYER_MOVE:
						moveCharacter(Direction.UP);
						break;
					case Game.PLAYER_BLOCK_MOVE:
						moveCharacter(Direction.UP);
						moveBlock(game.getBlock(Direction.UP));
						break;}
					break;
				case "Down":
					switch (game.move(Direction.DOWN)) {
					case Game.PLAYER_MOVE:
						moveCharacter(Direction.DOWN);
						break;
					case Game.PLAYER_BLOCK_MOVE:
						moveCharacter(Direction.DOWN);
						moveBlock(game.getBlock(Direction.DOWN));
						break;}
					break;
				case "Left":
					switch (game.move(Direction.LEFT)) {
					case Game.PLAYER_MOVE:
						moveCharacter(Direction.LEFT);
						break;
					case Game.PLAYER_BLOCK_MOVE:
						moveCharacter(Direction.LEFT);
						moveBlock(game.getBlock(Direction.LEFT));
						break;}
					break;
				case "Right":
					switch (game.move(Direction.RIGHT)) {
					case Game.PLAYER_MOVE:
						moveCharacter(Direction.RIGHT);
						break;
					case Game.PLAYER_BLOCK_MOVE:
						moveCharacter(Direction.RIGHT);
						moveBlock(game.getBlock(Direction.RIGHT));
						break;}
					break;
				case "X":
					move = game.redo();
					
					// Break if stack is empty
					if (move == null) break;
					
					// Move block if pushed
					if (move.isPush())
						moveBlock(game.getBlock(move.getDirection()));
					
					// Move player
					moveCharacter(move.getDirection());
					break;
				case "Z":
					// Original move
					move = game.undo();
					
					// Break if stack is empty
					if (move == null) break;
					
					// Get direction of move to perform
					Direction undoDirection = move.getDirection().getInverse();
					
					// Move block if pushed
					if (move.isPush())
						moveBlock(game.getBlock(move.getDirection()));
					
					// Move player
					moveCharacter(undoDirection);

					break;
				case "R":
					System.out.println("Reset");
					resetLevel();
				}
				
				//Update GUI after move
				update();				
		}});
	}
	
	//Update GUI
	private void update() {
		//Update labels
		levelLabel.setText(String.format("Level: %s", level));
		movesLabel.setText(String.format("Moves: %s", game.getPlayer().getMoveCount()));
		
		int[] playerCoords = game.getPlayer().getCoords();
		
		int minX = Math.max(playerCoords[0] - 2, 0);
		int maxX = Math.min(playerCoords[0] + 2, tilesX);
		int minY = Math.max(playerCoords[1] - 2, 0);
		int maxY = Math.min(playerCoords[1] + 2, tilesY);
		
		//Iterate through grid to update images
		for (int y = minY; y < maxY; y++) {
			for (int x = minX; x < maxX; x++) {
				//Reference to ImageView at tile (x, y)
				StackPane stackPane = (StackPane) scene.lookup(String.format("#Tile_%s_%s", x, y));
				
				//Update Image
				updateImage(x, y, stackPane);
			}
		}
	}
	
	//Set image at specified tile
	private void updateImage(int x, int y, StackPane stackPane) {
		//Tile
		Cell tile = game.get(x, y);
		
		//Corresponding StackPane
		ImageView baseImageView = new ImageView(new Image(getClass().getResourceAsStream("../icons/sokoban/Ground_Concrete.png")));
		baseImageView.setFitHeight(tileSize);
		baseImageView.setFitWidth(tileSize);

		stackPane.getChildren().setAll(baseImageView);
		
		ImageView imageView = new ImageView();

		//Set image based on grid values
		switch (tile.toChar()) {
		case Cell.WALL:
			imageView.setImage(new Image(getClass().getResourceAsStream("../icons/sokoban/Wall_Black.png")));
			imageView.setFitHeight(tileSize);
			imageView.setFitWidth(tileSize);
			break;
		case Cell.BLOCK_ENDPOINT:
			imageView.setImage(new Image(getClass().getResourceAsStream("../icons/sokoban/CrateDark_Blue.png")));
			imageView.setFitHeight(tileSize);
			imageView.setFitWidth(tileSize);
			break;
		case Cell.ENDPOINT:	
			imageView.setImage(new Image(getClass().getResourceAsStream("../icons/sokoban/EndPoint_Blue.png")));
			imageView.setFitHeight(tileSize/3);
			imageView.setFitWidth(tileSize/3);
			break;
		case Cell.EMPTY:
			stackPane.getChildren().clear();
			imageView.setImage(new Image(getClass().getResourceAsStream("../icons/sokoban/Ground_Concrete.png")));
			imageView.setFitHeight(tileSize);
			imageView.setFitWidth(tileSize);
			break;
		case Cell.NONE:
			stackPane.getChildren().clear();
			return;
		}
		
		stackPane.getChildren().add(imageView);
	}
	
	//Load previous level
	@FXML private void previousLevel() {
		level--;
		customLevel = false;
		resetLevel();
	}
	
	//Load next level
	@FXML private void nextLevel() {
		level++;
		customLevel = false;
		resetLevel();
	}
	
	//Reset current level
	@FXML private void resetLevel() {
		initializeGrid();		
		renderCharacter();
		renderBlocks();
		update();
	}
	
	
	//Character rendering
	private void renderCharacter() {
		// Initialize character container
		charPane = new StackPane();

		// Initialize character ImageView and set properties
		charImageView = new ImageView(new Image(getClass().getResourceAsStream("../icons/sokoban/Character_Front0.png")));
		charImageView.setFitHeight(tileSize*0.8);
		charImageView.setFitWidth(tileSize*0.6);
		
		// Set charPane properties
		charPane.setAlignment(Pos.CENTER);
		charPane.setTranslateX(game.getPlayer().getX() * tileSize);
		charPane.setTranslateY(game.getPlayer().getY() * tileSize);
		charPane.getChildren().setAll(charImageView);
		
		// Add character to gridPane
		gridPane.add(charPane, 0, 0);
	}
	
	//Character movement
	private void moveCharacter(Direction direction) {
		switch (direction) {
		case UP:
			charImageView.setImage(new Image(getClass().getResourceAsStream("../icons/sokoban/Character_Back0.png")));
			break;
		case DOWN:
			charImageView.setImage(new Image(getClass().getResourceAsStream("../icons/sokoban/Character_Front0.png")));
			break;
		case LEFT:
			charImageView.setImage(new Image(getClass().getResourceAsStream("../icons/sokoban/Character_Left0.png")));
			break;
		case RIGHT:
			charImageView.setImage(new Image(getClass().getResourceAsStream("../icons/sokoban/Character_Right0.png")));
			break;
		}
		
		//Animation duration
		duration = 300;
			
		//Transition properties
		TranslateTransition translateTransition = new TranslateTransition();
		translateTransition.setNode(charPane);
		translateTransition.setToX(game.getPlayer().getX() * tileSize);
		translateTransition.setToY(game.getPlayer().getY() * tileSize);
		translateTransition.setInterpolator(Interpolator.EASE_BOTH);
		translateTransition.setDuration(Duration.millis(duration));
		
		//Play Transition
		translateTransition.play();
		
		Timeline timeline = new Timeline();
		switch (direction) {
			case UP:
			timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration * 0.25),
			        ae -> charImageView.setImage(new Image(getClass().getResourceAsStream("../icons/sokoban/Character_Back1.png")))));
			timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration * 0.5),
					ae -> charImageView.setImage(new Image(getClass().getResourceAsStream("../icons/sokoban/Character_Back2.png")))));
			timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration * 0.75),
					ae -> charImageView.setImage(new Image(getClass().getResourceAsStream("../icons/sokoban/Character_Back1.png")))));
			timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration * 1),
					ae -> charImageView.setImage(new Image(getClass().getResourceAsStream("../icons/sokoban/Character_Back2.png")))));
			break;
			
			case DOWN:
			timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration * 0.25),
					ae -> charImageView.setImage(new Image(getClass().getResourceAsStream("../icons/sokoban/Character_Front1.png")))));
			timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration * 0.5),
					ae -> charImageView.setImage(new Image(getClass().getResourceAsStream("../icons/sokoban/Character_Front2.png")))));
			timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration * 0.75),
					ae -> charImageView.setImage(new Image(getClass().getResourceAsStream("../icons/sokoban/Character_Front1.png")))));
			timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration * 1),
					ae -> charImageView.setImage(new Image(getClass().getResourceAsStream("../icons/sokoban/Character_Front2.png")))));
			break;
		
			case LEFT:
			timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration * 0.25),
					ae -> charImageView.setImage(new Image(getClass().getResourceAsStream("../icons/sokoban/Character_Left1.png")))));
			timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration * 0.5),
					ae -> charImageView.setImage(new Image(getClass().getResourceAsStream("../icons/sokoban/Character_Left0.png")))));
			timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration * 0.75),
					ae -> charImageView.setImage(new Image(getClass().getResourceAsStream("../icons/sokoban/Character_Left1.png")))));
			timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration * 1),
					ae -> charImageView.setImage(new Image(getClass().getResourceAsStream("../icons/sokoban/Character_Left0.png")))));
			break;

			case RIGHT:
			timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration * 0.25),
					ae -> charImageView.setImage(new Image(getClass().getResourceAsStream("../icons/sokoban/Character_Right1.png")))));
			timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration * 0.5),
					ae -> charImageView.setImage(new Image(getClass().getResourceAsStream("../icons/sokoban/Character_Right0.png")))));
			timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration * 0.75),
					ae -> charImageView.setImage(new Image(getClass().getResourceAsStream("../icons/sokoban/Character_Right1.png")))));
			timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration * 1),
					ae -> charImageView.setImage(new Image(getClass().getResourceAsStream("../icons/sokoban/Character_Right0.png")))));
			break;
		}
		
		timeline.play();
		
		System.out.println(String.format("Player coords: [%s, %s] Pixel coords: [%s, %s]", 
				game.getPlayer().getX(), game.getPlayer().getY(), charPane.getTranslateX(), charPane.getTranslateY()));
	}

	//Block rendering
	private void renderBlocks() {
		Image blockImage = new Image(getClass().getResourceAsStream("../icons/sokoban/Crate_Blue.png"));
		
		// Render new image for each block in blockSet
		for (Block block : game.getBlocks()) {
			System.out.println(block);
			// Initialize block container
			StackPane blockPane = new StackPane();
			
			// Set blockPane properties
			blockPane.setAlignment(Pos.CENTER);
			blockPane.setTranslateX(block.getX() * tileSize);
			blockPane.setTranslateY(block.getY() * tileSize);
			
			// Initialize and set ImageView properties
			ImageView blockImageView = new ImageView(blockImage);
			blockImageView.setFitHeight(tileSize);
			blockImageView.setFitWidth(tileSize);
			
			// Insert ImageView into StackPane
			blockPane.getChildren().setAll(blockImageView);
			
			// Add block to blockMap
			blockMap.put(block, blockPane);
			
			// Add block to gridPane
			gridPane.add(blockPane, 0, 0);
		}
	}
	
	//Block movememnt
	private void moveBlock(Block block) {
		//Animation duration
		duration = 300;
		
//		if (game.get(block.getX(), block.getY()).isEndpoint())
//			getImageView(block)
		
		//Transition properties
		TranslateTransition translateTransition = new TranslateTransition();
		translateTransition.setNode(blockMap.get(block));
		translateTransition.setToX(block.getX() * tileSize);
		translateTransition.setToY(block.getY() * tileSize);
		translateTransition.setInterpolator(Interpolator.EASE_BOTH);
		translateTransition.setDuration(Duration.millis(duration));
		
		//Play Transition
		translateTransition.play();
	}
	
	//Returns corresponding ImageView if contained within node
	private ImageView getImageView(Pane pane) {
		return (ImageView) pane.getChildren()
							.stream()
							.filter(node -> node.getClass().getSimpleName().equals("ImageView"))
							.findFirst()
							.get();
	}
	
	//Handlers
	@FXML
    private void handleLoadClick(ActionEvent event) throws FileNotFoundException {
		StringBuilder levelStringBuilder = new StringBuilder();
		
		// Retrive parent for file chooser
    	Stage mainStage = (Stage) rootPane.getScene().getWindow();
    	
    	// Construct file chooser
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("Open Data File");
    	fileChooser.getExtensionFilters().addAll(
    	         new ExtensionFilter("Text file (*.txt)", "*.txt"),
    	         new ExtensionFilter("All Files", "*.*"));
    	
    	// Launch file chooser and retrive selected file
    	File selectedFile = fileChooser.showOpenDialog(mainStage);
    	
    	// File stream
    	Scanner fileScanner = new Scanner(selectedFile);
    	
    	// Scan through file
    	while (fileScanner.hasNextLine())
    		levelStringBuilder.append(fileScanner.nextLine() + '\n');
    	
    	// Close scanner
    	fileScanner.close();
    	
    	// Retrive level string
    	String levelString = levelStringBuilder.toString();
    	
    	// Update current level
    	game = new Game(Levels.parseStr(levelString));
    	customLevel = true;
    	
    	// Load new level
    	resetLevel();
	}

    @FXML
    private void handleSaveClick(ActionEvent event) throws FileNotFoundException {
    	// Retrive parent for file chooser
    	Stage mainStage = (Stage) rootPane.getScene().getWindow();
    	
    	// Construct file chooser
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("Open Data File");
    	fileChooser.getExtensionFilters().addAll(
    	         new ExtensionFilter("Text file (*.txt)", "*.txt"),
    	         new ExtensionFilter("All Files", "*.*"));
    	
    	// Launch file chooser and retrive selected file
    	File selectedFile = fileChooser.showSaveDialog(mainStage);
    	
    	// Create printstream
    	PrintStream printStream = new PrintStream(new FileOutputStream(selectedFile));
    	
    	// Print game to file
    	printStream.print(game);
    	
    	// Close stream
    	printStream.flush();
    	printStream.close();
    }

    
}
