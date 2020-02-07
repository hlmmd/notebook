#include <iostream>
#include <vector>
#include <string>
#include <algorithm>
#include <sstream>
#include <queue>
#include <time.h>
using namespace std;


#define list_entry(ptr, type, member) container_of(ptr, type, member)

#define container_of(ptr, type, member) ({ \
const typeof( ((type *)0)->member ) *__mptr = (ptr); \
(type *)( (char *)__mptr - offsetof(type,member) );})

#define offsetof(TYPE, MEMBER) ((size_t) &((TYPE *)0)->MEMBER)
struct list_head {
	struct list_head *next, *prev;
};

struct proto {
	int a;
	int b;
	list_head * node;
	int c;
	int d;

};

struct proto_list {
	proto *next;
};



int main()
{
	proto List[3];
	proto_list prolist;
	typeof(prolist);
//	const typeof(((proto *)0)->node) *__mptr = (ptr);

//	list_entry(prolist->next, proto, node);

	cout << offsetof(proto, node) << endl;

	system("pause");
	return 0;
}

#include <stdio.h>
#define offsetof(TYPE, MEMBER) ((size_t) &((TYPE *)0)->MEMBER)
#define container_of(ptr, type, member) ({	\
	const typeof( ((type *)0)->member ) *__mptr = (ptr);	\
	(type *)( (char *)__mptr - offsetof(type,member) );})
struct test_struct {
	int num;
	char ch;
	float f1;
};
int main(void)
{
	struct test_struct *test_struct;
	struct test_struct init_struct ={12,'a',12.3};
	char *ptr_ch = &init_struct.ch;
	test_struct =container_of(ptr_ch,struct test_struct,ch);
printf("test_struct->num =%d\n",test_struct->num);
printf("test_struct->ch =%c\n",test_struct->ch);
printf("test_struct->ch =%f\n",test_struct->f1);
	return 0;
}