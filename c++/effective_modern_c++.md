# effective modern c++

## 1：理解模板型别推到

## 2:理解auto型别推导

## 3:理解decltype

## 4:掌握查看型别推导结果的方法

## 5:优先选用auto，而非显式型别声明

## 6：当auto推导的型别不符合要求时，使用带显式型别的初始化物习惯用法

## 7：在创建对象时注意区分()和{}

## 8：优先选用nullptr，而非0或者NULL

## 9：优先选用别名申明，而非typedef

using aa = bb;

## 10：优先选用限定作用域的枚举型别，而非不限作用域的枚举型别

enum class XXX{};

## 11：优先选用删除函数，而非private未定义函数

ClassName() = delete;

## 12:为意在改写的函数添加override声明

## 13：优先选用const_iterator，而非iterator

## 14:只要函数不会发射异常，就为其加上noexcept声明

## 15：只要有可能使用constexpr，就使用它

## 16:保证const成员函数的线程安全性

## 17：理解特种成员函数的生成机制

## 18：使用std::unique_ptr管理具备专属所有权的资源

## 19：使用std::shared_ptr管理具备共享所有权的资源

## 20：对于类似std::shared_ptr但有可能悬空的指针使用std::weak_ptr

## 21：优先选用std::make_unique和std::make_shared，而非直接使用new

## 22：使用Pimpl习惯用法时，将特殊成员函数的定义放到实现文件中
