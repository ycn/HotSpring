package cn.hotdev.example.viewmodels.hello;

import cn.hotdev.example.models.hello.Hello;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by andy on 5/22/15.
 */
@NoArgsConstructor
@Data
public class HelloView {
    private long id;
    private String content;
    private String msg;

    public HelloView(Hello hello) {
        id = hello.getId();
        content = hello.getContent();
        msg = hello.getMsg();
    }
}
