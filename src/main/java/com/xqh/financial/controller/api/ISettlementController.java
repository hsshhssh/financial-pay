package com.xqh.financial.controller.api;

import com.xqh.financial.entity.PayUserSettlement;
import com.xqh.financial.entity.vo.PayAppSettlementVO;
import com.xqh.financial.entity.vo.PayUserSettlementVO;
import com.xqh.financial.utils.PageResult;
import com.xqh.financial.utils.Search;
import com.xqh.financial.utils.Sort;
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
    public Map<Integer, PayAppSettlementVO> getAppSettlementByDay(@PathVariable("day") int day);


    @PostMapping("/app/list")
    public PageResult<PayAppSettlementVO> list(@RequestParam("search") @Valid @NotNull Search search,
                                               @RequestParam(value = "page", defaultValue = "1")  int page,
                                               @RequestParam(value = "size", defaultValue = "10") int size,
                                               @RequestParam(value = "sort", required = false) Sort sort);

    @PostMapping("/user/list")
    public PageResult<PayUserSettlementVO> userList(@RequestParam("search") @Valid @NotNull Search search,
                                                    @RequestParam(value = "page", defaultValue = "1")  int page,
                                                    @RequestParam(value = "size", defaultValue = "10") int size,
                                                    @RequestParam(value = "sort", required = false) Sort sort);

    @PostMapping("/data")
    public List<Double> getPayData(@RequestParam("userId") @Min(1) Integer userId);

    @PostMapping("/app/month")
    public List<PayAppSettlementVO> appMonthList(@RequestParam(value = "userId", required = false) Integer userId,
                                                 @RequestParam(value = "appId", required = false) Integer appId,
                                                 @RequestParam(value = "month", required = false) Integer month,
                                                 @RequestParam(value = "year", required = false) Integer year);

}
