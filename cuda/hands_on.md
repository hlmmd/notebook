# Hands On GPU Accelerated Computer Vision with openCV and CUDA

[code](https://github.com/PacktPublishing/Hands-On-GPU-Accelerated-Computer-Vision-with-OpenCV-and-CUDA)
[video](https://www.youtube.com/playlist?list=PLTgRMOcmRb3PmzLcoRAqCdC_Q2kLKSvIX&disable_polymer=true)

## get started

## Parallel Programming using CUDA C

cudaDeviceSynchronize()

## Threads, Synchronization, and Memory

内存模型


Memory Access Pattern Speed Cached? Scope Lifetime
Global Read and Write Slow Yes Host and All Threads Entire Program
Local Read and Write Slow Yes Each Thread Thread
Registers Read and Write Fast - Each Thread Thread
Shared Read and Write Fast No Each Block Block
Constant Read only Slow Yes Host and All Threads Entire Program
Texture Read only Slow Yes Host and All Threads Entire Program

### Global memory

All memories allocated using cudaMalloc will be a global memory.

### Local memory and registers

### Cache memory

### Thread synchronization

### Shared memory

### Atomic operations

atomicAdd

### Constant memory

NVIDIA hardware provides 64 KB of this constant memory

cudaMemcpyToSymbol

## Advanced Concepts in CUDA

### CUDA Events

### Error handling in CUDA

CUDA-GDB

## Getting Started with OpenCV with CUDA Support

### Installation of OpenCV with CUDA support

### Basic Computer Vision Operations Using OpenCV and CUDA