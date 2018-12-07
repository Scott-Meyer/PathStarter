import org.apache.commons.io.FileUtils;
import org.jutils.jprocesses.JProcesses;
import org.jutils.jprocesses.model.ProcessInfo;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.logging.*;
/**
 * Class for every plugin.
 * This class is a basic class for starting up all apps which either have a .app file in ./apps/ or have
 * a property BasicAppStarter.[appName] in the main settings file.
 * TODO this should extend a more basic app class to be property OO.
 * TODO Starting of the apps, monitoring if the apps are running, updating the apps.
 * TODO I converted this from something else, so there is a LOT of wasted code and work. Finding Files then making them strings, then finding them as Files again. FIX IT.
 * @author srmeyer
 *
 */
public class BasicAppStarter
    implements Plugin{
    private final static Logger LOGGER = Logger.getLogger(BasicAppStarter.class.getName());
    private Settings settings;
    private final static String[] requiredFields = {"file_location"}; //Fields that every app file should have. Error out the app class if the file doesn't have this.
    private Set<String> ignoredApps;    //All the ignored apps. This comes from settings as "BasicAppStarter.IgnoredApps=something,something"
    private HashMap<String, App> apps;  //Hash of all the App objects. appName, AppObject.
    private HashMap<String, Boolean> isAppRunning; //Hash of all apps and weather or not they are running. Initiated when needed.
    private String[] appFiles;

    public BasicAppStarter() {
        apps = new HashMap<>();
    }

    /**
     * This is run once when PathStarter stars.
     * In this case, it finds all .app files in the "apps" folder, then creates an "App" object for each file.
     * Then runs App.start() which should start up that program.
     * @param s Settings.
     */
    @Override
    public void startup(Settings s) {
        settings = s;
        String ignoredApps = settings.getSetting("BasicAppStarter.IgnoredApps");
        if (ignoredApps != null)
            this.ignoredApps = new HashSet<>(Arrays.asList(settings.list(ignoredApps)));
        else
            this.ignoredApps = new HashSet<>();
        //Get all app names.
        appFiles = getAppFiles();

        //If checkIfRunning(), make a Hash for weather or not all apps are running.
        //Make all app objects, add them to hash.
        for (String appName : appFiles) {
            try {
                LOGGER.info("Loading app: " + appName);
                App app = new App("apps/" + appName + ".app");
                apps.put(appName, app);
            } catch (Exception e) {
                LOGGER.warning("App: " + appName + " failed to load. " + e);
            }
        }
        //Startup all the apps.
        for (String appName : appFiles) {
            try {
                //plugin wide checkIfRunning implementation.
                if (!checkIfRunning() || !isRunning(appName)) {
                    LOGGER.info("Starting " + appName);
                    apps.get(appName).start();
                } else {
                    LOGGER.info("Not starting " + appName + " Because its already running. (Main)");
                }
            } catch (Exception e) {
                LOGGER.warning("App: " + appName + " failed to load. " + e);
            }
        }
    }


    /**
     * Get the app files from apps/[appName].app
     * @return array of app names
     */
    public String[] getAppFiles() {
        File appsFolder = new File("apps");
        String[] extensions = new String[]{"app"};
        java.util.List<File> appFiles = (List<File>) FileUtils.listFiles(appsFolder, extensions, true);
        return appFiles.stream().map(file -> file.getName().split("\\.")[0])
                //Filter out the ignoredApps from settings.
                .filter(file -> !ignoredApps.contains(file)).toArray(String[]::new);
    }

    /**
     * Make apps/[appname].app files out of properties.
     * This is unfinished, because I havn't implemented this feature yet.
     */
    public void makeTempAppFiles() {
        //Get a list of all settings that correspond to this app.
        String[] appsFromSettings = settings.getPluginSettings(this.getClass().getName());
        //Filter the list, so it only has [pluginName].apps.[appName].*
        appsFromSettings = (String[])Arrays.stream(appsFromSettings)
                .filter((x) -> {
                String[] parts = x.split("\\.");
                if (parts.length > 3 && parts[2] == "apps")
                    return true;
                else
                    return false;
                }).toArray();
        for (String setting : appsFromSettings) {

            File appFile = new File("apps/"+setting.split(".")[3]+".app");
            appFile.canWrite();
        }
    }

    /**
     * Check the setting checkIfRunning.
     */
    private Boolean checkIfRunning() {
        return settings.getBoolSetting(this.getClass().getName() + ".CheckIfRunning", false);
    }

    /**
     * WARNING this is slow as hell, and could give false positives.
     * TODO IMPROVE.
     * @param appName
     * @return
     */
    private Boolean isRunning(String appName) {
        //Initalized the hashmap if its not there.
        if (isAppRunning == null) {
            initializeIsAppRunning();
        }
        if (isAppRunning.containsKey(appName))
            return isAppRunning.get(appName);
        else {
            LOGGER.severe("App name not found in isAppRunning");
            return false;
        }
    }

    private void initializeIsAppRunning() {
        isAppRunning = new HashMap<>();

        //make the isAppRunning the 'special' way for IncludeCommand.
        if (settings.getBoolSetting(this.getClass().getName()+".CheckIfRunning.IncludeCommand", false)) {
            List<ProcessInfo> processesList = JProcesses.getProcessList();
            String piName, piCommand;
            for (final ProcessInfo processInfo : processesList) {
                piName = processInfo.getName();
                piCommand = processInfo.getCommand();
                if (piName.equals("AutoHotkey.exe"))
                for (String name : appFiles) {
                    String filename = apps.get(name).processName();
                    if (piName.contains(filename) || piCommand.contains(filename)) {
                        isAppRunning.put(name, true);
                    }
                }
            }
        } else {
            ProcessHandle.allProcesses().forEach(process -> {
                String command = process.info().command().map(Object::toString).orElse("-");
                for (String name : appFiles) {
                    if (command.contains(apps.get(name).processName()))
                        isAppRunning.put(name, true);
                }
            });
        }



        for (String name : appFiles) {
            if (! isAppRunning.containsKey(name))
                isAppRunning.put(name, false);
        }
    }

    /**
     * Specfic apps for the app plugin
     */
    public class App
        extends Props {
        private String fileLocation;
        private String appName;

        /**
         * @param file This should be a .app file which is a java properties file.
         * @throws Exception
         */
        public App(String file) throws Exception {
            super(file);

            LOGGER.info("Loading: " + file);
            appName = file.split("\\.")[0];
            // Check this is a valid properties file.
            for (String field : requiredFields) {
                if (!properties.containsKey(field)) {
                    throw new Exception(file + " is missing " + field);
                }
                fileLocation = fileLocation();
            }
        }

        /**
         * Start up this app.
         */
        public void start() {
            //Utilize in order if checking. If getBoolSetting is false, its true, and if goes through.
            if (actuallyStart()) {
                try {
                    if (properties.getProperty("is_exe") != null && properties.getProperty("is_exe").toLowerCase().equals("true")) {
                        Process process = new ProcessBuilder(fileLocation).start();
                    } else {
                        File file = new File(fileLocation);

                        //first check if Desktop is supported by Platform or not
                        if (!Desktop.isDesktopSupported()) {
                            System.out.println("Desktop is not supported");
                            return;
                        }
                        Desktop desktop = Desktop.getDesktop();
                        if (file.exists()) desktop.open(file);
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else
                LOGGER.info("Not starting "+appName+" Because its already running.");
        }

        /**
         * Get the fileLocation (this should exist because its required).
         *
         * @return file location String
         */
        public String fileLocation() {
            return properties.getProperty("file_location");
        }

        /**
         * Get the name of the file that is going to be run. Needed to implement isAppRunning stuffs.
         * @return
         */
        public String processName() {
            if (properties.getProperty("process_name") != null) {
                return properties.getProperty("process_name");
            } else {
                String[] fileparts = fileLocation.split("/");
                return fileparts[fileparts.length - 1];
            }
        }

        private Boolean actuallyStart() {
            //For sure start if there is global check if running. That means the check has already been complete
            if (checkIfRunning()) {
                return true;
            }
            // If not, see if local CheckIfRunning is true
            else if (getBoolSetting("CheckIfRunning", false)) {
                if (isRunning(appName)) {
                    LOGGER.info("Not starting " + appName + " Because its already running. (APP)");
                    return false;
                } else
                    return true;
            }
            //No check of running. Just run
            else {
                return true;
            }
        }
    }
}