#  mysql 8.0 修改密码

```bash
#查看默认用户名密码
sudo cat /etc/mysql/debian.cnf

#登陆Mysql,修改密码
mysql -udebian-sys-maint -p
use mysql 
ALTER USER 'root'@'localhost' IDENTIFIED WITH caching_sha2_password BY 'NewPassword';
```