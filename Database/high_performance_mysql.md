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

## 创建高性能的索引

索引是存储引擎用于快速找到记录的一种数据结构。

B-tree索引限制：

* 如果不是按照索引的最左列开始查找，则无法使用索引
* 不能跳过索引中的列
* 如果查询中有某个列的范围查询，则其右边所有的列都无法使用索引优化查找

哈希索引

* 索引只包括哈希值和行指针，不包括字段。所以不能用索引中的值来避免读取行
* 哈希索引并不是按照索引值顺序存储的，无法用于排序
* 哈希索引不支持部分索引列匹配查找
* 哈希索引只支持等值的比较
* 速度快
* 哈希冲突

在B-tree基础上创建一个伪hash索引。

例如查找url，url较长，建索引开销大，可以计算hash值辅助索引

select id from url where url = 'xxx' and url_crc=CRC32('xxx)

根据CRC32进行哈希索引，速度块。如果直接用url建立索引，会慢很多

缺点：需要维护哈希值。可以使用触发器实现

不要使用SHA1和MD5函数，会浪费存储空间和计算时间

空间数据索引(R-Tree)

全文索引：查找文本中的关键词，而不是直接比较索引中的值

索引的优点

* 大大减少了服务器需要扫描的数据量
* 帮助服务器避免排序和临时表
* 随机IO变为顺序IO

Relational Database Index Design and the optimizers

高性能的索引策略

独立的列：索引不能是表达式的一部分，也不能是函数的参数

前缀索引和索引选择性

多列索引

把where条件里的列都建上索引？

选择合适的索引顺序

聚簇索引

……

……

* 单行访问是很慢的
* 按顺序访问范围数据是很快的
* 索引覆盖查询是很快的

## 查询性能优化

查询优化、索引优化、表结构优化

优化数据访问

是否向数据库请求了不需要的数据

* 查询不需要的记录
* 取出过多的列
* 多表关联时返回全部列
* 重复查询相同的数据

Mysql 最简单的  查询开销三个指标：

* 响应时间
* 扫描的行数
* 返回的行数

where条件执行结果：

* 在索引中使用where条件来过滤不匹配的记录。在存储引擎层完成
* 使用索引覆盖扫描（在Extra列中出现了using index)来返回记录，在MySql服务器层完成。
* 从数据表中返回数据，然后过滤不满足条件的记录（在Extra列中出现using where)。Mysql服务器层完成，先读取出数据再过滤。

重构查询的方式

一个复杂查询还是多个简单查询

切分查询 将删除旧的数据切分成较小的查询，如一次删除1w行数据

分解关联查询 将查询关联放到应用代码中 方便缓存

查询执行的基础

* 客户端发送一条查询给服务器
* 服务器先检查查询缓存
* 服务器进行SQL解析，预处理，由优化器生成对应的执行计划
* Mysql根据执行计划，调用存储引擎的API
* 将结果返回给客户端

……

## Mysql高级特性

分区表

……

## 优化服务器设置

Mysql配置文件位置：/etc/my.cnf /etc/mysql/my.cnf

```bash
#查找mysql可执行文件位置
which mysqld

#查找mysql默认配置文件位置
/path/to/mysqld --verbose --help |grep -A 1 'Default options'
```

……
