package com.lvds2000.AcornAPI.course;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lvds2000.AcornAPI.auth.RegistrationManager;
import com.lvds2000.AcornAPI.enrol.EnrolledCourse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.TlsVersion;


public class CourseSearcher {

    private OkHttpClient client;

    private static OkHttpClient iitClient = new OkHttpClient.Builder()
            .connectionSpecs(Collections.singletonList(
                    new ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
                    .tlsVersions(TlsVersion.TLS_1_2)
                    .cipherSuites(
                            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_ECDH_RSA_WITH_AES_256_GCM_SHA384,
                            CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384)
                    .build()))
            .build();

    private RegistrationManager registrationManager;

    public CourseSearcher(OkHttpClient client, RegistrationManager registrationManager) {
        this.client = client;
        this.registrationManager = registrationManager;
//		this.iitClient = new OkHttpClient.Builder().build();
    }

    public EnrolledCourse searchCourse(String courseCode, String courseSessionCode, String sectionCode, int registrationIndex) {
        return null;
    }


    /**
     * iit
     *
     * @param r
     * @return
     */
    public static void getCourseInfo(String code, final ResponseListener r) {

        Request request = new Request.Builder()
                .url("https://timetable.iit.artsci.utoronto.ca/api/20189/courses?code=" + code)
                .build();

        try {
            iitClient.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    r.failure();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String body = response.body().string();
                    if (body.trim().equals("[]")) {
                        r.failure();
                        return;
                    }
                    Map<String, JsonObject> courses = new HashMap<String, JsonObject>();
                    JsonParser parser = new JsonParser();
                    JsonObject obj = parser.parse(body).getAsJsonObject();

                    for (Entry<String, JsonElement> entry : obj.entrySet()) {
                        String courseCode = entry.getKey().substring(0, 8);
                        if (courses.keySet().contains(courseCode))
                            continue;
                        courses.put(courseCode, entry.getValue().getAsJsonObject());
                    }
                    r.response(courses);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
