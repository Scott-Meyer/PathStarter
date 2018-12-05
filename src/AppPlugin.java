import java.util.HashMap;
import java.util.logging.*;
/**
 * Class for every plugin.
 * @author srmeyer
 *
 */
public class AppPlugin
    implements Plugin{
    private final static Logger LOGGER =
            Logger.getLogger(AppPlugin.class.getName());
    private Settings settings;
    private static String[] requiredFields = {"name", "test"};
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
        String apps = settings.applicationProps.getProperty("AppPlugin.Apps");
        if (apps != null) {
            String[] appNames = apps.split(",");
            for (int i=0; i < appNames.length; i++) {
                appNames[i].trim();
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
        public App(String file) throws Exception {
            super(file);

            LOGGER.info("Loading: "+file);
            // Check this is a valid properties file.
            for (String field : requiredFields) {
                if (! properties.containsKey(field)) {
                    throw new Exception(file + " is missing "+field);
                }
            }
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