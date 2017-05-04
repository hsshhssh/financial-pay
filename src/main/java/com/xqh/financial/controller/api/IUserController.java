package com.xqh.financial.controller.api;

import com.xqh.financial.entity.dto.PayUserCreateDTO;
import com.xqh.financial.entity.dto.PayUserUpdateDTO;
import com.xqh.financial.entity.vo.PayUserVO;
import com.xqh.financial.utils.Search;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import java.util.List;

/**
 * Created by hssh on 2017/5/4.
 */
@RequestMapping("/pay/user")
public interface IUserController {

    @ApiOperation(value = "新增支付用户接口")
    @ApiImplicitParam(name = "user", value = "支付用户实体类", required = true, dataType = "PayUserCreateDTO")
    @PutMapping
    public int insert(@RequestBody @Valid  PayUserCreateDTO user,
                      HttpServletResponse resp);

    @ApiOperation(value = "修改支付用户信息接口")
    @ApiImplicitParam(name = "user", value = "支付用户实体类", required = true, dataType = "PayUserUpdateDTO")
    @PostMapping
    public int update(@RequestBody @Valid PayUserUpdateDTO user);

    @ApiOperation(value = "查询支付用户列表接口")
    @ApiImplicitParams({
        //@ApiImplicitParam
    })
    public List<PayUserVO> queryList(@RequestParam("search") Search search,
                                     @RequestParam(value = "page", defaultValue = "1") Integer page,
                                     @RequestParam(value = "size", defaultValue = "10") @Max(1000) Integer size);

}
