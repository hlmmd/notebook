# cuda thrust

## gitbub wiki

### 简介

Thrust是基于标准模板库（STL）的并行平台的C ++模板库。 通过Thrust，您可以通过与C ++，CUDA，OpenMP和TBB等技术完全可互操作的高级界面，以最少的编程工作来实现高性能并行应用程序。

在cuda4.0之后的版本中，被集成到cuda toolkit中，无需额外安装

### 查看thrust版本

```cpp
#include <thrust/version.h>
#include <iostream>

int main(void)
{
  int major = THRUST_MAJOR_VERSION;
  int minor = THRUST_MINOR_VERSION;

  std::cout << "Thrust v" << major << "." << minor << std::endl;

  return 0;
}
```

### Vectors

Thrust提供两种vector类型，host_vector和device_vector。分别位于CPU和GPU上。特性类似std::vector

```cpp
#include <thrust/host_vector.h>
#include <thrust/device_vector.h>

#include <iostream>

int main(void)
{
  // H has storage for 4 integers
  thrust::host_vector<int> H(4);

  // initialize individual elements
  H[0] = 14;
  H[1] = 20;
  H[2] = 38;
  H[3] = 46;
    
  // H.size() returns the size of vector H
  std::cout << "H has size " << H.size() << std::endl;

  // print contents of H
  for(int i = 0; i < H.size(); i++)
  {
    std::cout << "H[" << i << "] = " << H[i] << std::endl;
  }

  // resize H
  H.resize(2);
    
  std::cout << "H now has size " << H.size() << std::endl;

  // Copy host_vector H to device_vector D
  thrust::device_vector<int> D = H;
    
  // elements of D can be modified
  D[0] = 99;
  D[1] = 88;
    
  // print contents of D
  for(int i = 0; i < D.size(); i++)
  {
    std::cout << "D[" << i << "] = " << D[i] << std::endl;
  }

  // H and D are automatically destroyed when the function returns
  return 0;
}
```

特性：host_vector和device_vector可以通过=运算符互相赋值

device_vector可以通过[]运算符直接赋值。（每次都需要调用cudaMemcpy，谨慎使用）

```cpp
#include <thrust/host_vector.h>
#include <thrust/device_vector.h>

#include <thrust/copy.h>
#include <thrust/fill.h>
#include <thrust/sequence.h>

#include <iostream>

int main(void)
{
  // initialize all ten integers of a device_vector to 1
  thrust::device_vector<int> D(10, 1);

  // set the first seven elements of a vector to 9
  thrust::fill(D.begin(), D.begin() + 7, 9);

  // initialize a host_vector with the first five elements of D
  thrust::host_vector<int> H(D.begin(), D.begin() + 5);

  // set the elements of H to 0, 1, 2, 3, ...
  thrust::sequence(H.begin(), H.end());

  // copy all of H back to the beginning of D
  thrust::copy(H.begin(), H.end(), D.begin());

  // print D
  for(int i = 0; i < D.size(); i++)
  {
    std::cout << "D[" << i << "] = " << D[i] << std::endl;
  }

  return 0;
}
```

可以通过fill, copy, sequence函数给host_vector赋值



