package org.group1418.easy.escm.core.system.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.group1418.easy.escm.common.base.obj.TenantEntity;

/**
 * 菜单
 *
 * @author yq 2020年9月21日 15:28:17
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SystemMenu extends TenantEntity {
    private static final long serialVersionUID = 5786814160655628952L;

    /**
     * 上级菜单
     */
    private Long pid;
    /**
     * 菜单名称
     */
    private String title;
    /**
     * 路由名称 设定路由的名字，一定要填写不然使用<keep-alive>时会出现各种问题
     */
    private String routerName;
    /**
     * 组件地址
     */
    private String component;
    /**
     * 路由地址
     */
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
     * 是否外链菜单 若为外链,则path必须为 http://或 https:// 开头
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
     * 是否隐藏 即不会在侧边栏出现
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

    /**
     * 排序
     */
    private Integer sortNo;

}
