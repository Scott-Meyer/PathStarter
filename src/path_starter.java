import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * https://www.baeldung.com/java-9-process-api has children stuffs.
 * @author srmeyer
 *
 */
public class path_starter {
    /**
     * Gets the program running.
     * @param args
     */
    public static void main(String[] args) {
        Settings settings = new Settings();


        //Lets do something for each plugin.
        for (String plg : settings.plugins.keySet()) {
            Plugin plugin = settings.plugins.get(plg);
            System.out.println(plugin);
            System.out.println(plugin.name());
        }
        System.out.println("Hello World");
        
        LootFilters l = new LootFilters();
        
        try {
            Process process = new ProcessBuilder("C:\\Windows\\system32\\cmd.exe").start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        //final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        //executorService.scheduleAtFixedRate(path_starter::myTask, 0, 1, TimeUnit.SECONDS);
    }
    /**
     * This function is a busy loop that keeps track of everything.
     */
    private static void myTask() {
        System.out.println("Hello");
    }
}
