package world_io;

/**
 * This class provides the constants needed for reading and writing worlds
 * to XML files using WorldSchema.xsd. This includes all the element and
 * attribute names.
 * 
 * @author  Richard McKenna
 *          Debugging Enterprises
 * @version 1.0
 */
public class WorldIOSettings
{
    // THIS IS THE NAME OF THE SCHEMA THAT THIS PLUGIN USES. NOTE THAT
    // THIS SCHEMA FILE WILL BE INCLUDED IN THE LIBRARY'S JAR FILE
    // WHEN WE DEPLOY IT
    public static final String  WORLD_REGIONS_SCHEMA = "WorldSchema.xsd";
    
    // CONSTANTS FOR LOADING DATA FROM THE XML FILES
    public static final String WORLD_ID = "World";
    public static final String WORLD_REGIONS_NODE = "world_regions";
    public static final String REGIONS_LIST_NODE = "regions_list";
    public static final String REGIONS_MAPPINGS_NODE = "regions_mappings";
    public static final String REGION_NODE = "region";
    public static final String SUB_REGION_NODE = "sub_region";
    public static final String RED_ATTRIBUTE = "red";
    public static final String GREEN_ATTRIBUTE = "green";
    public static final String BLUE_ATTRIBUTE = "blue";
    public static final String NAME_ATTRIBUTE = "name";
    public static final String LEADER_ATTRIBUTE = "leader";
    public static final String CAPITAL_ATTRIBUTE = "capital";

    // FOR NICELY FORMATTED XML OUTPUT
    public static final String XML_INDENT_PROPERTY = "{http://xml.apache.org/xslt}indent-amount";
    public static final String XML_INDENT_VALUE = "5";
    public static final String YES_VALUE = "yes";
}