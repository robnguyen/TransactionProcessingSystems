/* DESC This program adds 10 students and 10 courses (non-conflicting in times)
 *      Also enrolls students to some courses, and gives marks for these students
 * By calling the students and courses packages and their procedures (each add_student and add_course overloaded function is called)
 * NOTE: RUN THIS BEFORE running p3-tests.sql 
 */

DECLARE
BEGIN
  dbms_output.put_line('Adding 10 students....');
  students.add_student('Rob');
  students.add_student('Greg');
  students.add_student('Dee', to_date('1987-05-10','yyyy-mm-dd'));
  students.add_student('Mac', to_date('1978-04-20','yyyy-mm-dd'));
  students.add_student('Cordelia', to_date('1992-02-15'), '78 Sandcliffe Avenue');
  students.add_student('Nick', to_date('1993-03-21'), '12 Doublecastle Private');
  students.add_student('Bonnie');
  students.add_student('Charlie');
  students.add_student('Dennis', to_date('1985-08-29','yyyy-mm-dd'));
  students.add_student('Jack', to_date('1972-09-01', 'yyyy-mm-dd'), '8 Cresthaven Drive');

  dbms_output.put_line('Adding 10 courses...');
  courses.add_course(1,'COMP1406');
  courses.add_course(2,'COMP2401');
  courses.add_course(3,'COMP3004','TB208','MWF','3:35-4:25');
  courses.add_course(4,'COMP3005','TB308','TR','8:35-9:25');
  courses.add_course(5,'COMP3000','SA406','MWF','1:35-2:25');
  courses.add_course(6,'COMP3308','SA516','TR','10:35-11:25');
  courses.add_course(7,'COMP4003','MK4302','MWF','10:35-11:25');
  courses.add_course(8,'BIOL2600', 'TB308','MWF','8:35-9:25');
  courses.add_course(9,'COMP2402','AZ103','TR','3:35-4:25');
  courses.add_course(10,'COMP3007','MK4302','TR','10:35-11:25');

  dbms_output.put_line('Enrolling students in courses...');
  students.enroll_course(1,3);
  students.enroll_course(1,4);
  students.enroll_course(1,5);
  students.enroll_course(1,6);

  students.enroll_course(2,3);
  students.enroll_course(2,5);
  
  students.enroll_course(3,10);
  students.enroll_course(3,9);

  students.enroll_course(4,6);
  students.enroll_course(4,7);
  students.enroll_course(5,8);
  students.enroll_course(5,9);
  students.enroll_course(6,3);
  students.enroll_course(6,4);
  students.enroll_course(7,9);
  students.enroll_course(7,10);

  dbms_output.put_line('Giving marks for students in some courses...');

  courses.give_mark(1,3,95.0);
  courses.give_mark(1,5,75.0);
  courses.give_mark(2,3,70.0);
  courses.give_mark(2,5,86.0);
  courses.give_mark(3,10,76.0);
  courses.give_mark(3,9,69.0);
  courses.give_mark(4,6,90.0);
  courses.give_mark(5,9,79.0);
  courses.give_mark(6,3,80.0);
  courses.give_mark(7,9,90.0);
  courses.give_mark(7,10,79.0);
  
  
  
END;
/
