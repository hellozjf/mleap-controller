# 安装

新建一个文件`docker-compose.yml`

```
version: '3'

services:

  mleap1:
    image: combustml/mleap-serving:0.9.0-SNAPSHOT
    volumes:
      - /tmp/models:/models
    networks:
      - my-bridge

  mleap2:
    image: combustml/mleap-serving:0.9.0-SNAPSHOT
    volumes:
      - /tmp/models:/models
    networks:
      - my-bridge

  mleap3:
    image: combustml/mleap-serving:0.9.0-SNAPSHOT
    volumes:
      - /tmp/models:/models
    networks:
      - my-bridge

  mleap-controller:
    image: hellozjf/mleap-controller:1.0.2
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

将该文件拷贝到装有docker的服务器上面，运行`docker-compose up -d`启动相关的服务

![](https://aliyun.hellozjf.com:7004/uploads/2019/4/3/menu.saveimg.savepath20190403142827.jpg)

上面的配置文件中，我只配置了三个mleap服务，如果需要部署多于三个mleap的服务，可以再增加mleap4、mleap5、……

<font color="#2db7f5">目前我已经将服务部署在192.168.2.150上面了，所以下面就以192.168.2.150来演示如何使用。</font>

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



# 版本说明

| 版本  | 内容                                                   |
| ----- | ------------------------------------------------------ |
| 1.0.2 | 修复一个bug，predict既要返回分类序号，也要返回分类名称 |
| 1.0.1 | 补充代码，调通陈晓曦的模型yythModel.zip                |
| 1.0.0 | 初始版本，调通林镇杰的模型swModel.zip                  |

