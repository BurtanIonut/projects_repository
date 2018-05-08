#define _CRT_SECURE_NO_DEPRECATE

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

#ifndef HASH_H_
#define HASH_H_

unsigned int hash(const char *str, unsigned int hash_length);

#endif

/*Structura ce retine un cuvant,*/
/*unitatea pentru bucket-uri*/
typedef struct Cell {
	struct Cell *urm;
	char *val;
} Cell;

/*Structura ce retine bucket-urile*/
/*si numarul lor*/
typedef struct My_hash {
	Cell **hash;
	unsigned int dim;
} My_hash;

/*Aloca memorie pentru o noua celua,*/
/*intoarce pointer la structura*/
Cell *new_cell(char *val);

/*Aloca memorie pentru un nou hash,*/
/*intoarce pointer la structura*/
My_hash *new_hash(unsigned int dim);

/*Cauta cuvantul in hash*/
int find_word(char *cuv, Cell **my_hash, unsigned int dim);

/*Deschide fisierul, trateaza erorile*/
FILE *safe_open(char *file, char *mode);

/*Citeste din fisier, trateaza erorile*/
size_t safe_read(void *ptr, size_t size, size_t nmemb, FILE *stream);

/*Scrie la stderr, inchide procesul*/
void *eroare(char *unde, char *misc);

/*Aloca memorie, trateaza erorile*/
void *safe_malloc(size_t size);

/*Executa toate comenzile din fisier*/
void execute(FILE *p, My_hash *hash);

/*Adauga un cuvant nou in hash*/
void add(char *cuv, Cell **hash, unsigned int dim);

/*Adauga o noua celula intr-un bucket*/
void add_cell(Cell **hash, unsigned int dim, Cell *cell);

/*Scrie in fisier continutul hash-ului*/
void print(Cell **hash, unsigned int dim, char *file);

/*Cauta cuvantul in hash, scrite True/False in*/
/*fisier, daca l-a gasit sau nu*/
void find(Cell **hash, unsigned int dim, char *cuv, char *file);

/*Sterge continutul bucket-urilor*/
void clear(My_hash *hash);

/*Sterge celula ce contine cuvantul, din hash*/
void my_remove(Cell **my_hash, unsigned int dim, char *cuv);

/*Redistribuie cuvintele in bucket-urile noi si*/
/*elibereaza memoria celor vechi*/
void resize(My_hash *hash, char *size);

/*Printeaza continutul bucket-ului in fisier*/
void print_bucket(Cell **hash, unsigned int dim, int index, char *file);
