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