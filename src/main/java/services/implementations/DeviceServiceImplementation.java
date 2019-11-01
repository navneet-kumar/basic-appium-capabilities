package services.implementations;

import models.TestDevice;
import services.DeviceService;

import java.util.Collection;

public class DeviceServiceImplementation implements DeviceService {
    @Override
    public Collection<TestDevice> getDevices() {
        return TestDeviceManager.getConnectedDevices();
    }
}
