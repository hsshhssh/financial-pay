package com.xqh.financial.controller;

import com.google.common.collect.Maps;
import com.xqh.financial.entity.other.MCHPayEntity;
import com.xqh.financial.utils.CommonUtils;
import com.xqh.financial.utils.MD5Facade;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Created by hssh on 2017/6/19.
 */
@Controller
@RequestMapping("mch")
public class MCHPayController
{
    private static Logger logger = LoggerFactory.getLogger(MCHPayController.class);

    @ResponseBody
    @GetMapping("pay")
    public void pay(@RequestParam("money") int money, HttpServletRequest req, HttpServletResponse resp)
    {
        try
        {
            //req.setAttribute("test", 123);
            req.getRequestDispatcher("/mch/pay/html").forward(req, resp);
        }
        catch (ServletException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @GetMapping("pay/html")
    public String payJsp(HttpServletRequest req, HttpServletResponse resp, ModelMap modelMap)
    {
        Map<String, String> map = Maps.newHashMap();

        map.put("funcode", "WP001");
        map.put("appId", "149788569308934");
        map.put("mhtOrderNo", "mhtOrderNo");
        map.put("mhtOrderName", "mhtOrderName");
        map.put("version", "1.0.0");
        map.put("mhtCurrencyType", "156");
        map.put("mhtOrderAmt", "1");
        map.put("mhtOrderDetail", "mhtOrderDetail");
        map.put("mhtOrderType", "01"); //订单类型 普通消费
        map.put("mhtOrderStartTime", CommonUtils.getFormatDate("yyyyMMddHHmmss"));
        map.put("notifyUrl", "www.baidu.com");
        map.put("frontNotifyUrl", "www.baidu.com");
        map.put("mhtCharset", "UTF-8");
        map.put("deviceType", "0601");
        map.put("outputType", "2"); //
        map.put("mhtReserved", "mchBankId=123&mhtReserved");
        map.put("payChannelType", "13");
        map.put("mhtSignType", "MD5");
        //map.put("mhtLimitPay", "1");
        map.put("payAccNo", "6222023602099939977");
        map.put("mhtSignature", MD5Facade.getFormDataParamMD5(map, "Hcb8kPw2VF1Tehp1doinI6L0K3sPcMGb", "UTF-8"));

        //Beanutils.BeanMap(obj);

        MCHPayEntity mchPayEntity = new MCHPayEntity();
        try {
            BeanUtils.populate(mchPayEntity, map);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        modelMap.addAttribute("command", mchPayEntity);

        // return模板文件的名称，对应src/main/resources/templates/index.html
        return "index";
    }

    @GetMapping("/pay/post")
    public void payPost(HttpServletRequest req, HttpServletResponse resp)
    {
        System.out.println("pay/post");
    }


    @PostMapping("callback")
    public void callback(HttpServletRequest req, HttpServletResponse resp)
    {
        BufferedReader reader = null;
        try {
            reader = req.getReader();
            StringBuilder reportBuilder = new StringBuilder();
            String tempStr = "";
            while((tempStr = reader.readLine()) != null){
                reportBuilder.append(tempStr);
            }

            logger.info("/mch/callback param: " +reportBuilder.toString());

            resp.getWriter().print("success=Y");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @PostMapping("notify")
    public void notify(HttpServletRequest req, HttpServletResponse resp)
    {
        Map<String, String[]> params = req.getParameterMap();
        String queryString = "";
        for (String key : params.keySet()) {
            String[] values = params.get(key);
            for (int i = 0; i < values.length; i++) {
                String value = values[i];
                queryString += key + "=" + value + "&";
            }
        }
        logger.info("/mch/notify param: " + queryString.substring(0, queryString.length() - 1));
    }

}
