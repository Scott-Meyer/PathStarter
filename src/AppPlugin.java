import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.*;
/**
 * Class for every plugin.
 * TODO Starting of the apps, monitoring if the apps are running, updating the apps, using the settings files in any way.
 * @author srmeyer
 *
 */
public class AppPlugin
    implements Plugin{
    private final static Logger LOGGER =
            Logger.getLogger(AppPlugin.class.getName());
    private Settings settings;
    private static String[] requiredFields = {"file_location"};
    private HashMap<String, App> apps;
    public AppPlugin() {
        apps = new HashMap<>();
    }

    @Override
    public void startup(Settings s) {
        settings = s;
        String[] appNames = getAppNames();
        App app;
        for (String appName : appNames) {
            try {
                app = new App(appName);
                apps.put(appName, app);
                app.start();
            } catch (Exception e) {
                LOGGER.warning("App: "+appName+" failed to load. "+e);
            }
        }
    }

    @Override
    public void run() {

    }

    /**
     * Get the app names out of properties.
     * @return array of app names
     */
    public String[] getAppNames() {
        String apps = settings.getSetting("AppPlugin.Apps");
        if (apps != null) {
            String[] appNames = apps.split(",");
            for (int i=0; i < appNames.length; i++) {
                appNames[i] = appNames[i].trim();
                appNames[i] += ".app";
            }
            return appNames;
        }
        return new String[]{};
    }

    /**
     * Specfic apps for the app plugin
     */
    public class App
        extends Props {
        private String fileLocation;
        public App(String file) throws Exception {
            super(file);

            LOGGER.info("Loading: "+file);
            // Check this is a valid properties file.
            for (String field : requiredFields) {
                if (! properties.containsKey(field)) {
                    throw new Exception(file + " is missing "+field);
                }
                fileLocation = fileLocation();
            }
        }
        public void start() {
            try {
                if (properties.getProperty("is_exe") != null && properties.getProperty("is_exe").toLowerCase().equals("true")) {
                        Process process = new ProcessBuilder(fileLocation).start();
                } else {
                    File file = new File(fileLocation);

                    //first check if Desktop is supported by Platform or not
                    if(!Desktop.isDesktopSupported()){
                        System.out.println("Desktop is not supported");
                        return;
                    }
                    Desktop desktop = Desktop.getDesktop();
                    if(file.exists()) desktop.open(file);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        public String fileLocation() {
            return properties.getProperty("file_location");
        }
        public String name() {
            return properties.getProperty("name");
        }
        public String test() {
            return properties.getProperty("test");
        }

        public String toString() {
            return super.toString() +
                    ", name = " + name() +
                    ", test = " + test();
        }
    }
}