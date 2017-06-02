package com.xqh.financial.controller.api;

import com.xqh.financial.entity.vo.PayCFRVO;
import com.xqh.financial.utils.PageResult;
import com.xqh.financial.utils.Search;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by hssh on 2017/5/25.
 */
@RequestMapping("/xqh/financial/pay/cfr")
public interface IPayCFRController
{
    @PostMapping("list")
    public PageResult<PayCFRVO> list(@RequestParam("search") @Valid @NotNull Search search,
                                     @RequestParam(value = "page", defaultValue = "1")  int page,
                                     @RequestParam(value = "size", defaultValue = "10") int size);


    @PostMapping("callback")
    public int callback(@RequestParam("id") @Min(1) int id);


}
