import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * This plugin updates your Neversink filter.
 * Accepted settings to add to your settings file are "NeversinkUpdater.color"
 * TODO Split up the code. stupid code blocks.
 */
public class NeversinkUpdater
    extends GitLootFilter
    implements Plugin {
    private static final Logger LOGGER = Logger.getLogger(NeversinkUpdater.class.getName());
    private JSONObject json;

    public NeversinkUpdater() {}

    public void startup(Settings s) {
        super.startup(s);

        //TODO this should be a setting but w/e I want a working prototype.
        String filterName = "neversink";
        String filterGit = "https://api.github.com/repos/NeverSinkDev/NeverSink-Filter";
        JSONObject jo = new JSONObject(getJson(filterGit+"/releases/latest"));


        //This downloads the file TODO
        String previousTimeString = settings.getSetting(this.getClass().getName()+".downloadedPublishTime");
        if (isNewerGitRelease(previousTimeString, jo)) {
            //Try to download the file. Also updated downloaded time.
            Boolean downloaded = false;
            LOGGER.info("Downloading " + filterName);
            try {
                URL neversink = new URL(jo.get("zipball_url").toString());
                File savedFile = new File(filterName + ".zip");
                FileUtils.copyURLToFile(neversink, savedFile);
                settings.setSetting("LootFilters." + filterName + ".downloadedPublishTime", jo.get("published_at").toString());
                downloaded = true;
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (downloaded) {
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
                String color = settings.getSetting(this.getClass().getName() + ".color");
                List<File> files;
                if (color == null || color.equals("all")) {
                    LOGGER.info("Going to move all neversink filters into your filter folder. Enjoy that.");
                    files = (List<File>) FileUtils.listFiles(dir, extensions, true);
                } else {
                    LOGGER.info("Going to move "+color+" never sink filtes into your filter folder.");
                    files = (List<File>) FileUtils.listFiles(dir, new WildcardFileFilter("*" + color + "*" + ".filter"), TrueFileFilter.TRUE);
                }
                for (File file : files) {
                    try {
                        FileUtils.copyFileToDirectory(file, dest);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                LOGGER.severe("Failed to download a new neversink filter zip file. Having to skip update.");
            }
        }
    }
}
