# UofT-Timetable
A Timetable Android app for University of Toronto.

## Screenshots

<img src="screenshots/1.png" height="400" alt="Screenshot"/> <img src="screenshots/2.png" height="400" alt="Screenshot"/> <img src="screenshots/3.png" height="400" alt="Screenshot"/>

## Support
Contact me via lvds2000@gmail.com

## TODO

 - [ ] Homescreen Widget

 - [ ] Show conflicts

 - [x] Support summer timetable  
 
 - [ ] Show course info(description) in a better way

## Known Bugs (in Version 33)
### I can see all crashes on Google Play Console now even if users didn't report it. I will try to fix these bugs once I have time, after my assignments due...
 
  - [x] crash when refresh timetable in multiple views (synchronization problem)
  
  `
  java.util.ConcurrentModificationException: 
  at java.util.ArrayList$Itr.next(ArrayList.java:831)
  at com.lvds2000.uoft_timetable.DrawerActivity.downloadCourseData(DrawerActivity.java:306)
  at com.lvds2000.uoft_timetable.TimetableFragment$1$1.run(TimetableFragment.java:141)
  `
  - [x] sometimes can't instant update timetable if already logged in, it always re-login.
  
  - [x] course may persist there after modification made on acorn, need to re-open this app.
  
  - [ ] some course block will be overrided if they have time conflict, please try not have too many unnecessary courses in enrollment cart for now.
  - [ ] can't support course has more than 3 activities. (i.e. LEC + TUT + PRA + ??? )
  
  `
  java.lang.ArrayIndexOutOfBoundsException: 
  at com.lvds2000.uoft_timetable.CourseListFragment.onCreateView(CourseListFragment.java:81)
  `
 ## Unknown Bugs
 
  - [ ] ??? (8 reports)
  
  `
  com.google.gson.JsonSyntaxException: 
  at com.google.gson.JsonParser.parse(JsonParser.java:65)
  at com.google.gson.JsonParser.parse(JsonParser.java:45)
  at com.lvds2000.AcornAPI.course.CourseManager$1.onResponse(CourseManager.java:124)
  `
  
 ## License

http://www.apache.org/licenses/LICENSE-2.0
