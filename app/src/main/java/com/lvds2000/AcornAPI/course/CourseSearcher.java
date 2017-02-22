package com.lvds2000.AcornAPI.course;

import com.lvds2000.AcornAPI.auth.RegistrationManager;
import com.lvds2000.AcornAPI.enrol.EnrolledCourse;
import com.lvds2000.AcornAPI.plan.PlannedCourse;
import okhttp3.OkHttpClient;

public class CourseSearcher {
	
	private OkHttpClient client;
	
	private RegistrationManager registrationManager;
	
	public CourseSearcher(OkHttpClient client, RegistrationManager registrationManager){
		this.client = client;
		this.registrationManager = registrationManager;
	}
	
	public EnrolledCourse searchCourse(String courseCode, String courseSessionCode, String sectionCode, int registrationIndex){
		return null;
		
		
		
	}

}
