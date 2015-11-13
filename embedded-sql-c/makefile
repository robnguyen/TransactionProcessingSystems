
ICLNT=-I/usr/include/oracle/11.2/client64
LCLNT=-L/usr/lib/oracle/11.2/client64/lib -l clntsh

p1: part1.o
	cc -o p1 part1.o $(LCLNT)

part1.o: part1.c
	cc -c part1.c $(ICLNT)

part1.c: part1.pc
	proc part1.pc
	
p2: part2.o
	cc -o p2 part2.o $(LCLNT)

part2.o: part2.c
	cc -c part2.c $(ICLNT)

part2.c: part2.pc
	proc part2.pc

p3: part3.o
	cc -o p3 part3.o $(LCLNT)

part3.o: part3.c
	cc -c part3.c $(ICLNT)

part3.c: part3.pc
	proc part3.pc

p4: part4.o
	cc -o p4 part4.o $(LCLNT)

part4.o: part4.c
	cc -c part4.c $(ICLNT)

part4.c: part4.pc
	proc part4.pc

drop: drop.o
	cc -o drop drop.o $(LCLNT)

drop.o: drop.c
	cc -c drop.c $(ICLNT)

drop.c: drop.pc
	proc drop.pc
rm:
	rm *.o *.c drop p1 p2 p3 p4
