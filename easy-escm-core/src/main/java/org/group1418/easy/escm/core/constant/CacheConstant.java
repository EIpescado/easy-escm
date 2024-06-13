package org.group1418.easy.escm.core.constant;

/**
 * redis缓存名称常量
 * @author yq 2024年3月8日 14:09:02
 */
public interface CacheConstant {


    interface Strings {
    }

    interface Hashs {
        /**
         * 系统客户端
         */
        String SYSTEM_CLIENT = "system_client";
        /**
         * 系统菜单
         */
        String SYSTEM_MENU = "system_menu";

        /**
         * 系统按钮
         */
        String SYSTEM_BUTTON = "system_button";

        /**
         * 租户
         */
        String SYSTEM_TENANT = "system_tenant";
    }

    interface Lists {
    }

}
