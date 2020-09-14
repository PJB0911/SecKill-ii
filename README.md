# 基于 SpringBoot 高并发商城秒杀系统（性能优化）


* [项目简介](#项目简介)
  * [什么是秒杀](#什么是秒杀)
  * [秒杀场景特点](#秒杀场景特点)
  * [秒杀架构设计理念](#秒杀架构设计理念)
  * [需要解决的问题](#需要解决的问题)
* [项目整体架构](#项目整体架构)
* [进阶项目核心知识点](#进阶项目核心知识点)
* [基础项目回顾](#基础项目回顾)
  * [项目结构—数据模型](#项目结构数据模型)
  * [项目结构—DAO/Service/Controller结构](#项目结构daoservicecontroller结构)
  * [全局异常处理类](#全局异常处理类)
* [项目云端部署](#项目云端部署)
  * [服务器配置](#服务器配置)
  * [数据库部署](#数据库部署)
  * [项目打包](#项目打包)
  * [deploy启动脚本](#deploy启动脚本)
  * [jmeter性能压测](#jmeter性能压测)
* [单机服务器并发容量问题和优化](#单机服务器并发容量问题和优化)
  * [项目架构](#项目架构)
  * [并发容量问题](#并发容量问题)
  * [Spring Boot内嵌Tomcat线程优化](#spring-boot内嵌tomcat线程优化)
  * [Spring Boot内嵌Tomcat网络连接优化](#spring-boot内嵌tomcat网络连接优化)
  * [优化效果](#优化效果)
  * [小结](#小结)
  * [下一步优化方向](#下一步优化方向)
* [分布式扩展优化](#分布式扩展优化)
  * [项目架构](#项目架构-1)
  * [Nginx部署前端静态资源](#nginx部署前端静态资源)
  * [Nginx反向代理处理Ajax请求](#nginx反向代理处理ajax请求)
  * [开启Tomcat Access Log验证](#开启tomcat-access-log验证)
  * [Nginx反向代理长连接优化](#nginx反向代理长连接优化)
  * [分布式扩展后的效果](#分布式扩展后的效果)
  * [扩展——Nginx高性能原因](#扩展——Nginx高性能原因)
	* [1.epoll多路复用](#1-epoll多路复用)
	* [2.master-worker进程模型](#2-masterworker进程模型)
	* [3.协程机制](#3-协程机制)
  * [小结](#小结-1)
  * [下一步优化方向](#下一步优化方向-1)
* [分布式会话](#分布式会话)
  * [基于Cookie传输SessionId](#基于cookie传输sessionid)
  * [基于Token传输类似SessionId](#基于token传输类似sessionid)
  * [小结](#小结-2)
  * [下一步优化方向](#下一步优化方向-2)
* [查询优化之多级缓存](#查询优化之多级缓存)
  * [项目架构](#项目架构-2)
  * [优化商品查询接口—Redis缓存](#优化商品查询接口—Redis缓存)
    * [缓存序列化格式问题](#缓存序列化格式问题)
    * [时间序列化格式问题](#时间序列化格式问题)
  * [优化商品查询接口—本地热点缓存](#优化商品查询接口本地热点缓存)
    * [本地缓存缺点](#本地缓存缺点)
  * [缓存优化后的效果](#缓存优化后的效果)
  * [Nginx Proxy Cache缓存](#nginx-proxy-cache缓存)
    * [Nginx Proxy Cache缓存效果](#nginx-proxy-cache缓存效果)
  * [Nginx lua脚本](#nginx-lua脚本)
    * [lua脚本实战](#lua脚本实战)
  * [OpenResty—Shared dict缓存](#openRestyShared-dict缓存)
    * [Shared dict缓存效果](#shared-dict缓存效果)
  * [OpenResty—直接读取Redis缓存](#openResty直接读取Redis缓存)
  * [缓存雪崩、缓存穿透、缓存更新](#缓存雪崩缓存穿透缓存更新)
  * [小结](#小结-3)
  * [下一步优化方向](#下一步优化方向-3)
* [查询优化之页面静态化](#查询优化之页面静态化)
  * [项目架构](#项目架构-3)
  * [CDN](#cdn)
    * [CDN使用](#cdn使用)
    * [CDN优化效果](#cdn优化效果)
    * [CDN深入](#cdn深入)
   	  * [cache controll响应头](#1-cache-controll响应头)
   	  * [浏览器三种刷新方式](#2-浏览器三种刷新方式)
      * [自定义缓存策略](#3-自定义缓存策略)
  * [全页面静态化](#全页面静态化)
    * [phantomJS实现全页面静态化](#phantomjs实现全页面静态化)
  * [小结](#小结-4)
  * [下一步优化方向](#下一步优化方向-4)
* [查询优化效果总结](#查询优化效果总结)
  * [Tomcat优化](#Tomcat优化)
  * [分布式扩展优化](#分布式扩展优化-1)
  * [缓存优化](#缓存优化)
  * [CDN优化](#CDN优化)
* [交易优化之缓存库存](#交易优化之缓存库存)
  * [交易接口瓶颈](#交易接口瓶颈)
  * [交易验证优化](#交易验证优化)
    * [用户校验缓存优化](#用户校验缓存优化)
    * [活动校验缓存优化](#活动校验缓存优化)
    * [缓存优化后的效果](#缓存优化后的效果)
  * [库存扣减优化](#库存扣减优化)
    * [索引优化](#索引优化)
    * [库存扣减缓存优化](#库存扣减缓存优化)
      * [1 RocketMQ](#rocketmq)
      * [2 同步数据库库存到缓存](#同步数据库库存到缓存)
      * [3 同步缓存库存到数据库（异步扣减库存）](#同步缓存库存到数据库（异步扣减库存）)
      * [4 异步扣减库存存在的问题](#异步扣减库存存在的问题)
  * [小结](#小结-5)
  * [下一步优化方向](#下一步优化方向-5)
* [交易优化之事务型消息](#交易优化之事务型消息)
  * [异步消息发送时机问题](#异步消息发送时机问题)
    * [解决方法](#解决方法)
  * [事务提交问题](#事务提交问题)
    * [解决方法](#解决方法-1)
  * [事务型消息](#事务型消息)
  * [更新下单流程](#更新下单流程)
  * [小结](#小结-6)
  * [下一步优化方向](#下一步优化方向-6)
* [库存流水](#库存流水)
  * [下单操作的处理](#下单操作的处理)
  * [UNKNOWN状态处理](#unknown状态处理)
  * [库存售罄处理](#库存售罄处理)
  * [防止同一用户多次秒杀下单](#防止同一用户多次秒杀下单)
  * [消息重复问题](#消息重复问题)
  * [小结](#小结-7)
    * [可以改进的地方](#可以改进的地方)
  * [下一步优化方向](#下一步优化方向-7)
* [流量削峰](#流量削峰)
  * [业务解耦—秒杀令牌](#业务解耦秒杀令牌)
  * [限流—令牌大闸](#限流令牌大闸)
    * [令牌大闸限流缺点](#令牌大闸限流缺点)
  * [限流—队列泄洪](#限流队列泄洪)
  * [小结](#小结-8)
  * [下一步优化方向](#下一步优化方向-8)
* [防刷限流](#防刷限流)
  * [验证码技术](#验证码技术)
  * [限流方案—限并发](#限流方案限并发)
  * [限流方案—令牌桶/漏桶](#限流方案令牌桶漏桶)
    * [令牌桶](#令牌桶)
    * [漏桶](#漏桶)
    * [区别](#区别)
  * [限流力度](#限流力度)
  * [限流范围](#限流范围)
  * [RateLimiter限流实现](#ratelimiter限流实现)
  * [防刷技术](#防刷技术)
    * [传统防刷技术](#传统防刷技术)
    * [黄牛为什么难防](#黄牛为什么难防)
    * [防黄牛方案](#防黄牛方案)
  * [小结](#小结-9)
  * [交易优化效果总结](#交易优化效果总结)
  * [总结—下单流程](#总结下单流程)
  * [问题汇总](#问题汇总)

------


## 开发工具 
IntelliJ IDEA 2019.3.3 x64

## 开发环境				
| JDK | Maven | Mysql |SpringBoot | Nginx | RocteqMQ | JMeter | Redis | CentOS |
|--|--|--|--|--|--|--|--|--|
|1.8 | 3.6.3 | 5.7 | 2.1.5.RELEASE | 1.16.1 | 4.7.1 | 5.3 | 5.0.8 | 7.4.6 |


## 项目简介
本项目是高并发商城秒杀系统的进阶项目，主要是模拟应对大并发场景下，如何完成商品的秒杀业务，以及针对秒杀场景下为应对大并发所做的优化。基础业务功能见[商城秒杀系统基本业务SecKill-i](https://github.com/PJB0911/SecKill-i)。

### 什么是秒杀

秒杀场景一般会在电商网站举行一些活动或者节假日在12306网站上抢票时遇到。对于电商网站中一些稀缺或者特价商品，电商网站一般会在约定时间点对其进行限量销售，因为这些商品的特殊性，会吸引大量用户前来抢购，并且会在约定的时间点同时在秒杀页面进行抢购。

### 秒杀场景特点

- 秒杀时大量用户会在同一时间同时进行抢购，网站瞬时访问流量激增。
- 秒杀一般是访问请求数量远远大于库存数量，只有少部分用户能够秒杀成功。
- 秒杀业务流程比较简单，一般就是下订单减库存。
- 秒杀商品，一般是定时上架，低廉价格，大幅推广。

### 秒杀架构设计理念

- **限流**： 鉴于只有少部分用户能够秒杀成功，所以要限制大部分流量，只允许少部分流量进入服务后端。
- **削峰**：对于秒杀系统瞬时会有大量用户涌入，所以在抢购一开始会有很高的瞬间峰值。高峰值流量是压垮系统很重要的原因，所以如何把瞬间的高流量变成一段时间平稳的流量也是设计秒杀系统很重要的思路。实现削峰的常用的方法有利用缓存和消息中间件等技术。
- **异步处理**：秒杀系统是一个高并发系统，采用异步处理模式可以极大地提高系统并发量，其实异步处理就是削峰的一种实现方式。
- **内存缓存**：秒杀系统最大的瓶颈一般都是数据库读写，由于数据库读写属于磁盘IO，性能很低，如果能够把部分数据或业务逻辑转移到内存缓存，效率会有极大地提升。
- **水平拓展**：当然如果想支持更多用户，更大的并发，最好就将系统设计成弹性可拓展的。拓展机器,像淘宝、京东等双十一活动时会增加大量机器应对交易高峰。

### 需要解决的问题：

- 云端部署，性能压测
- 单机服务器优化：Tomcat并发容量
- 分布式扩展：Nginx反向代理，负载均衡，分布式会话
- 查询性能优化：商品页面缓存、数据热点缓存（多级缓存：redis/guava/nginx）
- 查询性能优化：页面静态化(CDN)，前后端分离
- 交易性能优化：库存缓存、异步处理（RocketMQ）、缓存一致性
- 流量削峰：秒杀令牌，秒杀大闸，队列泄洪
- 接口安全：隐藏秒杀地址（即秒杀令牌）、防刷限流


### 参考：
- [敖丙带你设计秒杀系统](https://mp.weixin.qq.com/s/KWb3POodisbOEsQVblsoGw)
- [全栈秒杀系统设计](https://mp.weixin.qq.com/s/RRHN8t017ofOvb4nvlCStg)
- [高性能高并发商品秒杀系统设计与优化](https://github.com/Grootzz/seckill)
- [互联网Java秒杀系统设计与架构](https://github.com/qiurunze123/miaosha)
------

## 项目整体架构

![](https://github.com/PJB0911/SecKill-ii/blob/master/images/%E7%A7%92%E6%9D%80%E6%9E%B6%E6%9E%84.jpg)


## 进阶项目核心知识点

![](https://github.com/PJB0911/SecKill-ii/blob/master/images/points.png)

------

## 基础项目回顾

### 项目结构—数据模型

![](https://github.com/PJB0911/SecKill-ii/blob/master/images/models.png)

![](https://github.com/PJB0911/SecKill-ii/blob/master/images/datamodels.png)

### 项目结构—DAO/Service/Controller结构

![](https://github.com/PJB0911/SecKill-ii/blob/master/images/classmodels.png)

### 全局异常处理类

在[基础项目SecKill-i](https://github.com/PJB0911/SecKill-i)中，抛出的`BizException`会被`BaseController`拦截到，并进行相应处理。但是如果前端发送到后端的URL找不到，即**404/405错误**，此时根本进入不了后端的`Controller`，需要处理。

1  新建一个`controller.GlobalExceptionHandler`的类，整个类加上`@ControllerAdvice`接口，表示这是一个**AOP增强类**。

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public CommonReturnType doError(HttpServletRequest request, HttpServletResponse response,Exception ex){
        //开发过程中使用 ex.printStackTrace();
        Map<String,Object> responseData=new HashMap<>();
        if(ex instanceof BizException){
            BizException bizException=(BizException)ex;
            responseData.put("errCode",bizException.getErrCode());
            responseData.put("errMsg",bizException.getErrMsg());
        }else if(ex instanceof ServletRequestBindingException){
            //@RequestParam是必传的，如果没传，就会触发这个异常
            responseData.put("errCode", EmBizError.UNKNOWN_ERROR.getErrCode());
            responseData.put("errMsg","URL绑定路由问题");
        }else if(ex instanceof NoHandlerFoundException){
            responseData.put("errCode", EmBizError.UNKNOWN_ERROR.getErrCode());
            responseData.put("errMsg","没有找到对应的访问路径");
        }else{
            responseData.put("errCode", EmBizError.UNKNOWN_ERROR.getErrCode());
            responseData.put("errMsg",EmBizError.UNKNOWN_ERROR.getErrMsg());
        }
        return CommonReturnType.create(responseData,"fail");
    }
}
```

2 在**application.properties**配置文件中添加：

```properties
#处理404/405
spring.mvc.throw-exception-if-no-handler-found=true
spring.resources.add-mappings=false
```

如此，404/405错误会触发`NoHandlerFoundException`，然后被`GlobalExceptionHandler`捕获到。

------

## 项目云端部署

### 服务器配置

**阿里云ECS服务器**
- 1台用于mysql/redis/RocketMQ服务器（2核4G）
- 1台用于Nginx反向代理服务器（2核4G）
- 3台用于秒杀后端服务器（2核4G）

**阿里云CDN服务器**：页面静态化

参考[文件配置](https://github.com/PJB0911/SecKill-ii/tree/master/%E5%8F%82%E8%80%83%E6%96%87%E4%BB%B6)

### 数据库部署

使用`mysqldump -uroot -ppassword --databases dbName`指令，即可将开发环境的数据库dump成SQL语句。在云端服务器，直接用MySQL运行dump出来的SQL语句即可。

### 项目打包

本项目打成`jar`包，在服务器直接用`java -jar`运行。
1. `maven`打`jar`包首先需要添加以下属性，以便在打包的时候知道JDK的位置，不然报错。

```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>1.7</maven.compiler.source>
    <maven.compiler.target>1.7</maven.compiler.target>
    <JAVA_HOME>C:/Program Files/Java/jdk1.8.0_201</JAVA_HOME>
</properties>
```

2. 添加`spring-boot-maven-plugin`插件，使打包后的文件，能够找到Spring Boot的入口类，即`App.java`。

```xml
<plugins>
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <configuration>
          <compilerArguments>
            <verbose />
            <bootclasspath>${JAVA_HOME}/jre/lib/rt.jar</bootclasspath>
          </compilerArguments>
          <source>1.8</source>
          <target>1.8</target>
        </configuration>
    </plugin>
    <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
    </plugin>
</plugins>
```

3. 在开发目录执行`mvn clean package`即会清空`target`并打成`jar`包。

### deploy启动脚本

线上环境需要**更改一些配置**，比如在`9090`端口部署等等。Spring Boot支持在线上环境中使用`spring.config.additional-location`指定线上环境的配置文件，而不是打到`jar`包里的配置文件`application.properties`。

新建一个`sh`文件：

```shell
nohup java -Xms400m -Xmx400m -XX:NewSize=200m -XX:MaxNewSize=200m -jar miaosha.jar --spring.config.additional-location=/usr/projects/application.properties
```

使用`./deploy.sh &`即可在后台启动，使用`tail -f nohup.out`即可查看项目启动、运行的信息。


### jmeter性能压测

本项目使用`jmeter`来进行并发压测。使用方法新建一个线程组，添加需要压测的接口地址，查看结果树和聚合报告。
![](https://github.com/PJB0911/SecKill-ii/blob/master/images/Jmeter.png)

------

## 单机服务器并发容量问题和优化

### 项目架构

![](https://github.com/PJB0911/SecKill-ii/blob/master/images/frame1.png)


### 并发容量问题

使用`pstree -p pid | wc -l`命令可以查看Java进程一共维护了多少个线程，在没有压测的时候，Tomcat维护了28个线程（不同机器该值不同）；进行压测的时候，Tomcat维护的线程数量猛增至200+个。

使用`top -H`命令可以查看CPU的使用情况，主要关注`us`，用户进程占用的CPU；`sy`，内核进程占用的CPU；`load average`，反映了CPU的负载强度，1,5,15分钟load平均值，0代表通常，1代表打满，1+代表等待阻塞；`memory`：free空闲内存，used使用内存。

在**当前线程数量**的情况下，发送100个线程，CPU的压力**不算太大**，所有请求都得到了处理；而发送**5000**个线程，大量请求报错，默认的线程数量不够用了，可见需要提高Tomcat维护的线程数。


### Spring Boot内嵌Tomcat线程优化

高并发条件下，就是要榨干服务器的性能，而Spring Boot内嵌Tomcat默认的线程设置比较“温柔”——默认**最大等待队列**为100，默认**最大可连接**数为10000，默认**最大工作线程**数为200，默认**最小工作线程**数为10。当请求超过200+100后，会拒绝处理；当连接超过10000后，会拒绝连接。对于最大连接数，一般默认的10000就行了，而其它三个配置，则需要根据需求进行优化。

在`application.properties`里面进行修改：

`server.tomcat.accept-count=1000`

`server.tomcat.max-threads=800`

`server.tomcat.min-spare-threads=100`

`server.tomcat.max-connections=10000`（默认）

这里**最大等待队列**设为1000，**最大工作线程**数设为800，**最小工作线程**数设为100。

**等待队列不是越大越好**，一是受到内存的限制，二是大量的出队入队操作耗费CPU性能。

**最大线程数不是越大越好**，因为线程越多，CPU上下文切换的开销越大，存在一个“经验值”，对于一个4核8G的服务器，经验值是800-1000,1000以上会花费大量时间在cpu调度上。

而最小线程数设为100，则是为了应付一些**突发情况**。

当正常运行时，Tomcat维护了100个线程，而当压测时，线程数量猛增到800多+个。


### Spring Boot内嵌Tomcat网络连接优化

当然Spring Boot并没有把内嵌Tomcat的所有配置都导出。一些配置需要通过` WebServerFactoryCustomizer<ConfigurableWebServerFactory>`接口来实现自定义。

这里需要自定义`KeepAlive`长连接的配置，减少客户端和服务器的连接请求次数，避免重复建立连接，提高性能。

```java
@Component
public class WebServerConfiguration implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {
    @Override
    public void customize(ConfigurableWebServerFactory factory) {
        //使用对应工厂类提供的接口，定制化Tomcat connector
        ((TomcatServletWebServerFactory) factory).addConnectorCustomizers(new TomcatConnectorCustomizer() {
            @Override
            public void customize(Connector connector) {
                Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
                //定制化KeepAlive Timeout为30秒
                protocol.setKeepAliveTimeout(30000);
                //10000个请求则自动断开
                protocol.setMaxKeepAliveRequests(10000);
            }
        });
    }
}
```


### 优化效果

未调整线程数之前（2核CPU），200*50个请求，TPS在**150**左右，平均响应**1000毫秒**。调整之后，TPS在**250**左右，平均响应**400**毫秒。



### 小结

这一节通过`pstree -p pid | wc -l`和`top -H`指令，配合`jmeter`压测工具：

1. 发现了Spring Boot内嵌Tomcat的**线程容量问题**。通过在Spring Boot配置文件中添加配置项，提高了Tomcat的等待队列长度、最大工作线程、最小工作线程，榨干服务器性能。
2. Spring Boot内嵌Tomcat默认使用`HTTP 1.0`的**短连接**，由于Spring Boot并没有把所有Tomcat配置都暴露出来，所以需要编写一个配置类使用`HTTP 1.1`的**长连接**。

### 下一步优化方向

1. 服务器进行**分布式扩展**
2. **优化SQL查询**，比如添加索引。

------

## 分布式扩展优化

### 项目架构

![](https://github.com/PJB0911/SecKill-ii/blob/master/images/frame3.png)

### Nginx部署前端静态资源

**负载均衡**：将请求分摊到多个服务器上进行执行，保证所有后端服务器都将性能充分发挥，从而保持服务器集群的整体性能最优。[Nginx参考资料](https://blog.csdn.net/yao_it/article/details/86597499)

购买域名，将Nginx服务器的ip和域名miaoshaserver绑定

用户通过`nginx/html/resources`访问前端静态页面。而Ajax请求则会通过Nginx反向代理到3台不同的秒杀应用服务器，实现动静分离。

将静态资源上传到Nginx服务器相应目录`html/resource`，并修改`nginx.conf`中的

```text
location /resources/ {
	alias /usr/local/openresty/nginx/html/resources/; 
	index index.html index.html;
}
```

用户就能通过```http://miaoshaserver/resources/```访问到Nginx服务器上的静态页面。

### Nginx反向代理处理Ajax动态请求

Ajax请求通过Nginx服务器反向代理到3台应用服务器，因为3台服务器配置相同，采用**加权轮询**实现负载均衡。[负载均衡算法](https://www.cnblogs.com/diantong/p/11208508.html)

在`nginx.conf`里面添加以下字段：

```text
upstream backend_server{
    server miaoshaApp1_ip weight=1;
    server miaoshaApp2_ip weight=1;
	server miaoshaApp3_ip weight=1;
}
...
server{
    location / {
        proxy_pass http://backend_server;
        proxy_set_header Host $http_host:$proxy_port;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

这样，通过`http://miaoshaserver`访问Nginx服务器，请求会被均衡代理到下面的3个秒杀应用服务器上。

### 开启Tomcat Access Log验证

开启这个功能可以查看是哪个IP发过来的请求，在`application.properties`里面添加，非必须。

`server.tomcat.accesslog.enabled=true`

`server.tomcat.accesslog.directory=/opt/java/miaosha/tomcat`

`server.tomcat.accesslog.pattern=%h %l %u %t "%r" %s %b %D`

### Nginx反向代理长连接优化

Nginx服务器与**前端**的连接是**长连接**，但是与后端的**代理服务器**，默认是**短连接**，所以需要新添配置项。

```text
upstream backend_server{
    server miaoshaApp1_ip weight=1;
    server miaoshaApp2_ip weight=1;
	server miaoshaApp3_ip weight=1;
    keepalive 30;
}
...
server{
    location / {
        proxy_pass http://backend_server;
        proxy_set_header Host $http_host:$proxy_port;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        
        proxy_http_version 1.1;
        proxy_set_header Connection "";
    }
}
```



### 优化效果

- **单机环境**下，发送1000*30个请求，TPS在**1400**左右，平均响应时间**460**毫秒。CPU的`us`高达8.0，`loadaverage`三个指标加起来接近于2了（CPU核数）。
- **分布式**扩展后，TPS在**1700**左右，平均响应时间**440**毫秒。但是CPU的`us`只有2.5左右，`loadaverage`1分钟在0.5左右，服务器的压力小了很多，有更多的并发提升空间。
- **后端长连接**后，虽然TPS也是1700多，但是响应时间降低到了**350**毫秒。

通过`netstat -an | grep miaoshaApp1_ip`可以查看Nginx服务器与后端服务器的连接情况，没开启长连接时，每次连接端口都在变，开启后，端口维持不变。

### 扩展——Nginx高性能原因

#### 1. epoll多路复用

- 传统java开发都会使用**BIO**模型，也就是Blocking IO，阻塞模型。当客户端与服务器建立连接之后，通过`Socket.write()`向服务器发送数据，只有当数据写完之后，才会发送。如果当Socket缓冲区满了，那就不得不阻塞等待。

- **Linux Select**模型。该模式下，会监听一定数量的客户端连接，一旦发现有变动，就会唤醒自己，然后轮询这些连接，看哪些连接发生了变化，执行IO操作。相比阻塞式的BIO，效率更高，但是也有个问题，如果10000个连接变动了1个，那么效率将会十分低下。此外，**Java NIO**，即New IO或者Non-Blocking IO就借鉴了Linux Select模型。

- **epoll模型**，在**Linux Select**模型之上，新增了**回调函数**，一旦某个连接发生变化，直接执行回调函数，不用遍历，效率更高。

#### 2. master-worker进程模型

通过`ps -ef|grep nginx`命令可以看到有两个Nginx进程，一个标注为`master`，一个标注为`worker`，而且`worker`进程是`master`进程的子进程。这种父子关系的好处就是，`master`进程可以管理`worker`进程。

![](https://github.com/PJB0911/SecKill-ii/blob/master/images/ngxin2.jpg)

- **Ngxin进程结构**

![](https://github.com/PJB0911/SecKill-ii/blob/master/images/nginx.png)

- **Master-worker高效原理**

客户端的请求，并不会被`master`进程处理，而是交给下面的`worker`进程来处理，多个`worker`进程通过“**抢占**”的方式，取得处理权。`master`进程只处理管理员的信号，当配置或命令发生变化的时候，`master`会马上感知到并通知`worker`，并new出新的worker进程，将老的worker进程的连接转移到新的worker上如果。这就是Nginx高效率的原因之一，也是可以平滑重启的原理。

此外，`worker`进程是单线程的，没有阻塞的情况下，效率很高。而epoll模型避免了阻塞。

综上，**epoll机制**+**master-worker机制**使得`worker`进程可以高效率地执行单线程I/O操作。

#### 3. Nginx高性能原因—协程机制

Nginx引入了一种比线程更小的概念，那就是“**协程**”。协程是比线程更小的模型，它依附于线程的内存模型，切换开销更小。如果协程程序遇到阻塞，nginx的协程机制会自动的把协程的权限剥夺，并在线程中掉另一个非阻塞的线程去执行，代码同步编写非常简单，而且由于协程依附于线程，所以是串行的，不需要加锁。

**参考资料：**
- [什么是协程](https://blog.csdn.net/zheng199172/article/details/88800275)


### 小结

这一节对单机系统进行了分布式扩展，使得吞吐量和响应时间都有了一定提升。虽然提升不大，但是单个服务器的压力明显降低。

1. 前端静态资源部署到了Nginx服务器。
2. Nginx作为反向代理服务器，把后端项目部署到了3台秒杀应用服务器。
3. 优化Nginx与后端服务器的连接。
4. 分析了Nginx高效的原因，包括epoll多路复用、master-worker机制、协程机制。

### 下一步优化方向

基础项目的用户登录凭证，是存放在`HttpSession`里面的，而`HttpSession`又存放在Tomcat服务器。一旦实现了分布式扩展，多台服务器无法共享同一个Session，所以就需要引入**分布式会话**。

------

## 分布式会话

### 基于Cookie传输SessionId

用户第一次登录成功后，服务器会产生一个 `cookie` ,设置`cookie`中的value为Tomcat生成的`SessionId`，向Redis服务器中储存键值对，key为`cookie`的value（即sessionId），值为**UserModel序列化的字符串**（`UserModel`类实现`Serializable`接口），并为该键设置过期时间（30分钟），从而实现分布式会话。

1. 引入两个`jar`包，分别是`spring-boot-starter-data-redis`和`spring-session-data-redis`，某些情况下，可能还需要引入`spring-security-web`。

2. `config`包下新建一个`RedisConfig`的类，暂时没有任何方法和属性，添加`@Component`和`@EnableRedisHttpSession(maxInactiveIntervalInSeconds=3600)`注解让Spring识别并自动配置过期时间。

3. 在`application.properties`里面添加Redis相关连接配置。

```properties
spring.redis.host=RedisServerIp
spring.redis.port=6379
spring.redis.database=10
spring.redis.password=
```


这样，之前的代码，就会自动将SessionId保存到Redis服务器上。

```java
this.httpServletRequest.getSession().setAttribute("IS_LOGIN",true);
this.httpServletRequest.getSession().setAttribute("LOGIN_USER",userModel);
```

**缺陷**：在移动端或是很多浏览器会禁用cookie，导致通过cookie不奏效，因此可以采用基于token的方式。

### 基于Token传输

Spring Boot在Redis存入的`SessionId`有多项，不够简洁。一般常用UUID生成类似`SessionId`的唯一登录凭证`token`，然后将生成的`token`作为 key，`UserModel`作为VALUE存入到Redis服务器。

在用户登录成功之后，将用户信息存储在Redis中，然后生成一个Token返回给客户端，这个Token为存储在Redis中的用户信息的key，这样，当客户端第二次

访问服务端时会携带Token，首先到Redis中获取查询该token对应的用户使用是否存在，避免数据库查询用户次数，从而减轻数据库的访问压力。
    
1. 登录时生成 Token

```java
String uuidToken=UUID.randomUUID().toString();
uuidToken=uuidToken.replace("-","");
//建立Token与用户登录态的联系
redisTemplate.opsForValue().set(uuidToken,userModel);
//设置超时时间
redisTemplate.expire(uuidToken,1, TimeUnit.HOURS);
return CommonReturnType.create(uuidToken);
```

2. 将生成的`token`返回给前端，前端在登录成功之后，将`token`**存放到`localStorage`里面**。

```javascript
if (data.status == "success") {
    alter("登录成功");
    var token = data.data;
    window.localStorage["token"] = token;
    window.location.href = "listitem.html";
}
```

3. 前端的下单操作，需要验证登录状态。

```javascript
var token = window.localStorage["token"];
if (token == null) {
    alter("没有登录，不能下单");
    window.location.href = "login.html";
    return false;
}
```

4. 在请求后端下单接口的时候，需要把这个`token`带上。

```javascript
$.ajax({
    type: "POST",
    url: "http://" + g_host + "/order/createorder?token=" + token,
    ···
});
```

基础项目是使用`SessionId`来获取登录状态的。

```java
Boolean isLogin=(Boolean)httpServletRequest.getSession().getAttribute("IS_LOGIN");
if(isLogin==null||!isLogin.booleanValue())
    throw new BizException(EmBizError.USER_NOT_LOGIN,"用户还未登录，不能下单");
UserModel userModel = (UserModel)httpServletRequest.getSession().getAttribute("LOGIN_USER");
```

分布式会话利用前端携带的`Token`，从Redis服务器里面获取这个`Token`对应的`UserModel`对象。

```java
String token=httpServletRequest.getParameterMap().get("token")[0];
if(StringUtils.isEmpty(token)){
    throw new BizException(EmBizError.USER_NOT_LOGIN,"用户还未登录，不能下单");
}
UserModel userModel= (UserModel) redisTemplate.opsForValue().get(token);
if(userModel==null){
    throw new BizException(EmBizError.USER_NOT_LOGIN,"登录过期，请重新登录");
}
```

### 小结

本节引入了分布式会话，有两种常见的实现方式：

1. 第一种是通过Spring提供的API，将Tomcat的`SessionId`和`UserModel`存到Redis服务器上。
2. 第二种是通过UUID生成登录`token`，将`token`和`UserModel`存到Redis服务器上。

### 下一步优化方向

目前服务器的性能瓶颈在于数据库的大量读取操作，所以主要利用各种技术减少对数据库层面的访问，接下来会引入**缓存**，优化查询。

------

## 查询优化之多级缓存

多级缓存有两层含义，一个是**缓存**，一个是**多级**。内存的速度是磁盘的成百上千倍，高并发下，从磁盘I/O十分影响性能。所谓缓存，就是将磁盘中的热点数据，暂时存到内存里面，以后查询直接从内存中读取，减少磁盘I/O，提高速度。所谓多级，就是在多个层次设置缓存，一个层次没有就去另一个层次查询。

从缓存实现性能优化角度，用户的请求首先经过**CDN优化**，然后经过**Nginx服务器缓存**，再进入**本地缓存**，最后经过**Redis缓存**。前面的层层缓存使得用户请求读写数据库的压力大大减小。


### 项目架构

![](https://github.com/PJB0911/SecKill-ii/blob/master/images/frame5.png)

### 优化商品查询接口—Redis缓存

redis主要有**单机版**、**sentinal哨兵模式**和**集群cluster模式**。**哨兵模式**，用一个sentinal节点管理redis，sentinal和主从redis有长连接，并发送心跳，sentinal可以决定master和slave，应用程序只需要询问sentinal就可以知道哪一台是master，然后连接相应的master即可。**cluster模式**，多台redis的情况下，根据cluster来决定哪些是主分片，哪些是从分片，所有分片都有连接，应用程序只要连接到任意一台机器，就可以获取所有数据。

在商品查询接口`ItemController.getItem`中，客户端请求查询`ItemId`，就调用`ItemService`去数据库查询一次。`ItemService`会查三张表，分别是商品信息表`item`表、商品库存`stock`表和秒杀活动表`promo`，十分影响性能。

本项目使用**单机版Redis缓存**，从数据库查询到的的数据存入Redis中，这样每次请求先进行**缓存查询**，如果命中则直接从Redis服务器中获取返回;如果没有，则从数据库查询并存到Redis服务器。

```java
@RequestMapping(value = "/get",method = {RequestMethod.GET})
@ResponseBody
public CommonReturnType getItem(@RequestParam(name = "id")Integer id){
    ItemModel itemModel=(ItemModel)redisTemplate.opsForValue().get("item_"+id);
    //如果不存在，就执行下游操作，到数据查询
    if(itemModel==null){
        itemModel=itemService.getItemById(id);
        //设置itemModel到redis服务器
        redisTemplate.opsForValue().set("item_"+id,itemModel);
        //设置失效时间
        redisTemplate.expire("item_"+id,10, TimeUnit.MINUTES);
    }
    ItemVO itemVO=convertVOFromModel(itemModel);
    return CommonReturnType.create(itemVO);
}
```

#### 缓存序列化格式问题

采用上述方式，存到Redis的VALUE是类似`/x05/x32`的二进制格式，需要自定义`RedisTemplate`的序列化格式。

在`config.RedisConfig`中自定义格式化方法。

```java
@Bean
public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory){
    RedisTemplate redisTemplate=new RedisTemplate();
    redisTemplate.setConnectionFactory(redisConnectionFactory);
    
    //首先解决key的序列化格式
    StringRedisSerializer stringRedisSerializer=new StringRedisSerializer();
    redisTemplate.setKeySerializer(stringRedisSerializer);
    
    //解决value的序列化格式
    Jackson2JsonRedisSerializer jackson2JsonRedisSerializer=new Jackson2JsonRedisSerializer(Object.class);
    redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
    
    return redisTemplate;
}
```

这样，`ItemModel`的内容就会以JSON的格式存储和显示。

#### 时间序列化格式问题

对于日期而言，序列化后是长度较长的毫秒数。用户希望的是`yyyy-MM-dd HH:mm:ss`的格式，需要进一步处理。新建`serializer`包，里面新建两个类。

```java
public class JodaDateTimeJSONSerializer extends JsonSerializer<DateTime> {
    @Override
    public void serialize(DateTime dateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(dateTime.toString("yyyy-MM-dd HH:mm:ss"));
    }
}
```

```java
public class JodaDateTimeDeserializer extends JsonDeserializer<DateTime> {
    @Override
    public DateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String dateString = jsonParser.readValueAs(String.class);
        DateTimeFormatter formatter=DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        return DateTime.parse(dateString,formatter);
    }
}
```

回到`RedisConfig`类：

```java
public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory){
    ···
    //解决日期的序列化格式
    ObjectMapper objectMapper=new ObjectMapper();
    SimpleModule simpleModule=new SimpleModule();
    simpleModule.addSerializer(DateTime.class,new JodaDateTimeJSONSerializer());
    simpleModule.addDeserializer(DateTime.class,new JodaDateTimeDeserializer());
    objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    objectMapper.registerModule(simpleModule);
    jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
    redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
    return redisTemplate;
}
```

这样就规范了时间类型的序列化格式。

### 优化商品查询接口—本地热点缓存

Redis缓存虽好，但是分布式系统中有网络I/O，没有本地缓存快。可以在Redis缓存之前再添加一层“**本地热点**”缓存。所谓**本地**，就是利用**本地JVM的内存**。所谓**热点**，由于JVM内存有限，仅存放**多次查询**的数据。

本地缓存，是`HashMap`结构，但是`HashMap`不支持并发读写，肯定是不行的。`j.u.c`包里面的`ConcurrentHashMap`虽然也能用，但是无法高效处理过期时限、没有淘汰机制等问题，所以这里使用了`Google`的`Guava Cache`方案。

`Guava Cache`除了线程安全外，还可以控制超时时间，提供LRU淘汰机制。

1. 引用`google.guava`包后，在`service`包下新建一个`CacheService`类。

```java
@Service
public class CacheServiceImpl implements CacheService {
    private Cache<String,Object> commonCache=null;
    @PostConstruct
    public void init(){
        commonCache= CacheBuilder.newBuilder()
                //初始容量
                .initialCapacity(10)
                //最大100个KEY，超过后会按照LRU策略移除
                .maximumSize(100)
                //设置写缓存后多少秒过期，还有根据访问过期即expireAfterAccess
                .expireAfterWrite(60, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public void setCommonCache(String key, Object value) {
       commonCache.put(key,value);
    }

    @Override
    public Object getFromCommonCache(String key) {
        return commonCache.getIfPresent(key);
    }
}
```

2. 在`ItemController`中，首先从本地缓存中获取，如果本地缓存没有，就去Redis缓存获取，如果Redis也没有，就去数据库查询并存放到Redis缓存。如果Redis里面有，将其获取后存到本地缓存里面。

```java
public CommonReturnType getItem(@RequestParam(name = "id")Integer id){
    ItemModel itemModel=null;
    //第一级：先去本地缓存
    itemModel=(ItemModel)cacheService.getFromCommonCache("item_"+id);
    if(itemModel==null){
        //第二级：从redis里面获取
        itemModel=(ItemModel)redisTemplate.opsForValue().get("item_"+id);
        //如果不存在，就执行下游操作，到数据查询
        if(itemModel==null){
            itemModel=itemService.getItemById(id);
            //设置itemModel到redis服务器
            redisTemplate.opsForValue().set("item_"+id,itemModel);
            //设置失效时间
            redisTemplate.expire("item_"+id,10, TimeUnit.MINUTES);
        }
        //填充本地缓冲
        cacheService.setCommonCache("item_"+id,itemModel);
    }
    ItemVO itemVO=convertVOFromModel(itemModel);
    return CommonReturnType.create(itemVO);
}
```

#### 本地缓存缺点

本地缓存虽快，但是也有缺点：

1. 更新麻烦，容易产生脏缓存，需要设置较短的有效期。
2. 受到JVM容量的限制。

### 缓存优化后的效果

- **分布式扩展**，发送**1000*20**个请求，TPS在**1700**左右，平均响应时间**350**毫秒左右。
- 引入**Redis缓存**后，TPS峰值达到了**2100**左右，平均响应时间**250**毫秒左右，Redis服务器压力不大，还可以继续加并发量。
- 引入**本地缓存**后，TPS峰值高达**3600**多，平均响应时间只有**50**毫秒左右。
- 再次压测**1000*40**个请求，发现TPS峰值高达**4100**多，平均响应时间在**145**毫秒左右。Redis服务器压力减小，请求被本地缓存拦截。

### Nginx Proxy Cache缓存

通过Redis缓存，避免了MySQL大量的重复查询，提高了部分效率；通过本地缓存，减少了与Redis服务器的网络I/O，提高了大量效率。

前端（客户端）请求Nginx服务器，Nginx有分发过程，需要去请求后面的3台应用服务器，存在一定网络I/O，所以需要引入Nginx缓存，直接把**热点数据存放到Nginx服务器上**。

Nginx Proxy Cache的原理是基于**文件系统**的，依靠内存缓存文件地址，它把后端返回的响应内容，作为**文件**存放在Nginx指定目录下。

**参考资料：**
- [nginx 反向代理之 proxy_cache](https://www.cnblogs.com/yyxianren/p/10832172.html)
- [nginx proxy_cache 缓存配置](https://blog.csdn.net/dengjiexian123/article/details/53386586)
- [nginx缓存](https://mp.weixin.qq.com/s?__biz=MzU5NzgwNDIyNQ==&mid=2247483758&idx=1&sn=fcd46827f22a6276e505a6e6dc5ace32&chksm=fe4c94c0c93b1dd64ea0471121febe9cabab49882f0f9ebf67049b2d4e051ccb6f604bf144c4&token=1811637810&lang=zh_CN#rd)

在`nginx.conf`里面配置`proxy cache`：

```text
upstream backend_server{
    server miaoshaApp1_ip weight=1;
    server miaoshaApp2_ip weight=1;
	server miaoshaApp3_ip weight=1;
}
#申明一个cache缓存节点 evels 表示以二级目录存放
    proxy_cache_path /usr/local/openresty/nginx/tmp_cache levels=1:2 keys_zone=tmp_cache:100m inactive=7d max_size=10g;
...
server{
    location / {
        ···
        #proxy_cache 目录
        proxy_cache tmp_cache;
        proxy_cache_key $uri;
        #只有后端返回以下状态码才缓存
        proxy_cache_valid 200 206 304 302 7d;
    }
}
```

当多次访问后端商品详情接口时，在`nginx/tmp_cache/dir1/dir2`下生成了一个**文件**。`cat`该文件，内容是后端服务器返回的`JSON`格式的数据。

#### Nginx Proxy Cache缓存效果

TPS峰值**2800**左右，平均响应时间**225**毫秒左右，**不升反降**。原因：虽然客户端可以直接从Nginx服务器拿到缓存的数据，但是这些数据是基于**文件系统**的，是存放在**磁盘**上的，有**磁盘I/O**，虽然减少了一定的网络I/O，但是磁盘I/O并没有内存快，得不偿失，所以在**本项目中不建议使用**。

### Nginx lua脚本

排除Nginx Proxy Cache文件缓存,使用Nginx lua脚本做**内存缓存**。

lua也是基于协程机制的。

- 依附于线程的内存模型，切换开销小。
- 遇到阻塞则释放执行权，代码同步。
- 无需加锁。

lua脚本可以挂载在Nginx处理请求的起始、worker进程启动、内容输出等阶段。

#### lua脚本实战

在OpenResty下新建一个lua文件夹，专门用来存放lua脚本。

1. 新建一个`init.lua`。

```lua
	ngx.log(ngx.ERR, "init lua success");
```

在`nginx.conf`里面添加一个`init_by_lua_file   ../lua/init.lua;`的字段，指定上述lua脚本的位置。当Nginx启动的时候，就会执行这个lua脚本，输出"init lua success"。

ps. 在Nginx启动的时候，挂载lua脚本并没有什么作用。一般在内容输出阶段，挂载lua脚本。

2. 新建一个`staticitem.lua`。
 
```lua
	ngx.say("hello static item lua");
```

在`nginx.conf`里面添加一个新的location：

```text
location /staticitem/get{
    default_type "text/html";
    content_by_lua_file ../lua/staticitem.lua;
}
```

访问`/staticitem/get`，在页面就会响应出`hello static item lua`的内容。

3. 新建一个`helloworld.lua`。
 
```lua
	ngx.exec("/item/get?id=1");
```

在`nginx.conf`里面添加一个`helloworld`location。

```text
location /helloworld {
    default_type "application/json";
    content_by_lua_file ../lua/helloworld.lua;
}
```

这样，当访问`/helloworld`的时候就会跳转到`item/get?id=1`这个URL上。

**参考资料：**
- [Nginx+lua+openresty系列 | 第六篇：Lua入门](https://mp.weixin.qq.com/s?__biz=MzU5NzgwNDIyNQ==&mid=2247483763&idx=1&sn=5aad2f0d3f73d7e3e474ccf568e0f5a9&chksm=fe4c94ddc93b1dcbf829ccc03af6606d2fb8f25c60682691ba19a593721004d7b500e675eab9&token=480040588&lang=zh_CN#rd)


### OpenResty—Shared dict缓存

OpenResty对Nginx进行了扩展，添加了很多功能，比如集成了lua开发环境、提供了对MySQL、Redis、Memcached的支持等。比原版Nginx使用起来更加方便。

Shared dict,共享内存字典,Key-Value**内存**结构，对所有`worker`进程可见，并且可以指定LRU淘汰规则。

1. 和配置`proxy cache`一样，需要指定一个名为`my_cache`，大小为128m的`lua_shared_dict`：

```text
upstream backend_server

···
lua_shared_dict my_cahce 128m;
```

2. 在lua文件夹下，新建一个`itemshareddict.lua`脚本，编写两个函数。

```lua
--取缓存
function get_from_cache(key)
    --类似于拿到缓存对象
    local cache_ngx = ngx.shared.my_cache
    --从缓存对象中，根据key获得值
    local value = cache_ngx.get(key)
    return value
end
--存缓存
function set_to_cache(key,value,exptime)
    if not exptime then 
        exptime = 0
    end
    local cache_ngx = ngx.shared.my_cache
    local succ,err,forcible = cache_ngx.set(key,value,exptime)
    return succ 
end
```

3. 编写“main"函数。

```lua
--得到请求的参数，类似Servlet的request.getParameters
local args = ngx.req.get_uri_args()
local id = args["id"]
--从缓存里面获取商品信息
local item_model = get_from_cache("item_"..id)
if item_model == nil then
    --如果取不到，就请求后端接口
    local resp = ngx.location.capture("/item/get?id="..id)
    --将后端返回的json响应，存到缓存里面
    item_model = resp.body
    set_to_cache("item_"..id,item_model,1*60)
end
ngx.say(item_model)
```

4. 新建一个`luaitem/get`的location。

```text
location /luaitem/get{
    default_type "application/json";
    content_by_lua_file ../lua/itemshareddict.lua;
}
```

**请求流程**：当访问`/luaitem/get?id=xxx`的时候，会先从**Nginx本地内存shared dict缓存**中获取数据，如果有，直接返回给前端；如果本地缓存中没有，就会请求`item/get?id=xxx`这个URL后端接口，后端先从后端服务器**本地guava缓存**中获取，如果guava缓存没有，就去**Redis缓存**获取，如果Redis里面有，将其获取后存到本地缓存里面；如果Redis也没有，就去**数据库**查询并存放到Redis缓存。


#### Shared dict缓存效果

压测`/luaitem/get`，峰值TPS在**4000**左右，平均响应时间**150ms**左右，比`proxy cache`要高出不少。

使用Ngxin的Shared dict，**把压力转移到了Nginx服务器**，**后面3台Tomcat服务器压力减小**。同时**减少了与后面3台Tomcat服务器、Redis服务器和数据库服务器的网络I/O**，当网络I/O成为瓶颈时，Shared dict不失为一种好方法。

**缺点**:Shared dict依然受制于**缓存容量**和**缓存更新**问题。


**参考资料：**
- [lua里使用共享内存-lua_shared_dict](http://www.04007.cn/article/130.html)
- [Nginx+lua+openresty系列 | 第七篇：openresty企业级应用1](https://mp.weixin.qq.com/s?__biz=MzU5NzgwNDIyNQ==&mid=2247483779&idx=1&sn=7a06b401f3c730027662815623a957a7&chksm=fe4c942dc93b1d3b2c8372e3273df9609a19eb3a0b9639c53702bf49d40bd5d6f17709d9a9fb&token=1578665362&lang=zh_CN#rd)
- [基于nginx+lua+redis实现的多级缓存架构存取的控制逻辑](https://www.cnblogs.com/z-3FENG/articles/9592072.html)


### OpenResty—直接读取Redis缓存


Nginx的本地Shared dict内存缓存受制于**缓存容量**和**缓存更新**问题，OpenResty提供了可直接从Redis服务器中读取缓存的支持，可以解决**缓存脏数据**和**缓存容量**的问题。

平常使用缓存都是在后端的服务器中进行判断，是否去查询redis中的数据。在企业中一般采用Redis集群实现读写分离，redis master负责写主服务，redis slave服务器只负责读从服务。Nginx通过lua脚本直接从redis slave服务器中获取数据，减少对后端的tomcat中的请求。

本项目只有1台Redis服务器,所以Nginx直接从redis服务器获取缓存。


![](https://github.com/PJB0911/SecKill-ii/blob/master/images/nginx-redis.png)


1. 在lua文件夹下，新建一个`itemredis.lua`脚本。

```lua
local args = ngx.req.get_uri_args()
local id = args["id"]
local redis = require "resty.redis"
local cache = redis:new()
local ok,err = cache:connect(redis slave ip,6379)
local item_model = cache:get("item_"..id)
if item_model == ngx.null or item_model == nil then
    --如果取不到，就请求后端接口
    local resp = ngx.location.capture("/item/get?id="..id)
    --将后端返回的json响应，因为是只读redis slave 负责读，不需要存到缓存
    item_model = resp.body
end

ngx.say(item_model)
```

2. 新建一个`itemredis/get`的location。

```text
location /itemredis/get{
    default_type "application/json";
    content_by_lua_file ../lua/itemredis.lua;
}
```

**请求流程**：当访问`/itemredis/get?id=xxx`的时候，会先从**Redis读从服务器缓存**获取数据，如果有，直接返回给前端；如果没有，就会回源请求`item/get?id=xxx`这个URL后端接口，后端先从后端服务器**本地guava缓存**中获取，如果guava缓存没有，这时不用去查询**Redis缓存**，因为nginx服务器已经查询过redis中没有缓存，直接去**数据库**查询并存放到**Redis主写服务**，然后主从同步到**Redis读从服务器**。


**参考资料：**
- [Nginx+Lua+Redis 实现高性能缓存数据读取](https://segmentfault.com/p/1210000011625271/read)
- [使用nginx+lua脚本读写redis缓存](https://my.oschina.net/u/1175305/blog/1799941)


### 缓存雪崩、缓存穿透、缓存更新

**问题1**：缓存穿透指的是对某个一定不存在的数据进行请求，该请求将会穿透缓存到达数据库。

**解决方案**：1.对这些不存在的数据缓存一个空数据，对这类请求进行过滤。2.布隆过滤器

**问题2**：缓存雪崩指的是由于数据没有被加载到缓存中，或者缓存数据在同一时间大面积失效（过期），又或者缓存服务器宕机，导致大量的请求都到达数据库。

**解决方案**：
- 为了防止缓存在同一时间大面积过期导致的缓存雪崩，可以通过观察用户行为，合理设置缓存过期时间来实现；
- 为了防止缓存服务器宕机出现的缓存雪崩，可以使用分布式缓存，分布式缓存中每一个节点只缓存部分的数据，当某个节点宕机时可以保证其它节点的缓存仍然可用。
- **缓存预热**，避免在系统刚启动不久由于还未将大量数据进行缓存而导致缓存雪崩。
例如：首先针对不同的缓存设置不同的过期时间，比如session缓存，在userKey这个前缀中，设置是30分钟过期，并且每次用户响应的话更新缓存时间。这样每次取session,都会延长30分钟，相对来说，就减少了缓存过期的几率

**问题3**：缓存一致性要求数据更新的同时缓存数据也能够实时更新。

**解决方案**：需要更新数据时，先更新数据库，然后把缓存里对应的数据删除。

要保证缓存一致性需要付出很大的代价，缓存数据最好是那些对一致性要求不高的数据，允许缓存数据存在一些脏数据。

具体见参考资料↓。

**参考资料：**
- [缓存雪崩、缓存穿透、缓存更新了解多少](https://segmentfault.com/a/1190000017882763)
- [高性能网站设计之缓存更新的套路](https://blog.csdn.net/tTU1EvLDeLFq5btqiK/article/details/78693323)
- [《吊打面试官》系列-缓存雪崩、击穿、穿透](https://mp.weixin.qq.com/s/knz-j-m8bTg5GnKc7oeZLg)


### 小结

本节通过redis/guava/nginx实现了多级缓存：

1. 首先使用**Redis**对商品详情信息缓存。
2. 使用本地缓存**guava**在Redis之前再做一层缓存。
3. 将缓存提前，提到离客户端更近的Nginx服务器上，减少网络I/O，开启了Nginx的**proxy cache**，由于proxy cache是基于文件系统的，有磁盘I/O，性能没有得到提升。
4. 使用**OpenResty Shared Dict+Nginx+Lua**将Nginx的缓存从磁盘提到服务器内存，减少了网络IO,提升了性能。
5. Shared dict存在**缓存容量**和**缓存更新**问题，使用nginx+lua脚本直接读取redis缓存，但相比于Shared dict增加了网络IO。

### 下一步优化方向

当前的架构，**前端资源每次都要进行请求**，为了减少前端静态资源请求，将**前端资源**进行缓存。下一章将讲解静态资源CDN，将页面静态化。

------

## 查询优化之页面静态化

### 项目架构

当前架构，静态资源是直接从Nginx服务器上获取，而现在会先去CDN服务器上获取，如果没有则回源到Nginx服务器上获取。

![](https://github.com/PJB0911/SecKill-ii/blob/master/images/frame6.png)

### CDN

CDN的全称是**Content Delivery Network**，即**内容分发网络**，CDN是构建在网络之上的内容分发网络，依靠部署在各地的边缘服务器，通过中心平台的负载均衡、内容分发、调度等功能模块，使用户**就近获取**所需内容，降低网络拥塞，提高用户访问响应速度和命中率。CDN的关键技术主要有内容存储和分发技术。

CDN一般用来存储（缓存）项目的静态资源。访问静态资源，会从离用户**最近**的CDN服务器上返回静态资源。如果该CDN服务器上没有静态资源，则会执行**回源**操作，从Nginx服务器上获取静态资源。

CDN 主要有以下优点：
- 更快地将数据分发给用户；
- 通过部署多台服务器，从而提高系统整体的带宽性能；
- 多台服务器可以看成是一种冗余机制，从而具有高可用性。

#### CDN使用

1. 购买一个阿里云CDN服务器，选择要加速的域名（比如`miaoshaserver.fun.com`），同时要填写**源站**IP，也就是Nginx服务器，便于回源操作。
2. 接下来要配置`miaoshaserver.fun.com`的DNS解析规则。一般的解析规则是`A记录类型`，也就是把一个域名直接解析成**IP地址**。这里使用`CNAME`进行解析，将一个域名解析到另外一个域名。而这个”另一个域名“是云服务器厂商提供的，它会把请求解析到**最近**的CDN服务器上。
3. 访问`miaoshaserver.fun.com/resources/getitem.html?id=1`即可以CDN的方式访问静态资源。

#### CDN优化效果

- 没有使用CDN优化：发送500*20个请求，TPS在**700**左右，平均响应时间**400ms**。
- 使用CDN优化：发送500*20个请求，TPS在**1300**左右，平均响应时间**150ms**。


### CDN深入

#### **1 cache controll响应头** 

在响应里面有一个`cache controll`响应头，这个响应头表示**客户端是否可以缓存响应**。有以下几种缓存策略：

| 策略        | 说明                                                   |
| ----------- | ------------------------------------------------------ |
| private     | 客户端可以缓存                                         |
| public      | 客户端和代理服务器都可以缓存                           |
| max-age=xxx | 缓存的内容将在xxx秒后失效                              |
| no-cache    | 也会缓存，但是使用缓存之前会询问服务器，该缓存是否可用 |
| no-store    | 不缓存任何响应内容                                     |

- **选择缓存策略**

如果不缓存，那就选择`no-store`。如果需要缓存，但是需要重新验证，则选择`no-cache`；如果不需要重新验证，则选择`private`或者`public`。然后设置`max-age`，最后添加`ETag Header`。

<img src="https://github.com/PJB0911/SecKill-ii/blob/master/images/choosehead.png" width=60% />

- **有效性验证**

**ETag**：第一次请求资源的时候，服务器会根据**资源内容**生成一个**唯一标示ETag**（一般按照MD5或者hash等生成），并返回给浏览器。浏览器下一次请求，会把**ETag**（If-None-Match）发送给服务器，与服务器的ETag进行对比。如果一致，就返回一个**304**响应，即**Not Modify**，**表示浏览器缓存的资源文件依然是可用的**，直接使用就行了，不用重新请求。

**If-None-Match**：客户端发送的匹配ETag标识符

**Last-modified**：服务端响应的时候返回的资源最后被修改的时间的标识符

**If-Modified-Since**：客户端发送的匹配资源最后修改时间的标识符，早于Last-modified说明无效，晚于Last-modified说明有效


- **请求资源流程**

<img src="https://github.com/PJB0911/SecKill-ii/blob/master/images/requestResrProcess.png" width=80% />

#### **2 浏览器三种刷新方式**

- **a标签/回车刷新**

查看`max-age`是否有效，有效直接从缓存中获取，无效进入缓存协商逻辑。

- **F5刷新**

取消`max-age`或者将其设置为0，直接进入缓存协商逻辑。

- **CTRL+F5强制刷新**

直接去掉`cache-control`和协商头，重新请求资源。

**协商机制**：比较Last-modified和ETag到服务端，若服务端判断没变化则返回304不返回数据，否则200返回数据。

#### **3 自定义缓存策略**

CDN服务器，既充当了浏览器的服务端，又充当了Nginx的客户端。所以它的缓存策略尤其重要。除了按照服务器的`max-age`，CDN服务器还可以自己设置过期时间。

**总的规则就是**：源站没有配置，遵从CDN控制台的配置；CDN控制台没有配置，遵从服务器提供商的默认配置。源站有配置，CDN控制台有配置，遵从CDN控制台的配置；CDN控制台没有配置，遵从源站配置。

<img src="https://github.com/PJB0911/SecKill-ii/blob/master/images/cacheconfig.png" width=80% />



**参考资料：**
- [关于cdn、页面静态化](https://blog.csdn.net/haozi_rou/article/details/105375462?utm_medium=distribute.pc_relevant.none-task-blog-baidulandingword-1&spm=1001.2101.3001.4242)


### 全页面静态化

当前的架构，用户通过CDN请求到了静态资源，静态页面会在加载的时候，发送Ajax异步请求（如下）服务器获取动态数据，接收到后端的响应的json数据，再用**DOM渲染**。即每一个用户请求，都有一个请求**后端接口**并**渲染**的过程。考虑直接在**服务器端把页面渲染**，返回一个纯`html`文件给客户端。

```javascript
//页面加载完成
jQuery(document).ready(function(){
    //请求后端接口
    $.ajax({
        ···
        //接受到响应
        success: function(){
        //根据响应填充标签
        reloadDom();
    }
    })
})
```

**全页面静态化**：
- 在**服务端**完成 html，css，甚至 js 的加载工作，渲染成纯 **html** 文件后直接以静态资源的方式部署到 CDN 上。
- **不改动任何服务端代码**，通过全页面静态化的手段，把已经渲染好的，布满数据的 html 页面，直接以 **html** 文件的形式部署到 CDN 上；
- 全页面静态化的技术和网页爬虫非常的像，网页爬虫不知道引用的 js 文件或 css 文件，只知道最后爬出来的文件是一个可以在浏览器中渲染的文件；


#### phantomJS实现全页面静态化

phantomjs 是一个无头浏览器，可以借助其模拟 webkit js 的执行，就像一个爬虫，会把页面中的JS执行完毕后，返回一个渲染完成的`html`文件。
1. 新建 phantomjs 脚本 `getitem.js`。
```javascript
//引入包
var page = require("webpage").create();
var fs = require("fs");
page.open("http://miaoshaserver/resources/getitem.html?id=1",function(status){
	console.log("status = " + status);
    setTimeout(function(){
        fs.write("getitem.html",page.content,"w");
        phantom.exit();
    },1000);
})
```

打开指定目录下生成的`getitem.hmtl`，发现里面的标签都正确填充了，但是**还是发送了一次Ajax请求**。原因就在于，页面渲染完毕，Ajax请求的代码块仍然存在，仍然会发送。

2. 修改源文件 `getitem.html` （不是1中生成的）中的内容，在页面中添加一个隐藏域：`<input type="hidden" id="isInit" value="0"/>`。

3. 新增`hasInit`、`setHasInit`、`initView`三个函数。目的在于，由无头浏览器执行完，并且生成出来的静态 html 页面，由用户再去打开的时候 ，不会再发 ajax 请求给后端。

```javascript
function hasInit() {
    var isInit = $("#isInit").val();
    return isInit;
}

function setHasInit() {
    $("#isInit").val("1");
}

function initView() {
    var isInit = hasInit();
    //如果渲染过，直接返回
    if (isInit == "1") 
	return;
    //否则发送ajax请求
    $.ajax({
        ···
        success: function (data) {
            if (data.status == "success") {
                global_itemVO = data.data;
                //渲染页面
                reloadDom();
                setInterval(reloadDom, 1000);
                //将isInit的值设为1
                setHasInit();
            } 
        ···
}
```

4. 修改phantomJS代码。

```javascript
var page = require("webpage").create();
var fs = require("fs");
page.open("http://miaoshaserver/resources/getitem.html?id=1",function(status){
    //每隔1秒就尝试一次，防止JS没加载完
    var isInit = "0";
    setInterval(function(){
        if(isInit != "1"){
            //手动执行一次initView
            page.evaluate(function(){
                initView();
            })
            //手动设置hasInit
            isInit = page.evaluate(function(){
                return hasInit();
            })
        } else {
            fs.write("getitem.html",page.content,"w");
            phantom.exit();
        }
    },1000);
})
```
5. 当页面第一次加载时，`hasInit=0`，那么会发送Ajax请求并渲染页面，渲染完毕后，将`hasInit`置为1。当页面第二次加载时，由于`hasInit=1`，不会再次发送Ajax请求页面。将生成的完全静态化的 html 页面放到 CDN 上去，就可以在 CDN 服务器完全命中。



**参考资料：**
- [基于 phantomjs 的全页面静态化技术原理](https://blog.csdn.net/weixin_33669968/article/details/105947547)
- [基于 phantomjs 全页面静态化技术的优化](https://blog.csdn.net/weixin_33669968/article/details/105947546)



### 小结

1. 首先使用**CDN技术**将静态资源部署到CDN服务器上，提高了静态资源的响应速度。
2. 使用**全页面静态化技术**，使得用户在请求页面的时候，不会每次都去请求后端接口，然后进行页面渲染，而是直接得到一个已经渲染好的HTML页面，提高了响应速度。

### 下一步优化方向

接下来对**交易下单接口**进行性能优化，包括缓存库存、异步扣减库存等。

------

## 查询优化效果总结

### Tomcat优化

| 优化线程数和连接（200*50） | TPS  | 平均响应时间/ms |
| -------------------------- | ---- | --------------- |
| 优化前                     | 150  | 1000            |
| 优化后                     | 250  | 400             |

### 分布式扩展优化

| 分布式扩展（1000*30） | TPS  | 平均响应时间/ms | us   | load_average  |
| --------------------- | ---- | --------------- | ---- | ------------- |
| 单机环境              | 1400 | 460             | 8.0  | 三项相加≈核数 |
| 分布式扩展            | 1700 | 440             | 2.5  | 1分钟0.5      |
| 长连接优化            | 1700 | 350             | -    | -             |

### 缓存优化

| 缓存优化（1000*20）       | TPS      | 平均响应时间/ms |
| ------------------------- | -------- | --------------- |
| 未引入缓存                | 1700     | 350             |
| 引入Redis缓存             | 2100     | 250             |
| 引入本地缓存              | **3600** | **50**          |
| 引入Nginx Proxy Cache     | 2800     | 225             |
| 引入OpenResty Shared Dict | **4000** | 150             |

### CDN优化

| CDN优化（500*20） | TPS  | 平均响应时间/ms |
| ----------------- | ---- | --------------- |
| 未使用CDN         | 700  | 400             |
| 使用CDN           | 1300 | 150             |


------


## 交易优化之缓存库存

### 交易接口瓶颈

发送20*200个请求压测`createOrder`接口，TPS只有**280**左右，平均响应时间**460**毫秒。应用服务器`us`占用高达**75%**，1分钟的`load average`高达**2.21**，可见压力很大。相反，数据库服务器的压力则要小很多。

原因在于，在`OrderService.createOrder`方法里面，首先要去数据库**查询商品信息**，而在查询商品信息的过程中，又要去**查询秒杀活动信息**，最后还要查询**用户信息**。

```java
//查询商品信息的过程中，也会查询秒杀活动信息。
ItemModel itemModel=itemService.getItemById(itemId);
if(itemModel==null)
    throw new BizException(EmBizError.PARAMETER_VALIDATION_ERROR,"商品信息不存在");
//查询用户信息
UserModel userModel=userService.getUserById(userId);
```

最后还要对`stock`**库存表进行-1`update`操作**，对`order_info`**订单信息表进行添加`insert`操作**，对`item`**商品信息表进行销量+1`update`操作**。仅仅一个下单，就有**6次**数据库I/O操作，此外，减库存操作还存在**行锁阻塞**，所以下单接口并发性能很低。

**优化核心思想：减少对数据库的访问。**

### 交易验证优化

查询用户信息，是为了**用户风控策略**。判断用户信息是否存在是最基本的策略，在企业级中，还可以判断用户状态是否异常，是否异地登录等等。用户风控的信息，实际上可以缓存化，放到Redis里面。

查询商品信息、活动信息，是为了**活动校验策略**。商品信息、活动信息，也可以存入缓存中。活动信息，由于具有**时效性**，需要具备紧急下线的能力，可以编写一个接口，清除活动信息的redis缓存。

#### 用户校验缓存优化

思路很简单，就是先从Redis里面获取用户信息，没有再去数据库里查，并存到Redis里面。`UserService`新开一个`getUserByIdInCache`的方法。

```java
public UserModel getUserByIdInCache(Integer id) {
    UserModel userModel= (UserModel) redisTemplate.opsForValue().get("user_validate_"+id);
    if(userModel==null){
        userModel=this.getUserById(id);
        redisTemplate.opsForValue().set("user_validate_"+id,userModel);
        redisTemplate.expire("user_validate_"+id,10, TimeUnit.MINUTES);
    }
    return userModel;
}
```

#### 活动校验缓存优化

跟用户校验类似，`ItemService`新开一个`getItemByIdInCache`方法。

```java
public ItemModel getItemByIdInCache(Integer id) {
    ItemModel itemModel=(ItemModel)redisTemplate.opsForValue().get("item_validate_"+id);
    if(itemModel==null){
        itemModel=this.getItemById(id);
        redisTemplate.opsForValue().set("item_validate_"+id,itemModel);
         redisTemplate.expire("item_validate_"+id,10, TimeUnit.MINUTES);
    }
    return itemModel;
}
```

#### 缓存优化后的效果

压测1000*20个请求，TPS在**450**左右，平均响应**1500毫秒**。

优化之后，TPS在**1200**左右，平均响应**600毫秒**。

### 库存扣减优化

#### 索引优化

之前扣减库存的操作，会执行`update stock set stock = stock -#{amount} where item_id = #{itemId} and stock >= #{amount}`这条SQL语句。如果`where`条件的`item_id`字段没有**索引**，那么会**锁表**，性能很低。所以先查看`item_id`字段是否有索引，没有的话，使用`alter table stock add UNIQUE INDEX item_id_index(item_id)`，为`item_id`字段添加一个**唯一索引**，这样在修改的时候，只会**锁行**。

#### 库存扣减缓存优化

之前的下单，是**直接操作数据库**，一旦秒杀活动开始，大量的流量涌入扣减库存接口，**数据库压力很大**。所以采用在**缓存**中**扣减库存**，需要解决**两个**问题，第一个是活动开始前，将数据库的库存信息，同步到缓存中。第二个是下单之后，要将缓存中的库存信息同步到数据库中。这就需要用到**异步消息队列**——**RocketMQ**。

**1 RocketMQ 安装配置**

RocketMQ是阿里巴巴在RabbitMQ基础上改进的一个消息中间件，具体见[浅入浅出-RocketMQ](https://mp.weixin.qq.com/s/y-4TVwbc7AFGEA7q-_OkYw)。

默认的RocketMQ**配置**需要较大内存（`Xms4g Xmx4g Xmn2g`），会导致Java**内存不足**的问题。需要修改`bin/runbroker.sh`，`bin/runserver.sh`，`bin/tool.sh`、`conf/borker.conf `等文件配置。

**2 同步数据库库存到缓存**

- `PromoService`新建一个`publishPromo`的方法，把数据库的缓存存到Redis里面去。

```java
public void publishPromo(Integer promoId) {
    //通过活动id获取活动
    PromoDO promoDO=promoDOMapper.selectByPrimaryKey(promoId);
    if(promoDO.getItemId()==null || promoDO.getItemId().intValue()==0)
        return;
    ItemModel itemModel=itemService.getItemById(promoDO.getItemId());
    //库存同步到Redis
    redisTemplate.opsForValue().set("promo_item_stock_"+itemModel.getId(),itemModel.getStock());
}
```

当把**库存存到Redis的时候**，**商品可能被下单**，这样数据库的库存和Redis的库存就**不一致**了。解决方法就是活动**未开始**的时候，商品是**下架状态**，不能被下单。

- 修改`ItemService`中的`decreaseStock`方法，在Redis里面扣减库存。

```java
public boolean decreaseStock(Integer itemId, Integer amount) {
    // 老方法，直接在数据库减
    // int affectedRow=itemStockDOMapper.decreaseStock(itemId,amount);
    long result=redisTemplate.opsForValue().
                increment("promo_item_stock_"+itemId,amount.intValue()*-1);
    return (affectedRow >= 0);
}
```

**3 同步缓存库存到数据库（异步扣减库存）**

- 引入RocketMQ相应`jar`包，在Spring Boot配置文件中添加MQ配置。

```properties
mq.nameserver.addr=IP:9876
mq.topicname=stock
```

- 新建一个`mq.MQProducer`类，编写`init`方法，初始化生产者。

```java
public class MqProducer {
    private DefaultMQProducer producer;
    //即是IP:9867
    @Value("${mq.nameserver.addr}")
    private String nameAddr;
    //即是stock
    @Value("${mq.topicname}")
    private String topicName;
   
    @PostConstruct
    public void init() throws MQClientException {
        //Producer初始化，Group对于生产者没有意义，但是消费者有意义
        producer=new DefaultMQProducer("producer_group");
        producer.setNamesrvAddr(nameAddr);
        producer.start();
    }
}
```

- 编写`asyncReduceStock`方法，实现异步扣减库存。

```java
public boolean asyncReduceStock(Integer itemId, Integer amount)  {
    Map<String,Object> bodyMap=new HashMap<>();
    bodyMap.put("itemId",itemId);
    bodyMap.put("amount",amount);
    //创建消息
    Message message=new Message(topicName,"increase",
                JSON.toJSON(bodyMap).toString().getBytes(Charset.forName("UTF-8")));
    //发送消息
    try {
        producer.send(message);
    } catch (MQClientException e) {
      ···
        return false;
    }
    return true;
}
```

- 新建一个`mq.MqConsumer`类，与`MqProducer`类类似，也有一个`init`方法，实现**异步扣减库存**的逻辑。

```java
public class MqConsumer {
    private DefaultMQPushConsumer consumer;
    @Value("${mq.nameserver.addr}")
    private String nameAddr;
    @Value("${mq.topicname}")
    private String topicName;
    @Autowired
    private ItemStockDOMapper itemStockDOMapper;
    @PostConstruct
    public void init() throws MQClientException {
        consumer=new DefaultMQPushConsumer("stock_consumer_group");
        //监听名为topicName的话题
        consumer.setNamesrvAddr(nameAddr);
        //监听topicName话题下的所有消息
        consumer.subscribe(topicName,"*");
        //这个匿名类会监听消息队列中的消息
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                //实现缓存数据真正到数据库扣减的逻辑
                //从消息队列中获取消息
                Message message=list.get(0);
                //反序列化消息
                String jsonString=new String(message.getBody());
                Map<String,Object> map=JSON.parseObject(jsonString, Map.class);
                Integer itemId= (Integer) map.get("itemId");
                Integer amount= (Integer) map.get("amount");
               //去数据库扣减库存
                int updateRow=itemStockDOMapper.decreaseStock(itemId, amount);
                //返回消息消费成功
                if(updateRow==1)
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
				//消费失败
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        });
        consumer.start();
    }
}
```

- 修改`ItemService.decreaseStock`方法：

```java
public boolean decreaseStock(Integer itemId, Integer amount) {
    long affectedRow=redisTemplate.opsForValue().
                increment("promo_item_stock_"+itemId,amount.intValue()*-1);
    //>0，表示Redis扣减成功
    if(affectedRow>=0){
        //发送消息到消息队列，准备异步扣减
        boolean mqResult = mqProducer.asyncReduceStock(itemId,amount);
        if (!mqResult){
            //消息发送失败，需要回滚Redis
            redisTemplate.opsForValue().increment("promo_item_stock_"+itemId,amount.intValue());
            return false;
        }
        return true;
    } else {
        //Redis扣减失败，回滚
        redisTemplate.opsForValue().increment("promo_item_stock_"+itemId,amount.intValue());
        return false;
    }
}
```

**4 异步扣减库存存在的问题**

1. 如果异步消息发送失败，只能回滚Redis。
2. 消费端从数据库扣减操作执行失败，如何处理（[RocketMQ消费消息失败的处理办法](https://blog.csdn.net/LO_YUN/article/details/104301740)）。
3. 下单失败无法正确回补库存（比如用户取消订单）。


### 小结

这一章

1. 首先对**交易验证**进行了优化，把对用户、商品、活动的查询从数据库转移到了缓存中，优化效果明显。
2. 优化了减库存的逻辑，一是添加了索引，从锁表变成了锁行；二是将减库存的操作也移到了缓存中，先从缓存中扣，再从数据库扣。这就涉及到了**异步减库存**，所以引入**消息中间件**。

### 下一步优化方向

正如**异步扣减库存存在的问题**所述，这么处理还有许多漏洞，下一章将会引入**事务型消息**。

## 交易优化之事务型消息

### 异步消息发送时机问题

目前扣减库存的事务`ItemService.decreaseStock`是封装在`OrderService.createOrder`事务里面的。在扣减Redis库存、发送异步消息之后，还有订单入库、增加销量的操作。如果这些操作失败，那么`createOrder`**事务会回滚**，`decreaseStock`**事务也回滚**，但是Redis的**扣减操作却不能回滚**，会导致数据不一致。

#### 解决方法

解决的方法就是在订单入库、增加销量成功之后，再发送异步消息。

1. `ItemService.decreaseStock`只**负责扣减Redis库存**，**不发送异步消息**。

```java
public boolean decreaseStock(Integer itemId, Integer amount) {
    long result=redisTemplate.opsForValue().increment("promo_item_stock_"+itemId,amount.intValue()*-1);
    //>0，表示Redis扣减成功
    if(result>=0){
        //抽离了发送异步消息的逻辑
        return true;
    } else {
        //Redis扣减失败，回滚
        increaseStock(itemId, amount)
        return false;
    }
}

public boolean increaseStock(Integer itemId, Integer amount) {
    redisTemplate.opsForValue().increment("promo_item_stock_"+itemId,amount.intValue());
    return true;
}
```

2. 将发送异步消息的逻辑抽取出来：

```java
//ItemService
public boolean asyncDecreaseStock(Integer itemId, Integer amount) {
    return mqProducer.asyncReduceStock(itemId, amount);
}
```

3. 再在`OrderService.createOrder`里面调用：

```java
···
//订单入库
orderDOMapper.insertSelective(orderDO);
//销量增加
itemService.increaseSales(itemId,amount);
//执行完最后一步才发送异步消息
boolean mqResult=itemService.asyncDecreaseStock(itemId,amount);
    if(!mqResult){
        //回滚redis库存
        itemService.increaseStock(itemId,amount);
        throw new BizException(EmBizError.MQ_SEND_FAIL);
    }
```

这样，就算订单入库失败、销量增加失败、消息发送失败，都能保证缓存和数据库的一致性。

### 事务提交问题

但是这么做，依然有问题。Spring的`@Transactional`标签，会在**事务方法返回后才提交**，如果提交的过程中，发生了异常，则数据库回滚，但是Redis库存已扣，还是无法保证一致性，要在**事务提交成功后**，**再发送异步消息**。

#### 解决方法

Spring提供了`TransactionSynchronizationManager.registerSynchronization`方法，这个方法的传入一个`TransactionSynchronizationAdapter`的匿名类，通过`afterCommit`方法，在**事务提交成功后**，执行**发送消息操作**。

```java
TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
    @Override
    public void afterCommit() {
    boolean mqResult=itemService.asyncDecreaseStock(itemId,amount);
    if(!mqResult){
        itemService.increaseStock(itemId,amount);
        throw new BizException(EmBizError.MQ_SEND_FAIL);
    }
}
```

### 事务型消息

上面的做法，依然不能保证万无一失。假设现在**事务提交成功了**，等着执行`afterCommit`方法，这个时候**突然宕机了**，那么**订单已然入库**，**销量已然增加**，但是**去数据库扣减库存的这条消息**却“**丢失**”了。这里就需要引入RocketMQ的事务型消息。

所谓事务型消息，存在二阶段提交的概念，事务型消息会被发送到消息队列里面，这条消息处于`prepared`状态，`broker`会接受到这条消息，**但是不会把这条消息给消费者消费**。

处于`prepared`状态的消息，会执行`TransactionListener`的`executeLocalTransaction`方法，通过它返回Commit或rollback来决定发送的消息执行还是回滚，**改变事务型消息的状态**，**让消费端消费或是不消费**。

1. 在`mq.MqProducer`类里面新注入一个`TransactionMQProducer`类，与`DefaultMQProducer`类似，也需要设置服务器地址、命名空间等。

2. 新建一个`transactionAsyncReduceStock`的方法，该方法使用**事务型消息**进行异步扣减库存。

```java
// 事务型消息同步库存扣减消息
public boolean transactionAsyncReduceStock(Integer userId, Integer itemId, Integer promoId, Integer amount, String stockLogId) {
    Map<String, Object> bodyMap = new HashMap<>();
    bodyMap.put("itemId", itemId);
    bodyMap.put("amount", amount);
    //用于执行orderService.createOrder的传参
    Map<String, Object> argsMap = new HashMap<>();
    argsMap.put("itemId", itemId);
    argsMap.put("amount", amount);
    argsMap.put("userId", userId);
    argsMap.put("promoId", promoId);

    Message message = new Message(topicName, "increase",
                JSON.toJSON(bodyMap).toString().getBytes(Charset.forName("UTF-8")));
    try {
        //注意，发送的是sendMessageInTransaction
        transactionMQProducer.sendMessageInTransaction(message, argsMap);
    } catch (MQClientException e) {
        e.printStackTrace();
        return false;
    }
    return true;
}
```

3. 发送一个事务型消息到`broke`，而处于`prepared`状态的事务型消息，会执行`TransactionListener`的`executeLocalTransaction`方法：

```java
transactionMQProducer.setTransactionListener(new TransactionListener() {
    @Override
    public LocalTransactionState executeLocalTransaction(Message message, Object args) {
    //在事务型消息中去进行下单
    Integer itemId = (Integer) ((Map) args).get("itemId");
    Integer promoId = (Integer) ((Map) args).get("promoId");
    Integer userId = (Integer) ((Map) args).get("userId");
    Integer amount = (Integer) ((Map) args).get("amount");
    try {
        //调用下单接口
        orderService.createOrder(userId, itemId, promoId, amount);
    } catch (BizException e) {
        e.printStackTrace();
        //发生异常就回滚消息
        return LocalTransactionState.ROLLBACK_MESSAGE;
    }
    return LocalTransactionState.COMMIT_MESSAGE;
}
```

4. 在**事务型消息中去执行下单操作**，下单失败，则消息回滚，**不会去数据库扣减库存**。下单成功，则消息被消费，**扣减数据库库存**。

### 更新下单流程

之前的下单流程是：在`OrderController`里面调用了`OrderService.createOrder`方法，然后在该方法最后发送了异步消息，会导致异步消息丢失的问题。所以引入了**事务型消息**。

现在的下单流程是：在`OrderController`里面直接调用`MqProducer.transactionAsyncReduceStock`方法，发送一个事务型消息，然后在**事务型消息中调用`OrderService.createOrder`方法**，进行下单。

### 小结

1. 首先解决了**发送异步消息时机**的问题，之前是在`ItemService.decreaseStock`，当在Redis里面扣减成功后，发送异步消息。这样会导致数据库回滚，但Redis无法回滚的问题。所以把发送异步消息提到所有下单操作完成之后。
2. 其次，由于Spring的`@Transactional`标签是在方法返回后，才提交事务，如果返回阶段出了问题，那么数据库回滚了，但是缓存的库存却扣了。所以使用了`afterCommit`方法。
3. 最后，如果在执行`afterCommit`的时候，发生了异常，那么消息就发不出去，又会导致数据一致性问题。所以通过使用**事务型消息**，把**下单操作包装在异步扣减消息里面**，让下单操作跟扣减消息**同生共死**。

### 下一步优化方向

上述流程还有一个漏洞，就是当执行`orderService.createOrder`后，突然**又宕机了**，根本没有返回，这个时候事务型消息就会进入`UNKNOWN`状态，我们需要处理这个状态。

在匿名类`TransactionListener`里面，还需要覆写`checkLocalTransaction`方法。`UNKNOWN`状态表示`broker`会定期执行`checkLocalTransaction`方法来询问结果这个。因此在生成订单时出问题，也可以通过`UNKNOWN`来让`RocketMQ`自己查询状态。这就需要引入**库存流水**。

## 库存流水

1. 数据库新建一张`stock_log`的表，用来记录库存流水，添加一个`ItemService.initStockLog`方法。

```java
public String initStockLog(Integer itemId, Integer amount) {
    StockLogDO stockLogDO = new StockLogDO();
    stockLogDO.setItemId(itemId);
    stockLogDO.setAmount(amount);
    stockLogDO.setStockLogId(UUID.randomUUID().toString().replace("-", ""));
    //1表示初始状态，2表示下单成功，3表示扣减库存成功，4表示下单回滚
    stockLogDO.setStatus(1);
    stockLogDOMapper.insertSelective(stockLogDO);
    return stockLogDO.getStockLogId();
}
```

2. 用户请求后端`OrderController.createOrder`接口，先初始化库存流水的状态，再调用事务型消息去下单。

```java
//OrderController
//先检验用户登录信息
String token = httpServletRequest.getParameterMap().get("token")[0];
if (StringUtils.isEmpty(token)) {
    throw new BizException(EmBizError.USER_NOT_LOGIN, "用户还未登录，不能下单");
}
UserModel userModel = (UserModel) redisTemplate.opsForValue().get(token);
if (userModel == null) {
    throw new BizException(EmBizError.USER_NOT_LOGIN, "登录过期，请重新登录");
}

//初始化库存流水
String stockLogId = itemService.initStockLog(itemId, amount);

//发送事务型消息，完成下单逻辑
if (!mqProducer.transactionAsyncReduceStock(userModel.getId(), itemId, promoId, amount, stockLogId)) {
    throw new BizException(EmBizError.UNKNOWN_ERROR, "下单失败");
}
```

3. 事务型消息会调用`OrderService.createOrder`方法，执行Redis扣减库存、订单入库、销量增加的操作，当这些操作都完成后，就说明下单完成了，**等着异步更新数据库**。那么需要修改订单流水的状态。

```java
//OrderService.createOrder
//订单入库
orderDOMapper.insertSelective(orderDO);
//增加销量
itemService.increaseSales(itemId, amount);
StockLogDO stockLogDO = stockLogDOMapper.selectByPrimaryKey(stockLogId);
if (stockLogDO == null)
    throw new BizException(EmBizError.UNKNOWN_ERROR);
//设置库存流水状态为下单成功
stockLogDO.setStatus(2);
stockLogDOMapper.updateByPrimaryKeySelective(stockLogDO);
```

### 下单操作的处理

异步更新数据库，需要事务型消息从`prepare`状态变成`commit`状态。假如此时`orderService.createOrder`**本身发生了异常**，那么就回滚事务型消息，并且返回`LocalTransactionState.ROLLBACK_MESSAGE`，这个下单操作就会被取消。

如果**本身没有发生异常**，那么就返回`LocalTransactionState.COMMIT_MESSAGE`，此时事务型消息会从`prepare`状态变为`commit`状态，接着被消费端消费，异步扣减库存。

```java
//MqProducer.TransactionListener().executeLocalTransaction()
try {
    orderService.createOrder(userId, itemId, promoId, amount, stockLogId);
} catch (BizException e) {
    e.printStackTrace();
    //如果发生异常，createOrder已经回滚，此时要回滚事务型消息。
    //设置stockLog为回滚状态
    StockLogDO stockLogDO = stockLogDOMapper.selectByPrimaryKey(stockLogId);
    stockLogDO.setStatus(4);
    stockLogDOMapper.updateByPrimaryKeySelective(stockLogDO);
    return LocalTransactionState.ROLLBACK_MESSAGE;
}
return LocalTransactionState.COMMIT_MESSAGE;
```

### UNKNOWN状态处理

如上节结尾所述，如果在执行`createOrder`的时候，突然宕机了，此时事务型消息的状态是`UNKNOWN`，需要在`TransactionListener.checkLocalTransaction`方法中进行处理。

```java
public LocalTransactionState checkLocalTransaction(MessageExt message) {
    //根据是否扣减库存成功，来判断要返回COMMIT，ROLLBACK还是UNKNOWN
    String jsonString = new String(message.getBody());
    Map<String, Object> map = JSON.parseObject(jsonString, Map.class);
    String stockLogId = (String) map.get("stockLogId");
    StockLogDO stockLogDO = stockLogDOMapper.selectByPrimaryKey(stockLogId);
    if (stockLogDO == null)
        return LocalTransactionState.UNKNOW;
    //订单操作已经完成，等着异步扣减库存，那么就提交事务型消息
    if (stockLogDO.getStatus() == 2) {
        return LocalTransactionState.COMMIT_MESSAGE;
    //订单操作还未完成，需要执行下单操作，那么就维持为prepare状态
    } else if (stockLogDO.getStatus() == 1) {
        return LocalTransactionState.UNKNOW;
    }
    // //如果下单和减库存都完成，或者其他情况，都回滚
    return LocalTransactionState.ROLLBACK_MESSAGE;
}
```

### 库存售罄处理

现在是用户请求一次`OrderController.createOrder`就初始化一次流水，但是如果10000个用户抢10个商品，就会初始化10000次库存流水，这显然是不行的。

1. 解决的方法是在`ItemService.decreaseStock`中，如果库存没有了，就打上“**售罄标志**”。

```java
public boolean decreaseStock(Integer itemId, Integer amount) {
    long affectedRow = redisTemplate.opsForValue().
                increment("promo_item_stock_" + itemId, amount.intValue() * -1);
    if (affectedRow > 0) {
        return true;
    } else if (affectedRow == 0) {
        //打上售罄标识
        redisTemplate.opsForValue().set("promo_item_stock_invalid_" + itemId, "true");
        return true;
    } else {
        increaseStock(itemId, amount);
        return false;
    }
}
```

2. 在`OrderController.createOrder`初始化流水之前，先判断一下是否售罄，售罄了就直接抛出异常。

```java
//是否售罄
if (redisTemplate.hasKey("promo_item_stock_invalid_"+itemId))
    throw new BizException(EmBizError.STOCK_NOT_ENOUGH);
String stockLogId = itemService.initStockLog(itemId, amount);
```


### 防止同一用户多次秒杀下单

**考虑2种情形**
- 用户**已经秒杀成功**，再对一个商品发出多次秒杀请求，如果服务器不进行判断，就会让同一用户，重复下单。
- **未秒杀成功**的用户同时对一个商品发出多次秒杀请求，对于多次秒杀请求，服务器会判断用户的两次秒杀请求为合法请求，完成下单和减库存的数据库操作。
上述两种情况都是不合理的。一个用户只能秒杀一个商品，如果执行成功，则订单表中会出现多条商品id和用户id相同的记录，这就引发了超卖问题

1. 解决的方法是在`OrderService.createOrder`中，如果用户下单成功，就打上“**秒杀成功标志**”。

```java
//OrderService.createOrder
//订单入库
orderDOMapper.insertSelective(orderDO);
//增加销量
itemService.increaseSales(itemId, amount);
StockLogDO stockLogDO = stockLogDOMapper.selectByPrimaryKey(stockLogId);
if (stockLogDO == null)
    throw new BizException(EmBizError.UNKNOWN_ERROR);
//设置库存流水状态为成功
stockLogDO.setStatus(2);
stockLogDOMapper.updateByPrimaryKeySelective(stockLogDO);

//用户秒杀成功商品标记
redisTemplate.opsForValue().set("seckill_success_itemid"+itemId+"userid"+userId,true);
redisTemplate.expire("seckill_success_itemid"+itemId+"userid"+userId,6, TimeUnit.HOURS);

```

2. 在`OrderController.createOrder`初始化流水之前，先判断一下用户是否已经秒杀过，秒杀过了就直接抛出异常。

```java
//判断是否已经秒杀到商品，防止一人多次秒杀成功,
if(redisTemplate.hasKey("bought_itemid"+itemId+"userid"+userModel.getId()))
     throw new BizException(EmBizError.BOUGHT_ERROR);

```

3. 数据库订单表 `order_info`中的user_id和item_id字段创建一个**联合唯一索引**，则在插入两条`user_id`和`item_id`相同的记录时，将会操作失败，从而事务回滚，秒杀不成功，在数据库层面解决了同一个用户对一个商品发起多次请求引发的超卖问题。


### 消息重复问题

**考虑一种情形**
RocketMQ在消息量大，网络状况不好的的情况下，当第一次发送消息时，Broker接收到消息没有正确返回发送成功的状态 发送方可能就会自动重试发第二次消息就，就造成**消息重复**。

- 解决的方法是在`MqConsumer`中，如果数据库`item_stock`表已经扣减成功，就打上“**扣减库存成功标志**”。

```java
public class MqConsumer {
    private DefaultMQPushConsumer consumer;
    @Value("${mq.nameserver.addr}")
    private String nameAddr;
    @Value("${mq.topicname}")
    private String topicName;
    @Autowired
    private ItemStockDOMapper itemStockDOMapper;
    @PostConstruct
    public void init() throws MQClientException {
        consumer=new DefaultMQPushConsumer("stock_consumer_group");
        //监听名为topicName的话题
        consumer.setNamesrvAddr(nameAddr);
        //监听topicName话题下的所有消息
        consumer.subscribe(topicName,"*");
        //这个匿名类会监听消息队列中的消息
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                //实现缓存数据真正到数据库扣减的逻辑
                //从消息队列中获取消息
                Message message=list.get(0);
                //反序列化消息
                String jsonString=new String(message.getBody());
                Map<String,Object> map=JSON.parseObject(jsonString, Map.class);
                Integer itemId= (Integer) map.get("itemId");
                Integer amount= (Integer) map.get("amount");
		Integer stockLogId= (Integer) map.get("stockLogId");
		//防止重复消费，先校验扣除流水缓存，如果存在，直接返回，保持幂等性
		if(redisTemplate.hasKey("decreaseStock_success_stockLogId"+stockLogId+"itemId"+itemId))
		    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                //校验只有下单状态，才能减库存
                StockLogDO stockLogDO = stockLogDOMapper.selectByPrimaryKey(stockLogId);
                if (stockLogDO!=null && stockLogDO.getStatus() == 2) {
                    //去数据库扣减库存
                    int updateRow = itemStockDOMapper.decreaseStock(itemId, amount);
                    //扣减成功，缓存扣除流水成功消息，返回消息消费成功
                    if (updateRow == 1) {
                        redisTemplate.opsForValue().set("decreaseStock_success_stockLogId" + stockLogId + "itemId" + itemId, true);
                        redisTemplate.expire("decreaseStock_success_stockLogId" + stockLogId + "itemId" + itemId, 10, TimeUnit.MINUTES);
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    }
                }         
                   //消费失败
                  return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        });
        consumer.start();
    }
}
```


### 小结

这一节通过引入库存流水，来记录库存的状态，以便在**事务型消息处于不同状态时进行处理**。

事务型消息提交后，会在`broker`里面处于`prepare`状态，也即是`UNKNOWN`状态，等待被消费端消费，或者是回滚。`prepare`状态下，会执行`OrderService.createOrder`方法。

此时有两种情况：

1. `createOrder`执行完**没有宕机**，要么**执行成功**，要么**抛出异常**。**执行成功**，那么就说明下单成功了，订单入库了，Redis里的库存扣了，销量增加了，**等待着异步扣减库存**，所以将事务型消息的状态，从`UNKNOWN`变为`COMMIT`，这样消费端就会消费这条消息，异步扣减库存；**抛出异常**，那么订单入库、Redis库存、销量增加，就会被数据库回滚，此时去异步扣减的消息，就应该“丢弃”，所以发回`ROLLBACK`，进行回滚。
2. `createOrder`执行完**宕机**了，那么这条消息会是`UNKNOWN`状态，这个时候就需要在`checkLocalTransaction`进行处理。如果`createOrder`执行完毕，此时`stockLog.status==2`，就说明下单成功，需要去异步扣减库存，所以返回`COMMIT`。如果`status==1`，说明下单还未完成，还需要继续执行下单操作，所以返回`UNKNOWN`。如果`status==3`，说明下单失败，需要回滚，不需要异步扣减库存，所以返回`ROLLBACK`。

#### 可以改进的地方

目前只是扣减库存异步化，实际上销量逻辑和交易逻辑都可以异步化，这里就不作赘述。

### 下一步优化方向

目前下单接口会被脚本不停地刷，影响正常用户的体验。此外，验证逻辑和下单逻辑强关联，耦合度比较高。最后，验证逻辑也比较复杂。接下来会引入流量削峰技术。

## 流量削峰

秒杀秒杀，就是在活动开始的一瞬间，有大量流量涌入，优化不当，会导致服务器停滞，甚至宕机。所以引入流量削峰技术十分有必要。




### 业务解耦—秒杀令牌

**秒杀令牌原理**：
- 秒杀接口需要依靠令牌才能进入。秒杀令牌由秒杀活动模块负责生成。
- 秒杀活动模块对秒杀令牌生成全权处理，逻辑收口。
- 秒杀下单前用户需要先获得令牌才能秒杀。

**秒杀令牌**和**隐藏秒杀接口地址**的作用相似，目的都是为了秒杀开始之前，秒杀地址对客户端不可见,减少作弊带来的流量。[秒杀地址的隐藏](https://github.com/Grootzz/seckill#%E5%AE%89%E5%85%A8%E4%BC%98%E5%8C%96)，同时同一个用户**只能有1个秒杀令牌**，防止多次点击秒杀按钮。

之前**验证逻辑**和**下单逻辑**都耦合在`OrderService.createOrder`里面，现在利用秒杀令牌，使校验逻辑和下单逻辑分离。


1. `PromoService`新开一个`generateSecondKillToken`，将活动、商品、用户信息校验逻辑封装在里面。

```java
public String generateSecondKillToken(Integer promoId,Integer itemId,Integer userId) {
    //判断库存是否售罄，若Key存在，则直接返回下单失败
    if(redisTemplate.hasKey("promo_item_stock_invalid_"+itemId))
        return null;
    PromoDO promoDO=promoDOMapper.selectByPrimaryKey(promoId);
    PromoModel promoModel=convertFromDataObj(promoDO);
    if(promoModel==null) return null;
    if(promoModel.getStartDate().isAfterNow()) {
        promoModel.setStatus(1);
    }else if(promoModel.getEndDate().isBeforeNow()){
        promoModel.setStatus(3);
    }else{
        promoModel.setStatus(2);
    }
    //判断活动是否正在进行
    if(promoModel.getStatus()!=2) return null;
    //判断item信息是否存在
    ItemModel itemModel=itemService.getItemByIdInCache(itemId);
    if(itemModel==null) return null;
    //判断用户是否存在
    UserModel userModel=userService.getUserByIdInCache(userId);
    if(userModel==null) 
    	return null;
    // 判断是否已经秒杀到商品，防止一人多次秒杀成功
    if(redisTemplate.hasKey("seckill_success_itemid"+itemId+"userid"+userId))
    	return null;

    //如果已有秒杀令牌，表示进行过秒杀操作（即是否点击过秒杀按钮）
    String token= (String) redisTemplate.opsForValue().get("promo_token_" + promoId + "_userid_" + userId + "_itemid_" + itemId);
    if(token!=null)
    	return null;

    //生成Token，并且存入redis内，5分钟时限
    String token = UUID.randomUUID().toString().replace("-", "");
    redisTemplate.opsForValue().set("promo_token_" + promoId + "_userid_" + userId + "_itemid_" + itemId, token);		
    redisTemplate.expire("promo_token_" + promoId + "_userid_" + userId + "_itemid_" + itemId, 5, TimeUnit.MINUTES);
    }
    return token;
}
```

这样，`OrderService.createOrder`和`OrderController.createOrder`的校验逻辑就可以删除，校验逻辑和下单逻辑分开。

2. `OrderController`新开一个`generateToken`接口，以便前端请求，返回令牌。

```java
@RequestMapping(value = "/generatetoken",···)
@ResponseBody
public CommonReturnType generateToken(···) throws BizException {
    //用户登录状态校验
    ···
    //获取秒杀访问令牌
    String promoToken = promoService.generateSecondKillToken(promoId, itemId, userModel.getId());
    if (promoToken == null)
        throw new BizException(EmBizError.PARAMETER_VALIDATION_ERROR, "生成令牌失败");
    return CommonReturnType.create(promoToken);
}

```

3. 前端在点击“**下单**”后（不论点击多次都只会有1个秒杀令牌），首先会请求`generateToken`接口，返回秒杀令牌。然后将秒杀令牌`promoToken`作为参数，再去请求后端`createOrder`接口：

```java
@RequestMapping(value = "/createorder",···)
@ResponseBody
public CommonReturnType createOrder(··· @RequestParam(name = "promoToken", required = false) String promoToken) throws BizException {
    ···
    //校验秒杀令牌是否正确
    if (promoId != null) {
        String inRedisPromoToken = (String) redisTemplate.opsForValue().
                    get("promo_token_" + promoId + "_userid_" + userModel.getId() + "_itemid_" + itemId);
    if (inRedisPromoToken == null) 
        throw new BizException(EmBizError.PARAMETER_VALIDATION_ERROR, "令牌校验失败");
    if (!StringUtils.equals(promoToken, inRedisPromoToken)) 
        throw new BizException(EmBizError.PARAMETER_VALIDATION_ERROR, "令牌校验失败");
}

```

这样就彻底完成了校验逻辑和下单逻辑的分离。现在的问题是，假设有1E个用户请求下单，那么就会生成1E的令牌，这是十分消耗性能的，所以接下来会引入**秒杀大闸进行限流**。

### 限流—令牌大闸

**秒杀大闸的原理**：
- 依靠秒杀令牌的授权原理定制化发牌逻辑（控制令牌发放）。
- 根据秒杀商品**初始库存**颁发对应数量的令牌，控制大闸流量。
- 用户风控策略前置到秒杀令牌发放中。
- 库存售罄判断前置到秒杀令牌发放中。

大闸的意思就是**令牌的数量是有限的**，当令牌用完时，就不再发放令牌了，那么下单将无法进行。

1. 通过`PromoService.publishPromo`将库存发布到了Redis上，现在将令牌总量也发布到Redis上，设定令牌总量是库存的5倍。

```java
public void publishPromo(Integer promoId) {
    ···
    //库存同步到Redis
    redisTemplate.opsForValue().set("promo_item_stock_" + itemModel.getId(), itemModel.getStock());
    //大闸限制数量设置到redis内
    redisTemplate.opsForValue().set("promo_door_count_" + promoId, itemModel.getStock().intValue() * 5);
}

```

2. ，在`PromoService.generateSecondKillToken`方法中，在生成令牌之前，首先将Redis里的令牌总量减1，然后再判断是否剩余，如果<0，直接返回null。

```java
//如果已有秒杀令牌，表示进行过秒杀操作（即是否点击过秒杀按钮）
...

//获取大闸数量
long result = redisTemplate.opsForValue().increment("promo_door_count_" + promoId, -1);
if (result < 0) 
    return null;
//令牌生成       

```

这样，当令牌总量为0时，就不再发放令牌，也就无法下单了。

#### 令牌大闸限流缺点

当商品种类少、库存少的时候，令牌大闸效果还不错。但是一旦参与活动的商品库存太大，比如10000个，那么一秒钟也有上十万的流量涌入，限制能力是很弱的。所以需要**队列泄洪**。

### 限流—队列泄洪

队列泄洪，就是让多余的请求**排队等待**，依靠排队来限制**并发流量**，依靠**排队**和下游**拥塞窗口**程度调整队列释放流量大小。**排队**有时候比**多线程**并发效率更高，多线程毕竟有锁的竞争、上下文的切换，很消耗性能。而排队是无锁的，单线程的，某些情况下效率更高。

比如Redis就是**单线程模型**，多个用户同时执行`set`操作，只能一一等待。

比如MySQL的`insert`和`update`语句，会维护一个行锁。阿里SQL就不会，而是让多个SQL语句排队，然后依次执行。

像支付宝就使用了队列泄洪，双11的时候，支付宝作为网络科技公司，可以承受很高的TPS，但是下游的各个银行，无法承受这么高的TPS。支付宝维护了一个“拥塞窗口”，慢慢地向下游银行发送流量，保护下游。

在`OrderController`里面，之前拿到秒杀令牌后，就要开始执行下单的业务了。现在把**下单业务**封装到一个**固定大小的线程池中**，一次**只处理固定大小的请求**。

1. 在`OrderController`里面引入`j.u.c.ExcutorService`，创建一个`init`方法，初始化线程池。

```java
@PostConstruct
public void init() {
    //20个线程的线程池
    executorService = Executors.newFixedThreadPool(20);
}

```

2. 在拿到秒杀令牌后，使用线程池来处理下单请求。

```java
Future<Object> future = executorService.submit(new Callable<Object>() {
    @Override
    public Object call() throws Exception {
        String stockLogId = itemService.initStockLog(itemId, amount);
        if (!mqProducer.transactionAsyncReduceStock(userModel.getId(), itemId, promoId, amount, stockLogId)) {
            throw new BizException(EmBizError.UNKNOWN_ERROR, "下单失败");
        }
        return null;
    }
});
try {
future.get();
} catch (InterruptedException e) {
    ···
}

```

这样，就算瞬间涌入再多流量，得到处理的也就20个，其它全部等待。


### 小结

1. 使用秒杀令牌，实现了校验业务和下单业务的分离，也限制流量，同时为秒杀大闸做了铺垫。
2. 使用秒杀大闸，实现了限流的第一步，限制了流量的总量。
3. 使用队列泄洪，实现了限流的第二步，同一时间只有部分请求得到处理。

### 下一步优化方向

接下来将会引入防刷限流技术，比如验证码技术等。

## 防刷限流

### 验证码技术

之前的流程是，用户点击下单后，会直接拿到令牌然后执行下单流程。现在，用户点击下单后，前端会弹出一个“验证码”，用户输入之后，才能请求下单接口。

**验证码的作用**：
- 防止利用机器人等手段防止非目标用户参与秒杀；
- 减少单位时间内的请求数量。

对于一个秒杀商品，在开始秒杀后肯定会有许多用户参与秒杀，那么在开始秒杀的时候，用户请求数量是巨大，从而对服务器产生较大的压力，而通过验证码的方式就可以有效地将集中式的请求分散，从而达到削减请求峰值的目的。

1. 生成验证码的[CodeUti](https://github.com/PJB0911/SecKill-ii/blob/master/src/main/java/com/gan/util/CodeUtil.java)

2. `OrderController`新开一个`generateVerifyCode`接口。

```java
@RequestMapping(value = "/generateverifycode",···)
@ResponseBody
public void generateVerifyCode(HttpServletResponse response) throws BizException, IOException {
    ···验证
    //验证用户信息
    Map<String, Object> map = CodeUtil.generateCodeAndPic();
    //生成的验证码存到Redis里，并设置过期时间
    redisTemplate.opsForValue().set("verify_code_" + userModel.getId(), map.get("code"));
    redisTemplate.expire("verify_code_" + userModel.getId(), 10, TimeUnit.MINUTES);
    //生成的图片，响应到前端页面
    ImageIO.write((RenderedImage) map.get("codePic"), "jpeg", response.getOutputStream());
}

```

3. 获取秒杀令牌的`generateToken`接口，需要添加验证码校验逻辑。

```java
public CommonReturnType generateToken(··· @RequestParam(name = "verifyCode") String verifyCode) throws BizException {
    //验证用户登录信息
    ···
    //验证验证码的有效性
    String redisVerifyCode = (String) redisTemplate.opsForValue().get("verify_code_" + userModel.getId());
    if (StringUtils.isEmpty(redisVerifyCode))
        throw new BizException(EmBizError.PARAMETER_VALIDATION_ERROR, "请求非法");
    if (!redisVerifyCode.equalsIgnoreCase(verifyCode))
        throw new BizException(EmBizError.PARAMETER_VALIDATION_ERROR, "请求非法");
    //获取秒杀访问令牌
    ···
}

```

这样，就实现了在下单之前，添加一个验证码，限制部分流量的功能。

### 限流方案—限并发

限制并发量意思就是同一时间**只有一定数量的线程去处理请求**，实现也比较简单，维护一个**全局计数器**，当请求进入接口时，计数器-1，并且判断计数器是否>0，大于0则处理请求，小于0则拒绝等待。

但是一般衡量并发性，是用TPS或者QPS，而该方案由于限制了线程数，自然不能用TPS或者QPS衡量。

### 限流方案—令牌桶/漏桶

#### 令牌桶

客户端请求接口，必须先从令牌桶中获取令牌，令牌是由一个“定时器”定期填充的。在一个时间内，令牌的数量是有限的。令牌桶的大小为100，那么TPS就为100。

![](https://github.com/PJB0911/SecKill-ii/tree/master/images/tokenBucket.png)

#### 漏桶

客户端请求接口，会向漏桶里面“加水”。漏桶每秒漏出一定数量的“水”，也就是处理请求。只有当漏洞不满时，才能请求。

![](https://github.com/PJB0911/SecKill-ii/tree/master/images/leekBucket.png)

#### 区别

漏桶无法应对**突发流量**，比如突然来10个请求，只能处理一个。但是令牌桶，可以一次性处理10个。所以，令牌桶用得比较多。

### 限流力度和限流范围

**限流力度**分为**接口维**度和**总维度**，**接口维度**就是限制某个接口的流量，而**总维度**是限制所有接口的流量。

**限流范围**分为**集群限流**和**单机限流**，**集群限流**就是限制整个集群的流量，需要用Redis或者其它中间件技术来做统一计数器，往往会产生性能瓶颈。**单机限流**在负载均衡的前提下效果更好。

### RateLimiter限流实现

`google.guava.RateLimiter`就是令牌桶算法的一个实现类，用于**单机限流**，`OrderController`引入这个类，在`init`方法里面，初始令牌数量为200。

- [Guava RateLimiter限流](https://www.jianshu.com/p/5d4fe4b2a726)
- [限流与RateLimiter](https://www.cnblogs.com/xrq730/p/11025029.html)



```java
@PostConstruct
    public void init() {
    //20个线程的线程池
    executorService = Executors.newFixedThreadPool(20);
    //200个令牌，即200TPS
    orderCreateRateLimiter = RateLimiter.create(200);
}

```

请求`createOrder`接口之前，会调用`RateLimiter.tryAcquire`方法，看当前令牌是否足够，不够直接抛出异常。

```java
if (!orderCreateRateLimiter.tryAcquire())
     throw new BizException(EmBizError.RATELIMIT);

```

**RateLimiter的限制**

RateLimiter是**单机限流**的，也就是说它无法跨JVM使用，对于分布式系统，RateLimiter无法保证限流效果（如果单节点QPS限制在400/s，分布式系统总请求就是**节点数x400/s**），因此需要采用**Redis+LUA脚本**。
- [基于Redis+LUA脚本的令牌桶算法限流策略实现](https://blog.csdn.net/limingcai168/article/details/85168491)。
- [Redis+LUA脚本实现限制ip和用户名访问次数](https://segmentfault.com/a/1190000016552464)
- [限制用户访问次数：自定义注解+Spring拦截器+Redis统计次数](https://blog.csdn.net/bskfnvjtlyzmv867/article/details/89923044)

### 防刷技术

排队、限流、令牌只能控制总流量，无法控制黄牛流量。

#### 传统防刷技术

1. 限制一个会话（Session、Token）一定时间内请求接口的次数。多会话接入绕开无效，比如黄牛可以开启多个会话。
2. 限制一个IP一定时间内请求接口的次数。容易误伤，某个局域网的正常用户共享一个IP进行访问。而且IP可以被伪造。

#### 黄牛为什么难防

1. 模拟硬件设备，比如手机。一个看似正常的用户，可能是用模拟器模拟出来的。
2. 设备牧场，一屋子手机刷接口。
3. 人工作弊，这个最难防，请真人刷接口。

#### 防黄牛方案

1. **设备指纹方式**：采集终端设备各项数据，启动应用时生成一个唯一设备指纹。根据对应设备的指纹参数，估计是可疑设备的概率。
2. **凭证系统**：根据设备指纹下发凭证，在关键业务链路上带上凭证并由凭证服务器验证。凭证服务器根据设备指纹参数和风控系统判定凭证的可疑程度。若凭证分数低于设定值，则开启验证。

### 小结

1. 通过引入验证码技术，在发送秒杀令牌之前，再做一层限流。
2. 介绍常见限流的方案，使用`RateLimiter`实现了令牌桶限流。
3. 总结常见的防刷技术以及它们的缺点，及黄牛为什么难防，应该怎样防。

------


## 交易优化效果总结

### 交易验证优化

| 交易验证优化（1000*20） | TPS      | 平均响应时间/ms | us   | load average |
| ----------------------- | -------- | --------------- | ---- | ------------ |
| 优化前                  | 450      | 1500            | 7.5  | 1分钟2.21    |
| 优化后                  | **1200** | **600**         | -    | -            |




## 总结—下单流程

1. 运营发布秒杀活动，系统初始化，把商品库存数量stock缓存到Redis上面来。
2. 用户点击下单，需要输入**验证码**，后端判断**验证码**是否正确；如果验证码正确，检验**库存**、**用户**及**活动信息**，以及该**用户是否已经秒杀过**(秒杀令牌是否已有，订单缓存记录是否已有)，避免一个账户秒杀多个商品。如果检验成功**还有库存**并且**令牌还有余量**，返回秒杀令牌Token，将秒杀令牌promoToken作为参数去请求后端秒杀接口；如果没有库存、令牌没有余量，直接返回前端**库存不足**信息，即后面的大量请求无需给系统带来压力。。
3. 后端收到秒杀请求，校验**秒杀令牌**，验证失败，直接返回前端；校验正确，**秒杀请求封装后事务性消息入队**,同时给前端返回一个code (0)，即代表返回排队中。
4. Producer事务消息中调用下单方法，执行**秒杀事务**（redis预减库存，下订单，写入秒杀订单，销量增加）。秒杀方法要么**执行成功**，要么**抛出异常**。执行成功，消费端就可以异步扣减库存，抛出异常，ROLLBACK进行回滚。秒杀方法执行完**宕机**，根据库存流水状态判断**秒杀成功**、**秒杀未完成**，**秒杀失败**，秒杀成功，同上；秒杀未完成，继续等待；秒杀失败，回滚。
5. 此时，前端根据商品id和用户轮询请求接口SecKillResult,查看是否生成了商品订单，如果请求返回-1代表秒杀失败，返回0代表排队中，返回>0代表商品id说明秒杀成功。


## 问题汇总

- [超卖问题](https://github.com/Grootzz/seckill#%E8%B6%85%E5%8D%96%E9%97%AE%E9%A2%98)
- [缓存问题](#缓存雪崩缓存穿透缓存更新)
- [重复消费、顺序消费](https://mp.weixin.qq.com/s/OKon95MRUqDc9IwtEqPSjQ)
- [消息队列比较](https://www.jianshu.com/p/fec054f3e496)
- [其他问题](https://github.com/qiurunze123/miaosha)


 
