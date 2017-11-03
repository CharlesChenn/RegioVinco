package pacg;

import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * The classes from the pcg package, including this class, provide a
 * framework for making a point and click JavaFX game. This class serves as 
 * the focal point of that game, with access to all the important game data and 
 * controls. Note that it uses a custom JavaFX AnimationTimer to provide 
 * regular updates of the game data and rendering, so it also uses a 
 * ReentrantLock to make sure another thread doesn't try to change
 * data mid-render.
 *
 * @author Richard McKenna
 * @version 1.0
 */
public abstract class PointAndClickGame {

    // THIS IS THE NAME OF THE CUSTOMIZED GAME
    protected String name;
    
    // data WILL MANAGE OUR GAME DATA, WHICH SHOULD
    // BE CUSTOMIZED FOR THE GIVEN GAME
    protected PointAndClickGameDataModel data;

    // THERE WILL BE 2 THREADS AT WORK IN THIS APPLICATION,
    // THE MAIN GUI THREAD, WHICH WILL LISTEN FOR USER INTERACTION
    // AND CALL OUR EVENT HANDLERS, AND THE TIMER THREAD, WHICH 
    // WILL UPDATE ALL THE GAME DATA, WHICH WILL FORCE RENDERING.
    // THIS LOCK WILL MAKE SURE THAT ONE THREAD DOESN'T RUIN WHAT 
    // THE OTHER IS DOING (CALLED A RACE CONDITION). EACH THREAD WILL 
    // NEED TO LOCK THE DATA USING THIS OBJECT BEFORE EACH USE AND 
    // THEN UNLOCK WHEN DONE WITH IT.
    protected ReentrantLock dataLock;
    
    // THIS IS THE GAME WINDOW
    protected Stage window;
    
    // THIS IS THE GAME SCENE
    protected Scene scene;

    // EVERY OUR SCENE WILL CONTAIN A SINGLE StackPane, ON WHICH
    // WE MAY PLACE WHATEVER SHAPES AND IMAGES WE DESIRE IN LAYERS
    protected StackPane stackPane;

    // HERE ARE OUR GUI IMAGE COMPONENTS, SOME WHICH MAY BE CLICKABLE
    private TreeMap<String, ImageView> guiImages;
    private TreeMap<String, Button> guiButtons;

    // THIS TIMER WILL BE STARTED AND ITS handle FUNCTION
    // WILL BE CALLED EACH FRAME, FORCING DATA UPDATE AND
    // RENDERING TO HAPPEN.
    protected PointAndClickGameTimer gameTimerTask;
    
    // THESE WILL HELP KEEP TRACK OF THE FRAME RATE
    protected int framesPerSecond;
    protected int frameDuration;

    // THESE VARIABLES STORE THE PIXEL DISTANCES FROM THE EDGES
    // OF THE CANVAS WHERE THE GAME WILL BE PLAYED. BY SETTING
    // THESE VALUES TO 0, THE FULL CANVAS WOULD BE THE PLAY AREA.
    // THESE ARE LEFT protected, SO A SUB CLASS, WHICH REPRESENTS
    // A CUSTOM GAME COULD CHANGE THESE AS THE DEVELOPER SEES FIT.
    protected float boundaryLeft = 0;
    protected float boundaryRight = 0;
    protected float boundaryTop = 0;
    protected float boundaryBottom = 0;
    
    // THIS RESPONDS TO KEY PRESSES
    protected PointAndClickGameKeyController keyController;

    /**
     * This constructor sets up everything, including the GUI and the game data,
     * and starts the timer, which will force state updates and rendering. Note
     * that rendering will not be seen until the game's window is set visible.
     *
     * @param appTitle the name of the game application, it will be put in the
     * window's title bar.
     *
     * @param initFramesPerSecond the frame rate to be used for running the game
     * application. This refers to how many times each second the game state is
     * updated and rendered.
     */
    public PointAndClickGame(Stage initWindow, String appTitle, int initFramesPerSecond) {
	// THE GAME WINDOW, TITLE, AND FRAME RATE ARE
	// CUSTOMIZABLE AT CONSTRUCTION TIME
	window = initWindow;
	name = appTitle;
	framesPerSecond = initFramesPerSecond;

	// CALCULATE THE TIME EACH FRAME SHOULD TAKE
	frameDuration = 1000 / framesPerSecond;

	// CONSTRUCT OUR LOCK, WHICH WILL MAKE SURE
	// WE ARE NOT UPDATING THE GAME DATA SIMULATEOUSLY
	// IN TWO DIFFERENT THREADS
	dataLock = new ReentrantLock();

	// AND NOW SETUP THE FULL APP. NOTE THAT SOME
	// OF THESE METHODS MUST BE CUSTOMLY PROVIDED FOR
	// EACH GAME IMPLEMENTATION
	initData();
	initGUI();
	initHandlers();
	initTimer();
	initWindow();
    }

    // ACCESSOR METHODS
    // getDataModel
    // getFrameRate
    // getGUIButtons
    // getGUIDecor
    // getKeyController
    // getBoundaryLeft
    // getBoundaryRight
    // getBoundaryTop
    // getBoundaryBottom
    // getCanvas
    /**
     * For accessing the game data.
     *
     * @return the GameDataModel that stores all the game data.
     */
    public PointAndClickGameDataModel getDataModel() {
	return data;
    }
    
    public void addStackPaneLayer(Pane layer) {
	stackPane.getChildren().add(layer);
    }
    
    /**
     * Adds a new image to the user interface. Note that images may
     * be clickable, to be used like buttons.
     * 
     * @param type - Id for the image.
     * @param imagePath - Path to image file.
     * @param x - x location on screen
     * @param y - y location on screen
     * @return 
     */
    public ImageView addGUIImage(Pane pane, String type, Image img, int x, int y) {	
	ImageView view = createImageView(img, x, y);
	pane.getChildren().add(view);
	getGuiImages().put(type, view);
	return view;
    }
    
    public Button addGUIButton(Pane pane, String type, Image img, int x, int y) {
	ImageView view = createImageView(img, 0, 0);
	Button button = new Button();
	pane.getChildren().add(button);
	button.translateXProperty().setValue(x);
	button.translateYProperty().setValue(y);
	button.setGraphic(view);
	getGuiButtons().put(type, button);
	return button;
    }
    
    private ImageView createImageView(Image img, int x, int y) {
	ImageView view = new ImageView(img);
	view.setX(x);
	view.setY(y);
	return view;
    }
    /**
     * For accessing the frame rate.
     *
     * @return the frame rate, in frames per second of this application.
     */
    public int getFrameRate() {
	return framesPerSecond;
    }

    /**
     * For accessing the game GUI images.
     *
     * @return the data structure containing all the game images.
     */
    public TreeMap<String, ImageView> getGUIImages() {
	return getGuiImages();
    }
    
    public PointAndClickGameKeyController getKeyController() {
	return keyController;
    }

    /**
     * For accessing the distance from the left edge of the game canvas that
     * will be part of the playing game area.
     *
     * @return the left boundary in pixels of the playing game area.
     */
    public float getBoundaryLeft() {
	return boundaryLeft;
    }

    /**
     * For accessing the distance from the left edge of the game canvas that
     * represents the right edge of the playing game area.
     *
     * @return the right boundary in pixels of the playing game area.
     */
    public float getBoundaryRight() {
	return boundaryRight;
    }

    /**
     * For accessing the distance from the top edge of the game canvas that will
     * be part of the playing game area.
     *
     * @return the top boundary in pixels of the playing game area.
     */
    public float getBoundaryTop() {
	return boundaryTop;
    }

    /**
     * For accessing the distance from the top edge of the game canvas that
     * represents the bottom edge of the playing game area.
     *
     * @return the bottom boundary in pixels of the playing game area.
     */
    public float getBoundaryBottom() {
	return boundaryBottom;
    }

    /**
     * For accessing the canvas, which is the Pane that has
     * all the visible game nodes.
     */
    public Pane getStackPane() {
	return stackPane;
    }

    // INITIALIZATION METHODS - NOTE THAT METHODS ARE MADE private
    // IN PART TO REMOVE THE TEMPTATION TO OVERRIDE THEM
    // initWindow
    // initGUI
    // initHandler
    /**
     * Initializes our GUI's window. Note that this does not initialize the
     * components inside, or the event handlers.
     */
    private void initWindow() {
	// CONSTRUCT OUR WINDOW
	window.setTitle(name);

	// NOTE THAT THE WINDOW WILL BE RESIZED LATER
	window.setMaximized(true);
	window.setResizable(false);
    }

    /**
     * This should initialize and setup all GUI components. Note that it will
     * invoke the custom-implemented initGUIControls method, which the game
     * developer must provide to setup buttons.
     */
    private void initGUI() {
	// THIS WILL HOLD ALL OUR LAYERS
	stackPane = new StackPane();
	
	// INITIALIZE OUR GUI DATA STRUCTURES
	guiImages = new TreeMap();
	guiButtons = new TreeMap();
		
	// GUI CONTROLS ARE SETUP BY THE GAME DEVELOPER
	// USING THIS FRAMEWORK
	initGUIControls();

	// ULTIMATELY, EVERYTHING SHOULD BE INSIDE THE CANVAS
	scene = new Scene(stackPane, data.getGameWidth(), data.getGameHeight());
	window.setScene(scene);
    }

    /**
     * Sets up the event handler mechanics, including invoking the
     * initGUIHandlers method, which would be provided by the game developer
     * using this framework, and would presumably be different for each game.
     */
    private void initHandlers() {
	// SETUP THE KEY HANDLER
	keyController = new PointAndClickGameKeyController(this);
	
	// WE'LL TIE IT TO THE CANVAS
	stackPane.setOnKeyPressed(ke->{
	    keyController.processKeyPressed(ke);
	});
	stackPane.setFocusTraversable(true);
	stackPane.requestFocus();

	// AND NOW LET THE GAME DEVELOPER PROVIDE
	// CUSTOM HANDLERS
	initGUIHandlers();
    }

    /**
     * Sets up the timer, which will run the game updates and rendering on a
     * fixed-interval schedule.
     */
    public void initTimer() {
	gameTimerTask = new PointAndClickGameTimer(this);
	gameTimerTask.start();
    }

    // METHODS FOR RUNNING THE GAME, PROVIDING THE MECHANICS
    // OF THESE APPLICATIONS. NOTE THAT THE DEVELOPER USING
    // THIS FRAMEWORK NEED NOT EVEN KNOW ABOUT THESE METHODS,
    // JUST HOW TO PLUG INTO THEM THE SAME WAY YOU DON'T KNOW
    // ABOUT ALL THE INTERNAL WORKINGS OF SWING
    // beginUsingData
    // endUsingData
    // killApplication
    // processButtonPress
    // startGame
    // update
    
    /**
     * This method locks access to the game data for the thread that invokes
     * this method. All other threads will be locked out upon their own call to
     * this method and will be forced to wait until this thread ends its use.
     */
    public void beginUsingData() {
	dataLock.lock();
    }

    /**
     * This method frees access to the game data for the thread that invokes
     * this method. This will result in notifying any waiting thread that it may
     * proceed.
     */
    public void endUsingData() {
	dataLock.unlock();
    }

    /**
     * Call this method to kill the application associated with this object,
     * including closing the window.
     */
    public void killApplication() {
	window.close();
	System.exit(0);
    }

    /**
     * Displays the window, allowing the PointAndClickGame application to start
     * accepting user input and allow the user to actually play the game.
     */
    public void startGame() {
	// DISPLAY THE WINDOW
	window.show();
    }

    /**
     * This method is called once per frame and updates and renders everything
     * in the game including the gui.
     */
    public void update(double percentage) {
	// WE ONLY PERFORM GAME LOGIC
	// IF THE GAME IS UNDERWAY
	if (data.inProgress()) {
	    data.updateDebugText(this);
	    if (!data.isPaused()) {
		data.updateAll(this, percentage);
	    }
	}
	// WE ALWAYS HAVE TO WORRY ABOUT UPDATING THE GUI
	updateGUI();
    }

    // ABSTRACT METHODS - GAME-SPECIFIC IMPLEMENTATIONS REQUIRED
    // gameWon
    // gameLost
    // initData
    // initGUIControls
    // initGUIHandlers
    // reset
    // updateGUI
    
    /**
     * Initializes the game data used by the application. Note that it is this
     * method's obligation to construct and set this Game's custom GameDataModel
     * object as well as any other needed game objects.
     */
    public abstract void initData();

    /**
     * Initializes the game controls, like buttons, used by the game
     * application.
     */
    public abstract void initGUIControls();

    /**
     * Initializes the game event handlers for things like game gui buttons.
     */
    public abstract void initGUIHandlers();

    /**
     * Invoked when a new game is started, it resets all relevant game data and
     * gui control states.
     */
    public abstract void reset();

    /**
     * Updates the state of all gui controls according to the current game
     * conditions.
     */
    public abstract void updateGUI();

    /**
     * @return the guiImages
     */
    public TreeMap<String, ImageView> getGuiImages() {
        return guiImages;
    }

    /**
     * @return the guiButtons
     */
    public TreeMap<String, Button> getGuiButtons() {
        return guiButtons;
    }
}
