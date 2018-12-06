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
 * TODO Make case not matter for getSetting.
 * TODO search a plugin folder for all .plugin files, add their settings into this object cleanly.
 * TODO saving of settings that get changed (Probably wait for GUI implementation)
 * TODO I liked messing around with the default settings file and local changes file, but while it was fun to learn, seems unessiary in practice. Atleast copy the defaults into the user file...
 * @author srmeyer
 *
 **/
public class Settings 
    extends Props{
    private final static Logger LOGGER =
            Logger.getLogger(Settings.class.getName());
    private static String defaultPropertiesFile = "default.properties";
    private static String userPropertiesFile = "user.properties";
    public static String[] plugins;

    public Settings() {
        super(userPropertiesFile, setupDefaults());
        plugins = list(properties.getProperty("plugins"));

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


    public String getSetting(String setting) {
        return properties.getProperty(setting);
    }

    public void setSetting(String setting, String value) {
        properties.setProperty(setting, value);
        save();
    }

    /**
     * if your property is a comma serperated list, this will set it up into an array.
     * @param ls comma seperated list
     * @return array of items.
     */
    public String[] list(String ls) {
        String[] ret = ls.split(",");
        for (int i = 0; i < ret.length; i++) {
            ret[i] = ret[i].trim();
        }
        return ret;
    }

    /**
     * Get the settings of a plugin.
     * @param plugin the name of a plugin
     * @return list of all properties that start with [pluginName].
     */
    public String[] getPluginSettings(String plugin) {
        return properties.stringPropertyNames().stream()
                .filter(x -> x.split("\\.")[0].equals(plugin)).toArray(String[]::new);
    }

}
