package cn.hotdev.example.weixin;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/from_weixin")
public class WeixinXmlController {

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final WeixinService weixinService;

    @Autowired
    public WeixinXmlController(HttpServletRequest request,
                               HttpServletResponse response,
                               WeixinService weixinService) {
        this.request = request;
        this.response = response;
        this.weixinService = weixinService;
    }

}
