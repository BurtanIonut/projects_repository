#include "lib.h"
#include <arpa/inet.h>
#include <poll.h>
#include <netinet/in.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <unistd.h>
#include <string.h>

void mysend(msg t) {
			//Functia de transmitere mesaj
  int len = 0;
  memcpy( & len, & t.payload[1], 1);	//t.payload[1] este campul len din pachet
  t.len = len + 2;		 //dimensiunea totala a pachetului estea cea de mai sus + 2
  			  //un byte SOH, unul LEN
  send_message( & t);
}

msg * awaitRep(msg msj, int xSent, int time) {  //Functia de primire a unui nou mesaj, cu un altul deja trimis ("msj")

  msg * y = receive_message_timeout(time);
  if (y == NULL) {
    if (xSent == 3) {		//Conditia pentru a se iesi din program daca se retransmite
    			  //un pachet ca urmare a timeout-urilor
      puts("TRANSMISSION TERMINATED");
      return NULL;
    } else {
      mysend(msj);
      return awaitRep(msj, xSent + 1, time);
    }
  } else {
    return y;			//Se returneaza mesajul primit
  }
  return NULL;
}

pachet createpack(int aseq, char type, char * data, int dataSize) {
			//Creaza un pachet cu datele specificate in lista de parametrii
  char modseq;
  int seq;
  int len;
  unsigned short zero = 0;
  pachet pack;
  seq = aseq % 64;
  memcpy( & modseq, & seq, 1);		//Convertes intul primit ca parametru in char pentru a servi
			  //drept camp SEQ in pachet
  len = 5 + dataSize;		//Campul LEN va avea mereu valorea = nr bytes camp DATA +
  			  //nr bytes de la restul de campuri (1 TYPE 1 SEQ 2 CHECK 1 MARK)
  pack.soh = 0x01;		//Valoare standard pentru capul SOH
  memcpy( & pack.len, & len, 1);
  pack.seq = modseq;
  pack.type = type;
  pack.data = calloc(dataSize, 1);	//Aloc memorie pentru campul DATA cat specifica dataSize si scriu din 'data'
  memcpy(pack.data, data, dataSize);
  memcpy(pack.check, & zero, 2);
  pack.mark = 0x0D;		//Valoare standard pentru campul MARK
  return pack;
}

msg assembleMsg(pachet pack) {
			//Functie de pregatit mesajul pentru transmisie
  msg msj;
  int len = 0;
  int dataSize;
  unsigned short crc;
  memcpy( & len, & pack.len, 1);
  dataSize = len - 5;
  memcpy( & msj.payload[0], & pack.soh, 1);
  memcpy( & msj.payload[1], & pack.len, 1);
  memcpy( & msj.payload[2], & pack.seq, 1);
  memcpy( & msj.payload[3], & pack.type, 1);
  memcpy( & msj.payload[4], pack.data, dataSize);
  crc = crc16_ccitt(msj.payload, 4 + dataSize);  //Se calculeaza si suma de control
  memcpy( & msj.payload[4 + dataSize], & crc, 2);
  memcpy( & msj.payload[6 + dataSize], & pack.mark, 1);
  msj.len = len + 2;
  return msj;
}

char * dataS(int imaxl, int itime) {
			//Functie ce creaza campul DATA pentru pachet de tip 'S' cu
			  //campurile MAXL si TIME specificate in argumente
  char maxl;
  char time;
  char * data = calloc(11, 1);		//Aloc 11 bytes deoarece este dimensiunea standard pentru acest camp DATA
  memcpy( & maxl, & imaxl, 1);
  memcpy( & time, & itime, 1);
  data[0] = maxl;
  data[1] = time;
  return data;
}

char * dataNo() {		//Functie care creeaza un camp DATA "gol"

  char * data = calloc(1, 1);
  data[0] = 0x00;
  return data;
}

pachet unwrap(msg x) {
			//Functie care creeaza un pachet cu informatia din mesajul primit
  pachet pack;
  int dataSize = 0;
  memcpy( & dataSize, & x.payload[1], 1);
  dataSize -= 5;
  char data[dataSize];
  memcpy(data, & x.payload[4], dataSize);
  pack = createpack((int) x.payload[2], x.payload[3], data, dataSize);
  pack.check[0] = x.payload[dataSize + 4];
  pack.check[1] = x.payload[dataSize + 5];
  pack.mark = x.payload[dataSize + 6];
  return pack;
}

char check(msg msj) {
			//Functie care verifica daca mesajul a fost primit corect, fara bytes eronati
			  //Intoarce 'Y' in caz afirmativ, altfel 'N'
  unsigned short crc = crc16_ccitt(msj.payload, msj.len - 3);
  char oldcrc[2];
  char newcrc[2];
  memcpy(newcrc, & crc, 2);
  memcpy(oldcrc, & msj.payload[msj.len - 3], 2);
  if (oldcrc[0] == newcrc[0] && oldcrc[1] == newcrc[1]) {
    return 'Y';
  }
  return 'N';
}

msg * waitR(msg * r, int time) {
			//Ca funcia awaitRep de mai sus, mai concentrata
  msg * y = awaitRep( * r, 0, time);
  if (y == NULL) {
    return NULL;
  }
  return y;
}

msg * waitConf(char * argv, msg * t, msg * rep, int * seq, int time) {
			//Functie care asteapta confirmare (ACK) pentru mesajul trasnmis
  pachet pack;
  msg * y;
  y = waitR(t, time);
  if (y == NULL) {
    return NULL;
  }
  * rep = * y;
  pack = unwrap( * rep);
  while (1) {
    if (pack.type != 'Y' || (int) pack.seq != ( * seq) % 64) {
    			//In cazul in care se primeste NACK sau secventa nu corespunde se
    			  //retrnsimte mesajul pana la primitea unui ACK
      mysend( * t);
      y = waitR(t, time);
      if (y == NULL) {
        return NULL;
      }
      * rep = * y;
      pack = unwrap( * rep);
    } else {
        * seq = * seq + 1;		//Se incrementeaza numarul de secventa contorizat in procesul utilizator
    			  //in caz de primire a unui ACK
       return t;
    }
  }
}

int checkSeq(msg r, int seq) {
			//Funcite care verifica daca SEQ din mesaj coincide cu secventa contorizata de
			  //de proces
  if ((int) r.payload[2] - seq % 64 == 0) {

    return 1;
  }
  return 0;
}

char * evaluate(msg r, int seq) {
			//Evalueaza un mesaj atat din punct de vedere al erorilor de trasmisie cat si a
			  //secventei in cazul unui mesaj primit adecvat (fara erori)
  char * ret = calloc(2, 1);
  char eval = check(r);
  int evalseq;
  if (eval == 'N') {
    ret[0] = 'N';
    return ret;
  }
  evalseq = checkSeq(r, seq);
  ret[0] = 'Y';
  memcpy( & ret[1], & evalseq, 1);
  return ret;			//Intoarce un vector de 2 caractere in care primul corespunde evaluarii corectitudini
  			  //si al doilea corespunde corectitudinii secventei
}

msg * waitValid(char * argv, msg * prev, msg * crt, int * seq, int time) {
			//Functia asteapta sa primeasca un mesaj valid (fata erori de transmie si secventa adecvata)
			//De asemenea, trimite raspuns ultimuli mesaj primit (ACK sau NACK)
  char * eval = calloc(2, 1);
  pachet pack;
  msg * y;
  msg nack;
  eval[0] = 'N';
  mysend( * prev);		//Transmitere raspuns
  y = waitR(prev, time);		//Asteptare mesaj nou
  if (y == NULL) {
    printf("[%s]RECEIVER TERMINATED\n", argv);
    return NULL;
  }
  * crt = * y;
  eval = evaluate( * crt, * seq);
  pack = createpack( * seq, 'N', dataNo(), 1);  //Creare de pachet pentru raspuns de tip NACK
  nack = assembleMsg(pack);
  * prev = nack;
  while (eval[0] == 'N' || (int) eval[1] == 0) {//Se trimit NACK-uri pana e primeste un mesaj valid (ca mai sus)
    mysend( * prev);
    y = waitR(prev, time);
    * crt = * y;
    if (y == NULL) {
      printf("[%s]RECEIVER TERMINATED\n", argv);
      return NULL;
    }
    eval = evaluate( * crt, * seq);
  }
  pack = createpack( * seq, 'Y', dataNo(), 1);
  * prev = assembleMsg(pack);
  * seq = * seq + 1;		//Daca s-a primit mesaj valid se incremeteaza secventa contorizata in proces
  return prev;
}

msg * waitFirstValid(char * argv, msg * prev, msg * crt, int * seq, int time) {
			//Functia asteapta sa primeasca primul pachet valid (ca specificat anterior)
  pachet pack;
  msg ack, nack;
  char * eval;
  msg * y = receive_message_timeout(time * 3);
  if (y == NULL) {
    printf("[%s]RECEIVER TERMINATED\n", argv);
    return NULL;
  } * crt = * y;
  eval = evaluate( * crt, * seq);
  if (eval[0] == 'Y' && (int) eval[1] == 1) {
    pack = createpack( * seq, 'Y', dataNo(), 1);
    ack = assembleMsg(pack);	 	//Daca s-a primit mesaj valid se creaza un pachet ACK care va fi util
    * prev = ack;		  //functiei de transmitere a confirmarii
    * seq = * seq + 1;		//Daca s-a primit mesaj valid se incremeteaza secventa contorizata in proces
  } else if (eval[0] == 'N') {
    pack = createpack( * seq, 'N', dataNo(), 1);//Daca mesajul primit nu e valid se se creaza un pachet NACK care va fi util
    			  //functiei de transmitere a confirmarii, "waitValid"
    nack = assembleMsg(pack);		//Daca mesajul primit nu e valid se apeleaza functia "waitValid"
    * prev = nack;
   return waitValid(argv, prev, crt, seq, time);
  }
  return prev;
}

pachet createpackF(int seq, char * argv) {
			//Creaza un pachet pentru mesajul de tip 'F'
			 //argv este numele fisieruli (argv[x] in main)
  pachet pack = createpack(seq, 'F', argv, strlen(argv));
  return pack;
}

pachet createpackZ(int seq) {
			//Creaza un pachet pentru mesajul de tip 'Z'
  pachet pack = createpack(seq, 'Z', dataNo(), 1);
  return pack;
}

pachet createpackB(int seq) {
			//Creaza un pachet pentru mesajul de tip 'B'
  pachet pack = createpack(seq, 'B', dataNo(), 1);
  return pack;
}
