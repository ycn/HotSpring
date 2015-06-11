package cn.hotdev.example.models.mss;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Created by andy on 6/10/15.
 */
@EqualsAndHashCode(callSuper = false)
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MssCreate {

    private String partner_id; //趣活快送提供的唯一的appKey，必填
    private String partner_order_id; //合作伙伴订单ID(唯一) ，必填
    private String push_time; //推送时间，必填，从1970年开始计算精确到秒的timestamp格式
    private String notify_url; //状态回调接口，必填，用于接收订单处理结果
    private String sign; //签名，必填

    private String invoice; //发票抬头，选填
    private String total_price; //订单总价，必填
    private String add_time; //下单时间 ，必填，格式同push_time
    private String request_time; //要求送达时间，选填，若不填，值为add_time+50分钟
    private String remark; //订单备注，选填
    private String if_store_pay; //餐厅是否付款，1：未付款，2：已付款 (必填)
    private String city; //订单所在城市，必填，汉字，拼音不识别
    private String if_pay; //是否付款，0未付款/1已付款，必填
    private String payment_name; //付款方式：货到付款/在线支付/POS支付，必填
    private String shipping_fee; //快递费，必填
    private String shipping_name; //配送方式：定时送达/及时送达，必填
    private String expectmeal_time; //预计出餐时间，选填
    private String order_placed; //餐厅是否下单，string格式的true/false，true代表餐厅已下单，false表示餐厅未下单 必填
    private String order_plat; //是否是导流订单，string格式的true/false，true代表是导流订单，false表示是自营订单 必填

    private MssCustomInfo custom_info;
    private MssOrderItems order_items;


    @EqualsAndHashCode(callSuper = false)
    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class MssCustomInfo {

        private String buyer_id; //用户ID，必填
        private String buyer_name; //用户名，和consignee必有其一
        private String consignee; //收货人名称
        private String phone_mob; //收货人手机号，和phone_tel必有其一
        private String phone_tel; //收货人座机号
        private String address; //收货人地址，必填

    }

    @EqualsAndHashCode(callSuper = false)
    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class MssStoreInfo {

        private String seller_id; //卖家ID，必填,如果是导流订单，则为趣活的店铺ID，如果是自营订单则为自营的店铺ID
        private String seller_name; //卖家名称，必填
        private String address; //卖家地址，必填
        private String tel; //卖家电话，必填
        private String store_lng; //店铺经度值
        private String store_lat; //店铺纬度值
    }

    @EqualsAndHashCode(callSuper = false)
    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class MssOrderItems {

        private List<MssOrderGood> order_goods;
        private MssStoreInfo store_info;
    }

    @EqualsAndHashCode(callSuper = false)
    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class MssOrderGood {

        private String goods_id; //商品ID，必填
        private String goods_name; //商品名称	，必填
        private String price; //商品价格，必填
        private String quantity; //商品数量，必填
        private String specification; //商品规格，必填
        private String goods_remark; //商品备注，选填
        private String discount; //菜品当前折扣，必填
        private String packing_fee; //菜品包装费，必填，可以为0

    }
}
