import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.HttpResponse;
import org.json.JSONObject;
import java.io.*;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * The intention of this class is to update loot filters.
 *
 * TODO download loot filters, save/store version of loot filters. Start with just neversink.
 * TODO isolate downloading, and make it go to empty tmp folder.
 * @author srmeyer
 *
 */
public class LootFilters
    implements Plugin{
    private final static Logger LOGGER =
            Logger.getLogger(LootFilters.class.getName());
    private Settings settings;
    private JSONObject json;
    public LootFilters() { }

    @Override
    public void startup(Settings s) {
        settings = s;
        String lootFilterLocation = lootFilterLocation();
        LOGGER.info("Loot filter location: "+lootFilterLocation);

        //TODO this should be dynamic but w/e I want a working prototype.
        String filterName = "neversink";
        String filterGit = "https://api.github.com/repos/NeverSinkDev/NeverSink-Filter";
        JSONObject jo = new JSONObject(getJson(filterGit+"/releases/latest"));


        //This downloads the file TODO
        if (isNewer(filterName, jo)) {
            LOGGER.info("Downloading "+filterName);
            try {
                URL neversink = new URL(jo.get("zipball_url").toString());
                File savedFile = new File(filterName + ".zip");
                FileUtils.copyURLToFile(neversink, savedFile);
                settings.setSetting("LootFilters." + filterName + ".downloadedPublishTime", jo.get("published_at").toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Unzip the file to filterName
            //TODO possibly replace with  zip4j, also stabalize the code, generalize, move... update error handling. close out things...  IOUtils.closeQuietly(out). SO on.
            File savedFile = new File(filterName + ".zip");
            try {
                java.util.zip.ZipFile zipFile = new ZipFile(savedFile);
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    File entryDestination = new File(filterName, entry.getName());
                    if (entry.isDirectory()) {
                        entryDestination.mkdirs();
                    } else {
                        entryDestination.getParentFile().mkdirs();
                        InputStream in = zipFile.getInputStream(entry);
                        OutputStream out = new FileOutputStream(entryDestination);
                        IOUtils.copy(in, out);
                        IOUtils.closeQuietly(in);
                        out.close();
                    }
                }
                zipFile.close();
            } catch (Exception e) {
                e.printStackTrace();
            }


            //Move the main filter files
            //TODO generalize/move/handleerrors.... you get it by now.
            File dir = new File(filterName);
            File dest = new File(lootFilterLocation);
            String[] extensions = new String[]{"filter"};
            List<File> files = (List<File>) FileUtils.listFiles(dir, extensions, true);
            for (File file : files) {
                try {
                    FileUtils.copyFileToDirectory(file, dest);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void run() {

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
    private String getJson(String url) {
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
     * @param filterName
     * @param jsonResponce
     * @return
     */
    private Boolean isNewer(String filterName, JSONObject jsonResponce) {
        String previousTimeString = settings.getSetting("LootFilters."+filterName+".downloadedPublishTime");
        //If there is not a date that the previous download happened, we need to download filters.
        if (previousTimeString == null) {
            LOGGER.info("Downloading "+filterName+" Because there is no record of a previous download.");
            return true;
        } else {
            //Get a Calendar based on the settings
            GregorianCalendar previousTimeCal = strToCal(previousTimeString);
            //Get a Calendar based on the json responce.
            GregorianCalendar jsonTimeCal = strToCal(jsonResponce.get("published_at").toString());
            if (jsonTimeCal == null || previousTimeCal == null) {
                LOGGER.info("Downloading "+filterName+" Because one of the calendars couldn't be made.");
                return true;
            } else if (jsonTimeCal.compareTo(previousTimeCal) == -1) {
                LOGGER.info("Downloading "+filterName+" because there is a newer version.");
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
