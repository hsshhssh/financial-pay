package com.xqh.financial.controller;

import com.alibaba.fastjson.JSONObject;
import com.xqh.financial.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.TreeMap;

/**
 * Created by hssh on 2017/7/22.
 */
@RestController
@RequestMapping("/reyun")
public class ReYunADDemoController
{

    private static Logger logger = LoggerFactory.getLogger(ReYunADDemoController.class);


    @GetMapping("ad")
    public void ad(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        // 丧东
        //String idfa = "2D41B44B-6CA1-4201-ACE5-CC064CE8E881";
        //String mac = "48:3B:38:D6:21:FF";

        // me
        String idfa = "A89CF116-49C1-493A-9EBB-CB68AF567BAD";
        String mac = "90:B9:31:58:AE:80";
        String muid = CommonUtils.getMd5(idfa);
        String callback = URLEncoder.encode("http://139.196.51.152:8080/reyun/callback?id=1&app=2","UTF-8");



        StringBuffer sb = new StringBuffer();

        sb.append("http://uri6.com/ZNVV3m");
        sb.append("?idfa=" + idfa);
        sb.append("&mac=" + mac);
        sb.append("&muid=" + muid);
        sb.append("&clickid=clickid");
        sb.append("&callback=" + callback);

        logger.info("热云联盟 url: {}", sb.toString());

        resp.sendRedirect(sb.toString());

    }

    @GetMapping("callback")
    public void callback(HttpServletRequest req, HttpServletResponse resp)
    {
        TreeMap<String, String> params = CommonUtils.getParams(req);
        logger.info("热云 callback params:{}", JSONObject.toJSON(params));
    }

}
