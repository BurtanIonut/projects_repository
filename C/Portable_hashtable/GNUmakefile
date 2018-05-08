build: tema1.o my_lib.o
	gcc -Wall tema1.o my_lib.o -o tema1 -lhash -L.

tema1.o: tema1.c my_lib.c
	gcc -Wall -c my_lib.c
	gcc -Wall -c tema1.c

clean:
	rm tema1 *.o
