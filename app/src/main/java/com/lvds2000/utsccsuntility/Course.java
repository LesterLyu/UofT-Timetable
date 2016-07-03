package com.lvds2000.utsccsuntility;

/**
 * Created by LV on 2015-09-29.
 */
public class Course {
    private int maxLecAndTut = 5;
    public String name, code, lec, lecLocation,instructor ;
    public String lecTime[] = new String[maxLecAndTut];
    public String tutTime[] = new String[maxLecAndTut];
    public String tutTime2[] = new String[maxLecAndTut];
    public String tut[] = new String[maxLecAndTut];
    public String tutLocation[] = new String[maxLecAndTut];
    public boolean hasTut, isFall, isFull;
    public int color;

    Course(){
        this.name = "";
        this.code = "";
        this.lec = "";
        this.lecLocation = "";

        this.hasTut = false;
        for(int i=0; i<maxLecAndTut; i++){
            this.lecTime[i]="";
            this.tutTime[i] = "";
            this.tutTime2[i] = "";
            this.tut[i] = "";
            this.tutLocation[i] = "";
        }

        this.instructor = "";
        this.hasTut = false;
        this.isFall = true;
        this.isFull = true;
        this.color = 0;
    }


    public String getCourseCode(){

        return code;

    }
    public String getLectureWeekday(int num){
        return lecTime[num].substring(0, lecTime[num].indexOf(" ")).trim();

    }
    public String getTUTWeekday(int num){
        return tutTime[num].substring(0, tutTime[num].indexOf(" ")).trim();

    }
    public int getLectureWeekdayNum(int num){

        String s = lecTime[num].substring(0, lecTime[num].indexOf(" ")).trim();
        String[] weekday = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        int i = 0;
        for(String day: weekday){
            i++;
            if(s.equalsIgnoreCase(day)){
                return i;
            }

        }
        return 0;
    }
    public int getTUTWeekdayNum(int num){

        String s = tutTime[num].substring(0, tutTime[num].indexOf(" ")).trim();
        String[] weekday = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        int i = 0;
        for(String day: weekday){
            i++;
            if(s.equalsIgnoreCase(day)){
                return i;
            }

        }
        return 0;
    }
    public int getTUT2WeekdayNum(int num){

        String s = tutTime2[num].substring(0, tutTime2[num].indexOf(" ")).trim();
        String[] weekday = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        int i = 0;
        for(String day: weekday){
            i++;
            if(s.equalsIgnoreCase(day)){
                return i;
            }

        }
        return 0;
    }
    public boolean isTwoLecture(){
            return !(lecTime[0]=="");
    }
    public int getLecTimeNum(){
        int cnt = 0;
        for(int i=0; i<5; i++){
            if(lecTime[i]!=""){
                cnt++;
            }
        }
        return cnt;
    }
    public int getTutTimeNum(){
        int cnt = 0;
        for(int i=0; i<5; i++){
            if(tutTime[i]!=""){
                cnt++;
            }
        }
        return cnt;
    }
    public boolean isOnlineLec(){
        return (lecTime[1]==""&&code!="");
    }
    public boolean isLecTimeNull(int num){
        return (lecTime[num]=="");

    }
    public boolean isTutTimeNull(int num){
        return (tutTime[num]=="");

    }
    public boolean isTutTime2Null(int num){
        return (tutTime2[num]=="");

    }

    public String getLectureStartTime(int num){
        //System.out.println(lecTime[num]);
        return hour12To24(lecTime[num].substring(lecTime[num].indexOf(" "),
                lecTime[num].indexOf("M ") + 1).trim());
    }
    public float getLectureStartTimeNum(int num){
        String s = getLectureStartTime(num);
        String h = s.substring(0,s.indexOf(":")).trim();
        String min = s.substring(s.indexOf(":") + 1, s.indexOf(":") + 3);
        float min_float = Float.parseFloat(min)*5/3;
        return Float.parseFloat(h + "." + ((int) min_float));
    }
    public String getTUTStartTime(int num){
       // System.out.println(tutTime);
        return hour12To24(tutTime[num].substring(tutTime[num].indexOf(" "),
                tutTime[num].indexOf("M ") + 1).trim());
    }
    public String getTUT2StartTime(int num){
        // System.out.println(tutTime);
        return hour12To24(tutTime2[num].substring(tutTime2[num].indexOf(" "),
                tutTime2[num].indexOf("M ") + 1).trim());
    }
    public float getTUTStartTimeNum(int num){
        String s = getTUTStartTime(num);
        String h = s.substring(0,s.indexOf(":")).trim();
        String min = s.substring(s.indexOf(":") + 1, s.indexOf(":") + 3);
        float min_float = Float.parseFloat(min)*5/3;
        return Float.parseFloat(h + "." + ((int) min_float));
    }
    public float getTUT2StartTimeNum(int num){
        String s = getTUT2StartTime(num);
        String h = s.substring(0,s.indexOf(":")).trim();
        String min = s.substring(s.indexOf(":") + 1, s.indexOf(":") + 3);
        float min_float = Float.parseFloat(min)*5/3;
        return Float.parseFloat(h + "." + ((int) min_float));
    }

    public String getLectureEndTime(int num){
        return hour12To24(lecTime[num].substring(lecTime[num].indexOf("- ") + 2).trim());
    }
    public int getLectureEndTimeNum(int num){
        String s = getLectureEndTime(num);

        s = s.substring(0, s.indexOf(":"));
        return Integer.parseInt(s);
    }
    public String getTUTEndTime(int num){
        return hour12To24(tutTime[num].substring(tutTime[num].indexOf("- ") + 2).trim());
    }
    public String getTUT2EndTime(int num){
        return hour12To24(tutTime2[num].substring(tutTime2[num].indexOf("- ") + 2).trim());
    }
    public int getTUTEndTimeNum(int num){
        String s = getTUTEndTime(num);
        s = s.substring(0, s.indexOf(":"));
        return Integer.parseInt(s);
    }
    public int getTUT2EndTimeNum(int num){
        String s = getTUT2EndTime(num);
        s = s.substring(0, s.indexOf(":"));
        return Integer.parseInt(s);
    }
    public String hour12To24(String s){

        int num = Integer.parseInt(s.substring(0,s.indexOf(":")).trim());

        String out = "";
        String h = s.substring(0,s.indexOf(":")).trim();
        String min = s.substring(s.indexOf(":")+1, s.indexOf(":") + 3);
        String am_pm = s.substring(s.length() - 2);

        if(am_pm.equalsIgnoreCase("PM")&&s.indexOf("12")==-1) {
            h = (Integer.parseInt(h)+12)+"";
        }
        out = h+":"+min;
        //System.out.println(s+" "+out);
        return out;
    }

}
