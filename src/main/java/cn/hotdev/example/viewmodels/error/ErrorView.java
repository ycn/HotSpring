package cn.hotdev.example.viewmodels.error;

import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class ErrorView {
    private int status;
    private String message;

    public ErrorView(cn.hotdev.example.models.error.Error error) {
        status = error.getStatus().getValue();
        message = error.getMessage();
    }
}
