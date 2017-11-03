package regio_vinco;

import java.util.NoSuchElementException;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import pacg.KeyPressHook;

/**
 * This controller provides the appropriate responses for all interactions.
 */
public class RegioVincoController implements KeyPressHook {
    RegioVincoGame game;
    
    public RegioVincoController(RegioVincoGame initGame) {
	game = initGame;
    }
    
    public void processStartGameRequest() {
	game.reset();
    }
    
    public void processExitGameRequest() {
	game.killApplication();
    }
    
    public void processMapClickRequest(int x, int y) {
	((RegioVincoDataModel)game.getDataModel()).respondToMapSelection(game, x, y);
    }
    
    public void processNameModeRequest(){
        game.reset("name");
    }
    
    public void processLeaderModeRequest(){
        game.reset("leader");
    }
    
    public void processCapitalModeRequest(){
        game.reset("capital");
    }
    
    public void processFlagModeRequest(){
        game.reset("flag");
    }
    
    @Override
    public void processKeyPressHook(KeyEvent ke)
    {
        KeyCode keyCode = ke.getCode();
        if (keyCode == KeyCode.C)
        {
            try
            {    
                game.beginUsingData();
                RegioVincoDataModel dataModel = (RegioVincoDataModel)(game.getDataModel());
                dataModel.removeAllButOneFromeStack(game);
            }
            catch(NoSuchElementException e){
                
            }
            finally
            {
                game.updateLabels();
                game.endUsingData();
            }
        }
    }   
}
