package com.lvds2000.AcornAPI.course;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.lvds2000.AcornAPI.auth.RegistrationManager;
import com.lvds2000.AcornAPI.enrol.EnrolledCourse;
import com.lvds2000.AcornAPI.enrol.Meeting;
import com.lvds2000.AcornAPI.plan.PlannedCourse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 *
 * @author Lester Lyu
 *
 */
public class CourseManager {

    private OkHttpClient client;

    private RegistrationManager registrationManager;

    private boolean loaded = false;

    /**
     * includes enrolled courses and waiting listed courses and dropped(late withdraw) courses
     */
    private List<EnrolledCourse> appliedCourses;

    /**
     * courses that are in enrollment cart
     */
    private  List<PlannedCourse> plannedCourses;


    public CourseManager(OkHttpClient client, RegistrationManager registrationManager){
        this.client = client;
        this.registrationManager = registrationManager;
        appliedCourses = new ArrayList<EnrolledCourse>();
        plannedCourses = new ArrayList<PlannedCourse>();
    }

    /**
     * load courses from all registrations
     * Note:
     * UTSC separate 1 year into two registrations, UTSG has only one registration per year,
     * for UTM idk.
     */
    public void loadCourses(){
        // clear first
        appliedCourses = new ArrayList<EnrolledCourse>();
        plannedCourses = new ArrayList<PlannedCourse>();
        loaded = false;
        for(int i = 0; i < registrationManager.getNumberOfRegistrations(); i++){
            loadEnrolledCourses(i);
            loadPlannedCourse(i);
        }
    }

    private void loadEnrolledCourses(final int registrationIndex){
        JsonObject registionParams = registrationManager.getRegistrationParams(registrationIndex)
                .get("registrationParams").getAsJsonObject();
        //System.out.println(registionParams);
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("acorn.utoronto.ca")
                .addPathSegments("sws/rest/enrolment/course/enrolled-courses")
                .addQueryParameter("postCode", registionParams.get("postCode").getAsString())
                .addQueryParameter("postDescription", registionParams.get("postDescription").getAsString())
                .addQueryParameter("sessionCode", registionParams.get("sessionCode").getAsString())
                .addQueryParameter("sessionDescription", registionParams.get("sessionDescription").getAsString())
                .addQueryParameter("status", registionParams.get("status").getAsString())
                .addQueryParameter("assocOrgCode", registionParams.get("assocOrgCode").getAsString())
                .addQueryParameter("acpDuration", registionParams.get("acpDuration").getAsString())
                .addQueryParameter("levelOfInstruction", registionParams.get("levelOfInstruction").getAsString())
                .addQueryParameter("typeOfProgram", registionParams.get("typeOfProgram").getAsString())
                .addQueryParameter("designationCode1", registionParams.get("designationCode1").getAsString())
                .addQueryParameter("primaryOrgCode", registionParams.get("primaryOrgCode").getAsString())
                .addQueryParameter("secondaryOrgCode", registionParams.get("secondaryOrgCode").getAsString())
                .addQueryParameter("collaborativeOrgCode", registionParams.get("collaborativeOrgCode").getAsString())
                .addQueryParameter("adminOrgCode", registionParams.get("adminOrgCode").getAsString())
                .addQueryParameter("coSecondaryOrgCode", registionParams.get("coSecondaryOrgCode").getAsString())
                .addQueryParameter("yearOfStudy", registionParams.get("yearOfStudy").getAsString())
                .addQueryParameter("postAcpDuration", registionParams.get("postAcpDuration").getAsString())
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("loadEnrolledCourses", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String appliedCourseJson = response.body().string();
                //System.out.println(appliedCourseJson);
                Gson gson = new Gson();

                if(!appliedCourseJson.equals("{}")) {
                    // process json
                    JsonParser parser = new JsonParser();
                    JsonObject courseJsonObject = parser.parse(appliedCourseJson).getAsJsonObject();

                    List<EnrolledCourse> enrolledCourseList;
                    List<EnrolledCourse> waitlistedCourseList;
                    List<EnrolledCourse> droppedCourseList;

                    // enrolled course
                    if(courseJsonObject.has("APP")){
                        JsonArray jsonArrayAPP = courseJsonObject.get("APP").getAsJsonArray();
                        enrolledCourseList = gson.fromJson(jsonArrayAPP.toString(),
                                new TypeToken<List<EnrolledCourse>>(){}.getType());
                        appliedCourses.addAll(enrolledCourseList);
                    }
                    // wait listed course
                    if(courseJsonObject.has("WAIT")) {
                        // for waitlisted course, I need to get more info from
                        // https://acorn.utoronto.ca/sws/rest/enrolment/course/view
                        // i.e. wait list rank, etc

                        JsonArray jsonArrayWAIT = courseJsonObject.get("WAIT").getAsJsonArray();
                        waitlistedCourseList = gson.fromJson(jsonArrayWAIT.toString(), new TypeToken<List<EnrolledCourse>>(){}.getType());
                        // get courseCode and sectionCode
                        for(EnrolledCourse nextCourse: waitlistedCourseList){
                            String courseCode = nextCourse.getCode();
                            String courseSessionCode = nextCourse.getSessionCode();
                            String sectionCode = nextCourse.getSectionCode();
                            // load extra info but still the same object type
                            EnrolledCourse newCourse = loadExtraInfo(courseCode, courseSessionCode, sectionCode, registrationIndex);
                            System.out.println(newCourse);
                            appliedCourses.add(newCourse);
                        }

                    }
                    if(courseJsonObject.has("DROP")) {
                        JsonArray jsonArrayDROP = courseJsonObject.get("DROP").getAsJsonArray();
                        droppedCourseList = gson.fromJson(jsonArrayDROP.toString(), new TypeToken<List<EnrolledCourse>>(){}.getType());
                        appliedCourses.addAll(droppedCourseList);
                    }

                    Collections.sort(appliedCourses, new Comparator<EnrolledCourse>() {
                        @Override
                        public int compare(EnrolledCourse o1, EnrolledCourse o2) {
                            if(o1.getSectionCode().equalsIgnoreCase("S") && (o2.getSectionCode().equalsIgnoreCase("Y") || o2.getSectionCode().equalsIgnoreCase("F")))
                                return 1;
                            else if(o1.getSectionCode().equalsIgnoreCase("Y") && o2.getSectionCode().equalsIgnoreCase("F"))
                                return 1;
                            else if(!o1.getSectionCode().equalsIgnoreCase(o2.getSectionCode()))
                                return -1;
                            else{
                                return o1.getCode().compareToIgnoreCase(o2.getCode());
                            }
                        }
                    });
                    loaded = true;
                }
            }
        });
        long prev = System.currentTimeMillis();
        while(System.currentTimeMillis() - prev < 10000 && !loaded){

        }

        Log.i("loadEnrolledCourses", "consume " + (System.currentTimeMillis() - prev) + "ms");

    }

    /**
     * For waitlisted course to get waiting list rank
     * AND
     * For searching a course info, even though not enrolled or waiting listed yet.
     * @return a complete EnrolledCourse
     */
    private EnrolledCourse loadExtraInfo(String courseCode, String courseSessionCode, String sectionCode, int registrationIndex){
        loaded = false;
        JsonObject registionParams = registrationManager.getRegistrationParams(registrationIndex)
                .get("registrationParams").getAsJsonObject();
        //System.out.println(registionParams);
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("acorn.utoronto.ca")
                .addPathSegments("sws/rest/enrolment/course/view")
                .addQueryParameter("acpDuration", registionParams.get("acpDuration").getAsString())
                .addQueryParameter("activityApprovedInd", "")
                .addQueryParameter("activityApprovedOrg", "")
                .addQueryParameter("adminOrgCode", registionParams.get("adminOrgCode").getAsString())
                .addQueryParameter("assocOrgCode", registionParams.get("assocOrgCode").getAsString())
                .addQueryParameter("coSecondaryOrgCode", registionParams.get("coSecondaryOrgCode").getAsString())
                .addQueryParameter("collaborativeOrgCode", registionParams.get("collaborativeOrgCode").getAsString())
                .addQueryParameter("courseCode", courseCode)
                .addQueryParameter("courseSessionCode", courseSessionCode)
                .addQueryParameter("designationCode1", registionParams.get("designationCode1").getAsString())
                .addQueryParameter("levelOfInstruction", registionParams.get("levelOfInstruction").getAsString())
                .addQueryParameter("postAcpDuration", registionParams.get("postAcpDuration").getAsString())
                .addQueryParameter("postCode", registionParams.get("postCode").getAsString())
                .addQueryParameter("postDescription", registionParams.get("postDescription").getAsString())
                .addQueryParameter("primaryOrgCode", registionParams.get("primaryOrgCode").getAsString())
                .addQueryParameter("secondaryOrgCode", registionParams.get("secondaryOrgCode").getAsString())
                .addQueryParameter("sectionCode", sectionCode)
                .addQueryParameter("sessionCode", registionParams.get("sessionCode").getAsString())
                .addQueryParameter("sessionDescription", registionParams.get("sessionDescription").getAsString())
                .addQueryParameter("status", registionParams.get("status").getAsString())
                .addQueryParameter("subjectCode1", registionParams.get("subjectCode1").getAsString())
                .addQueryParameter("typeOfProgram", registionParams.get("typeOfProgram").getAsString())
                .addQueryParameter("useSws", registionParams.get("useSws").getAsString())
                .addQueryParameter("yearOfStudy", registionParams.get("yearOfStudy").getAsString())
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        final EnrolledCourse res[] = new EnrolledCourse[1];
        try {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String courseJson = response.body().string();
                    //System.out.println(courseJson);
                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();
                    JsonObject courseJsonObject = parser.parse(courseJson).getAsJsonObject().get("responseObject").getAsJsonObject();
                    res[0] = gson.fromJson(courseJsonObject, EnrolledCourse.class);
                    loaded = true;
                }
            });

            long prev = System.currentTimeMillis();
            while(System.currentTimeMillis() - prev < 15000 && !loaded){}
            Log.i("loadExtraInfo", "consume " + (System.currentTimeMillis() - prev) + "ms");
            return res[0];

        } catch(Exception e){
            e.printStackTrace();
        }
        return null;

    }

    /**
     *
     * @param courseCode CSC343H1
     * @param courseSessionCode 20171
     * @param sectionCode S
     * @param registrationIndex 0
     * @return
     */
    public String getCourseSpace(String courseCode, String courseSessionCode, String sectionCode, int registrationIndex){
        EnrolledCourse course = loadExtraInfo(courseCode, courseSessionCode, sectionCode, registrationIndex);
        String spaceInfo = courseCode + sectionCode + " Space: ";
        for(Meeting meeting: course.getMeetings()){
            spaceInfo += meeting.getDisplayName() + ": " + meeting.getEnrollmentSpaceAvailable() + "/" +  meeting.getEnrollSpace() + ", ";
        }
        spaceInfo = spaceInfo.substring(0, spaceInfo.length() - 2);
        return spaceInfo;
    }


    private void loadPlannedCourse(int registrationIndex){
        loaded = false;
        // https://acorn.utoronto.ca/sws/rest/enrolment/plan?candidacyPostCode=ASPRGHBSC&candidacySessionCode=20169&sessionCode=20169
        JsonObject registionParams = registrationManager.getRegistrationParams(registrationIndex).getAsJsonObject();
        //System.out.println(registionParams);
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("acorn.utoronto.ca")
                .addPathSegments("sws/rest/enrolment/plan")
                .addQueryParameter("candidacyPostCode", registionParams.get("candidacyPostCode").getAsString())
                .addQueryParameter("candidacySessionCode", registionParams.get("candidacySessionCode").getAsString())
                .addQueryParameter("sessionCode", registionParams.get("sessionCode").getAsString())
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String plannedCourseJson = response.body().string();
                //System.out.println(plannedCourseJson);
                Gson gson = new Gson();

                // cannot be empty json array
                if (!plannedCourseJson.equals("[]")) {
                    List<PlannedCourse> plannedCourseList = gson.fromJson(plannedCourseJson.toString(),
                            new TypeToken<List<PlannedCourse>>() {
                            }.getType());
                    plannedCourses.addAll(plannedCourseList);


                    Collections.sort(plannedCourses, new Comparator<PlannedCourse>() {
                        @Override
                        public int compare(PlannedCourse o1, PlannedCourse o2) {
                            if (o1.getSectionCode().equalsIgnoreCase("S") && (o2.getSectionCode().equalsIgnoreCase("Y") || o2.getSectionCode().equalsIgnoreCase("F")))
                                return 1;
                            else if (o1.getSectionCode().equalsIgnoreCase("Y") && o2.getSectionCode().equalsIgnoreCase("F"))
                                return 1;
                            else if (!o1.getSectionCode().equalsIgnoreCase(o2.getSectionCode()))
                                return -1;
                            else {
                                return o1.getCourseCode().compareToIgnoreCase(o2.getCourseCode());
                            }
                        }
                    });
                }
                loaded = true;
            }});
        long prev = System.currentTimeMillis();
        while(System.currentTimeMillis() - prev < 15000 && !loaded){}
        Log.i("loadPlannedCourse", "consume " + (System.currentTimeMillis() - prev) + "ms");
    }


    public List<EnrolledCourse> getAppliedCourses() {
        if (appliedCourses.size() == 0)
            loadCourses();
        return appliedCourses;
    }

    public List<PlannedCourse> getPlannedCourses() {
        return plannedCourses;
    }

    public void refresh() {
        appliedCourses = new ArrayList<EnrolledCourse>();
        plannedCourses = new ArrayList<PlannedCourse>();
        loadCourses();
    }
}
