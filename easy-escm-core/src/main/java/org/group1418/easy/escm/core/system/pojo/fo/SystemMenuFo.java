package org.group1418.easy.escm.core.system.pojo.fo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.group1418.easy.escm.common.validator.annotation.IntCheck;
import org.group1418.easy.escm.common.validator.annotation.StrCheck;

import java.io.Serializable;

/**
 * @author yq
 * @date 2020-09-21 15:33:47
 * @description 菜单 Fo
 * @since V1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SystemMenuFo implements Serializable {
    private static final long serialVersionUID = -1918237882723347378L;

    private Long pid;

    @StrCheck(name = "菜单标题")
    private String title;

    /**
     * 路由名称 一定要填写不然使用<keep-alive>时会出现各种问题 组件内Name
     */
    @StrCheck(name = "菜单路由")
    private String routerName;

    private String component;

    /**路由地址*/
    private String path;
    /**
     * 路由参数
     */
    private String query;
    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 是否外链菜单
     */
    private Boolean iFrame;
    /**
     * 是否被 <keep-alive> 缓存
     */
    private Boolean cached;
    /**
     * 是否固定在 tag-view中
     */
    private Boolean affix;
    /**
     * 是否隐藏
     */
    private Boolean hidden;

    /**
     * 是否在面包屑中显示
     */
    private Boolean breadCrumb;

    /**
     * 当路由设置了该属性，则会高亮相对应的侧边栏
     * 这在某些场景非常有用，比如：一个文章的列表页路由为：/article/list
     * 点击文章进入文章详情页，这时候路由为/article/1，但你想在侧边栏高亮文章列表的路由，就可以进行如下设置
     */
    private String activeMenu;
    /**
     * 权限标识
     */
    private String permission;

    @IntCheck(name = "菜单排序号")
    private Integer sortNo;


}
