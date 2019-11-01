package services.implementations;


import models.TestDevice;
import spark.utils.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * For managing the instance of the appium driver with Thread local
 */
public class TestDeviceManager {

    private static final String A_DEVICES = "devices";
    private static final String A_VERSION = "ro.build.version.release";
    private static final String[] A_UUID = {"get", "secure", "android_id"};
    private static final String ADB = System.getenv("ANDROID_HOME") + "/platform-tools/adb";
    /**
     * iOS Commands
     */
    private static final String[] i_DEVICES = {"instruments", "-s", A_DEVICES};
    /**
     * ADB Commands [Android]
     */
    private static PrintStream LOG = System.out;
    private static List<TestDevice> testDevices = null;

    public static List<TestDevice> getTestDevices() {
        return testDevices;
    }

    public static void setTestDevices(List<TestDevice> devices) {
        testDevices = devices;
    }

    /**
     * @return
     */
    public static List<TestDevice> getConnectedDevices() {
        List<TestDevice> testDeviceList = new ArrayList<>();
        String devices[] = null;

        LOG.print("Read ANDROID_HOME  : '" + ADB + "'.");

        if (StringUtils.isEmpty(ADB)) {
            LOG.print("Set environment variable 'ANDROID_HOME' to continue.\n Hint: if ANDROID_HOME is set, try running the tool with root privileges.");
            return testDeviceList;
        }

        // get android devices
        String androidDevices = execute(ADB, A_DEVICES);
        if (StringUtils.isEmpty(androidDevices)) {
            LOG.print("ANDROID devices not found");
        }
        devices = androidDevices.split("\\r?\\n");
        for (String device : devices) {
            LOG.print("PROCESSING - '" + device);
            String temp[] = device.split("\\s+");
            if (temp.length != 2) {
                continue;
            }
            String deviceName = temp[0];
            String platformVersion = adbShell(deviceName, "getprop", A_VERSION).replace("\n", "");
            String udid = adbShell(deviceName, "settings", A_UUID).replace("\n", "");

            testDeviceList.add(new TestDevice(deviceName, "ANDROID", platformVersion, udid));
        }

        // get ios devices
        /*
         * Alternativey, we can also use this logic to get all devices : String
         * iosDevices = UtilityHelper.execute( "idevice_id","-l" ); devices =
         * iosDevices.split( "\\r?\\n" ); for ( String device : devices ) { String
         * allInfo = UtilityHelper.execute( "ideviceinfo", "-u", device); }
         */
        String iosDevices = execute(i_DEVICES);
        if (StringUtils.isEmpty(iosDevices)) {
            System.out.println("iOS devices not found");
        }

        devices = iosDevices.split("\\r?\\n");
        for (String device : devices) {
            if (device.contains("(Simulator)")) {
                continue;
            }

            List<String> match = getRegexMatch("\\(.*\\)\\s\\[.*\\]", device);
            if (match.size() > 0) {
                String deviceID = getRegexMatch("\\[(.*)\\]", device).get(1);
                String platformVersion = getRegexMatch("\\((\\d.*)\\)", device).get(1);
                String deviceName = getRegexMatch("(.*?)\\(", device).get(1);
                TestDevice testDevice = new TestDevice(deviceName, "IOS", platformVersion, deviceID);
                System.out.println("Added device - '" + testDevice + "'");
                testDeviceList.add(testDevice);
            }
        }
        System.out.println("Found " + testDeviceList.size() + " live device(s)");
        return testDeviceList;
    }

    /**
     * Utility to get regex matches
     *
     * @param regex
     * @param input
     * @return List<String> containing all matches
     */
    public static List<String> getRegexMatch(String regex, String input) {
        List<String> matches = new ArrayList<>();

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);

        while (m.find()) {
            for (int i = 0; i <= m.groupCount(); i++) {
                matches.add(m.group(i));
            }
        }
        return matches;
    }

    /**
     * Execute the command on system and returns its output as String
     *
     * @param cmd
     * @return String, output of command if none empty string is returned
     */
    public static String execute(String... cmd) {
        System.out.println("excuting command line - '" + String.join(",", cmd) + "'");

        String NEW_LINE = System.getProperty("line.separator");
        StringBuilder response = new StringBuilder();
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(cmd);
            // from the doc: initially, this property is false, meaning that the
            // standard output and error output of a subprocess are sent to two
            // separate streams
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append(NEW_LINE);
            }
            process.waitFor();
            in.close();
        } catch (IOException | InterruptedException ex) {
            response.append(ex.getMessage());
        }
        String res = response.toString();
        LOG.print("Command line response - " + res);
        return res;
    }

    public static String adbShell(
            String deviceName, String action, String... shellCommand) {
        List<String> commands = new ArrayList<>();

        commands.add(ADB);
        commands.add("-s");
        commands.add(deviceName);
        commands.add("shell");

        switch (action) {
            case "action":
                commands.add("am");
                commands.add("start");
                commands.add("-a");
                break;

            case "getprop":
                commands.add("getprop");
                break;

            case "settings":
                commands.add("settings");
                break;

            default:
                LOG.print("Shell action '" + action + "' is not yet supported.");
                return "";
        }

        for (String s : shellCommand) {
            commands.add(s);
        }
        LOG.print("Shell command - " + commands);
        return execute(commands.toArray(new String[0]));
    }
}

