package com.xqh.financial.controller.api;

import com.xqh.financial.entity.PayApp;
import com.xqh.financial.entity.dto.PayAppCreateDTO;
import com.xqh.financial.entity.dto.PayAppUpdateDTO;
import com.xqh.financial.utils.PageResult;
import com.xqh.financial.utils.Search;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by hssh on 2017/5/26.
 */
@RequestMapping("/xqh/financial/pay/app")
public interface IPayAppController
{

    @PostMapping("listNoPage")
    public List<PayApp> listNoPage(@RequestParam(value = "userId", required = false) Integer userId);

    @PostMapping("/list")
    public PageResult list(@RequestParam("search") @Valid @NotNull Search search,
                           @RequestParam(value = "page", defaultValue = "1")  int page,
                           @RequestParam(value = "size", defaultValue = "10") int size);

    @PutMapping
    public int create(@RequestBody @Valid @NotNull PayAppCreateDTO dto);


    @PostMapping
    public int update(@RequestBody @Valid @NotNull PayAppUpdateDTO dto);
}
