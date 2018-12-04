/**
 * Class for every plugin.
 * @author srmeyer
 *
 */
public class Plugin 
    extends Props {
    private static String[] requiredFields = {"name", "test"};
    public Plugin(String file) throws Exception {
        super(file);
        
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
