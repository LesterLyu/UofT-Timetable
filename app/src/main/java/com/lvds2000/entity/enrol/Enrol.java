
package com.lvds2000.entity.enrol;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Enrol {

    @SerializedName("EnrolledCourse")
    @Expose
    private List<EnrolledCourse> aPP = new ArrayList<EnrolledCourse>();

    /**
     * 
     * @return
     *     The aPP
     */
    public List<EnrolledCourse> getAPP() {
        return aPP;
    }

    /**
     * 
     * @param aPP
     *     The EnrolledCourse
     */
    public void setAPP(List<EnrolledCourse> aPP) {
        this.aPP = aPP;
    }

}
