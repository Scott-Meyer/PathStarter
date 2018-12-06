import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

/**
 * https://www.baeldung.com/java-9-process-api has children stuffs.
 * TODO make the plugin startup functions run multithreaded.
 * @author srmeyer
 *
 */
public class path_starter {
    /**
     * Gets the program running.
     * @param args
     */
    private static Settings settings;
    private final static Logger gLogger = Logger.getGlobal();
    private final static Logger LOGGER = Logger.getLogger(path_starter.class.getName());
    public static HashMap<String, Plugin> plugins;
    public static Path path;

    public static void main(String[] args) {
        //ARG why did I bother with logging then not research it fully. Here, this'll do.
        System.setProperty("java.util.logging.SimpleFormatter.format","%4$s: %5$s [%2$s]%n");
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter());
        gLogger.addHandler(handler);


        settings = new Settings();
        plugins = initializePlugins(settings.plugins);
        startup();


        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        //executorService.scheduleAtFixedRate(path_starter::myTask, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * Initialize the plugin objects based on the plugins property.
     * @return
     */
    private static HashMap<String, Plugin> initializePlugins(String[] plgs) {
        HashMap<String, Plugin> plugins = new HashMap<>();

        //Class loader is used to load classes based on string names.
        JavaClassLoader classLoader = new JavaClassLoader();
        Object plgObject;

        //For every plugin in "plugins"
        for (String plugin : plgs) {
            plgObject = classLoader.getObject(plugin);
            if (plgObject instanceof Plugin) {
                plugins.put(plugin, (Plugin)plgObject);
            } else {
                LOGGER.warning("Plugin: "+plugin+" failed to load. Removed.");
                plugins.remove(plugin);
            }
        }
        return plugins;
    }
    /**
     * This function is a busy loop that keeps track of everything.
     */
    private static void myTask() {
        System.out.println(path.isPathOpen());
        for(String plugin : plugins.keySet()) {
            plugins.get(plugin).run();
        }
    }

    /**
     * This handles things that need to be done at startup.
     * 1. Check lootfilter download status.
     */
    private static void startup() {
        for(String plugin : plugins.keySet()) {
            plugins.get(plugin).startup(settings);
        }
        //path = new Path(settings);
    }
}
