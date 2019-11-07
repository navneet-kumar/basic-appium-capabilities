# basic-appium-capabilities
REST API for getting appium capabilities of connected devices

## Description
Finiding appium capabilities like `deviceName`,`platformVersion` and `udid` e.t.c is extremely painful, and it becomes more challenging when your device(s) is connected to some remote machine whose bash/terminal is not accessbile to you!

This solution can be helpful in such situation you need to run [this jar](output/basic-appium-capabilities-1.0.jar) file and make an http request for example:

### REQUEST <br>
`http://localhos:4567/devices`
<br>

### RESPONSE <br>
```
{
    "status": "SUCCESS",
    "devices": [
        {
            "deviceName": "07000c99a2d0e6cb",
            "platformName": "ANDROID",
            "platformVersion": "5.1.1",
            "udid": "dc93a5f2a0f4baf6"
        },
        {
            "deviceName": "Navneet's iPhone",
            "platformName": "IOS",
            "platformVersion": "12.4.2",
            "udid": "d07a7331a573252bd1272e304786600024f566cb"
        }
    ]
}
```
