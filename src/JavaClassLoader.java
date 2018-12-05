import java.lang.reflect.Constructor;
import java.util.logging.Logger;

/**
 * Load the dynamic plugins. Not sure if this is how its normally done but going off this:
 * https://examples.javacodegeeks.com/core-java/dynamic-class-loading-example/
 */
public class JavaClassLoader extends ClassLoader {

    private final static Logger LOGGER =
            Logger.getLogger(JavaClassLoader.class.getName());
    /**
     * Get the objects with givin name.
     * TODO look into singletons.
     * @param classBinName
     * @return
     */
    public Object getObject(String classBinName){

        try {

            // Create a new JavaClassLoader
            ClassLoader classLoader = this.getClass().getClassLoader();

            // Load the target class using its binary name
            Class loadedMyClass = classLoader.loadClass(classBinName);

            LOGGER.info("Loaded class name: " + loadedMyClass.getName());

            // Create a new instance from the loaded class
            Constructor constructor = loadedMyClass.getConstructor();
            Object myClassObject = constructor.newInstance();

            return myClassObject;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
