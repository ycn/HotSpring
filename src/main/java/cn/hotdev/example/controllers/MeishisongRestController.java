package cn.hotdev.example.controllers;

import cn.hotdev.example.models.mss.MssAck;
import cn.hotdev.example.models.mss.MssCreate;
import cn.hotdev.example.models.mss.MssCreateValidator;
import cn.hotdev.example.models.mss.MssResponse;
import cn.hotdev.example.services.MssService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by andy on 6/10/15.
 */
@RestController
@RequestMapping("/mss_test")
public class MeishisongRestController extends BaseRestController {
    private static final Logger log = LoggerFactory.getLogger(MeishisongRestController.class);

    private static final String STATE_REF = "信息处理状态：-1，订单参数缺失 ；-2，用户信息缺失；-3，菜品信息缺失；-4，店铺信息缺失；-5，店铺未开业；-6，非法联系方式；-7，店铺信息处理失败；-8，用户信息处理失败；-9，用户地址保存失败；-10，创建菜品《XXX》的分类有误；-11，创建菜品《XXX》有误；-12，菜品《XXX》价格低于美食送价格；1，订单生成成功；-20，订单生成失败";

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final MssCreateValidator mssCreateValidator;
    private final MssService mssService;

    @Autowired
    public MeishisongRestController(HttpServletRequest request,
                                    HttpServletResponse response,
                                    MssCreateValidator mssCreateValidator,
                                    MssService mssService) {
        this.request = request;
        this.response = response;
        this.mssCreateValidator = mssCreateValidator;
        this.mssService = mssService;
    }

    @InitBinder("mssCreate")
    protected void initMssCreateBinder(WebDataBinder binder) {
        binder.setValidator(mssCreateValidator);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public MssResponse create(@RequestBody MssCreate mssCreate) throws Exception {

        response.addHeader("Access-Control-Allow-Origin", "*");

        return mssService.create(mssCreate);

    }

    @RequestMapping(value = "/on_notify", method = RequestMethod.GET)
    public MssAck onNotify(@RequestParam(value = "order_state", required = true) int orderState,
                           @RequestParam(value = "partner_order_id", required = true) String partnerOrderId,
                           @RequestParam(value = "mss_order_id", required = true) String mssOrderId,
                           @RequestParam(value = "push_time", required = true) int pushTime,
                           @RequestParam(value = "msg", required = true) String msg,
                           @RequestParam(value = "state", required = false) int state,
                           @RequestParam(value = "action", required = false) String action,
                           @RequestParam(value = "emp_name", required = false) String empName,
                           @RequestParam(value = "tel", required = false) String tel,
                           @RequestParam(value = "actual_receipt", required = false) String actualReceipt, // 实收
                           @RequestParam(value = "actual_expend", required = false) String actualExpend, // 实付
                           @RequestParam(value = "rec_reason", required = false) String recReason, // 收款差价原因
                           @RequestParam(value = "exp_reason", required = false) String expReason, // 付款差价原因
                           @RequestParam(value = "reason", required = false) String reason // 订单取消原因
    ) throws Exception {

        log.info("[美食送] 收到 callback: order_state={}, partner_order_id={}, mss_order_id={}, push_time={}, msg={}",
                orderState,
                partnerOrderId,
                mssOrderId,
                pushTime,
                msg);


        switch (orderState) {

            // 已提交
            case 0:

                if (state != 1) {
                    log.info("[美食送] 收单失败: state={}, action={}, 参考={}",
                            state,
                            action,
                            STATE_REF);
                } else {
                    log.info("[美食送] 收单成功: action={}", action);
                }

                break;

            // 已确认
            case 1:
                log.info("[美食送] 订单已被确认");
                break;

            // 已分配
            case 2:
                log.info("[美食送] 订单已被分配");
                break;

            // 送餐员已取餐
            case 6:
                log.info("[美食送] 送餐员已取餐: emp_name={}, tel={}", empName, tel);
                break;

            // 已送达
            case 3:
                log.info("[美食送] 已送达: actual_receipt={}, actual_expend={}, rec_reason={}, exp_reason={}",
                        actualReceipt, actualExpend, recReason, expReason);
                break;

            // 已取消
            case 4:
                log.info("[美食送] 已取消: reason={}", reason);
                break;

            // 异常（需要重新提交订单）
            case 5:
                log.info("[美食送] 异常订单");
                break;

            default:
                log.info("[美食送] 非法的订单状态: order_state={}", orderState);
                break;

        }


        return new MssAck();
    }
}
