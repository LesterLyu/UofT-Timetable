package com.lvds2000.AcornAPI.auth;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.lvds2000.AcornAPI.exception.MoreThanOneRegistrationException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Lester Lyu
 */
public class RegistrationManager {

    private OkHttpClient client;

    JsonArray registrationsArray;

    public RegistrationManager(OkHttpClient client) {
        this.client = client;
    }

    public List<String> getEligibleRegistrations(){
        System.out.println("Requesting Eligible Registrations...");
        // https://acorn.utoronto.ca/sws/rest/enrolment/eligible-registrations
        Request request = new Request.Builder()
                .url("https://acorn.utoronto.ca/sws/rest/enrolment/eligible-registrations")
                .get()
                .build();

        final List<String> output = new ArrayList<String>();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String eligibleRegistrationsJson = response.body().string();
                System.out.println(eligibleRegistrationsJson);
                // parse the readable string
                JsonParser parser = new JsonParser();
                registrationsArray = parser.parse(eligibleRegistrationsJson).getAsJsonArray();

                for(JsonElement nextRegistration: registrationsArray) {
                    output.add(Jsoup.parse(nextRegistration.getAsJsonObject().get("post").getAsJsonObject().get("description").getAsString() + "(" +
                            nextRegistration.getAsJsonObject().get("candidacyPostCode").getAsString() + ") " +
                            nextRegistration.getAsJsonObject().get("sessionDescription").getAsString()).text());
                }
            }
        });
        long prev = System.currentTimeMillis();
        while(output.size() == 0 && System.currentTimeMillis() - prev < 15000 ){

        }
        Log.i("RegistrationManager", "consume " + (System.currentTimeMillis() - prev) + "ms");
        if(output.size() != 0)
            return output;

        return null;
    }


    public int getNumberOfRegistrations(){
        if(registrationsArray == null)
            getEligibleRegistrations();
        return registrationsArray.size();
    }


    public JsonObject getRegistrationParams(){
        if(registrationsArray == null)
            getEligibleRegistrations();
        if(registrationsArray.size() > 1)
            throw new MoreThanOneRegistrationException();
        return getRegistrationParams(0);
    }

    public JsonObject getRegistrationParams(int index){
        if(registrationsArray == null)
            getEligibleRegistrations();
        return registrationsArray.get(index).getAsJsonObject();
    }

}