# Linux TCP IP 协议栈分析

93/302

## 协议栈概述

### Linux 操作系统架构简介

Linux 是一款大内核操作系统

#### 微内核

微内核（Microkernel kernel）――在微内核中，大部分内核都作为独立的进程在特权状态下运行，它们通过消息传递进行通讯。

微内核设计的一个优点是在不影响系统其它部分的情况下，用更高效的实现代替现有文件系统模块的工作将会更加容易

#### 大内核

大内核（Monolithic kernel）――单内核是一个很大的进程。它的内部又可以被分为若干模块（或者是层次或其它）。但是在运行的时候，它是一个独立的二进制大映象。其模块间的通讯是通过直接调用其它模块中的函数实现的，而不是消息传递。

除了嵌入式，没有一款商用（包括开源）的操作
系统是以真正学术上的微内核方式存在。也就是说，所有的商用操作系统都是把驱动、文件系统、协议
栈等塞入内核之中，原因在于**安全和效率**。比如进程之间的调度，进程之间同步互斥等。

![linuxos](pics/linuxos.jpg)

### 网络协议发展介绍

TCP/IP 本身是一个协议族，还包含了ARP，ICMP，UDP 等协议

IP协议栈的中心就在于，使用IP 协议将数据包发送到任何网络，而且数据包到达的时机可以不同

![iso7model](pics/iso7model.jpg)

![linuxproto](pics/linuxproto.jpg)

在这个结构图中，我们注意glibc库和INET。我们使用的一些系统函数比如malloc、socket都是由这个库提供的。另一个模块是INET它不属于TCP/IP 体系必须的一个部分，但TCP/IP 层的接口要通过INET 层才能访问操作，这一操作是在网络初始化时就已经注册到BSD 风格的socket 层的。所谓BSD 风格就是我们常说的socket、bind、connect、listen、send 和recv 等系统接口的调用风格。

#### AF_INET 和 PF_INET

windows中AF_INET与PF_INET完全一样

在Unix/Linux系统中，在不同的版本中这两者有微小差别.对于BSD,是AF,对于POSIX是PF

建立socket时是指定协议，应该用PF_xxxx，设置地址时应该用AF_xxxx。当然AF_INET和PF_INET的值是相同的，混用也不会有太大的问题。

### 基本的数据结构和计算机术语

#### typeof关键字

C语言中，可以使用typeof关键字，获取表达式的类型，而不执行表达式。

可以用来实现安全的宏定义等，比如求max

```c
#define max(a,b)        \
({                      \
typeof (a) _a = (a);    \
typeof (b) _b = (b);    \
_a > _b ? _a : _b;      \
})
```

typeof不能包含static/extern以及const和volatile

#### 链表

```c
struct list_head {
    struct list_head *next, *prev;
};
```

这里的list_head 没有数据域。在Linux 内核链表中，不是在链表结构中包含数据，而是在数据结构中包含链表节点。

Linux 提供了一个list_entry(ptr, type, member)宏，用于访问到链表的所有者。

```c
#define list_entry(ptr, type, member) container_of(ptr, type, member)

#define container_of(ptr, type, member) ({ \
const typeof( ((type *)0)->member ) *__mptr = (ptr); \
(type *)( (char *)__mptr - offsetof(type,member) );})

#define offsetof(TYPE, MEMBER) ((size_t) &((TYPE *)0)->MEMBER)
```

例如，有一个由proto{}组成的链表，名字叫proto_list，而每个proto{}结构中的list_head{}成员名字叫node。我们要访问这个链表中第一个节点，则如此调用：

ptr是指向结构体中member的指针，type是结构体类型，member是结构体中成员变量名。

`list_entry(proto_list->next, struct proto, node);`

`((type*)0)->member`将0地址强制转换为type类型的指针，再访问其member成员。这样取地址的值转成size_t后，就是member在其所在结构体的偏移量。通过成员变量的地址和成员变量在所属结构中的offset，得到所属结构的指针。

`container_of`宏就利用这个技巧，用typeof定义一个指针指向ptr，即链表指针，__mptr是一个临时指针，用于保存ptr的值，之后先将ptr转成char \*，再减去偏移量（指针+-的是按照基类型大小进行的，所以先转成(char\*)，然后再转换成(type*)，作为所属结构体的指针返回。

这里有一个问题，为什么要定义一个临时指针，而不直接写成一行？

```c
#define container_of(ptr, type, member) ({ \
(type *)( (char *)ptr - offsetof(type,member) );})
```

看似也能完成同样的功能，但要考虑到，当传入的值不是一个值，而是表达式的情况。

```c
#define min(x,y) ((x) < (y) ? (x) : (y)) 
min(a++,b++)
```

比如上面这个例子，a++会被调用两次，最后并不能得到想要的结果。container_of中的临时变量就是用来解决这种情况的，还用const修饰符进行强调。

#### hlist 哈希表

hlist 是 hash list 的简称,即用拉链法实现的 hash 数据结构

注意，hlist是**单循环链表**。

![](pics/list_hlist.png)

```c
struct hlist_head {
struct hlist_node *first;
};
struct hlist_node {
struct hlist_node *next, **pprev;
};
```

习惯上称 hash 数组为hash 表,而将冲突节点链表称为 hash 链

## 系统初始化

编译linux源码时，可以用make menuconfig命令来选定需要编译到内核中的模块。

* \*表示模块被编译进内核,在系统启动的时候,被主调函数调用执行;
* M表示被编译成一个.ko 文件,系统启动的时候依靠脚本把这些目标文件装入内核

### 系统初始化流程简介

从加电到出现Login界面：

* 四个汇编程序:bootsect.S setup.S head.S entry.S
* init 目录下的 main.c 文件

我们主要关心四个方面：

1. 中断系统及调度系统
2. 文件系统的初始化
3. 设备管理系统的初始化
4. 网络协议的初始化

![](pics/kernel_init.png)

最后一个是rest_init函数，在此之前的函数都有**__init**宏，表示该函数再执行之后，其占用的空间会被释放。与网络相关的函数均在rest_init中。

![](pics/rest_init.png)

主要函数：kernel_thread

```c
int kernel_thread(int (*fn)(void *), void * arg, unsigned long flags)
```

此函数定义在 arch/i386/kernel/process.c 中,它利用 linux/i386 的 do_fork 函数创建一个新的内核态线程,Linux 的内核线程是没有虚拟存储空间的进程,它们运行在内核中,直接使用物理地址空间。

接下来就是init函数。其中有个do_basic_setup函数与网络初始化有关。

再do_initcalls中，这样两个变量__initcall_start 和__initcall_end

```c
extern initcall_t __initcall_start, __initcall_end;
static void __init do_initcalls(void)
{
    initcall_t *call;
    int count = preempt_count();
    //从__initcall_start 这个变量开始遍历, 直到遇到__initcall_end 这个变量
    for (call = &__initcall_start; call < &__initcall_end; call++)
    {
        char *msg;
        //...... 调用初始化函数, 我们目前是无法从代码上直接看到 call 是什么函数

        (*call)();
        msg = NULL;
        //......
    }
}
/* Make sure there is no pending stuff from the initcall sequence */
flush_scheduled_work();
```

这是通过ELF文件调用函数。

### 内核文件解读

#### ELF 文件格式

ELF 是*nix 系统上可执行文件的标准格式,它取代了 out 格式的可执行文件,原因在于它的可扩展
性。

ELF 格式的可执行文件可有多个 section。DWARF(Debugging With Attribute Record Format)是经常碰到的名词,它在 ELF 格式的可执行文件中。

ELF文件有三种不同的形式:

* Relocatable:由编译器和汇编器生成,由 linker 处理它。
* Executable:所有的重定位和符号解析都完成了,也许共享库的符号要在运行时刻解析。
* Shared Object:包含 linker 需要的符号信息和运行时刻所需的代码。

#### Link Scripts 知识

为什么我们编出来的代码肯定是在用户地址空间运行,而内核编出来的代码却一定是运行在内核空间?

链接器的作用。不能简单地认为链接器仅仅完成将各 obj 文件拼在一起的任务,而且它还指定每个段被装入内存的真正地址。

#### Linux内核镜像研究

～～～

### 中断及任务调度管理

Linux书籍中常说的 BottomHalf 已然不见了,它们被转成 tasklets。

#### 中断系统和软中断

##### 硬件中断

中断向量?中断请求号?

IRQ 是设备相关的号码

中断向量就纯粹是操作系统中关于如何处理中断的内存组织结构,它们之间存在某种映射关系。

在 IA32 体系的 Linux中,是一种直接映射的关系,所有的 IRQ 号产生的中断全部映射到从 INT_vec[32]开始的内存中

![](pics/irq_table.png)

intel CPU 保留使用的中断向量是 0~31,根本不可能有哪一种设备会使用这个区域的中断向量,这一部分就是我们常说的异常处理函数,还有一个比较特殊的中断向量号 0x80(即 128)就是系统调用号,由于不可能由外部设备引发这类中断,它们就被统称为内部中断。

每个外部中断都会调用 do_IRQ,此函数根据当时的 EAX 寄存器(i386 体系)值来判断当前属于哪
个 IRQ 去调用__do_IRQ。

##### 软件中断

以上都是硬件中断，而软件中断的操作流程如下(以socket()为例)：

![](pics/softwareirq.png)

mov 指令就是告诉内核要跳转的系统调用函数以及用户待传入内核的参数地址

所以,软件中断的处理方式和硬件的处理路径是完全不一样的,它不必经过 do_IRQ 这个函数,而是直接跳转到内核中的代码执行 sys_socketcall

使用sys_socketcall 来解复用不同的系统调用,这样做的好处是减少系统调用表的大小,可以集中管理网络方面的API

##### 软中断

在中断即将退出的时候会调用 irq_exit,它内部会判断是否还有中断要处理,如果已经没有了就调用 invoke_softirq,这是一个宏,它被定义成 do_softirq,此函数最终调用__do_softirq。软中断是在处理完所有中断之后才会处理的。

目前 Linux 内核中定义了 6 种软中断,而且告诫我们不要轻易的再定义新的软中断

虽然系统中定义了 6 种软中断,但在 start_kernel 函数中调用的 softirq_init,只初始化了2个，HI_SOFTIRQ和TASKLET_SOFTIRQ

软中断向量 0 (即 HI_SOFTIRQ)用于实现高优先级的软中断,软中断向量5(即 TASKLET_SOFTIRQ)
则用于实现诸如 tasklet 这样的一般性软中断。

Tasklet 机制是一种较为特殊的软中断。Tasklet 一词的原意是“小片任务”的意思,这里是指一小段可执行的代码,且通常以函数的形式出现。软中断向量 HI_SOFTIRQ 和 TASKLET_SOFTIRQ 均是用 tasklet机制来实现的。

tasklet 机制与一般意义上的软中断有所不同,而呈现出以下两个显著的特点:

1. 与一般的软中断不同,某一段 tasklet 代码在某个时刻**只能在一个**CPU上运行,而不像一般的软中断服务函数(即 softirq_action 结构中的 action 函数指针)那样??在同一时刻可以被多个CPU 并发地执行。
2. 与 BH 机制不同,不同的 tasklet 代码在**同一时刻可以在多个** CPU 上并发地执行,而不像 BH 机制那样必须严格地串行化执行(也即在同一时刻系统中只能有一个 CPU 执行 BH 函数)。

__do_softirq 内部最多只处理 10 个软中断,如果系统内部的软中断事件太多,那么就会通知 ksoftirqd 内核线程处理软中断

#### 各种语境下的切换

某一个进程只能运行在用户方式(user mode)或内核方式(kernel mode)下。用户程序运行在用户方式下,而系统调用运行在内核方式下

对于某一个特定的进程,它必定处于下面状态中的一个:

```
宏定义	值	含义
TASK_RUNNING	0	正在运行的进程(是系统的当前进程)或准备运行的进程(在Running 队列中,等待被安排到系统的 CPU)。处于该状态的进程实际参与了进程调度 
TASK_INTERRUPTIBLE	1	处于等待队列中的进程,待资源有效时唤醒,也可由其它进程被信号中断、唤醒后进入就绪状态 
TASK_UNINTERRUPTIBLE	2	处于等待队列中的进程,直接等待硬件条件,待资源有效时唤醒,不可由其它进程通过信号中断、唤醒 
TASK_STOPPED	4	进程被暂停,通过其它进程的信号才能唤醒。正在调试的进程可以在该停止状态 
TASK_ZOMBIE	8	终止的进程,是进程结束运行前的一个过度状态(僵死状态)。虽然此时已经释放了内存、文件等资源,但是在 Task 向量表 中仍有一个 task_struct 数据结构项。它不进行任何调度或状态 转换,等待父进程将它彻底释放 
```

#### 内核下的同步与互斥

同步与互斥是有区别但又互相联系,因为同步是建立在互斥的基础之上的。只有实现了互斥功能,才能实现同步机制。
步一般用 semaphore 表示,互斥一般用 spin_lock 来表示

内核中的执行路径主要有:

1. 用户进程的内核态,此时有进程 context,主要是代表进程在执行系统调用 等。
2. 中断或者异常或者自陷等,从概念上说,此时没有进程 context,不能进行 context switch。
3. 软中断,从概念上说,此时也没有进程 context。
4. 同时,相同的执行路径还可能在其他的 CPU 上运行。

local_irq_disable/local_irq_enable,表示只是对当前执行上下文的 CPU 进行开/关中断

Spin_lock 采用的方式是让一个进程运行,另外的进程忙等待

spin_lock有很多种

* 如果只要和其他 CPU 互斥——spin_lock/spin_unlock,
* 如果要和 irq 及其他 CPU 互斥—— spin_lock_irq/spin_unlock_irq,
* 如果既要和 irq 及其他 CPU 互斥,又要保存 EFLAG 的状态,——spin_lock_irqsave/spin_unlock_irqrestore,
* 如果 要和 bh 及其他 CPU 互斥——spin_lock_bh/spin_unlock_bh,
* 如果不需要和其他 CPU 互斥,只要和 bh 互斥,——local_bh_disable/local_bh_enable。

如果再单核处理器上，spin_lock就是一个空语句
```c
# define __raw_spin_lock(lock)
do { (void)(lock); } while (0)
```

内核中的 semaphore 机制主要通过 down()和 up()两个操作实现。down()用于获取资源,而 up()是释放资源。

semaphore 和 spin_lock 机制解决的都是两个进程的互斥问题,都是让一个进程退出临界区后另一个进程才进入的方法,不过 sempahore 机制实行的是让进程**暂时让出 CPU**,进入等待队列等待的策略,而 spin_lock 实行的却是却**进程在原地空转**,等着另一个进程结束的策略。

#### 异步手段

##### Timer
定时器函数,都是一些回调函数.

内核中的 Timer 不是线程，它们运行在中断级,所以 timer 函数不应该做任何精细的工作。

##### work queue

类似于 Timer,指定一个回调,然后挂接到一个特殊的队列,让系统在适当的时机调用它们。它与进程调度机制紧密结合,能够用于实现内核中异步事件通知机制。

##### 通知链

### 虚拟文件系统

VFS(Virtual File System，虚拟文件系统)是操作系统的骨架。

管理 VFS 数据结构的组成部分主要包括超级块(super block,sb)和 inode

Linux 将网络接口也作为一个文件去操作，这就是为什么同样的发送过程,可以用 send( ),也可以用 write()。

VFS 并不是一种实际的文件系统。它只存在于内存中,不存在于任何外存空间。VFS 在系统启动时建立,在系统关闭时消亡。(废话，不然为什么叫Virtual)

所谓超级块就是对所有文件系统的管理机构,每种文件系统都要把自己的信息挂到 super_blocks 这么一个全局链表上。内核中是分成 2 个步骤完成:首先每个文件系统必须通过 register_filesystem 函数将自己的 file_system_type 挂接到file_systems这个全局变量上,然后调用kern_mount 函数把自己的文件相关操作函数集合表挂到 super_blocks 上。每种文件系统类型的读超级块的例程(get_sb)必须由自己实现。

![](pics/sb_filesys.png)

文件系统由子目录和文件构成。每个子目录和文件只能由唯一的 inode 描述。inode 是 Linux 管理文件系统的最基本单位,也是文件系统连接任何子目录、文件的桥梁。VFS inode 只存在于内存中,可通过 inode缓存访问。

用ls -al命令可以查看文件类型。

* '-' 普通文件
* 'd' 目录文件
* 'b' 块设备
* 'c' 字符设备
* 'l' 链接
* 'p' 命名管道
* 's' socket套接字

### 网络协议栈各部分初始化

大致的顺序:

1. core_initcall:sock_init
2. fs_initcall:inet_init
3. subsys_initcall:net_dev_init
4. device_initcall:设备驱动初始化

#### 网络内存管理

数据包在应用层称为 data,在 TCP 层称为 segment,在 IP 层称为 packet,在数据链路层称为 frame。

![](pics/sock_sk_buff.png)

```c
struct sk_buff_head {
/* These two members must be first. */
/* 这两个成员必须放在前面。原因在于对 sk_buff_head 进行操作的时候,可以用 sk_buff
结构做类型的强制转换来完成,反过来一样 */
struct sk_buff *next;
struct sk_buff *prev;
__u32 qlen; /*该 sk_buff_head 结构引导的一个链表的节点的个数 */
};
```

```c
struct sk_buff
{
    /* These two members must be first. */
    struct sk_buff
        *next;
    struct sk_buff
        *prev;
    struct sk_buff_head *list;
    struct sock
        *sk;
    struct timeval
        stamp;
    struct net_device
        *dev;
    struct net_device
        *real_dev;
    //下面是关于第 4 层 / 传输层首格式, 只用 h 表示这个联合的名字, (其实我觉得可以叫 th, 即 transport header)
    union {
        struct tcphdr
            *th;
        struct udphdr
            *uh;
        struct icmphdr *icmph;
        struct igmphdr *igmph;
        struct iphdr
            *ipiph;
        struct ipv6hdr *ipv6h;
        unsigned char
            *raw;
    } h;
    //下面是关于第 3 层 / 网络层首格式, 就是传说中的 IP 头, 所以联合的名字叫 nh, 即 network header
    union {
        struct iphdr
            *iph;
        struct ipv6hdr *ipv6h;
        struct arphdr
            *arph;
        unsigned char
            *raw;
    } nh;
    //   下面就是 MAC 层的头
    union {
        struct ethhdr
            *ethernet;
        unsigned char
            *raw;
    } mac;
    //   以上这种安排也体现了报文各层头部的逻辑关系
    //  这个是路由 cache 的指针, 后面的章节会着重介绍
    struct dst_entry
        *dst;
    //   这个是 xfrm 相关的成员, 不必关心
    struct sec_path
        *sp;
    /*
* This is the control buffer. It is free to use for every
* layer. Please put your private variables there. If you
* want to keep them across layers you have to do a skb_clone()
* first. This is owned by whoever has the skb queued ATM.
*/
    char
        cb[48];
    unsigned int
        len,
        data_len,
        mac_len,
        csum;
    unsigned char
        local_df,
        cloned, /* 指示此 sk_buff{}是否被“克隆”过。*/
        //       当接收一个报文时, 创建一个 sk_buff{}, 然后根据地址类型指定该 skb 实际属于哪一种的报文类型, 然后上 层协议栈采取相应的处理方式处理该 skb, 见下面的表
        pkt_type,
        ip_summed;
    __u32
        priority;
    //    可以用 eth_type_trans 函数获取 protocol 的值, 如果以太网头大于 1536, 那么就返回以太网的 h_proto 值, 下面时常用值
    unsigned short protocol,
        void (*destructor)(struct sk_buff *skb);
#ifdef CONFIG_NET_SCHED
    __u32
        tc_index;
/* traffic control index */
#endif
    //    下面这些成员必须放在最后, 在 alloc_skb() 函数中(创建 sk_buff) 为了提高性能, 只将上面各部分全部 清 0, 而下面的部分可以后面指定。
    unsigned int truesize;
    atomic_t
        users; /* 每引用或“克隆”一次 sk_buff{ }结构的时候,都要自加 1 */
    unsigned char
        *head,
        *data,
        *tail,
        *end;
};
```

len 是指数据包全部数据的长度,包括 data 指向的数据和 end 后面的分片的数据的总长,而 data_len只包括分片的数据的长度。而 truesize 的最终值是 len+sizeof(struct sk_buff)。

为了使用套接字缓冲区,内核创建了两个后备高速缓存(lookaside cache),它们分别是skbuff_head_cache 和 skbuff_fclone_cache,协议栈中所使用到的所有的 sk_buff 结构都是从这两个后备高速缓存中分配出来的。两者的区别在于 skbuff_head_cache 在创建时指定的单位内存区域的大小是sizeof(struct sk_buff),可以容纳任意数目的 struct sk_buff,而 skbuff_fclone_cache 在创建时指定的单位内存区域大小是 2*sizeof(struct sk_buff)+sizeof(atomic_t),它的最小区域单位是一对 strcut sk_buff 和一个引用计数,这一对 sk_buff 是克隆的,即它们指向同一个数据缓冲区,引用计数值是 0,1 或 2,表示这一对中有几个 sk_buff 已被使用。
创建一个套接字缓冲区,最常用的操作是 alloc_skb,它在 skbuff_head_cache 中创建一个 struct sk_buff,如果要在 skbuff_fclone_cache 中创建,可以调用__alloc_skb,通过特定参数进行。

在 sk_buff{}中的 4 个指针 data、head、tail、end 初始化的时候,data、head、tail 都是指向申请到的数据区的头部,end 指向数据区的尾部。在以后的操作中,一般都是通过 data 和 tail 来获得在 sk_buff 中可用的数据区的开始和结尾

skb_clone 和 skb_copy 的区别:前者基本在 skbuff_fclone_cache 中分配内存,除非一定要对一个不是可以被克隆的对象进行克隆,那么才会在 skbuff_head_cache 中分配内存,而且只是sk_buff{}结构的复制,没有涉及到真正数据区(data)的复制;后者必定在 skbuff_head_cache 中进行,不仅复制 sk_buff{},而且复制了数据区。

#### 网络文件系统初始化 sock_init

socket 属于文件系统的一部分,网络通信可以被看作对文件的读取。这种特殊的文件系统叫 sockfs.

一节中提到在 sock_init 函数中先调用 init_inodecache,为创建 socket 文件系统做好内存准备。不过要注意的是在 Linux 内核中存在 init_inodecache 多个定义,但**都是静态型,即只能由该.c文件中的函数调用**

首先是调用 register_filesystem(&sock_fs_type);把文件系统类型注册到 file_systems 链表上,然后调用kern_mount(&sock_fs_type);把该文件系统注册到 super_blocks 上。

#### 网络协议初始化 inet_init

Linux 将不同的地址族抽象统一为 BSD 套接字接口

为了支持多种套接字类型,内核中是有多种相应的全局变量与之对应,而不是只有一种。比如 proto{}结构类型的,有 inet_protocol{}结构类型的,有 inet_protosw{}结构类型的,有 proto_ops{}结构类型的

注意,网络协议的初始化是在网络设备的初始化之前完成的（可以不需要设备，在本机上运行网络系统）

![](pics/inet_init.png)

* tcp_v4_init( )和 tcp_init( )的不同:前者什么都不做(即不在本书的讨论范围内),而后者才是用来初始化 TCP 协议需要的各项 hash 表和 sysctl_xxx 全局配置项的。
* arp_init 完成系统 neighbour 表的初始化。
* ip_rt_init 初始化 IP 路由表 rt_hash_table

一进入初始化就调用 proto_register3 次,先后为 tcp、udp、raw 的 proto{}结构申请空间并将其挂到一个全局链表 proto_list 上。这三个 proto 全局变量非常重要,是连接传输层和 IP 层的纽带。

```c
struct proto tcp_prot = {
    .name = "TCP",
    .owner = THIS_MODULE,
    .close = tcp_close,
    .connect = tcp_v4_connect,
    .accept = inet_csk_accept,
    .ioctl = tcp_ioctl,
    .init = tcp_v4_init_sock,
    .sendmsg = tcp_sendmsg,
    .recvmsg = tcp_recvmsg,
    .backlog_rcv = tcp_v4_do_rcv,
    .hash = tcp_v4_hash,
    .get_port = tcp_v4_get_port,
};

struct proto udp_prot = {
    .name = "UDP",
    .owner = THIS_MODULE,
    .close = udp_close,
    .connect = ip4_datagram_connect,
    .disconnect = udp_disconnect,
    .ioctl = udp_ioctl,
    .sendmsg = udp_sendmsg,
    .recvmsg = udp_recvmsg,
    .sendpage = udp_sendpage,
    .backlog_rcv = udp_queue_rcv_skb,
    .hash = udp_v4_hash,
    .unhash = udp_v4_unhash,
    .get_port = udp_v4_get_port,
};

struct proto raw_prot = {
    .name = "RAW",
    .owner = THIS_MODULE,
    .close = raw_close,
    .connect = ip4_datagram_connect,
    .disconnect = udp_disconnect,
    .ioctl = raw_ioctl,
    .init = raw_init,
    .setsockopt = raw_setsockopt,
    .getsockopt = raw_getsockopt,
    .sendmsg = raw_sendmsg,
    .recvmsg = raw_recvmsg,
    .bind = raw_bind,
    .backlog_rcv = raw_rcv_skb,
    .hash = raw_v4_hash,
    .unhash = raw_v4_unhash,
};
```

用户创建 socket 时,先指定 INET 地址族,在指定套接字类型。换句话说这是数据流发送的流向。

socket 层必须区分哪一个用户应该接收这个包,这叫做 socket 解复用。下面是初始化第二个方面的必要步骤:注册接收函数。

```c
/*
* Add a protocol handler to the hash tables
*/
//在 inet_init 函数中调用了 3 次,分别传入 icmp_protocol,udp_protocol,tcp_protocol
//这三个实体内容如下:

static struct inet_protocol tcp_protocol = {
    .handler = tcp_v4_rcv,
    .err_handler = tcp_v4_err,
    .no_policy = 1,
};
static struct inet_protocol udp_protocol = {
    .handler = udp_rcv,
    .err_handler = udp_err,
    .no_policy =
        1,
};
static struct inet_protocol icmp_protocol = {
    .handler = icmp_rcv,
};

struct inet_protocol
{
    int (*handler)(sk_buff *skb);
    void (*err_handler)(sk_buff *skb, u32 info);
    int no_policy;
};

int inet_add_protocol(struct inet_protocol *prot, unsigned char protocol)
{
    int hash, ret;
    hash = protocol & (MAX_INET_PROTOS - 1);
    if (inet_protos[hash])
    {
        ret = -1;
    }
    else
    {
        inet_protos[hash] = prot;
        ret = 0;
    }
    return ret;
}
```

对报文感兴趣的底层协议目前有两个,一个是 ARP,一个是 IP,报文从设备层送到上层之前,必须区分是 IP 报文还是 ARP 报文。然后才能往上层送。这个过程由一个数据结构来抽象,叫 packet_type{}

inet_init 函数最后调用了一个 dev_add_pack 函数,不仅是 inet_init 函数调用,有一个很很重要的模块也调用了它,就是 ARP 模块

```text
传说中 Linux 有一个能抓包的特性,方法如下:fd = socket(PF_PACKET, mode, htons(ETH_P_ALL));
如此这般就能把所有的报文收上来。实际上内核就是把一个 type 为 ETH_P_ALL 的 prot_hook 挂接到
ptype_all 链表上,其报文处理函数为 packet_rcv,
```

综合前面分析的初始化过程,那么网络协议栈的框架基本搭起来了,从上图中可以看到代码中用数个全局变量完成了 INET 层和传输层的搭建工作。记住,在这里都还只是 INET 层和传输层的组织架构,而 IP 层则没有全局变量去代表,只有函数。

![](pics/protocol_detail.png)

从左往右看,是用户界面的角度,分别代表了标识一个套接字的三元组:<地址族,类型,具体协议>,正好是调用 socket 系统函数的 3 个参数。

从右到左看,是内核中 3 个重要的数据结构,从上到下分别是 socket{}、sock{}、sk_buff{},正好是数据流的连接通道。

发送报文时,数据会由 socket{}通过相应的 proto_ops{}把数据传给 sock{},sock{}又通过proto{}把数据传到 sk_buff;反过来,当收到报文时,sk_buff{}通过 net_protocol{}把数据传给 sock{},后者又通过 proto{}把数据传给 socket{},socket{}最后把数据传给用户层。

### Linux 设备管理

设备初始化是我们要分析的第三和第四个大步骤

设备管理的目标是能对所有的外设进行良好的读、写、控制等操作。但是如果众多设备没有一个统一的接口,则不利于开发人员的工作。因此 Linux 采用了类似 UNIX 的方法,使用设备文件来实现这个统一接口。由此可见,设备文件的相关概念是设备管理的最基础部分。

#### 底层 PCI 模块的初始化

PCI 总线是用得最广泛的总线技术,而基于 PCI 总线的网卡设备已经是市场主流,我们就研究一下 PCI 网卡是如何被操控的,以此可以推断出在不同总线技术下驱动程序的实现基础。

驱动程序开发人员使用 module_init 宏来修饰自己驱动程序的第一个函数,促使初始化函数放在第6 段.initcall段中

如果是 PCI 驱动,那么为了做到这一点必须调用一个函数:pci_register_driver

#### 网络设备接口初始化例程

…………跳过，待补充

设备已经被注册了,那么是否就可以工作了呢?不是的,还得靠用户把这些网卡激活。

网卡被激活的时候,它要完成几个非常重用的事情:
1. 挂接中断处理函数(ISR),如果不能为驱动程序申请到中断,那说明要么网卡没插好,要么和其他设备发生了冲突,结果就是设备根本不能用。
2. 创建驱动程序内部接收环和发送缓冲区,网卡一般都要“环”的方式来存放报文。
3. 挂接接口状态扫描定时器,以 poll 的方式轮询接口是否真正 up 或 down。
4. 进一步打开设备特点寄存器,使其可以开始收发报文了

Linux2.6下网络驱动程序的初始化分为 4 个基本步骤:
1. 系统把驱动程序装入内存
2. PCI 为设备选择正确的驱动程序,并分配相应内存数据结构
3. 指定驱动程序如何处理报文格式
4. 用户打开设备使其可以真正工作起来

## 配置系统

### 配置过程分析

ifconfig 命令

strace 命令：追踪命令使用到的系统调用，包括参数

#### socket系统调用

在 BSD socket 层内使用 msghdr{ }结构保存数据;在 INET socket 层以下都使用 sk_buff{ }数据结构保存数据。

```c
struct socket
{
    struct proto_ops
        *ops;
    struct file
        *file;
    struct sock
        *sk;
    wait_queue_head_t wait;
    short type;
    //   ......
};
```

在内核中与 socket 对应的系统调用是 sys_soceket,所谓的创建套接口,就是在 sockfs 这个文件系统中创建一个节点,从 Linux/Unix 的角度来看,该节点是一个文件,不过这个文件具有非普通文件的属性,于是起了一个独特的名字——socket。由于 sockfs 文件系统是系统初始化时就保存在全局指针 sock_mnt中的,所以申请一个 inode 的过程便以 sock_mnt 为参数。

从进程角度看,一个套接口就是一个特殊的已打开文件。现在将 socket 结构的宿主 inode 与文件系统挂上钩,就是分配文件号以及 file 结构在目录数中分配一个 dentry 结构。指向 inode 使 file->f_dentry指向 inode 建立目录项和索引节点(即套接口的节点名)

socket 函数本身,经过 glibc 库对其封装,它将通过 int 0x80 产生一个软件中断(注意不是软中断),由内核导向执行 sys_socket,基本上参数会原封不动地传入内核,它们分别是(1) int family,(2) int type, (3) int protocol。

![](pics/sys_socket.png)

struct socket{}结构由sock_alloc函数创建

VFS 为了使 socket 系统工作——或者说 socket 为了适应 VFS系统框架,socket 提供了 2 个数据结构:super_operations——sockfs_ops 和 file_operations——socket_file_ops。前者是必须的,它创建了 VFS 必需的 inode,使 VFS 可以对其进行文件级别的管理;而后者是可选的,用户层可以使用标准文件系统的操作比如 write( )和 read( )对 socket 对象进行操作,也可以采用系统提供的 send( )和 recv( )接口对 socket 对象进行处理,但二者都归一到网络内部实现代码中。