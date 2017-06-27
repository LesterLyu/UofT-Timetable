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

## Bugs need to fix (in Version 31)
 
  - [ ] crash with waiting listed course
  
  `
  java.lang.NullPointerException: 
  at com.lvds2000.entity.Course.<init>(Course.java:86)
  at com.lvds2000.uoft_timetable.DrawerActivity.downloadCourseData(DrawerActivity.java:308)
  at com.lvds2000.uoft_timetable.DrawerActivity$3.run(DrawerActivity.java:257)
  `
  - [ ] crash on slow devices/networks (synchronization problem)
  
  `
  java.util.ConcurrentModificationException: 
  at java.util.ArrayList$Itr.next(ArrayList.java:831)
  at com.lvds2000.uoft_timetable.DrawerActivity.downloadCourseData(DrawerActivity.java:306)
  at com.lvds2000.uoft_timetable.TimetableFragment$1$1.run(TimetableFragment.java:141)
  `
## License

http://www.apache.org/licenses/LICENSE-2.0
