# UofT-Timetable
A Timetable Android app for University of Toronto.

[![Play Store](http://imgur.com/utWa1co.png "Play Store")](https://play.google.com/store/apps/details?id=com.lvds2000.utsccsuntility)

## Screenshots

<img src="screenshots/1.png" height="400" alt="Screenshot"/> <img src="screenshots/2.png" height="400" alt="Screenshot"/> <img src="screenshots/3.png" height="400" alt="Screenshot"/>

## Support
Contact me via lvds2000@gmail.com

## TODO

 - [ ] Homescreen Widget

 - [ ] Show conflicts

 - [x] Support summer timetable  
 
 - [x] Show course info(description) in a better way

## Known Bugs (in Version 33)
### I can see all crashes on Google Play Console now even if users didn't report it. I will try to fix these bugs once I have time, after my assignments due...
  
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
