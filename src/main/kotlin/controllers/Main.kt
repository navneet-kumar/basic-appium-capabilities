package controllers

import com.google.gson.GsonBuilder
import response.Response
import response.Status
import services.implementations.DeviceServiceImplementation
import services.implementations.LanguageServiceImplementation
import spark.Spark.get
import spark.Spark.post

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val deviceService = DeviceServiceImplementation()
        val languageService = LanguageServiceImplementation()
        val JSON = GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create()

        get("/devices") { _, res ->
            res.type("application/json")
            val response = deviceService.getDevices()
            if (response.isNotEmpty()) JSON.toJson(Response(Status.SUCCESS, JSON.toJsonTree(response)))
            else JSON.toJson(Response(Status.ERROR, "not active devices connected"))
        }

        post("/changeLanguage") { req, res ->
            res.type("application/json")

            val params = req.queryMap().toMap()
            val map = HashMap<String, String>()
            params.forEach {
                map[it.key] = it.value[0]
            }

            JSON.toJson(languageService.toEnglish(map))
        }
    }
}
