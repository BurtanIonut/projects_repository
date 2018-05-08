#include "my_lib.h"

My_hash *new_hash(unsigned int dim)
{
	My_hash *hash;

	hash = safe_malloc(sizeof(My_hash));
	hash->hash = calloc(sizeof(Cell *) * dim, 1);
	hash->dim = dim;
	return hash;
}

void print_bucket(Cell **hash, unsigned int dim, int index, char *file)
{
	FILE *p;
	Cell *cell = hash[index];

	if (index < 0 || (unsigned int) index > dim)
		eroare("INDEX INVALID", "");
	if (strcmp(file, "") == 0)
		p = stdout;
	else
		p = safe_open(file, "a");
	while (cell != NULL) {
		fprintf(p, "%s", cell->val);
		cell = cell->urm;
		if (cell == NULL)
			fprintf(p, "\n");
		else
			fprintf(p, " ");
	}
	if (p != stdout)
		fclose(p);
}

void resize(My_hash *hash, char *size)
{
	Cell **new_hash;
	Cell **old_hash = hash->hash;
	Cell *cell;
	Cell *crt;
	Cell *next;
	int i;
	unsigned int new_dim = 0;
	unsigned int dim = hash->dim;

	if (strcmp(size, "double") == 0) {
		new_dim = dim * 2;
	} else if (strcmp(size, "halve") == 0) {
		new_dim = dim / 2;
	} else {
		eroare("DIMENSIUNEA NU ESTE VALIDA", "");
		return;
	}
	/*Aloc memorie pentru noua lista*/
	/*de bucket-uri si introduc cuvintele*/
	new_hash = calloc(sizeof(Cell *) * new_dim, 1);
	for (i = 0; (unsigned int) i < dim; i++) {
		cell = old_hash[i];
		while (cell != NULL) {
			add(cell->val, new_hash, new_dim);
			cell = cell->urm;
		}
	}
	/*Eliberez memoria pentru bucket-urile*/
	/*vechi, dupa ce le schimb in hash*/
	hash->hash = new_hash;
	hash->dim = new_dim;
	for (i = 0; (unsigned int) i < dim; i++) {
		crt = old_hash[i];
		while (crt != NULL) {
			next = crt->urm;
			free(crt->val);
			crt->urm = NULL;
			free(crt);
			crt = next;
		}
		old_hash[i] = NULL;
	}
	free(old_hash);
}

void my_remove(Cell **my_hash, unsigned int dim, char *cuv)
{
	int index;
	Cell *crt;
	Cell *prev;

	if (find_word(cuv, my_hash, dim) == 1) {
		index = hash(cuv, dim);
		crt = my_hash[index];
		prev = crt;
		if (strcmp(crt->val, cuv) == 0) {
			my_hash[index] = crt->urm;
			free(crt->val);
			free(crt);
		} else {
			while (strcmp(crt->val, cuv) != 0) {
				prev = crt;
				crt = crt->urm;
			}
			prev->urm = prev->urm->urm;
			free(crt->val);
			free(crt);
		}
	}
}

void clear(My_hash *hash)
{
	Cell *crt;
	Cell *next;
	unsigned int dim = hash->dim;
	int i;

	/*Iterez prin toate bucket-urile,*/
	/*eliberez memoria celulelor*/
	/*toate vor pointa la NULL apoi*/
	for (i = 0; (unsigned int) i < dim; i++) {
		crt = hash->hash[i];
		while (crt != NULL) {
			next = crt->urm;
			free(crt->val);
			crt->urm = NULL;
			free(crt);
			crt = next;
		}
		hash->hash[i] = NULL;
	}
}

void find(Cell **hash, unsigned int dim, char *cuv, char *file)
{
	FILE *p;

	if (strcmp(file, "") == 0)
		p = stdout;
	else
		p = safe_open(file, "a");
	if (find_word(cuv, hash, dim) == 1)
		fprintf(p, "True\n");
	else
		fprintf(p, "False\n");
	if (p != stdout)
		fclose(p);
}

void print(Cell **hash, unsigned int dim, char *file)
{
	FILE *p;
	int i;
	Cell *cell;

	/*Daca al 2-lea argument este sirul vid*/
	/*iesirea print-ului va fi stdout*/
	if (strcmp(file, "") == 0)
		p = stdout;
	else
		p = safe_open(file, "a");
	for (i = 0; (unsigned int) i < dim; i++) {
		cell = hash[i];
		while (cell != NULL) {
			fprintf(p, "%s", cell->val);
			cell = cell->urm;
			if (cell == NULL)
				fprintf(p, "\n");
			else
				fprintf(p, " ");
		}
	}
	if (p != stdout)
		fclose(p);
}

void add_cell(Cell **my_hash, unsigned int dim, Cell *cell)
{
	unsigned int index = hash(cell->val, dim);
	Cell *prev;
	Cell *crt;

	if (my_hash[index] == NULL) {
		my_hash[index] = cell;
		return;
	}
	prev = my_hash[index];
	crt = prev->urm;
	while (crt != NULL) {
		prev = crt;
		crt = crt->urm;
	}
	prev->urm = cell;
}

int find_word(char *cuv, Cell **my_hash, unsigned int dim)
{
	Cell *cell;
	int index = (int) hash(cuv, dim);

	cell = my_hash[index];

	while (cell != NULL) {
		if (strcmp(cuv, cell->val) == 0)
			return 1;
		cell = cell->urm;
	}
	return 0;
}

Cell *new_cell(char *val)
{
	Cell *cell = safe_malloc(sizeof(Cell));

	cell->urm = NULL;
	cell->val = safe_malloc(strlen(val) + 1);
	strcpy(cell->val, val);
	cell->val[strlen(val)] = '\0';
	return cell;
}

void add(char *cuv, Cell **hash, unsigned int dim)
{
	Cell *cell;

	if (find_word(cuv, hash, dim) == 0) {
		cell = new_cell(cuv);
		add_cell(hash, dim, cell);
	}
}

void execute(FILE *p, My_hash *my_hash)
{
	int i;
	unsigned int j;
	Cell **hash = my_hash->hash;
	unsigned int dim = my_hash->dim;
	char *buf = safe_malloc(sizeof(char) * 20000);
	char *token;
	char **command = safe_malloc(2 * sizeof(char *));
	int conv_val;

	/*Citesc mereu cate o linie din fisier, dupa care*/
	/*salvez maxim primele 2 cuvinte din comanda*/
	/*daca al 2-lea cuvant este NULL, verific daca exista o*/
	/*comanda formata din cuvantul nenul, daca*/
	/*ambele cuvinte nu sunt NULL si token-ul pointeaza*/
	/*la NULL, atunci verific daca exista o comanda*/
	/*formata din cele 2 cuvinte nenule, daca token nu este NULL*/
	/*atunci verific daca exista o comanda formata din cele*/
	/*3 cuvinte. Execut fiecare comanda valida si tratez*/
	/*comenzile invalide*/
	while (fgets(buf, 20000, p) != NULL) {
		token = strtok(buf, "\n ");
		for (i = 0; token != NULL && i < 2; i++) {
			command[i] = token;
			token = strtok(NULL, "\n ");
		}
		if (token == NULL && i == 2) {
			if (strcmp(command[0], "add") == 0) {
				add(command[1], hash, dim);
			} else if (strcmp(command[0], "remove") == 0) {
				my_remove(hash, dim, command[1]);
			} else if (strcmp(command[0], "resize") == 0) {
				resize(my_hash, command[1]);
				dim = my_hash->dim;
				hash = my_hash->hash;
			} else if (strcmp(command[0], "find") == 0) {
				find(hash, dim, command[1], "");
			} else if (strcmp(command[0], "print") == 0) {
				print(hash, dim, command[1]);
			} else if (strcmp(command[0], "print_bucket") == 0) {
				for (j = 0; j < strlen(command[1]); j++) {
					if (!isdigit(command[1][j]))
						eroare("INDEXUL INVALID", "");
				}
				print_bucket(hash, dim, atoi(command[1]), "");
			} else if (strcmp("\n", command[0]) != 0) {
				eroare("COMANDA INVALIDA", "");
			}
		} else if (token == NULL && i == 1) {
			if (strcmp(command[0], "clear") == 0) {
				clear(my_hash);
				hash = my_hash->hash;
			} else if (strcmp(command[0], "print") == 0) {
				print(hash, dim, "");
			} else if (strcmp("\n", command[0]) != 0) {
				eroare("COMANDA INVALIDA", "");
			}
		} else {
			if (strcmp(command[0], "find") == 0) {
				find(hash, dim, command[1], token);
			} else if (strcmp(command[0], "print_bucket") == 0) {
				for (j = 0; j < strlen(command[1]); j++) {
					if (!isdigit(command[1][j]))
						eroare("INDEXUL INVALID", "");
				}
				conv_val = atoi(command[1]);
				print_bucket(hash, dim, conv_val, token);
			} else if (strcmp("\n", command[0]) != 0) {
				eroare("COMANDA INVALIDA", command[0]);
			}
		}
	}
	free(buf);
	free(command);
}

void *safe_malloc(size_t size)
{
	void *to_alloc = malloc(size);

	if (to_alloc == NULL)
		eroare("La alocare", "");
	return to_alloc;
}

void *eroare(char *unde, char *misc)
{
	fprintf(stderr, "EROARE: %s, DETALII: %s\n", unde, misc);
	exit(-1);
}

size_t safe_read(void *ptr, size_t size, size_t nmemb, FILE *stream)
{
	int val = fread(ptr, size, nmemb, stream);

	if (val < 0) {
		fclose(stream);
		eroare("Citire din fisier", "");
	}
	return (size_t) val;
}


FILE *safe_open(char *file, char *mode)
{
	FILE *p = fopen(file, mode);

	if (p == NULL)
		eroare("Fisier inexistent", file);
	return p;
}
