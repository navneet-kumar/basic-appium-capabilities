package services.implementations


import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject

import models.TestDevice
import org.apache.commons.lang3.SystemUtils
import spark.utils.StringUtils

/**
 * For managing the instance of the appium driver with Thread local
 */
object TestDeviceManager {
    private val logger = System.out
    val adb = System.getenv("ANDROID_HOME") + "/platform-tools/adb"


    /**
     * Fetch live ios device connected to your machine and return the listOf<TestDevice>
     */
    fun fetchLiveIosDevices(): List<TestDevice> {
        if (SystemUtils.IS_OS_WINDOWS) return emptyList() // windows machine

        // fetch active simulator's
        var listOfiOSDevices: List<TestDevice> = emptyList()
        var iosDevices = Gson().fromJson(Utility.execute("xcrun", "simctl", "list", "--json"), JsonObject::class.java)
        iosDevices = iosDevices["devices"] as JsonObject
        iosDevices.entrySet().forEach { entry ->
            if (entry.key.contains("iOS")) {
                (iosDevices[entry.key] as JsonArray).forEach {
                    val d = it as JsonObject
                    if (d["state"].asString == "Booted")
                        listOfiOSDevices = listOfiOSDevices.plus(
                                TestDevice(
                                        deviceName = d["name"].asString,
                                        platformName = "IOS",
                                        udid = d["udid"].asString,
                                        platformVersion = "\\d.*".toRegex().find(entry.key)!!.groups[0]!!.value.replace("-", ".")))
                }
            }
        }

        // fetch active real devices
        Utility.execute("instruments", "-s", "devices").split("\n").forEach {
            if (!it.contains("(Simulator)") && ".*\\(.*\\)\\s\\[.*\\]".toRegex().matches(it)) {
                listOfiOSDevices.plus(
                        TestDevice(
                                deviceName = Regex("(.*?)\\(").find(it)!!.groups[1]!!.value,
                                platformVersion = Regex("\\((.*?)\\)").find(it)!!.groups[1]!!.value,
                                udid = Regex("\\[(.*?)\\]").find(it)!!.groups[1]!!.value,
                                platformName = "IOS"
                        )
                )
            }
        }
        return listOfiOSDevices
    }

    /**
     * Fetch live android device connected to your machine and return the listOf<TestDevice>
     */
    fun fetchLiveAndroidDevices(): List<TestDevice> {
        var listOfAndroidDevices: List<TestDevice> = emptyList()
        logger.print("Read ANDROID_HOME  : $adb.")
        if (StringUtils.isEmpty(adb)) {
            logger.print("Set environment variable 'ANDROID_HOME' to continue.\n Hint: if ANDROID_HOME is set, try running the tool with root privileges.")
            return listOfAndroidDevices
        }
        val androidDevices = Utility.execute(adb, "devices").split("\n")
        for (device in androidDevices) {
            logger.println("PROCESSING - '$device'")
            val temp = device.split("\\s+".toRegex())
            if (temp.size != 2) continue
            listOfAndroidDevices = listOfAndroidDevices.plus(
                    TestDevice(
                            deviceName = temp[0],
                            platformVersion = Utility.adbShell(temp[0], ADBShellAction.PROPERTY, "ro.build.version.release"),
                            udid = temp[0],
                            platformName = "ANDROID"
                    )
            )
        }
        return listOfAndroidDevices;
    }
}