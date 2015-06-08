package cn.hotdev.example.models.exceptions;


import cn.hotdev.example.tools.StringTool;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class WeixinRuntimeException extends RuntimeException {

    public WeixinRuntimeException(String format, Object... arguments) {
        super(StringTool.formatString(format, arguments));
    }
}
