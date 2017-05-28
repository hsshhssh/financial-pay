package com.xqh.financial.controller.api;

import com.xqh.financial.entity.PayAppSettlement;
import com.xqh.financial.entity.PayUserSettlement;
import com.xqh.financial.entity.vo.PayAppSettlementVO;
import com.xqh.financial.utils.PageResult;
import com.xqh.financial.utils.Search;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * Created by hssh on 2017/5/14.
 */
@RequestMapping("/xqh/financial/settlement")
public interface ISettlementController
{

    @RequestMapping("/user/{day}")
    public Map<Integer, PayUserSettlement> getUserSettlementByDay(@PathVariable("day") int day);


    @RequestMapping("/app/{day}")
    public Map<Integer, PayAppSettlement> getAppSettlementByDay(@PathVariable("day") int day);


    @PostMapping("/app/list")
    public PageResult<PayAppSettlementVO> list(@RequestParam("search") @Valid @NotNull Search search,
                                               @RequestParam(value = "page", defaultValue = "1")  int page,
                                               @RequestParam(value = "size", defaultValue = "10") int size);

    @PostMapping("/data")
    public List<Double> getPayData(@RequestParam("userId") @Min(1) Integer userId);

}
