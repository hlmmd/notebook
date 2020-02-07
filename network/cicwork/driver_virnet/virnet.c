#include <linux/module.h>
#include <linux/errno.h>
#include <linux/netdevice.h>
#include <linux/version.h>

MODULE_LICENSE("GPL");

// 网络设备对象
static struct net_device *sg_dev;

// 有数据帧要发送时，kernel会调用该函数
static int virnet_send_packet(struct sk_buff *skb,struct net_device *dev)
{
    // 统计已发送的数据包
    dev->stats.tx_packets++;
    // 统计已发送的字节
    dev->stats.tx_bytes+=skb->len;
    // 释放数据帧
    dev_kfree_skb(skb);
    return 0;
}

// 驱动程序支持的操作
static struct net_device_ops sg_ops=
{
    // 发送数据帧
    .ndo_start_xmit=virnet_send_packet,
};

// 驱动程序初始化
static int virnet_init(void)
{
    // 创建一个网络设备，名为“virnet%d"，kernel会自动填写%d为网卡编号

    


#if LINUX_VERSION_CODE >= KERNEL_VERSION(4,4,1)
    // kernel 4.4.0-97上需要四个参数如下
    sg_dev=alloc_netdev(0,"virnet%d", NET_NAME_UNKNOWN, ether_setup);
#else
    sg_dev=alloc_netdev(0,"virnet%d",ether_setup);
#endif
    
    // 该网络设备的操作集
    sg_dev->netdev_ops=&sg_ops;
    // MAC地址是01:02:03:04:05:06
    memcpy(sg_dev->dev_addr,"\x01\x02\x03\x04\x05\x06",6);
    // 注册网络设备
    register_netdev(sg_dev);
    return 0;
}

// 驱动程序销毁
static void virnet_exit(void)
{
    // 注销网络设备
    unregister_netdev(sg_dev);
    // 释放对象
    free_netdev(sg_dev);
}

module_init(virnet_init);
module_exit(virnet_exit);