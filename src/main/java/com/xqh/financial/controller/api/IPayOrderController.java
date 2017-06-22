package com.xqh.financial.controller.api;

import com.xqh.financial.entity.PayOrder;
import com.xqh.financial.entity.vo.PayOrderVO;
import com.xqh.financial.utils.PageResult;
import com.xqh.financial.utils.Search;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Created by hssh on 2017/5/14.
 */
@RequestMapping("/xqh/financial/pay/order")
public interface IPayOrderController
{

    @PostMapping("list")
    public PageResult<PayOrderVO> list(@RequestParam("search") @Valid @NotNull Search search,
                                       @RequestParam(value = "page", defaultValue = "1")  int page,
                                       @RequestParam(value = "size", defaultValue = "10") int size);

    @GetMapping("get/{id}")
    public PayOrder queryOne(@PathVariable("id") int id);

    @PostMapping("test")
    public PayOrder test();

}
