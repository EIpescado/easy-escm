package org.group1418.easy.escm.core;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson2.JSONObject;
import org.group1418.easy.escm.common.annotation.ApiEncrypt;
import org.group1418.easy.escm.common.cache.RedisCacheService;
import org.group1418.easy.escm.common.config.properties.EasyEscmProp;
import org.group1418.easy.escm.common.saToken.UserHelper;
import org.group1418.easy.escm.common.utils.ExcelUtil;
import org.group1418.easy.escm.common.wrapper.R;
import org.group1418.easy.escm.core.system.entity.SystemClient;
import org.group1418.easy.escm.core.system.pojo.fo.LoginFo;
import org.group1418.easy.escm.core.system.service.ISystemClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yq 2024/1/13 12:08
 * @description TestController
 */
@RestController
public class TestController {

    @Autowired
    private EasyEscmProp easyEscmProp;
    @Autowired
    private TestService testService;
    @Autowired
    private ISystemClientService systemClientService;
    @Autowired
    private RedisCacheService redisCacheService;

    @GetMapping("test")
    @SaIgnore
    public R<String> test(String name, String pwd,String device) {
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
        return SaResult.data(UserHelper.currentUser());
    }

    // 测试注销  ---- http://localhost:8081/acc/logout
    @GetMapping("logout")
    public SaResult logout() {
        StpUtil.logout();
        return SaResult.ok();
    }

    @GetMapping("testAsync")
    public R<String> testAsync() {
        redisCacheService.batchDel("a");
        return R.ok();
    }

    @PostMapping("testEncrypt")
    @SaIgnore
    @ApiEncrypt(response = false)
    public R<JSONObject> testEncrypt(@RequestBody String body) {
        System.out.println(body);
        return R.ok(new JSONObject().fluentPut("a",1).fluentPut("body",body));
    }

    @PostMapping("jsonTest")
    @SaIgnore
    public R<LoginFo> jsonTest(@RequestBody @Validated  LoginFo fo, @RequestParam Integer abc) {
        return R.ok(fo);
    }

    @PostMapping("upload")
    @SaIgnore
    public R<String> minUpload(@RequestParam("file") MultipartFile file) {
        return R.ok();
    }

    @GetMapping("exportTest")
    @SaIgnore
    public void exportTest(HttpServletResponse response) {
        ExcelUtil.exportXlsx(systemClientService.list(), SystemClient.class,"test.xlsx",null, response);
    }

    @PostMapping("importTest")
    @SaIgnore
    public R<List<SystemClient>> importTest(@RequestParam("file") MultipartFile file) {
        List<SystemClient> exoList = new ArrayList<>();
        ExcelUtil.importFile(file.getOriginalFilename(), 1, file::getInputStream, SystemClient.class, (data, context, noText) -> {
            exoList.add(data);
        });
        return R.ok(exoList);
    }

}
