package com.lvds2000.utsccsuntility;

/**
 * Created by LV on 2015-09-29.
 */
public class Courses {
    public static int totalCourseNum , totalCourseNumFall, totalCourseNumWinter;
    static Course course[];

    Courses(int totalCourseNum){
        Courses.totalCourseNum = totalCourseNum;
        course = new Course[totalCourseNum];
        for(int i=0; i<totalCourseNum; i++){
            course[i] = new Course();
            System.out.println("course[i].color="+course[i].color);
        }


    }

//    public static void changeTotalCourseNum(int num){
//        totalCourseNum = num;
//        new Courses(num);
//
//    }
    public static int getTotalCourseNum(){
        return totalCourseNum;
    }
    public static int getFallCourseNum(){

        int cnt=0;
        for(int i=0; i<totalCourseNum; i++){
            if((course[i].isFall || course[i].isFull) && cnt<i){
                //System.out.println(i);
                cnt++;
            }
        }
        totalCourseNumFall = cnt + 1;
        //System.out.println("totalCourseNumFall: "+totalCourseNumFall);
        return totalCourseNumFall;
    }
    public static int getWinterCourseNum(){
        int cnt=0;
        for(int i=0; i<totalCourseNum; i++){
            if((course[i].isFull) && cnt<i){
                //System.out.println(i);
                cnt++;
            }
        }
        totalCourseNumWinter = totalCourseNum - getFallCourseNum()+cnt;
        return totalCourseNumWinter;

    }
    public static int getWinterCourseStartNum(){
        System.out.println("getWinterCourseStartNum()="+(getFallCourseNum() - 1));
        return getFallCourseNum();

    }

    public static float getEarliestClass(int num){ //num = 0 --> fall; num = 1 --> winter
        float time = 24;
        if(num==0){
            //summer
            for(int i=0; i<getTotalCourseNum(); i++){
                //lec
                if(course[i].isFall||course[i].isFull) {
                    for (int k = 0; k < course[i].getLecTimeNum(); k++) {
                        if (!course[i].isLecTimeNull(k) && time > course[i].getLectureStartTimeNum(k)) {
                            time = course[i].getLectureStartTimeNum(k);
                        }
                    }
                    //tut/pra
                    for (int k = 0; k < course[i].getTutTimeNum(); k++) {
                        if (!course[i].isTutTimeNull(k) && time > course[i].getTUTStartTimeNum(k)) {
                            time = course[i].getTUTStartTimeNum(k);
                        }
                    }
                }

//                if(!course[i].isOnlineLec() && time > course[i].getLectureStartTimeNum(1)){
//                    time = course[i].getLectureStartTimeNum(1);
//                }
//                if(course[i].isTwoLecture() && time > course[i].getLectureStartTimeNum(0)){
//                    time = course[i].getLectureStartTimeNum(0);
//                }
//                if(course[i].hasTut && time > course[i].getTUTStartTimeNum()){
//                    time = course[i].getTUTStartTimeNum();
//                }
            }
        }
        //winter
        else{
            for(int i=0; i<getTotalCourseNum(); i++){

                //lec
                if(!course[i].isFall||course[i].isFull) {
                    for (int k = 0; k < course[i].getLecTimeNum(); k++) {
                        if (!course[i].isLecTimeNull(k) && time > course[i].getLectureStartTimeNum(k)) {
                            time = course[i].getLectureStartTimeNum(k);
                        }
                    }
                    //tut/pra
                    for (int k = 0; k < course[i].getTutTimeNum(); k++) {
                        if (!course[i].isTutTimeNull(k) && time > course[i].getTUTStartTimeNum(k)) {
                            time = course[i].getTUTStartTimeNum(k);
                        }
                    }
                }
//
//                if(!course[i].isOnlineLec() && time > course[i].getLectureStartTimeNum(1)){
//                    time = course[i].getLectureStartTimeNum(1);
//                }
//                if(course[i].isTwoLecture() && time > course[i].getLectureStartTimeNum(0)){
//                    time = course[i].getLectureStartTimeNum(0);
//                }
//                if(course[i].hasTut && time > course[i].getTUTStartTimeNum()){
//                    time = course[i].getTUTStartTimeNum();
//                }
            }
        }

        System.out.println("Earliest Time: "+time);
        return time;
    }
    public static float getLatestClass(int num){ //num = 0 --> fall; num = 1 --> winter
        float time = 0;
        if(num==0){
            for(int i=0; i<getFallCourseNum(); i++){

                for(int k=0; k<course[i].getLecTimeNum(); k++){
                    if(!course[i].isLecTimeNull(k) && time < course[i].getLectureEndTimeNum(k) ){
                        time = course[i].getLectureEndTimeNum(k);
                    }
                }
                //tut/pra
                for(int k=0; k<course[i].getTutTimeNum(); k++){
                    if(!course[i].isTutTimeNull(k) && time < course[i].getTUTEndTimeNum(k)){
                        time = course[i].getTUTEndTimeNum(k);
                    }
                }

//                if(!course[i].isOnlineLec() && time < course[i].getLectureEndTimeNum(1)){
//                    time = course[i].getLectureEndTimeNum(1);
//                }
//                if(course[i].isTwoLecture() && time < course[i].getLectureEndTimeNum(0)){
//                    time = course[i].getLectureEndTimeNum(0);
//                }
//                if(course[i].hasTut && time < course[i].getTUTEndTimeNum()){
//                    time = course[i].getTUTEndTimeNum();
//                }
            }
        }
        else{
            for(int i=getWinterCourseStartNum(); i<getTotalCourseNum(); i++){

                for(int k=0; k<course[i].getLecTimeNum(); k++){
                    if(!course[i].isLecTimeNull(k) && time < course[i].getLectureEndTimeNum(k) ){
                        time = course[i].getLectureEndTimeNum(k);
                    }
                }
                //tut/pra
                for(int k=0; k<course[i].getTutTimeNum(); k++){
                    if(!course[i].isTutTimeNull(k) && time < course[i].getTUTEndTimeNum(k)){
                        time = course[i].getTUTEndTimeNum(k);
                    }
                }

//                if(!course[i].isOnlineLec() && time < course[i].getLectureEndTimeNum(1)){
//                    time = course[i].getLectureEndTimeNum(1);
//                }
//                if(course[i].isTwoLecture() && time < course[i].getLectureEndTimeNum(0)){
//                    time = course[i].getLectureEndTimeNum(0);
//                }
//                if(course[i].hasTut && time < course[i].getTUTEndTimeNum()){
//                    time = course[i].getTUTEndTimeNum();
//                }
            }
        }

        System.out.println("Latest Time: "+time);
        return time;
    }
    public static boolean hasCSCS08(){
        boolean out = false;
        for(int i=0; i<getTotalCourseNum(); i++){
            if(course[i].getCourseCode().indexOf("CSCA08")!=-1)
                out = true;
        }
        return out;
    }
    public static boolean hasCSCS20(){
        boolean out = false;
        for(int i=0; i<getTotalCourseNum(); i++){
            if(course[i].getCourseCode().indexOf("CSCA20")!=-1)
                out = true;
        }
        return out;
    }
    public static boolean hasCSCS67(){
        boolean out = false;
        for(int i=0; i<getTotalCourseNum(); i++){
            if(course[i].getCourseCode().indexOf("CSCA67")!=-1 || course[i].getCourseCode().indexOf("MATA67")!=-1)
                out = true;
        }
        return out;
    }
    public static boolean hasCourse(String courseCode){
        boolean out = false;
        for(int i=0; i<getTotalCourseNum(); i++){
            if(course[i].getCourseCode().contains(courseCode) || course[i].getCourseCode().contains(courseCode))
                out = true;
        }
        return out;
    }
    public static Course getCourse(String courseCode){
        Course selectedCourse = null;
        for(int i=0; i<getTotalCourseNum(); i++){
            if(course[i].getCourseCode().contains(courseCode)){
                selectedCourse = course[i];
            }
        }
        return selectedCourse;
    }

}
