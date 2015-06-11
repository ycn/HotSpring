package cn.hotdev.example.services;

import cn.hotdev.example.models.mss.MssCreate;
import cn.hotdev.example.models.mss.MssOrder;
import cn.hotdev.example.models.mss.MssResponse;
import cn.hotdev.example.tools.ObjectTool;
import cn.hotdev.example.tools.StringTool;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andy on 6/10/15.
 */
@Service
public class MssServiceImpl implements MssService {

    private static final Logger log = LoggerFactory.getLogger(MssServiceImpl.class);


    private ConfigService configService;

    @Autowired
    public MssServiceImpl(ConfigService configService) {
        this.configService = configService;
    }


    @Override
    public MssResponse support(double lng, double lat) throws Exception {

        String entry = configService.getConfig("mss_url");
        String appId = configService.getConfig("mss_app_id");
        String appKey = configService.getConfig("mss_app_key");

        String sign = StringTool.md5("lng={}lat={}partner_id={}", lng, lat, appId);

        HttpResponse<JsonNode> json = Unirest.get(entry + "Getregion/getByLng")
                .queryString("partner_id", appId)
                .queryString("lng", lng)
                .queryString("lat", lat)
                .queryString("sign", sign)
                .asJson();


        MssResponse response = getObject(json, MssResponse.class);

        log.debug("sign={}", sign);
        log.debug(StringTool.formatString("mssResponse: {}", response));

        return response;
    }

    @Override
    public MssResponse create(MssCreate mssCreate) throws Exception {

        String entry = configService.getConfig("mss_url");
        String appId = configService.getConfig("mss_app_id");
        String appKey = configService.getConfig("mss_app_key");

//        support(116.412, 39.91209);

        // 创建模拟订单

        int currentTime = (int) (System.currentTimeMillis() / 1000);

        mssCreate.setPartner_id(appId);
        mssCreate.setPush_time(currentTime + "");
        mssCreate.setNotify_url("http://wx.hotdev.cn/mss_test/on_notify");


        String sign = StringTool.md5("partner_id={}#partner_order_id={}#push_time={}#notify_url={}#key={}",
                mssCreate.getPartner_id(),
                mssCreate.getPartner_order_id(),
                mssCreate.getPush_time(),
                mssCreate.getNotify_url(),
                appKey);

        mssCreate.setSign(sign);


        mssCreate.setAdd_time(currentTime + "");
        mssCreate.setRequest_time((currentTime + 40 * 60) + ""); // 40分钟 送达
        mssCreate.setRemark("测试订单");
        mssCreate.setIf_store_pay("1");
        mssCreate.setIf_pay("0"); // 未付款
        mssCreate.setPayment_name("货到付款");
        mssCreate.setCity("北京");
        mssCreate.setShipping_fee("0"); // 快递费
        mssCreate.setShipping_name("及时送达");
        mssCreate.setOrder_placed("true");
        mssCreate.setOrder_plat("false");

        mssCreate.setCustom_info(getCustomInfo(mssCreate));
        mssCreate.setOrder_items(getOrderItems(mssCreate));

        String value = ObjectTool.serialize(mssCreate);
        log.info("sent to mss: (create) {}", value);

        // 发送请求
        HttpResponse<JsonNode> json = Unirest.post(entry + "order/addRecord")
                .body(value)
                .asJson();

        MssResponse response = getObject(json, MssResponse.class);

        return response;
    }

    @Override
    public MssResponse cancel(MssOrder mssOrder) throws Exception {
        String entry = configService.getConfig("mss_url");
        String appId = configService.getConfig("mss_app_id");
        String appKey = configService.getConfig("mss_app_key");

        int currentTime = (int) (System.currentTimeMillis() / 1000);

        mssOrder.setPartner_id(appId);
        mssOrder.setPush_time(currentTime + "");
        mssOrder.setNotify_url("http://wx.hotdev.cn/mss_test/on_notify");


        String sign = StringTool.md5("partner_id={}#partner_order_id={}#push_time={}#notify_url={}#key={}",
                mssOrder.getPartner_id(),
                mssOrder.getPartner_order_id(),
                mssOrder.getPush_time(),
                mssOrder.getNotify_url(),
                appKey);

        mssOrder.setSign(sign);

        String value = ObjectTool.serialize(mssOrder);
        log.info("sent to mss: (cancel) {}", value);

        // 发送请求
        HttpResponse<JsonNode> json = Unirest.post(entry + "order/cancel")
                .body(value)
                .asJson();

        MssResponse response = getObject(json, MssResponse.class);

        return response;
    }

    private MssCreate.MssCustomInfo getCustomInfo(MssCreate mssCreate) {
        MssCreate.MssCustomInfo mssCustomInfo = mssCreate.new MssCustomInfo();

        mssCustomInfo.setBuyer_id("KHW-U-001");
        mssCustomInfo.setBuyer_name("袁洋");
        mssCustomInfo.setConsignee("袁洋");
        mssCustomInfo.setPhone_mob("18518633326");
        mssCustomInfo.setAddress("安宁庄西路 IMOMA 311");

        return mssCustomInfo;
    }

    private MssCreate.MssStoreInfo getStoreInfo(MssCreate mssCreate) {
        MssCreate.MssStoreInfo mssStoreInfo = mssCreate.new MssStoreInfo();

        mssStoreInfo.setSeller_id("KHW-S-001");
        mssStoreInfo.setSeller_name("金百万马甸店");
        mssStoreInfo.setTel("4006662000");
        mssStoreInfo.setAddress("北京市朝阳区裕民路3号(近北辰路) 金百万马甸店");

        return mssStoreInfo;
    }

    private MssCreate.MssOrderItems getOrderItems(MssCreate mssCreate) {
        MssCreate.MssOrderItems mssOrderItems = mssCreate.new MssOrderItems();

        mssOrderItems.setStore_info(getStoreInfo(mssCreate));

        List<MssCreate.MssOrderGood> list = new ArrayList<MssCreate.MssOrderGood>();

        MssCreate.MssOrderGood good1 = mssCreate.new MssOrderGood();
        good1.setGoods_id("KHW-G-001");
        good1.setGoods_name("准成品-青椒土豆丝");
        good1.setGoods_remark("测试订单: 要多辣");
        good1.setPrice("32.0");
        good1.setQuantity("2");
        good1.setSpecification("spe");
        good1.setDiscount("1.0");
        good1.setPacking_fee("0");
        list.add(good1);

        MssCreate.MssOrderGood good2 = mssCreate.new MssOrderGood();
        good2.setGoods_id("KHW-G-002");
        good2.setGoods_name("准成品-西红柿鸡蛋");
        good2.setGoods_remark("测试订单");
        good2.setPrice("12.0");
        good2.setQuantity("1");
        good2.setSpecification("spe");
        good2.setDiscount("1.0");
        good2.setPacking_fee("0");
        list.add(good2);

        mssOrderItems.setOrder_goods(list);
        return mssOrderItems;
    }

    private <T> T getObject(HttpResponse<JsonNode> json, Class<T> type) throws IOException {
        String value = json.getBody().toString();
        log.info("mss test ret: {}", value);
        return ObjectTool.unserialize(value, type);
    }
}
