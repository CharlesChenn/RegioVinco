package regio_vinco;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import world_data.Region;

/**
 * This class represents a game text node that can be moved and
 * rendered and can respond to mouse interactions.
 */
public class MovableText {
    // A JAVAFX TEXT NODE IN THE SCENE GRAPH
    protected Text text;
    protected Label label;
    protected Image image;
    protected Region region;
    
    // USED FOR MANAGING NODE MOVEMENT
    protected double[] velocity = new double[2];
    protected double[] acceleration = new double[2];

    /**
     * Constructor for initializing a GameNode, note that the provided
     * text argument should not be null.
     * 
     * @param initText The text managed by this object.
     */
    public MovableText(Text initText, Label initLabel, Region initRegion) {
	text = initText;
        label = initLabel;
        region = initRegion;
    }
    
    public MovableText(Image initImage, Label initLabel, Text initText){
        label = initLabel;
        image = initImage;
        text = initText;
    }
    
    public MovableText(Text initText, Label initLabel){
        text = initText;
        label = initLabel;
    }
    // ACCESSOR AND MUTATOR METHODS
    
    public Region getRegion(){
        return region;
    }
    
    public Text getText() {
	return text;
    }
    
    public Image getImage(){
        return image;
    }
    
    public Label getLabel() {
        return label;
    }
    
    public void setText(Text initText) {
	text = initText;
    }
    
    public double getVelocityX() {
	return velocity[0];
    }
    
    public double getVelocityY() {
	return velocity[1];
    }
    
    public void setVelocityX(double initVelocityX) {
	velocity[0] = initVelocityX;
    }
    
    public void setVelocityY(double initVelocityY) {
	velocity[1] = initVelocityY;
    }

    /**
     * Called each frame, this function moves the node according
     * to its current velocity and updates the velocity according to
     * its current acceleration, applying percentage as a weighting for how
     * much to scale the velocity and acceleration this frame.
     * 
     * @param percentage The percentage of a frame this the time step
     * that called this method represents.
     */
    public void update(double percentage) {
	// UPDATE POSITION
	double x = label.translateXProperty().doubleValue();
	label.translateXProperty().setValue(x + (velocity[0] * percentage));
	double y = label.translateYProperty().doubleValue();
	label.translateYProperty().setValue(y + (velocity[1] * percentage));
	
	// UPDATE VELOCITY
	velocity[0] += (acceleration[0] * percentage);
	velocity[1] += (acceleration[1] * percentage);
    }
}