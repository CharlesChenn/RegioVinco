package pacg;

import java.util.ArrayList;

/**
 * This class provides the base class for a game data management class. Note
 * that general game data is already provided and managed here. A sub-class
 * would provide the game-specific data.
 *
 * @author Richard McKenna
 * @version 1.0
 */
public abstract class PointAndClickGameDataModel {

    // GAME WORLD DIMENSIONS
    protected int gameWidth;
    protected int gameHeight;

    // KEEPS TRACK OF WHETHER THE GAME IS IN PROGRESS OR NOT
    protected PointAndClickGameState gameState;

	// THESE VARIABLES HELP US DISPLAY TEXT ON THE
    // SCREEN FOR THE PURPOSE OF DEBUGGING
    protected ArrayList<String> debugText;
    protected boolean debugTextRenderingActive;
    protected int debugTextX;
    protected int debugTextY;

    // THIS ALLOWS US TO PAUSE THE GAME IF WE LIKE
    protected boolean paused;

    /**
     * Default constructor, it should be invoked by any child class.
     */
    public PointAndClickGameDataModel() {
	// AND INIT ALL THE DEBUG DISPLAY STUFF
	debugText = new ArrayList();
	debugTextRenderingActive = false;
	debugTextX = 10;
	debugTextY = 200;

	// WE START UNPAUSED
	paused = false;

	// THE GAME HASN'T STARTED YET
	gameState = PointAndClickGameState.NOT_STARTED;
    }

	// ACCESSOR METHODS
    // getDebugText
    // getDebugTextX
    // getDebugTextY
    // getGameHeight
    // getGameWidth
    // getGameState
    // isDebugTextRenderingActive
    // isPaused
    /**
     * For accessing the Vector containing the debug text being stored for
     * optional display.
     *
     * return the Vector that stores all the debug text.
     */
    public ArrayList<String> getDebugText() {
	return debugText;
    }

    /**
     * For accessing the x coordinate of the debug text.
     *
     * return the x coordinate of where we will start rendering the debug text.
     */
    public int getDebugTextX() {
	return debugTextX;
    }

    /**
     * For accessing the y coordinate of the debug text.
     *
     * @return the y coordinate of where we will start rendering the debug text.
     */
    public int getDebugTextY() {
	return debugTextY;
    }

    /**
     * For accessing the height of the game's canvas, and consequently the game
     * world.
     *
     * @return the height of the game world playing surface.
     */
    public int getGameHeight() {
	return gameHeight;
    }

    /**
     * For accessing the width of the game's canvas, and consequently the game
     * world.
     *
     * @return the width of the game world playing surface.
     */
    public int getGameWidth() {
	return gameWidth;
    }

    /**
     * For accessing the current game state.
     *
     * @return the current state of the game, which may only be one of
     * NOT_STARTED, IN_PROGRESS, LOSS, or WIN
     */
    public PointAndClickGameState getGameState() {
	return gameState;
    }

    /**
     * For asking if the debug text is currently being rendered.
     *
     * @return true if the debug text is currently active, and thus renderable,
     * false otherwise.
     */
    public boolean isDebugTextRenderingActive() {
	return debugTextRenderingActive;
    }

    /**
     * For asking if the game is currently paused.
     *
     * @return true if the game is paused, false otherwise
     */
    public boolean isPaused() {
	return paused;
    }

	// GAME STATE TEST METHODS
    // inProgress
    // lost
    // won
    /**
     * Asks if the game is currently in progress or not.
     *
     * @return true if the game is currently in progress, meaning gameplay is
     * active, false otherwise.
     */
    public boolean inProgress() {
	return gameState == PointAndClickGameState.IN_PROGRESS;
    }

    /**
     * Asks if the game is over and the player lost or not.
     *
     * @return true if the game is over and the player lost
     */
    public boolean lost() {
	return gameState == PointAndClickGameState.LOSS;
    }

    /**
     * Asks if the game is over and the player won or not.
     *
     * @return true if the game is over and the player won
     */
    public boolean won() {
	return gameState == PointAndClickGameState.WIN;
    }

	// MUTATOR METHODS
    // activateDebugTextRendering
    // beginGame
    // deactivateDebuTextRendering
    // endGameAsLoss
    // endGameAsWin
    // incDebugText
    // pause
    // setGameDimensions
    // setGameState
    // unpause
    /**
     * Activates the debug text, allowing it to be rendered.
     */
    public void activateDebugTextRendering() {
	debugTextRenderingActive = true;
    }

    /**
     * Mutator method for setting the game state to GameState.IN_PROGRESS
     */
    public void beginGame() {
	gameState = PointAndClickGameState.IN_PROGRESS;
    }

    /**
     * Deactivates the debug text, preventing it from being rendered.
     */
    public void deactivateDebugTextRendering() {
	debugTextRenderingActive = false;
    }

    /**
     * Mutator method for setting the game state to GameState.LOSS
     */
    public void endGameAsLoss() {
	gameState = PointAndClickGameState.LOSS;
    }

    /**
     * Mutator method for setting the game state to GameState.WIN
     */
    public void endGameAsWin() {
	gameState = PointAndClickGameState.WIN;
    }

    /**
     * Moves the position of the debug text by the provided increment.
     *
     * @param incX the amount by which to move the debug text on screen in the x
     * axis.
     *
     * @param incY the amount by which to move the debug text on screen in the y
     * axis.
     */
    public void incDebugText(int incX, int incY) {
	debugTextX += incX;
	debugTextY += incY;
    }

    /**
     * Pauses the game, meaning all game logic gets skipped.
     */
    public void pause() {
	paused = true;
    }

    /**
     * Mutator method for setting the dimensions of the playing surface.
     *
     * @param initGameWidth width in pixels of the playing surface
     *
     * @param initGameHeight height in pixels of the playing surface
     */
    public void setGameDimensions(int initGameWidth, int initGameHeight) {
	gameWidth = initGameWidth;
	gameHeight = initGameHeight;
    }

    /**
     * Mutator method for setting the game state.
     *
     * @param initGameState the game state to use.
     */
    public void setGameState(PointAndClickGameState initGameState) {
	gameState = initGameState;
    }

    /**
     * Unpauses the game, allowing game logic to be executed each frame.
     */
    public void unpause() {
	paused = false;
    }

    // ABSTRACT METHODS - GAME-SPECIFIC IMPLEMENTATIONS REQUIRED
    // reset
    // updateAll
    // updateDebugText
    /**
     * For resetting all game data to the start of a new game.
     *
     * @param game the game in progress
     */
    public abstract void reset(PointAndClickGame game);

    /**
     * Called each frame, this method is for updating all the game data that is
     * particular to the custom game application.
     *
     * @param game the game in progress that is to be udated.
     */
    public abstract void updateAll(PointAndClickGame game, double percentage);

    /**
     * Called each frame, this method is for updating the debug text that may be
     * currently displayed.
     *
     * @param game the game in progress from this method will likely derive the
     * textual description for debugging purposes.
     */
    public abstract void updateDebugText(PointAndClickGame game);
}
