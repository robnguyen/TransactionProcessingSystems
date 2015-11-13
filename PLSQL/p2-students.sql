/* STUDENTS package containing functions involving student table*/
CREATE OR REPLACE PACKAGE STUDENTS IS --spec

  /*PROCEDURE_NAME: add_student
  DESC: adds a student to the student table
  @param v_name: name of student(a NUMBER)
   */
  PROCEDURE add_student(v_name IN STUDENT.name%type);
  
  /*PROCEDURE_NAME: add_student
  DESC: adds a student to the student table
  @param v_name: name of student (a NUMBER)
  @param v_birthdate: birth day of the student (a DATE)
   */
  PROCEDURE add_student(v_name IN STUDENT.name%type,
                        v_birthdate IN STUDENT.birthdate%type);
  
  /*PROCEDURE_NAME: add_student
  DESC: adds a student to the student table
  @param v_name: name of student (a NUMBER)
  @param v_birthdate: birth day of the student (a DATE)
  @param v_address: address of student (a varchar2)
   */
  PROCEDURE add_student(v_name IN STUDENT.name%type,
                        v_birthdate IN STUDENT.birthdate%type,
                        v_address IN STUDENT.address%type);
  
  /*PROCEDURE_NAME: delete_student
  DESC: deletes a student from the student table, given a name
  @param v_name: name of student (a string)
   */
  PROCEDURE delete_student(v_name IN STUDENT.name%type);
  
  /*PROCEDURE_NAME: change_name
  DESC: Change a student's name given a S# 
  @param v_s#: student number (an INTEGER) 
  @param v_newname: the new name to change student record with (a varchar2)
   */
  PROCEDURE change_name(v_s# IN STUDENT.s#%type,
                        v_newname IN STUDENT.name%type);
  
  /*PROCEDURE_NAME: change_address
  DESC: Change a student's address given a S# 
  @param v_s#: student number (an INTEGER) 
  @param v_address: the new address to change student record with (a varchar2)
   */
  PROCEDURE change_address(v_s# IN STUDENT.s#%type,
                           v_newaddr IN STUDENT.address%type);    
  
  /*PROCEDURE_NAME: enroll_course 
  DESC: Enroll a student to a course, given a student and course # 
  @param v_s#: student number (an INTEGER) 
  @param v_c#: course number (an INTEGER)
   */
  PROCEDURE enroll_course(v_s# IN STUDENT.s#%type,
                          v_c# IN COURSE.c#%type);


END STUDENTS;
/

/* STUDENTS package body containing functions involving student table*/
CREATE OR REPLACE PACKAGE BODY STUDENTS IS --body

  /*PROCEDURE_NAME: add_student
  DESC: adds a student to the student table
  @param v_name: name of student(a NUMBER)
   */
  PROCEDURE add_student(v_name IN STUDENT.name%type)
  IS
  BEGIN
    add_student(v_name,NULL,NULL);
  END;
  
  
  /*PROCEDURE_NAME: add_student
  DESC: adds a student to the student table
  @param v_name: name of student (a NUMBER)
  @param v_birthdate: birth day of the student (a DATE)
   */
  PROCEDURE add_student(v_name IN STUDENT.name%type,
                        v_birthdate IN STUDENT.birthdate%type)
  IS
  BEGIN
    add_student(v_name,v_birthdate,NULL);  
  END;
  
  
  /*PROCEDURE_NAME: add_student
  DESC: adds a student to the student table
  @param v_name: name of student (a NUMBER)
  @param v_birthdate: birth day of the student (a DATE)
  @param v_address: address of student (a varchar2)
   */
  PROCEDURE add_student(v_name IN STUDENT.name%type,
                        v_birthdate IN STUDENT.birthdate%type,
                        v_address IN STUDENT.address%type)
  IS
    max_stu#  STUDENT.s#%type;
    name_exists EXCEPTION;
    PRAGMA EXCEPTION_INIT(name_exists,-1);    
    null_name   EXCEPTION;
    PRAGMA EXCEPTION_INIT(null_name, -1400);
  BEGIN
  
    select max(s#) into max_stu#
    FROM STUDENT s;
  
    --check if result returned NULL (no tuples from student table), start max_stu at 1 now; 
    if max_stu# is NULL then
      max_stu# := 1;
    else 
      max_stu# := max_stu# + 1;
    end if;
  
    insert into student values (max_stu#, v_name, v_birthdate, v_address );
  
    commit; 

    dbms_output.put_line('Student ' || v_name || '  was added');

  EXCEPTION
    when name_exists then
     dbms_output.put_line('Student name already exists in the database'); 
    when null_name then
      dbms_output.put_line('Student name that was entered was null, cannot add this student');
  
  END;
  
  
  /*PROCEDURE_NAME: delete_student
  DESC: deletes a student from the student table, given a name
  @param v_name: name of student (a string)
   */
  PROCEDURE delete_student(v_name IN STUDENT.name%type)
  IS
    v_count INTEGER;
  BEGIN


    /*check if the name is in the database. If student is not in table, tell user that no deletion
     took place*/
    select COUNT(*) into v_count 
    from student s 
    where s.name = v_name;
    
    if v_count > 0 then 
      delete from student s where s.name = v_name;   
      dbms_output.put_line('Student ' || v_name || ' was deleted');  
    else 
      dbms_output.put_line('Student is not in the table, no deletion took place');
    end if;
    
    commit;
    
  END;
  
  
  /*PROCEDURE_NAME: change_name
  DESC: Change a student's name given a S# 
  @param v_s#: student number (an INTEGER) 
  @param v_newname: the new name to change student record with (a varchar2)
   */
  PROCEDURE change_name(v_s# IN STUDENT.s#%type,
                        v_newname IN STUDENT.name%type)
  IS
    v_count INTEGER;
    stu_not_exist EXCEPTION;
  BEGIN

    --Check if the s# is in the student table
    select count(*) into v_count 
    from student 
    where s# = v_s#;

    if v_count > 0 then 
      update student set name = v_newname where s# = v_s#;
      dbms_output.put_line('Student ' || v_s# || ' name was changed to ' || v_newname);  
    else 
      raise stu_not_exist;
    end if;

    commit;

  EXCEPTION
    when stu_not_exist then
      dbms_output.put_line('The s# given does not match with any in the student table, no update took place');
       

  END;
  
  
  /*PROCEDURE_NAME: change_address
  DESC: Change a student's address given a S# 
  @param v_s#: student number (an INTEGER) 
  @param v_newaddr: the new address to change student record with (a varchar2)
   */
  PROCEDURE change_address(v_s# IN STUDENT.s#%type,
                           v_newaddr IN STUDENT.address%type)    
  IS
    v_count INTEGER;
    stu_not_exist EXCEPTION;
  BEGIN
    
    --Check if the s# is in the student table
    select count(*) into v_count 
    from student 
    where s# = v_s#;

    if v_count > 0 then 
      update student set address = v_newaddr where s# = v_s#;
      dbms_output.put_line('Student ' || v_s# || ' address was changed to ' || v_newaddr);  
    else 
      raise stu_not_exist;
    end if;

    commit;

  EXCEPTION 
    when stu_not_exist then
      dbms_output.put_line('The s# given does not match with any in the student table, no update took place');
        
  END;
  
  /*PROCEDURE_NAME: enroll_course 
  DESC: Enroll a student to a course, given a student and course # 
  @param v_s#: student number (an INTEGER) 
  @param v_c#: course number (an INTEGER)
   */
  PROCEDURE enroll_course(v_s# IN STUDENT.s#%type,
                          v_c# IN COURSE.c#%type)
  IS
    v_days                COURSE.days%type;
    v_times               COURSE.times%type;
    v_count               INTEGER;
    stu_already_enrolled  EXCEPTION;
    PRAGMA EXCEPTION_INIT(stu_already_enrolled,-1);
    time_conflict         EXCEPTION;
    null_value            EXCEPTION;
    PRAGMA EXCEPTION_INIT(null_value,-1400);

  BEGIN

    -- Get given course's time, and days 
    select days, times into v_days, v_times
    FROM course
    WHERE c# = v_c#;

    -- Check if there is a time conflict with any other of the student's courses
    select count(*) into v_count
    FROM grade g, course c
    WHERE g.s#        = v_s#
    AND   g.c#        = c.c#
    AND   c.days      = v_days
    AND   c.times     = v_times
    AND   c.c#       != v_c#;

    if v_count > 0 then
      raise time_conflict;
    end if;

    insert into grade values (v_s#,v_c#,NULL);
    dbms_output.put_line('Student# ' || v_s# || ' has been enrolled in Course# ' ||v_c#);
    commit;

     
  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      dbms_output.put_line('Course does not exist');
    WHEN stu_already_enrolled THEN
      dbms_output.put_line('Student has already been enrolled in this course');
    WHEN time_conflict THEN
      dbms_output.put_line('One of the student''s other courses has a time conflict with this course, not enrolling');
    WHEN null_value THEN
      dbms_output.put_line('Either the s# or c# are NULL, cannot enroll student into this course');
  END;


END STUDENTS;
/

