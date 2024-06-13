package org.group1418.easy.escm.core.system.controller;

import lombok.RequiredArgsConstructor;
import org.group1418.easy.escm.common.base.BaseController;
import org.group1418.easy.escm.common.wrapper.PageR;
import org.group1418.easy.escm.common.wrapper.R;
import org.group1418.easy.escm.core.system.pojo.fo.SystemTenantFo;
import org.group1418.easy.escm.core.system.pojo.qo.SystemTenantQo;
import org.group1418.easy.escm.core.system.pojo.to.SystemTenantTo;
import org.group1418.easy.escm.core.system.pojo.vo.SystemTenantVo;
import org.group1418.easy.escm.core.system.service.ISystemTenantService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * 租户 controller
 * @author yq 2024-06-07
 */
@RequestMapping("/tenant")
@RequiredArgsConstructor
@RestController
public class SystemTenantController extends BaseController {

    private final ISystemTenantService systemTenantService;

    @PostMapping
    public R<String> create(@RequestBody SystemTenantFo fo){
        systemTenantService.create(fo);
        return R.ok();
    }

    @PostMapping("{id}")
    public R<String> update(@PathVariable(name = "id")Long id, @RequestBody SystemTenantFo fo){
        systemTenantService.update(id,fo);
        return R.ok();
    }

    @GetMapping("{id}")
    public R<SystemTenantVo> get(@PathVariable(name = "id")Long id){
        return R.ok(systemTenantService.get(id));
    }

    @PostMapping("/delete/{id}")
    public R<String> delete(@PathVariable(name = "id")Long id){
        systemTenantService.delete(id);
        return R.ok();
    }

    @PostMapping("/search")
    public R<PageR<SystemTenantTo>> search(@RequestBody SystemTenantQo qo){
        return R.ok(systemTenantService.search(qo));
    }
}