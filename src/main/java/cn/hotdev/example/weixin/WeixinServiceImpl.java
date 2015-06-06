package cn.hotdev.example.weixin;

import cn.hotdev.example.services.ConfigService;
import lombok.NoArgsConstructor;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.*;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@NoArgsConstructor
@Service
public class WeixinServiceImpl implements WeixinService {

    private WxMpInMemoryConfigStorage wxMpConfigStorage;
    private WxMpService wxMpService;
    private WxMpMessageRouter wxMpMessageRouter;

    private ConfigService configService;

    @Autowired
    public WeixinServiceImpl(ConfigService configService) {

        this.configService = configService;

        String app_id = configService.getConfig("app_id");
        if (app_id == null || app_id.isEmpty()) {
            throw new RuntimeException("no config for weixin found !!!");
        }

        wxMpConfigStorage = new WxMpInMemoryConfigStorage();
        wxMpConfigStorage.setAppId(configService.getConfig("app_id"));
        wxMpConfigStorage.setSecret(configService.getConfig("app_secret"));
        wxMpConfigStorage.setToken(configService.getConfig("app_token"));
        wxMpConfigStorage.setAesKey(configService.getConfig("app_aeskey"));

        wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(wxMpConfigStorage);

        WxMpMessageHandler handler = new WxMpMessageHandler() {
            @Override
            public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
                WxMpXmlOutTextMessage outTextMessage
                        = WxMpXmlOutMessage.TEXT().content("测试加密消息").fromUser(wxMessage.getToUserName())
                        .toUser(wxMessage.getFromUserName()).build();
                return outTextMessage;
            }
        };

        wxMpMessageRouter = new WxMpMessageRouter(wxMpService);
        wxMpMessageRouter
                .rule()
                .async(false)
                .content("哈哈") // 拦截内容为“哈哈”的消息
                .handler(handler)
                .end();

    }

    @PostConstruct
    public void postInit() {
        // do nothing
    }

    @Override
    public boolean checkSignature(String signature, String timestamp, String nonce) {
        return wxMpService.checkSignature(timestamp, nonce, signature);
    }

    @Override
    public String routeMessage(String xmlInMessage) {
        WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(xmlInMessage);
        WxMpXmlOutMessage outMessage = wxMpMessageRouter.route(inMessage);
        return outMessage.toXml();
    }

    @Override
    public String routeEncryptedMessage(String xmlInMessage, String timestamp, String nonce, String msgSignature) {
        WxMpXmlMessage inMessage = WxMpXmlMessage.fromEncryptedXml(xmlInMessage, wxMpConfigStorage, timestamp, nonce, msgSignature);
        WxMpXmlOutMessage outMessage = wxMpMessageRouter.route(inMessage);
        return outMessage.toEncryptedXml(wxMpConfigStorage);
    }
}
