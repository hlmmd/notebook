# cuda

## win 10 CUDA安装

### cuda toolkit下载

目前最新版本是11.0

[https://developer.nvidia.com/cuda-toolkit](https://developer.nvidia.com/cuda-toolkit)

我安装的是[10.2版本](https://developer.nvidia.com/cuda-10.2-download-archive)

选择合适的cuda版本

![](https://raw.githubusercontent.com/hlmmd/cdnstore/master/2020/win10_cuda_setup/cuda_download.png)

在cmd中输出set cuda查看CUDA_PATH

![](https://raw.githubusercontent.com/hlmmd/cdnstore/master/2020/win10_cuda_setup/cuda_path.png)

### 添加CUDA文件

右键源文件文件夹->添加->新建项->选择CUDA C/C++File

![](https://raw.githubusercontent.com/hlmmd/cdnstore/master/2020/win10_cuda_setup/add_cudafile.png)

右键项目->生成依赖项->生成自定义，勾选CUDA

![](https://raw.githubusercontent.com/hlmmd/cdnstore/master/2020/win10_cuda_setup/cuda_config.png)

设置.cu文件属性，项类型设置为CUDA C/C++

![](https://raw.githubusercontent.com/hlmmd/cdnstore/master/2020/win10_cuda_setup/cuda_property.png)

### 项目配置

设置项目平台为x64

设置include目录：

右键点击项目属性–>配置属性–>VC++目录–>包含目录，添加

`$(CUDA_PATH)\include`

右键点击项目属性–>配置属性–>VC++目录–>库目录，添加

`$(CUDA_PATH)\lib\x64`

链接器->输入，添加

```bash
cublas.lib
cuda.lib
cudadevrt.lib
cudart.lib
cudart_static.lib
OpenCL.lib
```

此时vs能顺利编译运行cuda项目

## opencv cuda

### 源码下载

[OpenCV源码](https://github.com/opencv/opencv)，当前最新版本为[4.3.0](https://github.com/opencv/opencv/archive/4.3.0.tar.gz)

[opencv_contrib源码](https://github.com/opencv/opencv_contrib)，[4.3.0](https://github.com/opencv/opencv_contrib/archive/4.3.0.tar.gz)

[cmake](https://cmake.org/download/)

将源码进行解压，例解压至`E:\opencv\opencv-4.3.0`

### 使用cmake和vs编译源码

在cmake中设置好源码路径和build路径，点击configure。

configure完成后，找到OPENCV_EXTRA_MODULES_PATH，将value设置为opencv_contrib\modules路径。重新configure一次。

勾选cuda相关选项，点击generate。此时build目录下会产生OpenCV.sln，使用vs打开。

在解决方案中，选择cmakeTargets->build All，右键生成。注意debug和release版本。

编译生成的文件在build/install目录下。