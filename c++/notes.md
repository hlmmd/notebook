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

## volatile

Volatile关键词的第一个特性：易变性。所谓的易变性，在汇编层面反映出来，就是两条语句，下一条语句不会直接使用上一条语句对应的volatile变量的寄存器内容，而是重新从内存中读取。

Volatile关键词的第二个特性：“不可优化”特性。volatile告诉编译器，不要对我这个变量进行各种激进的优化，甚至将变量直接消除，保证程序员写在代码中的指令，一定会被执行。

volatile顺序性：
* C/C++ Volatile变量，与非Volatile变量之间的操作，是可能被编译器交换顺序的。
* C/C++ Volatile变量间的操作，是不会被编译器交换顺序的。

第三个特性：”顺序性”，能够保证Volatile变量间的顺序性，编译器不会进行乱序优化。

## __thread

__thread是GCC内置的线程局部存储设施，存取效率可以和全局变量相比。

__thread变量每一个线程有一份独立实体，各个线程的值互不干扰。可以用来修饰那些带有全局性且值可能变，但是又不值得用全局变量保护的变量。

只能修饰POD类型(类似整型指针的标量，不带自定义的构造、拷贝、赋值、析构的类型，二进制内容可以任意复制memset,memcpy,且内容可以复原)

不能修饰class类型，因为无法自动调用构造函数和析构函数，可以用于修饰全局变量，函数内的静态变量，不能修饰函数的局部变量或者class的普通成员变量，且__thread变量值只能初始化为编译期常量，即编译期间就能确定值。

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

## 右值引用 折叠引用 

左值和右值

右值引用。c++11标准提供右值引用。

```cpp
A a1 = GetA();   // a1是左值
A&& a2 = GetA(); // a2是右值引用
```

a1是左值，在构造时使用了GetA() 产生的临时对象，之后GetA()产生的临时对象会销毁。

a2是右值引用，其指向的就是GetA()所产生的对象，这个对象的声明周期是和a2的声明周期是一致的。即少了临时对象，从而省去了临时对象的构造和析构。

纯右值引用折叠后才是右值引用，只要有一个左值引用，就是左值引用。

```
A& & 变成 A&
A& && 变成 A&
A&& & 变成 A&
A&& && 变成 A&&
```

## move 

移动构造函数。

类不会生成移动构造函数，在传入右值引用时，如果未定义则匹配左值引用，即复制构造函数

std::move  将一个左值强制转化为右值引用，继而可以通过右值引用使用该值，以用于移动语义。

移动构造函数和移动赋值函数定义方法如下

```cpp
Thread(Thread &&t);
Thread &operator=(Thread &&t);
```

## forward 完美转发

不管是T&&、左值引用、右值引用，std::forward都会按照原来的类型完美转发

## remove_reference

## 线程池

ThreadPool.cc中，start函数确定了初始启动线程数量（固定，不会自动扩容缩容）

双端队列queue_用于存储task，notEmpty_条件变量用来通知线程池执行task，notFull_通知线程池可以继续添加任务。

## std::bind

在bind中，如果要传递一个引用需要使用std::ref，常量引用为std::cref

## using 

构造函数的 using 声明
在 C++11 中，派生类能够重用其直接基类定义的构造函数。

```cpp
class Derived : Base {
public:
    using Base::Base;
    /* ... */
};
```

如上 using 声明，对于基类的每个构造函数，编译器都生成一个与之对应（形参列表完全相同）的派生类构造函数。生成如下类型构造函数：

```cpp
Derived(parms) : Base(args) { }
```

## decltype

decltype 关键字用于检查实体的声明类型或表达式的类型及值分类。语法：

```cpp
decltype ( expression )
```

decltype 使用

```cpp
// 尾置返回允许我们在参数列表之后声明返回类型
template <typename It>
auto fcn(It beg, It end) -> decltype(*beg)
{
    // 处理序列
    return *beg;    // 返回序列中一个元素的引用
}
// 为了使用模板参数成员，必须用 typename
template <typename It>
auto fcn2(It beg, It end) -> typename remove_reference<decltype(*beg)>::type
{
    // 处理序列
    return *beg;    // 返回序列中一个元素的拷贝
}
```

## initializer_list 列表初始化

用花括号初始化器列表初始化一个对象，其中对应构造函数接受一个 std::initializer_list 参数.

initializer_list 使用

```cpp
#include <iostream>
#include <vector>
#include <initializer_list>
 
template <class T>
struct S {
    std::vector<T> v;
    S(std::initializer_list<T> l) : v(l) {
         std::cout << "constructed with a " << l.size() << "-element list\n";
    }
    void append(std::initializer_list<T> l) {
        v.insert(v.end(), l.begin(), l.end());
    }
    std::pair<const T*, std::size_t> c_arr() const {
        return {&v[0], v.size()};  // 在 return 语句中复制列表初始化
                                   // 这不使用 std::initializer_list
    }
};
 
template <typename T>
void templated_fn(T) {}
 
int main()
{
    S<int> s = {1, 2, 3, 4, 5}; // 复制初始化
    s.append({6, 7, 8});      // 函数调用中的列表初始化
 
    std::cout << "The vector size is now " << s.c_arr().second << " ints:\n";
 
    for (auto n : s.v)
        std::cout << n << ' ';
    std::cout << '\n';
 
    std::cout << "Range-for over brace-init-list: \n";
 
    for (int x : {-1, -2, -3}) // auto 的规则令此带范围 for 工作
        std::cout << x << ' ';
    std::cout << '\n';
 
    auto al = {10, 11, 12};   // auto 的特殊规则
 
    std::cout << "The list bound to auto has size() = " << al.size() << '\n';
 
//    templated_fn({1, 2, 3}); // 编译错误！“ {1, 2, 3} ”不是表达式，
                             // 它无类型，故 T 无法推导
    templated_fn<std::initializer_list<int>>({1, 2, 3}); // OK
    templated_fn<std::vector<int>>({1, 2, 3});           // 也 OK
}
```

## 如何定义一个只能在堆上（栈上）生成对象的类？

### 只能在堆上

方法：将析构函数设置为私有

原因：C++ 是静态绑定语言，编译器管理栈上对象的生命周期，编译器在为类对象分配栈空间时，会先检查类的析构函数的访问性。若析构函数不可访问，则不能在栈上创建对象。

### 只能在栈上

方法：将 new 和 delete 重载为私有

原因：在堆上生成对象，使用 new 关键词操作，其过程分为两阶段：第一阶段，使用 new 在堆上寻找可用内存，分配给对象；第二阶段，调用构造函数生成对象。将 new 操作设置为私有，那么第一阶段就无法完成，就不能够在堆上生成对象。

## 智能指针

```cpp
#include <memory>
```

### shared_ptr

多个智能指针可以共享同一个对象，对象的最末一个拥有着有责任销毁对象，并清理与该对象相关的所有资源。

支持定制型删除器（custom deleter），可防范 Cross-DLL 问题（对象在动态链接库（DLL）中被 new 创建，却在另一个 DLL 内被 delete 销毁）、自动解除互斥锁

### weak_ptr

weak_ptr 允许你共享但不拥有某对象，一旦最末一个拥有该对象的智能指针失去了所有权，任何 weak_ptr 都会自动成空（empty）。因此，在 default 和 copy 构造函数之外，weak_ptr 只提供 “接受一个 shared_ptr” 的构造函数。

可打破环状引用（cycles of references，两个其实已经没有被使用的对象彼此互指，使之看似还在 “被使用” 的状态）的问题

### unique_ptr

unique_ptr 是 C++11 才开始提供的类型，是一种在异常时可以帮助避免资源泄漏的智能指针。采用独占式拥有，意味着可以确保一个对象和其相应的资源同一时间只被一个 pointer 拥有。一旦拥有着被销毁或编程 empty，或开始拥有另一个对象，先前拥有的那个对象就会被销毁，其任何相应资源亦会被释放。

unique_ptr 用于取代 auto_ptr

## 强制类型转换运算符

static_cast
dynamic_cast
const_cast
reinterpret_cast

## 运行时类型信息 (RTTI)

### dynamic_cast
用于多态类型的转换

### typeid
typeid 运算符允许在运行时确定对象的类型

type_id 返回一个 type_info 对象的引用

如果想通过基类的指针获得派生类的数据类型，基类必须带有虚函数
只能获取对象的实际类型

### type_info

type_info 类描述编译器在程序中生成的类型信息。 此类的对象可以有效存储指向类型的名称的指针。 type_info 类还可存储适合比较两个类型是否相等或比较其排列顺序的编码值。 类型的编码规则和排列顺序是未指定的，并且可能因程序而异。

头文件：typeinfo

### typeid、type_info 使用

```cpp
#include <iostream>
using namespace std;

class Flyable                       // 能飞的
{
public:
    virtual void takeoff() = 0;     // 起飞
    virtual void land() = 0;        // 降落
};
class Bird : public Flyable         // 鸟
{
public:
    void foraging() {...}           // 觅食
    virtual void takeoff() {...}
    virtual void land() {...}
    virtual ~Bird(){}
};
class Plane : public Flyable        // 飞机
{
public:
    void carry() {...}              // 运输
    virtual void takeoff() {...}
    virtual void land() {...}
};

class type_info
{
public:
    const char* name() const;
    bool operator == (const type_info & rhs) const;
    bool operator != (const type_info & rhs) const;
    int before(const type_info & rhs) const;
    virtual ~type_info();
private:
    ...
};

void doSomething(Flyable *obj)                 // 做些事情
{
    obj->takeoff();

    cout << typeid(*obj).name() << endl;        // 输出传入对象类型（"class Bird" or "class Plane"）

    if(typeid(*obj) == typeid(Bird))            // 判断对象类型
    {
        Bird *bird = dynamic_cast<Bird *>(obj); // 对象转化
        bird->foraging();
    }

    obj->land();
}

int main(){
	Bird *b = new Bird();
	doSomething(b);
	delete b;
	b = nullptr;
	return 0;
}
```

