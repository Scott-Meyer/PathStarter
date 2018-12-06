import java.io.IOException;
import java.util.logging.Logger;

/**
 * This is the class that handles path!
 * TODO the ability to notice when path closes.
 */

public class Path
    implements Plugin {

    private Settings settings;
    private boolean steam;
    private Process process;
    private final static Logger LOGGER = Logger.getLogger(Path.class.getName());
    
    public Path() { }

    public void startup(Settings s) {
        settings = s;
        if (isSteam()) {
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
            process = new ProcessBuilder(pathExeLocation()).start();
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
            process = new ProcessBuilder(pathExeLocation()).start();
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

    /**
     * return weather or not steam is used to start POE.
     * If the property doesn't exist, assume false.
     * @return
     */
    public boolean isSteam() {
        String steam = settings.getSetting(this.getClass().getName()+".steam");
        if (steam == null) {
            return false;
        } else if (steam.toLowerCase().equals("false")) {
            return false;
        } else {
            return true;
        }
    }

    public String pathExeLocation() {
        System.out.println(this.getClass().getName());
        if (settings.getSetting(this.getClass().getName()+".poe_exe") != null) {
            return settings.getSetting(this.getClass().getName()+".poe_exe");
        } else {
            LOGGER.severe("PATH EXE LOCATION MISSING!");
        }
        System.exit(1);
        return null;
    }
}
