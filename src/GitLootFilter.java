import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.HttpResponse;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

/**
 * The intention of this class is to be extended for specific git based loot filters.
 * @author srmeyer
 *
 */
public class GitLootFilter {
    protected final static Logger LOGGER =
            Logger.getLogger(GitLootFilter.class.getName());
    protected Settings settings;
    protected String lootFilterLocation;
    public GitLootFilter() { }

    public void startup(Settings s) {
        settings = s;
        lootFilterLocation = lootFilterLocation();
        LOGGER.info("Loot filter location: "+lootFilterLocation);
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

    /**
     * This should probably be out in another class.
     * @param url
     * @return
     */
    protected String getJson(String url) {
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {

            HttpGet httpGet = new HttpGet("https://api.github.com/repos/NeverSinkDev/NeverSink-Filter/releases/latest");
            HttpResponse response = httpClient.execute(httpGet);
            if (response.getCode() != 200) {
                LOGGER.severe("repsonce from github not 200. AHHHHHHH Scary");
            }

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(((CloseableHttpResponse) response).getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            return result.toString();

        } catch (IOException e) {

            // handle
            return null;
        }
    }

    /**
     * returns weather or the the git release is newer than the last thing downloaded
     * Note: Assumes newer if it can't confirm last download is older.
     * @param previousTimeString
     * @param gitJSONObject
     * @return true if git release is newer.
     */
    protected Boolean isNewerGitRelease(String previousTimeString, JSONObject gitJSONObject) {
        //If there is not a date that the previous download happened, we need to download filters.
        if (previousTimeString == null) {
            LOGGER.info("Git release is newer because there is no record of a previous download.");
            return true;
        } else {
            //Get a Calendar based on the settings
            GregorianCalendar previousTimeCal = strToCal(previousTimeString);
            //Get a Calendar based on the json responce.
            GregorianCalendar jsonTimeCal = strToCal(gitJSONObject.get("published_at").toString());
            if (jsonTimeCal == null || previousTimeCal == null) {
                LOGGER.info("Git release is newer because one of the calendars couldn't be made.");
                return true;
            } else if (jsonTimeCal.compareTo(previousTimeCal) == -1) {
                LOGGER.info("Git release is newer because there is a newer version.");
                return true;
            } else {
                return false;
            }
        }
    }
    private GregorianCalendar strToCal(String dateTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        GregorianCalendar gc = new GregorianCalendar();
        try {
            gc.setTime(sdf.parse(dateTime));
        } catch (ParseException e) {
            //TODO this.
            e.printStackTrace();
            return null;
        }
        return gc;
    }
}
