package com.xqh.financial.controller.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Throwables;
import com.xqh.financial.controller.api.IJFTPayController;
import com.xqh.financial.entity.PayOrderSerial;
import com.xqh.financial.entity.PayPJI;
import com.xqh.financial.entity.other.CallbackEntity;
import com.xqh.financial.mapper.PayPJIMapper;
import com.xqh.financial.service.JFTPayService;
import com.xqh.financial.service.XQHPayAsyncService;
import com.xqh.financial.service.XQHPayTxService;
import com.xqh.financial.utils.CommonUtils;
import com.xqh.financial.utils.ExampleBuilder;
import com.xqh.financial.utils.Search;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by hssh on 2018/2/10.
 */
@RestController
@Slf4j
public class JFTPayController implements IJFTPayController
{
    @Resource
    private XQHPayTxService xqhPayTxService;
    @Resource
    private XQHPayAsyncService xqhPayAsyncService;
    @Resource
    private JFTPayService jftPayService;
    @Resource
    private PayPJIMapper payPJIMapper;

    @Override
    public void callback(HttpServletRequest req, HttpServletResponse resp)
    {
        String postParamStr = null;
        try
        {
            postParamStr = CommonUtils.getPostParams(req);
        } catch (IOException e)
        {
            log.error("金付通异步回调 解析参数异常");
            CommonUtils.writeResponse(resp, "fail");
            return ;
        }
        JSONObject params = JSONObject.parseObject(postParamStr);
        log.info("金付通异步回调 params:{}", JSONObject.toJSON(params));

        PayOrderSerial orderSerial = jftPayService.verifyCallbackParam(params);
        if(null == orderSerial)
        {
            log.error("金付通异步回调 校验不通过 params:{}", JSONObject.toJSON(params));
            CommonUtils.writeResponse(resp, "fail");
            return ;
        }

        // 获取秘钥
        Search search = new Search();
        search.put("appId_eq", orderSerial.getAppId());
        List<PayPJI> pjiList = payPJIMapper.selectByExample(new ExampleBuilder(PayPJI.class).search(search).build());
        if(pjiList.size() != 1)
        {
            log.error("金付通异步回调 配置有误 orderSerial:{}", JSONObject.toJSON(orderSerial));
            CommonUtils.writeResponse(resp, "fail");
            return ;
        }

        // 新增订单和回调记录
        CallbackEntity callbackEntity = null;
        try
        {
            callbackEntity = xqhPayTxService.insertOrderAndGenCallbackEntity(orderSerial, params.getString("transaction_id"), "金付通支付通道");
        } catch (DuplicateKeyException e)
        {
            // 重复回调 => 返回成功
            log.warn("金付通支付通道 异步回调 重复回调 orderSerial:{} param:{} e:{}", orderSerial, params, Throwables.getStackTraceAsString(e));
            jftPayService.sendResult(resp, pjiList.get(0).getJftKey());
            return ;
        }
        if(null == callbackEntity)
        {
            log.error("金付通支付通道 异步回调 插入订单失败 param:{}", params);
            CommonUtils.writeResponse(resp, "fail");
            return;
        }
        //返回成功
        log.warn("金付通支付通道 异步回调 回调成功 orderSerial:{} param:{}", orderSerial, params);
        jftPayService.sendResult(resp, pjiList.get(0).getJftKey());

        // 回调商户
        xqhPayAsyncService.callbackUser(callbackEntity, "金付通支付通道");
    }

}
