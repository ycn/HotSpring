package cn.hotdev.example.models.exceptions;


import cn.hotdev.example.utils.StringTool;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class WeixinException extends RuntimeException {
    
    public WeixinException(String format, Object... arguments) {
        super(StringTool.formatString(format, arguments));
    }
}
