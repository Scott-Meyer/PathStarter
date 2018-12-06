public interface RunningPlguin
    extends Plugin {
    /**
     * This will be run every single time the main app busy waits.
     */
    public void run();
}
