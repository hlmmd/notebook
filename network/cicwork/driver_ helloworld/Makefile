obj-m:=hello.o
PWD:=$(shell pwd)
BUILDSYSTEM_DIR:=/lib/modules/$(shell uname -r)/build

all:
	$(MAKE) -C $(BUILDSYSTEM_DIR) M=$(PWD) modules

clean:
	$(MAKE) -C $(BUILDSYSTEM_DIR) M=$(PWD) clean
