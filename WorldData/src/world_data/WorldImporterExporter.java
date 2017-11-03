package world_data;

import java.io.File;

/**
 * This interface provides the requirements for an importer/exporter
 * plugin to be used for loading and saving worlds to an XML file. Note
 * that this library, and thus this interface, does not have a particular
 * XML file structure/schema. So various formats could be used, which
 * would be up to the plugin developer.
 * 
 *  @author Richard McKenna
 *          Debugging Enterprises
 *          Version 1.0
 */
public interface WorldImporterExporter
{
    /**
     * Loads the geographic contents of regionsFile into worldToLoad.
     * 
     * @param regionsFile The XML file from which the data will be extracted.
     * 
     * @param worldToLoad The data read from the file will be loaded
     * into this world.
     * 
     * @return true if the world loaded successfully, false otherwise.
     */
    public boolean loadWorld(File regionsFile, WorldDataManager worldToLoad); 

    /**
     * Saves the geographic data found in worldToSave into worldFile.
     * 
     * @param worldFile The file to which we'll save our world.
     * 
     * @param worldToSave The world that contains all the data we'll
     * be saving.
     * 
     * @return true if the world saved successfully, false otherwise.
     */
    public boolean saveWorld(File worldFile, WorldDataManager worldToSave);
}