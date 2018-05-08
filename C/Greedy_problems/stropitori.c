#include<stdlib.h>
#include<stdio.h>


int main (int argc, char** argv) {

	FILE* p = fopen("stropitori.in", "r");
	FILE* q = fopen("stropitori.out", "w");

	char* buf = calloc(100, 1);						//Buffer pentru stocat numele stadionului
	int nrs = 0;									//Numar de stropitori
	int dim = 0;									//Dimeniunea stadionului
	long long int* strop;							//Multimea pozitiilor stropitorilor
	long long int* pow;								//Multimea puterilor stropitorilor din multimea precedenta
	int i;
	int cont = 0;									//Numar ce reprezinta solutia
	int offset = 0;									//Un offset utilizat ulterior pentru a asigura ca 2 stropitori nu stropesc
													  //in acelasi loc

	fgets(buf, 100, p);								//Se citeste numele stadionului
	fscanf(p, "%d", &nrs);							//Se citeste numarul de stropitori
	fscanf(p, "%d", &dim);							//Se citeste dimensiunea stadionului
	strop = calloc(nrs, sizeof(long long int));		//Se pregateste memoria pentru vectorul de stropitori
	pow = calloc(nrs, sizeof(long long int));		//Se pregateste memoria pentru vectorul de puteri

	for(i = 0; i < nrs; i++) {						//Se citesc stropitorile
		fscanf(p, "%lli", &strop[i]);
	}
	for(i = 0; i < nrs; i++) {						//Se citesc puterile
		fscanf(p, "%lli", &pow[i]);
	}

	if (strop[0] - pow[0] < 0) {					//Se verifa daca prima stropitoare poate stropi la stanga
		if (strop[0] + pow[0] < strop[1]) {			 //Daca nu, se verifica daca poat stropi la dreapta
			offset = pow[0];						  //Daca da, atunci offsetul devine egal cu peterea stropitorii, asta va servi ca o constrangere pentru urmatoarea
			cont++;									   //stropitoare, daca ar vrea sa ude la stanga. Se incrementeaza numarul din solutie
		}
	}
	else {
		cont++;										//Daca poate uda la stanga, se incrementeaza numarul din solutie
	}

	if (nrs == 0) {									//Trateaza cazul in care nu avem stropitori
		fprintf(q, "%d", cont);
		free(pow);
		free(strop);
		free(buf);
		fclose(p);
		fclose(q);
		return 0;
	}

	for (i = 1; i < nrs - 1; i++) {							//Se itereaza prin stropitori, fara prima, pana la penultima
		if (strop[i] - pow[i] <= strop[i - 1] + offset) {	//Se verifica daca stropitoarea curenta poate uda la stanga (se tine cont de offset)
			if (strop[i] + pow[i] < strop[i + 1]) { 		  //In cazul in care stropitoarea poate uda la dreapta se procedeaza ca la prima stropitoare
				offset = pow[i];
				cont++;
			}
			else {
				offset = 0;									//Daca sta pe loc, offsetul devine 0, nu ne intereseaza
			}
		}
		else {												//Daca uda la stanga, numarul din solutie se incrementeaza si offsetul devin 0
			offset = 0;
			cont++;
		}
	}
	if (strop[nrs - 1] - pow[nrs - 1] <= strop[nrs - 2] + offset) { 	//In cazul ultimei stropitori se verifica daca poate uda in stanga, la fel ca la celelalte
		if (strop[nrs - 1] + pow[nrs - 1] <= dim) {						//Se verifica daca poate uda in dreapta prin a se asigura ca nu se depaseste limita stadionului
			cont++;											 	 		 //daca poate uda la dreapta se incrementeaza numarul din solutie
		}
	}
	else {													//Daca poate uda la stanga se incrementeaza solutia
		cont++;
	}
	fprintf(q, "%d", cont);									//Se scrie in fisier solutia

	free(buf);												//Se elibereaza memoria si se inchid fisierele
	free(pow);
	free(strop);
	fclose(p);
	fclose(q);

	return 0;
}
