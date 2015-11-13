/* PART 4:
 * This program prints out all students and their information. 
 * Under each student, a list of courses (if any) that they have enrolled are displayed. 
 *
 * NOTE: the script -a command outputs the execution of this program inaccurately,
 * when running the program through sqlplus, it should have proper format
 */
DECLARE
  stu_tuple STUDENT%rowtype;
  crs_tuple COURSE%rowtype;
  
  CURSOR all_stus IS select * from student ORDER BY s# ASC;
  CURSOR enrolled_crs(stuNo STUDENT.s#%type)
  IS  select c.* 
      FROM course c, grade g
      WHERE c.c# = g.c#
      AND   g.s# = stuNo
      ORDER BY c.c# ASC;
BEGIN
  dbms_output.put_line('  STUDENTS                                                                                                                   ');
  dbms_output.put_line('+----------+--------------------------------------------------+----------+--------------------------------------------------+');
  dbms_output.put_line('|   S#     |                    NAME                          |BIRTHDATE |                 ADDRESS                          |');
  dbms_output.put_line('+==========+==================================================+==========+==================================================+');
  
  -- Print each student (and their enrolled courses)
  OPEN all_stus;
  LOOP
    fetch all_stus into stu_tuple;
    exit when all_stus%NOTFOUND;
  
    dbms_output.put_line('|' || lpad(to_char(stu_tuple.s#),10) ||
    '|' || rpad(stu_tuple.name,50) ||
    '|' || NVL(rpad(stu_tuple.birthdate,10),rpad(' ',10))  ||
    '|' || rpad(NVL(stu_tuple.address, ' '),50) ||
    '|');

    dbms_output.put_line('+----------+--------------------------------------------------+----------+--------------------------------------------------+');  
    dbms_output.put_line('|                                                                                                                           |');
    dbms_output.put_line('|     ENROLLED COURSES                                                                                                      |');
    dbms_output.put_line('|     +----------+--------------------+------------------------------+-----+--------------------+                           |');
    dbms_output.put_line('|     |   C#     |      NAME          |           LOCATION           | DAY |    TIMES           |                           |');
    dbms_output.put_line('|     +==========+====================+==============================+=====+====================+                           |');
    
    --Print all enrolled courses for this student
    OPEN enrolled_crs(stu_tuple.s#);
    LOOP
      FETCH enrolled_crs into crs_tuple;
      exit when enrolled_crs%NOTFOUND;
      
      dbms_output.put_line('|     |' || lpad(to_char(crs_tuple.c#),10) ||
        '|' || rpad(crs_tuple.name,20) ||
        '|' || rpad(crs_tuple.location,30) ||
        '|' || rpad(crs_tuple.days,5) ||
        '|' || rpad(crs_tuple.times,20) ||
        '|' || '                           |' );
      
      dbms_output.put_line('|     +----------+--------------------+------------------------------+-----+--------------------+                           |');           
        
    END LOOP;
    CLOSE enrolled_crs;
    
    dbms_output.put_line('|                                                                                                                           |');
    dbms_output.put_line('+----------+--------------------------------------------------+----------+--------------------------------------------------+');  
  END LOOP;

  CLOSE all_stus;

END;
/
