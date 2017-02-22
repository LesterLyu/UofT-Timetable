
package com.lvds2000.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lvds2000.AcornAPI.enrol.Meeting;
import com.lvds2000.AcornAPI.enrol.Time;
import com.lvds2000.AcornAPI.plan.Day;

import java.util.ArrayList;
import java.util.List;

public class Activity {

    @SerializedName("activityId")
    @Expose
    private String activityId;
    @SerializedName("enroled")
    @Expose
    private boolean enroled;
    @SerializedName("commaSeparatedInstructorNames")
    @Expose
    private String commaSeparatedInstructorNames;
    @SerializedName("days")
    @Expose
    private List<Day> days = new ArrayList<Day>();
    @SerializedName("waitlistRank")
    @Expose
    private int waitlistRank;

    Activity(com.lvds2000.AcornAPI.plan.Activity activity){
        activityId = activity.getActivityId();
        enroled = Boolean.parseBoolean(activity.getEnroled());
        commaSeparatedInstructorNames = activity.getCommaSeparatedInstructorNames();
        days = activity.getDays();
        try{
            waitlistRank = Integer.parseInt(activity.getWaitlistRank());
        }catch (NumberFormatException e){
            waitlistRank = 0;
        }
    }

    /**
     * for enrolled and waitlisted courses
     * @param meeting the activity
     * @param isWaitlisted if this activity is in a waiting list
     */
    Activity(Meeting meeting, boolean isWaitlisted){
        activityId = meeting.getDisplayName();
        enroled = !isWaitlisted;
        commaSeparatedInstructorNames = meeting.getCommaSeparatedInstructorNames();
        for(Time time: meeting.getTimes()){
            days.add(new Day(time));
        }
        waitlistRank = meeting.getWaitlistRank();
    }


    /**
     * 
     * @return
     *     The activityId
     */
    public String getActivityId() {
        return activityId;
    }

    /**
     * 
     * @param activityId
     *     The activityId
     */
    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    /**
     * 
     * @return
     *     The enroled
     */
    public boolean getEnroled() {
        return enroled;
    }

    /**
     * 
     * @param enroled
     *     The enroled
     */
    public void setEnroled(boolean enroled) {
        this.enroled = enroled;
    }

    /**
     * 
     * @return
     *     The commaSeparatedInstructorNames
     */
    public String getCommaSeparatedInstructorNames() {
        return commaSeparatedInstructorNames;
    }

    /**
     * 
     * @param commaSeparatedInstructorNames
     *     The commaSeparatedInstructorNames
     */
    public void setCommaSeparatedInstructorNames(String commaSeparatedInstructorNames) {
        this.commaSeparatedInstructorNames = commaSeparatedInstructorNames;
    }

    /**
     * 
     * @return
     *     The days
     */
    public List<Day> getDays() {
        return days;
    }

    /**
     * 
     * @param days
     *     The days
     */
    public void setDays(List<Day> days) {
        this.days = days;
    }

    /**
     * 
     * @return
     *     The waitlistRank
     */
    public int getWaitlistRank() {
        return waitlistRank;
    }

    /**
     * 
     * @param waitlistRank
     *     The waitlistRank
     */
    public void setWaitlistRank(int waitlistRank) {
        this.waitlistRank = waitlistRank;
    }



}
