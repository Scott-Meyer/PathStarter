/**
 * Plugins must implement plugin.
 */
public interface Plugin {

    /**
     * This will be run when the path starter is started.
     */
    public void startup(Settings s);
}
