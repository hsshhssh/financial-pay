package com.xqh.financial.controller.api;

import com.xqh.financial.entity.PayApp;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Created by hssh on 2017/5/26.
 */
@RequestMapping("/xqh/financial/pay/app")
public interface IPayAppController
{

    @PostMapping("listNoPage")
    public List<PayApp> listNoPage(@RequestParam("userId") int userId);

}
