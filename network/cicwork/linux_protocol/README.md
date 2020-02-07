# notes

## 编译内核 

* 内核版本：3.16.65
* 操作系统： ubuntu 18.04

执行`make menuconfig`

提示错误
```bash
qinrui@qinrui:~/Github/linux-3.16.65$ make menuconfig
 *** Unable to find the ncurses libraries or the
 *** required header files.
 *** 'make menuconfig' requires the ncurses libraries.
 *** 
 *** Install ncurses (ncurses-devel) and try again.
 *** 
scripts/kconfig/Makefile:199: recipe for target 'scripts/kconfig/dochecklxdialog' failed
make[1]: *** [scripts/kconfig/dochecklxdialog] Error 1
Makefile:547: recipe for target 'menuconfig' failed
make: *** [menuconfig] Error 2
```

安装所需的库后可以顺利编译。

`sudo apt install ncurses-dev`
