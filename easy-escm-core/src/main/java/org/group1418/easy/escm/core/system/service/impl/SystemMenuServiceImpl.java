package org.group1418.easy.escm.core.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import lombok.RequiredArgsConstructor;
import org.group1418.easy.escm.common.base.impl.BaseServiceImpl;
import org.group1418.easy.escm.common.exception.EasyEscmException;
import org.group1418.easy.escm.common.saToken.obj.CurrentUser;
import org.group1418.easy.escm.common.cache.RedisCacheService;
import org.group1418.easy.escm.common.utils.DbUtil;
import org.group1418.easy.escm.common.utils.StreamUtil;
import org.group1418.easy.escm.common.wrapper.ButtonNode;
import org.group1418.easy.escm.common.wrapper.MenuTreeNode;
import org.group1418.easy.escm.common.wrapper.Tree;
import org.group1418.easy.escm.core.constant.CacheConstant;
import org.group1418.easy.escm.core.system.entity.SystemMenu;
import org.group1418.easy.escm.core.system.mapper.SystemMenuMapper;
import org.group1418.easy.escm.core.system.pojo.fo.SystemMenuFo;
import org.group1418.easy.escm.core.system.service.ISystemButtonService;
import org.group1418.easy.escm.core.system.service.ISystemMenuService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 菜单
 *
 * @author yq 2020-09-21 15:33:47
 */
@Service
@RequiredArgsConstructor
public class SystemMenuServiceImpl extends BaseServiceImpl<SystemMenuMapper, SystemMenu> implements ISystemMenuService {

    private final ISystemButtonService systemButtonService;
    private final RedisCacheService redisCacheService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(SystemMenuFo fo) {
        commonCheck(fo);
        SystemMenu systemMenu = new SystemMenu();
        BeanUtils.copyProperties(fo, systemMenu);
        baseMapper.insert(systemMenu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, SystemMenuFo fo) {
        if (id.equals(fo.getPid())) {
            throw EasyEscmException.i18n("permission.menu.parent.can.not.be.self");
        }
        commonCheck(fo);
        SystemMenu systemMenu = baseMapper.selectById(id);
        Assert.notNull(systemMenu);
        BeanUtils.copyProperties(fo, systemMenu);
        baseMapper.updateById(systemMenu);
        DbUtil.afterTransactionCommit(() -> redisCacheService.hDel(CacheConstant.Hashs.SYSTEM_MENU, id));
    }

    private void commonCheck(SystemMenuFo fo) {
        //菜单若为iFrame  path必须以http/https开头
        if (fo.getIFrame()) {
            if (!HttpUtil.isHttp(fo.getPath()) && HttpUtil.isHttps(fo.getPath())) {
                throw EasyEscmException.i18n("menu.iframe.must.start.with.http");
            }
        }
        //非根节点 component 不可为空
        if (fo.getPid() != null && StrUtil.isEmpty(fo.getComponent())) {
            throw EasyEscmException.i18n("menu.component.can.not.blank");
        }
    }

    @Override
    public List<MenuTreeNode> userMenuTree(Long rootId) {
        CurrentUser currentUser = new CurrentUser();
        //todo
        return userMenuTree(rootId, currentUser.getId(), false);
    }

    @Override
    public List<MenuTreeNode> userMenuTree(Long rootId, Long userId, Boolean superAdmin) {
        List<String> menuIds;
        List<String> buttonIds;
        //菜单
        List<MenuTreeNode> menuTreeNodeList;
        //按钮
        List<ButtonNode> buttonNodeList;
        //管理员显示所有菜单和按钮
        if (BooleanUtil.isTrue(superAdmin)) {
            menuIds = baseMapper.getAllMenuIds();
            buttonIds = systemButtonService.getAllButtonIds();
        } else {
            menuIds = baseMapper.getUserMenuIds(userId);
            buttonIds = systemButtonService.getUserButtonIds(userId);
        }
        //批量从缓存拿出数据 存在已删除或禁用的菜单
        if (CollectionUtil.isEmpty(menuIds)) {
            return null;
        }
        menuTreeNodeList = CollUtil.defaultIfEmpty(redisCacheService.hmGet(CacheConstant.Hashs.SYSTEM_MENU, menuIds), new ArrayList<>(menuIds.size()));
        //缓存中未包含全部,则查数据库并存入缓存
        if (CollUtil.size(menuIds) > CollUtil.size(menuTreeNodeList)) {
            List<String> noCacheMenuIds = CollUtil.subtractToList(menuIds, menuTreeNodeList.stream().map(n -> n.getId().toString()).collect(Collectors.toList()));
            List<SystemMenu> noCacheSystemMenus = baseMapper.selectBatchIds(noCacheMenuIds);
            if (CollUtil.isNotEmpty(noCacheSystemMenus)) {
                Map<String, Object> noCacheMenuMap = StreamUtil.toMap(noCacheSystemMenus, sm -> sm.getId().toString(), sm -> {
                    MenuTreeNode menuTreeNode = toMenuTreeNode(sm);
                    menuTreeNodeList.add(menuTreeNode);
                    return menuTreeNode;
                });
                redisCacheService.hmSet(CacheConstant.Hashs.SYSTEM_MENU, noCacheMenuMap);
            }
        }
        buttonNodeList = CollUtil.defaultIfEmpty(redisCacheService.hmGet(CacheConstant.Hashs.SYSTEM_BUTTON, buttonIds), new ArrayList<>(buttonIds.size()));
        //缓存中未包含全部,则查数据库并存入缓存
        if (CollUtil.size(buttonIds) > CollUtil.size(buttonNodeList)) {
            List<String> noCacheButtonIds = CollUtil.subtractToList(buttonIds, buttonNodeList.stream().map(n -> n.getId().toString()).collect(Collectors.toList()));
            List<ButtonNode> noCacheButtonNodes = systemButtonService.batchGetButtonNodes(noCacheButtonIds);
            if (CollUtil.isNotEmpty(noCacheButtonNodes)) {
                Map<String, Object> noCacheButtonMap = StreamUtil.toMap(noCacheButtonNodes, sb -> sb.getId().toString(), sb -> {
                    buttonNodeList.add(sb);
                    return sb;
                });
                redisCacheService.hmSet(CacheConstant.Hashs.SYSTEM_BUTTON, noCacheButtonMap);
            }
        }
        //构建树
        List<MenuTreeNode> menuTreeNodes = this.buildTree(menuTreeNodeList, buttonNodeList);
        return menuTreeNodes.stream().filter(node -> node.getId().equals(rootId)).collect(Collectors.toList());
    }

    private MenuTreeNode toMenuTreeNode(SystemMenu systemMenu) {
        MenuTreeNode menuTreeNode = new MenuTreeNode();
        menuTreeNode.setName(systemMenu.getRouterName());
        menuTreeNode.setComponent(systemMenu.getComponent());
        menuTreeNode.setPath(systemMenu.getPath());
        menuTreeNode.setQuery(systemMenu.getQuery());
        menuTreeNode.setHidden(systemMenu.getHidden());
        menuTreeNode.setIFrame(systemMenu.getIFrame());
        menuTreeNode.setSortNo(systemMenu.getSortNo());
        menuTreeNode.setPid(systemMenu.getPid());
        menuTreeNode.setId(systemMenu.getId());
        menuTreeNode.setEnabled(systemMenu.getEnabled());

        MenuTreeNode.MenuMeta meta = new MenuTreeNode.MenuMeta();
        meta.setTitle(systemMenu.getTitle());
        meta.setIcon(systemMenu.getIcon());
        meta.setCached(systemMenu.getCached());
        meta.setAffix(systemMenu.getAffix());
        meta.setBreadCrumb(systemMenu.getBreadCrumb());
        meta.setActiveMenu(systemMenu.getActiveMenu());
        menuTreeNode.setMeta(meta);
        return menuTreeNode;
    }

    private List<MenuTreeNode> buildTree(List<MenuTreeNode> menuTreeNodeList, List<ButtonNode> buttonNodeList) {
        Map<Long, List<ButtonNode>> buttonMap = null;
        if (CollectionUtil.isNotEmpty(buttonNodeList)) {
            buttonMap = buttonNodeList.stream().filter(n -> n != null && n.getEnabled()).collect(Collectors.groupingBy(ButtonNode::getPid));
        }
        //构建树
        Map<Long, List<ButtonNode>> finalButtonMap = buttonMap;
        return new Tree<Long, MenuTreeNode>(-1L).build(menuTreeNodeList, node -> {
            //将按钮挂载到菜单下
            MenuTreeNode.MenuMeta menuMeta = node.getMeta();
            if (menuMeta != null) {
                List<ButtonNode> buttons = CollectionUtil.isNotEmpty(finalButtonMap) ? finalButtonMap.get(node.getId()) : ListUtil.empty();
                if (CollectionUtil.isNotEmpty(buttons)) {
                    menuMeta.setButtons(buttons.stream().collect(Collectors.groupingBy(ButtonNode::getPosition)));
                }
            }
        });
    }

    @Override
    public List<MenuTreeNode> wholeTree() {
        List<MenuTreeNode> nodes = baseMapper.getAllNodes();
        return new Tree<Long, MenuTreeNode>(-1L).build(nodes);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<MenuTreeNode> nodes) {
        if (CollectionUtil.isNotEmpty(nodes)) {
            Map<Boolean, List<MenuTreeNode>> map = nodes.stream().collect(Collectors.groupingBy(MenuTreeNode::getBeButton));
            //菜单
            List<MenuTreeNode> menus = map.get(false);
            List<Long> menuIds = new ArrayList<>();
            List<Long> buttonIds = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(menus)) {
                for (MenuTreeNode node : menus) {
                    menuIds.add(node.getId());
                }
                baseMapper.deleteBatchIds(menuIds);
            }
            //按钮
            List<MenuTreeNode> buttons = map.get(true);
            if (CollectionUtil.isNotEmpty(buttons)) {
                for (MenuTreeNode node : buttons) {
                    buttonIds.add(node.getId());
                }
                systemButtonService.removeByIds(buttonIds);
            }
            DbUtil.afterTransactionCommit(() -> {
                if (CollUtil.isNotEmpty(menuIds)) {
                    redisCacheService.hDel(CacheConstant.Hashs.SYSTEM_MENU, menuIds);
                }
                if (CollUtil.isNotEmpty(buttonIds)) {
                    redisCacheService.hDel(CacheConstant.Hashs.SYSTEM_BUTTON, buttonIds);
                }
            });
        }
    }

}
