 package regio_vinco;

import java.io.File;
import javafx.application.Application;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import world_data.WorldDataManager;
import world_io.WorldIO;

/**
 * This is the Regio Vinco game application. Note that it extends the
 * PointAndClickGame class and overrides all the proper methods for setting up
 * the Data, the GUI, the Event Handlers, and update and timer task, the thing
 * that actually does the update scheduled rendering.
 *
 * @author Richard McKenna
 * @version 1.0
 */
public class RegioVinco extends Application {

    // THESE CONSTANTS SETUP THE GAME DIMENSIONS. THE GAME WIDTH
    // AND HEIGHT SHOULD MIRROR THE BACKGROUND IMAGE DIMENSIONS. WE
    // WILL NOT RENDER ANYTHING OUTSIDE THOSE BOUNDS.

    public static final int GAME_WIDTH = 2100;
    public static final int GAME_HEIGHT = 1300;

    // FOR THIS APP WE'RE ONLY PLAYING WITH ONE MAP, BUT
    // IN THE FUTURE OUR GAMES WILL USE LOTS OF THEM
    public static final String REGION_NAME = "Afghanistan";
    public static final String DATA_PATH = "./data/";
    public static final String MAPS_PATH = "./data/maps/";
    public static final String XML_PATH = "./data/xml/";
    public static final String ANTHEM_FILE_PATH = " National Anthem.mid";
    public static final String FLAGS_FILE_PATH = " Flag.png";
    public static final String MAP_FILE_PATH = " Map.png";
    public static final String XML_FILE_PATH = " Data.xml";
    public static final String SCORE_FILE_PATH = " Scores.txt";
    public static final String AFG_MAP_FILE_PATH = MAPS_PATH + "GreyscaleAFG.png";

    // HERE ARE THE PATHS TO THE REST OF THE IMAGES WE'LL USE
    public static final String GUI_PATH = "./data/gui/";
    public static final String BACKGROUND_FILE_PATH = GUI_PATH + "RegioVincoBackground.jpg";
    public static final String TITLE_FILE_PATH = GUI_PATH + "RegioVincoTitle.png";
    public static final String START_BUTTON_FILE_PATH = GUI_PATH + "RegioVincoStartButton.png";
    public static final String START_BUTTON_MO_FILE_PATH = GUI_PATH + "RegioVincoStartButtonMouseOver.png";
    public static final String EXIT_BUTTON_FILE_PATH = GUI_PATH + "RegioVincoExitButton.png";
    public static final String EXIT_BUTTON_MO_FILE_PATH = GUI_PATH + "RegioVincoExitButtonMouseOver.png";
    public static final String SUB_REGION_FILE_PATH = GUI_PATH + "RegioVincoSubRegion.png";
    public static final String WIN_DISPLAY_FILE_PATH = GUI_PATH + "RegioVincoWinDisplay.png";
    public static final String MCKENNA_FILE_PATH = MAPS_PATH + "RichardMcKenna.jpg";
    public static final String MCKENNASMILE_FILE_PATH = MAPS_PATH + "McKennaSmile.jpg";
    public static final String SPLASH_FILE_PATH = GUI_PATH + "RegioVincoSplash.jpg";
    public static final String NAVIGATION_FILE_PATH = GUI_PATH + "RegioVincoNavigation.jpg";
    public static final String SETTINGS_ICON_FILE_PATH = GUI_PATH + "SettingsButton.png";
    public static final String SETTINGS_ICON_MO_FILE_PATH = GUI_PATH + "SettingsButtonMouseOver.png";
    public static final String HELP_ICON_FILE_PATH = GUI_PATH + "HelpButton.png";
    public static final String HELP_ICON_MO_FILE_PATH = GUI_PATH + "HelpButtonMouseOver.png";
    public static final String GLOBE_ICON_FILE_PATH = GUI_PATH + "GlobeIcon.png";
    public static final String GLOBE_ICON_MO_FILE_PATH = GUI_PATH + "GlobeIconMouseOver.png";
    public static final String SETTINGS_FILE_PATH = GUI_PATH + "RegioVincoSettings.png";
    public static final String COMING_SOON_FILE_PATH = GUI_PATH + "ComingSoon.png";
    public static final String WORLD_MAP_FILE_PATH = MAPS_PATH + "TheWorldMap.png";
    public static final String SOUND_ON_FILE_PATH = GUI_PATH + "SoundOnButton.png";
    public static final String SOUND_ON_MO_FILE_PATH = GUI_PATH + "SoundOnButtonMouseOver.png";
    public static final String SOUND_OFF_FILE_PATH = GUI_PATH + "SoundOffButton.png";
    public static final String SOUND_OFF_MO_FILE_PATH = GUI_PATH + "SoundOffButtonMouseOver.png";
    public static final String PLAY_FILE_PATH = GUI_PATH + "PlayButton.png";
    public static final String PLAY_MO_FILE_PATH = GUI_PATH + "PlayButtonMouseOver.png";
    public static final String STOP_FILE_PATH = GUI_PATH + "StopButton.png";
    public static final String STOP_MO_FILE_PATH = GUI_PATH + "StopButtonMouseOver.png";
    public static final String CAPITAL_FILE_PATH = GUI_PATH + "CapitalButton.png";
    public static final String CAPITAL_MO_FILE_PATH = GUI_PATH + "CapitalButtonMouseOver.png";
    public static final String FLAG_FILE_PATH = GUI_PATH + "FlagButton.png";
    public static final String FLAG_MO_FILE_PATH = GUI_PATH + "FlagButtonMouseOver.png";
    public static final String LEADER_FILE_PATH = GUI_PATH + "LeaderButton.png";
    public static final String LEADER_MO_FILE_PATH = GUI_PATH + "LeaderButtonMouseOver.png";
    public static final String NAME_FILE_PATH = GUI_PATH + "NameButton.png";
    public static final String NAME_MO_FILE_PATH = GUI_PATH + "NameButtonMouseOver.png";
    public static final String STOP_GAME_FILE_PATH = GUI_PATH + "StopGameButton.png";
    public static final String STOP_GAME_MO_FILE_PATH = GUI_PATH + "StopGameButtonMouseOver.png";

    // HERE ARE SOME APP-LEVEL SETTINGS, LIKE THE FRAME RATE. ALSO,
    // WE WILL BE LOADING SpriteType DATA FROM A FILE, SO THAT FILE
    // LOCATION IS PROVIDED HERE AS WELL. NOTE THAT IT MIGHT BE A 
    // GOOD IDEA TO LOAD ALL OF THESE SETTINGS FROM A FILE, BUT ALAS,
    // THERE ARE ONLY SO MANY HOURS IN A DAY
    public static final int TARGET_FRAME_RATE = 30;
    public static final String APP_TITLE = "Regio Vinco";
    
    //SPASH IMAGE
    public static final String MCKENNA_TYPE = "MCKENNA_TYPE";
    public static final String MCKENNA_TYPE_2 = "MCKENNA TYPE 2";
    public static final String SPLASH_BACKGROUND_TYPE = "SPLASH BACKGROUND TYPE";
    
    // BACKGROUND IMAGE
    public static final String BACKGROUND_TYPE = "BACKGROUND_TYPE";
    public static final int BACKGROUND_X = 0;
    public static final int BACKGROUND_Y = 0;
    
    // TITLE IMAGE
    public static final String TITLE_TYPE = "TITLE_TYPE";
    public static final int TITLE_X = 900;
    public static final int TITLE_Y = 0;
    
    // START GAME BUTTON
    public static final String START_TYPE = "START_TYPE";
    public static final int START_X = 900;
    public static final int START_Y = 100;

    // EXIT GAME BUTTON
    public static final String EXIT_TYPE = "EXIT_TYPE";
    public static final int EXIT_X = 1050;
    public static final int EXIT_Y = 100;
    
    // THE GAME MAP LOCATION
    public static final String MAP_TYPE = "MAP_TYPE";
    public static final String SUB_REGION_TYPE = "SUB_REGION_TYPE";
    public static final int MAP_X = 0;
    public static final int MAP_Y = 0;

    // THE WIN DIALOG
    public static final String WIN_DISPLAY_TYPE = "WIN_DISPLAY";
    public static final int WIN_X = 350;
    public static final int WIN_Y = 150;
    
    // THIS IS THE X WHERE WE'LL DRAW ALL THE STACK NODES
    public static final int STACK_X = 900;
    public static final int STACK_INIT_Y = 550;
    public static final int STACK_INIT_Y_INC = 50;

    public static final Color REGION_NAME_COLOR = RegioVincoDataModel.makeColor(240, 240, 240);

    public static final int SUB_STACK_VELOCITY = 2;
    public static final int FIRST_REGION_Y_IN_STACK = GAME_HEIGHT - 50;

    public static final String AUDIO_DIR = "./data/audio/";
    public static final String AFGHAN_ANTHEM_FILE_NAME = AUDIO_DIR + "AfghanistanNationalAnthem.mid";
    public static final String SUCCESS_FILE_NAME = AUDIO_DIR + "Success.wav";
    public static final String FAILURE_FILE_NAME = AUDIO_DIR + "Failure.wav";
    public static final String TRACKED_FILE_NAME = AUDIO_DIR + "Tracked.wav";
    public static final String AFGHAN_ANTHEM = "AFGHAN_ANTHEM";
    public static final String SUCCESS = "SUCCESS";
    public static final String FAILURE = "FAILURE";
    public static final String TRACKED_SONG = "TRACKED_SONG";
    public static final String PROVINCES = "Afghanistan Provinces";
    public static final String REGIONS_FOUND = "Regions Found: ";
    public static final String REGIONS_LEFT = "Regions Left: ";
    public static final String INCORRECT_GUESSES = "Incorrect Guesses: ";
    
    //THIS IS FOR THE WIN SCREEN LABELS
    public static final String WIN_SCREEN_REGION = "Region: Afghanistan";
    public static final String WIN_SCREEN_SCORE = "Score: ";
    public static final String WIN_SCREEN_GAME_DURATION = "Game Duration: ";
    public static final String WIN_SCREEN_SUBREGIONS = "Sub Regions: ";
    public static final String WIN_SCREEN_CONGRATS = "Congratulations\nfrom the people\nof ";
    
    //NAVIGATION IMAGE
    public static final String NAVIGATION_TYPE = "NAVIGATION TYPE";
    
    //SETTINGS & HELP IMAGES
    public static final String SETTINGS_ICON_TYPE = "SETTINGS ICON TYPE";
    public static final String SETTINGS_ICON_TYPE_2 = "SETTINGS ICON TYPE 2";
    public static final String HELP_ICON_TYPE = "HELP ICON TYPE";
    public static final String HELP_ICON_TYPE_2 = "HELP ICON TYPE 2";
    public static final String GLOBE_ICON_TYPE = "GLOBE ICON TYPE";
    public static final String GLOBE_ICON_TYPE_2 = "GLOBE ICON TYPE_2";
    public static final String SETTINGS_TYPE = "SETTINGS TYPE";
    public static final int SETTINGS_ICON_X = 0;
    public static final int SETTINGS_ICON_Y = 0;
    public static final String SOUND_ON_TYPE = "SOUND ON TYPE";
    public static final String SOUND_OFF_TYPE = "SOUND OFF TYPE";
    public static final String PLAY_TYPE = "PLAY TYPE";
    public static final String STOP_TYPE = "STOP TYPE";
    public static final String CAPITAL_TYPE = "CAPITAL TYPE";
    public static final String FLAG_TYPE = "FLAG TYPE";
    public static final String NAME_TYPE = "NAME TYPE";
    public static final String LEADER_TYPE = "LEADER TYPE";
    public static final String STOP_GAME_TYPE = "STOP GAME TYPE";
    
    //BUTTON POSITIONS ON NAVIGATION SCREEN
    public static final int SETTINGS_X = 1000;
    public static final int SETTINGS_Y = 70;
    public static final int GAME_X = 900;
    public static final int GAME_Y = 150;
    
    public static final int TITLE_LABEL_X = 380;
    public static final int TITLE_LABEL_Y = 10;
    
    public static final String FILE_WORLD_SCHEMA = "./data/The World/RegionData.xsd";

    /**
     * This is where the RegioVinco application starts. It proceeds to make a
     * game and pass it the window, and then starts it.
     *
     * @param primaryStage The window for this JavaFX application.
     */
    @Override
    public void start(Stage primaryStage) {
        // MAKE THE DATA MANAGER
	WorldDataManager worldDataManager = new WorldDataManager();
	
	// INIT THE FILE I/O
        // AND OUR IMPORTER/EXPORTER
        File schemaFile = new File(FILE_WORLD_SCHEMA);
        WorldIO worldIO = new WorldIO(schemaFile);
        worldDataManager.setWorldImporterExporter(worldIO);
	RegioVincoGame game = new RegioVincoGame(primaryStage, worldDataManager);
	game.startGame();
    }

    /**
     * The RegioVinco game application starts here. All game data and GUI
     * initialization is done through the constructor, so we will just construct
     * our game and set it visible to start it up.
     *
     * @param args command line arguments, which will not be used
     */
    public static void main(String[] args) {
	launch(args);
    }
}
