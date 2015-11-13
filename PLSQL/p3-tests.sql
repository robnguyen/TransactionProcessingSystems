/* part of Part 3: This file contains a PL/SQL that will execute tests for each of the procedures 
 * from the Students and Courses packages
 * This program will add some students/courses for testing purposes, but they will be removed by the end of the program
 */

DECLARE
BEGIN
  dbms_output.put_line('--------------------------------------');
  dbms_output.put_line('Start testing cases add_student(name):');
  dbms_output.put_line('--------------------------------------');
  dbms_output.put_line('1.Adding student with null name');
  students.add_student(null);
  dbms_output.put_line('2.Adding student that isn''t in table');
  students.add_student('Valerie');
  dbms_output.put_line('3.Adding a student with a name already in table');
  students.add_student('Cordelia');
  dbms_output.put_line('--------------------------------------');
 
  
  dbms_output.put_line('Start testing cases add_student(name,birthdate):');
  dbms_output.put_line('--------------------------------------');
  dbms_output.put_line('1.Adding student with null name, null birthdate');
  students.add_student(null,null);
  dbms_output.put_line('2.Adding student that isn''t in table, with a birthday');
  students.add_student('Julia',to_date('1990-05-29','yyyy-mm-dd'));
  dbms_output.put_line('3.Adding student that is already in table, with a birthday');
  students.add_student('Dee',to_date('1990-05-29','yyyy-mm-dd'));
  dbms_output.put_line('--------------------------------------');

  dbms_output.put_line('Start testing cases add_student(name,birthdate,address):');
  dbms_output.put_line('--------------------------------------');
  dbms_output.put_line('1.Adding student with null name, null birthdate, null address');
  students.add_student(null,null,null);
  dbms_output.put_line('2.Adding student that isn''t in table, with a birthday and address');
  students.add_student('Tyrone',to_date('1990-05-29','yyyy-mm-dd'), '7 Loonie Street');
  dbms_output.put_line('3.Adding student that is already in table, with a birthday and address');
  students.add_student('Dee',to_date('1990-05-29','yyyy-mm-dd'), '9 Mirror Street');
  dbms_output.put_line('--------------------------------------');

  dbms_output.put_line('Start testing cases delete_student(name):');
  dbms_output.put_line('--------------------------------------');
  dbms_output.put_line('1. Delete a null student');
  students.delete_student(null);
  dbms_output.put_line('2. Delete a student not actually in table');
  students.delete_student('Draco');
  dbms_output.put_line('3. Delete a student in table');
  students.delete_student('Valerie');
  dbms_output.put_line('--------------------------------------');
  
  
  dbms_output.put_line('Start testing cases change_name(s#,name):');
  dbms_output.put_line('--------------------------------------');
  dbms_output.put_line('1. Change a name for a null s# student');
  students.change_name(null, 'Morna');
  dbms_output.put_line('2. Change a name for a s# not in student table');
  students.change_name(20,'Bonnie');
  dbms_output.put_line('3. Change a name for a student in the database');
  students.change_name(12,'Nona');
  dbms_output.put_line('--------------------------------------');


  dbms_output.put_line('Start testing cases change_address(s#,newaddr):');
  dbms_output.put_line('--------------------------------------');
  dbms_output.put_line('1. Change an address for a null s# student');
  students.change_address(null, '72 clown street');
  dbms_output.put_line('2. Change an address for a s# not in student table');
  students.change_address(20,'72 clown street');
  dbms_output.put_line('3. Change a name for a student in the database');
  students.change_address(12,'22 Jump street');
  dbms_output.put_line('--------------------------------------');

  dbms_output.put_line('Start testing cases enroll_course(s#,c#):');
  dbms_output.put_line('--------------------------------------');
  dbms_output.put_line('1. NULL case test'); 
  students.enroll_course(null, null); 
  dbms_output.put_line('2. Enroll a student normal case'); 
  students.enroll_course(12,6);
  dbms_output.put_line('3. Try to enroll the same student in the same class');
  students.enroll_course(12,6);
  dbms_output.put_line('4. Enroll the student in a course that would conflict with the previous course enrolled'); 
  students.enroll_course(12,10);
  dbms_output.put_line('--------------------------------------');
  
  dbms_output.put_line('Start testing cases add_course(c#,name):');
  dbms_output.put_line('--------------------------------------');
  dbms_output.put_line('1. NULL case test'); 
  courses.add_course(null, null); 
  dbms_output.put_line('2. Create a course normal case'); 
  courses.add_course(11,'BIRD3803');
  dbms_output.put_line('3. Create a course with same c#, different course name');
  courses.add_course(11,'BLAH3803');
  dbms_output.put_line('4. Create a course with different c#, same course name');
  courses.add_course(12,'BIRD3803');
  dbms_output.put_line('--------------------------------------');


  dbms_output.put_line('Start testing cases add_course(c#,name,loc,days,time):');
  dbms_output.put_line('--------------------------------------');
  dbms_output.put_line('1. NULL case test'); 
  courses.add_course(null,null,null,null,null);
  dbms_output.put_line('2. Create a course normal case');
  courses.add_course(12,'COMP4004','TB208','TR','3:35-4:25');
  dbms_output.put_line('3. Create a course but would time/location conflict with previously added course');
  courses.add_course(13,'COMP4005','TB208','TR','3:35-4:25');
  dbms_output.put_line('4. Create a course With an invalid Days (not MWF or TR)');
  courses.add_course(14,'COMP8990','TB209','T','3:35-4:25');
  dbms_output.put_line('5. Create a course With an invalid times)');
  courses.add_course(14,'COMP8990','TB209','TR','2:00-4:25');
  
  
  dbms_output.put_line('--------------------------------------');


  dbms_output.put_line('Start testing cases delete_course(name):');
  dbms_output.put_line('--------------------------------------');
  dbms_output.put_line('1. NULL case test');
  courses.delete_course(NULL);
  dbms_output.put_line('2. delete a course that has students enrolled');
  students.enroll_course(10,11);
  courses.delete_course('BIRD3803');
  dbms_output.put_line('3. delete a course normal (no students enrolled)');
  courses.delete_course('COMP4004');
  dbms_output.put_line('--------------------------------------');

  dbms_output.put_line('Start testing cases change_loc(c#,loc)');
  dbms_output.put_line('--------------------------------------');
  dbms_output.put_line('1. NULL case test');
  courses.change_loc(NULL,NULL);
  dbms_output.put_line('2. Change location normal case (no conflicts');
  courses.add_course(13,'COMP4005','TB208','TR','3:35-4:25');
  courses.change_loc(13,'TB308');
  dbms_output.put_line('3. Change location conflict case');
  courses.change_loc(13,'AZ103');
  dbms_output.put_line('--------------------------------------');


  dbms_output.put_line('Start testing cases give_mark(s#,c#)');
  dbms_output.put_line('--------------------------------------');
  students.enroll_course(10,13);
  dbms_output.put_line('1. NULL case test');
  courses.give_mark(NULL,NULL,NULL);
  dbms_output.put_line('2. Test for student not enrolled in this course');
  courses.give_mark(4,13,87.0);
  dbms_output.put_line('3. Mark is not proper format');
  courses.give_mark(10,13,-40);
  dbms_output.put_line('4. Normal test case for giving mark to enrolled student');
  courses.give_mark(10,13,78.0);
  dbms_output.put_line('--------------------------------------');
  
  

  dbms_output.put_line('Deleting all students that were added in this test (that were not already deleted)');
  students.delete_student('Nona');
  students.delete_student('Tyrone');
  
  dbms_output.put_line('Deleting all courses that were added in this test (that were not already deleted');

  delete from grade where s#=10 and c#=13;
  delete from grade where s#=10 and c#=11;
  courses.delete_course('BIRD3803');
  courses.delete_course('COMP4004');
  courses.delete_course('COMP4005');

  commit;
END;
/
