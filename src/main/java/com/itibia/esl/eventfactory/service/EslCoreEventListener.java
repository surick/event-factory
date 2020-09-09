package com.itibia.esl.eventfactory.service;

import com.alibaba.fastjson.JSONObject;
import com.itibia.esl.eventfactory.EventFactoryApplication;
import com.itibia.esl.eventfactory.model.PbxHost;
import com.itibia.esl.eventfactory.repository.PbxHostRepository;
import org.freeswitch.esl.client.inbound.Client;
import org.freeswitch.esl.client.internal.IModEslApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author Jin
 * @since 2020/9/8
 */
@Component
public class EslCoreEventListener implements ApplicationListener<ContextRefreshedEvent> {
    protected final static Logger logger = LoggerFactory.getLogger(EslCoreEventListener.class);

    @Autowired
    private PbxHostRepository pbxHostRes;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        List<PbxHost> pbxHostList = pbxHostRes.findAll();

        try {
            final Client inboudClient = new Client();

            if (pbxHostList.size() > 0) {
                inboudClient.connect(
                        new InetSocketAddress(
                                pbxHostList.get(0).getIpaddr(), pbxHostList.get(0).getPort()), pbxHostList.get(0).getPassword(), 10);

                inboudClient.addEventListener((context, eslEvent) -> {

                    String json = JSONObject.toJSONString(event);
                    rabbitTemplate.convertAndSend(EventFactoryApplication.EVENT, json);
                });
                inboudClient.setEventSubscriptions(IModEslApi.EventFormat.PLAIN, "all");

                // 不区分大小写，对事件进行过滤，只关注需要的事件
                for (FSEventType fSEventType : FSEventType.values()) {
                    inboudClient.addEventFilter("Event-Name", fSEventType.name());
                }
            }
        } catch (Throwable t) {
            logger.error("start error { }", t);
        }
    }

    enum FSEventType {
        // 跟呼叫相关的通道事件
        CHANNEL_CREATE, // 通道创建事件
        CHANNEL_ANSWER, // 通道应答事件
        CHANNEL_BRIDGE, // 通道桥接事件
        CHANNEL_HANGUP, // 通道挂断事件
        RECORD_START,    // 录音开始时间
        RECORD_STOP,    // 录音结束时间
        CHANNEL_HANGUP_COMPLETE,
        CHANNEL_HOLD,
        CHANNEL_UNHOLD,
        CC_AGENT_UPDATE,
        CUSTOM,
        DTMF,
        BACKGROUND_JOB,
        // CHANNEL_DESTROY,

        // CHANNEL_ORIGINATE,
        // CHANNEL_OUTGOING,

        HEARTBEAT, // 心跳包
        SESSION_HEARTBEAT, // 计费心跳包心跳包
    }
}
