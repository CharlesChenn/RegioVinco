package pacg;

import javafx.animation.AnimationTimer;

/**
 * This simple class serves as the task executed each frame for updating, and
 * then rendering the game.
 *
 * @author Richard McKenna
 * @version 1.0
 */
public class PointAndClickGameTimer extends AnimationTimer {

    // WE'LL UPDATE THIS game OBJECT EACH FRAME
    protected PointAndClickGame game;
    
    // THE TARGET FRAME RATE
    protected long targetFPS = 50;
    
    // THE TIME OF THE LAST FRAME
    protected long lastTime = 0;
    
    // JavaFX HAS A HIGH RESOLUTION TIMER
    protected long TIMER_RESOLUTION = 1000000;

    /**
     * Constructor for initializing the task, it simply stores away the game to
     * use in its updates.
     *
     * @param initGame the game to be updated and rendered each frame.
     */
    public PointAndClickGameTimer(PointAndClickGame initGame) {
	// STORE FOR LATER
	game = initGame;
    }

    /**
     * Called 30 times per second, or whatever the current frame rate is, this
     * method updates the game and renders it, making sure to get a lock on the
     * data before doing so and releasing the data when done.
     */
    @Override
    public void handle(long now) {
	try {
	    // LOCK THE DATA
	    game.beginUsingData();

	    // CALCULATE THE PERCENTAGE OF THE TARGET FPS
	    // THAT THE LAST FRAME TOOK
	    double percentage = 0.0;
	    if (lastTime != 0)
		percentage = ((double)(now - lastTime))/(targetFPS * TIMER_RESOLUTION);
	    
	    // RECORD THIS FOR THE NEXT FRAME
	    lastTime = now;
	    
	    // UPDATE THE GAME
	    game.update(percentage);
	    
	} finally {
	    // RELEASE IT, SINCE THE OTHER THREAD
	    // MIGHT WANT TO UPDATE STUFF IN RESPONSE
	    // TO A MOUSE CLICK
	    game.endUsingData();
	}
    }
}
