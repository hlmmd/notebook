# c++编程

## noncopyable

继承noncopyable类的派生类不能被拷贝，只能被移动。涉及拷贝的函数有两个：拷贝构造函数和拷贝复制操作符。将这两个方法声明为不可访问或者删除（=delete），即可达到不可拷贝的效果。同理，可以有copyable类

default用于表示默认构造函数。

* 不含参数或者只含一个参数并且参数有默认值的构造函数。
* 如果类没有定义任何构造函数，编译器会为其合成一个无参默认构造函数，也就是合成的默认构造函数。
* 如果类定义了构造函数，编译器不会为其合成默认构造函数，也就不能执行默认初始化了
* 可以指定编译器合成一个默认构造函数，用default关键字。（只能对具有合成版本的成员函数使用此关键字，如默认构造函数和拷贝控制成员）*

class noncopyable的基本思想是把构造函数和析构函数设置protected权限，这样子类可以调用，但是外面的类不能调用，那么当子类需要定义构造函数的时候不至于通不过编译。但是最关键的是noncopyable把拷贝构造函数和拷贝赋值函数做成了private的，继承自noncopyable的类在执行拷贝操作时会调用基类的拷贝操作，但是基类的拷贝操作是private的，因此无法调用，引发编译错误。

```cpp
class noncopyable
{
private:
    noncopyable(const noncopyable &) = delete;
    const noncopyable &operator=(const noncopyable &) = delete;

protected:
    noncopyable() = default;
    ~noncopyable() = default;
};

class copyable
{
 protected:
  copyable() = default;
  ~copyable() = default;
};
```

## SFINAE 

Substitution Failure Is Not An Error，当我们在进行模板特化的时候，如果替换后，代码变成了无效代码，编译器也不应该抛出错误，而是继续寻找其他的替换方案。

这种技术在代码中的一个大的用途就是在编译时期来确定某个 type 是否具有我们需要的性质

```cpp
template <typename T>
struct has_no_destroy
{
    template <typename C>
    static char test(decltype(&C::no_destroy));
    template <typename C>
    static int32_t test(...);
    //根据匹配结果对应函数的返回值类型不同，判断T是否具有no_destroy函数。如果有test返回char，没有则返回int
    const static bool value = sizeof(test<T>(0)) == sizeof(char);
};
```

## 类静态成员变量初始化

**一定要在类外进行初始化，否则编译报错**

## linux 时间戳函数

linux 2038问题。(int 型时间戳)

clock_gettime 精确到ns，优先使用。计算时注意将int转换成int64_t，否则可能溢出

```cpp
struct timespec t_;
int64_t ms_;
int64_t ns_;
int64_t us_;
clock_gettime(CLOCK_MONOTONIC, &t_);
ms_ = static_cast<int64_t>(t_.tv_sec) * 1000 + t_.tv_nsec / 1000000;
us_ = static_cast<int64_t>(t_.tv_sec) * 1000000 + t_.tv_nsec / 1000;
ns_ = static_cast<int64_t>(t_.tv_sec) * 1000000000 + t_.tv_nsec;
```

gettimeofday 精确到us

CLOCK_MONOTONIC: 以绝对时间为准，获取的时间为系统重启到现在的时间，更改系统时间对它没有影响。

CLOCK_REALTIME: 相对时间，从1970.1.1到目前的时间。更改系统时间会更改获取的值。它以系统时间为坐标。

## Condition

条件变量允许我们通过通知进而实现线程同步

条件变量可以履行发送者或接收者的角色。

作为发送者，它可以通知一个或多个接收者。

## CountDownLatch

允许一个或多个线程一直等待，直到其他线程的操作执行完后再执行

CountDownLatch是通过一个计数器来实现的，计数器的初始值为线程的数量。每当一个线程完成了自己的任务后，计数器的值就会减1。当计数器值到达0时，它表示所有的线程已经完成了任务，然后在闭锁上等待的线程就可以恢复执行任务。

```cpp
//Main thread start
//Create CountDownLatch for N threads
//Create and start N threads
//Main thread wait on latch
//N threads completes there tasks are returns
//Main thread resume execution
```