package regio_vinco;

import audio_manager.AudioManager;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import pacg.PointAndClickGame;
import static regio_vinco.RegioVinco.*;
import static regio_vinco.RegioVincoDataModel.makeColor;
import world_data.Region;
import world_data.RegionType;
import world_data.WorldDataManager;

/**
 * This class is a concrete PointAndClickGame, as specified in The PACG
 * Framework. Note that this one plays Regio Vinco.
 *
 * @author McKillaGorilla
 */
public class RegioVincoGame extends PointAndClickGame {

    // THIS PROVIDES GAME AND GUI EVENT RESPONSES
    RegioVincoController controller;
    WorldDataManager world;

    // THIS PROVIDES MUSIC AND SOUND EFFECTS
    AudioManager audio;
    
    // THESE ARE THE GUI LAYERS
    Pane backgroundLayer;
    Pane gameLayer;
    Pane guiLayer;
    Pane splashLayer;
    Pane helpLayer;
    Pane navigationLayer;
    Pane settingsLayer;
    
    //STUFF REQUIRED FOR THE WIN SCREEN
    Pane winLayer;
    Label region;
    Label score;
    Label gameDuration;
    Label subRegions;
    Label winScreenIncorrectGuesses;
    Label congrats;
    ImageView winView;
    ImageView winFlag;
    Label noFlag;

    //LABELS ON THE BOTTOM OF THE SCREEN USED TO CALCULATE SCORE
    Label provinces;
    Label regionsFound;
    Label regionsLeft;
    Label incorrectGuesses;
    
    Label regionTitle;
    Button worldNode;
    Button continentNode;
    Button nationNode;
    ArrayList<Button> ancestorRegions;
    
    //FOR MUSIC AND SOUND ON/OFF
    boolean musicOn;
    boolean soundsOn;
    
    String currentRegion;
    String currentRegionPath;
    String continentRegion;
    Label highestScore;
    Label fastestTime;
    Label fewestGuesses;
    Label highestScoreMO;
    Label fastestTimeMO;
    Label fewestGuessesMO;
    Scanner fileReader;
    
    //FOR MOUSE-OVER STUFF
    Label regionMOLabel;
    Label regionFlagImage;
    Label capitalMode;
    Label flagMode;
    Label nameMode;
    Label leaderMode;
    
    boolean gameOn = false;
    String currentGameMode;
    
    FileWriter fw;
    BufferedWriter bw;
    /**
     * Get the game setup.
     */
    public RegioVincoGame(Stage initWindow, WorldDataManager initWorld) {
	super(initWindow, APP_TITLE, TARGET_FRAME_RATE);
        initWindow.setMaxWidth(GAME_WIDTH);
        initWindow.setMaxHeight(GAME_HEIGHT);
	initAudio();
        world = initWorld;
        regionTitle = new Label();
        regionTitle.setLayoutX(TITLE_LABEL_X);
        regionTitle.setLayoutY(TITLE_LABEL_Y);
        regionTitle.setFont(new Font("Verdana", 40));
        regionTitle.setStyle("-fx-text-fill: white;");
        navigationLayer.getChildren().add(regionTitle);
        currentRegion = "The World";
        soundsOn = true;
    }
    
    public String getCurrentRegionPath(){
        return currentRegionPath;
    }
    
    public boolean getGameOn(){
        return gameOn;
    }
    
    public boolean getSoundsOn(){
        return soundsOn;
    }
    
    public AudioManager getAudio() {
	return audio;
    }
    
    public Pane getGameLayer() {
	return gameLayer;
    }
    
    public Label getRegionsFound(){
        return regionsFound;
    }
    
    public Label getRegionsLeft(){
        return regionsLeft;
    }
    
    public Label getIncorrectGuesses(){
        return incorrectGuesses;
    }
    
    public WorldDataManager getWorldDataManager(){
        return world;
    }
    
    public Button getWorldNode(){
        return worldNode;
    }
    
    public Button getContinentNode(){
        return continentNode;
    }
    
    public Button getNationNode(){
        return nationNode;
    }
    
    public String getCurrentRegion(){
        return currentRegion;
    }
    
    public void setCurrentRegion(String currentRegion){
        this.currentRegion = currentRegion;
    }

    /**
     * Initializes audio for the game.
     */
    private void initAudio() {
	audio = new AudioManager();
	try {
	    audio.loadAudio(TRACKED_SONG, TRACKED_FILE_NAME);
	    audio.play(TRACKED_SONG, true);
            musicOn = true;

	    audio.loadAudio(AFGHAN_ANTHEM, AFGHAN_ANTHEM_FILE_NAME);
	    audio.loadAudio(SUCCESS, SUCCESS_FILE_NAME);
	    audio.loadAudio(FAILURE, FAILURE_FILE_NAME);
	} catch (Exception e) {
	    
	}
    }

    // OVERRIDDEN METHODS - REGIO VINCO IMPLEMENTATIONS
    // initData
    // initGUIControls
    // initGUIHandlers
    // reset
    // updateGUI
    /**
     * Initializes the complete data model for this application, forcing the
     * setting of all game data, including all needed SpriteType objects.
     */
    @Override
    public void initData() {
	// INIT OUR DATA MANAGER
	data = new RegioVincoDataModel();
	data.setGameDimensions(GAME_WIDTH, GAME_HEIGHT);

	boundaryLeft = 0;
	boundaryRight = GAME_WIDTH;
	boundaryTop = 0;
	boundaryBottom = GAME_HEIGHT;
    }

    /**
     * For initializing all GUI controls, specifically all the buttons and
     * decor. Note that this method must construct the canvas with its custom
     * renderer.
     */
    @Override
    public void initGUIControls() {
	// LOAD THE GUI IMAGES, WHICH INCLUDES THE BUTTONS
	// THESE WILL BE ON SCREEN AT ALL TIMES
	backgroundLayer = new Pane();
	addStackPaneLayer(backgroundLayer);
	addGUIImage(backgroundLayer, BACKGROUND_TYPE, loadImage(BACKGROUND_FILE_PATH), BACKGROUND_X, BACKGROUND_Y);
	
	
	// THEN THE GAME LAYER
	gameLayer = new Pane();
	addStackPaneLayer(gameLayer);
        gameLayer.setVisible(false);
	
	// THEN THE GUI LAYER
	guiLayer = new Pane();
	addStackPaneLayer(guiLayer);
        addGUIImage(guiLayer, TITLE_TYPE, loadImage(TITLE_FILE_PATH), TITLE_X, TITLE_Y);
//	Button startButton = addGUIButton(guiLayer, START_TYPE, loadImage(START_BUTTON_FILE_PATH), START_X - 4, START_Y);
//        startButton.setStyle("-fx-background-color: black;");
//	Button exitButton = addGUIButton(guiLayer, EXIT_TYPE, loadImage(EXIT_BUTTON_FILE_PATH), EXIT_X - 12, EXIT_Y);
//        exitButton.setStyle("-fx-background-color: black;");
        provinces = new Label(PROVINCES);
        provinces.setFont(new Font("Verdana", 20));
        provinces.setLayoutX(900);
        provinces.setLayoutY(150);
        provinces.setStyle("-fx-background-color: black;" + "-fx-text-fill: yellow;");
        provinces.setPrefSize(300,50);
        guiLayer.getChildren().add(provinces);
        provinces.setVisible(false);
        regionsFound = new Label(REGIONS_FOUND + ((RegioVincoDataModel)data).getRegionsFound());
        regionsFound.setFont(new Font("Verdana", 20));
        regionsFound.setStyle("-fx-background-color: black;" + "-fx-text-fill: orange;");
        regionsFound.setLayoutX(100);
        regionsFound.setLayoutY(630);
        regionsFound.setVisible(true);
        regionsLeft = new Label(REGIONS_LEFT + ((RegioVincoDataModel)data).getRegionsNotFound());
        regionsLeft.setFont(new Font("Verdana", 20));
        regionsLeft.setStyle("-fx-background-color: black;" + "-fx-text-fill: orange;");
        regionsLeft.setLayoutX(300);
        regionsLeft.setLayoutY(630);
        regionsLeft.setVisible(true);
        incorrectGuesses = new Label(INCORRECT_GUESSES + ((RegioVincoDataModel)data).getNumIncorrectGuesses());
        incorrectGuesses.setFont(new Font("Verdana", 20));
        incorrectGuesses.setStyle("-fx-backgroudn-color: black;" + "-fx-text-fill: orange;");
        incorrectGuesses.setLayoutX(500);
        incorrectGuesses.setLayoutY(630);
        incorrectGuesses.setVisible(true);
        guiLayer.setVisible(false);
        
        helpLayer = new Pane();
        addStackPaneLayer(helpLayer);
        addGUIImage(helpLayer, TITLE_TYPE, loadImage(TITLE_FILE_PATH), TITLE_X, TITLE_Y);
        helpLayer.setStyle("-fx-background-color: black");
        Button helpSettingsButton = addGUIButton(helpLayer, SETTINGS_ICON_TYPE_2, loadImage(SETTINGS_ICON_FILE_PATH), SETTINGS_X, SETTINGS_Y);
        helpSettingsButton.setStyle("-fx-background-color: transparent;");
        Button helpGlobeButton = addGUIButton(helpLayer, GLOBE_ICON_TYPE_2, loadImage(GLOBE_ICON_FILE_PATH), SETTINGS_X + 75, SETTINGS_Y);
        helpGlobeButton.setStyle("-fx-background-color: transparent;");
        Text helpText = new Text();
        Text helpTextTitle = new Text();
        Text name = new Text();
        name.setText("Charles Chen");
        helpTextTitle.setText("Regio Vinco!");
        helpText.setText("The Region Vinco! game is a game for learning about the world and its geography. Through playing the game, "
                + "the player can practice their knowledge of continents, nations, states, and province borders, as well as "
                + "capitals, flags, and leaders. \n\nThe game lets the user select the map of their choice to play and will keep track "
                + "of player accomplishments for all provided played maps.");
        helpText.setWrappingWidth(700);
        helpText.setLayoutX(100);
        helpText.setLayoutY(150);
        helpText.setFont(new Font("Verdana", 20));
        helpText.setStyle("-fx-fill: white;");
        helpTextTitle.setLayoutX(365);
        helpTextTitle.setLayoutY(100);
        helpTextTitle.setFont(new Font("Verdana", 25));
        helpTextTitle.setStyle("-fx-fill: white;");
        name.setLayoutX(650);
        name.setLayoutY(350);
        name.setFont(new Font("Verdana", 20));
        name.setStyle("-fx-fill: white;");
        helpLayer.getChildren().addAll(helpText, helpTextTitle, name);
        helpLayer.setVisible(false);
        
        navigationLayer = new Pane();
        addStackPaneLayer(navigationLayer);
        Label cover = new Label();
        cover.setStyle("-fx-background-color: black;");
        cover.setLayoutX(GAME_X);
        cover.setLayoutY(0);
        cover.setPrefSize(300, 250);
        navigationLayer.getChildren().add(cover);
//        addGUIImage(navigationLayer, NAVIGATION_TYPE, loadImage(NAVIGATION_FILE_PATH), BACKGROUND_X, BACKGROUND_Y);
        addGUIImage(navigationLayer, TITLE_TYPE, loadImage(TITLE_FILE_PATH), TITLE_X, TITLE_Y);
        Button settingsButton = addGUIButton(navigationLayer, SETTINGS_ICON_TYPE, loadImage(SETTINGS_ICON_FILE_PATH), SETTINGS_X, SETTINGS_Y);
        settingsButton.setStyle("-fx-background-color: transparent;");
        Button helpButton = addGUIButton(navigationLayer, HELP_ICON_TYPE, loadImage(HELP_ICON_FILE_PATH), SETTINGS_X + 75, SETTINGS_Y);
        helpButton.setStyle("-fx-background-color: transparent;");
        Button capitalButton = addGUIButton(navigationLayer, CAPITAL_TYPE, loadImage(CAPITAL_FILE_PATH), GAME_X, GAME_Y);
        capitalButton.setStyle("-fx-background-color: transparent;");
        capitalMode = new Label("Capital Mode");
        capitalMode.setStyle("-fx-background-color: transparent;" + "-fx-text-fill: white;");
        capitalMode.setFont(new Font("Verdana", 15));
        capitalMode.setLayoutX(GAME_X);
        capitalMode.setLayoutY(GAME_Y + 55);
        capitalMode.setVisible(false);
        Button flagButton = addGUIButton(navigationLayer, FLAG_TYPE, loadImage(FLAG_FILE_PATH), GAME_X + 55, GAME_Y);
        flagButton.setStyle("-fx-background-color: transparent;");
        flagMode = new Label("Flag Mode");
        flagMode.setStyle("-fx-background-color: transparent;" + "-fx-text-fill: white;");
        flagMode.setFont(new Font("Verdana", 15));
        flagMode.setLayoutX(GAME_X + 50);
        flagMode.setLayoutY(GAME_Y + 55);
        flagMode.setVisible(false);
        Button nameButton = addGUIButton(navigationLayer, NAME_TYPE, loadImage(NAME_FILE_PATH), GAME_X + 110, GAME_Y);
        nameButton.setStyle("-fx-background-color: transparent;");
        nameMode = new Label("Name Mode");
        nameMode.setStyle("-fx-background-color: transparent;" + "-fx-text-fill: white;");
        nameMode.setFont(new Font("Verdana", 15));
        nameMode.setLayoutX(GAME_X + 100);
        nameMode.setLayoutY(GAME_Y + 55);
        nameMode.setVisible(false);
        Button leaderButton = addGUIButton(navigationLayer, LEADER_TYPE, loadImage(LEADER_FILE_PATH), GAME_X + 165, GAME_Y);
        leaderButton.setStyle("-fx-background-color: transparent;");
        leaderMode = new Label("Leader Mode");
        leaderMode.setStyle("-fx-background-color: transparent;" + "-fx-text-fill: white;");
        leaderMode.setFont(new Font("Verdana", 15));
        leaderMode.setLayoutX(GAME_X + 150);
        leaderMode.setLayoutY(GAME_Y + 55);
        leaderMode.setVisible(false);
        Button stopGameButton = addGUIButton(navigationLayer, STOP_GAME_TYPE, loadImage(STOP_GAME_FILE_PATH), GAME_X + 220, GAME_Y);
        stopGameButton.setStyle("-fx-background-color: transparent");
        stopGameButton.setDisable(true);
        navigationLayer.getChildren().addAll(capitalMode, flagMode, nameMode, leaderMode);
        
        // NOTE THAT THE MAP IS ALSO AN IMAGE, BUT
	// WE'LL LOAD THAT WHEN A GAME STARTS, SINCE
	// WE'LL BE CHANGING THE PIXELS EACH TIME
	// FOR NOW WE'LL JUST LOAD THE ImageView
	// THAT WILL STORE THAT IMAGE
	ImageView mapView = new ImageView();
	mapView.setX(MAP_X);
	mapView.setY(MAP_Y);
	getGuiImages().put(MAP_TYPE, mapView);
	navigationLayer.getChildren().add(mapView);
        worldNode = new Button("World");
        worldNode.setStyle("-fx-background-color: transparent;" + "-fx-text-fill: white;");
        worldNode.setFont(new Font("Verdana", 20));
        worldNode.setLayoutX(0);
        worldNode.setLayoutY(600);
        worldNode.setPrefHeight(100);
        continentNode = new Button();
        continentNode.setStyle("-fx-background-color: transparent;" + "-fx-text-fill: white;");
        continentNode.setFont(new Font("Verdana", 20));
        continentNode.setPrefHeight(100);
        continentNode.setLayoutX(150);
        continentNode.setLayoutY(600);
        continentNode.setVisible(false);
        nationNode = new Button();
        nationNode.setStyle("-fx-background-color: transparent;" + "-fx-text-fill: white;");
        nationNode.setFont(new Font("Verdana", 20));
        nationNode.setPrefHeight(100);
        nationNode.setLayoutX(300);
        nationNode.setLayoutY(600);
        nationNode.setVisible(false);
        regionMOLabel = new Label("");
        regionMOLabel.setStyle("-fx-background-color: transparent;" + "-fx-text-fill: white;");
        regionMOLabel.setFont(new Font("Verdana", 20));
        regionMOLabel.setLayoutX(900);
        regionMOLabel.setLayoutY(250);
        regionFlagImage = new Label();
        regionFlagImage.setLayoutX(900);
        regionFlagImage.setLayoutY(350);
        highestScore = new Label("Highest Score: ");
        highestScore.setStyle("-fx-background-color: transparent;" + "-fx-text-fill: white;");
        highestScore.setFont(new Font("Verdana", 20));
        highestScore.setLayoutX(12);
        highestScore.setLayoutY(605);
        fastestTime = new Label("Fastest Time: ");
        fastestTime.setStyle("-fx-background-color: transparent;" + "-fx-text-fill: white;");
        fastestTime.setFont(new Font("Verdana", 20));
        fastestTime.setLayoutX(312);
        fastestTime.setLayoutY(605);
        fewestGuesses = new Label("Fewest Guesses: ");
        fewestGuesses.setStyle("-fx-background-color: transparent;" + "-fx-text-fill: white;");
        fewestGuesses.setFont(new Font("Verdana", 20));
        fewestGuesses.setLayoutX(612);
        fewestGuesses.setLayoutY(605);
        highestScoreMO = new Label();
        highestScoreMO.setStyle("-fx-background-color: transparent;" + "-fx-text-fill: white;");
        highestScoreMO.setFont(new Font("Verdana", 20));
        highestScoreMO.setLayoutX(900);
        highestScoreMO.setLayoutY(275);
        highestScoreMO.setVisible(false);
        fastestTimeMO = new Label();
        fastestTimeMO.setStyle("-fx-background-color: transparent;" + "-fx-text-fill: white;");
        fastestTimeMO.setFont(new Font("Verdana", 20));
        fastestTimeMO.setLayoutX(900);
        fastestTimeMO.setLayoutY(295);
        fastestTimeMO.setVisible(false);
        fewestGuessesMO = new Label();
        fewestGuessesMO.setStyle("-fx-background-color: transparent;" + "-fx-text-fill: white;");
        fewestGuessesMO.setFont(new Font("Verdana", 20));
        fewestGuessesMO.setLayoutX(900);
        fewestGuessesMO.setLayoutY(315);
        fewestGuessesMO.setVisible(false);
        navigationLayer.getChildren().addAll(worldNode, continentNode, nationNode, regionMOLabel, regionFlagImage, highestScore, fastestTime, fewestGuesses, highestScoreMO, fastestTimeMO, fewestGuessesMO);
        
        splashLayer = new Pane();
	addStackPaneLayer(splashLayer);
	addGUIImage(splashLayer, SPLASH_BACKGROUND_TYPE, loadImage(SPLASH_FILE_PATH), BACKGROUND_X, BACKGROUND_Y);
        Button enterButton = addGUIButton(splashLayer, MCKENNA_TYPE, loadImage(MCKENNA_FILE_PATH), 200, STACK_INIT_Y - 300);
        Button enterButton2 = addGUIButton(splashLayer, MCKENNA_TYPE_2, loadImage(MCKENNA_FILE_PATH), 900, STACK_INIT_Y - 300);
        enterButton.setStyle("-fx-background-color: transparent;");
        enterButton2.setStyle("-fx-background-color: transparent;");
        Label enter = new Label();
        ImageView gameLabel = new ImageView(loadImage(GUI_PATH + "SplashScreenTitle.png"));
        splashLayer.getChildren().addAll(enter, gameLabel);
        enter.setText("Click on McKenna to enter");
        enter.setFont(new Font("Sans Serif", 35));
        enter.setStyle("-fx-background-color: transparent;" + "-fx-text-fill: goldenrod;" + "-fx-font-weight: bold;");
        enter.setLayoutX(400);
        enter.setLayoutY(620);
        enter.setVisible(true);
        gameLabel.setX(520);
        gameLabel.setY(30);
        
        settingsLayer = new Pane();
        addStackPaneLayer(settingsLayer);
        settingsLayer.setStyle("-fx-background-color: black");
        addGUIImage(settingsLayer, TITLE_TYPE, loadImage(TITLE_FILE_PATH), TITLE_X, TITLE_Y);
        Button settingsGlobeButton = addGUIButton(settingsLayer, GLOBE_ICON_TYPE, loadImage(GLOBE_ICON_FILE_PATH), SETTINGS_X, SETTINGS_Y);
        settingsGlobeButton.setStyle("-fx-background-color: transparent;");
        Button settingsHelpButton = addGUIButton(settingsLayer, HELP_ICON_TYPE_2, loadImage(HELP_ICON_FILE_PATH), SETTINGS_X + 75, SETTINGS_Y);
        settingsHelpButton.setStyle("-fx-background-color: transparent;");
        Button soundOnButton = addGUIButton(settingsLayer, SOUND_ON_TYPE, loadImage(SOUND_ON_FILE_PATH), 350, 200);
        Button soundOffButton = addGUIButton(settingsLayer, SOUND_OFF_TYPE, loadImage(SOUND_OFF_FILE_PATH), 450, 200);
        soundOnButton.setStyle("-fx-background-color: transparent;");
        soundOffButton.setStyle("-fx-background-color:transparent;");
        Button playButton = addGUIButton(settingsLayer, PLAY_TYPE, loadImage(PLAY_FILE_PATH), 350, 400);
        Button stopButton = addGUIButton(settingsLayer, STOP_TYPE, loadImage(STOP_FILE_PATH), 450, 400);
        playButton.setStyle("-fx-background-color: transparent;");
        stopButton.setStyle("-fx-background-color: transparent;");
        Label musicTitle = new Label("Music On/Off");
        musicTitle.setStyle("-fx-background-color: transparent;" + "-fx-text-fill: white;" + "-fx-font-weight: bold;");
        musicTitle.setFont(new Font("Verdana", 25));
        musicTitle.setLayoutX(373);
        musicTitle.setLayoutY(370);
        Label soundTitle = new Label("Sound On/Off");
        soundTitle.setStyle("-fx-background-color: transparent;" + "-fx-text-fill: white;" + "-fx-font-weight: bold;");
        soundTitle.setFont(new Font("Verdana", 25));
        soundTitle.setLayoutX(370);
        soundTitle.setLayoutY(170);
        Label settingsTitle = new Label("Regio Vinco! Settings");
        settingsTitle.setStyle("-fx-background-color: transparent;" + "-fx-text-fill: white;" + "-fx-font-weight: bold;");
        settingsTitle.setFont(new Font("Verdana", 40));
        settingsTitle.setLayoutX(250);
        settingsTitle.setLayoutY(100);
        settingsLayer.getChildren().addAll(soundTitle, musicTitle, settingsTitle);
        settingsLayer.setVisible(false);
        
        // NOW LOAD THE WIN DISPLAY, WHICH WE'LL ONLY
	// MAKE VISIBLE AND ENABLED AS NEEDED
        winLayer = new Pane();
        addStackPaneLayer(winLayer);
        winLayer.setMaxSize(500, 400);
        winLayer.setLayoutX(WIN_X);
        winLayer.setLayoutY(WIN_Y);
        winView = new ImageView(loadImage("./data/gui/RegioVincoWinDisplay.png"));
        winView.setLayoutX(0);
        winView.setLayoutY(0);
        winFlag = new ImageView();
        winFlag.setLayoutX(25);
        winFlag.setLayoutY(25);
        winLayer.getChildren().addAll(winView, winFlag);
        region = new Label("Region: ");
        region.setFont(new Font("Verdana", 20));
        region.setStyle("-fx-text-fill: navy;" + "-fx-font-weight: bold;" + "-fx-background-color: transparent;");
        score = new Label();
        score.setFont(new Font("Verdana", 20));
        score.setStyle("-fx-text-fill: navy;" + "-fx-font-weight: bold;" + "-fx-background-color: transparent;");
        gameDuration = new Label();
        gameDuration.setFont(new Font("Verdana", 20));
        gameDuration.setStyle("-fx-text-fill: navy;" + "-fx-font-weight: bold;" + "-fx-background-color: transparent;");
        subRegions = new Label();
        subRegions.setFont(new Font("Verdana", 20));
        subRegions.setStyle("-fx-text-fill: navy;" + "-fx-font-weight: bold;" + "-fx-background-color: transparent;");
        winScreenIncorrectGuesses = new Label();
        winScreenIncorrectGuesses.setFont(new Font("Verdana", 20));
        winScreenIncorrectGuesses.setStyle("-fx-text-fill: navy;" + "-fx-font-weight: bold;"+ "-fx-background-color: transparent;");
        congrats = new Label();
        congrats.setStyle("-fx-background-color: transparent;" + "-fx-text-fill: navy;" + "-fx-font-weight: bold;");
        congrats.setFont(new Font("Verdana", 20));
        congrats.setLayoutX(275);
        congrats.setLayoutY(30);
        winLayer.getChildren().addAll(region, score, gameDuration, subRegions, winScreenIncorrectGuesses, congrats);
        region.setLayoutX(50);
        region.setLayoutY(175);
        region.setVisible(true);
        score.setLayoutX(50);
        score.setLayoutY(205);
        score.setVisible(true);
        gameDuration.setLayoutX(50);
        gameDuration.setLayoutY(235);
        gameDuration.setVisible(true);
        subRegions.setLayoutX(50);
        subRegions.setLayoutY(265);
        subRegions.setVisible(true);
        winScreenIncorrectGuesses.setLayoutX(50);
        winScreenIncorrectGuesses.setLayoutY(295);
        winScreenIncorrectGuesses.setVisible(true);
        winLayer.setVisible(false);
    }
    
    // HELPER METHOD FOR LOADING IMAGES
    public Image loadImage(String imagePath) {	
        File file = new File(imagePath);
        if(!file.exists()){
//            System.out.println("no img");
            return null;
        }
	Image img = new Image("file:" + imagePath);
	return img;
    }

    /**
     * For initializing all the button handlers for the GUI.
     */
    @Override
    public void initGUIHandlers() {
	controller = new RegioVincoController(this);

//	Button startButton = guiButtons.get(START_TYPE);
//	startButton.setOnAction(e -> {
//	    controller.processStartGameRequest();
//            provinces.setVisible(true);
//            regionsFound.setVisible(true);
//            winLayer.setVisible(false);
//            updateLabels();
//	});
//
//	Button exitButton = guiButtons.get(EXIT_TYPE);
//	exitButton.setOnAction(e -> {
//	    controller.processExitGameRequest();
//	});

	// MAKE THE CONTROLLER THE HOOK FOR KEY PRESSES
	keyController.setHook(controller);
        
        //ENTER BUTTON ON THE HOME SCREEN
        Button enterButton = getGuiButtons().get(MCKENNA_TYPE);
        Button enterButton2 = getGuiButtons().get(MCKENNA_TYPE_2);
        enterButton.setOnMouseEntered(e -> {
            enterButton.setGraphic(new ImageView(loadImage(MCKENNASMILE_FILE_PATH)));
            enterButton2.setGraphic(new ImageView(loadImage(MCKENNASMILE_FILE_PATH)));
        });
        enterButton.setOnMouseExited(e -> {
            enterButton.setGraphic(new ImageView(loadImage(MCKENNA_FILE_PATH)));
            enterButton2.setGraphic(new ImageView(loadImage(MCKENNA_FILE_PATH)));
        });
        enterButton2.setOnMouseEntered(e -> {
            enterButton.setGraphic(new ImageView(loadImage(MCKENNASMILE_FILE_PATH)));
            enterButton2.setGraphic(new ImageView(loadImage(MCKENNASMILE_FILE_PATH)));
        });
        enterButton2.setOnMouseExited(e -> {
            enterButton.setGraphic(new ImageView(loadImage(MCKENNA_FILE_PATH)));
            enterButton2.setGraphic(new ImageView(loadImage(MCKENNA_FILE_PATH)));
        });
        enterButton.setOnAction(e ->{
            splashLayer.setVisible(false);
            reloadMap("The World");
        });
        enterButton2.setOnAction(e -> {
            splashLayer.setVisible(false);
            reloadMap("The World");
        });
        
        //SETTINGS BUTTON ON THE NAVIGATION SCREEN
        Button settingsButton = getGuiButtons().get(SETTINGS_ICON_TYPE);
        settingsButton.setOnAction(e -> {
            settingsLayer.setVisible(true);
            navigationLayer.setVisible(false);
        });
        settingsButton.setOnMouseEntered(e -> {
            settingsButton.setGraphic(new ImageView(loadImage(SETTINGS_ICON_MO_FILE_PATH)));
        });
        settingsButton.setOnMouseExited(e -> {
            settingsButton.setGraphic(new ImageView(loadImage(SETTINGS_ICON_FILE_PATH)));
        });
        
        //GLOBE BUTTON ON THE SETTINGS SCREEN
        Button settingsGlobeButton = getGuiButtons().get(GLOBE_ICON_TYPE);
        settingsGlobeButton.setOnAction(e -> {
            settingsLayer.setVisible(false);
            navigationLayer.setVisible(true);
        });
        settingsGlobeButton.setOnMouseEntered(e -> {
            settingsGlobeButton.setGraphic(new ImageView(loadImage(GLOBE_ICON_MO_FILE_PATH)));
        });
        settingsGlobeButton.setOnMouseExited(e -> {
            settingsGlobeButton.setGraphic(new ImageView(loadImage(GLOBE_ICON_FILE_PATH)));
        });
        
        //HELP BUTTON ON THE NAVIGATION SCREEN
        Button helpButton = getGuiButtons().get(HELP_ICON_TYPE);
        helpButton.setOnAction(e -> {
            helpLayer.setVisible(true);
            navigationLayer.setVisible(false);
        });
        helpButton.setOnMouseEntered(e -> {
            helpButton.setGraphic(new ImageView(loadImage(HELP_ICON_MO_FILE_PATH)));
        });
        helpButton.setOnMouseExited(e -> {
            helpButton.setGraphic(new ImageView(loadImage(HELP_ICON_FILE_PATH)));
        });
        
        //HELP BUTTON ON THE SETTINGS SCREEN
        Button settingsHelpButton = getGuiButtons().get(HELP_ICON_TYPE_2);
        settingsHelpButton.setOnAction(e -> {
            settingsLayer.setVisible(false);
            helpLayer.setVisible(true);
        });
        settingsHelpButton.setOnMouseEntered(e -> {
            settingsHelpButton.setGraphic(new ImageView(loadImage(HELP_ICON_MO_FILE_PATH)));
        });
        settingsHelpButton.setOnMouseExited(e -> {
            settingsHelpButton.setGraphic(new ImageView(loadImage(HELP_ICON_FILE_PATH)));
        });
        
        //SETTINGS BUTTON ON HELP SCREEN
        Button helpSettingsButton = getGuiButtons().get(SETTINGS_ICON_TYPE_2);
        helpSettingsButton.setOnAction(e -> {
            helpLayer.setVisible(false);
            settingsLayer.setVisible(true);
        });
        helpSettingsButton.setOnMouseEntered(e -> {
            helpSettingsButton.setGraphic(new ImageView(loadImage(SETTINGS_ICON_MO_FILE_PATH)));
        });
        helpSettingsButton.setOnMouseExited(e -> {
            helpSettingsButton.setGraphic(new ImageView(loadImage(SETTINGS_ICON_FILE_PATH)));
        });
        
        //GLOBE BUTTON ON HELP SCREEN
        Button helpGlobeButton = getGuiButtons().get(GLOBE_ICON_TYPE_2);
        helpGlobeButton.setOnAction(e -> {
            helpLayer.setVisible(false);
            navigationLayer.setVisible(true);
        });
        helpGlobeButton.setOnMouseEntered(e -> {
            helpGlobeButton.setGraphic(new ImageView(loadImage(GLOBE_ICON_MO_FILE_PATH)));
        });
        helpGlobeButton.setOnMouseExited(e -> {
            helpGlobeButton.setGraphic(new ImageView(loadImage(GLOBE_ICON_FILE_PATH)));
        });
        
        //SOUND ON BUTTON ON SETTINGS SCREEN
        Button soundOnButton = getGuiButtons().get(SOUND_ON_TYPE);
        soundOnButton.setOnAction(e -> {
            soundsOn = true;
        });
        soundOnButton.setOnMouseEntered(e -> {
            soundOnButton.setGraphic(new ImageView(loadImage(SOUND_ON_MO_FILE_PATH)));
        });
        soundOnButton.setOnMouseExited(e -> {
            soundOnButton.setGraphic(new ImageView(loadImage(SOUND_ON_FILE_PATH)));
        });
        
        //SOUND OFF BUTTON ON SETTINGS SCREEN
        Button soundOffButton = getGuiButtons().get(SOUND_OFF_TYPE);
        soundOffButton.setOnAction(e -> {
            soundsOn = false;
        });
        soundOffButton.setOnMouseEntered(e -> {
            soundOffButton.setGraphic(new ImageView(loadImage(SOUND_OFF_MO_FILE_PATH)));
        });
        soundOffButton.setOnMouseExited(e -> {
            soundOffButton.setGraphic(new ImageView(loadImage(SOUND_OFF_FILE_PATH)));
        });
        
        //PLAY BUTTON ON SETTINGS SCREEN
        Button playButton = getGuiButtons().get(PLAY_TYPE);
        playButton.setOnAction(e -> {
            if(!musicOn){
                if(!audio.isPlaying(TRACKED_SONG)){
                    audio.play(TRACKED_SONG, true);
                    musicOn = true;
                }
            }
        });
        playButton.setOnMouseEntered(e -> {
            playButton.setGraphic(new ImageView(loadImage(PLAY_MO_FILE_PATH)));
        });
        playButton.setOnMouseExited(e -> {
            playButton.setGraphic(new ImageView(loadImage(PLAY_FILE_PATH)));
        });
        
        //STOP BUTTON ON SETTINGS SCREEN
        Button stopButton = getGuiButtons().get(STOP_TYPE);
        stopButton.setOnAction(e -> {
            if(musicOn){
                if(audio.isPlaying(TRACKED_SONG)){ //WHEN UP TO ANTHEM YOU WILL GET ISSUES
                    audio.stop(TRACKED_SONG);
                    musicOn = false;
                }
            }
        });
        stopButton.setOnMouseEntered(e -> {
            stopButton.setGraphic(new ImageView(loadImage(STOP_MO_FILE_PATH)));
        });
        stopButton.setOnMouseExited(e -> {
            stopButton.setGraphic(new ImageView(loadImage(STOP_FILE_PATH)));
        });

	// SETUP MOUSE PRESSES ON THE MAP
	ImageView mapView = getGuiImages().get(MAP_TYPE);
	mapView.setOnMouseClicked(e -> {
            dataLock = new ReentrantLock();
            dataLock.lock();
            try{
                if(!gameOn)
                    ((RegioVincoDataModel)data).respondToNavigationMapSelection(this, (int)e.getX(), (int)e.getY());
                else{
                    controller.processMapClickRequest((int) e.getX(), (int) e.getY());
                    updateLabels();
                }
            }
            catch(Exception ex){
            }
            finally{
                dataLock.unlock();
            }
	});
        
        //CAPITAL BUTTON ON NAVIGATION SCREEN
        Button capitalButton = getGuiButtons().get(CAPITAL_TYPE);
        capitalButton.setOnAction(e -> {
            currentGameMode = "capital";
            doPrevGameStuff();
            controller.processCapitalModeRequest();
        });
        capitalButton.setOnMouseEntered(e -> {
            capitalButton.setGraphic(new ImageView(loadImage(CAPITAL_MO_FILE_PATH)));
            capitalMode.setVisible(true);
        });
        capitalButton.setOnMouseExited(e -> {
            capitalButton.setGraphic(new ImageView(loadImage(CAPITAL_FILE_PATH)));
            capitalMode.setVisible(false);
        });
        
        //FLAG BUTTON ON NAVIGATION SCREEN
        Button flagButton = getGuiButtons().get(FLAG_TYPE);
        flagButton.setOnAction(e -> {
            currentGameMode = "flag";
            doPrevGameStuff();
            controller.processFlagModeRequest();
        });
        flagButton.setOnMouseEntered(e -> {
            flagButton.setGraphic(new ImageView(loadImage(FLAG_MO_FILE_PATH)));
            flagMode.setVisible(true);
        });
        flagButton.setOnMouseExited(e -> {
            flagButton.setGraphic(new ImageView(loadImage(FLAG_FILE_PATH)));
            flagMode.setVisible(false);
        });
        
        //NAME BUTTON ON NAVIGATION SCREEN
        Button nameButton = getGuiButtons().get(NAME_TYPE);
        nameButton.setOnAction(e -> {
            currentGameMode = "name";
            doPrevGameStuff();
            gameOn = true;
            controller.processNameModeRequest();
        });
        nameButton.setOnMouseEntered(e -> {
            nameButton.setGraphic(new ImageView(loadImage(NAME_MO_FILE_PATH)));
            nameMode.setVisible(true);
        });
        nameButton.setOnMouseExited(e -> {
            nameButton.setGraphic(new ImageView(loadImage(NAME_FILE_PATH)));
            nameMode.setVisible(false);
        });
        
        //LEADER BUTTON ON NAVIGATION SCREEN
        Button leaderButton = getGuiButtons().get(LEADER_TYPE);
        leaderButton.setOnAction(e -> {
            currentGameMode = "leader";
            doPrevGameStuff();
            gameOn = true;
            controller.processNameModeRequest();
        });
        leaderButton.setOnMouseEntered(e -> {
            leaderButton.setGraphic(new ImageView(loadImage(LEADER_MO_FILE_PATH)));
            leaderMode.setVisible(true);
        });
        leaderButton.setOnMouseExited(e -> {
            leaderButton.setGraphic(new ImageView(loadImage(LEADER_FILE_PATH)));
            leaderMode.setVisible(false);
        });
        
        //STOP GAME BUTTON ON NAVIGATION SCREEN
        Button stopGameButton = getGuiButtons().get(STOP_GAME_TYPE);
        stopGameButton.setOnAction(e -> {
            ConfirmDialog cd = new ConfirmDialog(window);
            String ans = cd.showYesNoCancel("Stop Game", "Are you sure you want to stop the game?");
            if(ans.equals("Yes")){
//                continentRegion = null;
                guiLayer.setVisible(false);
                gameLayer.setVisible(false);
                winLayer.setVisible(false);
                if(musicOn && data.won()){
                    if(audio.getMidiAudio().containsKey(currentRegion + "_SONG")){
                        if(audio.isPlaying(currentRegion + "_SONG"))
                            audio.stop(currentRegion + "_SONG");
                    }
                    audio.play(TRACKED_SONG, true);
                }
                data.endGameAsLoss();
                reloadMap(currentRegion);
                gameOn = false;
                currentGameMode = "";
                getGuiButtons().get(STOP_GAME_TYPE).setDisable(true);
                worldNode.setVisible(true);
                if(world.getAllRegions().get(currentRegion).getType() == RegionType.CONTINENT || world.getAllRegions().get(currentRegion).getType() == RegionType.NATION)
                    continentNode.setVisible(true);
                if(world.getAllRegions().get(currentRegion).getType() == RegionType.NATION)
                    nationNode.setVisible(true);
                regionMOLabel.setVisible(true);
                regionFlagImage.setVisible(true);
                highestScore.setVisible(true);
                fastestTime.setVisible(true);
                fewestGuesses.setVisible(true);
                nameMode.setDisable(false);
                leaderMode.setDisable(false);
                capitalMode.setDisable(false);
                flagMode.setDisable(false);
                regionsLeft.setVisible(false);
                incorrectGuesses.setVisible(false);
                regionsFound.setVisible(false);
                ((RegioVincoDataModel)data).getTimer().setVisible(false);
            }
        });
        stopGameButton.setOnMouseEntered(e -> {
            stopGameButton.setGraphic(new ImageView(loadImage(STOP_GAME_MO_FILE_PATH)));
        });
        stopGameButton.setOnMouseExited(e -> {
            stopGameButton.setGraphic(new ImageView(loadImage(STOP_GAME_FILE_PATH)));
        });
	
	// KILL THE APP IF THE USER CLOSES THE WINDOW
	window.setOnCloseRequest(e->{
	    controller.processExitGameRequest();
	});
        
        worldNode.setOnAction(e -> {
            removePastRegions("world");
            setCurrentRegion("The World");
            reloadMap("The World");
            continentNode.setVisible(false);
            nationNode.setVisible(false);
            continentRegion = null;
        });
        continentNode.setOnAction(e -> {
//            removePastRegions("continent");
//            reloadMap(continentNode.getText());
//            setCurrentRegion(continentNode.getText());
//            nationNode.setVisible(false);
            
            //THIS COULD POSSIBLY CAUSE PROBLEMS LATER WITH THE PIXEL COLORING
            String temp = continentNode.getText();
            worldNode.fire();
            ((RegioVincoDataModel)data).resetMaps();
            setCurrentRegion(temp);
            this.reloadMap(temp);
            if(this.getWorldDataManager().getAllRegions().get(temp).getType() == RegionType.CONTINENT){
                this.getContinentNode().setText(temp);
                this.getContinentNode().setVisible(true);
            }
            else if(this.getWorldDataManager().getAllRegions().get(temp).getType() == RegionType.NATION){
                this.getNationNode().setText(temp);
                this.getNationNode().setVisible(true);
            }
//            continentRegion = null;
        });
        
        //MOUSE OVER REGIONS
        ImageView mapImages = getGuiImages().get(MAP_TYPE);
        mapImages.setOnMouseMoved(e -> {
            if(!gameOn){
                boolean isValidRegion;
                Object[] arr = ((RegioVincoDataModel)data).changeMouseOverLabels(this, (int)e.getX(), (int)e.getY());
                Region theRegion = (Region)arr[1];
                Color theColor = (Color)arr[0];
                if(theRegion == null)
                    isValidRegion = false;
                else
                    isValidRegion = true;
                if(isValidRegion){
        //          regionMOLabel.setVisible(true);
                    regionMOLabel.setText(theRegion.getName());
                    highestScoreMO.setVisible(true);
                    fastestTimeMO.setVisible(true);
                    fewestGuessesMO.setVisible(true);
                    highestScoreMO.setText("Highest Score: ");
                    fastestTimeMO.setText("Fastest Time: ");
                    fewestGuessesMO.setText("Fewest Guesses: ");
                    File scoresFile = new File(currentRegionPath + "/" + theRegion.getName() + "/" + theRegion.getName() + " Scores.txt");
                    try {
                        fileReader = new Scanner(scoresFile);
                        highestScoreMO.setText("Highest Score: " + fileReader.next());
                        fastestTimeMO.setText("Fastest Time: " + fileReader.next());
                        fewestGuessesMO.setText("Fewest Guesses: " + fileReader.next());
                    } catch (FileNotFoundException ex) {
//                        System.out.println("No file found.");
                    }
                    if(theRegion.getType() == RegionType.NATION){
                        regionFlagImage.setGraphic(new ImageView(loadImage("./data/The World/" + theRegion.getParentRegion().getName() + "/" + theRegion.getName() + "/" + theRegion.getName() + FLAGS_FILE_PATH)));
                        regionFlagImage.setVisible(true);
                    }
                }
                else if(theColor.equals(Color.PINK)){
                    regionFlagImage.setVisible(false);
                    regionMOLabel.setText("Region Not Playable");
                }
                else{
                    regionFlagImage.setVisible(false);
                    highestScoreMO.setVisible(false);
                    fastestTimeMO.setVisible(false);
                    fewestGuessesMO.setVisible(false);
                    regionMOLabel.setText("");
                }
            }
        });
    }

    /**
     * Called when a game is restarted from the beginning, it resets all game
     * data and GUI controls so that the game may start anew.
     */
    @Override
    public void reset() {
	// IF THE WIN DIALOG IS VISIBLE, MAKE IT INVISIBLE
	ImageView winView = getGuiImages().get(WIN_DISPLAY_TYPE);
	winView.setVisible(false);
        
        // AND RESET ALL GAME DATA
	((RegioVincoDataModel)data).reset(this);
    }
    
    public void reset(String gameMode) {
	// IF THE WIN DIALOG IS VISIBLE, MAKE IT INVISIBLE
//	ImageView winView = getGuiImages().get(WIN_DISPLAY_TYPE);
//	winView.setVisible(false);
        
        // AND RESET ALL GAME DATA
	((RegioVincoDataModel)data).reset(this, gameMode);
    }

    /**
     * This mutator method changes the color of the debug text.
     *
     * @param initColor Color to use for rendering debug text.
     */
    public static void setDebugTextColor(Color initColor) {
//        debugTextColor = initColor;
    }

    /**
     * Called each frame, this method updates the rendering state of all
     * relevant GUI controls, like displaying win and loss states and whether
     * certain buttons should be enabled or disabled.
     */
    int backgroundChangeCounter = 0;

    @Override
    public void updateGUI() {
	// IF THE GAME IS OVER, DISPLAY THE APPROPRIATE RESPONSE
	if (data.won()) {
            int scorePrint = 10000;
            scorePrint -= ((RegioVincoDataModel)data).getGameDurationSeconds();
            scorePrint -= ((RegioVincoDataModel)data).getNumIncorrectGuesses()*100;
            if(scorePrint < 0)
                scorePrint = 0;
            File scoreFile = new File(currentRegionPath + currentRegion + SCORE_FILE_PATH);
            try {
                fileReader = new Scanner(scoreFile);
            } catch (FileNotFoundException ex) {
                System.out.println("The file doesn't exist?!");
            }
            if(scorePrint > Integer.parseInt(fileReader.next())){
                try {
                    scoreFile.createNewFile();
                    fw = new FileWriter(scoreFile.getAbsoluteFile());
                    bw = new BufferedWriter(fw);
                    String scoresToAdd = scorePrint + " " + ((RegioVincoDataModel)data).getGameDurationString() + " " + ((RegioVincoDataModel)data).getNumIncorrectGuesses();
                    bw.write(scoresToAdd);
                    bw.close();
//                    System.out.println("finished writing.");
                } catch (IOException ex) {
                }
            }
            regionsFound.setVisible(false);
            regionsLeft.setVisible(false);
            incorrectGuesses.setVisible(false);
            provinces.setVisible(false);
            ((RegioVincoDataModel)data).getTimer().setVisible(false);
            region.setText("Region: " + currentRegion);
            score.setText(WIN_SCREEN_SCORE + scorePrint);
            gameDuration.setText(WIN_SCREEN_GAME_DURATION + ((RegioVincoDataModel)data).getGameDurationString());
            subRegions.setText(WIN_SCREEN_SUBREGIONS + ((RegioVincoDataModel)data).getRegionsFound());
            winScreenIncorrectGuesses.setText(INCORRECT_GUESSES + ((RegioVincoDataModel)data).getNumIncorrectGuesses());
            congrats.setText(WIN_SCREEN_CONGRATS + currentRegion + "!");
            try{
                winFlag.setImage(loadImage(currentRegionPath + currentRegion + FLAGS_FILE_PATH));
            }catch(Exception ex){
            }
            winLayer.setVisible(true);
	}
    }

    public void reloadMap(String regionName) {
        String path;
        File scores;
        if(!gameOn){
            if(regionName.equals("The World"))
                path = DATA_PATH + "The World/";
            else if(continentRegion == null)
                path = DATA_PATH + "The World/" + regionName + "/";
            else
                path = DATA_PATH + "The World/" + continentRegion + "/" + regionName + "/";
        }
        else{
            if(regionName.equals("The World"))
                path = DATA_PATH + "The World/";
            else if(regionName.equals("North America") || regionName.equals("South America") || regionName.equals("Africa") || regionName.equals("Asia") || regionName.equals("Oceania") || regionName.equals("Europe") || regionName.equals("Antarctica"))
                path = DATA_PATH + "The World/" + regionName + "/";
            else
                path = DATA_PATH + "The World/" + continentRegion + "/" + regionName + "/";
        }
        scores = new File(path + regionName + SCORE_FILE_PATH);
        if(!scores.exists()){
            try{
                scores.createNewFile();
                fw = new FileWriter(scores.getAbsoluteFile());
                bw = new BufferedWriter(fw);
                bw.write("0 0 0");
                bw.close();
                System.out.println("Done.");
            }catch(IOException ex){
                System.out.println("File already exists.");
            }
        }
        try {
            fileReader = new Scanner(scores);
        } catch (FileNotFoundException ex) {
            System.out.println("file doesn't exist");
        }
        System.out.println(path);
	Image tempMapImage = loadImage(path + regionName + MAP_FILE_PATH);
	PixelReader pixelReader = tempMapImage.getPixelReader();
	WritableImage mapImage = new WritableImage(pixelReader, (int) tempMapImage.getWidth(), (int) tempMapImage.getHeight());
        for(int i = 0; i < mapImage.getWidth(); i++){
            for(int k = 0; k < mapImage.getHeight(); k++){
                Color c = pixelReader.getColor(i,k);
                if(c.equals(Color.rgb(220, 110, 0)))
                    mapImage.getPixelWriter().setColor(i, k, Color.BLACK);
            }
        }
        
	ImageView mapView = getGuiImages().get(MAP_TYPE);
	mapView.setImage(mapImage);
	int numSubRegions = ((RegioVincoDataModel) data).getRegionsFound() + ((RegioVincoDataModel) data).getRegionsNotFound();
	this.boundaryTop = -(numSubRegions * 50);
        
	// AND GIVE THE WRITABLE MAP TO THE DATA MODEL
	((RegioVincoDataModel) data).setMapImage(mapImage);
        File file = new File(path + regionName + XML_FILE_PATH);
        world.load(file);
//        Iterator<String> it = world.getAllRegions().keySet().iterator();
//        int numParents = 0;
//        Region regionCursor = world.getAllRegions().get(regionName);
//        while(regionCursor.getParentRegion() != null){
//            numParents++;
//            regionCursor = regionCursor.getParentRegion();
//        }
//        while(it.hasNext()){
//            String subRegionName = it.next();
//            System.out.println(subRegionName);
//            Region region = world.getRegion(subRegionName);
//            if(!subRegionName.equals(regionName)){
//                ((RegioVincoDataModel)data).getColorToSubRegionMappings().put(makeColor(region.getRed(), region.getGreen(), region.getBlue()), subRegionName);
//                ((RegioVincoDataModel)data).getPixels().put(subRegionName, new ArrayList());
//            }
//        }
        for(Region r : (world.getAllRegions().get(regionName)).getSubRegionsAL()){
            ((RegioVincoDataModel)data).getColorToSubRegionMappings().put(makeColor(r.getRed(), r.getGreen(), r.getBlue()), r.getName());
            ((RegioVincoDataModel)data).getPixels().put(r.getName(), new ArrayList());
        }
        for (int i = 0; i < mapImage.getWidth(); i++) {
	    for (int j = 0; j < mapImage.getHeight(); j++) {
		Color c = pixelReader.getColor(i, j);
		if (((RegioVincoDataModel)data).getColorToSubRegionMappings().containsKey(c)) {
		    String subRegion = ((RegioVincoDataModel)data).getColorToSubRegionMappings().get(c);
		    ArrayList<int[]> subRegionPixels = ((RegioVincoDataModel)data).getPixels().get(subRegion);
		    int[] pixel = new int[2];
		    pixel[0] = i;
		    pixel[1] = j;
		    subRegionPixels.add(pixel);
                    
//                    File xmlFile = new File(path + subRegion + "/" + subRegion+ XML_FILE_PATH);
//                    if(!xmlFile.exists()){
//                        mapImage.getPixelWriter().setColor(i,j, Color.PINK);
//                    }
		}
	    }
	}
//        Region subRegion = world.getAllRegions().get(regionName);
//        int numColored = 0;
//        Iterator<String> it2 = world.getAllRegions().keySet().iterator();
//        while(it2.hasNext()){
//            String subRegionName = it2.next();
//            subRegion = world.getAllRegions().get(subRegionName);
////            System.out.println(subRegion.getType());
//            if(!subRegionName.equals(regionName)){
//                File xmlFile = new File(path + subRegionName + "/" + subRegionName + XML_FILE_PATH);
//                if(!xmlFile.exists()){
//                    System.out.println(subRegionName);
//                    ((RegioVincoDataModel)data).changeSubRegionColorOnMap(this, subRegionName, Color.PINK);
//                    System.out.println(++numColored);
//                }
//            }
//        }
        for(Region r : (world.getAllRegions().get(regionName)).getSubRegionsAL()){
            File xmlFile = new File(path + r.getName() + "/" + r.getName() + XML_FILE_PATH);
            if(!xmlFile.exists()){
                System.out.println(r.getName());
                ((RegioVincoDataModel)data).changeSubRegionColorOnMap(this, r.getName(), Color.PINK);
            }
        }
        regionTitle.setText(regionName);
        changeAncestorButton(regionName);
        ((RegioVincoDataModel)data).getMapImage();
        currentRegionPath = path;
        if(world.getAllRegions().get(currentRegion).getType() == RegionType.WORLD){
            if(((RegioVincoDataModel)data).checkIfGameModeAvailable("capital", this))
                getGuiButtons().get(CAPITAL_TYPE).setDisable(false);
            else
                getGuiButtons().get(CAPITAL_TYPE).setDisable(true);
            if(((RegioVincoDataModel)data).checkIfGameModeAvailable("flag", this))
                getGuiButtons().get(FLAG_TYPE).setDisable(false);
            else
                getGuiButtons().get(FLAG_TYPE).setDisable(true);
            if(((RegioVincoDataModel)data).checkIfGameModeAvailable("leader", this))
                getGuiButtons().get(LEADER_TYPE).setDisable(false);
            else
                getGuiButtons().get(LEADER_TYPE).setDisable(true);
            if(((RegioVincoDataModel)data).checkIfGameModeAvailable("name", this))
                getGuiButtons().get(NAME_TYPE).setDisable(false);
            else
                getGuiButtons().get(NAME_TYPE).setDisable(true);
        }
        else if(world.getAllRegions().get(currentRegion).getType() == RegionType.CONTINENT){
            if(((RegioVincoDataModel)data).checkIfGameModeAvailable("capital", this))
                getGuiButtons().get(CAPITAL_TYPE).setDisable(false);
            else
                getGuiButtons().get(CAPITAL_TYPE).setDisable(true);
            if(((RegioVincoDataModel)data).checkIfGameModeAvailable("flag", this))
                getGuiButtons().get(FLAG_TYPE).setDisable(false);
            else
                getGuiButtons().get(FLAG_TYPE).setDisable(true);
            if(((RegioVincoDataModel)data).checkIfGameModeAvailable("leader", this))
                getGuiButtons().get(LEADER_TYPE).setDisable(false);
            else
                getGuiButtons().get(LEADER_TYPE).setDisable(true);
            if(((RegioVincoDataModel)data).checkIfGameModeAvailable("name", this))
                getGuiButtons().get(NAME_TYPE).setDisable(false);
            else
                getGuiButtons().get(NAME_TYPE).setDisable(true);
        }
        else if(world.getAllRegions().get(currentRegion).getType() == RegionType.NATION){
            if(((RegioVincoDataModel)data).checkIfGameModeAvailable("capital", this))
                getGuiButtons().get(CAPITAL_TYPE).setDisable(false);
            else
                getGuiButtons().get(CAPITAL_TYPE).setDisable(true);
            if(((RegioVincoDataModel)data).checkIfGameModeAvailable("flag", this))
                getGuiButtons().get(FLAG_TYPE).setDisable(false);
            else
                getGuiButtons().get(FLAG_TYPE).setDisable(true);
            if(((RegioVincoDataModel)data).checkIfGameModeAvailable("leader", this))
                getGuiButtons().get(LEADER_TYPE).setDisable(false);
            else
                getGuiButtons().get(LEADER_TYPE).setDisable(true);
            if(((RegioVincoDataModel)data).checkIfGameModeAvailable("name", this))
                getGuiButtons().get(NAME_TYPE).setDisable(false);
            else
                getGuiButtons().get(NAME_TYPE).setDisable(true);
        }
        highestScore.setText("Highest Score: " + fileReader.next());
        fastestTime.setText("Fastest Time: " + fileReader.next());
        fewestGuesses.setText("Fewest Guesses: " + fileReader.next());
    }
    
    public void updateLabels(){
        regionsFound.setText(REGIONS_FOUND + ((RegioVincoDataModel)data).getRegionsFound());
        regionsLeft.setText(REGIONS_LEFT + ((RegioVincoDataModel)data).getRegionsNotFound());
        incorrectGuesses.setText(INCORRECT_GUESSES + ((RegioVincoDataModel)data).getNumIncorrectGuesses());
    }
    
    public boolean checkIfDeepestLevel(WorldDataManager world){
        Iterator it = world.getAllRegions().keySet().iterator();
        int numSubRegions = 0;
        int numSubRegionsWithNoChildren = 0;
        while(it.hasNext()){
            String subRegionName = (String)it.next();
            Region subRegion = world.getAllRegions().get(subRegionName);
            if(!subRegion.hasSubRegions()){
                numSubRegionsWithNoChildren++;
            }
            numSubRegions++;
        }
        if(numSubRegions == numSubRegionsWithNoChildren)
            return true;
        else
            return false;
    }
    
    public void changeAncestorButton(String regionName){
        if(world.getAllRegions().get(regionName).getType() == RegionType.WORLD)
            worldNode.setText(regionName);
        else if(world.getAllRegions().get(regionName).getType() == RegionType.CONTINENT){
            continentNode.setText(regionName);
            continentRegion = regionName;
        }
        else if(world.getAllRegions().get(regionName).getType() == RegionType.NATION)
            nationNode.setText(regionName);
    }
    
    public void removePastRegions(String regionType){
        if(regionType.equals("world")){
            world.clearRegions();
        }
        else if(regionType.equals("continent") && !(currentRegion.equals(continentNode.getText()))){
            if(!world.getAllRegions().get(currentRegion).getParentRegion().getName().equals("The World")){
                Iterator it;
                for(it = world.getAllRegions().get(currentRegion).getSubRegions(); it.hasNext();){
                    world.removeRegion(world.getAllRegions().get(currentRegion));
                }
                setCurrentRegion(world.getAllRegions().get(currentRegion).getParentRegion().getName());
                Iterator it2;
                for(it2 = world.getAllRegions().get(currentRegion).getSubRegions(); it2.hasNext();){
                    world.removeRegion(world.getAllRegions().get(currentRegion));
                }
            }
        }
    }
    
    public void doPrevGameStuff(){
        nameMode.setDisable(true);
        leaderMode.setDisable(true);
        capitalMode.setDisable(true);
        flagMode.setDisable(true);
        highestScore.setVisible(false);
        fastestTime.setVisible(false);
        fewestGuesses.setVisible(false);
        getGuiButtons().get(CAPITAL_TYPE).setDisable(true);
        getGuiButtons().get(LEADER_TYPE).setDisable(true);
        getGuiButtons().get(FLAG_TYPE).setDisable(true);
        getGuiButtons().get(NAME_TYPE).setDisable(true);
        getGuiButtons().get(STOP_GAME_TYPE).setDisable(false);
        guiLayer.setVisible(true);
        gameLayer.setVisible(true);
        worldNode.setVisible(false);
        continentNode.setVisible(false);
        nationNode.setVisible(false);
        regionMOLabel.setVisible(false);
        regionFlagImage.setVisible(false);
        highestScoreMO.setVisible(false);
        fastestTimeMO.setVisible(false);
        fewestGuessesMO.setVisible(false);
        gameOn = true;
    }
    
    public String getCapital(String regionName){
        return world.getAllRegions().get(regionName).getCapital();
    }
    
    public String getLeader(String regionName){
        return world.getAllRegions().get(regionName).getLeader();
    }
}