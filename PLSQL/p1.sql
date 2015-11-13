
/* Part 1 : This file contains a PL/SQL program to create each of the tables using static SQL's Execute immediate command
 *
 */

DECLARE
  studentTable varchar2(250) := 'create table student(s# integer not null,name varchar2(50) NOT NULL UNIQUE, birthdate date, address varchar2(50), PRIMARY KEY(s#))';

  courseTable varchar2(500) := 'create table course(c# integer not null,name varchar2(50) NOT NULL UNIQUE,location varchar2(50),days varchar2(3) check (days in (''MWF'',''TR'')),
                                    times varchar2(15) check (times in (''8:35-9:25'',''10:35-11:25'',''1:35-2:25'',''3:35-4:25'')),
                                    PRIMARY KEY (c#))';

  gradeTable  varchar2(500) := 'create table grade(s# integer not null,c# integer not null,mark float check (mark  >= 0.0 and mark <= 100.),
                                    PRIMARY KEY (s#,c#),
                                    FOREIGN KEY (s#) references student on delete cascade,
                                    FOREIGN KEY (c#) references course )';

BEGIN
  execute immediate studentTable;
  execute immediate courseTable;
  execute immediate gradeTable;
END;
/

