package cn.hotdev.example.models.error;

import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data
public class Error {
    private Integer status;
    private String message;
}
