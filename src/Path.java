import java.io.IOException;
import java.util.logging.Logger;

/**
 * This is the class that handles path!
 * TODO the ability to notice when path closes.
 */

public class Path {

    private Settings settings;
    private boolean steam;
    private Process process;
    private final static Logger LOGGER = Logger.getLogger(Path.class.getName());
    
    public Path(Settings settings) {
        this.settings = settings;
        if (settings.isSteam()) {
            LOGGER.info("Starting Steam Path");
            steam = true;
            steamPath();
        } else {
            LOGGER.info("Starting Normal Path");
            steam = false;
            normalPath();
        }
        
    }

    private void steamPath() {
        try {
            System.out.println(settings.pathExeLocation());
            process = new ProcessBuilder(settings.pathExeLocation()).start();
            System.out.println(process.isAlive());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void normalPath() {
        try {
            process = new ProcessBuilder(settings.pathExeLocation()).start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * This should return weather or not POE is currently open.
     * @return
     */
    public boolean isPathOpen() {
        return process.isAlive();
        /*
        if (steam) {
            return true;
        } else {
            return process.isAlive();
        }
        */
    }
}
