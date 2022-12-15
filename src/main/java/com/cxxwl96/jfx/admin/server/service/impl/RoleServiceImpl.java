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

import com.cxxwl96.jfx.admin.server.base.service.impl.BaseServiceImpl;
import com.cxxwl96.jfx.admin.server.entity.Role;
import com.cxxwl96.jfx.admin.server.entity.RoleMenu;
import com.cxxwl96.jfx.admin.server.function.PropertyFunc;
import com.cxxwl96.jfx.admin.server.lang.Result;
import com.cxxwl96.jfx.admin.server.mapper.RoleMapper;
import com.cxxwl96.jfx.admin.server.service.RoleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import cn.hutool.core.collection.CollUtil;

/**
 * 角色服务实现类
 *
 * @author cxxwl96
 * @since 2021/8/22 14:34
 */
@Service
public class RoleServiceImpl extends BaseServiceImpl<RoleMapper, Role> implements RoleService {
    @Autowired
    private RoleMenuServiceImpl roleMenuService;

    @Autowired
    private AuthServiceImpl authService;

    /**
     * 保存角色
     *
     * @param entity 角色
     * @return 角色
     */
    @Override
    public Result<Role> saveOrUpdateRole(Role entity) {
        if (entity == null) {
            return Result.failed("保存角色为null");
        }
        // 鉴权当前用户是否能操作角色
        if (nonAuthOperate(CollUtil.newArrayList(entity))) {
            return Result.failed("角色权限不足，保存的角色等级高于或等于自己最高等级的角色");
        }
        if (!super.saveOrUpdate(entity)) {
            return Result.failed("保存失败");
        }
        return Result.success("保存成功");
    }

    /**
     * 删除角色
     *
     * @param ids 角色id
     * @return 是否删除成功
     */
    @Override
    @Transactional
    public Result<Boolean> deleteRoles(Collection<String> ids) {
        // 鉴权当前用户是否能操作角色
        final List<Role> roles = super.listByIds(ids);
        if (nonAuthOperate(roles)) {
            return Result.failed("用户权限不足，删除的角色中存在角色等级高于或等于自己最高等级的角色");
        }
        // 删除角色权限
        ids.forEach(this::deleteRoleMenus);
        // 删除角色
        if (!super.removeByIds(ids)) {
            return Result.failed("删除失败");
        }
        return Result.success("删除成功");
    }

    /**
     * 鉴权当前用户是否能操作角色，角色低等级用户不能操作高等级角色
     *
     * @param roles 角色ID
     * @return true: 没有权限操作角色, false: 有权限操作角色
     */
    private boolean nonAuthOperate(Collection<Role> roles) {
        if (CollUtil.isEmpty(roles)) {
            return false;
        }
        // 超级管理员不拦截
        final Result<Boolean> authResult = authService.hasAdministrator();
        if (authResult.isSuccess() && authResult.getData()) {
            return false;
        }
        // 获取当前用户角色最高的角色等级
        AtomicInteger maxLevel = new AtomicInteger();
        authService.getCurrentAuthUserRoles()
            .successAndThen(result -> result.getData()
                .stream()
                .max(Comparator.comparing(Role::getLevel))
                .ifPresent(role -> maxLevel.set(role.getLevel())));
        // 角色中等级是否包含大于等于当前角色等级
        return roles.stream().anyMatch(role -> role.getLevel() >= maxLevel.get());
    }

    /**
     * 获取角色
     *
     * @param id 角色id
     * @return 角色
     */
    @Override
    public Result<Role> getRole(String id) {
        Role role = super.getById(id);
        if (role == null) {
            return Result.failed("找不到该角色");
        }
        return Result.success(role);
    }

    /**
     * 获取角色的菜单权限（获取角色权限）
     *
     * @param roleId 角色ID
     * @return 菜单权限ID集合
     */
    @Override
    public Result<List<String>> getRoleMenus(String roleId) {
        final List<String> menuIds = roleMenuService.lambdaQuery()
            .eq(RoleMenu::getRoleId, roleId)
            .list()
            .stream()
            .map(RoleMenu::getMenuId)
            .distinct()
            .collect(Collectors.toList());
        return Result.success(menuIds);
    }

    /**
     * 设置角色的菜单权限（分配权限）
     *
     * @param roleId 角色ID
     * @param menuIds 菜单权限ID集合
     * @return 是否设置成功
     */
    @Override
    @Transactional
    public Result<Boolean> setRoleMenus(@NotNull String roleId, @NotNull List<String> menuIds) {
        // 先全部删除
        deleteRoleMenus(roleId);
        // 再添加
        final List<RoleMenu> roleMenus = new ArrayList<>();
        menuIds.forEach(menuId -> {
            final RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenus.add(roleMenu);
        });
        if (roleMenus.size() > 0 && !roleMenuService.saveBatch(roleMenus)) {
            log.error("save batch role menu fail");
            return Result.failed("分配权限失败");
        }
        authService.clearUserAuthorityByRoleId(roleId);
        return Result.success("保存成功");
    }

    /**
     * 删除角色权限
     *
     * @param roleId 角色ID
     */
    private void deleteRoleMenus(String roleId) {
        long count = roleMenuService.lambdaQuery().eq(RoleMenu::getRoleId, roleId).count();
        if (count > 0) {
            final Map<String, Object> columnMap = new HashMap<>();
            PropertyFunc<RoleMenu, ?> column = RoleMenu::getRoleId;
            columnMap.put(column.getColumnName(), roleId);
            if (!roleMenuService.removeByMap(columnMap)) {
                throw new RuntimeException("delete role menu fail");
            }
        }
    }
}
