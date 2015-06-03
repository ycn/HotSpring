package cn.hotdev.example.internals.exceptions;

import cn.hotdev.example.internals.tools.Tools;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class NotFoundException extends RuntimeException {

    public NotFoundException(String format, Object... arguments) {
        super(Tools.formatString(format, arguments));
    }
}
