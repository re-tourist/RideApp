# Final Project Server

## 服务器

101.37.79.220

root

Aa@123456

## ssh连接服务器

```bash
ssh root@101.37.79.220
```

## 上传文件

```
scp -r file_name root@ip:/dir

scp -r backend root@101.37.79.220:/home/
```

## 后端启动

```bash
cd /home/backend
mvn clean package -DskipTests

nohup java -jar target/backend-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
```

## 查看日志

```
tail -f app.log
```

## 查看进程

如果要重新执行后端记得先杀死之前的进程，减少不必要的资源浪费

```
ps -ef | grep java
kill pid
```

