/*
 * Part 4: JDBC program as a client program to manage students and courses 
 * */

import java.sql.*;
import java.io.*;
import oracle.jdbc.*;
import oracle.sql.*;
import java.util.*;
import java.math.BigDecimal;

class Part4{
  public static Connection conn = null;
  public static ArrayDescriptor crsNtDesc = null;
  public static ArrayDescriptor stuNtDesc = null;
  public static StructDescriptor stuDesc = null;
  public static StructDescriptor crsDesc = null;
  public static StructDescriptor gradDesc = null;
  public static StructDescriptor underGradDesc = null;

  public static void main (String[] args){
    try{
      DriverManager.registerDriver
      (new oracle.jdbc.driver.OracleDriver());

      System.out.println("Connecting to JDBC...");

      conn = DriverManager.getConnection
        //         ("jdbc:oracle:thin:@moon.scs.carleton.ca:1522:moon",
      ("jdbc:oracle:thin:@localhost:1521:xe",
         //          "oracleusername",
         //          "system",
       "oracle",
         //          "oracleuserpassword");
         //          "oracle11g");
       "oracle11g");

      conn.setAutoCommit(false);
      System.out.println("JDBC connected.\n");


      crsNtDesc = ArrayDescriptor.createDescriptor("COURSE_NT",conn);

      stuNtDesc = ArrayDescriptor.createDescriptor("STUDENT_NT",conn);

      stuDesc = StructDescriptor.createDescriptor("STUDENT_T",conn);
      crsDesc = StructDescriptor.createDescriptor("COURSE_T",conn);
      gradDesc = StructDescriptor.createDescriptor("GRAD_T",conn);
      underGradDesc = StructDescriptor.createDescriptor("UNDERGRAD_T",conn);

      Boolean loop = true;
      while (loop){
        String option = null;

        System.out.println("------------------------------------------------");
        System.out.println("1. Insert Course"); 
        System.out.println("2. Delete Course"); 
        System.out.println("3. Insert Student"); 
        System.out.println("4. Delete Student"); 
        System.out.println("5. Take Course"); 
        System.out.println("6. Drop Course"); 
        System.out.println("7. Add Prerequisite"); 
        System.out.println("8. Exit"); 

        Console console = System.console();
        option = console.readLine("Please select an option (input number or type option): ");

        if (option.equals("8") || option.toUpperCase().equals("EXIT")){
          break;
        }
        else if(option.equals("1") || option.toUpperCase().equals("INSERT COURSE")){
          System.out.println("------------------------------------------------");
          String courseName = console.readLine("Enter course name: ");
          insertCourse(courseName);
        }
        else if(option.equals("2") || option.toUpperCase().equals("DELETE COURSE")){
          System.out.println("------------------------------------------------");
          String courseName = console.readLine("Enter course name: ");
          deleteCourse(courseName);

        }
        else if(option.equals("3") || option.toUpperCase().equals("INSERT STUDENT")){
          System.out.println("------------------------------------------------");
          String stuName = console.readLine("Enter student name: ");
          String gender = console.readLine("Enter gender (M or F): ");
          String grad = console.readLine("Are you an undergrad or grad?: ");
          String phoneNumber = console.readLine("If available, enter phone number (XXX-XXX-XXXX): ");

          insertStudent(stuName,gender,grad,phoneNumber);

        }
        else if(option.equals("4") || option.toUpperCase().equals("DELETE STUDENT")){
          System.out.println("------------------------------------------------");
          String stuName = console.readLine("Enter student name: ");

          deleteStudent(stuName);

        }
        else if(option.equals("5") || option.toUpperCase().equals("TAKE COURSE")){
          System.out.println("------------------------------------------------");
          String stuName = console.readLine("Enter student name: ");
          String crsName = console.readLine("Enter course name: "); 

          takeCourse(stuName,crsName);

        }
        else if(option.equals("6") || option.toUpperCase().equals("DROP COURSE")){
          System.out.println("------------------------------------------------");
          String stuName = console.readLine("Enter student name: ");
          String crsName = console.readLine("Enter course name: "); 

          dropCourse(stuName,crsName);

        }
        else if(option.equals("7") || option.toUpperCase().equals("ADD PREREQUISITE")){
          System.out.println("------------------------------------------------");
          String preCrsName= console.readLine("Enter prerequisite course name: ");
          String crsName= console.readLine("Enter course name to add prerequisite to: "); 

          addPrerequisite(preCrsName,crsName);

        }
      }


      conn.close();
    }
    catch (Exception e){
      System.out.println("SQL exception: ");
      e.printStackTrace();
      System.exit(-1);
    }
  }

  public static void insertCourse(String crsName){
    PreparedStatement pStmt = null;
    ResultSet rs = null;
    int maxCourseNum = 0; 
    try{

      //Check if user entered nothing
      if(crsName == null || crsName.isEmpty()){
        System.out.println("You must enter a course name");
        return;
      }

      //First, check if the course exists in the database
      pStmt = conn.prepareStatement("select 1 from course where name = ?", 
        ResultSet.TYPE_SCROLL_INSENSITIVE,
        ResultSet.CONCUR_READ_ONLY);

      pStmt.setString(1,crsName);
      rs = pStmt.executeQuery();
      if (rs.first()){
        System.out.println("A course with the same name already exists, not inserting");
        return;
      }


      rs.close();
      pStmt.close();

      //Get the max course number in the database, if none exists, then start the course number at 1
      pStmt = conn.prepareStatement("select max(c#) from course",
       ResultSet.TYPE_SCROLL_INSENSITIVE,
       ResultSet.CONCUR_READ_ONLY);

      rs = pStmt.executeQuery();

      if (rs.first()){
        maxCourseNum = rs.getInt(1);
      }

      rs.close();
      pStmt.close();
      
      Object[] crsAttributes = new Object[4];
      crsAttributes[0] = new Integer(maxCourseNum + 1);
      crsAttributes[1] = crsName;
      crsAttributes[2] = new oracle.sql.ARRAY(crsNtDesc,conn,null);
      crsAttributes[3] = new oracle.sql.ARRAY(stuNtDesc,conn,null);

      STRUCT newCourse = new STRUCT(crsDesc,conn,crsAttributes);


      pStmt = conn.prepareStatement("insert into course values(?)");
      pStmt.setObject(1, newCourse);

      pStmt.execute();

      conn.commit();

      System.out.println("Course named " + crsName + " created with course number " + String.format("%d",maxCourseNum + 1));

    }
    catch(Exception e){
      System.out.println("SQL exception: ");
      e.printStackTrace();
      System.exit(-1);
    }
    finally{
      try{
        if (rs != null){
          rs.close();
        }
        if (pStmt != null){
          pStmt.close();
        }
      }
      catch (SQLException e){
        e.printStackTrace();
      }
    }
  }

  public static void deleteCourse(String crsName){
    PreparedStatement pStmt = null;
    ResultSet rs = null;
    try{

      //Check if user entered nothing
      if(crsName == null || crsName.isEmpty()){
        System.out.println("You must enter a course name");
        return;
      }

      //First, check if the course exists in the database
      pStmt = conn.prepareStatement("select value(c) from course c where name = ?", 
        ResultSet.TYPE_SCROLL_INSENSITIVE,
        ResultSet.CONCUR_UPDATABLE);

      //Also need to check if the course doesn't have students. If it does, then can't delete
      pStmt.setString(1,crsName);
      rs = pStmt.executeQuery();
      if (!rs.first()){
        System.out.println("A course with this name doesn't exist, not deleting");
        return;
      }
/*
      CourseT delCourse = (CourseT) rs.getObject(1);

      java.sql.Ref[] studentList = (java.sql.Ref[]) delCourse.getStudents().getArray();
*/

      STRUCT delCourse = (STRUCT) rs.getObject(1);

      Object[] courseAttributes = delCourse.getAttributes();

      oracle.sql.ARRAY students = (oracle.sql.ARRAY) courseAttributes[3];
      java.sql.Ref[] studentList = (java.sql.Ref[]) students.getArray();

      if(studentList != null){
        if(studentList.length != 0){
          System.out.println("There are students still in this course. Cannot delete this course");
          return;
        }

      }

      rs.deleteRow();

      conn.commit();

      System.out.println("Course named " + crsName + " has been deleted.");

    }
    catch(Exception e){
      System.out.println("SQL exception: ");
      e.printStackTrace();
      System.exit(-1);
    }
    finally{
      try{
        if (rs != null){
          rs.close();
        }
        if (pStmt != null){
          pStmt.close();
        }
      }
      catch (SQLException e){
        e.printStackTrace();
      }
    }
  }

  //Helper method to validate a phone number using regex 
  private static boolean isValidPhoneNumber(String phone) {
    if (phone.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}")) return true;
    else return false;
  }

  public static void insertStudent(String stuName, String gender, String grad, String phoneNumber){
    PreparedStatement pStmt = null;
    ResultSet rs = null;
    int maxStuNum = 0;
    try{

      //Checking Input 
      if(stuName == null || stuName.isEmpty()){
        System.out.println("Please enter the student's name");
        return;
      }

      if(!gender.toUpperCase().equals("M") && !gender.toUpperCase().equals("F")){
        System.out.println("Please enter a valid gender (M or F)");
        return;
      }

      if(!grad.toUpperCase().equals("GRAD") && !grad.toUpperCase().equals("UNDERGRAD")){
        System.out.println("Please enter either grad or undergrad");
        return;
      }

      //If there is input for phone, make sure it is in correct format
      if(phoneNumber != null && !phoneNumber.isEmpty() && !isValidPhoneNumber(phoneNumber)){
        System.out.println("Please enter a valid phone number (Format XXX-XXX-XXXX)");
        return;
      }


      //Get the students in the DB
      pStmt = conn.prepareStatement("select value(s) from student s",
        ResultSet.TYPE_SCROLL_INSENSITIVE,
        ResultSet.CONCUR_READ_ONLY);
      rs = pStmt.executeQuery();

      boolean isExists = false;

      //If there is a matching student name to the input specified, set the flag 
      //Meanwhile, get the maximum student number
      while (rs.next()){

        STRUCT stu = (STRUCT) rs.getObject(1);
        Object[] stuAttributes = stu.getAttributes();
        String sName = (String) stuAttributes[1];
        int sNo =  ((BigDecimal) stuAttributes[0]).intValue();

        if(sName.equals(stuName)){
          isExists = true;
          break;
        }
        maxStuNum = sNo; 
      }

      //Return if nname already exists
      if(isExists){
        System.out.println("Student with this name already exists. Not inserting");
        return;
      }

      rs.close();
      pStmt.close();

      //Insert the new student, either grad or undergrad, into database using prepared statement
      pStmt = conn.prepareStatement("insert into student values(?)");


      if (grad.toUpperCase().equals("GRAD")){

        Object[] gradAttributes = new Object[5];
        gradAttributes[0] = new BigDecimal(maxStuNum + 1);
        gradAttributes[1] = stuName;
        gradAttributes[2] = gender;
        gradAttributes[3] = new oracle.sql.ARRAY(crsNtDesc,conn,null);
        gradAttributes[4] = phoneNumber;

        STRUCT aNewGrad = new STRUCT(gradDesc,conn,gradAttributes);

        pStmt.setObject(1,aNewGrad);
      }
      else if (grad.toUpperCase().equals("UNDERGRAD")){
        Object[] underGradAttributes = new Object[5];
        underGradAttributes[0] = new BigDecimal(maxStuNum + 1);
        underGradAttributes[1] = stuName;
        underGradAttributes[2] = gender;
        underGradAttributes[3] = new oracle.sql.ARRAY(crsNtDesc,conn,null);
        underGradAttributes[4] = phoneNumber;

        STRUCT aNewUnderGrad = new STRUCT(underGradDesc,conn,underGradAttributes);
        
        pStmt.setObject(1,aNewUnderGrad);

      }

      pStmt.execute();

      conn.commit();

      System.out.println("Inserted new " + grad + " student named " + stuName + " with student number " + String.format("%d",maxStuNum + 1));

    }
    catch(Exception e){
      System.out.println("SQL exception: ");
      e.printStackTrace();
      System.exit(-1);
    }
    finally{
      try{
        if (rs != null){
          rs.close();
        }
        if (pStmt != null){
          pStmt.close();
        }
      }
      catch (SQLException e){
        e.printStackTrace();
      }
    }
  }

  public static void deleteStudent(String stuName){
    PreparedStatement pStmt = null;
    ResultSet rs = null;
    try{
      if (stuName.isEmpty()){
        System.out.println("Please enter a student name");
        return;
      }

      pStmt = conn.prepareStatement("select value(s) from student s where s.name = ?",
       ResultSet.TYPE_SCROLL_INSENSITIVE,
       ResultSet.CONCUR_UPDATABLE);
      pStmt.setString(1,stuName);
      rs = pStmt.executeQuery();

      if (!rs.first()){
        System.out.println("Student with given name does not exist. Not deleting");
        return;
      }

      STRUCT stu = (STRUCT) rs.getObject(1);
      Object[] stuAttributes = stu.getAttributes();
      oracle.sql.ARRAY stuArray = (oracle.sql.ARRAY) stuAttributes[3];
      java.sql.Ref[] stuCourses = (java.sql.Ref[]) stuArray.getArray();

    //Check if student's taking courses is 0, if it's not, can't delete student
      if(stuCourses.length != 0){
        System.out.println("Student is still taking courses. Cannot delete student");
        return;
      }

      rs.deleteRow();
      conn.commit();

      System.out.println("Deleted student named " + stuName);

    }
    catch(Exception e){
      System.out.println("SQL exception: ");
      e.printStackTrace();
      System.exit(-1);
    }
    finally{
      try{
        if (rs != null){
          rs.close();
        }
        if (pStmt != null){
          pStmt.close();
        }
      }
      catch (SQLException e){
        e.printStackTrace();
      }
    }
  }

  public static void takeCourse(String stuName, String crsName){
    PreparedStatement pStmt = null;
    ResultSet rs = null;
    REF studentRef = null;
    REF courseRef = null;

    try{

    //Check course existence in database, getting ref
      pStmt = conn.prepareStatement ("select ref(c) from course c where c.name = ?",
        ResultSet.TYPE_SCROLL_INSENSITIVE,
        ResultSet.CONCUR_READ_ONLY);

      pStmt.setString(1,crsName);

      rs = pStmt.executeQuery();
      if(!rs.first()){
        System.out.println("Specified course by name does not exist");
        return;
      }


      courseRef = (REF) rs.getRef(1);
      rs.close();
      pStmt.close();

    //Check if student exists in the database
      pStmt = conn.prepareStatement("select ref(s), value(s) from student s where s.name = ?",
        ResultSet.TYPE_SCROLL_INSENSITIVE,
        ResultSet.CONCUR_READ_ONLY);
      pStmt.setString(1,stuName);

      rs = pStmt.executeQuery();
      if(!rs.first()){
        System.out.println("Specified student by name does not exist");
        return;
      }

      studentRef = (REF) rs.getRef(1);

      STRUCT student = (STRUCT) rs.getObject(2);
      Object[] studentAttributes = student.getAttributes();

    //Construct the nested table array of the student's courses
      REF[] stuCourses = (REF[]) ((oracle.sql.ARRAY) studentAttributes[3]).getArray();

    //Compare the course ref found earlier with every ref value in the student's courses, and set the flag to true if there is a match(error state)
      boolean isAlreadyTaken = false;
      for (REF i : stuCourses){
        if (i.toString().equals(courseRef.toString())){
          isAlreadyTaken = true;
          break;
        }
      }

      if(isAlreadyTaken){
        System.out.println("This student is already taking this course, cannot take this course again");
        return;
      }

      rs.close();
      pStmt.close();

    //Add course into student's list of courses
      pStmt = conn.prepareStatement("insert into table(select s.courses from student s where s.name = ?)" 
        + "select ref(c) from course c where c.name = ?",
        ResultSet.TYPE_SCROLL_INSENSITIVE,
        ResultSet.CONCUR_UPDATABLE);

      pStmt.setString(1,stuName);
      pStmt.setString(2,crsName);

      pStmt.execute();

      pStmt.close();

    //Add student into course's list of students
      pStmt = conn.prepareStatement("insert into table(select c.students from course c where c.name = ?)" 
        + "select ref(s) from student s where s.name = ?",
        ResultSet.TYPE_SCROLL_INSENSITIVE,
        ResultSet.CONCUR_UPDATABLE);

      pStmt.setString(1,crsName);
      pStmt.setString(2,stuName);

      pStmt.execute();

      pStmt.close();

      conn.commit();

      System.out.println("Added student named " + stuName + " into course " + crsName);

    }
    catch(Exception e){
      System.out.println("SQL exception: ");
      e.printStackTrace();
      System.exit(-1);
    }
    finally{
      try{
        if (rs != null){
          rs.close();
        }
        if (pStmt != null){
          pStmt.close();
        }
      }
      catch (SQLException e){
        e.printStackTrace();
      }
    }
  }

  public static void dropCourse(String stuName, String crsName){
    PreparedStatement pStmt = null;
    ResultSet rs = null;
    REF crsRef = null;

    try{



    //Get the course's ref 
      pStmt = conn.prepareStatement("select ref(c) from course c where c.name = ?",
       ResultSet.TYPE_SCROLL_INSENSITIVE,
       ResultSet.CONCUR_READ_ONLY);

      pStmt.setString(1,crsName);
      rs = pStmt.executeQuery();
      if(!rs.first()){
        System.out.println("Course with specified name does not exist");
        return;
      }

      crsRef = (REF) rs.getRef(1);

      rs.close();
      pStmt.close();


    //Get the student's object value
      pStmt = conn.prepareStatement("select value(s) from student s where s.name = ?",
       ResultSet.TYPE_SCROLL_INSENSITIVE,
       ResultSet.CONCUR_READ_ONLY);
      pStmt.setString(1,stuName);

      rs = pStmt.executeQuery();
      if(!rs.first()){
        System.out.println("Student with specified name does not exist");
        return;
      }

      STRUCT student = (STRUCT) rs.getObject(1);

      Object[] studentAttributes = student.getAttributes();

    //Get the student's nested table of course refs
      REF[] studentCourses = (REF[]) ((oracle.sql.ARRAY)studentAttributes[3]).getArray();


    //Loop through the student's courses being taken
      boolean hasCourse = false;
      for (REF i : studentCourses){
        if(i.toString().equals(crsRef.toString())){
          hasCourse = true;
          break;
        }
      }

    //Case where there was no matches with the course's ref in the student's courses taken. 
      if (hasCourse == false){
        System.out.println("Student is not taking this course specified, not dropping any courses");
        return;
      }

      rs.close();
      pStmt.close();

    //Delete the course ref from the student's list of courses
      pStmt = conn.prepareStatement("delete from table (select s.courses from student s where s.name = ?) courses" 
        + " WHERE deref(courses.COLUMN_VALUE).name = ?",
        ResultSet.TYPE_SCROLL_INSENSITIVE,
        ResultSet.CONCUR_UPDATABLE);

      pStmt.setString(1,stuName);
      pStmt.setString(2,crsName);

      pStmt.execute();

      pStmt.close();

    //Delete the student ref from the course's list of students
      pStmt = conn.prepareStatement("delete from table (select c.students from course c where c.name = ?) students"
        + " WHERE deref(students.COLUMN_VALUE).name = ?",
        ResultSet.TYPE_SCROLL_INSENSITIVE,
        ResultSet.CONCUR_UPDATABLE);

      pStmt.setString(1,crsName);
      pStmt.setString(2,stuName);

      pStmt.execute();
      pStmt.close();

      conn.commit();

      System.out.println("Dropping student named " + stuName + " from course named " + crsName);



    }
    catch(Exception e){
      System.out.println("SQL exception: ");
      e.printStackTrace();
      System.exit(-1);
    }
    finally{
      try{
        if (rs != null){
          rs.close();
        }
        if (pStmt != null){
          pStmt.close();
        }
      }
      catch (SQLException e){
        e.printStackTrace();
      }
    }
  }

  public static void addPrerequisite(String crsAName, String crsBName){
    PreparedStatement pStmt = null;
    ResultSet rs = null;
    REF crsARef = null;
    REF crsBRef = null;
    STRUCT crsA = null;
    STRUCT crsB = null;
    try{

      //Get the refs for course A and B 
      
      //Get ref of course A
      pStmt = conn.prepareStatement("select ref(c), value(c) from course c where c.name = ?",
                                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                                    ResultSet.CONCUR_READ_ONLY);

      pStmt.setString(1,crsAName);

      rs = pStmt.executeQuery();
      if(!rs.first()){
        System.out.println("Prerequisite course does not exist. Can't add prerequisite.");
        return;
      }

      crsARef = (REF) rs.getRef(1);
      crsA = (STRUCT) rs.getObject(2);

      rs.close();
      pStmt.close();


      //Get ref of course B, as well as the the object value (to get the nested table of prereqs)
      pStmt = conn.prepareStatement("select ref(c), value(c) from course c where c.name = ?",
                                     ResultSet.TYPE_SCROLL_INSENSITIVE,
                                     ResultSet.CONCUR_READ_ONLY);

      pStmt.setString(1,crsBName);

      rs = pStmt.executeQuery();

      if(!rs.first()){
        System.out.println("Course to add a prerequisite to does not exist. Can't add prerequisite");
        return;
      }

      crsBRef = (REF) rs.getRef(1); 
      crsB = (STRUCT) rs.getObject(2);

      Object[] crsAAttributes = crsA.getAttributes();
      REF[] crsAPrereqs = (REF[]) ((oracle.sql.ARRAY) crsAAttributes[2]).getArray();

      //Check if Prerequisite course A has B as a prerequisite. This would introduce a circular prequisite for the courses which doesn't make sense
      for(REF i : crsAPrereqs){
        if(i.toString().equals(crsBRef.toString())){
          System.out.println("Prerequisite course (A) has course (B) as a prerequisite. Cannot have this circular prereq logic");
          return;
        }
      }



      Object[] crsBAttributes = crsB.getAttributes();
      REF[] crsBPrereqs = (REF[]) ((oracle.sql.ARRAY) crsBAttributes[2]).getArray();

      //Check if prerequisite course A is already a prereq for B. If so, then return with error message
      for (REF i: crsBPrereqs){
        if (i.toString().equals(crsARef.toString())){
          System.out.println("Course A is already a prerequisite of the target course B. Not adding this prerequisite again");
          return;
        }
      }


      rs.close();
      pStmt.close();

      pStmt = conn.prepareStatement("insert into table(select c.prereqs from course c where c.name = ?)" 
                                     + "select ref(x) from course x where x.name = ?",
                                     ResultSet.TYPE_SCROLL_INSENSITIVE,
                                     ResultSet.CONCUR_UPDATABLE);
      pStmt.setString(1,crsBName);
      pStmt.setString(2,crsAName);
      pStmt.execute();

      pStmt.close();

      conn.commit();

      System.out.println("Made course named " + crsAName + " a prerequisite for course named " + crsBName);




    }
    catch(Exception e){
      System.out.println("SQL exception: ");
      e.printStackTrace();
      System.exit(-1);
    }
    finally{
      try{
        if (rs != null){
          rs.close();
        }
        if (pStmt != null){
          pStmt.close();
        }
      }
      catch (SQLException e){
        e.printStackTrace();
      }
    }
  }


}
