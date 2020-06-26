#include <stdio.h>
extern "C" void cube(int *a, int n);
//extern "C" void print(int *a, int n);

int main()
{
    int N = 10;
    int a[10];
    cube(a, N);

    for (int i = 0; i < N; i++)
        printf("%d\n", a[i]);

    //print(&a[0], N);
    return 0;
}