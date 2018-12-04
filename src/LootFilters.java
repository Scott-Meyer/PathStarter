import java.io.InputStream;

/**
 * The intention of this class is to update loot filters.
 * At some point the entire plugin system needs to be upgraded somehow
 * so that this can be a plugin. But w/e, even this is going to be done shitly/manually.
 * @author srmeyer
 *
 */
public class LootFilters {
    public LootFilters() {
        System.out.println(lootFilterLocation());
    }
    
    /**
     * Get location where loot filters should go
     * Thanks: https://stackoverflow.com/questions/9677692/getting-my-documents-path-in-java
     * @return
     */
    private String lootFilterLocation() {
        String myDocuments = null;
        
        try {
            Process p =  Runtime.getRuntime().exec("reg query \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders\" /v personal");
            p.waitFor();
    
            InputStream in = p.getInputStream();
            byte[] b = new byte[in.available()];
            in.read(b);
            in.close();
    
            myDocuments = new String(b);
            myDocuments = myDocuments.split("\\s\\s+")[4];
    
        } catch(Throwable t) {
            t.printStackTrace();
        }
        myDocuments += "\\My Games\\Path of Exile\\";
        return myDocuments;
    }
}
