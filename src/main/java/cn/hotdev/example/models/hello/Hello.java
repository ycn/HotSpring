package cn.hotdev.example.models.hello;

import cn.hotdev.example.models.base.Base;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
public class Hello extends Base {
    private long id;
    private String content;
    private String msg;
}
