#include <stdlib.h>
#include <stdio.h>

#include "main.h"

struct cel* initCell (struct coord coord) {		//structura necesara pentru crearea de liste dublu inlantuite

	struct cel* cel = malloc(sizeof(struct cel));
	cel->prev = NULL;
	cel->next = NULL;
	cel->coord = coord;
	return cel;
}

void enque(struct snake* snek, struct coord coord) {		//adaugarea unei celule noi in lista (un nou segment la coada sarpelui)

	struct cel* aux = initCell(coord);
	snek->tail->next = aux;
	aux->prev = snek->tail;
	snek->tail = aux;
	aux->next = NULL;
}

void initSnake (struct snake* snek) {						//introduce capul sarpelui ca primul element al listei

	snek->noggin = initCell(snek->head);
	snek->tail = snek->noggin;
}


struct coord moveSnake (struct snake* snek, int num_lines, int num_cols) {		//in functie de directie, coada si capul sarpelui se modifica cu  o
																				//pozitie (mai in detaliu, celula care inainte reprezenta coada devine,
	struct coord coord = snek->tail->coord;										//dupa ce se "lipeste" la vechiul cap, noul cap).
	struct coord headCoord = snek->noggin->coord;
	if (snek->noggin != snek->tail) {
		snek->noggin->prev = snek->tail;
		snek->tail->next = snek->noggin;
		snek->noggin = snek->tail;
		snek->tail->prev->next = NULL;
		snek->tail = snek->tail->prev;
		snek->noggin->prev = NULL;
	}
	snek->noggin->coord = headCoord;

	switch(snek->direction) {
		case 'S':
			if (snek->noggin->coord.line == num_lines){
				snek->noggin->coord.line = 0;
			}
			else {
				snek->noggin->coord.line++;
			}
			break;
		case 'N':
			if (snek->noggin->coord.line == 0){
				snek->noggin->coord.line = num_lines;
			}
			else {
				snek->noggin->coord.line--;
			}
			break;
		case 'E':
			if (snek->noggin->coord.col == num_cols){
				snek->noggin->coord.col = 0;
			}
			else {
				snek->noggin->coord.col++;
			}
			break;
		case 'V':
			if (snek->noggin->coord.col == 0){
				snek->noggin->coord.col = num_cols;
			}
			else {
				snek->noggin->coord.col--;
			}
			break;
		default :
			break;
	}
	snek->head = snek->noggin->coord;
	return coord;
}

void buildSnake(struct snake* snek, int **world, int num_lines, int num_cols) {
	struct coord crt = snek->head;
	int checkN = 1, checkW = 1, checkE = 1, checkS = 1;

	switch (snek->direction) {
		case 'N':
				checkN = 0;
				break;
		case 'S':
				checkS = 0;
				break;
		case 'E':
				checkE = 0;
				break;
		case 'V':
				checkW = 0;
				break;
		default :
				break;
	}

	while (1) {		//cat timp exista celula cu encodingul specific, sarpele creste, se tine cont de faptul ca odata verificata o directie,
						//cea opusa nu va fi verificata la urmatoarea iteratie
		if(checkN == 1) {
			if (crt.line == 0) {
			 	if(world[num_lines][crt.col] == snek->encoding) {
			 		crt.line = num_lines;printf("%d",snek->encoding);
						enque(snek, crt);
							checkS = 0;
							checkE = 1;
							checkW = 1;
				}
				else {
					checkN = 0;
				}
			}
			else if (world[crt.line - 1][crt.col] == snek->encoding){
				crt.line--;
				enque(snek, crt);
				checkS = 0;
				checkE = 1;
				checkW = 1;
			}
			else {
				checkN = 0;
			}
		}
		else if(checkS == 1) {
			if (crt.line == num_lines) {
				if(world[0][crt.col] == snek->encoding) {
					crt.line = 0;
					enque(snek, crt);
					checkE = 1;
					checkW = 1;
					checkN = 0;
				}
				else {
					checkS = 0;
				}
			}
			else if (world[crt.line + 1][crt.col] == snek->encoding){
				crt.line++;
				enque(snek, crt);
				checkE = 1;
				checkW = 1;
				checkN = 0;
			}
			else {
				checkS = 0;
			}
		}
		else if(checkE == 1) {
			if (crt.col == num_cols) {
				if(world[crt.line][0] == snek->encoding) {
						crt.col = 0;
						enque(snek, crt);
						checkW = 0;
						checkN = 1;
						checkS = 1;
				}
				else {
					checkE = 0;
				}
			}
			else if (world[crt.line][crt.col + 1] == snek->encoding) {
				crt.col++;
				enque(snek, crt);
				checkW = 0;
				checkN = 1;
				checkS = 1;
			}
			else {
				checkE = 0;
			}
		}
		else if(checkW == 1) {
			if (crt.col == 0) {
				if(world[crt.line][num_cols] == snek->encoding) {
					crt.col = num_cols;
					enque(snek, crt);
					checkE = 0;
					checkS = 1;
					checkN = 1;
				}
				else {
					checkW = 0;
				}
			}
			else if (world[crt.line][crt.col - 1] == snek->encoding) {
				crt.col--;
				enque(snek, crt);
				checkE = 0;
				checkN = 1;
				checkS = 1;
			}
			else {
				checkW = 0;
			}
		}
		else {
			break;
		}
	}
}

void updateWorld (int** world, struct snake* snek, struct coord* coord, int num_snakes) {

	int i;
	#pragma omp parallel for
	for (i = 0; i < num_snakes; i++) {
		world[snek[i].head.line][snek[i].head.col] = snek[i].encoding;
		world[coord[i].line][coord[i].col] = 0;
	}
}

int collisions (int** world, struct snake* snakes, struct coord* coords, int num_snakes) {

	int i, j;
	int size = 0;
	#pragma omp parallel for
	for(i = 0; i < num_snakes; i++) {		//"taiem" toate cozile serpilor din reprezentarea matriceala
		world[coords[i].line][coords[i].col] = 0;
	}

	for(i = 0; i < num_snakes; i++) {	//ce verifica daca pozitia capului sarpelui curent corespunde cu o valoare diferita de '0' in "lumea"
											//de la pasul precedent. Daca la pozitie se afla valoarea '0', sarpele se muta.
		if (world[snakes[i].head.line][snakes[i].head.col] != 0 ) {
			#pragma omp parallel for
			for(j = 0; j < i; j ++) {	//in caz de coliziune, serpii mutati anterior trebuie sa faca un pas in spate (aici doar capul)
				world[snakes[j].head.line][snakes[j].head.col] = 0;
			}
			#pragma omp parallel for
			for(j = 0; j < num_snakes; j++) {	//se restaureaza cozile in caz de coliziune
				world[coords[j].line][coords[j].col] = snakes[j].encoding;
			}
			return 1;
		}
		else {	//se muta sarpele daca nu se gaseste o coliziune (la pozitia capului sarpelui curent din matrice se seteaza valoarea encodingului)
			world[snakes[i].head.line][snakes[i].head.col] = snakes[i].encoding;
		}
	}

	return 0;

}

void run_simulation(int num_lines, int num_cols, int **world, int num_snakes,
	struct snake *snakes, int step_count, char *file_name)
{

	int i;
	int j;
	for (i = 0; i < num_snakes; i ++){
		initSnake(&snakes[i]);
		buildSnake(&snakes[i], world, num_lines - 1, num_cols - 1);
	}

	struct coord* coord = malloc(num_snakes* sizeof (struct coord));	//retin aici pozitii cozilor inainte ca fiecare sarpe sa faca un pas
	for (i = 0; i < step_count; i++) {
		#pragma omp parallel for
		for (j = 0; j < num_snakes; j++) {
			coord[j] = moveSnake(&snakes[j], num_lines - 1, num_cols - 1);		//fiecare sarpe face un pas
		}
		if(collisions(world, snakes, coord, num_snakes) == 0){			//daca nu se produc coliziuni,
			updateWorld(world, snakes, coord, num_snakes);				 //"lumea" se modifica in concordanta cu pozitiile noi ale serpilor
		}
		else {
			#pragma omp parallel for
			for(j = 0; j < num_snakes; j++) {
				snakes[j].head = snakes[j].noggin->next->coord;			//in caz in care s-a detectat o coliziune, lumea ramane nemodificata iar
			}															 //coordonatele capetelor serpilor devin cele de la pasul precedent
			break;
		}
	}
}