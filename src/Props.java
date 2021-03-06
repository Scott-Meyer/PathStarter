import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Properties class, to be extended by any class that needs to use properties.
 * @author srmeyer
 *
 */
public class Props {
    protected Properties properties = null;
    private String propertiesFile;

    public Props(String file) {
        this(file, null);
    }

    public Props(String file, Properties prop) {
        propertiesFile = file;
        // create and load default properties
        if (prop == null) {
            properties = new Properties();
        } else {
            properties = new Properties(prop);
        }

        try {
            File afile = new File(file);
            if (!(afile.isFile())) {
                System.out.println(file+" Does not exist. Creating empty file.");
                afile.createNewFile();
            }
            
            FileInputStream in = new FileInputStream(file);
            properties.load(in);
            in.close();
        } catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    /**
     * save the properties.
     */
    public void save() {
        try {
            FileOutputStream out = new FileOutputStream(propertiesFile);
            properties.store(out, "---No Comment---");
            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Boolean getBoolSetting(String s, Boolean defaultValue) {
        String setting = properties.getProperty(s);
        if (setting == null)
            return defaultValue;
        else if (setting.toLowerCase().equals("true"))
            return true;
        else
            return false;
    }
}
