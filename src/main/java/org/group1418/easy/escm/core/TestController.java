package org.group1418.easy.escm.core;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.group1418.easy.escm.common.config.properties.CustomConfigProperties;
import org.group1418.easy.escm.common.wrapper.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yq 2024/1/13 12:08
 * @description TestController
 */
@RestController
public class TestController {

    @Autowired
    private CustomConfigProperties customConfigProperties;
    @Autowired
    private TestService testService;

    @GetMapping("test")
    @SaIgnore
    public R<Object> test(String name, String pwd,String device) {
        // 第一步：比对前端提交的账号名称、密码
        if ("zhang".equals(name) && "123456".equals(pwd)) {
            // 第二步：根据账号id，进行登录
            StpUtil.login(10001, new SaLoginModel().setDevice(device));
            StpUtil.getSession().set("test","1");
            StpUtil.getTokenSession().set("abc",2);
            return R.ok();
        }
        return R.fail("登录失败");
    }

    // 查询登录状态  ---- http://localhost:8081/acc/isLogin
    @GetMapping("isLogin")
    public SaResult isLogin() {
        return SaResult.ok("是否登录：" + StpUtil.isLogin());
    }

    // 查询 Token 信息  ---- http://localhost:8081/acc/tokenInfo
    @GetMapping("tokenInfo")
    public SaResult tokenInfo() {
        return SaResult.data(StpUtil.getTokenInfo());
    }

    // 测试注销  ---- http://localhost:8081/acc/logout
    @GetMapping("logout")
    public SaResult logout() {
        StpUtil.logout();
        return SaResult.ok();
    }

    @GetMapping("testAsync")
//    @SaCheckRole("super-admin")
//    @SaCheckPermission(value = "super-admin")
    public R<Object> testAsync() {
        testService.testAsync();
        return R.ok();
    }
}
