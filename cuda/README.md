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