package regio_vinco;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;

/**
 * This class serves to present a dialog with multiple button options to
 * the user and lets one access which was selected.
 * 
 * @author Richard McKenna
 */
public class ConfirmDialog extends Stage {
    // GUI CONTROLS FOR OUR DIALOG
    VBox messagePane;
    Scene messageScene;
    Label messageLabel;
    HBox buttonBox;
    Button yesButton;
    Button noButton;
    Button okButton;
    Button cancelButton;
    String selection;
    
    // CONSTANT CHOICES
    public static final String YES = "Yes";
    public static final String NO = "No";
    public static final String OK = "OK";
    public static final String CANCEL = "Cancel";
    
    /**
     * Initializes this dialog so that it can be used repeatedly
     * for all kinds of messages.
     * 
     * @param primaryStage The owner of this modal dialog.
     */
    public ConfirmDialog(Stage primaryStage) {
        // MAKE THIS DIALOG MODAL, MEANING OTHERS WILL WAIT
        // FOR IT WHEN IT IS DISPLAYED
        initModality(Modality.WINDOW_MODAL);
        initOwner(primaryStage);
        
        // LABEL TO DISPLAY THE CUSTOM MESSAGE
        messageLabel = new Label();        

        EventHandler handler = (EventHandler<ActionEvent>) (ActionEvent ae) -> {
            Button sourceButton = (Button)ae.getSource();
            ConfirmDialog.this.selection = sourceButton.getText();
            ConfirmDialog.this.hide();
        };
        
        // YES, NO, AND CANCEL BUTTONS
        yesButton = new Button(YES);
        noButton = new Button(NO);
	okButton = new Button(OK);
        cancelButton = new Button(CANCEL);
        yesButton.setOnAction(handler);
        noButton.setOnAction(handler);
	okButton.setOnAction(handler);
        cancelButton.setOnAction(handler);

        // NOW ORGANIZE OUR BUTTONS
        buttonBox = new HBox();
	
	// WE'LL ADD THE BUTTONS AS NEEDED
        
        // WE'LL PUT EVERYTHING HERE
        messagePane = new VBox();
        messagePane.setAlignment(Pos.CENTER);
        messagePane.getChildren().add(messageLabel);
        messagePane.getChildren().add(buttonBox);
        
        // MAKE IT LOOK NICE
        messagePane.setPadding(new Insets(10, 20, 20, 20));
        messagePane.setSpacing(10);

        // AND PUT IT IN THE WINDOW
        messageScene = new Scene(messagePane);
        this.setScene(messageScene);
    }

    /**
     * Accessor method for getting the selection the user made.
     * 
     * @return Either YES, NO, or CANCEL, depending on which
     * button the user selected when this dialog was presented.
     */
    public String getSelection() {
        return selection;
    }
 
    /**
     * This method loads a custom message into the label and
     * then pops open the dialog.
     * 
     * @param message Message to appear inside the dialog.
     */
    public String showOkCancel(String title, String message) {
	selection = CANCEL;
	setTitle(title);
        messageLabel.setText(message);
	buttonBox.getChildren().clear();
	buttonBox.getChildren().addAll(okButton, cancelButton);
        this.showAndWait();
	return selection;
    }
    
    public String showYesNoCancel(String title, String message) {
	selection = CANCEL;
	setTitle(title);
	messageLabel.setText(message);
	buttonBox.getChildren().clear();
	buttonBox.getChildren().addAll(yesButton, noButton, cancelButton);
	this.showAndWait();
	return selection;
    }
}