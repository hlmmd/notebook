## 面试流程

1. 问好，确认面试者身份，简单自我介绍 1m
2. 面试者自我介绍 2m
3. 提问开始，c++、数据结构与算法、操作系统、简历上写着的技能 15-20m
4. 写题 30m
5. 简单问项目 5-10m
6. 反问环节 3m

## c++

1. 深拷贝与浅拷贝
2. c++多态
3. 什么是虚函数
4. 构造函数&析构函数可以是虚函数么
5. 静态成员函数可以是虚函数么
6. 虚函数的实现原理
7. 虚函数可以inline么
8. volatile 作用
9. explicit 作用
10. 定义一个空类，size是多大？为什么不是0
11. 空类会自动生成哪些函数
12. new 和 delete & malloc 和 free 的区别
13. operator new & operator delete
14. placement new
15. 如何定义只能在堆or栈上生成对象的类
16. c++ 11 特性是否了解
17. 右值引用、std::move、std::forward
18. 智能指针，shared_ptr环形引用
19. 四种强制类型转换
20. lambda表达式、参数捕获方式
21. stl是否了解、常用的数据结构
22. map、set、unordered_map、unordered_set的底层结构，操作复杂度
23. deque的底层数据结构、stack&queue的实现
24. vector的常用接口
25. vector的扩容机制
26. push_back和emplace_back的区别
27. resize和reverse的区别
28. 迭代器失效问题
29. 用c++写过多线程么，怎么写？
30. mutex、lock_guard、unique_lock
31. condition_variable用法
32. template是否了解
33. 什么是特化？偏特化与全特化
34. type_traits、enable_if、SFINAE
35. 模板可变参

## 操作系统

1. 是否常用linux系统，列举常用的bash命令
2. 硬链接和软连接的区别？如何创建
3. 进程间通信的常用方式
4. kill命令与信号
5. 虚拟内存的作用

## 数据结构与算法

* 二叉树的层序遍历
* nth_element
* 如何处理哈希冲突

## 算法题

给定由大写字母构成的字符串str，和正整数k，每次替换可以将字符串中的字符替换为任意字符，求最多替换k次的情况下，能得到最长连续相同的字串

AAAB 1 ， 得到4
AABBAABA 2，得到6

整数数组nums，正整数k，每次操作是让nums中任一个数+1,至多操作k次后，nums中出现次数最多数的次数

{6, 3, 0, 4, 2} 3 ,最多3个4

设计一个数据结构，支持 insert、erase、getRandom 三种接口，且时间复杂度都是O（1）

unordered_map + vector

```cpp
class RandomizedSet
{
public:
    /** Initialize your data structure here. */
    RandomizedSet();

    /** Inserts a value to the set. Returns true if the set did not already contain the specified element. */
    bool insert(int val);

    /** Removes a value from the set. Returns true if the set contained the specified element. */
    bool remove(int val);

    /** Get a random element from the set. */
    int getRandom();
};
```

```cpp
class RandomizedSet
{
public:
    // vector删除的时候，把想删除的元素和最后一个元素交换，再pop_back()，可以达到O(1)复杂度
    /** Initialize your data structure here. */
    RandomizedSet()
    {
    }

    /** Inserts a value to the set. Returns true if the set did not already contain the specified element. */
    bool insert(int val)
    {
        if (map_.count(val))
            return false;

        map_[val] = values_.size();
        values_.push_back(val);
        return true;
    }

    /** Removes a value from the set. Returns true if the set contained the specified element. */
    bool remove(int val)
    {

        if (!map_.count(val))
            return false;

        int index = map_[val];

        std::swap(values_[index], values_.back());
        map_[values_[index]] = index;
        values_.pop_back();
        map_.erase(val);
        return true;
    }

    /** Get a random element from the set. */
    int getRandom()
    {
        if (values_.size() == 0)
            return -1;
        return values_[rand() % values_.size()];
    }

    std::unordered_map<int, int> map_;
    std::vector<int> values_;
};
```

二维数组迭代器 实现构造函数、HasNext和GetNext。可以随意添加成员函数和变量。注意编程规范。

```cpp
#include <iostream>
#include <vector>
using namespace std;

class Iterator
{
public:
    Iterator(vector<vector<int>>* pdata);
    bool HasNext();
    int GetNext();

private:
    vector<vector<int>>* mpData;
};

int main()
{
    vector<vector<int>> data = {
        {},
        {1, 2, 3},
        {4, 5},
        {},
        {6, 7, 8, 9},
        {},
        {},
        {10}};

    Iterator it(&data);
    while (it.HasNext())
    {
        cout << it.GetNext() << endl;
    }
    return 0;
}
```

answer

```cpp
#include <iostream>
#include <vector>
using namespace std;

class Iterator
{
public:
    explicit Iterator(vector<vector<int>>* pdata) : mpData(pdata),
                                                    mI(0), mJ(0)
    {
        GetNextAux();
    }
    bool HasNext()
    {
        return mI < mpData->size();
    }
    int GetNext()
    {
        int ret = mpData->at(mI)[mJ];
        GetNextAux();
        return ret;
    }

private:
    bool GetNextAux()
    {
        size_t colSize = mpData->at(mI).size();
        mJ++;
        if (mJ >= colSize)
        {
            mJ = 0;
            mI++;
            while (mI < mpData->size() && mpData->at(mI).size() == 0)
            {
                mI++;
            }
        }
    }
    vector<vector<int>>* mpData;
    size_t mI;
    size_t mJ;
};

int main()
{
    vector<vector<int>> data = {
        {},
        {1, 2, 3},
        {4, 5},
        {},
        {6, 7, 8, 9},
        {},
        {},
        {10}};

    Iterator it(&data);
    while (it.HasNext())
    {
        cout << it.GetNext() << endl;
    }
    return 0;
}
```