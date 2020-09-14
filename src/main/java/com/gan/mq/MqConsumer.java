package com.gan.mq;

import com.alibaba.fastjson.JSON;
import com.gan.dao.ItemStockDOMapper;
import com.gan.dao.StockLogDOMapper;
import com.gan.dataobject.StockLogDO;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private StockLogDOMapper stockLogDOMapper;

    /**
     * 初始化 Consumer
     *
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
                String stockLogId = (String) map.get("stockLogId");
                //防止重复消费，先校验扣除流水缓存，如果存在，直接返回，保持幂等性
                if (redisTemplate.hasKey("decreaseStock_success_stockLogId" + stockLogId + "itemId" + itemId))
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
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        });
        consumer.start();
    }
}
