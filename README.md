<p align="center" dir="auto">
    <img src="assets/imgs/title.png" alt="title" />
</p>

<p align="center" dir="auto">
	<a target="_blank" rel="noopener noreferrer nofollow" href=""><img src="https://camo.githubusercontent.com/3ff122b482c6bb1be2597d3490532710d56c933de79e83b08d09ea43ac6b09e4/68747470733a2f2f696d672e736869656c64732e696f2f6d6176656e2d63656e7472616c2f762f636f6d2e74616f62616f2e6172746861732f6172746861732d7061636b6167696e672e737667" alt="maven" style="max-width: 100%;"></a>
	<a target="_blank" rel="noopener noreferrer nofollow" href=""><img src="https://camo.githubusercontent.com/d6deb5fc41d9ac3b6957f5fa6701dd35388ea6b42c40d2ee33074e39f19a2662/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f6c6963656e73652d4170616368652532304c6963656e7365253230322e302d626c75652e737667" alt="AUR" style="max-width: 100%;"></a>
	<a target="_blank" rel="noopener noreferrer nofollow" href=""><img src="https://img.shields.io/badge/version-1.0.0-brightgreen.svg" alt="" style="max-width: 100%;"></a>
	<a target="_blank" rel="noopener noreferrer nofollow" href=""><img src="https://camo.githubusercontent.com/8ce54d9a733db1476537ba4416300074b60cbc6e02df3bc3bd3aea9cf26bb9c7/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f4a444b2d382b2d677265656e2e737667" style="max-width: 100%;"></a>
	<a target="_blank" rel="noopener noreferrer nofollow" href=""><img src="https://camo.githubusercontent.com/60da1a81d368d749148133b4fa42c10c1b6d5db167a935f327ef6dc095df627e/68747470733a2f2f7472617669732d63692e636f6d2f64726f6d6172612f6875746f6f6c2e7376673f6272616e63683d76342d6d6173746572" style="max-width: 100%;"></a>
</p>


------

基于JavaFX、Springboot开发的javafx权限管理脚手架系统，软件架构基于角色的访问控制(Role-Based Access Control)，即RBAC模型，也叫五张表模型。

| 编号 | 1        | 2        | 3        | 4              | 5              |
| ---- | -------- | -------- | -------- | -------------- | -------------- |
| 名称 | 用户表   | 角色表   | 权限表   | 用户角色关系表 | 角色权限关系表 |
| 表名 | sys_user | sys_role | sys_perm | rel_user_role  | rel_role_perm  |

主要功能有：用户管理，角色管理、菜单权限管理、系统设置，以及登录用户修改密码、配置个性菜单等

# 技术栈

javafx、springboot、mybatis-plus、h2、jfoenix、datafx、tilesfx

附：h2数据库jdbc连接格式：

| 连接                                                         | url格式                                                      |
| :----------------------------------------------------------- | :----------------------------------------------------------- |
| [嵌入式（本地）连接](http://www.h2database.com/html/features.html#embedded_databases) | jdbc:h2:[file:][]<databaseName> <br/>jdbc:h2:~/test<br/>jdbc:h2:file:/test<br/>jdbc:h2:file:C:/test (Windows only) |
| [内存数据库（私有）](http://www.h2database.com/html/features.html#in_memory_databases) | jdbc:h2:mem:                                                 |
| [内存数据库（被命名）](http://www.h2database.com/html/features.html#in_memory_databases) | jdbc:h2:mem:<databaseName><br/>jdbc:h2:mem:test              |
| [使用TCP/IP的服务器模式（远程连接）](http://www.h2database.com/html/tutorial.html#using_server) | jdbc:h2:tcp://<server>[:<port>]/[<path>]<databaseName><br/>jdbc:h2:tcp://localhost/~/test<br/>jdbc:h2:tcp://dbserv:8080/~/test |
| [使用SSL/TLS的服务器模式（远程连接）](http://www.h2database.com/html/advanced.html#ssl_tls_connections) | jdbc:h2:ssl://<server>[:<port>]/<databaseName><br/>jdbc:h2:ssl://secureserv:8080/~/test; |
| [使用加密文件](http://www.h2database.com/html/features.html#file_encryption) | jdbc:h2:<url>;CIPHER=[AES\|XTEA]<br/>jdbc:h2:ssl://secureserv/~/test;CIPHER=AES <br/>jdbc:h2:file:~/test;CIPHER=XTEA |
| [文件锁](http://www.h2database.com/html/features.html#database_file_locking) | jdbc:h2:<url>;FILE_LOCK={NO\|FILE\|SOCKET}<br/>jdbc:h2:file:~/test;FILE_LOCK=NO <br/>jdbc:h2:file:~/test;CIPHER=XTEA;FILE_LOCK=SOCKET |
| [仅打开存在的数据库](http://www.h2database.com/html/features.html#database_only_if_exists) | jdbc:h2:<url>;IFEXISTS=TRUE<br/>jdbc:h2:file:~/test;IFEXISTS=TRUE |
| [当虚拟机退出时并不关闭数据库](http://www.h2database.com/html/features.html#do_not_close_on_exit) | jdbc:h2:<url>;DB_CLOSE_ON_EXIT=FALSE                         |
| [用户名和密码](http://www.h2database.com/html/features.html#passwords) | jdbc:h2:<url>[;USER=<username>][;PASSWORD=]<br/>jdbc:h2:file:~/test;USER=sa;PASSWORD= |
| [更新记入索引](http://www.h2database.com/html/features.html#log_index_changes) | jdbc:h2:<url>;LOG=2<br/>jdbc:h2:file:~/sample;LOG=2          |
| [调试跟踪项设置](http://www.h2database.com/html/features.html#trace_options) | jdbc:h2:<url>;TRACE_LEVEL_FILE=<level 0..3><br/>jdbc:h2:file:~/sample;TRACE_LEVEL_FILE=3 |
| [忽略位置参数设置](http://www.h2database.com/html/features.html#ignore_unknown_settings) | jdbc:h2:<url>;IGNORE_UNKNOWN_SETTINGS=TRUE                   |
| [指定文件读写模式](http://www.h2database.com/html/features.html#custom_access_mode) | jdbc:h2:<url>;ACCESS_MODE_LOG=rws;ACCESS_MODE_DATA=rws       |
| [在Zip文件中的数据库](http://www.h2database.com/html/features.html#database_in_zip) | jdbc:h2:zip:<zipFileName>!/<databaseName><br/>jdbc:h2:zip:~/db.zip!/test |
| [兼容模式](http://www.h2database.com/html/features.html#compatibility) | jdbc:h2:<url>;MODE=<databaseType><br/>jdbc:h2:~/test;MODE=MYSQL |
| [自动重连接](http://www.h2database.com/html/features.html#auto_reconnect) | jdbc:h2:<url>;AUTO_RECONNECT=TRUE jdbc:h2:tcp://localhost/~/test;AUTO_RECONNECT=TRUE |
| [自动混合模式](http://www.h2database.com/html/features.html#auto_mixed_mode) | jdbc:h2:<url>;AUTO_SERVER=TRUE<br/>jdbc:h2:~/test;AUTO_SERVER=TRUE |
| [更改其他设置](http://www.h2database.com/html/features.html#other_settings) | jdbc:h2:<url>;<setting>=<value>[;<setting>=<value>...] <br/>jdbc:h2:file:~/test;TRACE_LEVEL_SYSTEM_OUT=3 |

# 功能结构

```text
主页
系统管理
    用户管理
    角色管理
    菜单权限管理
设置
    个人设置
```

其他通用功能将持续更新中…

# 贡献

-   感谢[JFoenix](https://github.com/jfoenixadmin/JFoenix/)、[TilesFX](https://github.com/HanSolo/tilesfx)提供的客户端组件
-   感谢[DataFX](https://guigarage.com/2014/05/datafx-8-0-tutorials)提供的组件关联技术支持
-   感谢[JavaFX中文官网](https://openjfx.cn/)技术支持
-   感谢[sqlcipher](https://www.zetetic.net/sqlcipher/ios-tutorial/)提供的数据库加密技术
-   感谢[fx-falsework](https://github.com/zunhua5201314/fx-falsework)提供的界面模板参考

注：以上无先后顺序排列

# 相关文档

API文档：

[https://openjfx.cn/javadoc/15/](https://gitee.com/link?target=https%3A%2F%2Fopenjfx.cn%2Fjavadoc%2F15%2F)

[https://openjfx.cn/javadoc/14/](https://gitee.com/link?target=https%3A%2F%2Fopenjfx.cn%2Fjavadoc%2F14%2F)

[https://openjfx.cn/javadoc/13/](https://gitee.com/link?target=https%3A%2F%2Fopenjfx.cn%2Fjavadoc%2F13%2F)

[https://openjfx.cn/javadoc/12/](https://gitee.com/link?target=https%3A%2F%2Fopenjfx.cn%2Fjavadoc%2F12%2F)

[https://openjfx.cn/javadoc/11/](https://gitee.com/link?target=https%3A%2F%2Fopenjfx.cn%2Fjavadoc%2F11%2F)

CSS样式参考：

[https://openjfx.cn/javadoc/15/javafx.graphics/javafx/scene/doc-files/cssref.html](https://gitee.com/link?target=https%3A%2F%2Fopenjfx.cn%2Fjavadoc%2F15%2Fjavafx.graphics%2Fjavafx%2Fscene%2Fdoc-files%2Fcssref.html)

FXML样式参考：

[https://openjfx.cn/javadoc/15/javafx.fxml/javafx/fxml/doc-files/introduction_to_fxml.html](https://gitee.com/link?target=https%3A%2F%2Fopenjfx.cn%2Fjavadoc%2F15%2Fjavafx.fxml%2Fjavafx%2Ffxml%2Fdoc-files%2Fintroduction_to_fxml.html)

openfx提供的SceneBuilder可视化布局工具：

https://openjfx.cn/scene-builder/

# 使用

-   执行命令

```sh
java -Dpw.salt=cxxwl96@sina.com -Djasypt.encryptor.password=cxxwl96@sina.com -Dfile.encoding=utf-8 -jar jfx-admin-1.0.0.jar
```

-   用户
    1.  账号：administer（超级管理员），密码：cxxwl96@sina.com
    2.  账号：admin（系统管理员），密码：123456
    3.  账号：test（游客），密码：123456

# Download

Release：[https://github.com/cxxwl96/jfx-admin/releases](https://github.com/cxxwl96/jfx-admin/releases)

# 演示图

<img src="assets/imgs/iShot2022-12-08 00.47.05.png" alt="iShot2022-12-08 00.47.05" style="zoom:50%;" />

<img src="assets/imgs/iShot2022-12-08 00.47.41.png" alt="iShot2022-12-08 00.47.41" style="zoom:50%;" />

<img src="assets/imgs/iShot2022-12-07 23.14.01.png" alt="iShot2022-12-07 23.14.01" style="zoom:50%;" />

<img src="assets/imgs/iShot2022-12-07 23.12.21.png" alt="iShot2022-12-07 23.12.21" style="zoom:50%;" />

<img src="assets/imgs/iShot2022-12-07 23.14.09.png" alt="iShot2022-12-07 23.14.09" style="zoom:50%;" />

<img src="assets/imgs/iShot2022-12-07 23.12.55.png" alt="iShot2022-12-07 23.12.55" style="zoom:50%;" />

<img src="assets/imgs/iShot2022-12-07 23.14.15.png" alt="iShot2022-12-07 23.14.15" style="zoom:50%;" />

<img src="assets/imgs/iShot2022-12-07 23.13.04.png" alt="iShot2022-12-07 23.13.04" style="zoom:50%;" />

<img src="assets/imgs/iShot2022-12-07 23.14.21.png" alt="iShot2022-12-07 23.14.21" style="zoom:50%;" />

<img src="assets/imgs/iShot2022-12-07 23.13.32.png" alt="iShot2022-12-07 23.13.32" style="zoom:50%;" />

<img src="assets/imgs/iShot2022-12-07 23.14.30.png" alt="iShot2022-12-07 23.14.30" style="zoom:50%;" />

<img src="assets/imgs/iShot2022-12-07 23.13.42.png" alt="iShot2022-12-07 23.13.42" style="zoom:50%;" />