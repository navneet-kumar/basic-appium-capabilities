package response

import com.google.gson.JsonElement

class Response {

    var status: Status? = null
    var message: String? = null
    var testDevice: JsonElement? = null

    /* We can use Lombok to avoid below methods */

    constructor() {
        this.status = Status.SUCCESS
    }

    constructor(status: Status) {
        this.status = status
    }

    constructor(status: Status, message: String) {
        this.status = status
        this.message = message
    }

    constructor(status: Status, devices: JsonElement) {
        this.status = status
        this.testDevice = devices
    }
}
