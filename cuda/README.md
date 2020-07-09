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

## ubuntu

### 下载及编译

使用opencv3.4.2版本。尝试了4.3.0，没有编译成功，没有继续研究了。

[参考教程](https://medium.com/@bnarasapur/compile-opencv-with-cuda-from-the-source-1b98e9108a59)

[opencv-3.4.2](https://github.com/opencv/opencv/archive/3.4.2.tar.gz)

[opencv_contrib-3.4.2](https://github.com/opencv/opencv_contrib/archive/3.4.2.tar.gz)

解压，假设都加压到**~/opencv/**目录下。

```bash
cd ~/opencv/opencv-3.4.2
mkdir build
cd build

cmake -D CMAKE_BUILD_TYPE=RELEASE \
-D CMAKE_INSTALL_PREFIX=/usr/local \
-D WITH_CUDA=ON \
-D ENABLE_FAST_MATH=1 \
-D CUDA_FAST_MATH=1 \
-D WITH_CUBLAS=1 \
-D INSTALL_PYTHON_EXAMPLES=ON \
-D OPENCV_EXTRA_MODULES_PATH=../../opencv_contrib-3.4.2/modules \
-D BUILD_EXAMPLES=ON \
-DBUILD_opencv_cudacodec=OFF ..

make -j 4 #量力而行
sudo make install
sudo ldconfig
```

### 遇到的问题


#### `fatal error: opencv2/xfeatures2d/cuda.hpp: No such file or directory`

解决方法：

直接在报错的地方修改源代码,要改两个地方

文件位置：`~/opencv/opencv-3.4.2/modules/stitching/include/opencv2/stitching/detail/matchers.hpp`

`/home/qinrui/opencv/opencv-3.4.2/samples/gpu/surf_keypoint_matcher.cpp`

```cpp
//matchers.hpp
#ifdef HAVE_OPENCV_XFEATURES2D
//#  include "opencv2/xfeatures2d/cuda.hpp"
#  include "/home/qinrui/opencv/opencv_contrib-3.4.2/modules/xfeatures2d/include/opencv2/xfeatures2d/cuda.hpp"
#endif

//surf_keypoint_matcher.cpp
//#include "opencv2/xfeatures2d/cuda.hpp"
#include "/home/qinrui/opencv/opencv_contrib-3.4.2/modules/xfeatures2d/include/opencv2/xfeatures2d/cuda.hpp"
```

[github issue](https://github.com/opencv/opencv_contrib/issues/1534)

#### 找不到`boostdesc_bgm`等一系列文件

[github issue](https://github.com/opencv/opencv_contrib/issues/1301)

在cmake的时候会下载，但国内访问github很慢，容易下载失败，所以手动下载，再将文件放到`~/opencv/opencv_contrib-3.4.2/modules/xfeatures2d/src`目录中。

[可以直接用我这个](http://qch3ajwsl.bkt.clouddn.com/opencv_files.zip)

#### `undefined reference to `cv::cuda::SURF_CUDA::SURF_CUDA()`

缺少了链接。修改`<build_dir>/samples/gpu/CMakeFiles/example_gpu_surf_keypoint_matcher.dir/link.txt `，添加两个链接

```bash
CMakeFiles/example_gpu_surf_keypoint_matcher.dir/surf_keypoint_matcher.cpp.o 
../../modules/xfeatures2d/CMakeFiles/opencv_xfeatures2d.dir/src/surf.cuda.cpp.o 
../../modules/xfeatures2d/CMakeFiles/cuda_compile.dir/src/cuda/cuda_compile_generated_surf.cu.o ……
```

### 测试代码

通过一个简单的例子，测试opencv with cuda是否安装成功。

```cpp
#include <iostream>
#include "opencv2/opencv.hpp"
int main(int argc, char *argv[])
{
    //Read Two Images
    cv::Mat h_img1 = cv::imread("images/cameraman.tif");
    cv::Mat h_img2 = cv::imread("images/circles.png");
    //Create Memory for storing Images on device
    cv::cuda::GpuMat d_result1, d_img1, d_img2;
    cv::Mat h_result1;
    //Upload Images to device
    d_img1.upload(h_img1);
    d_img2.upload(h_img2);
    cv::cuda::add(d_img1, d_img2, d_result1);
    //Download Result back to host
    d_result1.download(h_result1);
    cv::imshow("Image1 ", h_img1);
    cv::imshow("Image2 ", h_img2);
    cv::imshow("Result addition ", h_result1);
    cv::imwrite("images/result_add.png", h_result1);
    cv::waitKey();
    return 0;
}
```

编译命令：

```bash
g++ -std=c++11 test.cpp `pkg-config opencv --libs --cflags` -o image_read
```