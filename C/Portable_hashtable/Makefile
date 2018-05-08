build: tema1.obj my_lib.obj
	cl /W3 tema1.obj my_lib.obj hash.lib

tema1.obj: tema1.c my_lib.c
	cl /W3 /c my_lib.c
	cl /W3 /c tema1.c

clean:
	del *.obj tema1.exe
