package org.group1418.easy.escm.core.system.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.alibaba.fastjson.JSON;
import org.group1418.easy.escm.common.annotation.ApiEncrypt;
import org.group1418.easy.escm.common.utils.ValidateUtils;
import org.group1418.easy.escm.common.wrapper.R;
import org.group1418.easy.escm.core.system.pojo.fo.LoginFo;
import org.group1418.easy.escm.core.system.pojo.vo.LoginVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yq 2024/3/29 15:29
 * @description AuthController
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("login")
    @SaIgnore
//    @ApiEncrypt
    public R<LoginVo> login(@RequestBody String body) {
        return R.ok();
    }
}
