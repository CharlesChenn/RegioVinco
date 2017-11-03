package pacg;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * This class provides some basic responses to key presses, including
 * togging the debugging rendering and pausing. In addition, the hook
 * function allows for a descendant class to hook into key presses
 * with custom responses.
 *
 * @author Richard McKenna
 * @version 1.0
 */
public class PointAndClickGameKeyController {

    // THE GAME FROM WHICH THE EVENTS WERE GENERATED
    private PointAndClickGame game;
    
    // THIS IS A HOOK FOR RESPONDING TO KEY PRESSES
    private KeyPressHook hook;

    /**
     * The constructor just sets up access to the game for which to respond to.
     *
     * @param initGame the game being played.
     */
    public PointAndClickGameKeyController(PointAndClickGame initGame) {
	game = initGame;
	hook = null;
    }
    
    public void setHook(KeyPressHook initHook) {
	hook = initHook;
    }

    /**
     * This key handler simply activates our debug text display, which may then
     * be rendered by the canvas.
     *
     * @param ke the event object, it contains information about the user
     * interaction, like which key was pressed.
     */
    public void processKeyPressed(KeyEvent ke) {
	// THE 'D' KEY TOGGLES DEBUG TEXT DISPLAY
	if (ke.getCode() == KeyCode.D) {
	    // TOGGLE IT OFF
	    if (game.getDataModel().isDebugTextRenderingActive()) {
		game.getDataModel().deactivateDebugTextRendering();
	    } // TOGGLE IT ON
	    else {
		game.getDataModel().activateDebugTextRendering();
	    }
	} // THE 'P' KEY PAUSES THE GAME, WHICH MEANS 
	// ALL UPDATE LOGIC GETS SKIPPED
	else if (ke.getCode() == KeyCode.P) {
	    // TOGGLE THE OFF
	    if (game.getDataModel().isPaused()) {
		game.getDataModel().unpause();
	    } // TOGGLE IT ON
	    else {
		game.getDataModel().pause();
	    }
	}
	
	// CALL THE HOOK FUNCTION
	if (hook != null)
	    hook.processKeyPressHook(ke);
    }
}
