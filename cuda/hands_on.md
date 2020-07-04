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

