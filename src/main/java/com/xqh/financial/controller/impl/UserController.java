package com.xqh.financial.controller.impl;

import com.xqh.financial.controller.api.IUserController;
import com.xqh.financial.entity.PayUser;
import com.xqh.financial.entity.dto.PayUserCreateDTO;
import com.xqh.financial.entity.dto.PayUserUpdateDTO;
import com.xqh.financial.entity.vo.PayUserVO;
import com.xqh.financial.service.UserService;
import com.xqh.financial.utils.CommonUtils;
import com.xqh.financial.utils.DozerUtils;
import com.xqh.financial.utils.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import java.util.List;

/**
 * Created by hssh on 2017/5/4.
 */
@RestController
public class UserController implements IUserController {

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Override
    public int insert(@RequestBody @Valid PayUserCreateDTO user,
                      HttpServletResponse resp) {
        PayUser payUser = DozerUtils.map(user, PayUser.class);
        int id = 0;

        try {
           id = userService.insertSingle(payUser);
        } catch (DuplicateKeyException e) {
            logger.error("用户名重复 username{} user:{}", user.getUsername(), user);
            CommonUtils.sendError(resp ,HttpServletResponse.SC_EXPECTATION_FAILED, "用户名重复" + user.getUsername());
        }
        return id;
    }

    @Override
    public int update(@RequestBody @Valid PayUserUpdateDTO user) {
        return 0;
    }

    @Override
    public List<PayUserVO> queryList(@RequestParam("search") Search search, @RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "size", defaultValue = "10") @Max(1000) Integer size) {
        return null;
    }
}
