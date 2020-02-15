# Linux多线程服务端编程

## 线程安全的对象生命周期管理

## 线程同步精要

四项原则

1. 尽量最低限度地共享对象，减少需要同步的场合。
2. 使用高级的并发编程构建，如TaskQueue，Produce-Consumer Quere，CountDownLatch
3. 只使用非递归的互斥器和条件变量，慎用读写锁，不用信号量
4. 除了atomic整数外，不要自己编写lock-free代码

### 互斥器mutex

用RAII封装mutex的创建、销毁、加锁和解锁。不手动调用lock和unlock，交给构造和析构函数负责。

使用非递归的mutex，可能会导致线程把自己锁死。能够暴露出代码中的问题。

在可能出现递归调用的情况中，可以**把函数拆分成加锁和不加锁两个版本**，已经加过锁的函数中调用不加锁版本。

实现isLockedByThisThread()函数，来判断当前的线程是否已经被加过锁。muduo中给出了实现。

使用MutexLockGuard时，可以在函数定义时加上 __attribute__ ((noinline))，避免被编译器内联，产生死锁。

对象析构race condition 

### 条件变量conditon variable

一个或多个线程等待某个布尔表达式为真，等待别的线程唤醒它。  

wait端：

* 必须受到mutex保护
* 在mutex上锁的时候才能调用wait()
* 把判断布尔条件和wait()放在while循环中(spurious wakeup,虚假唤醒)

singal/broadcast端：

* 在signal之前修改布尔值
* 使用mutex保护布尔值
* 注意区分signal和broadcast 单播和广播

条件变量是非常底层的同步原语，很少直接使用，一般用来实现高层的同步措施。如BlockingQueue和CountDownLatch。

倒计时CountDownLatch主要用于：

* 主线程发起多个子线程，等待这些子线程各自都完成一定的任务之后，主线程才继续执行。（等待初始化）
* 主线程发起多个子线程，子线程等待主线程完成一些任务后开始执行。（等待起跑）

### 不要用读写锁和信号量

read/write

* 容易在原来read lock保护的函数中调用了会修改状态的函数。
* 效率不一定更高
* reader lock可能允许提升为writer lock

程序中不应该出现"哲学家就餐"这类复杂的IPC问题的设计（一个线程同时抢两个资源，一个资源可以被两个线程争夺）

### 封装mutex和condition

如果一个class要包含MutexLock和Condition，需要注意声明顺序和初始化顺序，mutex_应该先于condition_构造，并作为后者的构造参数。

mutex和condition通常用来实现更高级的并发编程工具，尽量减少使用。

### 线程安全的Singleton实现

double checked locking，DCL

通过pthread_once执行

如何销毁？atexit()函数

### sleep不是同步原语

等待某个事件发生，应该用select等函数或者用Condition，不应该用sleep在用户态做轮询

### 借助shared_ptr实现copy-on-write

...

## 多线程服务器的适用场合与常用编程模型

### 进程与线程

### 单线程服务器的常用编程模型

Reactor模式，non-blocking IO + IO multitplexing，event-loop,event-driven，适合IO密集型应用

缺点：要求事件的回调函数必须是非阻塞的，容易割裂业务逻辑，不容易理解和维护。（解决办法：协程coroutine）

Proactor模式 异步。windwos asio。目前受操作系统限制。

### 多线程服务器的常用编程模型

1. 为每个请求创建线程，使用阻塞IO。线程开销大，伸缩性不佳
2. 在1的基础上引入线程池
3. non-blocking IO + one loop per thread模式
4. Leader/Follower等高级模式

one loop per thread

每个IO线程里有一个event loop(或者叫Reactor),用于处理读写和定时事件。

好处：
* 线程数目基本固定
* 方便在线程间调配负载
* IO事件发生的线程是固定的

eventloop需要满足线程安全

推荐模式：one loop per thread + thread pool

### 进程间通信只使用TCP

tcp socket可以跨主机，具有可伸缩性。单处理器->多处理器

TCP socket有tcpdump和wireshark等工具进行调试和重现，可跨主机跨语言。

### 多线程服务器的适用场合

* 一个单线程的进程
* 一个多线程的进程
* 多个单线程的进程
* 多个多线程的进程

必须使用单线程的程序：调用fork的进程，如守护进程。

单线程缺点：非抢占式的。a事件执行期间，b事件必须等待a执行完才能执行。

...

### 一些问题

1.linux能同时启动多少个线程?

系统最大线程数：/proc/sys/kernel/pid_max 32768

用户最大线程数：ulimit -u

2.多线程能提高并发度么？

连接数->不能

3.多线程能提高吞吐量么?

对与计算密集型，不能

4.多线程能降低响应时间么？

可以。可以处理突发请求。

多线程分担负载

5.多线程程序如何让IO和计算互相重叠，降低latency

...

## C++多线程系统编程精要




