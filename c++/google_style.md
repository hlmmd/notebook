# google c++ program style

## 1. 头文件

通常每一个 .cc 文件都有一个对应的 .h 文件.


### Self-contained 头文件

头文件应该能够自给自足（self-contained,也就是可以作为第一个头文件被引入），以 .h 结尾。至于用来插入文本的文件，说到底它们并不是头文件，所以应以 .inc 结尾。不允许分离出 -inl.h 头文件的做法.

### 1.3. 前置声明

尽可能地避免使用前置声明。使用 #include 包含需要的头文件即可。

### 1.4. 内联函数

只有当函数只有 10 行甚至更少时才将其定义为内联函数.

### 1.5. #include 的路径及顺序

使用标准的头文件包含顺序可增强可读性, 避免隐藏依赖: 

* 相关头文件
* C 库
* C++ 库
* 其他库的 .h
* 本项目内的 .h.

## 2. 作用域

### 2.1. 命名空间

鼓励在 .cc 文件内使用匿名命名空间或 static 声明. 使用具名的命名空间时, 其名称可基于项目名或相对路径. 禁止使用 using 指示（using-directive）。禁止使用内联命名空间（inline namespace）。

内联命名空间会自动把内部的标识符放到外层作用域，比如：

```cpp
namespace X {
inline namespace Y {
void foo();
}  // namespace Y
}  // namespace X
```

X::Y::foo() 与 X::foo() 彼此可代替。内联命名空间主要用来保持跨版本的 ABI 兼容性。

### 2.2. 匿名命名空间和静态变量

在 .cc 文件中定义一个不需要被外部引用的变量时，可以将它们放在匿名命名空间或声明为 static 。但是不要在 .h 文件中这么做。

### 2.3. 非成员函数、静态成员函数和全局函数

### 2.4. 局部变量

将函数变量尽可能置于最小作用域内, 并在变量声明时进行初始化.

有一个例外, 如果变量是一个对象, 每次进入作用域都要调用其构造函数, 每次退出作用域都要调用其析构函数. 这会导致效率降低.

```cpp
// 低效的实现
for (int i = 0; i < 1000000; ++i) {
    Foo f;                  // 构造函数和析构函数分别调用 1000000 次!
    f.DoSomething(i);
}
```

### 2.5. 静态和全局变量

禁止定义静态储存周期非POD变量，禁止使用含有副作用的函数初始化POD全局变量，因为多编译单元中的静态变量执行时的构造和析构顺序是未明确的，这将导致代码的不可移植。

POD : Plain Old Data): 即 int, char 和 float, 以及 POD 类型的指针、数组和结构体

同一个编译单元内是明确的，静态初始化优先于动态初始化，初始化顺序按照声明顺序进行，销毁则逆序。不同的编译单元之间初始化和销毁顺序属于未明确行为 (unspecified behaviour)。

综上所述，我们只允许 POD 类型的静态变量，即完全禁用 vector (使用 C 数组替代) 和 string (使用 const char [])。

## 3. 类

### 3.1. 构造函数的职责

构造函数不允许调用虚函数. 如果代码允许, 直接终止程序是一个合适的处理错误的方式. 否则, 考虑用 Init() 方法或工厂函数.

### 3.2. 隐式类型转换

不要定义隐式类型转换. 对于转换运算符和单参数构造函数, 请使用 explicit 关键字.

在类型定义中, 类型转换运算符和单参数构造函数都应当用 explicit 进行标记. 

不能以一个参数进行调用的构造函数不应当加上 explicit. 接受一个 std::initializer_list 作为参数的构造函数也应当省略 explicit, 以便支持拷贝初始化 (例如 MyType m = {1, 2};) 

### 3.3. 可拷贝类型和可移动类型

如果你的类型需要, 就让它们支持拷贝 / 移动. 否则, 就把隐式产生的拷贝和移动函数禁用.


### 3.4. 结构体 VS. 类

仅当只有数据成员时使用 struct, 其它一概使用 class.

### 3.5. 继承

使用组合常常比使用继承更合理. 如果使用继承的话, 定义为 public 继承.

不要过度使用实现继承. 组合常常更合适一些. 尽量做到只在 “is-a” 的情况下使用继承.

在声明重载时, 请使用 override, final 或 virtual 的其中之一进行标记.

### 3.6. 多重继承

只在以下情况我们才允许多重继承: 最多只有一个基类是非抽象类; 其它基类都是以 Interface 为后缀的 纯接口类.

### 3.7. 接口

接口是指满足特定条件的类, 这些类以 Interface 为后缀 (不强制).

当一个类满足以下要求时, 称之为纯接口:

* 只有纯虚函数 (“=0”) 和静态函数 (除了下文提到的析构函数).
* 没有非静态数据成员.
* 没有定义任何构造函数. 如果有, 也不能带有参数, 并且必须为 protected.
* 如果它是一个子类, 也只能从满足上述条件并以 Interface 为后缀的类继承.

### 3.8. 运算符重载

除少数特定环境外, 不要重载运算符. 也不要创建用户定义字面量

### 3.9. 存取控制

将 所有 数据成员声明为 private, 除非是 static const 类型成员

### 3.10. 声明顺序

类定义一般应以 public: 开始, 后跟 protected:, 最后是 private:. 省略空部分.

在各个部分中, 建议将类似的声明放在一起, 并且建议以如下的顺序: 类型 (包括 typedef, using 和嵌套的结构体与类), 常量, 工厂函数, 构造函数, 赋值运算符, 析构函数, 其它函数, 数据成员.

## 4. 函数

### 4.1. 参数顺序

函数的参数顺序为: 输入参数在先, 后跟输出参数.

### 4.2. 编写简短函数

一般以40行为标准

### 4.3. 引用参数

所有按引用传递的参数必须加上 const.

### 4.4. 函数重载

若要使用函数重载, 则必须能让读者一看调用点就胸有成竹, 而不用花心思猜测调用的重载函数到底是哪一种. 这一规则也适用于构造函数.

### 4.5. 缺省参数

只允许在非虚函数中使用缺省参数, 且必须保证缺省参数的值始终一致

### 4.6. 函数返回类型后置语法

后置返回类型是显式地指定 Lambda 表达式 的返回值的唯一方式

常用于模板

```cpp
template <class T, class U> auto add(T t, U u) -> decltype(t + u);
```

## 5. 来自 Google 的奇技

### 5.1. 所有权与智能指针

动态分配出的对象最好有单一且固定的所有主, 并通过智能指针传递所有权.

### 5.2. Cpplint

使用 cpplint.py 检查风格错误.

[http://github.com/google/styleguide/blob/gh-pages/cpplint/cpplint.py](http://github.com/google/styleguide/blob/gh-pages/cpplint/cpplint.py)

## 6. 其他 C++ 特性

### 6.1. 引用参数

所有按引用传递的参数必须加上 const.

### 6.2. 右值引用

只在定义移动构造函数与移动赋值操作时使用右值引用. 不要使用 std::forward.

### 6.3. 函数重载

若要用好函数重载，最好能让读者一看调用点（call site）就胸有成竹，不用花心思猜测调用的重载函数到底是哪一种。该规则适用于构造函数。

### 6.4. 缺省参数

我们不允许使用缺省函数参数，少数极端情况除外。尽可能改用函数重载。

### 6.5. 变长数组和 alloca()

不允许使用变长数组和 alloca().

改用更安全的分配器（allocator），就像 std::vector 或 std::unique_ptr<T[]>.

### 6.6. 友元

允许合理的使用友元类及友元函数.

### **6.7. 异常**

goolge 不使用异常

[https://zh-google-styleguide.readthedocs.io/en/latest/google-cpp-styleguide/others/#id5](https://zh-google-styleguide.readthedocs.io/en/latest/google-cpp-styleguide/others/#id5)

### **6.8. 运行时类型识别**

RTTI 允许程序员在运行时识别 C++ 类对象的类型. 它通过使用 typeid 或者 dynamic_cast 完成.

google不使用。

### 6.9. 类型转换

使用 C++ 的类型转换, 如 static_cast<>(). 

### 6.10. 流

只在记录日志时使用流.

### 6.11. 前置自增和自减

对简单数值 (非对象), 两种都无所谓. 对迭代器和模板类型, 使用前置自增 (自减).

### 6.12. const 用法

任何可能的情况下都要使用 const. 此外有时改用 C++11 推出的 constexpr 更好。

 为类中的函数加上 const 限定符表明该函数不会修改类成员变量的状态

 ### 6.13. constexpr 用法

 变量可以被声明成 constexpr 以表示它是真正意义上的常量，即在编译时和运行时都不变。函数或构造函数也可以被声明成 constexpr, 以用来定义 constexpr 变量。

 ### 6.14. 整型

 <stdint.h> 定义了 int16_t, uint32_t, int64_t 等整型

比较有符合变量和无符号变量 引发bug

使用断言来指出变量为非负数, 而不是使用无符号型

### 6.15. 64 位下的可移植性

### 6.16. 预处理宏

使用宏时要非常谨慎, 尽量以内联函数, 枚举和常量代替之.

* 不要在 .h 文件中定义宏.
* 在马上要使用时才进行 #define, 使用后要立即 #undef.
* 不要只是对已经存在的宏使用#undef，选择一个不会冲突的名称；
* 不要试图使用展开后会导致 C++ 构造不稳定的宏, 不然也至少要附上文档说明其行为.
* 不要用 ## 处理函数，类和变量的名字。

### 6.17. 0, nullptr 和 NULL

整数用 0, 实数用 0.0, 指针用 nullptr 或 NULL, 字符 (串) 用 '\0'.

一些 C++ 编译器对 NULL 的定义比较特殊，可以输出有用的警告

### 6.18. sizeof

尽可能用 sizeof(varname) 代替 sizeof(type).

### 6.19. auto

auto 只能用在局部变量里用。别用在文件作用域变量，命名空间作用域变量和类数据成员里。永远别列表初始化 auto 变量。

### 6.20. 列表初始化

### 6.21. Lambda 表达式

适当使用 lambda 表达式。别用默认 lambda 捕获，所有捕获都要显式写出来。

Lambda 表达式是创建匿名函数对象的一种简易途径，常用于把函数当参数传，例如：

```cpp
std::sort(v.begin(), v.end(), [](int x, int y) {
    return Weight(x) < Weight(y);
});
```

* 按 format 小用 lambda 表达式怡情。
* 禁用默认捕获，捕获都要显式写出来。打比方，比起 [=](int x) {return x + n;}, 您该写成 [n](int x) {return x + n;} 才对，这样读者也好一眼看出 n 是被捕获的值。
* 匿名函数始终要简短，如果函数体超过了五行，那么还不如起名（acgtyrant 注：即把 lambda 表达式赋值给对象），或改用函数。
* 如果可读性更好，就显式写出 lambd 的尾置返回类型，就像auto.

### 6.22. 模板编程

不要使用复杂的模板编程

### 6.23. Boost 库

只使用 Boost 中被认可的库.

```cpp
Call Traits : boost/call_traits.hpp
Compressed Pair : boost/compressed_pair.hpp
<The Boost Graph Library (BGL) : boost/graph, except serialization (adj_list_serialize.hpp) and parallel/distributed algorithms and data structures(boost/graph/parallel)
Property Map : boost/property_map.hpp
The part of Iterator that deals with defining iterators: boost/iterator/iterator_adaptor.hpp, boost/iterator/iterator_facade.hpp, and boost/function_output_iterator.hpp
The part of Polygon that deals with Voronoi diagram construction and doesn’t depend on the rest of Polygon: boost/polygon/voronoi_builder.hpp, boost/polygon/voronoi_diagram.hpp, and boost/polygon/voronoi_geometry_type.hpp
Bimap : boost/bimap
Statistical Distributions and Functions : boost/math/distributions
Multi-index : boost/multi_index
Heap : boost/heap
The flat containers from Container: boost/container/flat_map, and boost/container/flat_set
```

### 6.24. C++11

## 7. 命名约定

### 7.1. 通用命名规则

函数命名, 变量命名, 文件命名要有描述性; 少用缩写.

### 7.2. 文件命名

文件名要全部小写, 可以包含下划线 (\_) 或连字符 (-), 依照项目的约定. 如果没有约定, 那么 “_” 更好.

小写原因：

* 可移植性。Linux大小写敏感，而windows和mac不敏感。避免跨平台出问题
* 易读性
* 易用性，系统默认目录通常是大写开头
* 便于命令行操作，查找等。

可接受的文件命名示例:

* my_useful_class.cc
* my-useful-class.cc
* myusefulclass.cc
* myusefulclass_test.cc // _unittest 和 _regtest 已弃用.

C++ 文件要以 .cc 结尾, 头文件以 .h 结尾

### 7.3. 类型命名

类型名称的每个单词首字母均大写, 不包含下划线: MyExcitingClass, MyExcitingEnum.

所有类型命名 —— 类, 结构体, 类型定义 (typedef), 枚举, 类型模板参数 —— 均使用相同约定, 即以大写字母开始, 每个单词首字母均大写, 不包含下划线

### 7.4. 变量命名

变量 (包括函数参数) 和数据成员名一律小写, 单词之间用下划线连接. 类的成员变量以下划线结尾, 但结构体的就不用

### 7.5. 常量命名

声明为 constexpr 或 const 的变量, 或在程序运行期间其值始终保持不变的, 命名时以 “k” 开头, 大小写混合.

### 7.6. 函数命名

常规函数使用大小写混合, 取值和设值函数则要求与变量名匹配: MyExcitingFunction(), MyExcitingMethod(), my_exciting_member_variable(), set_my_exciting_member_variable().

对于首字母缩写的单词, 更倾向于将它们视作一个单词进行首字母大写 

取值和设值函数的命名与变量一致. 一般来说它们的名称与实际的成员变量对应, 但并不强制要求. 例如 int count() 与 void set_count(int count).

### 7.7. 命名空间命名

命名空间以小写字母命名. 最高级命名空间的名字取决于项目名称. 要注意避免嵌套命名空间的名字之间和常见的顶级命名空间的名字之间发生冲突.

### 7.8. 枚举命名

枚举的命名应当和 常量 或 宏 一致: kEnumName 或是 ENUM_NAME.

### 7.9. 宏命名

通常 不应该 使用宏. 如果不得不用,全部大写, 使用下划线

### 7.10. 命名规则的特例

## 注释

### 8.1. 注释风格

使用 // 或 /* */, 统一就好.

### 8.2. 文件注释

在每一个文件开头加入版权公告.

每个文件都应该包含许可证引用. 为项目选择合适的许可证版本.(比如, Apache 2.0, BSD, LGPL, GPL)

不要在 .h 和 .cc 之间复制注释, 这样的注释偏离了注释的实际意义.

### 8.3. 类注释

每个类的定义都要附带一份注释, 描述类的功能和用法, 除非它的功能相当明显.

如果类的声明和定义分开了(例如分别放在了 .h 和 .cc 文件中), 此时, 描述类用法的注释应当和接口定义放在一起, 描述类的操作和实现的注释应当和实现放在一起.

### 8.4. 函数注释

函数声明处的注释描述函数功能; 定义处的注释描述函数实现.

函数声明处注释的内容:

* 函数的输入输出.
* 对类成员函数而言: 函数调用期间对象是否需要保持引用参数, 是否会释放这些参数.
* 函数是否分配了必须由调用者释放的空间.
* 参数是否可以为空指针.
* 是否存在函数使用上的性能隐患.
* 如果函数是可重入的, 其同步前提是什么?

### 8.5. 变量注释

每个类数据成员 (也叫实例变量或成员变量) 都应该用注释说明用途. 如果有非变量的参数(例如特殊值, 数据成员之间的关系, 生命周期等)不能够用类型与变量名明确表达, 则应当加上注释. 然而, 如果变量类型与变量名已经足以描述一个变量, 那么就不再需要加上注释.

### 8.6. 实现注释

对于代码中巧妙的, 晦涩的, 有趣的, 重要的地方加以注释.

代码前注释

行注释

函数参数注释

### 8.7. 标点, 拼写和语法

### 8.8. TODO 注释

对那些临时的, 短期的解决方案, 或已经够好但仍不完美的代码使用 TODO 注释.

TODO 注释要使用全大写的字符串 TODO, 在随后的圆括号里写上你的名字, 邮件地址, bug ID, 或其它身份标识和与这一 TODO 相关的 issue.

### 8.9. 弃用注释

通过弃用注释（DEPRECATED comments）以标记某接口点已弃用.

## 9. 格式

### 9.1. 行长度

每一行代码字符数不超过 80

### 9.2. 非 ASCII 字符

尽量不使用非 ASCII 字符, 使用时必须使用 UTF-8 编码.

### 9.3. 空格还是制表位

只使用空格, 每次缩进 2 个空格.

### 9.4. 函数声明与定义

返回类型和函数名在同一行, 参数也尽量放在同一行, 如果放不下就对形参分行, 分行方式与 函数调用 一致.

### 9.5. Lambda 表达式

Lambda 表达式对形参和函数体的格式化和其他函数一致; 捕获列表同理, 表项用逗号隔开.

### 9.6. 函数调用

要么一行写完函数调用, 要么在圆括号里对参数分行, 要么参数另起一行且缩进四格. 如果没有其它顾虑的话, 尽可能精简行数, 比如把多个参数适当地放在同一行里.

### 9.7. 列表初始化格式

您平时怎么格式化函数调用, 就怎么格式化 列表初始化.

### 9.8. 条件语句

倾向于不在圆括号内使用空格. 关键字 if 和 else 另起一行.

### 9.9. 循环和开关选择语句

### 9.10. 指针和引用表达式

句点或箭头前后不要有空格. 指针/地址操作符 (*, &) 之后不能有空格.

### 9.11. 布尔表达式

### 9.12. 函数返回值

不要在 return 表达式里加上非必须的圆括号.

### 9.13. 变量及数组初始化

用 =, () 和 {} 均可.

### 9.14. 预处理指令

即使预处理指令位于缩进代码块中, 指令也应从行首开始.

### 9.15. 类格式

访问控制块的声明依次序是 public:, protected:, private:, 每个都缩进 1 个空格.

### 9.16. 构造函数初始值列表

构造函数初始化列表放在同一行或按四格缩进并排多行.

### 9.17. 命名空间格式化

命名空间内容不缩进.

### 9.18. 水平留白

水平留白的使用根据在代码中的位置决定. 永远不要在行尾添加没意义的留白.

### 9.19. 垂直留白

垂直留白越少越好.

## 10. 规则特例

前面说明的编程习惯基本都是强制性的. 但所有优秀的规则都允许例外, 这里就是探讨这些特例.

### 10.1. 现有不合规范的代码

对于现有不符合既定编程风格的代码可以网开一面.

### 10.2. Windows 代码

Windows 程序员有自己的编程习惯, 主要源于 Windows 头文件和其它 Microsoft 代码. 我们希望任何人都可以顺利读懂你的代码, 所以针对所有平台的 C++ 编程只给出一个单独的指南.




























