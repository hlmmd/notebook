# effective c++

## 导读

explicit禁止隐式的类型转换。尽可能将构造函数申明成explicit

## 让自己习惯c++

### 01：视c++为一个语言联邦 

内置类型 copy by value比copy by reference效率高

### 02：尽量以const,enum,inline替换#define

用编译器替代预处理器

### 03：尽可能地使用const

const在*左边：指向常量

const在*右边：常指针

const 成员函数 mutable修饰符，表示变量可以在const函数中修改

const可以帮助编译器判断错误。

当const和non-const成员函数有着实质等价实现时，令non-const调用const版本可以避免代码重复

### 04:确定对象被使用前已先被初始化

构造函数的赋值和初始化

在函数体内为赋值，使用成员初始值列称为初始化。

初始化列表通常效率更高

const或者reference类型的成员变量必须用初始化列表。所以应当**总是使用初始化列表**

**c++有着固定的成员初始化顺序，class的成员变量总是以其声明的次序被初始化，与初始化列表中的顺序无关**

static对象分为local 和non-local。函数内的static称为local static，其他global、namesapce、class、file作用域内的static称为non-local static。

多个编译单元内的non-local static 对象初始化顺序不确定。

可以通过Singleton模式，将non-local static转换为local static。

注意static在多线程环境下的问题。

## 构造/析构/赋值运算

### 05：了解c++默默编写并调用哪些函数

编译器为一个空类生成：默认构造函数、默认赋值构造函数、默认复制构造函数、析构函数。这些函数都是public且inline的。

在成员变量涉及const和reference时，编译器不会自动生成copy assignment构造函数

### 06：若不想使用编译器自动生成的函数，应当明确拒绝

对于某些类，需要设计成不可copy的，即noncopyable，需要明确拒绝复制构造函数等。可以用delete关键字。

父类构造函数定义为私有，子类不能继承也不会产生默认的构造函数。

### 07：为多态基类声明virtual析构函数

避免类子类derived的成分没有被销毁

在含有virtual函数的类中将析构函数声明为虚函数

而在不含virtual的类中，则不应该声明为虚函数（增加空间占用）

不应该继承没有虚析构函数的类，比如stl中的vector，string等类

抽象类可以定义析构函数为纯虚函数，但同时**必须要给出其定义**,不然运行错误。

因为继承抽象类的类在析构时，先调用子类的析构函数，子类调用父类的虚析构函数

带多态性质的基类应该声明一个virtual析构函数，任何有virtual函数的类其析构函数都应该是虚函数

不带多态性质的基类不应当声明虚析构函数

### 08：别让异常逃离析构函数

如果析构函数可能会抛出异常，那么要提供一个普通函数用来执行该操作。

### 09：绝不在构造和析构过程中调用virtual函数

此时虚函数不起作用，不会调用子类的虚函数。

可以将构造过程中的虚函数改为non-virtual，然后在子类的构造函数中，对父类传递一个带指定参数的构造函数

### 10：令operator=返回一个reference to *this

### 11：在operator中处理“自我赋值”

确保当对象自我赋值时有良好的行为

比较地址、异常处理、copy and swap

确定函数操作多个对象，其中多个对象是同一个对象时，仍然正确。

### 12：复制对象时勿忘其每一个成分

编译器不会提示错误，所以在添加成员变量后要修改copy构造函数

在继承后，要注意父类部分是否被copy了。要调用父类合适的copy函数

不要尝试用一个copy函数去调用另一个copy函数以减少代码。

## 资源管理

### 13：以对象管理资源

RAII，资源取得时机便是初始化时机，构造函数获取资源，析构函数释放资源

shared_ptr管理资源

### 14：在资源管理类中小心copy行为

禁止copy或采用引用计数

### 15：在资源管理类中提供对原始资源的访问

API可能会要求访问class的原始资源，所以RAII class应该提供一个“取得其持有资源”的方法

最好使用显示转换，不要用隐式转换。

### 16：成对使用new和delete时要采取相同形式

使用typedef避免定义数组

new和delete的形式要相同（有无[]，即数组形式）

### 17：以独立语句将newed对象置入智能指针

否则一旦抛出异常，可能导致难以察觉的资源泄露

## 设计与声明

### 18：让接口容易被正确使用，不容易被误用

接口一致性，与内置类型的行为兼容

阻止误用：建立新类型，限制类型上的操作，束缚对象值类型，消除客户的资源管理责任

shared_ptr支持定制型删除器，可防范DLL问题，可用来自动解除mutex

### 19：设计class犹如设计type

### 20：宁以pass-by-reference-to-const替换pass-by-value

在涉及虚函数时，如果以父类的传值作为参数，则可能会导致所有特化信息被切除，无法正确表现子类虚函数

内置类型、STL迭代器和函数对象，pass-by-value更合适

### 21：必须返回对象时，别妄想返回其reference

绝不要返回pointer或者reference指向一个Local stack对象，或者返回reference指向一个heap-allocated对象。

### 22：将成员变量声明为private

protected不必public更具封装性。（继承）

成员变量声明为private。

作为库的开发者，修改private变量不会对客户代码造成巨大影响。

### 23：宁以non-member、non-friend替换member函数

增加封装性、包裹弹性和机能扩充性

### 24：若所有参数皆需类型转换，请为此采用non-member函数

### 25：考虑写出一个不抛出异常的swap函数

当std::swap（基于copy和copy assginment)对你的类型效率不高时，提供一个swap成员函数，并确定这个函数不抛出异常。

如果你提供一个member swap，也提供一个non-member swap来调用前者。对于class，也请特化std::swap

调用swap时应针对std::swap使用using声明式，然后调用swap并不带任何“命名空间资格修饰”

为“用户定义类型”进行std templates全特化是好的，但不要在std内加入全新的东西

## 实现

### 26：尽可能延后变量定义式的出现时间

尽可能延后变量定义式的出现。（结构清晰+减少可能不必要的构造和析构）

### 27：尽量少做转型动作

const_cast：常量性转除，并不能修改常量，只是提供一种“妥协”（函数参数匹配等）

dynamic_cast：执行安全向下转型，用来决定某个对象是否归属继承体系中的某个类型。子类转父类

reinterpret_cast：执行低级转换，不常用，如pointer to int 转 int

static_cast：用来强迫隐式转换。

尽量避免使用转型，尤其是dynamic_cast

如果转型是必要的，试着将它隐藏于某个函数背后，不让客户自行转型

尽量使用C++形式转型

### 28：避免返回handles指向对象内部成分

### **29：为“异常安全”而努力是值得的**

...

### 30：透彻了解inlining的里里外外

inline代码膨胀问题、调试问题、软件升级问题

inline隐喻提出：将函数定义在class定义式内

inline通常用于小型、被频繁调用的程序上

### **31：将文件间的编译依存关系降至最低**

相依赖于申明式，而不相依赖于定义式

程序库头文件应该以“完全且仅有声明式”的形式存在

## 继承与面向对象设计

### 32：确定你的public继承塑模出is-a关系

public 继承意味着is-a。适用于base classes身上的每一件事情也一定适用于derived classes身上，因为每一个derived class对象也是一个base class对象

### 33：避免遮盖继承而来的名称

子类定义的名称会覆盖父类。

可以用using声明式或转交函数

### 34：区分接口继承和实现继承

public继承下，子类总是继承父类的借口

纯虚函数只具体指定接口继承

普通虚函数具体指定借口继承及缺省实现继承

非虚函数具体制定接口继承以及强制性实现继承

### 35：考虑virtual函数以外的其他选择

虚函数的替代方法包括NVI手法及strategy设计模式的多种形式。

将机能从成员函数移到class外部函数，带来的一个缺点是，非成员函数无法访问class的non-public成员

std::function对象的行为就像一般函数指针，可接纳“与给定之目标签名式兼容”的所有可调用物

### 36：绝不重新定义继承而来的普通函数

### 37：绝不重新定义继承而来的缺省参数值

### 38：通过复合塑模出has-a或“根据某物实现出”

composition的意义和public继承完全不同

在应用域，复合意味着has-a。在实现域，复合意味着is-implemented-in-terms-of

### 39：明智而审慎地使用private继承

private继承意味着is-implemented-in-terms-of。它通常比复合的级别低。但是当derived class需要访问父类protected成员，或需要重新定义继承而来的虚函数时，这么设计是合理的

和composition不同，private继承可以造成empty base最优化。这对致力于“对象尺寸最小化”的程序库开发者而言，可能很重要。

### 40：明智而审慎地使用多重继承

多重继承比单一继承复杂，可能导致歧义性，以及对virtual继承的需要。

virtual继承会增加大小、速度、初始化等成本。如果virtual base classes不带任何数据，将是最具实用价值的情况

可用于：public继承某个interface class和private继承某个协助实现的class两两结合

## 模板与泛型编程

### 41：了解隐式接口和编译期多态

classes和templates都支持借口和多态

对classes而言接口是显式的，以函数签名为中心。多态则是通过虚函数发生于运行期。

对template参数而言，借口是隐式的，多态通过具现化和函数重载解析发生于编译器。

### 42：了解typename的双重意义

申明template参数时，前缀关键字class和typename可互换

typename可标识嵌套从属类型名称，但不能在基类列或成员初始列中出现。

### 43：学习处理模板化基类内的名称

可以在子类模板中通过this->获取父类模板的成员名称

### 44：将与参数无关的代码抽离templates

### 45：运用成员函数模板接受所有兼容类型

### 46：需要类型转换时请为模板定义非成员函数

### 47：请使用traits classes表现类型信息

### 48：认识template元编程

## 定制new 和 delete

### 49:了解new-handler的行为

### 50：了解new和delete的合理替换时机

### 51：编写new和delete时需要固守常规

### 52：写了placement new也要写placement delete

## 杂项讨论

### 53：不要轻忽编译器的靖难搞

### 54：让自己熟悉包括TR1在内的标准程序库

### 55：让自己熟悉boost


