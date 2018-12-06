import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;


/**
 * Gets the settings from properties.
 * TODO search a plugin folder for all .plugin files, add their settings into this object cleanly.
 * TODO saving of settings that get changed (Probably wait for GUI implementation)
 * @author srmeyer
 *
 **/
public class Settings 
    extends Props{
    private final static Logger LOGGER =
            Logger.getLogger(Settings.class.getName());
    private static String defaultPropertiesFile = "defaultProperties";
    private static String userPropertiesFile = "userProperties";
    public static String[] plugins;

    public Settings() {
        super(userPropertiesFile, setupDefaults());
        plugins = properties.getProperty("plugins").split(",");

        /*
        for (String plugin : applicationProps.getProperty("plugins").split(",")) {
            try {
                Plugin plg = new Plugin(plugin+".plugin");
                plugins.put(plugin, plg);
            } catch(Exception e) {
                e.printStackTrace();
            }
            
        }
        */
    }
    
    /**
     * Setup the default properties to be used by the Props class
     * @return Properties
     */
    private static Properties setupDefaults() {
        // create and load default properties
        Properties defaultProps = new Properties();
        FileInputStream in = null;
        try {
            in =new FileInputStream(defaultPropertiesFile);
            defaultProps.load(in);
            in.close();
        
            // create application properties with default
            Properties applicationProps = new Properties(defaultProps);
        
            // if there are is not a last load properties, set one up.
            File afile = new File(userPropertiesFile);
            if (!(afile.isFile())) {
                LOGGER.warning("First run, using default properties");
            }
        } catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return defaultProps;
    }
    
    /**
     * return weather or not steam is used to start POE.
     * If the property doesn't exist, assume false.
     * @return
     */
    public boolean isSteam() {
        if (getSetting("steam") != null) {
            String steam = properties.getProperty("steam").toLowerCase();
            if (steam.equals("true"))
                return true;
            else
                return false;
        } else {
            return false;
        }
    }
    
    public String pathExeLocation() {
        if (getSetting("poe_exe") != null) {
            return getSetting("poe_exe");
        } else {
            LOGGER.severe("PATH EXE LOCATION MISSING!");
        }
        System.exit(1);
        return null;
    }

    public String getSetting(String setting) {
        return properties.getProperty(setting);
    }

    public void setSetting(String setting, String value) {
        properties.setProperty(setting, value);
        save();
    }

}
