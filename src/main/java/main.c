#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <stdbool.h>

#define MY_CAST 			"CAST"
#define OPPONENT_CAST 		"OPPONENT_CAST"
#define LEARNABLE 			"LEARN"
#define REST				"REST"
#define WAIT				"WAIT"
#define BREW				"BREW"

#define NUMBER_OF_ACTION	255
#define TOME_SIZE			6
#define NUMBER_OF_BREWABLE	5

#define MAX_INV_SIZE		10	//Number of items in inventory

#define DEBUG				0	//1 to enable and 0 to disable
#define INFO				1	//1 to enable and 0 to disable

/**************************************************************************
 *
 *
 *
 * Computation Time
 * microsecondes
 *
 *
 * *************************************************************************/

#include <time.h>
int64_t start_point;
int64_t turn_number = 0;

#define FIRST_TURN_TIMEOUT 	1000 * 1000 * 1000	//Milliseconds converted into Nano
#define EACH_TURN_TIMEOUT	50 	 * 1000 * 1000

int64_t lire_heure_ns(void)
{
  struct timespec  result = {};
  clock_gettime(CLOCK_MONOTONIC, &(result));
  return (result.tv_sec*1000000000) + (result.tv_nsec);
}

int64_t current_elapsed(void) {
	return lire_heure_ns() - start_point;
}

bool has_time(int64_t required_nano) {
	return current_elapsed() + required_nano < ((turn_number==0) ? FIRST_TURN_TIMEOUT : EACH_TURN_TIMEOUT);
}

void start_turn(void) {
	turn_number = turn_number + 1;
	start_point = lire_heure_ns();
}

void end_turn(void) {
	if(INFO)
		fprintf(stderr, "Time: %lld ms [ %lld nano ]", (current_elapsed()/(1000*1000)), current_elapsed());
}

/**************************************************************************
 *
 *
 *
 * Structures
 *
 *
 *
 * *************************************************************************/

typedef struct Receipt {
	int action_id;
	char* action;
	signed int* delta;			//Pointer vers array of 4 int
	int price;
	int tome_index;
	int tax_count;
	bool castable;
	bool repeatable;

} Receipt;

/**
 *
 * Permit to store a list of Receipt.
 *
 **/
typedef struct ReceiptList {
	Receipt** list;
	int count;
} ReceiptList;

typedef struct Player {
	signed int* inv;			//0..3 to access to the correct info
	int score;			//Number of rupeed

	ReceiptList * casts;
} Player;				//Corresponding to a player


typedef struct PathNode {
	Receipt* cast;
	int repeat;
} PathNode;

typedef struct Path {
	Receipt * destination;
	PathNode * path_nodes[NUMBER_OF_ACTION];
	int length;
} Path;

typedef struct Solver {
	Receipt * brewable[NUMBER_OF_ACTION];
	Receipt * learnable[TOME_SIZE];
	signed int * start_point;
	Receipt * casts[NUMBER_OF_ACTION];

	Path * paths[NUMBER_OF_ACTION];
	int paths_length;
} Solver;


/**************************************************************************
 *
 *
 *
 * Global Variable
 *
 *
 *
 * *************************************************************************/

Player* players[2];		//Used to store info related to myself or opponent
Receipt* receipts[NUMBER_OF_ACTION];			//Ids goes between 0 and 100
int brewable_count;								//Keep track of the brewable count
Receipt* brewable[NUMBER_OF_BREWABLE];
Receipt* learnable[TOME_SIZE];
Receipt* wait_receipt, *rest_receipt;




/**************************************************************************
 *
 *
 *
 * Functions
 *
 *
 *
 * *************************************************************************/
ReceiptList* allocateList() {
	ReceiptList* ret = malloc(sizeof(ReceiptList));
	ret->list = calloc(NUMBER_OF_ACTION, sizeof(Receipt*));
	ret->count = -1;
	return ret;
}

void addToList(ReceiptList* _list, Receipt* _element) {
	_list->list[_list->count+1] = _element;
	_list->count = _list->count + 1;
}

void printReceipt(Receipt* receipt) {
	fprintf(stderr, "Receipt id: %d\n", receipt->action_id);
}

void printReceiptList(ReceiptList* list) {
	for(int i = 0 ; i <= list->count ; i++) {
		printReceipt(list->list[i]);
	}
}

void printLearnable() {
	for(int i = 0 ; i < TOME_SIZE ; i++) {
		if(learnable[i] != NULL)
			printReceipt(learnable[i]);
	}
}

void printBrewable() {
	for(int i = 0 ; i < NUMBER_OF_BREWABLE; i++) {
		if(brewable[i] != NULL)
			printReceipt(brewable[i]);
	}
}

void printDelta(signed int* delta) {
	fprintf(stderr, "[%d %d %d %d]\n", delta[0], delta[1], delta[2], delta[3]);
}

void printAction(Receipt* _receipt) {
	Receipt* receipt = _receipt;
	if(receipt == NULL) {
		receipt = wait_receipt;
	}
	 if(receipt->action_id == 32000) {
		end_turn();
		fprintf(stdout, "%s\n", receipt->action);
	}
	else {
		end_turn();
		fprintf(stdout, "%s %d\n", receipt->action, receipt->action_id);
	}
}

void printRepeatableAction(Receipt* receipt, int repeat) {
	end_turn();
	fprintf(stdout, "%s %d %d\n", receipt->action, receipt->action_id, repeat);
}

void printPlayer(Player* player) {
	printDelta(player->inv);
}

signed int* allocateDelta(signed int d1, signed int d2, signed int d3, signed int d4) {
	int* ret = calloc(4, sizeof(signed int));
	ret[0] = d1;
	ret[1] = d2;
	ret[2] = d3;
	ret[3] = d4;
	return ret;
}

int compareDelta(signed int* a, signed int* b) {
	for(int i = 0 ; i < 4 ; i++) {
		if(a[i]!=b[i])
			return a[i]-b[i];
	}
	return 0;
}

void addCastToPlayer(Player* player, Receipt* cast) {
	addToList(player->casts, cast);
}

signed int* sumDeltas(signed int* a, signed int* b) {
	int* ret = calloc(4, sizeof(int));
	for(int i = 0 ; i < 4 ; i++) {
		ret [i] = a[i] + b[i];
	}
	return ret;
}

bool isDeltaValid(signed int * delta) {
	return ((delta[0]>0?delta[0]:0)+(delta[1]>0?delta[1]:0)+(delta[2]>0?delta[2]:0)+(delta[3]>0?delta[3]:0)) <= MAX_INV_SIZE;
}

bool isCastPossible(signed int * ref, signed int * delta) {
	signed int* sum = sumDeltas(ref, delta);
	if(!isDeltaValid(sum))
		return false;
	for(int i = 0 ; i < 4 ; i++) {
		if(sum[i] != ref[i] && ref[i] > 0 && sum[i] < 0) {    //from positive to negative
            return false;
		}
	}
	return true;
}

/**
 *
 * Permit to update or alloc a new Receipt
 *
 **/
void updateOrAllocReceipt(int _action_id, char* _action, int* _delta,
		int _price, int _tome_index, int _tax_count, bool _castable,
		bool _repeatable) {
		Receipt* receipt = receipts[_action_id];

		if(receipt == NULL) {
			receipt = malloc(sizeof(Receipt));
			receipt->delta = calloc(4, sizeof(signed int));
			memcpy(receipt->delta, _delta, sizeof(signed int)*4);
			receipts[_action_id] = receipt;
			receipt->action = calloc(21, sizeof(char));
		}
		else {
			free(_delta);
		}
		receipt->action_id = _action_id;
		strcpy(receipt->action, _action);
		receipt->repeatable = _repeatable;
		receipt->price = _price;
		receipt->tome_index = _tome_index;
		receipt->tax_count = _tax_count;
		receipt->castable = _castable;
		if(DEBUG){
		    fprintf(stderr, "Receipt id: %d  ", receipt->action_id);
		    printDelta(receipt->delta);
        }
		//Add to myself or opponent cast or Learnable
		if(strcmp(_action, MY_CAST) == 0) {
			if(DEBUG)
				fprintf(stderr, "Receipt %d added to my casts\n", receipt->action_id);
			addCastToPlayer(players[0], receipt);
		} else if(strcmp(_action, OPPONENT_CAST) == 0) {
			if(DEBUG)
				fprintf(stderr, "Receipt %d added to opponent cast\n", receipt->action_id);
			addCastToPlayer(players[1], receipt);
		} else if(strcmp(_action, LEARNABLE) == 0) {
			if(DEBUG)
				fprintf(stderr, "Receipt %d added to learnable\n", receipt->action_id);
			learnable[receipt->tome_index] = receipt;
		} else if(strcmp(_action, BREW) == 0) {
			if(DEBUG)
				fprintf(stderr, "Receipt %d added to BREWABLE\n", receipt->action_id);
			brewable[brewable_count] = receipt;
			brewable_count = brewable_count + 1;
		}
}

void updateInventory(Player* player, signed int _inv_1, signed int _inv_2, signed int _inv_3, signed int _inv_4, int _score) {
	if(player->inv == NULL) {
		player->inv = allocateDelta(_inv_1, _inv_2, _inv_3, _inv_4);
	}
	else {
		player->inv[0] = _inv_1;
		player->inv[1] = _inv_2;
		player->inv[2] = _inv_3;
		player->inv[3] = _inv_4;
	}
	player->score = _score;

	if(DEBUG) {
		printPlayer(player);
	}
}

/**
 *
 * Copy or create new solver.
 *
 * typedef struct Solver {
	Receipt * brewable[];
	Receipt * learnable[];
	signed int * start_point;
	Receipt ** casts;

	Path* paths[];
	int paths_length;
} Solver;
 *
 **/
Solver * allocateSolver(Solver * solver) {
	Solver *ret = malloc(sizeof(Solver));
	ret->paths_length = -1;
	if(solver == NULL) {
		ret->start_point = allocateDelta(players[0]->inv[0], players[0]->inv[1], players[0]->inv[2], players[0]->inv[3]);
		for(int i = 0 ; i < TOME_SIZE ; i++) {
			ret->learnable[i] = malloc(sizeof(Receipt));
			memcpy(ret->learnable[i], learnable[i], sizeof(Receipt));
		}
		for(int i = 0 ; i < NUMBER_OF_BREWABLE ; i++) {
			ret->brewable[i] = malloc(sizeof(Receipt));
			memcpy(ret->brewable[i], brewable[i], sizeof(Receipt));
		}
		for(int i = 0 ; i <= players[0]->casts->count ; i++) {
			ret->casts[i] = malloc(sizeof(Receipt));
			memcpy(ret->casts[i], players[0]->casts->list[i], sizeof(Receipt));
		}
	}
	else {
		ret->start_point = allocateDelta(solver->start_point[0], solver->start_point[1], solver->start_point[2], solver->start_point[3]);
		for(int i = 0 ; i < TOME_SIZE ; i++) {
			ret->learnable[i] = malloc(sizeof(Receipt));
			memcpy(ret->learnable[i], solver->learnable[i], sizeof(Receipt));
		}
		for(int i = 0 ; i < NUMBER_OF_BREWABLE ; i++) {
			ret->brewable[i] = malloc(sizeof(Receipt));
			memcpy(ret->brewable[i], solver->brewable[i], sizeof(Receipt));
		}
		for(int i = 0 ; i < players[0]->casts->count ; i++) {
			ret->casts[i] = malloc(sizeof(Receipt));
			memcpy(ret->casts[i], solver->casts[i], sizeof(Receipt));
		}
	}

	return ret;
}

/**
 *
 * Return the heuristique of a transition.
 *
 * Higher is better
 *
 *
 *
 *
 * Example:
 * 	[ 3 0 -3 0 ]	 current_delta
 * 	[ 2 1 -3 0 ]	 next_delta
 * 	[ 0 0 0 0 ]		 destination
 *
 * 	will return: 1
 *
 *  [ 3 0 -3 0 ]	 current_delta
 * 	[ 2 0 -2 0 ]	 next_delta
 * 	[ 0 0 0 0 ]		 destination
 *
 * 	will return: 2
 *
 *
 *  [ 3 0 -3 0 ]	 current_delta
 * 	[ 3 0 -4 1 ]	 next_delta
 * 	[ 0 0 0 0 ]		 destination
 *
 * 	will return: -2
 *
 *
 *
 *
 **/
signed int heuristique(signed int * current_delta, signed int * next_delta, signed int * destination) {
	signed int ret = 0;
	if(DEBUG) {
        fprintf(stderr, "Current Delta: ");
        printDelta(current_delta);
        fprintf(stderr, "Next delta: ");
        printDelta(next_delta);
    }

    signed int missing_ap[4] = {destination[0]-next_delta[0], destination[1]-next_delta[1], destination[2]-next_delta[2], destination[3]-next_delta[3]};
    signed int missing_av[4] = {destination[0]-current_delta[0], destination[1]-current_delta[1], destination[2]-current_delta[2], destination[3]-current_delta[3]};

    if(missing_ap[3] < 0) {
        //missing some at tier 3
        ret += missing_ap[3] - missing_av[3];
        ret += missing_ap[2] - missing_av[2];
        ret += missing_ap[1] - missing_av[1];
        ret += missing_ap[0] - missing_av[0];
    }
     if (missing_ap[2]<0) {
        //missing at tier 2
        ret += missing_ap[2] - missing_av[2];
        ret += missing_ap[1] - missing_av[1];
        ret += missing_ap[0] - missing_av[0];
    }
     if (missing_ap[1] < 0) {
        //missing at tier 1
        ret += missing_ap[1] - missing_av[1];
        ret += missing_ap[0] - missing_av[0];
    }
     if(missing_ap[0] < 0) {
        //missing at tier 0
        ret += missing_ap[0] - missing_av[0];
    }

	if(DEBUG){
        fprintf(stderr, "Heuristique: %d\n", ret);
    }
	return ret;
}


/**
 *
 * Return the next PathNode with repeatable. Could also be REST
 *
 * NULL for arrive
 *
 * */
PathNode* computePathNode(signed int* start, signed int* arrived, Receipt ** castable) {
	if(DEBUG) {
		fprintf(stderr, "Compute next node from: ");
		printDelta(start);
		fprintf(stderr, " arrived: ");
		printDelta(arrived);
	}

	if(compareDelta(start, arrived) == 0) {
		return NULL;
	}
	//Take all cast and look the one who advance the most
	Receipt* best_cast = NULL;
	signed int weight = -32000;
	for(int i = 0 ; i < NUMBER_OF_ACTION ; i++) {
		signed int current_weight = 0;
		Receipt* current_cast = castable[i];
		if(current_cast == NULL)
			break;														//No more cast to test

		if(! isCastPossible(start, current_cast->delta))
			continue;
		signed int * next_delta = sumDeltas(current_cast->delta, start);
		current_weight = heuristique(start, next_delta, arrived);
        if(current_cast->castable == false)
            current_weight -= -1;                                       //Cost for REST1

		if(weight < current_weight) {
			weight = current_weight;
			best_cast = current_cast;
		}
	}

	//if wated cast is not castable then REST and reset cast
	if(best_cast == NULL)
		return NULL;													//No cast found so impossible!

	if(DEBUG) {
		fprintf(stderr, "Best cast: %d weight: %d\n", best_cast->action_id, weight);
	}

	if(!best_cast->castable) {											//Need to rest before the cast
		best_cast=rest_receipt;
		for(int i = 0 ; i < NUMBER_OF_ACTION ; i++) {
			if(castable[i] == NULL)										//Reset castable
				break;
			castable[i]->castable = true;
		}
	}
	else {
		best_cast->castable = false;
	}

	PathNode* ret = malloc(sizeof(PathNode));
	ret->cast = best_cast;
	ret->repeat = 0;
	return ret;
}


/**
 *
 * Compute the path between inventory and Receipt
 *
 **/
void computePath(Path * ret, signed int* inventory, Receipt* destination, Receipt ** castable, int depth_limit) {
    ret->destination = destination;
    ret->length = -1;
	if(DEBUG) {
		fprintf(stderr, "Recherche depuis: ");
		printDelta(inventory);
		fprintf(stderr, "  Destination: ");
		printDelta(destination->delta);
	}
	bool stop = false;
	signed int * current = allocateDelta(destination->delta[0], destination->delta[1], destination->delta[2], destination->delta[3]);
	signed int * inv = allocateDelta(inventory[0], inventory[1], inventory[2], inventory[3]);
	while(stop == false && ret->length < depth_limit) {
        if(DEBUG) {
            fprintf(stderr, "Current elasped: %ld\n", current_elapsed());
        }
		PathNode * next_node = computePathNode(sumDeltas(current,inv), allocateDelta(0, 0, 0, 0), castable);
		if(next_node == NULL) {
            //No path
			stop = true;
		} else {
			current = sumDeltas(current, next_node->cast->delta);
			ret->path_nodes[ret->length + 1] = next_node;
			ret->length = ret->length + 1;
            if(current[0] == 0 && current[1] == 0 && current[2] == 0 && current[3] == 0) {
                PathNode * end = malloc(sizeof(PathNode));
                end->cast=destination;
                end->repeat=0;
                ret->path_nodes[ret->length + 1] = end;
                ret->length = ret->length + 1;
            }
		}
	}

    if(stop == false && INFO) {
        fprintf(stderr, "Maximum Depth\n");
    }
    if(INFO) {
        fprintf(stderr, "\n");
    }
}

/**
 *
 * Permit to compute all path
 *
 **/
void solve(Solver* solver) {
	if(INFO)
		fprintf(stderr, "Solver solve\n");

    int64_t start;
	//For each Brewable
	for(int i = 0 ; i < NUMBER_OF_BREWABLE; i++) {

		Receipt* brew = solver->brewable[i];
		if(INFO) {
			fprintf(stderr, "Start solving receipt: %d\n", brew->action_id);
		}
		start = current_elapsed();
        Path * path_to_brew = malloc(sizeof(Path));
        solver->paths[solver->paths_length+1] = path_to_brew;
        solver->paths_length = solver->paths_length + 1;
		computePath(path_to_brew, solver->start_point, brew, solver->casts, 3000);
        if(INFO) {
            fprintf(stderr, "Computed in %ld nano\n", (current_elapsed()-start));
        }
	}

	if(INFO) {
        fprintf(stderr, "Solver end with %d paths\n", solver->paths_length);
		for(int i = 0 ; i <= solver->paths_length ; i++) {
			Path * p = solver->paths[i];
			fprintf(stderr, "Path to: %d is in %d steps\n", p->destination->action_id, p->length);
		}
	}
}

PathNode* computeNextMove() {
	Solver * solver = allocateSolver(NULL);
	solve(solver);

    Path * f = solver->paths[0];
    if(INFO) {
        fprintf(stderr, "Choosen path length: %d\n", f->length);
    }
	return f->path_nodes[0];    //Path is computed reverse
}



/**************************************************************************
 *
 *
 *
 * Main program
 *
 *
 *
 * *************************************************************************/
int main()
{
	//Init
	wait_receipt = malloc(sizeof(Receipt));
	rest_receipt = malloc(sizeof(Receipt));
	wait_receipt->action = WAIT;
	rest_receipt->action = REST;
	wait_receipt->action_id = 32000;
	rest_receipt->action_id = 32000;
	wait_receipt->repeatable = 0;
	rest_receipt->repeatable = 0;
	wait_receipt->castable = 0;
	rest_receipt->castable = 0;
	wait_receipt->delta = allocateDelta(0, 0, 0, 0);
	rest_receipt->delta = allocateDelta(0, 0, 0, 0);
	players[0] = malloc(sizeof(Player));
	players[1] = malloc(sizeof(Player));
	players[0]->casts = allocateList();
	players[1]->casts = allocateList();

    // game loop
    while (1) {

		players[0]->casts->count = -1;		//Reset cast list
		players[1]->casts->count = -1;

		for(int i = 0 ; i < TOME_SIZE; i++) {
			learnable[i] = NULL;
		}

        // the number of spells and recipes in play
        int action_count;
        scanf("%d", &action_count);
        start_turn();	//Start the timer

        brewable_count = 0;
        for (int i = 0; i < action_count; i++) {
            // the unique ID of this spell or recipe
            int action_id;
            // in the first league: BREW; later: CAST, OPPONENT_CAST, LEARN, BREW
            char action_type[21];
            // tier-0 ingredient change
            signed int delta_0;
            // tier-1 ingredient change
            signed int delta_1;
            // tier-2 ingredient change
            signed int delta_2;
            // tier-3 ingredient change
            signed int delta_3;
            // the price in rupees if this is a potion
            int price;
            // in the first two leagues: always 0; later: the index in the tome if this is a tome spell, equal to the read-ahead tax
            int tome_index;
            // in the first two leagues: always 0; later: the amount of taxed tier-0 ingredients you gain from learning this spell
            int tax_count;
            // in the first league: always 0; later: 1 if this is a castable player spell
            bool castable;
            // for the first two leagues: always 0; later: 1 if this is a repeatable player spell
            bool repeatable;
            int _castable;
            int _repeatable;

            scanf("%d%s%d%d%d%d%d%d%d%d%d", &action_id, action_type, &delta_0, &delta_1, &delta_2, &delta_3, &price, &tome_index, &tax_count, &_castable, &_repeatable);
            castable = _castable;
            repeatable = _repeatable;
            signed int* delta = allocateDelta(delta_0, delta_1, delta_2, delta_3);
            updateOrAllocReceipt(action_id, action_type, delta, price, tome_index, tax_count, castable, repeatable);
        }

		//Update player
        for (int i = 0; i < 2; i++) {
            // tier-0 ingredients in inventory
            int inv_0;
            int inv_1;
            int inv_2;
            int inv_3;
            // amount of rupees
            int score;
            scanf("%d%d%d%d%d", &inv_0, &inv_1, &inv_2, &inv_3, &score);

			updateInventory(players[i], inv_0, inv_1, inv_2, inv_3, score);
        }

        // Write an action using printf(). DON'T FORGET THE TRAILING \n
        // To debug: fprintf(stderr, "Debug messages...\n");

		PathNode* path_node_to_play = computeNextMove();

        // in the first league: BREW <id> | WAIT; later: BREW <id> | CAST <id> [<times>] | LEARN <id> | REST | WAIT
        if(path_node_to_play != NULL) {
            if(path_node_to_play->repeat != 0) {
                printRepeatableAction(path_node_to_play->cast, path_node_to_play->repeat);
            } else {
                printAction(path_node_to_play->cast);
            }
        }
        else {
            printAction(wait_receipt);
        }
    }

    return 0;
}
