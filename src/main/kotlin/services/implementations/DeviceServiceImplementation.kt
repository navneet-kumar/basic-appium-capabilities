package services.implementations

import models.TestDevice
import services.DeviceService

class DeviceServiceImplementation : DeviceService {
    override fun getDevices(): Collection<TestDevice> {
        return TestDeviceManager.fetchLiveAndroidDevices().plus(TestDeviceManager.fetchLiveIosDevices())
    }
}