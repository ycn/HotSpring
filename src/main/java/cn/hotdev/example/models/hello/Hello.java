package cn.hotdev.example.models.hello;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by andy on 5/22/15.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Hello {
    private long id;
    private String content;
    private String msg;
}
