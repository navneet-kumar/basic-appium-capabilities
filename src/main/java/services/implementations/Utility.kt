package services.implementations

import org.apache.commons.lang3.StringUtils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

object Utility {
    private val logger = System.out
    /**
     * Execute the command on system and returns its output as String
     *
     * @param cmd
     * @return String, output of command if none empty string is returned
     */
    fun execute(vararg cmd: String): String {
        logger.print("executing command line - '${cmd.joinToString(",")}'")
        val response = StringBuilder()
        try {
            val processBuilder = ProcessBuilder(*cmd)
            processBuilder.redirectErrorStream(true)
            val process = processBuilder.start()

            val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))

            var line: String? = bufferedReader.readLine()
            while (line != null) {
                response.append(line).append(System.getProperty("line.separator"))
                line = bufferedReader.readLine()
            }
            process.waitFor()
            bufferedReader.close()
        } catch (ex: IOException) {
            response.append(ex.message)
        } catch (ex: InterruptedException) {
            response.append(ex.message)
        }

        val res = StringUtils.strip(response.toString(), System.getProperty("line.separator"))
        logger.print("Command line response - $res")
        return res
    }

    /**
     * @param deviceName
     * @param action
     * @param shellCommand
     * @return
     */
    fun adbShell(
            deviceName: String, action: ADBShellAction, vararg shellCommand: String): String {
        val commands = ArrayList<String>()

        commands.add(TestDeviceManager.adb)
        commands.add("-s")
        commands.add(deviceName)
        commands.add("shell")

        when (action) {
            ADBShellAction.ACTION -> {
                commands.add("am")
                commands.add("start")
                commands.add("-a")
            }
            ADBShellAction.PROPERTY -> commands.add("getprop")
            ADBShellAction.SETTINGS -> commands.add("settings")
        }

        for (s in shellCommand) {
            commands.add(s)
        }
        logger.print("Shell command - $commands")
        return execute(*commands.toTypedArray())
    }
}

enum class ADBShellAction {
    ACTION, //
    PROPERTY, //
    SETTINGS
}