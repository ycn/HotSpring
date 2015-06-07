package cn.hotdev.example.weixin;


import cn.hotdev.example.models.exceptions.BadRequestException;
import cn.hotdev.example.models.exceptions.WeixinException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@ResponseBody
@RequestMapping("/from_weixin")
public class WeixinController {

    private static final Logger log = LoggerFactory.getLogger(WeixinController.class);

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final WeixinService weixinService;

    @Autowired
    public WeixinController(HttpServletRequest request,
                            HttpServletResponse response,
                            WeixinService weixinService) {
        this.request = request;
        this.response = response;
        this.weixinService = weixinService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
    public String echo(@RequestParam(value = "signature", required = true) String signature,
                       @RequestParam(value = "timestamp", required = true) String timestamp,
                       @RequestParam(value = "nonce", required = true) String nonce,
                       @RequestParam(value = "echostr", required = true) String echoStr) {

        try {

            if (weixinService.checkSignature(signature, timestamp, nonce)) {
                return plainTextMessage(echoStr);
            } else {
                throw new WeixinException("not from weixin server");
            }
        } catch (Exception e) {
            throw new WeixinException("weixin echo service error: {}", e.toString());
        }
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/xml;charset=UTF-8")
    public String onMessage(@RequestParam(value = "signature", required = true) String signature,
                            @RequestParam(value = "timestamp", required = true) String timestamp,
                            @RequestParam(value = "nonce", required = true) String nonce,
                            @RequestParam(value = "encrypt_type", defaultValue = "raw", required = false) String encryptType,
                            @RequestParam(value = "msg_signature", required = false) String msgSignature,
                            @RequestBody(required = true) String xmlInMessage) {

        try {

            if (weixinService.checkSignature(signature, timestamp, nonce)) {

                String outMessage;

                if ("raw".equalsIgnoreCase(encryptType)) {

                    outMessage = weixinService.routeMessage(xmlInMessage);

                } else if ("aes".equalsIgnoreCase(encryptType)) {

                    if (msgSignature == null || msgSignature.isEmpty()) {
                        throw new BadRequestException("msg_signature is missing");
                    }

                    outMessage = weixinService.routeEncryptedMessage(xmlInMessage, timestamp, nonce, msgSignature);

                } else {
                    throw new WeixinException("encrypt_type not support");
                }

                return outMessage;

            } else {
                throw new WeixinException("not from weixin server");
            }
        } catch (Exception e) {
            throw new WeixinException("weixin message service error: {}", e.toString());
        }
    }

    private String plainTextMessage(String message) {
        response.setContentType("text/plain;charset=UTF-8");
        return message;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public String otherException(Exception e) {
        log.error("weixin controller got error: {}", e.toString());
        return plainTextMessage("server error");
    }
}
