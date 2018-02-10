package com.xqh.financial.entity.vo;

import com.xqh.financial.utils.PageResult;
import lombok.Data;

/**
 * Created by hssh on 2017/10/4.
 */
@Data
public class PayVerifySearchVO
{
    private PageResult<PayVerifyVO> verifyVOPage;

    private PayVerifyTotalVO payVerifyTotalVO;

    private Double totalDiff;

}
