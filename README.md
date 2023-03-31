# 网络协议测试工具
## 简介
* 网络协议测试工具，目前支持udp、tcp、websocket的协议测试，可以测试远程服务端，也支持开启本地服务端进行测试。
* 支持定时发送、按次数发送。
* 自定义生成随机数据
* 历史记录保存

## 项目架构
* 采用netty框架作为协议的服务端和客户端
* 采用javafx编写GUI界面
## 安装说明
本项目基于jdk15编写,运行本项目请先安装jdk15.
下载release中的rar压缩包,并解压。
解压后将会看到run.bat,protocol_test-0.1.0-beta.jar两个文件以及一个lib目录。
### windows用户
点击run.bat即可运行.
### mac用户和linux用户
在安装目录执行
```
javaw -jar protocol_test-0.1.0-beta.jar
```
命令启动本软件.
### 注意
请不要将jar包和lib放在不同的目录。
## 其他
软件仍在开发中，计划实现的功能开发完成以后，将发布exe版本。
### 预计功能
* mqtt 协议测试
* json格式编辑器
* 自定义协议测试

