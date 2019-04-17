# 安装

1. 确保安装了docker，使用`docker --version`查看是否已经安装docker，如果没有安装，请参考 [https://docs.docker.com/install/linux/docker-ce/centos/](https://docs.docker.com/install/linux/docker-ce/centos/) 安装docker
2. 确保安装了docker-compose工具，使用`docker-compose --version`查看是否已经安装了docker-compose，如果没有安装，请参考 [https://docs.docker.com/compose/install/](https://docs.docker.com/compose/install/) 安装docker-compose
3. 在服务器任意位置新建一个文件夹，然后在文件夹下面新建一个`docker-compose.yml`文件，内容为

    ```
    version: '3'
    
    services:
    
      mleap1:
        image: combustml/mleap-serving:0.9.0-SNAPSHOT
        restart: unless-stopped
        volumes:
          - /tmp/models:/models
        networks:
          - my-bridge
    
      mleap2:
        image: combustml/mleap-serving:0.9.0-SNAPSHOT
        restart: unless-stopped
        volumes:
          - /tmp/models:/models
        networks:
          - my-bridge
    
      mleap3:
        image: combustml/mleap-serving:0.9.0-SNAPSHOT
        restart: unless-stopped
        volumes:
          - /tmp/models:/models
        networks:
          - my-bridge
    
      mleap-controller:
        image: hellozjf/mleap-controller:1.0.2
        restart: unless-stopped
        ports:
          - 8081:8080
        volumes:
          - /tmp/models:/models
        networks:
          - my-bridge
    
    networks:
      my-bridge:
        driver: bridge
    ```

    这个文件中只能同时使用3个模型，需要使用更多请自行在`docker-compose.yml`后面增加mleap4、mleap5……

4. 使用`docker-compose up -d`开启容器，如果遇到端口冲突，修改`docker-compose.yml`文件中的8081端口为任意未被占用的端口。使用`docker-compose down`删除容器。

# 使用

RestTemplate代码实现参见test包下面的`SwModelTest`和`YythModelTest`

## 上传模型

![](https://aliyun.hellozjf.com:7004/uploads/2019/4/3/menu.saveimg.savepath20190403141620.jpg)

注意：上传的时候可能会失败，如果失败了请删除服务器上面/tmp/models下面的模型，重新上传一边

## 预测模型

<font color="#ff9900">注意，上传模型使用的url，需要和预测模型使用的url保持一致。</font>例如上传时用了`mleap1`，那么预测的时候也要使用`mleap1`

### 有词性

![](https://aliyun.hellozjf.com:7004/uploads/2019/4/3/menu.saveimg.savepath20190403142005.jpg)

### 无词性

![](https://aliyun.hellozjf.com:7004/uploads/2019/4/3/menu.saveimg.savepath20190403142127.jpg)

## 问题

<font color="#ed4014">目前不支持重启后自动加载模型，因此重启过docker容器需要重新上传模型</font>

# 程序源码

[https://github.com/hellozjf/mleap-controller.git](https://github.com/hellozjf/mleap-controller.git)

# 版本说明

| 版本  | 内容                                                   |
| ----- | ------------------------------------------------------ |
| 1.0.2 | 修复一个bug，predict既要返回分类序号，也要返回分类名称 |
| 1.0.1 | 补充代码，调通陈晓曦的模型yythModel.zip                |
| 1.0.0 | 初始版本，调通林镇杰的模型swModel.zip                  |

