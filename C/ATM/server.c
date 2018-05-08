#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

typedef struct{										//Structura pentru retinerea datelor despre un utlizator

	char last[13];
	char first[13];
	char card[7];
	char pin[5];
	char pass[17];
	double credit;

} user;
													//Structura pentru a retine date despre starea unui utlizator
typedef struct{

	user usr;
	int tries;
	int block;
	int sock;

} user_serv;

char** getTok(char* buf, int* nrParam) {			//Functie de "impartit" un string in substringuri
													 //substringurile se vor separa dupa " " si "\n" in strtok(..)
	int i = 0;										 //numarul de sustringuri obtinute se salveaza la '&nrParam'
	char** aux = calloc(6, sizeof(char*));
	char* tok = strtok(buf, " \n");
	while(tok != NULL) {
		aux[i] = tok;
		tok = strtok(NULL, " \n");
		i++;
	}
	*nrParam = i;
	return aux;
}

int checkLog(user_serv* usrs, int maxcl, int sock, int* client) {	//Functie care verifica daca EXISTA 'userul' de pe
																	  //pozitia 'j' din vectoul de 'useri' care are
																	  //conexiune stabilita pe socketul 'sock' cu serverul,
	int j;															  //se intoarce '1' in caz afirmatif si pozitia se salveaza la '&client'
	for(j = 0; j < maxcl; j++) {
		if(usrs[j].sock == sock) {
			*client = j;
			return 1;
		}
	}
	return 0;
}

int main(int argc, char** argv) {

	int i, j;				//Contore si variabile ajutatoare
	int n;					//Variabila pentru testarea valorii intoarse de apelul 'recv(..)'
	int sockT, cl_sock;		//Socketul pe care se face 'listen' si socketul pe care se 'accepta' conexiuni
	int portno;				//Stocheaza 'portul' dat ca parametru
	socklen_t cl_len;		//Dimensiune utilizata de apelul 'accept(..)' (egala cu lungimea tipului de date 'sockaddr_in')
	int maxcl = 0;			//Numar maxim de clienti care se pot conecta simultan
	int nrPrm;				//Numar parametri dati unei comenzi + 1 (ex: login 112233 1234 are 3 parametri)
	int blocked;			//Valoare auxiliara care ajuta la testarea unui card daca este blocat
	double credit = 0.0;	//Valoare auxiliara pentru gestionarea operatiilor ce utilizeaza soldul

	char* buffer;			//Buffer pentru trimitere/primire date de la clienti
	char** tok;				//Vector de substringui obtinute dupa apelul functiei getTok(..)

	struct sockaddr_in serv_addr, cl_addr;		//Structuri pentru retinerea informatiilor adreselor pentru server/clienti

	fd_set read_fds;		//Retine descriptorii pentu fisierele ce pot initia o operatie
	fd_set temp_fds;		//Retine descriptorii activi la un momentdat
	int fdmax;				//Numarul maxim de descriptori

	FILE* p = fopen(argv[2], "r");							//Deschid fisierul de 'credentiale' primit ca parametru
	user_serv* usrs = calloc(maxcl, sizeof (user_serv));	//Initiez vectorul de useri

	fscanf(p, "%d", &maxcl);								//Citesc din fisier numarul maxim de utilizatori

	buffer = calloc(256, 1);								//Se citesc datele utilizatorilor din fisier....
	fgets(buffer, 256, p);
	for(j = 0 ;j < maxcl; j++) {
		buffer = calloc(256, 1);
		fgets(buffer, 256, p);
		tok = getTok (buffer, &nrPrm);

		memcpy(usrs[j].usr.last, tok[0], strlen(tok[0]));
		usrs[j].usr.last[strlen(usrs[j].usr.last)] = '\0';

		memcpy(usrs[j].usr.first, tok[1], strlen(tok[1]));
		usrs[j].usr.first[strlen(usrs[j].usr.first)] = '\0';

		memcpy(usrs[j].usr.card, tok[2], strlen(tok[2]));
		usrs[j].usr.card[strlen(usrs[j].usr.card)] = '\0';

		memcpy(usrs[j].usr.pin, tok[3], strlen(tok[3]));
		usrs[j].usr.pin[strlen(usrs[j].usr.pin)] = '\0';

		memcpy(usrs[j].usr.pass, tok[4], strlen(tok[4]));
		usrs[j].usr.pass[strlen(usrs[j].usr.pass)] = '\0';

		credit = atof(tok[5]);
		memcpy(&usrs[j].usr.credit, &credit, 8);

		usrs[j].block = 0;
		usrs[j].tries = 0;
		usrs[j].sock = -1;
	}
															//....se termina de citit datele
															//Se deschide un socket pe care se asteapta conexiuni
	sockT = socket(AF_INET ,SOCK_STREAM, 0);
	if (sockT < 0) {
		printf("ERROR opening socket!\n");
		fclose(p);
		return 0;
	}

	memset((char *) &serv_addr, 0, sizeof(serv_addr));
	portno = atoi(argv[1]);

    serv_addr.sin_family = AF_INET;
    serv_addr.sin_addr.s_addr = INADDR_ANY;
    serv_addr.sin_port = htons(portno);
    														//Apelul 'setsockopt(..)' previne eroarea de 'bind(..)'
    int yes = 1;
    setsockopt(sockT, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof (int));

    if (bind(sockT, (struct sockaddr *) &serv_addr, sizeof(struct sockaddr)) < 0) {
    	printf("ERROR on binding!\n");
    	close(sockT);
    	fclose(p);
    	return 0;
    }
         						 //Se initiaza setul cu descriptorii de fisiere
    FD_ZERO(&read_fds);
    FD_ZERO(&temp_fds);
       							//Se 'asculta' conexiuni, maxim 20 simultane
    listen(sockT, 20);
    						    //Se actualizeaza setul de descriptori cu socketul de 'listen' -sockT si de 'stdin' -0
    FD_SET(sockT, &read_fds);
    FD_SET(0, &read_fds);
    fdmax = sockT;
    							//Corpul principal al serverului
    while(1) {
    	temp_fds = read_fds;	//Fiecare iteratie presupune un nou set de descriptori activi
    	if (select(fdmax + 1, &temp_fds, NULL, NULL, NULL) < 0) {
    		printf("ERROR in select!\n");
    		close(sockT);
			close(cl_sock);
			fclose(p);
    		return 0;
    	}
    	for(i = 0; i <= fdmax; i++) {					//Se cauta descriptorii 'activi'
    		if (FD_ISSET(i, &temp_fds)) {	            //Daca se gaseste un astfel de descriptor...
    			buffer = calloc(256, 1);
    			if (i == 0) {
    				fgets(buffer, 255, stdin);
    				if (strcmp (buffer, "quit\n") == 0) {  //In cazul in care se primeste de la 'stdin' comanda 'quit' (QUIT DIN SERVER)
						for (j = 1; j <= fdmax; j++) {	   //Se  trimite intentia serverlui la clienti, se 'dezactiveaza' socketurile asociate
														    //utilizatorilor inregistrati , se inchid socketurile asociate clientilor
							if (FD_ISSET(j, &read_fds)) {
								if (j != sockT) {
									send(j, buffer, strlen(buffer), 0);
									usrs[j].sock = -1;
									close(j);
								}
							}
						}								//Se sterg toti descriptorii, se elibereaza memoria si se inchid restul de socketi
						puts("BYE!");
						FD_ZERO(&read_fds);
						free(buffer);
    					close(sockT);
						close(cl_sock);
						close(0);
						fclose(p);
						return 0;
    				}
    			}
    			else if (i == sockT) {								//In cazul in care se primeste o noua conexiune
    				memset((char *) &cl_addr, 0, sizeof(cl_addr));
    				cl_len = sizeof (cl_addr);
    				if((cl_sock = accept(sockT, (struct sockaddr* ) &cl_addr, &cl_len)) < 0) {
    					printf("ERROR in accept!\n");
    					free(buffer);
    					close(sockT);
						close(cl_sock);
						fclose(p);
    					return 0;
    				}								//Se aduga un nou descriptor pentu noua conexiune
    				FD_SET (cl_sock, &read_fds);
    				if (cl_sock > fdmax) {
    					fdmax = cl_sock;
    				}
    				printf("New connection: %s, port %d, socket %d\n", inet_ntoa(cl_addr.sin_addr), ntohs(cl_addr.sin_port), cl_sock);
    			}
    			else {
					if ((n = recv(i, buffer, 256, 0)) <= 0) {
						if (n == 0) {									//Cazul in care clientul nu se inchide cu 'quit' (merge pentru inchiderea cu 'ctrl-C')
							printf("Server: socket %d hung up\n", i);
							close(i);
							FD_CLR(i, &read_fds);
						} else {
							printf("ERROR in receive!\n");
							free(buffer);
    						close(sockT);
							close(cl_sock);
							fclose(p);
							return 0;
						}
					}
					else {														//Cazurile in care se primesc mesaje de le clienti
						printf ("Recv from sock: %d, msg: %s\n", i, buffer);
						if (buffer[0] == '\n') {							//Comanda 'goala', se ignora
							break;
						}
						tok = getTok(buffer, &nrPrm);						//Se imparte comanda in substringuri si se determina numarul de parametrii

						if(strcmp(tok[0], "login") == 0 && nrPrm == 3) {	//Cazul in care se primeste un 'login' valid
							puts("loging in..");
							for(j = 0; j < maxcl; j++) {					//Se cauta cardul in vectorul de utilizatori
								if(strcmp(usrs[j].usr.card, tok[1]) == 0) {	//Cazul in care cardul exista
									puts("Card found!");
									if(usrs[j].sock != -1) {				//Cazul in care userul are un socket deja asociat (Sesiune deja deschisa)
										sprintf(buffer, "inuse");
										send(i, buffer, strlen(buffer), 0);
										break;
									}
									blocked = usrs[j].block;
									if (blocked == 0) {						//Cazul in care cardul nu este blocat
										if(strcmp(usrs[j].usr.pin, tok[2]) == 0) {		//Cazul in care PIN-ul s-a introdus corect
											puts("Pin found!");
											usrs[j].sock = i;
											usrs[j].tries = 0;
											sprintf(buffer, "Welcome %s %s", usrs[j].usr.last, usrs[j].usr.first);
											send(i, buffer, strlen(buffer), 0);
											break;
										}
										else {								//Cazul in care pinul este introdus gresit
											puts("Wrong PIN!");
											if(usrs[j].tries == 2) {		//Cazul in care contul se blocheaza
												usrs[j].block = 1;
												sprintf(buffer, "block");
												send(i, buffer, strlen(buffer), 0);
												break;
											}
											sprintf(buffer, "wrong");
											send(i, buffer, strlen(buffer), 0);
											usrs[j].tries++;
											break;
										}
									}
									else {									//Cazul in care se incearca autentificarea pe un cont deja blocat
										puts("Account blocked!");
										sprintf(buffer, "block");
										send(i, buffer, strlen(buffer), 0);
										break;
									}
								}
								else if (j + 1 == maxcl){					//Cazul in care cardul cautat nu este asociat niciunui utilizator
									sprintf(buffer, "nocard");
									send(i, buffer, strlen(buffer), 0);
									break;
								}
							}
						}
						else if (strcmp(tok[0], "logout") == 0 && nrPrm == 1) {  //Cazul in care se primeste un logout valid
							for (j = 0; j < maxcl; j++) {
								if (usrs[j].sock == i) {
									usrs[j].sock = -1;
									sprintf(buffer, "Goodbye : %s %s!\n", usrs[j].usr.last, usrs[j].usr.first);
									send(i, buffer, strlen(buffer), 0);
								}
							}
						}
						else if (strcmp(tok[0], "listsold") == 0 && nrPrm == 1) {	//Cazul in care se primeste o cerere de sold-cont valida
							if(checkLog(usrs, maxcl, i, &j) == 1) {
								sprintf(buffer, "sold %.2lf", usrs[j].usr.credit);
								send(i, buffer, strlen(buffer), 0);
							}
						}
						else if (strcmp(tok[0], "getmoney") == 0 && nrPrm == 2) {	//Cazul in care se primeste o cerere de retragere-numerar valida
							if (checkLog(usrs, maxcl, i, &j) == 1) {
								credit = atof(tok[1]);
								if(strstr(tok[1], ".") != NULL && credit != 0) {	//Cazul in care suma nu este acceptata
									puts("Numar invalid!");
									sprintf(buffer, "nonr");
									send(i, buffer, strlen(buffer), 0);
								}
								else {
									if (atoi(tok[1]) % 10 != 0) {					//Cazul in care suma nu este multiplu de 10
										sprintf(buffer, "nomul");
										send(i, buffer, strlen(buffer), 0);
									}
									else if (usrs[j].usr.credit - credit < 0) {		//Cazul in care se cere extragerea unei sume mai mari decat disponibile
										sprintf(buffer, "nomon");
										send(i, buffer, strlen(buffer), 0);
									}
									else {													//Cazul in care suma se retrage cu succes
										usrs[j].usr.credit = usrs[j].usr.credit - credit;
										sprintf(buffer, "retras %.2lf", credit);
										send(i, buffer, strlen(buffer), 0);
									}
								}
							}
						}
						else if (strcmp(tok[0], "putmoney") == 0 && nrPrm == 2) {	//Cazul in care se cere o operatie de depunere-numerar
							if (checkLog(usrs, maxcl, i, &j) == 1) {
								credit = atof(tok[1]);
								if(credit == 0) {									//Cazul in care suma nu este valida
									puts("Numar invalid!");
									sprintf(buffer, "invalid!\n");
									send(i, buffer, strlen(buffer), 0);
								}
								else {													//Cazul in care se reuseste depunerea
									usrs[j].usr.credit = usrs[j].usr.credit + credit;
									sprintf(buffer, "pus %.2lf", credit);
									send(i, buffer, strlen(buffer), 0);
								}
							}
						}
						else if (strcmp(tok[0], "quit") == 0 && nrPrm == 1) {		//Cazul in care un client se deconecteaza
							printf("Session: %d closed! \n", i);
							close(i);
							usrs[i].sock = -1;
							FD_CLR(i, &read_fds);
						}
						else {												//Cazul in care se primeste o operatie nevalida
							sprintf(buffer, "unknown!");
							printf("From socket: %d, %s\n", i, buffer);
							send(i, buffer, strlen(buffer), 0);
						}
					}
    			}
    			free(buffer);
    		}
    	}
    }
	return 0;
}

