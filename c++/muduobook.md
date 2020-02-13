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

条件变量conditon variable

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




