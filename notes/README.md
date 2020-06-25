# notes

## win10 关闭 Antimalware Service Executable

打开注册表，路径\HKEY_LOCAL_MACHINE\SOFTWARE\Policies\Microsoft\Windows Defender

右键新建两个DWORD，分别命名为DisableAntiSpyware和DisableAntiVirus，值都设置为1

![](https://raw.githubusercontent.com/hlmmd/cdnstore/master/2020/Antimalware/fxxkwindowsdefender.jpg)