import java.io.IOException;

public class Path {

    private Settings settings;
    private boolean steam;
    private Process process;
    
    public Path(Settings settings) {
        this.settings = settings;
        if (settings.isSteam()) {
            steam = true;
            steamPath();
        } else {
            steam = false;
            normalPath();
        }
        
    }
    
    private void steamPath() {
        //Desktop.getDesktop().browse("steam://run/666");
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
        if (steam) {
            return true;
        } else {
            return process.isAlive();
        }
    }
}
