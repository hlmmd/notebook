# 高性能mysql

## MySQL基准测试

基准测试工具

ab 命令

http_load

JMeter

### 案例

## 服务器性能剖析

profiling

什么是性能？响应时间

性能剖析工具

New Relic

xhprof

ifp

## Schema与数据类型优化

数据库设计  beginning database design

选择优化的数据类型

* 更小的通常更好
* 简单就好
* 尽量避免NULL

整数类型

TINYINT SMALLINT MEDIUMINT INT BIGINT

8 16 24 32 64

UNSIGNED

DECIMAL FLOAT DOUBLE

char varchar

binary varbinary

慷慨是不明智的

使用ENUM代替字符串

枚举按照内部存储的整数进行排序

缺点：字符串列表是固定的

日期和时间类型：YEAR DATE

DATETIME YYYYMMDDHHMMSS

TIMESTAMP FROM_UNIXTIME：时间戳转日期

UNIX_TIMESTAMP日期转为时间戳

位数据类型 BIT SET

选择标识符

缓存表和汇总表

影子表

计数器表

ALTER TABLE

修改.frm文件

* 避免过度设计
* 使用小而简单的合适数据类型
* 尽量使用相同的数据类型存储相似或相关的值
* 注意可变长字符串
* 尽量使用整形定义标志列
* 避免使用Mysql已经遗弃的特性
* 小心使用ENUM和SET，避免使用BIT 

