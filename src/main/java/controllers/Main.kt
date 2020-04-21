package controllers

import com.google.gson.GsonBuilder
import response.Response
import response.Status
import services.implementations.DeviceServiceImplementation
import spark.Spark.get

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val deviceService = DeviceServiceImplementation()
        val JSON = GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create()

        get("/devices") { _, res ->
            res.type("application/json")
            val response = deviceService.devices
            if (response.isNotEmpty()) JSON.toJson(Response(Status.SUCCESS, JSON.toJsonTree(response)))
            else JSON.toJson(Response(Status.ERROR, "not active devices connected"))
        }
    }
}
