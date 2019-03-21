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

将该文件拷贝到装有docker的服务器上面，运行`docker-compose up -d`启动相关的服务

![](https://aliyun.hellozjf.com:7004/uploads/2019/3/21/menu.saveimg.savepath20190321141927.jpg)

上面的配置文件中，我只配置了三个mleap服务，如果需要部署多于三个mleap的服务，可以再增加mleap4、mleap5、……

# 使用

## 通过postman使用

### 上线模型

![](https://aliyun.hellozjf.com:7004/uploads/2019/3/21/menu.saveimg.savepath20190321142456.jpg)

### 预测数据（单条）

![](https://aliyun.hellozjf.com:7004/uploads/2019/3/21/menu.saveimg.savepath20190321142857.jpg)

### 预测数据（多条）

![](https://aliyun.hellozjf.com:7004/uploads/2019/3/21/menu.saveimg.savepath20190321143016.jpg)

## 通过java使用

### 上线模型

```
    /**
     * 测试上线模型
     */
    @Test
    public void testOnlineModel() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://aliyun.hellozjf.com:8080/mleap1/onlineModel";
        Resource resource = new ClassPathResource("swModel.zip");
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("file", resource);
        String result = restTemplate.postForObject(url, param, String.class);
        log.debug("result = {}", result);
    }
```

### 预测数据（单条）

```
    /**
     * 测试税务专有词预测
     * 需要导入swModel.zip模型
     * @throws Exception
     */
    @Test
    public void testPredict() throws Exception {
        String url = "http://aliyun.hellozjf.com:8080/mleap1/predict";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>("新个税继续教育专项附加扣除中，扣除范围是怎么规定的？", requestHeaders);

        String result = restTemplate.postForObject(url, requestEntity, String.class);
        JsonNode jsonNode = objectMapper.readTree(result);
        log.debug("result = {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode));
    }
```

预测数据（多条）

```
    /**
     * 测试税务专有词预测
     * 需要导入swModel.zip模型
     * @throws Exception
     */
    @Test
    public void testPredict2() throws Exception {
        String url = "http://aliyun.hellozjf.com:8080/mleap1/predict2";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        String body = "自然人税收管理系统扣缴客户端，导出的劳务报酬所得，一般劳务报酬所得填写什么？\n" +
                "个税工资计算？\n" +
                "1.个体是否需要报税2.什么时间开始报税\n" +
                "住房租金扣除问题？\n" +
                "2019年全年一次性奖金个人所得税如何计算缴纳？\n" +
                "【个人所得税税收政策的热点问题】男女朋友共同购买的房屋，都是共同还款人，房产证是女方的名字，住房贷款利息，男方能扣吗？";
        HttpEntity<String> requestEntity = new HttpEntity<>(body, requestHeaders);

        String result = restTemplate.postForObject(url, requestEntity, String.class);
        JsonNode jsonNode = objectMapper.readTree(result);
        log.debug("result = {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode));
    }
```

