package models;

public class TestDevice {
    private String deviceName;
    private String platformName;
    private String platformVersion;
    private String udid;

    public TestDevice(String deviceName, String platformName, String platformVersion, String udid) {
        this.deviceName = deviceName;
        this.platformName = platformName;
        this.platformVersion = platformVersion;
        this.udid = udid;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getPlatformVersion() {
        return platformVersion;
    }

    public void setPlatformVersion(String platformVersion) {
        this.platformVersion = platformVersion;
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }
}
