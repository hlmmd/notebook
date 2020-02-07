#include <stdio.h>
#include <stdlib.h>

#define list_entry(ptr, type, member) container_of(ptr, type, member)

#define container_of(ptr, type, member) ({ \
const typeof( ((type *)0)->member ) *__mptr = (ptr); \
(type *)( (char *)ptr - offsetof(type,member) ); })

#define offsetof(TYPE, MEMBER) ((size_t) & ((TYPE *)0)->MEMBER)
struct list_head
{
    struct list_head *next, *prev;
};

struct proto
{
    int a;
    int b;
    struct list_head *node;
    int c;
    int d;
};

struct proto_list
{
    struct proto *next;
};

int main()
{
    struct proto List[3];
    List[0].a = 100;
    struct proto_list prolist;

    struct proto *p;

    p = list_entry(&List[0].node, struct proto, node);

    printf("%d\n", p->a);
    printf("%d\n", offsetof(struct proto, node));

    printf("%x\n", (int)p);
    printf("%x\n", (int)&List[0]);

    return 0;
}