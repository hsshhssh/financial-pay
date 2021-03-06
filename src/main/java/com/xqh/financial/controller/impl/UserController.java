package com.xqh.financial.controller.impl;

import com.github.pagehelper.Page;
import com.xqh.financial.controller.api.IUserController;
import com.xqh.financial.entity.PayUser;
import com.xqh.financial.entity.XqhUserRole;
import com.xqh.financial.entity.dto.PayUserCreateDTO;
import com.xqh.financial.entity.dto.PayUserUpdateDTO;
import com.xqh.financial.entity.vo.PayUserVO;
import com.xqh.financial.entity.vo.UserInfoVO;
import com.xqh.financial.mapper.PayUserMapper;
import com.xqh.financial.mapper.XqhUserRoleMapper;
import com.xqh.financial.service.UserService;
import com.xqh.financial.utils.*;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hssh on 2017/5/4.
 */
@RestController
public class UserController implements IUserController {

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PayUserMapper payUserMapper;

    @Autowired
    private XqhUserRoleMapper userRoleMapper;

    @Override
    public int insert(@RequestBody @Valid PayUserCreateDTO user,
                      HttpServletResponse resp) {
        PayUser payUser = DozerUtils.map(user, PayUser.class);
        int id = 0;

        try
        {
           id = userService.insertSingle(payUser);
        }
        catch (DuplicateKeyException e)
        {
            logger.error("用户名重复 username{} user:{}", user.getUsername(), user);
            CommonUtils.sendError(resp, ErrorResponseEunm.DUPLICATE_USERNAME);
        }
        return id;
    }

    @Override
    public int update(@RequestBody @Valid PayUserUpdateDTO user)
    {
        PayUser payUser = DozerUtils.map(user, PayUser.class);

        payUser.setUpdateTime((int) (System.currentTimeMillis()/1000));

        return payUserMapper.updateByPrimaryKeySelective(payUser);

    }

    @Override
    public PageResult<PayUserVO> queryList(@RequestParam("search") @NotNull Search search,
                                     @RequestParam(value = "page", defaultValue = "1") Integer page,
                                     @RequestParam(value = "size", defaultValue = "10") @Max(1000) Integer size)
    {

        Example example = new ExampleBuilder(PayUser.class).search(search).sort(Arrays.asList("id_desc")).build();

        Page<PayUser> payUserList = (Page<PayUser>) payUserMapper.selectByExampleAndRowBounds(example, new RowBounds(page, size));

        return new PageResult<>(payUserList.getTotal(), DozerUtils.mapList(payUserList, PayUserVO.class));



    }

    @Override
    public UserInfoVO login(@RequestParam(value = "userName") String userName,
                            @RequestParam(value = "password") String password,
                            HttpServletResponse resp)
    {

        Search search = new Search();
        search.put("username_eq", userName);
        Example example = new ExampleBuilder(PayUser.class).search(search).build();

        List<PayUser> payUsers = payUserMapper.selectByExample(example);
        if(payUsers.size() != 1)
        {
            logger.warn("登录失败 用户不存在 userName:{} password:{}, payUsers:{}", userName, password, payUsers);
            CommonUtils.sendError(resp, ErrorResponseEunm.INVALID_USER);
            return null;
        }

        PayUser payUser = payUsers.get(0);
        if(!CommonUtils.getMd5(password).equals(payUser.getPassword()))
        {
            logger.warn("登录失败 密码错误 useName:{}, password:{}", userName, password);
            CommonUtils.sendError(resp, ErrorResponseEunm.INVALID_PASSWORD);
            return null;
        }

        // 登录成功 返回用户信息
        return userService.genUserInfoVOByPayUser(payUser);

    }

    @Override
    public UserInfoVO info(@RequestParam(value = "token") String token)
    {
        Search search = new Search();
        search.put("username_eq", token);
        Example example = new ExampleBuilder(PayUser.class).search(search).build();

        List<PayUser> payUserList = payUserMapper.selectByExample(example);

        return userService.genUserInfoVOByPayUser(payUserList.get(0));

    }

    @Override
    public int reset(@RequestParam(value = "userName") String userName,
                    @RequestParam(value = "passwordOld") String passwordOld,
                    @RequestParam(value = "password") String password,
                    HttpServletResponse resp)
    {

        Search search = new Search();
        search.put("username_eq", userName);
        Example example = new ExampleBuilder(PayUser.class).search(search).build();

        List<PayUser> payUserList = payUserMapper.selectByExample(example);

        if(payUserList.size() != 1)
        {
            CommonUtils.sendError(resp, ErrorResponseEunm.INVALID_USER);
            return 0;
        }

        PayUser payUser = payUserList.get(0);
        if(!CommonUtils.getMd5(passwordOld).equals(payUser.getPassword()))
        {
            CommonUtils.sendError(resp, ErrorResponseEunm.INVALID_PASSWORD);
            return 0;
        }

        // 重置密码
        PayUser record = new PayUser();
        record.setId(payUser.getId());
        record.setPassword(CommonUtils.getMd5(password));
        payUserMapper.updateByPrimaryKeySelective(record);

        return 1;

    }

    @Override
    public List<XqhUserRole> getRoleByUserId(@PathVariable @NotNull @Min(1) Integer userId)
    {
        Search search = new Search();
        search.put("userId", userId);

        Example example = new ExampleBuilder(XqhUserRole.class).search(search).build();

        List<XqhUserRole> userRoleList = userRoleMapper.selectByExample(example);

        return userRoleList;
    }


}
