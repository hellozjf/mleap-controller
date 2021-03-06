# 安装

## 1.1.0及以后

1. 安装docker，docker-compose和harbor，不懂请咨询我
2. 将文件拷贝到服务器的`/opt/docker/mleap`文件夹中，它们分别是[start.sh](https://raw.githubusercontent.com/hellozjf/mleap-controller/master/mleap/start.sh)，[stop.sh](https://raw.githubusercontent.com/hellozjf/mleap-controller/master/mleap/stop.sh)，[clear.sh](https://raw.githubusercontent.com/hellozjf/mleap-controller/master/mleap/clear.sh)，[config.txt](https://raw.githubusercontent.com/hellozjf/mleap-controller/master/mleap/config.txt)，[mleap-controller-1.1.0.jar](https://raw.githubusercontent.com/hellozjf/mleap-controller/master/mleap/mleap-controller-1.1.0.jar)，[README.md](https://raw.githubusercontent.com/hellozjf/mleap-controller/master/mleap/README.md)
3. 阅读README.md进行相应操作

## 1.1.0以前

1. 确保安装了docker，使用`docker --version`查看是否已经安装docker，如果没有安装，请参考 [https://docs.docker.com/install/linux/docker-ce/centos/](https://docs.docker.com/install/linux/docker-ce/centos/) 安装docker
    具体步骤是运行

    ```
    yum install -y yum-utils \
      device-mapper-persistent-data \
      lvm2
    ```

    再运行

    ```
    yum-config-manager \
        --add-repo \
        https://download.docker.com/linux/centos/docker-ce.repo
    ```

    再运行

    ```
    yum install docker-ce docker-ce-cli containerd.io
    ```

    再运行

    ```
    systemctl start docker
    systemctl enable docker
    ```

    此外要确保防火墙已经关闭，SELinux已经关闭

    ```
    systemctl stop firewalld
    systemctl disable firewalld
    
    # 编辑/etc/selinux/config，设置SELINUX=disabled
    ```

    最后运行`docker run hello-world`，如果正常输出表示docker安装成功

    如果出现linux内核报错的话，则需要升级一下内核，参考文档[https://www.cnblogs.com/sexiaoshuai/p/8399599.html](https://www.cnblogs.com/sexiaoshuai/p/8399599.html)

2. 确保安装了docker-compose工具，使用`docker-compose --version`查看是否已经安装了docker-compose，如果没有安装，请参考 [https://docs.docker.com/compose/install/](https://docs.docker.com/compose/install/) 安装docker-compose
    具体步骤是运行`curl -L "https://github.com/docker/compose/releases/download/1.24.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose`，然后再运行`chmod +x /usr/local/bin/docker-compose`

3. 首先在服务器192.168.2.149上面修改`/etc/docker/daemon.json`文件，将其内容更改为`{"insecure-registries": ["192.168.2.150"]}`，使用`systemctl restart docker`重启服务，这样做的目的是能够通过http协议从harbor服务器上面拉取镜像。
    在服务器192.168.2.149的`/opt/docker`目录下新建一个文件夹mleap，然后在文件夹下面新建一个`docker-compose.yml`文件，内容为

    ```
    version: '3'
    
    services:
    
      mleap1:
        image: 192.168.2.150/zrar/mleap-serving:0.9.0-SNAPSHOT
        restart: unless-stopped
        volumes:
          - /opt/models:/models
        networks:
          - my-bridge
    
      mleap2:
        image: 192.168.2.150/zrar/mleap-serving:0.9.0-SNAPSHOT
        restart: unless-stopped
        volumes:
          - /opt/models:/models
        networks:
          - my-bridge
    
      mleap3:
        image: 192.168.2.150/zrar/mleap-serving:0.9.0-SNAPSHOT
        restart: unless-stopped
        volumes:
          - /opt/models:/models
        networks:
          - my-bridge
    
      mleap4:
        image: 192.168.2.150/zrar/mleap-serving:0.9.0-SNAPSHOT
        restart: unless-stopped
        volumes:
          - /opt/models:/models
        networks:
          - my-bridge
    
      mleap5:
        image: 192.168.2.150/zrar/mleap-serving:0.9.0-SNAPSHOT
        restart: unless-stopped
        volumes:
          - /opt/models:/models
        networks:
          - my-bridge
    
      mleap-controller:
        image: 192.168.2.150/zrar/mleap-controller:1.0.5
        restart: unless-stopped
        ports:
          - 8083:8080
        volumes:
          - /opt/models:/models
        networks:
          - my-bridge
        environment:
          - TZ=Asia/Shanghai
    
    networks:
      my-bridge:
        driver: bridge
        ipam:
          driver: default
          config:
            - subnet: 10.1.1.48/28
    
    ```

    这个文件中只能同时使用5个模型，需要使用更多请自行在`docker-compose.yml`后面增加mleap6、mleap7……

4. 使用`docker-compose up -d`开启容器，如果遇到端口冲突，修改`docker-compose.yml`文件中的8081端口为任意未被占用的端口。使用`docker-compose down`删除容器。

# 使用

## 通过Web界面使用（1.1.0及以后）

访问[ip]:8081

### 添加模型

![](https://aliyun.hellozjf.com:7004/uploads/2019/5/24/{FF630569-D804-4263-8A8A-F0BA9C4F7CE2}_20190524100637.jpg)

点击添加按钮，在最下方会出现一行，输入模型名称、模型描述、分词方式，点击保存即可完成添加模型

### 删除模型

![](https://aliyun.hellozjf.com:7004/uploads/2019/5/24/{2709E99B-8E16-4AEB-9ACB-767C32C6EFE2}_20190524100831.jpg)

点击删除按钮，然后再点击保存按钮，即可完成模型删除

### 上线模型

在你想要上传的模型那行，点击上线模型链接，之后选择要上传的文件并确定，文件会自动上传到服务器。模型上线成功之后，会更新模型的MD5值。

![](https://aliyun.hellozjf.com:7004/uploads/2019/5/24/{FD54A351-746D-428E-A6A3-B3F847D4E28C}_20190524101052.jpg)

### 下线模型

在你想要下线的模型那行，点击下线模型链接，服务器会自动下线模型，并删除模型。成功删除模型之后会删除该模型的MD5值

![](https://aliyun.hellozjf.com:7004/uploads/2019/5/24/{DC80829D-66D2-4176-A121-DDCBF612B5A3}_20190524101332.jpg)

### 测试模型

在你想要测试的模型那行，点击测试模型链接，然后会弹出一个对话框，输入你想要测试的文本，点击测试按钮。测试的结果会显示在下面几个文本框中。

![](https://aliyun.hellozjf.com:7004/uploads/2019/5/24/{39C5B953-FFFD-40A6-B143-27CD9ED70FE8}_20190524101618.jpg)

### 下载模型

在你想要下载的模型那行，点击下载按钮，即可将服务器上的模型文件下载到本地

![](https://aliyun.hellozjf.com:7004/uploads/2019/5/24/{C2EFBA08-72B7-4B60-AC8B-307C2F257F48}_20190524101738.jpg)

## 通过Web界面使用（1.0.4 - 1.0.6）

访问[ip]:8081

![](https://aliyun.hellozjf.com:7004/uploads/2019/5/6/{C0D5C297-E6A2-465E-9958-A698BE76C1F4}_20190506163949.jpg)

### 上传模型

首先选择模型文件，然后点击上线，上线成功之后会有提示

### 预测模型

将待预测的文本写到输入框中，点击测试，之后会有测试结果

### 下线模型

点击下线按钮

## 通过Postman使用（1.0.0 - 1.0.3）

RestTemplate代码实现参见test包下面的`SwModelTest`和`YythModelTest`

### 上传模型

![](https://aliyun.hellozjf.com:7004/uploads/2019/4/3/menu.saveimg.savepath20190403141620.jpg)

注意：上传的时候可能会失败，如果失败了请删除服务器上面/opt/models下面的模型，重新上传一边

目前在`test/resources`下面有三个模型，它们分别是

| 模型名称                | 模型含义     |
| ----------------------- | ------------ |
| swModel.zip             | 税务模型     |
| yythModel.zip           | 语音通话     |
| qgfxModel.zip           | 情感分析     |
| QaSatClassification.zip | 税种分类模型 |



### 查看模型对应的路径

访问[http://192.168.2.149:8081/h2](http://192.168.2.149:8081/h2)，修改`JDBC URL`为`jdbc:h2:file:/app/mleap`，修改`User Name`为`root`，修改`Password`为`123456`，点击连接

![](https://aliyun.hellozjf.com:7004/uploads/2019/4/29/{AE3900F1-3C18-4934-8DFC-12260F917993}_20190429104356.jpg)

然后在SQL执行窗口中输入`SELECT * FROM MLEAP_ENTITY `，点击`Run`，之后即可查看模型对应的路径

![](https://aliyun.hellozjf.com:7004/uploads/2019/4/29/{3517F9FA-3771-4C00-80C2-D034129906E7}_20190429104502.jpg)

### 预测模型

<font color="#ff9900">注意，上传模型使用的url，需要和预测模型使用的url保持一致。</font>例如上传时用了`mleap1`，那么预测的时候也要使用`mleap1`

#### 有词性

![](https://aliyun.hellozjf.com:7004/uploads/2019/4/3/menu.saveimg.savepath20190403142005.jpg)

#### 无词性

![](https://aliyun.hellozjf.com:7004/uploads/2019/4/3/menu.saveimg.savepath20190403142127.jpg)

# 程序源码

[mleap-controller](https://github.com/hellozjf/mleap-controller.git)，[mleap-bridge](https://github.com/hellozjf/mleap-bridge.git)

## 源码部署到docker

想要源码部署到docker首先需要做以下两步

1. 自己电脑，DOCKER_HOST设置为`tcp://192.168.56.111:2376`，将192.168.56.111设置为自己的虚拟机IP地址

2. 进入192.168.56.111，编辑`/etc/systemd/system/multi-user.target.wants/docker.service`，改成`ExecStart=/usr/bin/dockerd`，然后编辑`/etc/docker/daemon.json`，修改为

   ```
   {
     "hosts": ["tcp://0.0.0.0:2376","unix:///var/run/docker.sock"],
     "insecure-registries": ["192.168.2.150"]
   }
   ```



之后就能在自己的IDEA里面使用下面命令构造和上传docker镜像了

`mvn clean install`，将工程打包，构造docker镜像，并上传到虚拟机192.168.56.111的docker仓库中

`mvn clean deploy`，将工程打包，构造docker镜像，并上传到docker研发中心仓库192.168.2.150中

## 程序组织结构图

![](https://aliyun.hellozjf.com:7004/uploads/2019/5/24/mleap-controller架构图20190524.png)

# Harbor仓库相关

## 上传镜像到Harbor仓库

其实通过`mvn clean install`已经能够将mleap-controller的镜像上传到研发中心的docker仓库了，下面介绍手动上传的办法。

harbor位于192.168.2.150上面，我们先在192.168.2.150上面登录以便后续能够上传

```
docker login 192.168.2.150
```

确保192.168.2.150上面有需要发布的镜像

```
docker pull combustml/mleap-serving:0.9.0-SNAPSHOT
docker pull hellozjf/mleap-controller:1.0.4
```

给需要发布的镜像打tag，并上传到harbor

```
docker tag combustml/mleap-serving:0.9.0-SNAPSHOT 192.168.2.150/zrar/mleap-serving:0.9.0-SNAPSHOT
docker tag hellozjf/mleap-controller:1.0.4 192.168.2.150/zrar/mleap-controller:1.0.4
docker push 192.168.2.150/zrar/mleap-serving:0.9.0-SNAPSHOT
docker push 192.168.2.150/zrar/mleap-controller:1.0.4
```

## 从仓库中下载镜像

拉取前确保`/etc/docker/daemon.json`文件的内容为`{"insecure-registries": ["192.168.2.150"]}`

```
docker pull 192.168.2.150/zrar/mleap-serving:0.9.0-SNAPSHOT
docker pull 192.168.2.150/zrar/mleap-controller:1.0.5
```

# 版本说明

| 版本  | 内容                                                         |
| ----- | ------------------------------------------------------------ |
| 1.1.0 | 实现动态添加/删除模型，界面美化                              |
| 1.0.6 | 增加税种分类模型                                             |
| 1.0.5 | 纠正mleap_controller的时区，docker-compose.yml规定网段，数据库文件存外部 |
| 1.0.4 | 增加了上线、测试、下线模型的界面                             |
| 1.0.3 | 增加了数据库，以便重启后能自动加载模型，同时更新了qgfxModel.zip |
| 1.0.2 | 修复一个bug，predict既要返回分类序号，也要返回分类名称       |
| 1.0.1 | 补充代码，调通陈晓曦的模型yythModel.zip                      |
| 1.0.0 | 初始版本，调通林镇杰的模型swModel.zip                        |

