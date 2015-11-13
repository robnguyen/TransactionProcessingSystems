/* PART 5: Displaying each table using one cursor variable (and case expressions/statements)
 *
 * NOTE: the script -a command outputs the execution of this program inaccurately,
 * when running the program through sqlplus, it should have proper format
 *
 */

DECLARE
  type scgRef is ref cursor;
  cursorv     scgRef;
  flag        varchar2(10) := 'student';
  stu_tuple   STUDENT%ROWTYPE;
  crs_tuple   COURSE%ROWTYPE;
  gra_tuple   GRADE%ROWTYPE;

BEGIN


  LOOP
    CASE flag
    --Student case with cursor variable 
    WHEN 'student' THEN
      open cursorv for select * from student;
      DBMS_OUTPUT.PUT(CHR(10));
      DBMS_OUTPUT.PUT_LINE('STUDENTS');
      DBMS_OUTPUT.PUT_LINE(RPAD('S#',10) || ' ' || LPAD('NAME',50) || ' ' || LPAD('BIRTHDATE',10) || ' ' || LPAD('ADDRESS',50));
      DBMS_OUTPUT.PUT_LINE('---------- -------------------------------------------------- ---------- --------------------------------------------------');    
      LOOP
        FETCH cursorv into stu_tuple;
        EXIT WHEN cursorv%notfound;
        DBMS_OUTPUT.PUT_LINE(RPAD(to_char(stu_tuple.s#),10) || 
          ' ' || LPAD(stu_tuple.name,50) ||
          ' ' || NVL(Lpad(stu_tuple.birthdate,10),Lpad(' ',10)) ||
          ' ' || LPAD(NVL(stu_tuple.address,' '),50));
      END LOOP;
      close cursorv;
      flag := 'course';
      DBMS_OUTPUT.PUT_LINE('---------------------------------------------------------------------------------------------------------------------------');    
      DBMS_OUTPUT.PUT(CHR(10));
    
    --Course print case with cursor variable
    WHEN 'course' THEN
      open cursorv for select * from course;
      DBMS_OUTPUT.PUT_LINE('COURSES');
      DBMS_OUTPUT.PUT_LINE(RPAD('C#',10) ||
        ' ' || LPAD('NAME',20) ||
        ' ' || LPAD('LOCATION',30) ||
        ' ' || LPAD('DAYS',5) ||
        ' ' || LPAD('TIMES',20));

      DBMS_OUTPUT.PUT_LINE('---------- -------------------- ------------------------------ ----- --------------------');     
      LOOP
        FETCH cursorv into crs_tuple;
        exit WHEN cursorv%notfound;
        DBMS_OUTPUT.PUT_LINE(RPAD(to_char(crs_tuple.c#),10) ||
          ' ' || LPAD(crs_tuple.name,20) ||
          ' ' || LPAD(crs_tuple.location,30) ||
          ' ' || LPAD(crs_tuple.days,5) ||
          ' ' || LPAD(crs_tuple.times,20));
      END LOOP;
      close cursorv;
      flag := 'grade';
      DBMS_OUTPUT.PUT_LINE('-----------------------------------------------------------------------------------------');     
      DBMS_OUTPUT.PUT(CHR(10));
    
    --Grade print case with cursor variable
    WHEN 'grade' THEN
      open cursorv for select * from grade;
      DBMS_OUTPUT.PUT_LINE('GRADES');
      DBMS_OUTPUT.PUT_LINE(RPAD('S#',10) ||
        ' ' || RPAD('C#',10) ||
        ' ' || RPAD('MARK', 5));
      
      DBMS_OUTPUT.PUT_LINE('---------- ---------- -----');   
      LOOP
        FETCH cursorv into gra_tuple;
        exit WHEN cursorv%notfound;
        DBMS_OUTPUT.PUT_LINE(RPAD(to_char(gra_tuple.s#),10) ||
          ' ' || RPAD(to_char(gra_tuple.c#),10) ||
          ' ' || RPAD(to_char(gra_tuple.mark),5)); 
      END LOOP;
      close cursorv;
      flag := 'exit';
      DBMS_OUTPUT.PUT_LINE('---------------------------');   
    ELSE
      EXIT;
    END CASE;

  END LOOP;


END;
/
