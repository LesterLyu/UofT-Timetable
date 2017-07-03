package com.lvds2000.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lvds2000.AcornAPI.enrol.EnrolledCourse;
import com.lvds2000.AcornAPI.enrol.Meeting;
import com.lvds2000.AcornAPI.plan.PlannedCourse;

import java.util.ArrayList;
import java.util.List;

/**
 * A custom course object
 */
public class Course {
    // CSC235H1
    @SerializedName("courseCode")
    @Expose
    private String courseCode;
    // S Y F
    @SerializedName("sectionCode")
    @Expose
    private String sectionCode;
    // LEC 0101
    @SerializedName("primaryActivityId")
    @Expose
    private String primaryActivityId;
    // TUT 0101
    @SerializedName("secondaryActivityId")
    @Expose
    private String secondaryActivityId;
    // PRA 0101
    @SerializedName("thirdActivityId")
    @Expose
    private String thirdActivityId;
    // Calculus II
    @SerializedName("courseTitle")
    @Expose
    private String courseTitle;
    // 20169
    @SerializedName("regSessionCode1")
    @Expose
    private String regSessionCode1;
    // 20171
    @SerializedName("regSessionCode2")
    @Expose
    private String regSessionCode2;
    @SerializedName("regSessionCode3")
    @Expose
    private String regSessionCode3;
    @SerializedName("activities")
    @Expose
    private List<Activity> activities = new ArrayList<Activity>();
    @SerializedName("color")
    @Expose
    public int color;

    public Course(PlannedCourse plannedCourse){
        this.courseCode = plannedCourse.getCourseCode();
        this.sectionCode = plannedCourse.getSectionCode();
        this.courseTitle = plannedCourse.getCourseTitle();
        this.primaryActivityId = plannedCourse.getPrimaryActivityId();
        this.secondaryActivityId = plannedCourse.getSecondaryActivityId();
        this.thirdActivityId = plannedCourse.getThirdActivityId();
        this.regSessionCode1 = plannedCourse.getRegSessionCode1();
        this.regSessionCode2 = plannedCourse.getRegSessionCode2();
        this.regSessionCode3 = plannedCourse.getRegSessionCode3();
        if (plannedCourse.getInfo().getPrimaryActivities().size() != 0)
            activities.add(new Activity(plannedCourse.getInfo().getPrimaryActivities().get(0)));
        if (plannedCourse.getInfo().getSecondaryActivities().size() != 0)
            activities.add(new Activity(plannedCourse.getInfo().getSecondaryActivities().get(0)));
        if (plannedCourse.getInfo().getThirdActivities().size() != 0)
            activities.add(new Activity(plannedCourse.getInfo().getThirdActivities().get(0)));
    }

    public Course(EnrolledCourse enrolledCourse){
        courseCode = enrolledCourse.getCode();
        sectionCode = enrolledCourse.getSectionCode();
        courseTitle = enrolledCourse.getTitle();

        if(enrolledCourse.getStatus().equalsIgnoreCase("APP"))
            // Enrolled
            primaryActivityId = enrolledCourse.getPrimaryTeachMethod() + " " + enrolledCourse.getPrimarySectionNo();
        else if(enrolledCourse.getStatus().equalsIgnoreCase("WAIT") && enrolledCourse.getMeetingList().size() == 1){
            // Waitlisted, only show the course with one waiting listed
            primaryActivityId = (String) enrolledCourse.getWaitlistMeetings();
        }
        secondaryActivityId = enrolledCourse.getSecondaryTeachMethod1() + " " + enrolledCourse.getSecondaryTeachMethod1();
        thirdActivityId = enrolledCourse.getSecondaryTeachMethod2() + " " + enrolledCourse.getSecondarySectionNo2();
        regSessionCode1 = enrolledCourse.getRegSessionCode1();
        regSessionCode2 = enrolledCourse.getRegSessionCode2();
        regSessionCode3 = enrolledCourse.getRegSessionCode3();

        if(!enrolledCourse.getStatus().equalsIgnoreCase("DROP"))
            for(Meeting meeting: enrolledCourse.getMeetings()) {
                if(enrolledCourse.getStatus().equalsIgnoreCase("APP"))
                    activities.add(new Activity(meeting, false));
                else if(enrolledCourse.getStatus().equalsIgnoreCase("WAIT"))
                    activities.add(new Activity(meeting, true));
            }
    }

    /**
     *
     * @return e.g. CSC108H3
     */
    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getSectionCode() {
        return sectionCode;
    }

    public void setSectionCode(String sectionCode) {
        this.sectionCode = sectionCode;
    }

    public String getPrimaryActivityId() {
        return primaryActivityId;
    }

    public void setPrimaryActivityId(String primaryActivityId) {
        this.primaryActivityId = primaryActivityId;
    }

    public String getSecondaryActivityId() {
        return secondaryActivityId;
    }

    public void setSecondaryActivityId(String secondaryActivityId) {
        this.secondaryActivityId = secondaryActivityId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public String getThirdActivityId() {
        return thirdActivityId;
    }

    public void setThirdActivityId(String thirdActivityId) {
        this.thirdActivityId = thirdActivityId;
    }

    public String getRegSessionCode1() {
        return regSessionCode1;
    }

    public void setRegSessionCode1(String regSessionCode1) {
        this.regSessionCode1 = regSessionCode1;
    }

    public String getRegSessionCode2() {
        return regSessionCode2;
    }

    public void setRegSessionCode2(String regSessionCode2) {
        this.regSessionCode2 = regSessionCode2;
    }

    public String getRegSessionCode3() {
        return regSessionCode3;
    }

    public void setRegSessionCode3(String regSessionCode3) {
        this.regSessionCode3 = regSessionCode3;
    }

    public int getColor(){
        return color;
    }

    public void setColor(int color){
        this.color = color;
    }

    public void setActivities(List<Activity> activities){
        this.activities = activities;
    }

    public List<Activity> getActivities(){
        return activities;
    }

}
