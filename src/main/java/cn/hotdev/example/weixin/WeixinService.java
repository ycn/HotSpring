package cn.hotdev.example.weixin;

import org.springframework.stereotype.Service;

@Service
public interface WeixinService {


    boolean checkSignature(String signature, String timestamp, String nonce);

    String routeMessage(String xmlInMessage);

    String routeEncryptedMessage(String xmlInMessage, String timestamp, String nonce, String msgSignature);
}
