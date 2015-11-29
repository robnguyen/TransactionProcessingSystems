/*
 * Part 4-test: Test cases 
 * */

import java.sql.*;
import java.io.*;
import oracle.jdbc.*;
import oracle.sql.*;
import java.util.*;
import java.math.BigDecimal;


class Part4Test{
    public static void main (String[] args){
    try{
      Part4 p4 = new Part4();

      DriverManager.registerDriver
      (new oracle.jdbc.driver.OracleDriver());

      System.out.println("Connecting to JDBC...");

      p4.conn = DriverManager.getConnection
        //         ("jdbc:oracle:thin:@moon.scs.carleton.ca:1522:moon",
      ("jdbc:oracle:thin:@localhost:1521:xe",
         //          "oracleusername",
         //          "system",
       "oracle",
         //          "oracleuserpassword");
         //          "oracle11g");
       "oracle11g");

      p4.conn.setAutoCommit(false);
      System.out.println("JDBC connected.\n");

      p4.crsNtDesc = ArrayDescriptor.createDescriptor("COURSE_NT",p4.conn);

      p4.stuNtDesc = ArrayDescriptor.createDescriptor("STUDENT_NT",p4.conn);

      p4.stuDesc = StructDescriptor.createDescriptor("STUDENT_T",p4.conn);
      p4.crsDesc = StructDescriptor.createDescriptor("COURSE_T",p4.conn);
      p4.gradDesc = StructDescriptor.createDescriptor("GRAD_T",p4.conn);
      p4.underGradDesc = StructDescriptor.createDescriptor("UNDERGRAD_T",p4.conn);


      System.out.println("====================");
      System.out.println("Begin testing");

      System.out.println("Test cases for insert_course");
      System.out.println("----------------------------");

      System.out.println("Test case 1: Inserting an empty string for a course");
      p4.insertCourse(null);
      System.out.println("Test case 2: Inserting a course that already exists in DB");
      p4.insertCourse("OS");
      System.out.println("Test case 3: Inserting a course regular");
      p4.insertCourse("History");

      System.out.println("====================");

      System.out.println("Test cases for delete_course");
      System.out.println("----------------------------");
      System.out.println("Test case 1: Deleting an emptys string name for a course");
      p4.deleteCourse(null);
      System.out.println("Test case 2: Deleting a course that still has students in it");
      p4.deleteCourse("DBMS");
      System.out.println("Test Case 3: Deleting a course that has no students in it");
      p4.deleteCourse("History");

      System.out.println("====================");
      System.out.println("Test cases for insert_student");
      System.out.println("----------------------------");
      System.out.println("Test case 1: Inserting a student with an empty string for a name");
      p4.insertStudent("","M","grad","");
      System.out.println("test case 2: Inserting a student that already exists in the DB");
      p4.insertStudent("Mike","M","undergrad","");
      System.out.println("Test case 3: inserting a student that doesn't exist in DB");
      p4.insertStudent("Cordelia","F","undergrad","613-727-6031");

      System.out.println("====================");
      System.out.println("Test cases for delete_student");
      System.out.println("----------------------------");
      System.out.println("Test case 1: Trying to delete a student with no name specified");
      p4.deleteStudent("");
      System.out.println("Test case 2: Trying to delete a student who has courses being taken");
      p4.deleteStudent("Mike");
      System.out.println("Test case 3: Trying to delete a student with no courses taken");
      p4.deleteStudent("Cordelia");

      System.out.println("====================");
      System.out.println("Test cases for take_course");
      System.out.println("----------------------------");
      System.out.println("Test case 1: Trying to take a course for a student who doesn't exist in DB");
      p4.takeCourse("Cordelia","DBMS");
      System.out.println("Test case 2: Trying to take a course for a student where the course doesn't exist in DB");
      p4.takeCourse("Mike","Scientology");
      System.out.println("Test case 3: Trying to take a course for a student where the course is already being taken by the student");
      p4.takeCourse("Mike","DBMS");
      System.out.println("Test case 4: Taking a course for a student who isn't taking it");
      p4.takeCourse("Mike","OS");

      System.out.println("====================");
      System.out.println("Test cases for drop_course");
      System.out.println("----------------------------");
      System.out.println("Test case 1: Trying to drop a course for a student who doesn't exist in DB");
      p4.dropCourse("","DBMS");
      System.out.println("Test case 2: trying to drop a course for a student where the student isn't taking the course");
      p4.dropCourse("Mike","Math");
      System.out.println("Test case 3: Dropping a course that the student is taking");
      p4.dropCourse("Mike","OS");

      System.out.println("====================");
      System.out.println("Test cases for add_prerequisite");
      System.out.println("----------------------------");
      System.out.println("Test case 1: Adding a prerequisite which doesn't exist in DB");
      p4.addPrerequisite("Basket","DBMS");
      System.out.println("test case 2: Adding a prerequisite, where the prerequisite has the course as a prereq, circular prereq");
      p4.addPrerequisite("Comp","Math");
      System.out.println("Test case 3: Adding a prerequisite, which is already a prerequisite for the course");
      p4.addPrerequisite("Math","Comp");
      System.out.println("Test case 4: Adding a prerequisite");
      p4.addPrerequisite("Math","DBMS");

      System.out.println("====================");
      System.out.println(" END OF TESTING ");
      System.out.println("----------------------------");


      p4.conn.close();
    }
    catch (Exception e){
      System.out.println("SQL exception: ");
      e.printStackTrace();
      System.exit(-1);
    }
  }


}
