package com.xqh.financial.controller.impl;

import com.github.pagehelper.Page;
import com.google.common.collect.ImmutableMap;
import com.xqh.financial.controller.api.IPayVerifyController;
import com.xqh.financial.entity.PayVerify;
import com.xqh.financial.entity.dto.PayVerifyUpdateDTO;
import com.xqh.financial.entity.vo.PayVerifySearchVO;
import com.xqh.financial.entity.vo.PayVerifyTotalVO;
import com.xqh.financial.entity.vo.PayVerifyVO;
import com.xqh.financial.mapper.PayVerifyMapper;
import com.xqh.financial.service.PayVerifyService;
import com.xqh.financial.utils.*;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hssh on 2017/10/3.
 */
@RestController
public class PayVerifyController implements IPayVerifyController
{
    private static Logger logger = LoggerFactory.getLogger(IPayVerifyController.class);

    @Autowired
    private PayVerifyMapper payVerifyMapper;

    @Autowired
    private PayVerifyService payVerifyService;

    public static Map<Integer, Double> diffMap = new HashMap<>();

    @Override
    public void update(@RequestBody @NotNull @Valid PayVerifyUpdateDTO dto,
                       HttpServletResponse resp)
    {
        PayVerify payVerify = DozerUtils.map(dto, PayVerify.class);

        PayVerify verifyOld = payVerifyMapper.selectByPrimaryKey(dto.getId());

        double diff = DoubleUtils.sub(verifyOld.getSettlementMoney(), payVerify.getTransfer1(), payVerify.getTransfer2(), payVerify.getTransfer3(), payVerify.getTransfer4(), payVerify.getTransfer5(), payVerify.getTransfer6());

        //if(diff < 0)
        //{
        //    logger.error("转账金额大于结算金额");
        //    CommonUtils.sendError(resp, ErrorResponseEunm.INVALID_TRANFER_MONEY);
        //    return;
        //}

        payVerify.setDiff(diff);
        payVerify.setUpdateTime((int) (System.currentTimeMillis()/1000));

        payVerifyMapper.updateByPrimaryKeySelective(payVerify);

    }

    @Override
    public PayVerifySearchVO search(@RequestParam("search") @Valid @NotNull Search search,
                                    @RequestParam(value = "page", defaultValue = "1")  int page,
                                    @RequestParam(value = "size", defaultValue = "10") int size)
    {
        Example example = new ExampleBuilder(PayVerify.class).search(search).sort(Arrays.asList("id_desc")).build();

        Page<PayVerify> verifyList = (Page<PayVerify>) payVerifyMapper.selectByExampleAndRowBounds(example, new RowBounds(page, size));

        PayVerifySearchVO res = new PayVerifySearchVO();
        // 列表数据
        res.setVerifyVOPage(new PageResult<>(verifyList.getTotal(), DozerUtils.mapList(verifyList.getResult(), PayVerifyVO.class)));


        // 列表总计数据
        PayVerifyTotalVO totalVO = new PayVerifyTotalVO();
        totalVO.setTotalMoneyTotal((double) 0);
        totalVO.setSettlementMoneyTotal((double) 0);
        totalVO.setTransfer1Total((double) 0);
        totalVO.setTransfer2Total((double) 0);
        totalVO.setTransfer3Total((double) 0);
        totalVO.setTransfer4Total((double) 0);
        totalVO.setTransfer5Total((double) 0);
        totalVO.setTransfer6Total((double) 0);
        totalVO.setDiffTotal((double) 0);

        for (PayVerify payVerify : verifyList)
        {
            totalVO.setTotalMoneyTotal(totalVO.getTotalMoneyTotal() + (payVerify.getTotalMoney() == null ? 0 : payVerify.getTotalMoney()));
            totalVO.setSettlementMoneyTotal(totalVO.getSettlementMoneyTotal() + (payVerify.getSettlementMoney() == null ? 0 : payVerify.getSettlementMoney()));
            totalVO.setTransfer1Total(totalVO.getTransfer1Total() + (payVerify.getTransfer1() == null ? 0 : payVerify.getTransfer1()));
            totalVO.setTransfer2Total(totalVO.getTransfer2Total() + (payVerify.getTransfer2() == null ? 0 : payVerify.getTransfer2()));
            totalVO.setTransfer3Total(totalVO.getTransfer3Total() + (payVerify.getTransfer3() == null ? 0 : payVerify.getTransfer3()));
            totalVO.setTransfer4Total(totalVO.getTransfer4Total() + (payVerify.getTransfer4() == null ? 0 : payVerify.getTransfer4()));
            totalVO.setTransfer5Total(totalVO.getTransfer5Total() + (payVerify.getTransfer5() == null ? 0 : payVerify.getTransfer5()));
            totalVO.setTransfer6Total(totalVO.getTransfer6Total() + (payVerify.getTransfer6() == null ? 0 : payVerify.getTransfer6()));
            totalVO.setDiffTotal(totalVO.getDiffTotal() + payVerify.getDiff());
        }

        totalVO.setTotalMoneyTotal(DoubleUtils.div(totalVO.getTotalMoneyTotal(), 100));
        totalVO.setSettlementMoneyTotal(DoubleUtils.div(totalVO.getSettlementMoneyTotal(), 100));
        totalVO.setTransfer1Total(DoubleUtils.div(totalVO.getTransfer1Total(), 100));
        totalVO.setTransfer2Total(DoubleUtils.div(totalVO.getTransfer2Total(), 100));
        totalVO.setTransfer3Total(DoubleUtils.div(totalVO.getTransfer3Total(), 100));
        totalVO.setTransfer4Total(DoubleUtils.div(totalVO.getTransfer4Total(), 100));
        totalVO.setTransfer5Total(DoubleUtils.div(totalVO.getTransfer5Total(), 100));
        totalVO.setTransfer6Total(DoubleUtils.div(totalVO.getTransfer6Total(), 100));
        totalVO.setDiffTotal(DoubleUtils.div(totalVO.getDiffTotal(), 100));

        res.setPayVerifyTotalVO(totalVO);


        // 总差额
        Integer userId = search.get("userId_eq") == null ? null : Integer.valueOf((String.valueOf(search.get("userId_eq"))));
        Double totalDiff = Double.valueOf(0);
        if(null != userId) {
            totalDiff = diffMap.get(userId);
        } else {
            for (Integer tempUserId : diffMap.keySet())
            {
                totalDiff = DoubleUtils.add(totalDiff, diffMap.get(tempUserId));
            }
        }
        res.setTotalDiff(totalDiff);

        return res;
    }

    @Override
    public void refreshDiffMap()
    {
        diffMap = ImmutableMap.copyOf(payVerifyService.getDiffMap());
    }
}
