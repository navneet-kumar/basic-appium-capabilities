package services

import models.TestDevice

interface DeviceService {
    fun getDevices(): Collection<TestDevice>
}
