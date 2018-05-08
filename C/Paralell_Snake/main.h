#ifndef __MAIN_H__
#define __MAIN_H__

struct coord
{
	int line;
	int col;
};

struct cel
{
	struct cel* next;
	struct cel* prev;
	struct coord coord;
};

struct snake
{
	struct coord head;
	int encoding;
	char direction;

	struct cel* noggin;
	struct cel* tail;
};


void updateWorld(int** world, struct snake* snakes, struct coord* coord, int num_snakes);

int collisions(int** world, struct snake* snakes, struct coord* coords, int num_snakes);

void buildSnake (struct snake* snek, int** world, int num_lines, int num_cols);

struct coord moveSnake (struct snake* snek, int num_lines, int num_cols);

void initSnake(struct snake* snek);

void enque(struct snake* snek, struct coord coord);

struct cel* initCell(struct coord coord);

void print_world(char *file_name, int num_snakes, struct snake *snakes,
	int num_lines, int num_cols, int **world);

void read_data(char *file_name, int *num_snakes, struct snake **snakes,
	int *num_lines, int *num_cols, int ***world);

void run_simulation(int num_lines, int num_cols, int **world, int num_snakes,
	struct snake *snakes, int step_count, char *file_name);

#endif