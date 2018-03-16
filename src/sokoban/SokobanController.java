package sokoban;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
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
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SokobanController {
	private Game game;
	private Scene scene;
	private GridPane gridPane;
	private StackPane charPane;
	private ImageView charImageView;
	private String blockColor;
	private double tileWidth;
	private double tileHeight;
	private double tileSize;
	private int tilesX;
	private int tilesY;
	
	// Animation
	private Image image1, image2;
	private double duration;
	private int animationCycles;
	
	// Image map (Maps from file name to corresponding Image)
	private static final String BEIGE = "Beige", 	BLUE = "Blue", 
								BLACK = "Black", 	BROWN = "Brown", 
								GRAY = "Gray", 		PURPLE = "Purple", 
								RED = "Red", 		YELLOW = "Yellow";
	private HashMap<String, Image> imageMap;
	
	// Block container
	private HashMap<Block, StackPane> blockMap;
	
	// FXML References
	@FXML StackPane rootPane;
	@FXML AnchorPane subRootPane;
	@FXML Label levelLabel;
	@FXML Label movesLabel;
	
	
	
	// Initialize Game
	public void initialize() throws FileNotFoundException {
		// Initialize game
		game = new Game();
		
		// Initialize block map
		blockMap = new HashMap<>();
		
		// Set block color
		blockColor = BLUE;
		
		// Fill imageMap
		initializeImageMap();
		
		// Render game graphics
		renderGame();
	}

	// Render game graphics
	private void renderGame() {
		initializeGrid();
		renderCharacter();
		renderBlocks();
		
		// Set labels
		levelLabel.setText(String.format("Level: %s", game.getLevel()));
		movesLabel.setText(String.format("Moves: %s", game.getMoveCount()));
	}
	
	// Initialize GUI
	private void initializeGrid() {
		// Create Grid Pane to encapsulate tiles
		gridPane = new GridPane();
		gridPane.setAlignment(Pos.CENTER);
		
		// Board dimensions
		tilesX = game.getWidth();
		tilesY = game.getHeight();
		
		// Calculate tile dimensions
		tileWidth = subRootPane.getMinWidth() / tilesX;
		tileHeight = subRootPane.getMinHeight() / tilesY;
		tileSize = Math.round(Math.min(tileWidth, tileHeight));
		
		// Explicitly fix rounding errors :(
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
		
		// Generate tiles
		for (int y = 0; y < tilesY; y++)
			for (int x = 0; x < tilesX; x++) {
				// Initialize new tile
				StackPane tile = new StackPane();
					
				// Set identifier (scene.lookup("#id"))
				tile.setId(String.format("Tile_%s_%s", x, y));

				// Apply size specification
				tile.setPrefSize(tileSize, tileSize);
				
				// Add image to imageView
				updateImage(x, y, tile);

				// Append tile to gridPane
				gridPane.add(tile, x, y);
			}

		// Insert Grid Pane into subRootPane
		subRootPane.getChildren().setAll(gridPane);
		AnchorPane.setBottomAnchor(gridPane, 0d);
		AnchorPane.setTopAnchor(gridPane, 0d);
		AnchorPane.setLeftAnchor(gridPane, 0d);
		AnchorPane.setRightAnchor(gridPane, 0d);
	}
	
	// Fills imageMap with images from 'icons.sokoban'
	private void initializeImageMap() throws FileNotFoundException {
		// Initialize
		imageMap = new HashMap<>();
		
    	File importFolder = new File(getClass().getResource("../icons/sokoban").getPath());
    	FilenameFilter fileFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".png");
			}
		};
		
    	for (File imageFile : importFolder.listFiles(fileFilter))
    		imageMap.put(imageFile.getName(), new Image(new FileInputStream(imageFile)));
	}
	
	// Set key listener (Note: Do not call prior to scene loading)
	public void postInitialize() {
		// Create reference to current scene
		scene = rootPane.getScene();
		
		// Set key listener
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				// Local variables
				Move move = null;
				boolean moonWalk = false;
				
				// Get key pressed
				String key = event.getCode().getName();
				
				// Perform action based on pressed key
				switch (key) {
				case "Up":
					move = game.move(Direction.UP);
					break;
				case "Down":
					move = game.move(Direction.DOWN);
					break;
				case "Left":
					move = game.move(Direction.LEFT);
					break;
				case "Right":
					move = game.move(Direction.RIGHT);
					break;
				case "X":
					move = game.redo();
					break;
				case "Z":
					move = game.undo();
					moonWalk = true;
					break;
				case "R":
					resetLevel();
					return;
				default:
					System.out.println("Key not supported: " + key);
					return;
				}
				
				// Break if move is invalid
				if (move == Move.ILLEGAL) return;
				
				// Move character
				moveCharacter(move.getDirection(), moonWalk);
				
				// Move block
				if (move.isPush())
					moveBlock(move.getDirection());
					
				// Update GUI
				update();			
				System.out.printf("undoStack: %s\n"
								+ "redoStack: %s\n", 
								game.undoStack, game.redoStack);
		}});
	}
	
	
	
	// Update GUI
	private void update() {
		// Update labels
		levelLabel.setText(String.format("Level: %s", game.getLevel()));
		movesLabel.setText(String.format("Moves: %s", game.getMoveCount()));
		
		int[] playerCoords = game.getPlayer().getCoords();
		
		int minX = Math.max(playerCoords[0] - 2, 0);
		int maxX = Math.min(playerCoords[0] + 2, tilesX);
		int minY = Math.max(playerCoords[1] - 2, 0);
		int maxY = Math.min(playerCoords[1] + 2, tilesY);
		
		// Iterate through grid to update images
		for (int y = minY; y < maxY; y++) {
			for (int x = minX; x < maxX; x++) {
				// Reference to ImageView at tile (x, y)
				StackPane stackPane = (StackPane) scene.lookup(String.format("#Tile_%s_%s", x, y));
				
				// Update Image
				updateImage(x, y, stackPane);
			}
		}
	}
	
	// Set image at specified tile
	private void updateImage(int x, int y, StackPane stackPane) {
		// Tile
		Cell tile = game.get(x, y);
		
		// Corresponding StackPane
		ImageView baseImageView = new ImageView(imageMap.get("Ground_Concrete.png"));
		baseImageView.setFitHeight(tileSize);
		baseImageView.setFitWidth(tileSize);

		stackPane.getChildren().setAll(baseImageView);
		
		ImageView imageView = new ImageView();

		// Set image based on grid values
		switch (tile.toChar()) {
		case Cell.WALL:
			imageView.setImage(imageMap.get("Wall_Black.png"));
			imageView.setFitHeight(tileSize);
			imageView.setFitWidth(tileSize);
			break;
		case Cell.ENDPOINT:	
			imageView.setImage(imageMap.get(String.format("EndPoint_%s.png", blockColor)));
			imageView.setFitHeight(tileSize/3);
			imageView.setFitWidth(tileSize/3);
			break;
		case Cell.EMPTY:
			stackPane.getChildren().clear();
			imageView.setImage(imageMap.get("Ground_Concrete.png"));
			imageView.setFitHeight(tileSize);
			imageView.setFitWidth(tileSize);
			break;
		case Cell.NONE:
			stackPane.getChildren().clear();
			return;
		}
		
		stackPane.getChildren().add(imageView);
	}
	
	
	
	// Load previous level
	@FXML private void previousLevel() {
		// Lower level bound
		if (game.getLevel() <= 1) return;
		
		// Load new level
		game.load(game.getLevel() - 1);
		
		// Render graphics
		renderGame();
	}
	
	// Load next level
	@FXML private void nextLevel() {
		// Upper level bound
		if (game.getLevel() >= Levels.getNumOfLevels()) return;
		
		// Load new level
		game.load(game.getLevel() + 1);
		
		// Render graphics
		renderGame();
	}
	
	// Reset current level
	@FXML private void resetLevel() {
		// Reload current level
		game.load(game.getLevel());

		// Render graphics
		renderGame();
	}

	
	
	// Character rendering
	private void renderCharacter() {
		// Initialize character container
		charPane = new StackPane();

		// Initialize character ImageView and set properties
		charImageView = new ImageView(imageMap.get("Character_Front0.png"));
		charImageView.setFitHeight(tileSize*0.8);
		charImageView.setFitWidth(tileSize*0.6);
		
		// Set charPane properties
		charPane.setAlignment(Pos.CENTER);
		charPane.setTranslateX(game.getPlayer().getX() * tileSize);
		charPane.setTranslateY(game.getPlayer().getY() * tileSize);
		charPane.setViewOrder(-1);
		charPane.getChildren().setAll(charImageView);
		
		// Add character to gridPane
		gridPane.add(charPane, 0, 0);
	}
	
	// Character movement
	private void moveCharacter(Direction direction, boolean moonWalk) {
		// Set animation properties
		duration = 300;
		animationCycles = 2;

		// Change properties to perform moonwalk if requested
// 		if (moonWalk) {
// 			duration = 300;		
// 			animationCycles = 2;
// 		}
		
		switch (direction) {
		case UP:
			charImageView.setImage(imageMap.get("Character_Back0.png"));
			break;
		case DOWN:
			charImageView.setImage(imageMap.get("Character_Front0.png"));
			break;
		case LEFT:
			charImageView.setImage(imageMap.get("Character_Left0.png"));
			break;
		case RIGHT:
			charImageView.setImage(imageMap.get("Character_Right0.png"));
			break;
		}
		
		// Animation duration
		
			
		// Transition properties
		TranslateTransition translateTransition = new TranslateTransition();
		translateTransition.setNode(charPane);
		translateTransition.setToX(game.getPlayer().getX() * tileSize);
		translateTransition.setToY(game.getPlayer().getY() * tileSize);
		translateTransition.setInterpolator(Interpolator.EASE_BOTH);
		translateTransition.setDuration(Duration.millis(duration));
		
		// Play Transition
		translateTransition.play();
		
		switch (direction) {
			case UP:
			image1 = imageMap.get("Character_Back1.png");
			image2 = imageMap.get("Character_Back2.png");
			break;
			
			case DOWN:
			image1 = imageMap.get("Character_Front1.png");
			image2 = imageMap.get("Character_Front2.png");
			break;
		
			case LEFT:
			image1 = imageMap.get("Character_Left1.png");
			image2 = imageMap.get("Character_Left0.png");
			break;

			case RIGHT:
			image1 = imageMap.get("Character_Right1.png");
			image2 = imageMap.get("Character_Right0.png");
			break;
		}
		
		Timeline timeline = new Timeline();
		for (int i = 0; i < animationCycles; i++) {
			timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration * (i + 0.5)/animationCycles),
			        ae -> charImageView.setImage(image1)));
			timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration * (i + 1.0)/animationCycles),
					ae -> charImageView.setImage(image2)));
		}

		timeline.play();
	}

	// Block rendering
	private void renderBlocks() {
		// Local vars
		ImageView blockImageView;
		
		// Render new image for each block in blockSet
		for (Block block : game.getBlocks()) {
			// Initialize block container
			StackPane blockPane = new StackPane();
			
			// Set blockPane properties
			blockPane.setAlignment(Pos.CENTER);
			blockPane.setTranslateX(block.getX() * tileSize);
			blockPane.setTranslateY(block.getY() * tileSize);
			
			// Initialize ImageView based on block state
			if (game.get(block.getX(), block.getY()).isEndpoint())
				blockImageView = new ImageView(imageMap.get(String.format("CrateDark_%s.png", blockColor)));
			else
				blockImageView = new ImageView(imageMap.get(String.format("Crate_%s.png", blockColor)));
			
			// Set ImageView properties
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
	
	// Block movememnt
	private void moveBlock(Direction direction) {
		// Local vars
		TranslateTransition translateTransition;
		Timeline timeline;
		ImageView blockImageView;
		Block block;
		
		// Get block
		block = game.getBlock(direction);
		
		// Transition properties
		translateTransition = new TranslateTransition();
		translateTransition.setNode(blockMap.get(block));
		translateTransition.setToX(block.getX() * tileSize);
		translateTransition.setToY(block.getY() * tileSize);
		translateTransition.setInterpolator(Interpolator.EASE_BOTH);
		translateTransition.setDuration(Duration.millis(duration));

		// Play Transition
		translateTransition.play();
		
		// Setup timeline
		timeline = new Timeline();
		blockImageView = getImageView(blockMap.get(block));
		
		// Add actionEvents
		if (game.get(block.getX(), block.getY()).isEndpoint())
			timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration),
					ae -> blockImageView.setImage(imageMap.get(String.format("CrateDark_%s.png", blockColor)))));
		else
			timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration),
					ae -> blockImageView.setImage(imageMap.get(String.format("Crate_%s.png", blockColor)))));
		
		// Play timeline
		timeline.play();
	}
	
	
	
	// Returns ImageView if contained within node
	private ImageView getImageView(Pane pane) {
		return (ImageView) pane.getChildren()
							.stream()
							.filter(node -> node.getClass().getSimpleName().equals("ImageView"))
							.findFirst()
							.get();
	}
	
	
	
	// Handlers
	@FXML
    private void handleLoadClick(ActionEvent event) throws FileNotFoundException {
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

    	// Break if no file has been selected
    	if (selectedFile == null) return;
    	
    	// Load new level
    	game.load(selectedFile);
    	
    	// Update
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
    	
    	// Break if no file has been selected
    	if (selectedFile == null) return;
    	
    	// Save current game
    	game.save(selectedFile);
    }

    
}
