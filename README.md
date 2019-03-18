# docker-compose.yml

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
    image: hellozjf/mleap-controller:1.0.0
    ports:
      - 8080:8080
    volumes:
      - /tmp/models:/models
    networks:
      - my-bridge

networks:
  my-bridge:
    driver: bridge
```

# 上传模型

```
POST 192.168.2.150:8080/mleap1/onlineModel
```

# 获取数据

```
POST 192.168.2.150:8080/mleap1/invokeModel
```

