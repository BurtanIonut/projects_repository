#include <stdlib.h>
#include <stdio.h>

//Structura interval, are un camp pentru marginea din dreapta si unul pentru cea din stanga
typedef struct {
	int l;
	int r;
} interval;

//Functia de comparare a 2 intervale folosita de 'qsort'
int comp (const void* i1, const void* i2) {

	//Daca marginile din stanga sunt egale se compara dupa marginile din dreapta
	if (((*(interval*)i1).l - (*(interval*)i2).l) == 0) {
		return  ((*(interval*)i1).r - (*(interval*)i2).r);
	}
	return  ((*(interval*)i1).l - (*(interval*)i2).l);
}

//Functia itereaza prin multimea punctelor ce trebuie incluse in intervale si intoarce
  //urmatorul numar de la care se reia procesul de incluziune
//Argumentul 'crl' este limita curenta (pana la care numerele mai mici au fost deja incluse).
  //se cauta primul numar mai mare ca 'crl' si se intoarce respectivul numar din multime.
int getNewLim (int* nr, int crl, int* prevl) {

	int i = 0;
	while (crl > nr[*prevl + i]) {
		i++;
	}
	*prevl = *prevl + i;
	return nr[*prevl];
}

int main (int argc, char** argv) {

	int M = 0;			//Nr intervale
	int N = 0;			//Nr numere
	int cont = 0;		//Nr de intervale folosite in solutie
	int i;
	int lim = 0;		//Limita curenta
	int liml = 0;		//Limita stanga
	int limr = 0;		//Limita dreapta

	interval aux;		//Interval auxiliar
	interval max;		//Interval 'maxim'

	FILE * p = fopen("points.in", "r");
	FILE * q = fopen("points.out", "w");

	fscanf(p,"%d", &N);
	fscanf(p,"%d", &M);

	int* nr = calloc(N, sizeof(int));			//Pregatesc multimea de numere pentru a stoca datele din fisier

	interval* mul = calloc(M, sizeof(interval));		//Pregatesc multimea de intervale

	for (i = 0; i < N; i++) {					//Citesc numerele
		fscanf(p,"%d", &nr[i]);
	}
	for (i = 0; i < M; i++) {					//Citesc intervalele
		fscanf(p, "%d", &mul[i].l);
		fscanf(p, "%d", &mul[i].r);
	}

	liml = nr[0];								//Limita stanga devine primul numar din multime
	limr = nr[N - 1];							//Limita dreapta devine ultimul numar din multime

	qsort(mul, M, sizeof(interval), comp);		//Sortez multimea de intervale cu functia 'comp' descrisa mai sus

	max.l = 0;									//Initializez intervalul 'maxim' cu 0 pentru ambele margini
	max.r = 0;

	for (i = 0; i < M; i++) {					//Iterez prin multimea intervalelor
		aux = mul[i];							//Intervalul auxiliar devine intervalul curent
		if (aux.l <= liml) {					//Daca limita intervalului auxiliar este <= ca cea din stanga (initial primul numar)
			if (max.r < aux.r) {					//Daca limita din dreapta a intervalului este mai mare ca cea 'maxima'
				max = aux;								//Se seteaza noul maxim
			}
		}
		else {										//Daca nu, atunci inseamna ca s-au epuizat intervalele ce contin numere pana la limita din stanga
			liml = getNewLim(nr, max.r, &lim);		  //inclusiv si se cauta o noua limita, se incrementeaza cu 1 si numarul de intervale din solutie
			max = aux;
			cont++;
		}
		if(aux.r >= limr) {						//Daca limita din dreapta a intervalului curent este mai mare ca ultimul numar din multime se incheie cautarea,
			cont++;								  //inseamna ca toate numere au fost incluse in intervale. Se incrementeaza numarul de intervale din solutie.
			break;
		}
	}
	fprintf(q,"%d", cont);						//Se scrie numarul de intervale in fisier
												//Se elibereaza memoria si se inchid fisierele
	free(nr);
	fclose(p);
	fclose(q);

	return 0;
}
