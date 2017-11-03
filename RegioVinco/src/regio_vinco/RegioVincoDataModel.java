package regio_vinco;

import audio_manager.AudioManager;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import pacg.PointAndClickGame;
import pacg.PointAndClickGameDataModel;
import static regio_vinco.RegioVinco.*;
import world_data.Region;
import world_data.RegionType;
import world_data.WorldDataManager;

/**
 * This class manages the game data for the Regio Vinco game application. Note
 * that this game is built using the Point & Click Game Framework as its base. 
 * This class contains methods for managing game data and states.
 *
 * @author Richard McKenna
 * @version 1.0
 */
public class RegioVincoDataModel extends PointAndClickGameDataModel {
    // THIS IS THE MAP IMAGE THAT WE'LL USE
    private WritableImage mapImage;
    private PixelReader mapPixelReader;
    private PixelWriter mapPixelWriter;
    
    // AND OTHER GAME DATA
    private String regionName;
    private String subRegionsType;
    private HashMap<Color, String> colorToSubRegionMappings;
    private HashMap<String, Color> subRegionToColorMappings;
    private HashMap<String, ArrayList<int[]>> pixels;
    private LinkedList<String> redSubRegions;
    private LinkedList<MovableText> subRegionStack;
    
    private int incorrectGuesses = 0;
    private Label timer = new Label();
    private long timeStart = System.currentTimeMillis();
    private long timeEnd;
    private long gameDurationSeconds;
    private String gameDurationString;

    /**
     * Default constructor, it initializes all data structures for managing the
     * Sprites, including the map.
     */
    public RegioVincoDataModel() {
	// INITIALIZE OUR DATA STRUCTURES
	colorToSubRegionMappings = new HashMap();
	subRegionToColorMappings = new HashMap();
	subRegionStack = new LinkedList();
	redSubRegions = new LinkedList();
        pixels = new HashMap();
    }
    
    public void setMapImage(WritableImage initMapImage) {
	mapImage = initMapImage;
	mapPixelReader = mapImage.getPixelReader();
	mapPixelWriter = mapImage.getPixelWriter();
    }
    public WritableImage getMapImage(){
        return mapImage;
    }

    public void removeAllButOneFromeStack(RegioVincoGame game) {
        if(game.currentGameMode.equals("leader") || game.currentGameMode.equals("capital")){
            while (subRegionStack.size() > 1) {
                MovableText text = subRegionStack.removeFirst();
                ((RegioVincoGame)game).gameLayer.getChildren().remove(text.getLabel());
                String subRegionName = text.getRegion().getName();

                // TURN THE TERRITORY GREEN
                changeSubRegionColorOnMap(game, subRegionName, Color.GREEN);
            }
            changeSubRegionColorOnMap(game, subRegionStack.getFirst().getRegion().getName(), getColorMappedToSubRegion(subRegionStack.getFirst().getRegion().getName()));
            startTextStackMovingDown();
        }
        else{
            while (subRegionStack.size() > 1) {
                MovableText text = subRegionStack.removeFirst();
                ((RegioVincoGame)game).gameLayer.getChildren().remove(text.getLabel());
                String subRegionName = text.getText().getText();

                // TURN THE TERRITORY GREEN
                changeSubRegionColorOnMap(game, subRegionName, Color.GREEN);
            }
            changeSubRegionColorOnMap(game, subRegionStack.getFirst().getText().getText(), getColorMappedToSubRegion(subRegionStack.getFirst().getText().getText()));
            startTextStackMovingDown();
        }
    }

    // ACCESSOR METHODS
    public String getRegionName() {
	return regionName;
    }

    public String getSubRegionsType() {
	return subRegionsType;
    }
    
    public PixelReader getMapPixelReader(){
        return mapPixelReader;
    }
    
    public Label getTimer(){
        return timer;
    }

    public void setRegionName(String initRegionName) {
	regionName = initRegionName;
    }

    public void setSubRegionsType(String initSubRegionsType) {
	subRegionsType = initSubRegionsType;
    }

    public String getSecondsAsTimeText(long numSeconds) {
	long numHours = numSeconds / 3600;
	numSeconds = numSeconds - (numHours * 3600);
	long numMinutes = numSeconds / 60;
	numSeconds = numSeconds - (numMinutes * 60);

	String timeText = "";
	if (numHours > 0) {
	    timeText += numHours + ":";
	}
	timeText += numMinutes + ":";
	if (numSeconds < 10) {
	    timeText += "0" + numSeconds;
	} else {
	    timeText += numSeconds;
	}
	return timeText;
    }

    public int getRegionsFound() {
	return getColorToSubRegionMappings().keySet().size() - subRegionStack.size();
    }

    public int getRegionsNotFound() {
	return subRegionStack.size();
    }
    
    public int getNumIncorrectGuesses() {
        return incorrectGuesses;
    }
    
    public LinkedList<MovableText> getSubRegionStack() {
	return subRegionStack;
    }
    
    public String getSubRegionMappedToColor(Color colorKey) {
	return getColorToSubRegionMappings().get(colorKey);
    }
    
    public Color getColorMappedToSubRegion(String subRegion) {
	return subRegionToColorMappings.get(subRegion);
    }
    
    public long getTimeStart() {
        return timeStart;
    }
    
    public long getTimeEnd() {
        return timeEnd;
    }
    
    public String getGameDurationString() {
        return gameDurationString;
    }
    
    public long getGameDurationSeconds() {
        return gameDurationSeconds;
    }

    // MUTATOR METHODS

    public void addColorToSubRegionMappings(Color colorKey, String subRegionName) {
	getColorToSubRegionMappings().put(colorKey, subRegionName);
    }

    public void addSubRegionToColorMappings(String subRegionName, Color colorKey) {
	subRegionToColorMappings.put(subRegionName, colorKey);
    }

    public void respondToMapSelection(RegioVincoGame game, int x, int y) {
        // THIS IS WHERE WE'LL CHECK TO SEE IF THE
	// PLAYER CLICKED ON THE CORRECT SUBREGION
        boolean correctSelection = false;
	Color pixelColor = mapPixelReader.getColor(x, y);
	String clickedSubRegion = getColorToSubRegionMappings().get(pixelColor);
//        System.out.println(clickedSubRegion);
	if ((clickedSubRegion == null) || (subRegionStack.isEmpty())) {
	    return;
	}
        if(game.currentGameMode.equals("capital")){
            if(game.getCapital(clickedSubRegion).equals(subRegionStack.get(0).getText().getText()))
                correctSelection = true;
        }
        else if(game.currentGameMode.equals("leader")){
            if(game.getLeader(clickedSubRegion).equals(subRegionStack.get(0).getText().getText()))
                correctSelection = true;
        }
        else if(game.currentGameMode.equals("name")){
            if(clickedSubRegion.equals(subRegionStack.get(0).getText().getText()))
                correctSelection = true;
        }
        else{
            if(clickedSubRegion.equals(subRegionStack.get(0).getText().getText()))
                correctSelection = true;
        }
	if (correctSelection) {
	    // YAY, CORRECT ANSWER
            if(game.getSoundsOn())
                game.getAudio().play(SUCCESS, false);

	    // TURN THE TERRITORY GREEN
	    changeSubRegionColorOnMap(game, clickedSubRegion, Color.GREEN);

	    // REMOVE THE BOTTOM ELEMENT FROM THE STACK
	    ((RegioVincoGame)game).gameLayer.getChildren().remove(subRegionStack.removeFirst().getLabel());

	    // AND LET'S CHANGE THE RED ONES BACK TO THEIR PROPER COLORS
	    for (String s : redSubRegions) {
		Color subRegionColor = subRegionToColorMappings.get(s);
		changeSubRegionColorOnMap(game, s, subRegionColor);
	    }
	    redSubRegions.clear();

	    startTextStackMovingDown();

	    if (subRegionStack.isEmpty()) {
		this.endGameAsWin();
                boolean audioExists = true;
                try{
                    game.audio.loadAudio(game.currentRegion + "_SONG", game.currentRegionPath + game.currentRegion + ANTHEM_FILE_PATH);
                    audioExists = true;
                }catch(Exception e){
                    audioExists = false;
                    System.out.println("no audio");
                }
                if(audioExists && game.musicOn){
                    game.getAudio().stop(TRACKED_SONG);
                    game.getAudio().play(game.currentRegion + "_SONG", false);
                    System.out.println(game.currentRegionPath + game.currentRegion + ANTHEM_FILE_PATH);
                }
                timeEnd = System.currentTimeMillis();
                gameDurationSeconds = (timeEnd - timeStart) / 1000;
                gameDurationString = getSecondsAsTimeText(gameDurationSeconds);
	    }
	} else {
	    if (!redSubRegions.contains(clickedSubRegion)) {
		// BOO WRONG ANSWER
                if(game.getSoundsOn())
                    game.getAudio().play(FAILURE, false);
                incorrectGuesses++;

		// TURN THE TERRITORY TEMPORARILY RED
		changeSubRegionColorOnMap(game, clickedSubRegion, Color.RED);
		redSubRegions.add(clickedSubRegion);
	    }
	}
    }
    
    public Object[] changeMouseOverLabels(RegioVincoGame game, int x, int y){
        Object[] arr = new Object[2];
        Color regionOnMap = mapPixelReader.getColor(x, y);
        String mousedOverRegion = colorToSubRegionMappings.get(regionOnMap);
        arr[0] = regionOnMap;
        arr[1] = game.getWorldDataManager().getRegion(mousedOverRegion);
        return arr;
    }
    
    public void respondToNavigationMapSelection(RegioVincoGame game, int x, int y){
        Color regionOnMap = mapPixelReader.getColor(x, y);
        String clickedOnSubRegion = getColorToSubRegionMappings().get(regionOnMap);
        if(clickedOnSubRegion == null)
            return;
        game.setCurrentRegion(clickedOnSubRegion);
        resetMaps();
        game.reloadMap(clickedOnSubRegion);
        if(game.getWorldDataManager().getAllRegions().get(clickedOnSubRegion).getType() == RegionType.CONTINENT){
            game.getContinentNode().setText(clickedOnSubRegion);
            game.getContinentNode().setVisible(true);
        }
        else if(game.getWorldDataManager().getAllRegions().get(clickedOnSubRegion).getType() == RegionType.NATION){
            game.getNationNode().setText(clickedOnSubRegion);
            game.getNationNode().setVisible(true);
        }
//        resetMaps();
//        System.out.println(clickedOnSubRegion);
    }

    public void startTextStackMovingDown() {
	// AND START THE REST MOVING DOWN
	for (MovableText mT : subRegionStack) {
	    mT.setVelocityY(SUB_STACK_VELOCITY);
	}
    }

    public void changeSubRegionColorOnMap(RegioVincoGame game, String subRegion, Color color) {
        // THIS IS WHERE WE'LL CHECK TO SEE IF THE
	// PLAYER CLICKED NO THE CORRECT SUBREGION
	ArrayList<int[]> subRegionPixels = getPixels().get(subRegion);
	for (int[] pixel : subRegionPixels) {
	    mapPixelWriter.setColor(pixel[0], pixel[1], color);
	}
    }

    public int getNumberOfSubRegions() {
	return getColorToSubRegionMappings().keySet().size();
    }

    /**
     * Resets all the game data so that a brand new game may be played.
     *
     * @param game the Regio Vinco game in progress
     */
    @Override
    public void reset(PointAndClickGame game) {

	// THIS GAME ONLY PLAYS AFGHANISTAN
//	regionName = "Afghanistan";
//	subRegionsType = "Provinces";
        

	// LET'S CLEAR THE DATA STRUCTURES
//	getColorToSubRegionMappings().clear();
//	subRegionToColorMappings.clear();
//	subRegionStack.clear();
//	redSubRegions.clear();
//        incorrectGuesses = 0;

        // INIT THE MAPPINGS - NOTE THIS SHOULD 
	// BE DONE IN A FILE, WHICH WE'LL DO IN
	// FUTURE HOMEWORK ASSIGNMENTS
//	colorToSubRegionMappings.put(makeColor(200, 200, 200), "Badakhshan");
//	colorToSubRegionMappings.put(makeColor(198, 198, 198), "Nuristan");
//	colorToSubRegionMappings.put(makeColor(196, 196, 196), "Kunar");
//	colorToSubRegionMappings.put(makeColor(194, 194, 194), "Laghman");
//	colorToSubRegionMappings.put(makeColor(192, 192, 192), "Kapisa");
//	colorToSubRegionMappings.put(makeColor(190, 190, 190), "Panjshir");
//	colorToSubRegionMappings.put(makeColor(188, 188, 188), "Takhar");
//	colorToSubRegionMappings.put(makeColor(186, 186, 186), "Kunduz");
//	colorToSubRegionMappings.put(makeColor(184, 184, 184), "Baghlan");
//	colorToSubRegionMappings.put(makeColor(182, 182, 182), "Parwan");
//	colorToSubRegionMappings.put(makeColor(180, 180, 180), "Kabul");
//	colorToSubRegionMappings.put(makeColor(178, 178, 178), "Nangrahar");
//	colorToSubRegionMappings.put(makeColor(176, 176, 176), "Maidan Wardak");
//	colorToSubRegionMappings.put(makeColor(174, 174, 174), "Logar");
//	colorToSubRegionMappings.put(makeColor(172, 172, 172), "Paktia");
//	colorToSubRegionMappings.put(makeColor(170, 170, 170), "Khost");
//	colorToSubRegionMappings.put(makeColor(168, 168, 168), "Samangan");
//	colorToSubRegionMappings.put(makeColor(166, 166, 166), "Balkh");
//	colorToSubRegionMappings.put(makeColor(164, 164, 164), "Jowzjan");
//	colorToSubRegionMappings.put(makeColor(162, 162, 162), "Faryab");
//	colorToSubRegionMappings.put(makeColor(160, 160, 160), "Sar-e Pol");
//	colorToSubRegionMappings.put(makeColor(158, 158, 158), "Bamyan");
//	colorToSubRegionMappings.put(makeColor(156, 156, 156), "Ghazni");
//	colorToSubRegionMappings.put(makeColor(154, 154, 154), "Paktika");
//	colorToSubRegionMappings.put(makeColor(152, 152, 152), "Badghis");
//	colorToSubRegionMappings.put(makeColor(150, 150, 150), "Ghor");
//	colorToSubRegionMappings.put(makeColor(148, 148, 148), "Daykundi");
//	colorToSubRegionMappings.put(makeColor(146, 146, 146), "Oruzgan");
//	colorToSubRegionMappings.put(makeColor(144, 144, 144), "Zabul");
//	colorToSubRegionMappings.put(makeColor(142, 142, 142), "Herat");
//	colorToSubRegionMappings.put(makeColor(140, 140, 140), "Farah");
//	colorToSubRegionMappings.put(makeColor(138, 138, 138), "Nimruz");
//	colorToSubRegionMappings.put(makeColor(136, 136, 136), "Helmand");
//	colorToSubRegionMappings.put(makeColor(134, 134, 134), "Kandahar");

        
	// REST THE MOVABLE TEXT
//	Pane gameLayer = ((RegioVincoGame)game).getGameLayer();
//	gameLayer.getChildren().clear();
//	for (Color c : getColorToSubRegionMappings().keySet()) {
//	    String subRegion = getColorToSubRegionMappings().get(c);
//	    subRegionToColorMappings.put(subRegion, c);
//	    Text textNode = new Text(subRegion);
//            Label labelNode = new Label();
//            labelNode.setPrefSize(300,50);
//            labelNode.setGraphic(textNode);
//            labelNode.setStyle("-fx-background-color: rgb(" + (c.getRed()*255) + "," + (c.getGreen()*255) + "," + (c.getBlue()*255) +");");
//	    MovableText subRegionText = new MovableText(textNode, labelNode);
//	    subRegionText.getText().setFill(Color.NAVY);
//            textNode.setFont(Font.font("Verdana", 23));
//	    labelNode.setLayoutX(STACK_X);
//	    gameLayer.getChildren().add(labelNode);
//            subRegionStack.add(subRegionText);
//	}
        
//	Collections.shuffle(subRegionStack);

//	int y = STACK_INIT_Y;
//	int yInc = STACK_INIT_Y_INC;
	// NOW FIX THEIR Y LOCATIONS
//	for (MovableText mT : subRegionStack) {
//	    int tY = y + yInc;
//	    mT.getLabel().setLayoutY(tY);
//	    yInc -= 50;
//	}

	// RELOAD THE MAP
	//((RegioVincoGame) game).reloadMap();
//        if(((RegioVincoGame)game).guiLayer.getChildren().contains(((RegioVincoGame)game).getRegionsFound())){
            //SET ALL TO TruE
//            ((RegioVincoGame)game).getRegionsLeft().setVisible(true);
//            ((RegioVincoGame)game).getIncorrectGuesses().setVisible(true);
//            ((RegioVincoGame)game).getRegionsFound().setVisible(true);
//            timer.setVisible(true);
//            ImageView map = ((RegioVincoGame)game).getGUIImages().get(MAP_TYPE);
//            map.setVisible(true);
//        }
//        else{
//            ((RegioVincoGame)game).guiLayer.getChildren().add(((RegioVincoGame)game).getRegionsFound());
//            ((RegioVincoGame)game).guiLayer.getChildren().add(((RegioVincoGame)game).getRegionsLeft());
//            ((RegioVincoGame)game).guiLayer.getChildren().add(((RegioVincoGame)game).getIncorrectGuesses());
//        }
//        timeStart = System.currentTimeMillis();

//	// LET'S RECORD ALL THE PIXELS
//	pixels = new HashMap();
//	for (MovableText mT : subRegionStack) {
//	    getPixels().put(mT.getText().getText(), new ArrayList());
//	}
//
//	for (int i = 0; i < mapImage.getWidth(); i++) {
//	    for (int j = 0; j < mapImage.getHeight(); j++) {
//		Color c = mapPixelReader.getColor(i, j);
//		if (getColorToSubRegionMappings().containsKey(c)) {
//		    String subRegion = getColorToSubRegionMappings().get(c);
//		    ArrayList<int[]> subRegionPixels = getPixels().get(subRegion);
//		    int[] pixel = new int[2];
//		    pixel[0] = i;
//		    pixel[1] = j;
//		    subRegionPixels.add(pixel);
//		}
//	    }
//	}

	// RESET THE AUDIO
//	AudioManager audio = ((RegioVincoGame) game).getAudio();
//	audio.stop(AFGHAN_ANTHEM);

//	if (!audio.isPlaying(TRACKED_SONG)) {
//	    audio.play(TRACKED_SONG, true);
//            ((RegioVincoGame)game).musicOn = true;
//	}
	// LET'S GO
//	beginGame();
//        if(!((RegioVincoGame)game).guiLayer.getChildren().contains(timer)){
//            timer.setLayoutX(40);
//            timer.setLayoutY(630);
//            timer.setFont(new Font("Verdana", 20));
//            timer.setStyle("-fx-background-color: black;" + "-fx-text-fill: orange;");
//            ((RegioVincoGame)game).guiLayer.getChildren().add(timer);
//        }
    }
    
    public void reset(RegioVincoGame game, String gameMode) {

	// LET'S CLEAR THE DATA STRUCTURES
//	getColorToSubRegionMappings().clear();
//	subRegionToColorMappings.clear();
	subRegionStack.clear();
	redSubRegions.clear();
        incorrectGuesses = 0;

        Image tempImage = game.loadImage(game.currentRegionPath + "/" + game.currentRegion + MAP_FILE_PATH);
        if(tempImage == null)
            return;
        PixelReader pixelReader = tempImage.getPixelReader();
        WritableImage mapImage = new WritableImage(pixelReader, (int)tempImage.getWidth(), (int)tempImage.getHeight());
        for(int i = 0; i < mapImage.getWidth(); i++){
            for(int k = 0; k < mapImage.getHeight(); k++){
                Color c = pixelReader.getColor(i,k);
                if(c.equals(Color.rgb(220, 110, 0)))
                    mapImage.getPixelWriter().setColor(i, k, Color.BLACK);
            }
        }
        ImageView mapView = game.getGuiImages().get(MAP_TYPE);
	mapView.setImage(mapImage);
        setMapImage(mapImage);
        
	// REST THE MOVABLE TEXT
	Pane gameLayer = ((RegioVincoGame)game).getGameLayer();
	gameLayer.getChildren().clear();
	for (Color c : getColorToSubRegionMappings().keySet()) {
	    String subRegion = getColorToSubRegionMappings().get(c);
	    subRegionToColorMappings.put(subRegion, c);
            Text textNode;
            ImageView flagView;
            Label labelNode;
            Image flagImage;
            MovableText subRegionText;
            if(game.currentGameMode.equals("leader")){
                Region theRegion = game.world.getAllRegions().get(subRegion);
                if(theRegion != null){
                    textNode = new Text(game.getLeader(subRegion));
                    labelNode = new Label();
                    labelNode.setPrefSize(300,50);
                    labelNode.setGraphic(textNode);
                    labelNode.setStyle("-fx-background-color: rgb(" + (c.getRed()*255) + "," + (c.getGreen()*255) + "," + (c.getBlue()*255) +");");
                    subRegionText = new MovableText(textNode, labelNode, theRegion);
                    subRegionText.getText().setFill(Color.NAVY);
                    textNode.setFont(Font.font("Verdana", 23));
                    labelNode.setLayoutX(STACK_X);
                    gameLayer.getChildren().add(labelNode);
                    subRegionStack.add(subRegionText);
                }
            }
            else if(game.currentGameMode.equals("name")){
                textNode = new Text(subRegion);
                labelNode = new Label();
                labelNode.setPrefSize(300,50);
                labelNode.setGraphic(textNode);
                labelNode.setStyle("-fx-background-color: rgb(" + (c.getRed()*255) + "," + (c.getGreen()*255) + "," + (c.getBlue()*255) +");");
                subRegionText = new MovableText(textNode, labelNode);
                subRegionText.getText().setFill(Color.NAVY);
                textNode.setFont(Font.font("Verdana", 23));
            }
            else if(game.currentGameMode.equals("capital")){
                Region theRegion = game.world.getAllRegions().get(subRegion);
                if(theRegion != null){
                    if(!game.getCapital(subRegion).isEmpty()){
                        textNode = new Text(game.getCapital(subRegion));
                        labelNode = new Label();
                        labelNode.setPrefSize(300,50);
                        labelNode.setGraphic(textNode);
                        labelNode.setStyle("-fx-background-color: rgb(" + (c.getRed()*255) + "," + (c.getGreen()*255) + "," + (c.getBlue()*255) +");");
                        subRegionText = new MovableText(textNode, labelNode, theRegion);
                        subRegionText.getText().setFill(Color.NAVY);
                        textNode.setFont(Font.font("Verdana", 23));
                        labelNode.setLayoutX(STACK_X);
                        gameLayer.getChildren().add(labelNode);
                        subRegionStack.add(subRegionText);
                    }
                }
            }
            else{
                flagView = new ImageView();
                flagImage = game.loadImage(game.currentRegionPath + subRegion + "/" + subRegion + FLAGS_FILE_PATH);
                if(flagImage != null){
                    flagView.setImage(flagImage);
                    textNode = new Text(subRegion);
                    labelNode = new Label();
                    labelNode.setGraphic(flagView);
                    labelNode.setStyle("-fx-background-color: rgb(" + (c.getRed()*255) + "," + (c.getGreen()*255) + "," + (c.getBlue()*255) +");");
                    subRegionText = new MovableText(flagImage, labelNode, textNode);
                    labelNode.setLayoutX(STACK_X);
                    gameLayer.getChildren().add(labelNode);
                    subRegionStack.add(subRegionText);
                }
            }
	}
	Collections.shuffle(subRegionStack);
  
	// NOW FIX THEIR Y LOCATIONS
        if(!((RegioVincoGame)game).currentGameMode.equals("flag")){
            int y = STACK_INIT_Y;
            int yInc = STACK_INIT_Y_INC;
            for (MovableText mT : subRegionStack) {
                int tY = y + yInc;
                mT.getLabel().setLayoutY(tY);
                yInc -= 50;
            }
        }
        else{
            int y = STACK_INIT_Y - 130;
            int yInc = STACK_INIT_Y_INC;
//            int yInc = (int) subRegionStack.get(0).getImage().getHeight();
            System.out.println(subRegionStack.get(0).getText().getText());
            for (MovableText mT : subRegionStack) {
                int tY = y + yInc;
                mT.getLabel().setLayoutY(tY);
//                yInc -= mT.getImage().getHeight();
                yInc -= 140;
            }
        }

	// RELOAD THE MAP
	//((RegioVincoGame) game).reloadMap();
        if(((RegioVincoGame)game).navigationLayer.getChildren().contains(((RegioVincoGame)game).getRegionsFound())){
            //SET ALL TO TruE
            ((RegioVincoGame)game).getRegionsLeft().setVisible(true);
            ((RegioVincoGame)game).getIncorrectGuesses().setVisible(true);
            ((RegioVincoGame)game).getRegionsFound().setVisible(true);
            timer.setVisible(true);
            ImageView map = ((RegioVincoGame)game).getGUIImages().get(MAP_TYPE);
            map.setVisible(true);
        }
        else{
            ((RegioVincoGame)game).navigationLayer.getChildren().add(((RegioVincoGame)game).getRegionsFound());
            ((RegioVincoGame)game).navigationLayer.getChildren().add(((RegioVincoGame)game).getRegionsLeft());
            ((RegioVincoGame)game).navigationLayer.getChildren().add(((RegioVincoGame)game).getIncorrectGuesses());
        }
        timeStart = System.currentTimeMillis();

	// RESET THE AUDIO
//	AudioManager audio = ((RegioVincoGame) game).getAudio();
//	audio.stop(AFGHAN_ANTHEM);

//	if (!audio.isPlaying(TRACKED_SONG)) {
//	    audio.play(TRACKED_SONG, true);
//	}
	// LET'S GO
	beginGame();
        if(!((RegioVincoGame)game).navigationLayer.getChildren().contains(timer)){
            timer.setLayoutX(40);
            timer.setLayoutY(630);
            timer.setFont(new Font("Verdana", 20));
            timer.setStyle("-fx-background-color: black;" + "-fx-text-fill: orange;");
            ((RegioVincoGame)game).navigationLayer.getChildren().add(timer);
        }
    }
   
    // HELPER METHOD FOR MAKING A COLOR OBJECT
    public static Color makeColor(int r, int g, int b) {
	return Color.color(r/255.0, g/255.0, b/255.0);
    }

    // STATE TESTING METHODS
    // UPDATE METHODS
	// updateAll
	// updateDebugText
    
    /**
     * Called each frame, this thread already has a lock on the data. This
     * method updates all the game sprites as needed.
     *
     * @param game the game in progress
     */
    @Override
    public void updateAll(PointAndClickGame game, double percentage) {
	for (MovableText mT : subRegionStack) {
	    mT.update(percentage);
	}
	if (!subRegionStack.isEmpty()) {
	    MovableText bottomOfStack = subRegionStack.get(0);
	    double bottomY = bottomOfStack.getLabel().getLayoutY() + bottomOfStack.getLabel().getTranslateY() + 50;
            if(!(((RegioVincoGame)game).currentGameMode.equals("flag"))){
                if (bottomY >= FIRST_REGION_Y_IN_STACK) {
                    double diffY = bottomY - FIRST_REGION_Y_IN_STACK;
                    for (MovableText mT : subRegionStack) {
                        mT.getText().setY(mT.getText().getY() - diffY);
                        mT.setVelocityY(0);
                    }
                }
                subRegionStack.peek().getLabel().setStyle("-fx-background-color: lime");
                subRegionStack.peek().getText().setStyle("-fx-fill: crimson");
            }
            else{
                if (bottomY >= 700 - bottomOfStack.getImage().getHeight()) {
                    double diffY = bottomY - (700 - bottomOfStack.getImage().getHeight());
                    for (MovableText mT : subRegionStack) {
                        mT.getLabel().setLayoutY(mT.getLabel().getLayoutY() - diffY);
                        mT.setVelocityY(0);
                    }
                }
            }
	}
        
        timer.setText(getSecondsAsTimeText(((System.currentTimeMillis() - timeStart)) / 1000));
    }

    /**
     * Called each frame, this method specifies what debug text to render. Note
     * that this can help with debugging because rather than use a
     * System.out.print statement that is scrolling at a fast frame rate, we can
     * observe variables on screen with the rest of the game as it's being
     * rendered.
     *
     * @return game the active game being played
     */
    public void updateDebugText(PointAndClickGame game) {
	debugText.clear();
    }

    /**
     * @return the colorToSubRegionMappings
     */
    public HashMap<Color, String> getColorToSubRegionMappings() {
        return colorToSubRegionMappings;
    }

    /**
     * @return the pixels
     */
    public HashMap<String, ArrayList<int[]>> getPixels() {
        return pixels;
    }
    
    public void resetMaps(){
        colorToSubRegionMappings.clear();
        subRegionToColorMappings.clear();
        subRegionStack.clear();
        redSubRegions.clear();
    }
    public void setPixels(HashMap<String, ArrayList<int[]>> pixels){
        this.pixels = pixels;
    }
    
    public boolean checkIfGameModeAvailable(String gameMode, RegioVincoGame game){
        LinkedList<MovableText> temp = new LinkedList<MovableText>();
        for (Color c : getColorToSubRegionMappings().keySet()) {
	    String subRegion = getColorToSubRegionMappings().get(c);
	    subRegionToColorMappings.put(subRegion, c);
            Text textNode;
            ImageView flagView;
            Label labelNode;
            Image flagImage;
            MovableText subRegionText;
            if(gameMode.equals("leader")){
                Region theRegion = game.world.getAllRegions().get(subRegion);
                if(theRegion != null){
                    if(!game.getLeader(subRegion).isEmpty()){
                        textNode = new Text(game.getLeader(subRegion));
                        labelNode = new Label();
                        labelNode.setPrefSize(300,50);
                        labelNode.setGraphic(textNode);
                        labelNode.setStyle("-fx-background-color: rgb(" + (c.getRed()*255) + "," + (c.getGreen()*255) + "," + (c.getBlue()*255) +");");
                        subRegionText = new MovableText(textNode, labelNode, theRegion);
                        subRegionText.getText().setFill(Color.NAVY);
                        textNode.setFont(Font.font("Verdana", 23));
                        labelNode.setLayoutX(STACK_X);
                        game.gameLayer.getChildren().add(labelNode);
                        temp.add(subRegionText);
                    }
                }
            }
            else if(gameMode.equals("name")){
                textNode = new Text(subRegion);
                labelNode = new Label();
                labelNode.setPrefSize(300,50);
                labelNode.setGraphic(textNode);
                labelNode.setStyle("-fx-background-color: rgb(" + (c.getRed()*255) + "," + (c.getGreen()*255) + "," + (c.getBlue()*255) +");");
                subRegionText = new MovableText(textNode, labelNode);
                subRegionText.getText().setFill(Color.NAVY);
                textNode.setFont(Font.font("Verdana", 23));
                labelNode.setLayoutX(STACK_X);
                game.gameLayer.getChildren().add(labelNode);
                temp.add(subRegionText);
            }
            else if(gameMode.equals("capital")){
                Region theRegion = game.world.getAllRegions().get(subRegion);
                if(theRegion != null){
                    if (!game.getCapital(subRegion).isEmpty()){
                        textNode = new Text(game.getCapital(subRegion));
                        labelNode = new Label();
                        labelNode.setPrefSize(300,50);
                        labelNode.setGraphic(textNode);
                        labelNode.setStyle("-fx-background-color: rgb(" + (c.getRed()*255) + "," + (c.getGreen()*255) + "," + (c.getBlue()*255) +");");
                        subRegionText = new MovableText(textNode, labelNode, theRegion);
                        subRegionText.getText().setFill(Color.NAVY);
                        textNode.setFont(Font.font("Verdana", 23));
                        labelNode.setLayoutX(STACK_X);
                        game.gameLayer.getChildren().add(labelNode);
                        temp.add(subRegionText);
                    }
                }
            }
            else{
                flagView = new ImageView();
                flagImage = game.loadImage(game.currentRegionPath + subRegion + "/" + subRegion + FLAGS_FILE_PATH);
                if(flagImage != null){
                    flagView.setImage(flagImage);
                    textNode = new Text(subRegion);
                    labelNode = new Label();
                    labelNode.setGraphic(flagView);
                    labelNode.setStyle("-fx-background-color: rgb(" + (c.getRed()*255) + "," + (c.getGreen()*255) + "," + (c.getBlue()*255) +");");
                    subRegionText = new MovableText(flagImage, labelNode, textNode);
                    labelNode.setLayoutX(STACK_X);
                    game.gameLayer.getChildren().add(labelNode);
                    temp.add(subRegionText);
                }
            }
	}
        if(temp.isEmpty())
            return false;
        else
            return true;
    }
}
