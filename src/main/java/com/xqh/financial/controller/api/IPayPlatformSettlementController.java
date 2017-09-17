package com.xqh.financial.controller.api;

import com.xqh.financial.entity.vo.PayPlatformSettlementVO;
import com.xqh.financial.entity.vo.PaySimplePlatformVO;
import com.xqh.financial.utils.PageResult;
import com.xqh.financial.utils.Search;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by hssh on 2017/9/16.
 */
@RequestMapping("/xqh/financial/pay/platformSettlement")
public interface IPayPlatformSettlementController
{
    @PostMapping("/list")
    public PageResult<PayPlatformSettlementVO> list(@RequestParam("search") @Valid @NotNull Search search,
                                                    @RequestParam(value = "page", defaultValue = "1")  int page,
                                                    @RequestParam(value = "size", defaultValue = "10") int size);

        @PostMapping("simplePlatform/list")
    public List<PaySimplePlatformVO> simplePlatformList();

}
