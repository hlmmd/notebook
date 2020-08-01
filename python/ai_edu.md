# 基本python语法

## 循环

```python
for i in range(len(f)): # 以索引做循环变量
    print(i, type(f[i]))

for i, v in enumerate(f): # 上例的简化版本
    print(i, type(v))
```

## 排序

使用lambda表达式指定排序关键字

```
great.sort(key=lambda person: person['age'], reverse=True)
```




