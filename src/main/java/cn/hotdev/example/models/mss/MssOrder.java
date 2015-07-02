package cn.hotdev.example.models.mss;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Created by andy on 6/11/15.
 */
@EqualsAndHashCode(callSuper = false)
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MssOrder implements Serializable {

    private String partner_id; //趣活快送提供的唯一的appKey，必填
    private String partner_order_id; //合作伙伴订单ID(唯一) ，必填
    private String push_time; //推送时间，必填，从1970年开始计算精确到秒的timestamp格式
    private String notify_url; //状态回调接口，必填，用于接收订单处理结果
    private String sign; //签名，必填

}
