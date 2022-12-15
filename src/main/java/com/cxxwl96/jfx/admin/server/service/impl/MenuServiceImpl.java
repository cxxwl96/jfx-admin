/*
 * Copyright (c) 2021-2022, jad (cxxwl96@sina.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cxxwl96.jfx.admin.server.service.impl;

import com.cxxwl96.jfx.admin.server.base.service.impl.TreeServiceImpl;
import com.cxxwl96.jfx.admin.server.entity.Menu;
import com.cxxwl96.jfx.admin.server.entity.RoleMenu;
import com.cxxwl96.jfx.admin.server.entity.Tree;
import com.cxxwl96.jfx.admin.server.lang.Result;
import com.cxxwl96.jfx.admin.server.mapper.MenuMapper;
import com.cxxwl96.jfx.admin.server.service.MenuService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * MenuServiceImpl
 *
 * @author cxxwl96
 * @since 2021-06-18
 */
@Slf4j
@Service
public class MenuServiceImpl extends TreeServiceImpl<MenuMapper, Menu> implements MenuService {

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private RoleServiceImpl roleService;

    @Autowired
    private UserRoleServiceImpl userRoleService;

    @Autowired
    private RoleMenuServiceImpl roleMenuService;

    /**
     * 保存菜单
     *
     * @param menu 菜单
     * @return 菜单
     */
    @Override
    public Result<Menu> saveMenu(@NotNull Menu menu) {
        if (StrUtil.isNotBlank(menu.getPermissions())) {
            final boolean exists = super.lambdaQuery()
                .ne(Menu::getId, menu.getId())
                .eq(Menu::getPermissions, menu.getPermissions())
                .exists();
            if (exists) {
                return Result.failed("权限标识" + menu.getPermissions() + "已存在");
            }
        }
        if (StrUtil.isNotBlank(menu.getCode())) {
            final boolean exists = super.lambdaQuery()
                .ne(Menu::getId, menu.getId())
                .eq(Menu::getCode, menu.getCode())
                .exists();
            if (exists) {
                return Result.failed("菜单编码" + menu.getPermissions() + "已存在");
            }
        }
        log.info("Save menu: " + menu.getTitle());
        if (!super.saveOrUpdate(menu)) {
            log.error("Save menu error");
            return Result.failed("保存失败");
        }
        // 清除用户授权信息
        authService.clearUserAuthorityByMenuId(menu.getId());
        // 清空用户菜单缓存
        authService.clearUserMenuData();
        return Result.success("保存成功", menu);
    }

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     * @param includeSelf 是否包含自己
     * @return 是否删除成功
     */
    @Override
    public Result<Boolean> deleteMenu(String id, boolean includeSelf) {
        List<Menu> list;
        final Result<List<Menu>> menuTreeResult = authService.getUserMenuTree();
        if (!menuTreeResult.isSuccess()) {
            return Result.failed(menuTreeResult.getMsg());
        }
        final Tree<Menu> tree = new Tree<>(menuTreeResult.getData());
        if (includeSelf) {
            list = tree.getSubList(id);
        } else {
            list = tree.getChildrenList(id);
        }
        boolean success = false;
        if (CollUtil.isNotEmpty(list)) {
            final List<String> ids = list.stream().map(Menu::getId).collect(Collectors.toList());
            success = super.removeByIds(ids);
            // 清除用户授权信息
            for (String menuId : ids) {
                authService.clearUserAuthorityByMenuId(menuId);
            }
            // 清空用户菜单缓存
            authService.clearUserMenuData();
            // 删除用户菜单
            final List<RoleMenu> roleMenuIds = roleMenuService.lambdaQuery()
                .in(RoleMenu::getMenuId, ids)
                .select(RoleMenu::getId)
                .list();
            roleMenuService.removeByIds(roleMenuIds);
        }
        return success ? Result.success("删除成功") : Result.failed("删除失败");
    }

    /**
     * 通过编码获取菜单，菜单不存在或存在多个菜单则获取失败
     *
     * @param code 菜单编码
     * @return 菜单
     */
    @Override
    public Result<Menu> getMenuByCode(String code) {
        final List<Menu> menus = super.lambdaQuery().eq(Menu::getCode, code).list();
        if (menus.size() == 0) {
            return Result.failed("菜单不存在");
        }
        if (menus.size() > 1) {
            return Result.failed("存在多个菜单");
        }
        return Result.success(menus.get(0));
    }
}
