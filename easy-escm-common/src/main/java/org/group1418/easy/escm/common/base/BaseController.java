package org.group1418.easy.escm.common.base;

import cn.hutool.core.util.StrUtil;

/**
 * 基础BaseController
 * @author yq on 2021年4月14日 10:41:44
 */
public class BaseController {

    public String redirect(String url) {
        return StrUtil.format("redirect:{}", url);
    }
}
