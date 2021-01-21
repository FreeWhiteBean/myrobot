package simbot.demo.listener;

import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.MsgSender;
import org.springframework.stereotype.Component;

/**
 * 测试监听器。
 * 在未配置simbot依赖扫描的情况下，应当使用Springboot的依赖管理注解，例如@Component。
 * @author ForteScarlet
 */
//@Component
public class MyListener {

    /**
     * 监听私聊消息。
     * @param privateMsg
     */
    @OnPrivate
    public void privateListen(PrivateMsg privateMsg, MsgSender sender){
        // 复读私聊的信息。
        sender.SENDER.sendPrivateMsg(privateMsg, privateMsg.getMsgContent());
    }


}
