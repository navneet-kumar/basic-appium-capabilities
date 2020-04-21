package services.implementations

import models.TestDevice
import main.java.services.DeviceService

class DeviceServiceImplementation : DeviceService {
    override fun getDevices(): Collection<TestDevice> {
        return TestDeviceManager.fetchLiveAndroidDevices().plus(TestDeviceManager.fetchLiveIosDevices())
    }
}
