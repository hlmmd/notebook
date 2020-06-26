# GPU高性能编程CUDA实战

## CUDA C 简介

`__global__` 修饰符，表示该函数运行在设备上

cudaMalloc函数用于在设备上申请内存，第一个参数是void**（同malloc的返回值），第二个参数是申请空间大小。

cudaFree()用于释放空间，其行为和free()函数相同

主机代码中不能对cudaMalloc申请获得的指针进行解引用

访问设备内存的两种方式：
* 在设备代码中调用
* 使用cudaMemcpy()函数

cudaMemcpy()
* 目的指针
* 源指针
* 大小
* 复制类型cudaMemcpyHostToDevice，cudaMemcpyDeviceToHost，cudaMemcpyDeviceToDevice

```c++
__global__ void add(int a ,int b,int *c) {
	*c = a + b;
}

int main()
{
	int c;
	int* dev_c;
	cudaMalloc((void**)&dev_c, sizeof(int));
	add << <1, 1 >> > (7, 2, dev_c);
	cudaMemcpy(&c, dev_c, sizeof(int), cudaMemcpyDeviceToHost);
	cudaFree(dev_c);
	printf("%d\n",c);
	return 0;
}
```

cudaGetDeviceCount(int * count)能获取CUDA设备的数量

cudaDeviceProp结构体保存了设备属性，可以通过cudaGetDeviceProperties获取

## cuda C 并行编程

核函数调用时，尖括号里第一个参数表示设备在执行和函数时使用的并行线程块的数量。

那么，代码中如何知道正在运行的是哪一个线程块？使用内置变量blockIdx.x

```cpp
__global__ void add(int* a, int* b, int* c) {
    int tid = blockIdx.x;    // this thread handles the data at its thread id
    if (tid < N)
        c[tid] = a[tid] + b[tid];
}
```

`__device__`修饰符：表示代码在GPU上运行，只能由其他device或者global函数调用

内置变量gridDim 线程格每一维的大小(gridDim.x,gridDim.y)

## 线程协作

核函数调用时，尖括号内的第二个参数是每个线程块启动的线程数量

线程索引threadIdx ， (threadIdx.x,threadIdx.y)

我的显卡1060的规格是

```text
Max threads per block:  1024
Max thread dimensions:  (1024, 1024, 64)
Max grid dimensions:  (2147483647, 65535, 65535)
```

二位索引转换为线性索引的算法：

int tid = threadIdx.x+blockIdx.x*blockDim.x;

对每一个线程格，blockDim保存了每一维的线程数量

```c++
__global__ void add(int* a, int* b, int* c)
{
    int tid = threadIdx.x + blockIdx.x * blockDim.x;
    while (tid < N)
    {
        c[tid] = a[tid] + b[tid];
        tid += blockDim.x * gridDim.x;
    }
}
```

二维索引的计算

```
int x = threadIdx.x + blockIdx.x * blockDim.x;
int y = threadIdx.y + blockIdx.y * blockDim.y;
int offset = x + y * blockDim.x * gridDim.x;
```

共享内存和同步

`__share__`关键字用于定义共享内存变量

`__syncthreads()` 确保线程块中的每个线程都执行完该函数之前的语句后，才会执行下一条语句

线程发散：当某些线程需要执行一条指令，其他线程不需要执行时，成为线程发散

```cpp
__global__ void dot(float* a, float* b, float* c)
{
	__shared__ float cache[threadsPerBlock];
	int tid = threadIdx.x + blockIdx.x * blockDim.x;
	int cacheIndex = threadIdx.x;

	float temp = 0;
	while (tid < N)
	{
		temp += a[tid] * b[tid];
		tid += blockDim.x * gridDim.x;
	}

	cache[cacheIndex] = temp;

	__syncthreads();

	int i = blockDim.x / 2;
	while (i != 0)
	{
		if (cacheIndex < i)
			cache[cacheIndex] += cache[cacheIndex + i];
		__syncthreads();
		i /= 2;
	}
	if (cacheIndex == 0)
		c[blockIdx.x] = cache[0];
}
```

## 常量内存与事件

`__constant__`定义常量

`cudaMemcpyToSymbol()` 用于取代cudaMemcpy，用于给cuda常量内存赋值

常量内存读取相同的数据可以节约内存带宽
* 对常量内存的单次读操作可以广播到其他临近的线程，节约15次读取
* 常量内存的数据将缓存起来，因此对相同地址的连续读操作不会产生额外的内存通信量

使用事件来测量性能

CUDA事件本质上是一个时间戳

```cpp
cudaEvent_t start, stop;

cudaEventCreate(&start);
cudaEventCreate(&stop);
cudaEventRecord(start, 0);

//something happened

cudaEventRecord(stop, 0);
cudaEventSynchronize(stop);
float elapsedTime;
cudaEventElapsedTime(&elapsedTime, start, stop);
printf("time : %f ms\n", elapsedTime);
cudaEventDestroy(start);
cudaEventDestroy(stop);
```

cudaEventSynchronize() 事件同步函数，保证在某个事件之前的所有GPU工作都完成了