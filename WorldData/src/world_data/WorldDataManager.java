package world_data;

import java.io.File;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;

/**
 *  This is a data management class for Regions, providing a means
 *  to add, remove, and find regions.
 * 
 *  @author Richard McKenna
 *          Debugging Enterprises
 *          Version 1.0
 */
public class WorldDataManager 
{
    // THIS IS WHERE WE'LL STORE ONE OF EACH REGION
    private HashMap<String, Region> allRegions;
    
    // THERE SHOULD ONLY BE ONE WORLD NODE, WHICH IS THE ROOT
    private Region world;
    
    // THIS WILL DO THE READING/WRITING OF FILES. NOTE THAT
    // THE CONCRETE CLASS IMPLEMENTATION MUST BE PROVIDED
    private WorldImporterExporter worldImporterExporter;
        
    /**
     * Default constructor, it will initialize all necessary data structures
     * such that xml files may be loaded. Note that a constructed
     * importer/exporter must be set separately.
     */
    public WorldDataManager()
    {
        // INIT WHERE WE'LL PUT OUR DATA
        allRegions = new HashMap();
    }    
    
    // ACCESSOR METHODS

   /**
    * Accessor method for getting all the regions currently in the world.
    * 
    * @return A Collection containing all the regions currently in the world.
    */
   public HashMap<String, Region> getAllRegions()
   {
       return allRegions;
   }
   
   /**
     * Accessor method for getting one of the regions in the current world.
     * 
     * @param regionId The unique key (identifier) for the Region to retrieve.
     * 
     * @return The Region that corresponds to the regionId.
     */
    public Region getRegion(String regionId)
    {
        return allRegions.get(regionId);
    }

    /**
     * Accessor method for getting the root of the world.
     * 
     * @return The root region for the world.
     */
    public Region getWorld()
    {
        return world;
    }
    
    /**
     * This method is for getting the sequence of nodes, in order, from the
     * root of this world to the region provided as an argument.
     * 
     * @param region The region we're looking to find a path to.
     * 
     * @return A LinkedList of all the Regions in the path from the root
     * of the world to the region argument.
     */
    public LinkedList<Region> getPathFromRoot(Region region)
    {
        // WE'LL PUT ALL THE REGIONS IN THE PATH HERE
        LinkedList<Region> path = new LinkedList();
        
        // PUT THE REGION ITSELF
        path.add(region);

        // AND NOW PUT ALL ITS ANCESTORS IN ORDER
        Region regionToAdd = region.getParentRegion();
        while (regionToAdd != null)
        {
            // PREPEND THE TO THE FRONT OF THE LIST
            path.addFirst(regionToAdd);
            regionToAdd = regionToAdd.getParentRegion();
        }
        // AND RETURN THE PATH
        return path;
    }
    
    /**
     * This method is for getting the sequence of nodes, in order, from the
     * the region provided to the root of the world.
     * 
     * @param region The region we're looking to find a path from.
     * 
     * @return A LinkedList of all the Regions in the path from region
     * to the root of the world.
     */    
    public LinkedList<Region> getPathToRoot(Region region)
    {
        // WE'LL PUT ALL THE REGIONS IN THE PATH HERE
        LinkedList<Region> path = new LinkedList();

        // PUT THE REGION ITSELF
        path.add(region);
        
        Region regionToAdd = region.getParentRegion();
        while (regionToAdd != null)
        {
            // APPEND THE TO THE END OF THE LIST
            path.addLast(regionToAdd);
            regionToAdd = regionToAdd.getParentRegion();
        }
        // AND RETURN THE PATH
        return path;
    }

    /**
     * Tests to see if the testRegion argument is part of the current
     * world or not.
     * 
     * @param testRegion The region to test.
     * 
     * @return true if testRegion is in the current world, false otherwise.
     */
    public boolean hasRegion(Region testRegion)
    {
        return (allRegions.containsKey(testRegion.getName()));
    }

    // MUTATOR METHODS
    
    /**
     * Mutator method for setting the root of the world.
     * 
     * @param initRoot The region that will become the root of the world.
     * All other regions will be descendant regions of this region.
     */
    public void setRoot(Region initRoot)
    {
        // PUT THE ROOT IN THE LIST WITH ALL THE OTHER REGIONS
        allRegions.put(initRoot.getName(), initRoot);
        
        // AND MAKE IT THE ROOT
        world = initRoot;
    }

    /**
     * Mutator method for setting the file reader/writer. Note that
     * WorldImporterExporter is an interface, so a concrete 
     * implementation of XML file reading and writing of a world
     * must be provided by some other class.
     * 
     * @param wie The XML region file reader/writer object.
     */
    public void setWorldImporterExporter(WorldImporterExporter wie)
    {
        worldImporterExporter = wie;
    }
    
    // ADDITIONAL SERVICE METHODS
    
    /**
     * Adds a region to this world, which includes hooking up the linkage
     * between the parent and child nodes.
     * 
     * @param regionToAdd The node to add to this world.
     */
    public void addRegion(Region regionToAdd, Region parentRegion)
    {
        allRegions.put(regionToAdd.getName(), regionToAdd);
        if (parentRegion != null)
        {
            parentRegion.addSubRegion(regionToAdd);
            regionToAdd.setParentRegion(parentRegion);
        }
    }
     
    /**
     * Adds a region to this world to the list of all regions, but does
     * not add it to the world. That must be done by hooking up the
     * parent and child nodes separately.
     * 
     * @param regionToAdd Region to add to the list of all regions.
     */
    public void addRegion(Region regionToAdd)
    {
        allRegions.put(regionToAdd.getName(), regionToAdd);
    }
  
    /**
     * Removes all of the regions from the world.
     */
    public void clearRegions()
    {
        allRegions.clear();
    }
 
    /**
     * Removes the regionToRemove argument from this world. Note that
     * this method removes the region from the list of all regions
     * and decouples it from its parent region.
     * 
     * @param regionToRemove Region to remove from the world.
     */    
    public void removeRegion(Region regionToRemove)
    {
        allRegions.remove(regionToRemove.getName());
        Region parentOfRegionToRemove = regionToRemove.getParentRegion();
        if (parentOfRegionToRemove != null)
        {
            parentOfRegionToRemove.removeSubRegion(regionToRemove);
        }
    }

    /**
     * Empties the world of all regions spare one, a new one that will be
     * the root world region with worldName as its name.
     * 
     * @param worldName The name of the new empty world we are resetting to.
     */
    public void reset(String worldName)
    {
        // GET RID OF ALL REGIONS0
        allRegions.clear();
        
        // AND NOW MAKE OUR ROOT WORLD REGION
//        world = new Region(worldName, worldName, RegionType.WORLD);
//        allRegions.put(worldName, world); 
    }    
 
    // FILE READING/WRITING METHODS - NOTE THAT THESE FUNCTION 
    // IMPLEMENTATIONS WOULD BE PROVIDED BY THE IMPORT/EXPORT PLUGIN

    /**
     * Loads the contents of fileToLoad into this world.
     * 
     * @param fileToLoad XML file that describes a world.
     * 
     * @return true if the world loaded successfully, false otherwise.
     */
    public boolean load(File fileToLoad)
    {
        return worldImporterExporter.loadWorld(fileToLoad, this);
    } 

    /**
     * Saves the current world to the fileToSave file.
     * 
     * @param fileToSave XML file to write this world to.
     * 
     * @return true if the world saved successfully, false otherwise.
     */
    public boolean save(File fileToSave)
    {
        return worldImporterExporter.saveWorld(fileToSave, this);
    }
}