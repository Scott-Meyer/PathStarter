import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

/**
 * Gets the settings from properties.
 * @author srmeyer
 *
 **/
public class Settings 
    extends Props{
    private static String defaultPropertiesFile = "defaultProperties";
    private static String userPropertiesFile = "userProperties";
    public static Properties applicationProps;
    public HashMap<String, Plugin> plugins = new HashMap<String, Plugin>();
    public Settings() {
        super(userPropertiesFile, setupDefaults());
        for (String plugin : applicationProps.getProperty("plugins").split(",")) {
            try {
                Plugin plg = new Plugin(plugin+".plugin");
                plugins.put(plugin, plg);
            } catch(Exception e) {
                e.printStackTrace();
            }
            
        }
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
            applicationProps = new Properties(defaultProps);
        
            // if there are is not a last load properties, set one up.
            File afile = new File(userPropertiesFile);
            if (!(afile.isFile())) {
                System.out.println("First run, using default properties");
            }
        } catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return defaultProps;
    }
    
    /**
     * Get all of the plugins.
     * key = "plugins", they are comma seperated.
     */
    private String[] getPluginFiles() {
        String[] plugins = applicationProps.getProperty("plugins").split(",");
        for(int i = 0; i < plugins.length; i++) {
            plugins[i] += ".plugin";
        }
        return plugins;
        //return Arrays.stream(applicationProps.getProperty("plugins").split(","))
        //        .map(s -> s += ".plugin").toArray(String[]::new);
    }
    
    /**
     * return weather or not steam is used to start POE.
     * If the property doesn't exist, assume false.
     * @return
     */
    public boolean isSteam() {
        if (applicationProps.containsKey("steam")) {
            String steam = applicationProps.getProperty("steam").toLowerCase();
            if (steam.equals("true"))
                return true;
            else
                return false;
        } else {
            return false;
        }
    }
    
    public String pathExeLocation() {
        return applicationProps.getProperty("poe_exe");
    }
}
