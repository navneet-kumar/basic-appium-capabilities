package services.implementations

import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileElement
import io.appium.java_client.PerformsTouchActions
import io.appium.java_client.TouchAction
import io.appium.java_client.ios.IOSDriver
import io.appium.java_client.remote.MobileCapabilityType
import io.appium.java_client.service.local.AppiumDriverLocalService
import io.appium.java_client.service.local.AppiumServiceBuilder
import io.appium.java_client.touch.WaitOptions
import io.appium.java_client.touch.offset.PointOption
import org.openqa.selenium.remote.DesiredCapabilities
import response.Response
import response.Status
import services.LanguageService
import java.time.Duration
import java.util.*

class PlatformTouchAction(performsActions: PerformsTouchActions) : TouchAction<PlatformTouchAction>(performsActions)

class LanguageServiceImplementation : LanguageService {

    private val iOSCapabilities = DesiredCapabilities(mapOf(
            Pair("bundleId", "com.apple.Preferences"),
            Pair(MobileCapabilityType.AUTOMATION_NAME, "XCUITest")
    ))

    private val appium: AppiumDriverLocalService = AppiumServiceBuilder()
            .usingAnyFreePort()
            .build()

    override fun toEnglish(device: Map<String, String>): Response {
        val platform = device["platformName"] ?: error("Platform Name is mandatory")
        return when (platform.toLowerCase()) {
            "android" -> return Response(Status.ERROR, "Language switching to android is not supported yet")
            "ios" -> switchToEnglish(device)
            else -> return Response(Status.ERROR, "Platform not specified/ not supported.")
        }
    }

    private fun switchToEnglish(device: Map<String, String>): Response {
        return try {
            // start appium locally.
            appium.start()
            // create session
            val driver = IOSDriver<MobileElement>(appium.url, iOSCapabilities.merge(DesiredCapabilities(device)))

            // check existing language
            val header = driver.findElementByClassName("XCUIElementTypeNavigationBar")
            if (header.text != TranslationExtension.Settings.value(Locale.JAPAN))
                Response(Status.ERROR, "Settings header '${header.text}' and device language is already in english")

            // swipe to general option
            swipeUp(driver)

            //tap on general
            val btnGeneral = driver.findElementByXPath("//XCUIElementTypeCell[@name=\"${TranslationExtension.General.value(Locale.JAPAN)}\"]")
            btnGeneral.click()

            // swipe to language and region option
            swipeUp(driver)
            swipeUp(driver)

            //tap on language and region option
            val btnLanguageAndRegion = driver.findElementByXPath("//XCUIElementTypeCell[@name=\"${TranslationExtension.LanguageRegion.value(Locale.JAPAN)}\"]")
            btnLanguageAndRegion.click()

            // tap on iphone language
            val btnIphoneLanguage = driver.findElementByXPath("//XCUIElementTypeCell[@name=\"${TranslationExtension.CurrentLanguage.value(Locale.JAPAN)}\"]")
            btnIphoneLanguage.click()

            // Check on english language
            val options = driver.findElementsByClassName("XCUIElementTypeCell")
            options.forEach { item ->
                val elements = item.findElementsByClassName("XCUIElementTypeStaticText")
                val element = elements.find { it.text === "English" }
                if (element != null) {
                    item.click()
                    return@forEach
                }
            }

            // confirm language
            val BtnConfirm = driver.findElementByXPath("//XCUIElementTypeButton[@name=\"${TranslationExtension.BtnChange.value(Locale.JAPAN)}\"]")
            BtnConfirm.click()

            // kill app
            Thread.sleep(4000)
            driver.terminateApp(iOSCapabilities.asMap()["bundleId"] as String)

            Response(Status.SUCCESS, "Language changed Successfully !")
        } catch (ex: Exception) {
            appium.stop()
            Response(Status.ERROR, ex.localizedMessage)
        }
    }

    fun swipeUp(driver: AppiumDriver<MobileElement>) {
        val deviceDimension = driver.manage().window().size
        // Source and target Point Option
        val source = PointOption.point(deviceDimension.getWidth() / 2, deviceDimension.getHeight() / 2)
        val target = PointOption.point(deviceDimension.getWidth() / 2, 1)

        val swipe = PlatformTouchAction(driver)
                .press(source)
                .waitAction(WaitOptions.waitOptions(Duration.ofMillis(500)))
                .moveTo(target)
                .release()
        swipe.perform()
    }
}