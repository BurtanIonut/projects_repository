#define _CRT_SECURE_NO_DEPRECATE

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include "my_lib.h"

int main(int argc, char **argv)
{
	FILE *p;
	unsigned int dim_hash;
	My_hash *hash;
	int i;

	if (argc == 1)
		eroare("Prea putine argumente", "");
	dim_hash = (unsigned int) atoi(argv[1]);
	hash = new_hash(dim_hash);
	if (argc > 2) {
		/*Iterez prin toate input-urile*/
		/*Execut pe rand comenzile din fisiere*/
		for (i = 2; i < argc; i++) {
			p = safe_open(argv[i], "r");
			execute(p, hash);
			fclose(p);
		}
	} else {
		execute(stdin, hash);
	}
	clear(hash);
	free(hash->hash);
	free(hash);
	return 0;
}
