package com.xqh.financial.controller.api;

import com.xqh.financial.entity.vo.PayPlatformVO;
import com.xqh.financial.utils.PageResult;
import com.xqh.financial.utils.Search;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Created by hssh on 2017/10/8.
 */
@RequestMapping("/xqh/financial/pay/platform")
public interface IPayPlatformController
{
    @PostMapping("list")
    public PageResult<PayPlatformVO> list(@RequestParam("search") @Valid @NotNull Search search,
                                          @RequestParam(value = "page", defaultValue = "1")  int page,
                                          @RequestParam(value = "size", defaultValue = "10") int size);
}
