<img src="assets/logo/logo.svg" width="80" height="80" alt="icon" align="left"/>

网络协议测试工具
===
简体中文 | [English](./README.md)
## 简介
 简单、易用、快速的多网络协议集成测试工具。  
本软件为每种协议分别提供服务端支持和客户端支持。旨在为物联网行业系统开发者提供快速的数据传输验证。  
对于硬件端开发者，可以启动服务端功能验证硬件发送的数据;
对于软件端开发者，可以启动客户端功能模拟硬件数据的发送进行快速开发、和问题定位。  
 目前支持udp、tcp、websocket的协议测试。
## 预览
* 服务端预览
<img src="assets/image/prview-server.png" width="600px">

* 客户端预览
<img src="assets/image/preview-client.png" width="600px">

## 项目架构
* 采用netty框架作为协议的服务端和客户端
* 采用javafx编写GUI界面
## 功能介绍
* 支持数据定时发送、按次数发送。
* 支持自定义格式以及使用JavaScript生成随机数据。
* 支持JSON,HEX,Base64的编码转换发送和显示。
* 支持历史记录保存。
## 安装说明
本项目基于jdk15编写，运行本项目请先安装jdk15。
[下载openjdk15](https://jdk.java.net/java-se-ri/15)
> 项目仍在开发中，故暂时采用安装jdk的方式。项目发布正式版后，
> 将会将对应jre一起打包，介时将不必在手动安装jdk。

### 下载
下载release中的压缩包，并解压。  
解压后将会看到 **run.bat，protocol_test[版本号].jar** 两个文件以及一个 **lib** 文件夹。
### 运行
#### windows
点击run.bat即可运行。
#### mac和linux
由于javafx是非跨平台的,所以在mac和linux下需要clone源代码后编译成jar包,执行以下命令启动.
```
javaw -jar filename.jar
```
filename 为对应jar包的名字。

 以下是一个示例:
```
javaw -jar protocol_test-0.2.0-beta.jar
```
> **注意** <br>
>请不要将jar包和lib放在不同的目录。
## 使用说明
### 连接
#### 服务端
输入本机端口后在对应端口开启服务。
#### 客户端
输入要连接的主机ip地址后点击连接即可。
> **注意** <br>
> 不需要输入协议的名称。 <br>
> 例如想要连接的主机地址为: tcp://127.0.0.1:10250 <br>
> 只需要输入: 127.0.0.1:10250 即可。
### 数据发送
#### 固定文本
在输入框中输入要发送的数据，点击发送即可发送数据。  
点击定时发送将默认1秒发送一次数据，再次点击后停止发送。
#### 随机文本
点击输入框左侧设置按钮进入发送设置页面进行设置。
#### 发送设置页面
可以在这里设置数据的发送方式，发送间隔、发送次数以及发送文本类型等。  
通过占位符 **%d** ，**%f** 可以在你想要的位置插入随机的整型和浮点型数据。
>浮点型数据可指定小数位数。如"%.2f"表示保留2位小数。

输入自定义格式文本后，点击确认将会弹出数值范围设置面板，可以设置随机数据的范围。
#### 消息页面
一条消息由4个部分组成：发送时间、收发主机、消息长度、消息内容。  
* 可以点击侧边栏的显示设置按钮自定义四个部分的显示；  
* 对于长消息，可以点击侧边栏的软回车按钮来设置是否进行换行;    
* 若历史消息不在使用，也可以点击垃圾桶图标清空历史消息。
* 选中消息后，点击右键弹出菜单，可以复制消息，可以选择复制整条消息或只复制消息内容部分。
#### 客户端组
服务端页面的客户端组部分显示了与当前服务建立连接的所有客户端。
可以多选客户端，批量向它们发送消息、断开连接。  
>多选方式为按住ctrl键后用鼠标选择客户端。

由于UDP协议是无连接的，所以UDP服务端的客户端组会在数据包到达时进行添加，而不会销毁，
若确定UDP客户端已关闭，请手动清除。
#### 保存
退出程序时将会弹出保存对话框，点击确认后即可保存配置(收发的历史消息将不会被保存)。  
下次启动程序时将会自动加载配置。
#### 工作流预览

 https://github.com/lao-boli/protocol_test/assets/66947448/3764c423-b5b0-459c-9f5c-6798a8275bcf
 
## 预计开发功能
* mqtt协议测试
* 环境变量配置
* wss协议测试
* 自定义协议测试
