#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <unistd.h>

int main(int argc, char **argv) {

    int sock;					//Socketul pe care se vor trimite mesaje catre server
    int i;						//Contor
    int n; 						//Se foloseste la testarea valorii intoarse de apelul recv(..)
    int fdmax;					//Numar maxim de descriptori de fisier
    int session = 0;			//Indica daca exista o sesiune deschisa pentru clientul curent
    char *buffer = calloc(1,1);	//Buffer utilizat la trimitere/primire mesaje
    char* tok;					//String utilizat pentru a determina 'numele' comenzii
    char* aux;					//Auxiliar pentru a nu 'strica' bufferul dupa un apel strtok(..)
    char* file;				 	//Numele fisierului de log

    file = calloc(32, 1);										//Se deschide fisierul de log
    sprintf(file, "%s%d%s", "client-", getpid(), ".log");
    FILE* p = fopen(file, "w");

    struct sockaddr_in serv_addr;
    fd_set read_fds;
    fd_set tmp_fds;

	sock = socket(AF_INET, SOCK_STREAM, 0);						//Se deschide un socket pentru trimitere/primire mesaje
    if (sock < 0) {
        printf("ERROR opening socket!\n");
        fprintf(p, "ATM> -10: Eroare la apel sock()\n");
        fclose(p);
        return 0;
    }

    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = htons(atoi(argv[2]));
    inet_aton(argv[1], &serv_addr.sin_addr);


    if (connect(sock, (struct sockaddr*) &serv_addr, sizeof(serv_addr)) < 0) {	//Se realizeaza conetarea la server
    	printf("ERROR connecting!\n");
    	fprintf(p,"ATM> -10: Eroare la apel connect()");
    	fclose(p);
    	close(sock);
    	return 0;
    }
    							//Se initiaza setul cu descriptori de fisiere cu valori pentru socket si stdin
    FD_ZERO(&read_fds);
    FD_ZERO(&tmp_fds);
    FD_SET(sock, &read_fds);
    FD_SET(0, &read_fds);
    fdmax = sock;

    while(1) {					//Corp principal al clientului
        tmp_fds = read_fds;
		if (select(fdmax + 1, &tmp_fds, NULL, NULL, NULL) < 0) {
			printf("ERROR selecting!\n");
			fprintf(p,"ATM> -10 : Eroare la apel select()");
    	 	fclose(p);
    		close(sock);
    		return 0;
		}
		for(i = 0; i <= fdmax; i++) {					//Se itereaza prin toti descriptorii
			if (FD_ISSET(i, &tmp_fds)) {				//Cazul in care se gaseste un descriptor activ
				free(buffer);
				buffer = calloc(256, 1);
				if (i == 0) {							//Cazul in care se citeste de la tastatur (file descriptor 0)
					fgets(buffer, 255, stdin);
					aux = strdup(buffer);
					tok = strtok(aux, " ");
					if(strcmp(tok, "login") == 0 && session == 1) {			//Cazul in care se citeste o comanda de login si exista o sesiune deja deschisa
						puts("Session already opened!");
						fprintf(p,"ATM> -2 : Sesiune deja deschisa\n");
						break;
					}
					else if(strcmp(tok, "logout\n") == 0 && session == 0) {		//Cazul in care se citeste o comanda de logout si nu exista o sesiune deja deschisa
						puts("No previous login!");
						fprintf(p, "ATM> -1 : Clientul nu este autentificat\n");
						break;
					}
					else if(strcmp(tok, "quit\n") == 0) {				//Cazul in care se citeste o comanda de 'quit'
						send(sock, buffer, strlen(buffer), 0);
						fclose(p);
    					close(sock);
						return 0;
					}
					else if (session != 1 && strcmp(tok, "login") != 0) {		//Cazul in care se citeste o comanda diferita de 'login' fara a exista o sesiune deschisa
						puts(" Clientul nu este autentificat");
						fprintf(p,"ATM> -1 : Clientul nu este autentificat\n");
						break;
					}
					send(sock, buffer, strlen(buffer), 0);
				}
				else if ((n = recv(i, buffer, 256, 0)) <= 0) {				//Cazul in care se primeste mesaj de la server
					if (n == 0) {
						printf("Server on socket %d hung up\n", i);
						fclose(p);
    					close(sock);
						return 0;
					} else {
						printf("ERROR in receive!\n");
						fprintf(p,"ATM> -10 : Eroare la apel recv()");
    					fclose(p);
    					close(sock);
						return 0;
					}
				}
				else {
					aux = strdup(buffer);
					tok = strtok(aux, " ");
					if(strcmp(tok, "Welcome") == 0) {						//Cazul in care login a reusit
						session = 1;
						fprintf(p, "ATM> %s\n", buffer);
					}
					else if(strcmp(tok, "Goodbye") == 0) {					//Cazul in care logout-ul a reusit
						puts("Disconnected");
						fprintf(p, "ATM> Deconectare de la bancomat\n");
						session = 0;
					}
					else if(strcmp(tok, "quit\n") == 0) {					//Cazul in care quit-ul a reusit
						printf ("SERVER: %s\n", buffer);
						fclose(p);
    					close(sock);
						return 0;
					}
					else if(strcmp(buffer, "block") == 0) {					//Cazul in care contul este blocat
						printf("ATM> -5 : Card blocat\n");
						fprintf(p,"ATM> -5 : Card blocat\n");
					}
					else if(strcmp(buffer, "wrong") == 0) {					//Cazul in care s-a gresit PIN-ul
						printf("ATM> -3 : PIN gresit\n");
						fprintf(p,"ATM> -3 : PIN gresit\n");
					}
					else if(strcmp(buffer, "nomon") == 0) {					//Cazul in care suma este prea mare pentru a fi retrasa
						printf("ATM> -8 : Fonduri insuficiente\n");
						fprintf(p,"ATM> -8 : Fonduri insuficiente\n");
					}
					else if(strcmp(buffer, "nomul") == 0 || strcmp(buffer, "nonr") == 0) {	//Cazul in care suma nu este multiplu de 10
						printf("ATM> -9 : Suma ne este multiplu de 10\n");
						fprintf(p,"ATM> -9 : Suma ne este multiplu de 10\n");
					}
					else if(strcmp(buffer, "nocard") == 0) {				//Cazul in care nu exista cardul
						printf("ATM> -4 : Numar card inexistent\n");
						fprintf(p, "ATM> -4 : Numar card inexistent\n");
					}
					else if(strcmp(buffer, "unknown") == 0) {				//Cazul in care s-a trimis o comanda invalida
						printf("ATM> -6 : Operatie esuata\n");
						fprintf(p," ATM> -6 : Operatie esuata\n");
					}
					else if(strcmp(buffer, "inuse") == 0) {					//Cazul in care exista o sesiune deja deschisa pentru cardul vizat
						printf("ATM> -2 : Sesiune deja deschisa\n");
						fprintf(p, "ATM> -2 : Sesiune deja deschisa\n");
					}
					else if(strcmp(tok, "pus") == 0) {						//Cazul in care depunerea reuseste
						tok = strtok(NULL, " ");
						printf("ATM> Suma %s depusa cu succes\n", tok);
						fprintf(p, "ATM> Suma %s depusa cu succes\n", tok);
					}
					else if(strcmp(tok, "retras") == 0) {					//Cazul in care se reuseste retragerea de numerar
						tok = strtok(NULL, " ");
						printf("ATM> Suma %s retrasa cu succes\n", tok);
						fprintf(p, "ATM> Suma %s retrasa cu succes\n", tok);
					}
					else if(strcmp(tok, "sold") == 0) {						//Cazul in care operatia de sold-cont reuseste
						tok = strtok(NULL, " ");
						printf("ATM> %s\n", tok);
						fprintf(p, "ATM> %s\n", tok);
					}
					break;
				}
			}
        }
    }
    return 0;
}
