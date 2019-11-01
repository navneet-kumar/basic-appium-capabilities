package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import response.Response;
import response.Status;
import services.DeviceService;
import services.implementations.DeviceServiceImplementation;

import static spark.Spark.get;

public class main {
    public static void main(String[] args) {

        final DeviceService deviceService = new DeviceServiceImplementation();
        final Gson JSON = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();

        get(
                "/devices",
                (req, res) -> {
                    res.type("application/json");
                    return JSON.toJson(
                            new Response(Status.SUCCESS, JSON.toJsonTree(deviceService.getDevices())));
                });
    }
}
