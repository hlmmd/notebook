#include "cuda_runtime.h"
#include "device_launch_parameters.h"
#include <stdio.h>
#include "cube.cuh"
 
__global__ void cube_core(int *dev_a, int *dev_b){
	int tid=blockIdx.x;
	int tmp=*(dev_a+tid);
	*(dev_b+tid)=tmp*tmp*tmp;
}
 
void cube(int result[], int n){
	int a[n];
	for(int i=0;i<n;i++){
		a[i]=i;	
	}	
	int *dev_a=NULL;
	int *dev_b=NULL;
	cudaMalloc((void**)&dev_a,n*sizeof(int));
	cudaMemset((void**)&dev_a,0,n*sizeof(int));
	cudaMalloc((void**)&dev_b,n*sizeof(int));
	cudaMemset((void**)&dev_b,0,n*sizeof(int));
	cudaMemcpy(dev_a,(void**)&a,n*sizeof(int),cudaMemcpyHostToDevice);
	cube_core<<<n,1>>>(dev_a,dev_b);
	cudaMemcpy((void **)&result[0],dev_b,n*sizeof(int),cudaMemcpyDeviceToHost);
	printf("finish\n");
}