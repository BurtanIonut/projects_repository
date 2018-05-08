#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <time.h>
#include "lib.h"

#define HOST "127.0.0.1"
#define PORT 10000

int main(int argc, char * * argv) {

  init(HOST, PORT);
  FILE * p;				//Pointer catre fisierul din care urmeaza sa se citeasca date
  int seq = 0;				//Secventa contorizata pentru a tine evidenta secventelor mesajelor
  				  //trimise/primite
  int maxl = 250;			//Valoare predefinita a dimensiunii maxime pentru campul DATA
  int time = 5;				//Valoare predefinita a dimensiunii timpului de asteptat inainte de timeout
  int i;
  int amount;				//Numar de bytes cititi din fisier
  char * buf;				//Buffer pentru datele citite
  msg t;				//Mesaj de trimis catre receiver
  msg rep;				//Mesaj primit de la receiver (ACK/NACK)
  pachet pack;
  pack = createpack(seq, 'S', dataS(maxl, time), 11);	//Se creaza pachetul 'S' cu campurile aferente
  t = assembleMsg(pack);
  time = time * 1000;			//Dupa ce se asambleaza mesajul de tip 'S' se seteaza "time" in milisecunde
  mysend(t);
  if (waitConf(argv[0], & t, & rep, & seq, time) == NULL) {	//Se asteapta ACK pentru ultimul mesaj trimis
    return 0;
  }
  buf = calloc(maxl, 1);
  amount = 0;
  for (i = 1; i < argc; i++) {			//Se trece prin toate fisierele de input
    p = fopen(argv[i], "rb");
    pack = createpackF(seq, argv[i]);
    t = assembleMsg(pack);
    mysend(t);				//Se trimite mesaj de tip 'F'
    if (waitConf(argv[0], & t, & rep, & seq, time) == NULL) {
      return 0;
    }
    amount = fread(buf, 1, maxl, p);
    while (amount > 0) {			//Se citesc si se trimit toate datele din fisierul curent prin mesaje 'D'
      pack = createpack(seq, 'D', buf, amount);
      t = assembleMsg(pack);
      mysend(t);
      if (waitConf(argv[0], & t, & rep, & seq, time) == NULL) {
        return 0;
      }
      amount = fread(buf, 1, maxl, p);
    }
    fclose(p);
    pack = createpackZ(seq);
    t = assembleMsg(pack);
    mysend(t);				//Se trimite mesaj de tip 'Z'
    if (waitConf(argv[0], & t, & rep, & seq, time) == NULL) {
      return 0;
    }
  }
  pack = createpackB(seq);
  t = assembleMsg(pack);
  mysend(t);				//Se trimite mesaj de tip 'B' dupa ce au fost proceste toate input-urile
  if (waitConf(argv[0], & t, & rep, & seq, time) == NULL) {
    return 0;
  }
  puts("SENDER ENDED");
  return 0;
}
