/**
 * Plugins must implement plugin.
 */
public interface Plugin {

    /**
     * This will be run when the path starter is started.
     */
    public void startup(Settings s);

    /**
     * This will be run every single time the main app busy waits.
     */
    public void run();
}
