package com.xqh.financial.controller.api;

import com.xqh.financial.entity.dto.PayVerifyUpdateDTO;
import com.xqh.financial.entity.vo.PayVerifySearchVO;
import com.xqh.financial.utils.Search;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Created by hssh on 2017/10/3.
 */
@RequestMapping("/xqh/financial/pay/verify")
public interface IPayVerifyController
{
    @PostMapping
    public void update(@RequestBody @NotNull @Valid PayVerifyUpdateDTO dto,
                       HttpServletResponse resp);


    @PostMapping("search")
    public PayVerifySearchVO search(@RequestParam("search") @Valid @NotNull Search search,
                                    @RequestParam(value = "page", defaultValue = "1")  int page,
                                    @RequestParam(value = "size", defaultValue = "10") int size);

    @GetMapping("refresh/diffMap")
    public void refreshDiffMap();


}
