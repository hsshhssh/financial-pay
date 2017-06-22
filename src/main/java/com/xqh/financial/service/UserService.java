package com.xqh.financial.service;

import com.google.common.collect.Lists;
import com.xqh.financial.entity.PayUser;
import com.xqh.financial.entity.XqhUserRole;
import com.xqh.financial.entity.vo.UserInfoVO;
import com.xqh.financial.mapper.PayUserMapper;
import com.xqh.financial.mapper.XqhUserRoleMapper;
import com.xqh.financial.utils.ExampleBuilder;
import com.xqh.financial.utils.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created by hssh on 2017/5/4.
 */
@Service
public class UserService {

    @Autowired
    PayUserMapper payUserMapper;

    @Autowired
    XqhUserRoleMapper xqhUserRoleMapper;

    public int insertSingle(PayUser payUser)
    {
        int nowTime = (int) (System.currentTimeMillis()/1000);
        payUser.setCreateTime(nowTime);
        payUser.setUpdateTime(nowTime);
        payUserMapper.insertSelective(payUser);
        return payUser.getId();
    }

    /**
     * 获得用户信息对象
     * @param payUser
     * @return
     */
    public UserInfoVO genUserInfoVOByPayUser(PayUser payUser)
    {
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setAvatar("https://wdl.wallstreetcn.com/48a3e1e0-ea2c-4a4e-9928-247645e3428b");
        userInfoVO.setIntroduction(payUser.getName());
        userInfoVO.setName(payUser.getName());

        // 获取角色
        Search search = new Search();
        search.put("userId_eq", payUser.getId());
        Example example = new ExampleBuilder(XqhUserRole.class).search(search).build();

        List<XqhUserRole> xqhUserRoleList = xqhUserRoleMapper.selectByExample(example);

        List<String> roleList = Lists.newArrayList();
        for (XqhUserRole xqhUserRole : xqhUserRoleList)
        {
            roleList.add(xqhUserRole.getRoleName());
        }

        userInfoVO.setRole(roleList);


        userInfoVO.setToken(payUser.getUsername());
        userInfoVO.setUid(payUser.getId());

        return userInfoVO;

    }

}
