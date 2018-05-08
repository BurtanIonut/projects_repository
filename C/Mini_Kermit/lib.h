#ifndef LIB
#define LIB

typedef struct {
    int len;
    char payload[1400];
} msg;

typedef struct pachet {
	char soh;
	char len;
	char seq;
	char type;
	char* data;
	char check[2];
	char mark;
} pachet;

void init(char* remote, int remote_port);
void set_local_port(int port);
void set_remote(char* ip, int port);
int send_message(const msg* m);
int recv_message(msg* r);
msg* receive_message_timeout(int timeout); //timeout in milliseconds
unsigned short crc16_ccitt(const void *buf, int len);

void mysend (msg t);
pachet createpack (int seq, char type, char* data, int dataSize);
msg assembleMsg (pachet pack);
char* dataS (int imaxl, int itime);
pachet unwrap(msg x);
char check (msg msj);
msg* awaitRep (msg msj, int xSent, int time);
msg* waitR(msg* r, int time);
msg* waitConf(char* argv, msg* t, msg* rep, int* seq, int time);
int checkSeq (msg r, int seq);
char* evaluate (msg r, int seq);
msg* waitValid(char* argv, msg* prev, msg* crt, int* seq, int time);
msg* waitFirstValid(char* argv,msg* prev, msg* crt, int* seq, int time);
pachet createpackF(int seq, char* argv);
pachet createpackZ(int seq);
pachet createpackB(int seq);
#endif
