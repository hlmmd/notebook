# 深度探索c++对象模型

## 什么是C++对象模型

1.语言中直接支持面向对象程序设计的部分
2.对于各种支持的底层实现机制

c++对象模型的第一个概念是“不变量”

C++class的virtual functions在编译器就确定下来，无法在运行期动态地增加或者取代其中之一。这使得虚拟调用操作能够快速地dispatch结果，付出的成本是执行期的弹性

静态初始化(static initialization)

保证全局变量能在main之前初始化

c++在布局以及存取时间上主要的额外负担是由virtual引起的，包括：virtual functions和virtual base class

## 关于对象

1.1 c++对象模型

non static data member：配置于每个class object之内

static data members : 存放在静态存储区

static 和 non staitc：代码段

Virtual functions的步骤：

每一个class产生一堆指向虚函数的指针，存在虚函数表之中，即vtbl

每一个class object被安插一个指针vptr，指向相关的虚函数表。vptr的设定和重置都由类的构造析构赋值等运算符自动完成，每一个class所关联的type_info object（用于支持RTTI），也存在虚函数表中，放在第一个slot

虚继承

baseclass不管在继承链中被派生多少次，永远只会存在一个实例。

虚继承：扩充虚函数表或者增加一个虚基类表

[https://blog.csdn.net/longlovefilm/article/details/80558879](https://blog.csdn.net/longlovefilm/article/details/80558879)

继承和组合

cast是一种编译器指令，只影响被指的内存大小和类型

切割问题。讲子类类型赋值给父类类型引发切割，此时多态不会呈现。

OO和OB

OO: object oriented 面向对象。更灵活，支持多态

OB: object based 基于对象。效率更高，速度快，结构紧凑，但缺乏弹性。没有virtual

## 构造函数语意学

* implicit
* explicit
* trivial
* nontrivial
* memberwise
* bitwise
* semantics

conversion运算符

Schwarz Error

explicit关键字

阻止单一参数的构造函数被当作一个转换构造函数(conversion)

explicit关键字只对有一个参数的类构造函数有效, 如果类构造函数参数大于或等于两个时, 是不会产生隐式转换的, 所以explicit关键字也就无效了

但是将拷贝构造函数声明成explicit并不是良好的设计，一般只将有单个参数的constructor声明为explicit，而copy constructor不要声明为explicit.

带有默认构造的成员类对象

如果class A内含一个或者一个以上的member class objects,那么class A的每一个constructor必须调用每一个member classes的default constructor。

如果有多个member需要初始化，那么就按照声明顺序依次初始化。

### copy构造

bitwise copy semantic

## Data语意学

对于一个空类，编译器会安插进去一个char，使得一个class的两个object能在内存中获得独一无二的地址

* 1.数据类型自身的对齐值：
对于char型数据，其自身对齐值为1，对于short型为2，对于int,float,double类型，其自身对齐值为4，单位字节。
* 2.结构体或者类的自身对齐值：其成员中自身对齐值最大的那个值。
* 3.指定对齐值：#pragma pack (value)时的指定对齐值value。
* 4.数据成员、结构体和类的有效对齐值：自身对齐值和指定对齐值中小的那个值。

空虚基类