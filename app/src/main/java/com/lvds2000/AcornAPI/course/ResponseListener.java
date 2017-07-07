package com.lvds2000.AcornAPI.course;

import com.google.gson.JsonObject;

import java.util.Map;

public interface ResponseListener {
    void response(Map<String, JsonObject> data);
    void failure();
}