/* PACKAGE SPEC for COURSES 
   DESC: Includes all procedures relating to courses
 */
CREATE OR REPLACE PACKAGE COURSES IS --spec

  /*PROCEDURE_NAME: add_course
  DESC: Adds a course to the course table, given a c# and a name
  @param v_c#: course number (INTEGER)
  @param v_name: course name (Varchar2)
   */
  PROCEDURE add_course(v_c# IN COURSE.c#%type,
                       v_name IN COURSE.name%type);

  /*PROCEDURE_NAME: add_course
  DESC: Adds a course to the course table, given a c# and a name, loc, days, and time
  @param v_c#: course number (INTEGER)
  @param v_name: course name (Varchar2)
  @param v_loc: location of the course (VARCHAR2)
  @param v_days: day of the course (VARCHAR2)
  @param v_time: time of the course (VARCHAR2)
   */
  PROCEDURE add_course(v_c#  COURSE.c#%type,
                       v_name  COURSE.name%type,
                       v_loc  COURSE.location%type,
                       v_days COURSE.days%type,
                       v_time COURSE.times%type);

  /*PROCEDURE_NAME: delete_course 
  DESC: delete a course from the course table, given the course name, provided there are no students in the course 
  @param v_name: course name (Varchar2)
   */
  PROCEDURE delete_course(v_name COURSE.name%type);

  /*PROCEDURE_NAME: change_loc 
  DESC: Change a course's location, provided it doesn't conflict with the time/location of the day 
  @param v_c#: course number (INTEGER)
  @param v_loc: new location of the course (Varchar2)
   */
  PROCEDURE change_loc(v_c# COURSE.c#%type,
                       v_loc COURSE.location%type);

  /*PROCEDURE_NAME: give_mark 
  DESC: Assign a mark to the student enrolled in the course 
  @param v_s#: student number (INTEGER)
  @param v_c#: course number (INTEGER)
  @param v_mark: given mark (FLOAT)
   */
  PROCEDURE give_mark(v_s# STUDENT.s#%type,
                      v_c# COURSE.c#%type,
                      v_mark GRADE.mark%type);
  


END COURSES;
/

/* PACKAGE SPEC BODY for COURSES 
   DESC: Includes all procedures relating to courses
 */
CREATE OR REPLACE PACKAGE BODY COURSES IS -- body

  /*PROCEDURE_NAME: add_course
  DESC: Adds a course to the course table, given a c# and a name
  @param v_c#: course number (INTEGER)
  @param v_name: course name (Varchar2)
   */
  PROCEDURE add_course(v_c# COURSE.c#%type,
                       v_name COURSE.name%type)
  IS
  BEGIN
    add_course(v_c#,v_name,NULL,NULL,NULL);  
  END;

  /*PROCEDURE_NAME: add_course
  DESC: Adds a course to the course table, given a c# and a name, loc, days, and time
  @param v_c#: course number (INTEGER)
  @param v_name: course name (Varchar2)
  @param v_loc: location of the course (VARCHAR2)
  @param v_days: day of the course (VARCHAR2)
  @param v_time: time of the course (VARCHAR2)
   */
  PROCEDURE add_course(v_c# COURSE.c#%type,
                       v_name COURSE.name%type,
                       v_loc COURSE.location%type,
                       v_days COURSE.days%type,
                       v_time COURSE.times%type)
  IS
    c#_exists               EXCEPTION;
    name_exists             EXCEPTION;
    time_location_conflict  EXCEPTION;
    v_count                 INTEGER;
    null_value              EXCEPTION;
    PRAGMA EXCEPTION_INIT(null_value,-1400);
    invalid_days_times      EXCEPTION;
    PRAGMA EXCEPTION_INIT(invalid_days_times,-2290);
   
  BEGIN 
    --Check if c# argument already exists in database 
    select count(*) into v_count 
    FROM course
    WHERE c# = v_c#;

    if v_count > 0 then
      raise c#_exists;
    end if;

    --check if name argument already exists in database
    select count(*) into v_count
    FROM course
    WHERE name = v_name;

    if v_count > 0 then
      raise name_exists;
    end if;

    --check for time and location conflict with other courses
    select count(*) into v_count
    FROM course
    WHERE location = v_loc
    AND   days     = v_days
    AND   times    = v_time;

    if v_count > 0 then
     raise time_location_conflict;
    end if;

    insert into course values(v_c#, v_name, v_loc, v_days, v_time);
    
    dbms_output.put_line('Course#' || v_c# || ' ' || v_name || ' has been added at location ' || v_loc || ' on ' || v_days || ' at ' || v_time);

    commit;

  EXCEPTION
    when c#_exists then
      dbms_output.put_line('The c# given already exists in the database, no new course was added');
    when name_exists then
      dbms_output.put_line('The name ' || v_name || ' already exists in the database, no new course was added');  
    when time_location_conflict then
      dbms_output.put_line('There is a time conflict at this location. Another course has already taken this classroom at this time of day');
    when null_value then
      dbms_output.put_line('Null values for c# or name was given, not creating course');
    when invalid_days_times then
      dbms_output.put_line('Days ' || v_days || ' or Times ' || v_time || ' are invalid, course was not added');

  END;

  /*PROCEDURE_NAME: delete_course 
  DESC: delete a course from the course table, given the course name, provided there are no students in the course 
  @param v_name: course name (Varchar2)
   */
  PROCEDURE delete_course(v_name COURSE.name%type)
  IS
    v_count           INTEGER;
    has_stu           EXCEPTION;
    course_not_found  EXCEPTION;
  BEGIN

    --Check if there are any students enrolled in the course
    select count(*) into v_count
    FROM course c, grade g
    WHERE c.name = v_name
    AND   c.c# = g.c#;
    
    if v_count > 0 then
      raise has_stu;
    end if;

    --Check if there is any course in the table to actually delete
    select count(*) into v_count
    FROM course
    WHERE name = v_name;

    if v_count > 0 then
      delete from course c where c.name = v_name;
      dbms_output.put_line('Course ' || v_name || ' was deleted');
    else 
      raise course_not_found;
    end if;

    commit;
  
  EXCEPTION
    when has_stu then
      dbms_output.put_line('Course ' || v_name || ' has students enrolled, cannot delete');
    when course_not_found then
      dbms_output.put_line('Course ' || v_name || ' is not in the table, no deletion took place');

  END;

  /*PROCEDURE_NAME: change_loc 
  DESC: Change a course's location, provided it doesn't conflict with the time/location of the day 
  @param v_c#: course number (INTEGER)
  @param v_loc: new location of the course (Varchar2)
   */
  PROCEDURE change_loc(v_c# COURSE.c#%type,
                       v_loc COURSE.location%type)
  IS
    v_count   INTEGER;
    v_days    COURSE.days%type;
    v_times   COURSE.times%type;
    conflict  EXCEPTION;
  BEGIN

    --Get the days and times for the course in question
    select days, times into v_days, v_times
    FROM course
    WHERE c# = v_c#;

     
    --Check if new location would conflict with any other courses
    select count(*) into v_count
    FROM course
    WHERE c# != v_c#
    AND   location = v_loc
    AND   days     = v_days
    AND   times    = v_times;
    
    if v_count > 0 then
      raise conflict;
    end if;

    update course set location = v_loc where c# = v_c#; 

    dbms_output.put_line('Course# ' || v_c# || ' location changed to ' || v_loc);
    commit;

  EXCEPTION 
    WHEN conflict THEN
      dbms_output.put_line('Changing location to ' || v_loc || ' for course# ' || v_c# || ' would cause a conflict with another course, not changing location');
    WHEN NO_DATA_FOUND THEN
      dbms_output.put_line('the course# given ' || v_c# || ' does not exist in the database');

  END;

  /*PROCEDURE_NAME: give_mark 
  DESC: Assign a mark to the student enrolled in the course 
  @param v_s#: student number (INTEGER)
  @param v_c#: course number (INTEGER)
  @param v_mark: given mark (FLOAT)
   */
  PROCEDURE give_mark(v_s# STUDENT.s#%type,
                      v_c# COURSE.c#%type,
                      v_mark GRADE.mark%type)
  IS
    v_count       INTEGER;
    invalid_mark  EXCEPTION;
    PRAGMA EXCEPTION_INIT(invalid_mark,-2290);
  BEGIN

    --Check if given student is actually enrolled in this course
    select 1 into v_count
    FROM grade
    WHERE s# = v_s# 
    AND   c# = v_c#;
    
    --Set mark  
    update grade 
    set mark = v_mark 
    where s# = v_s# 
    and   c# = v_c#;
    
    dbms_output.put_line('Student# ' || v_s# || ' was given a mark of ' || v_mark || ' at Course# ' || v_c#);

    commit;

  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      dbms_output.put_line('Given student and course enrollment was not found, can''t assign a mark');
    WHEN invalid_mark THEN
      dbms_output.put_line('Given mark must be between 0 and 100, or blank (NULL)');

  END;
  



END COURSES;
/
