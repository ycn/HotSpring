package cn.hotdev.example.internals.exceptions;

import cn.hotdev.example.internals.tools.Tools;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class AlreadyExistsException extends RuntimeException {

    public AlreadyExistsException(String format, Object... arguments) {
        super(Tools.formatString(format, arguments));
    }
}
