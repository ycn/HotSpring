package cn.hotdev.example.viewmodels.error;

import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class ErrorView {
    private long code;
    private String msg;

    public ErrorView(cn.hotdev.example.models.error.Error error) {
        code = error.getCode();
        msg = error.getMsg();
    }
}
