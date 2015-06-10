package cn.hotdev.example.weixin.handlers;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutNewsMessage;

import java.util.Map;

/**
 * Created by andy on 6/10/15.
 */
public class MeishisongHandler implements WxMpMessageHandler {
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {

        WxMpXmlOutNewsMessage.Item item = new WxMpXmlOutNewsMessage.Item();
        item.setDescription("美食送测试");
        item.setPicUrl("http://placehold.it/350x150");
        item.setTitle("美食送测试");
        item.setUrl("http://www.hotdev.cn/mss.test.html");

        WxMpXmlOutNewsMessage m = WxMpXmlOutMessage.NEWS()
                .fromUser(wxMessage.getToUserName())
                .toUser(wxMessage.getFromUserName())
                .addArticle(item)
                .build();


        return m;
    }
}
