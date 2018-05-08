#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <time.h>
#include "lib.h"

#define HOST "127.0.0.1"
#define PORT 10001

int main(int argc, char * * argv) {

  init(HOST, PORT);
  FILE * p;				//Pointer catrte fisierele de output
  int seq = 0;				//Contor pentru secventele mesajelor
  int maxl = 0;				//Dimensiune maxima acceptata pentu un camp DATA primit
  int time = 0;				//Time de asteptat maxim inainte de un timeout
  char cond;				//Caracter ce va servi drept variabila pentru campul type din pachet
  char * recv_;				//Prefix pentru pachetul de output
  int amount = 0;			//Numar de bytes ce urmeaza a fi scrisi in fisierul de output
  msg crt, prev;			//"Prev" serveste drept mesaj de raspuns pentru penultimul mesaj primit de
  				 //la sender in timp ce "Crt" serveste drept mesajul in proces de gestionare

  pachet pack;
  if (waitFirstValid(argv[0], & prev, & crt, & seq, 5000) == NULL) {
  				//Pachet "unviversal" pentru diferite operatii
  				//Se asteapta pachetul initial de tip 'S' ce va seta "time" si "maxl"
    return 0;
  }
  memcpy( & maxl, & crt.payload[4], 1);
  time = (int) crt.payload[5];
  time = time * 1000;			//Se seteaza "time" in milisecunde, va fi utilizat drept valoare de timeout
  if (waitValid(argv[0], & prev, & crt, & seq, time) == NULL) {	//Comportament descris in "mylib.c"
    return 0;
  }
  pack = unwrap(crt);			//Se despacheteaza mesajul primit intr-un pachet "pack"
  cond = crt.payload[3];			//Tipul pachetului
  recv_ = calloc(5, 1);			//Prefixul fisierului de output, setat ca stringul "recv_"
  memcpy(recv_, "recv_", 5);
  while (cond != 'B') {			//Daca se primeste pachet te tip 'B' se opreste transmisia (se iese din while)
    if (cond == 'F') {			//Se deschide/creeaza un fisier corespunzator in cazul mesajlui de tip 'F'
      p = fopen(strcat(recv_, pack.data), "wb");
      recv_ = calloc(5, 1);
      memcpy(recv_, "recv_", 5);
    } else if (cond == 'D') {			//Daca tipul mesajului este de tip 'D' se scrie in fisier din campul DATA din pack
      memcpy( & amount, & pack.len, 1);
      amount = amount - 5;			//Aflam dimensiunea campuli DATA
      if (amount <= maxl) {
        fwrite(pack.data, 1, amount, p);
      }
      amount = 0;
    } else if (cond == 'Z') {			//Daca mesajul este de tip 'Z' se inchide fisierul curent
      fclose(p);
    }
    if (waitValid(argv[0], & prev, & crt, & seq, time) == NULL) {
      return 0;				//Se asteapta urmatorul mesaj valid ce urmeaza a fi procesat si se trimite ACK pentru
      				  //mesajul precedent
    }
    pack = unwrap(crt);
    cond = pack.type;
  }
  puts("RECEIVER ENDED");
  return 0;
}
