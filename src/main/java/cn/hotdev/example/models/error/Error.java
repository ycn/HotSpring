package cn.hotdev.example.models.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class Error {
    private long code;
    private String msg;
}
