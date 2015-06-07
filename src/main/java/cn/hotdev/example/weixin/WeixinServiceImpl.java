package cn.hotdev.example.weixin;

import cn.hotdev.example.services.ConfigService;
import lombok.NoArgsConstructor;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@NoArgsConstructor
@Service
public class WeixinServiceImpl implements WeixinService {

    private static final Logger log = LoggerFactory.getLogger(WeixinServiceImpl.class);

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

        wxMpMessageRouter = new WxMpMessageRouter(wxMpService);
        wxMpMessageRouter
                /* test message */
                .rule()
                .async(false)
                .content("哈哈") // 拦截内容为“哈哈”的消息
                .handler(new WeixinTestMessageHandler())
                .end()
                /* message */
                .rule()
                .async(false)
                .handler(new WeixinMessageHandler())
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
        log.info("got weixin message: {}", inMessage);
        WxMpXmlOutMessage outMessage = wxMpMessageRouter.route(inMessage);
        return outMessage.toXml();
    }

    @Override
    public String routeEncryptedMessage(String xmlInMessage, String timestamp, String nonce, String msgSignature) {
        WxMpXmlMessage inMessage = WxMpXmlMessage.fromEncryptedXml(xmlInMessage, wxMpConfigStorage, timestamp, nonce, msgSignature);
        log.info("got weixin encrypted message: {}", inMessage);
        WxMpXmlOutMessage outMessage = wxMpMessageRouter.route(inMessage);
        return outMessage.toEncryptedXml(wxMpConfigStorage);
    }
}
