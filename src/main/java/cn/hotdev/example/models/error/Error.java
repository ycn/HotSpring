package cn.hotdev.example.models.error;

import cn.hotdev.example.constants.ErrorStatus;
import cn.hotdev.example.models.base.Base;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;


@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
public class Error extends Base {
    private ErrorStatus status;
    private String message;

    public Error(ErrorStatus status) {
        this.status = status;
        this.message = status.getMessage();
    }

    public Error(ErrorStatus status, String format, Object... arguments) {
        this.status = status;
        this.message = String.format(format.replaceAll("\\{\\}", "%s"), arguments);
    }
}
