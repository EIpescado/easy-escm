package org.group1418.easy.escm.core.system.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import org.group1418.easy.escm.common.wrapper.R;
import org.group1418.easy.escm.core.system.pojo.vo.LoginVo;
import org.group1418.easy.escm.core.system.service.IAuthService;
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
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @PostMapping("login")
    @SaIgnore
//    @ApiEncrypt
    public R<LoginVo> login(@RequestBody String body) {
        return R.ok(authService.login(body));
    }
}
