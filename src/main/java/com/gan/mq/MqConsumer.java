package com.gan.mq;

import com.alibaba.fastjson.JSON;
import com.gan.dao.ItemStockDOMapper;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Component
public class MqConsumer {
    private DefaultMQPushConsumer consumer;
    //即是IP:9867
    @Value("${mq.nameserver.addr}")
    private String nameAddr;
    //即是stock
    @Value("${mq.topicname}")
    private String topicName;
    @Autowired
    private ItemStockDOMapper itemStockDOMapper;

    /**
     * 初始化 Consumer
     * @throws MQClientException
     */
    @PostConstruct
    public void init() throws MQClientException {
        consumer = new DefaultMQPushConsumer("stock_consumer_group");
        consumer.setNamesrvAddr(nameAddr); //监听该地址下的话题
        consumer.subscribe(topicName, "*");  //监听stock话题下的所有消息
        //这个匿名类会监听消息队列中的消息
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                //实现缓存数据真正到数据库扣减的逻辑
                //从消息队列中获取消息
                Message message = list.get(0);
                //反序列化消息
                String jsonString = new String(message.getBody());
                Map<String, Object> map = JSON.parseObject(jsonString, Map.class);
                Integer itemId = (Integer) map.get("itemId");
                Integer amount = (Integer) map.get("amount");
                //去数据库扣减库存
                int updateRow=itemStockDOMapper.decreaseStock(itemId, amount);
                //返回消息消费成功
                if(updateRow==1)
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        });
        consumer.start();
    }
}
