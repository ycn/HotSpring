package cn.hotdev.example.models.exceptions;

import cn.hotdev.example.tools.StringTool;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class NotFoundException extends RuntimeException {

    public NotFoundException(String format, Object... arguments) {
        super(StringTool.formatString(format, arguments));
    }
}
