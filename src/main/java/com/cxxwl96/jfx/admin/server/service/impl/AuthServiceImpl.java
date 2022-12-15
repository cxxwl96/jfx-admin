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

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.cxxwl96.jfx.admin.server.entity.Menu;
import com.cxxwl96.jfx.admin.server.entity.Role;
import com.cxxwl96.jfx.admin.server.entity.RoleMenu;
import com.cxxwl96.jfx.admin.server.entity.Tree;
import com.cxxwl96.jfx.admin.server.entity.User;
import com.cxxwl96.jfx.admin.server.entity.UserRole;
import com.cxxwl96.jfx.admin.server.enums.RoleType;
import com.cxxwl96.jfx.admin.server.enums.Status;
import com.cxxwl96.jfx.admin.server.lang.Result;
import com.cxxwl96.jfx.admin.server.service.AuthService;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * AuthServiceImpl
 *
 * @author cxxwl96
 * @since 2022/9/16 22:44
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    // 登录的用户的key
    private static final String LOGIN_USER = "LoginUser";

    // 登录的用户的角色的key
    private static final String LOGIN_USER_ROLES = "LoginUserRoles";

    // 授权用户的授权信息的key
    private static final String SECURITY_USER_GRANTED_AUTHORITY = "SecurityUserGrantedAuthority";

    // 授权用户菜单列表的key
    private static final String SECURITY_USER_MENU_LIST = "SecurityUserMenuList";

    // 授权用户菜单树的key
    private static final String SECURITY_USER_MENU_TREE = "SecurityUserMenuTree";

    @Autowired
    private UserSecurityImpl userSecurity;

    @Lazy
    @Autowired
    private UserServiceImpl userService;

    @Lazy
    @Autowired
    private UserRoleServiceImpl userRoleService;

    @Lazy
    @Autowired
    private RoleServiceImpl roleService;

    @Lazy
    @Autowired
    private RoleMenuServiceImpl roleMenuService;

    @Lazy
    @Autowired
    private MenuServiceImpl menuService;

    /**
     * 登录
     *
     * @param username username
     * @param password password
     * @return result
     */
    @Override
    public Result<User> login(@NotBlank String username, @NotBlank String password) {
        final List<User> users = userService.lambdaQuery().eq(User::getUsername, username).list();
        if (users.size() > 1) {
            log.error("The number of queried users is greater than 1");
            return Result.failed("The number of queried users is greater than 1");
        }
        if (users.size() == 0 || !userSecurity.matchesPassword(username, password, users.get(0).getPassword())) {
            return Result.failed("账户或密码错误");
        }
        // 更新用户登录时间
        final User user = users.get(0);
        user.setLastLogin(LocalDateTime.now());
        final LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, user.getId()).set(User::getLastLogin, user.getLastLogin());
        userService.update(null, wrapper);
        // 登录成功将用户信息提交给spring容器管理
        SpringUtil.unregisterBean(LOGIN_USER);
        SpringUtil.registerBean(LOGIN_USER, users.get(0));
        return Result.success("登录成功", users.get(0));
    }

    /**
     * 退出登录
     *
     * @return result
     */
    @Override
    public Result<?> logout() {
        getCurrentAuthUser().successAndThen(result -> {
            final User authUser = result.getData();
            clearUserAuthorityByUserId(authUser.getId());
        });
        SpringUtil.unregisterBean(LOGIN_USER);
        return Result.success("退出成功");
    }

    /**
     * 获取已授权用户
     *
     * @return result
     */
    @Override
    public Result<User> getCurrentAuthUser() {
        try {
            final User user = SpringUtil.getBean(LOGIN_USER, User.class);
            user.setPassword(null);
            return Result.success(user);
        } catch (NoSuchBeanDefinitionException exception) {
            log.error("The user is not authenticated, " + exception.getMessage(), exception);
            return Result.failed("用户未登录认证");
        }
    }

    /**
     * 获取已授权用户的角色
     *
     * @return 已授权用户的角色
     */
    @Override
    public Result<List<Role>> getCurrentAuthUserRoles() {
        // 获取当前登录的用户
        final Result<User> userResult = getCurrentAuthUser();
        if (!userResult.isSuccess()) {
            return Result.failed(userResult.getMsg());
        }
        final User user = userResult.getData();
        try {
            // 获取当前用户的角色
            List<Role> roles = SpringUtil.getBean(LOGIN_USER_ROLES + "_" + user.getId());
            return Result.success(roles);
        } catch (NoSuchBeanDefinitionException exception) {
            // 获取已授权用户的角色
            final Result<List<Role>> roleResult = userService.getUserRoles(user.getId());
            if (!roleResult.isSuccess()) {
                return Result.failed(roleResult.getMsg());
            } // 将authorities注入spring
            SpringUtil.registerBean(LOGIN_USER_ROLES + "_" + user.getId(), roleResult.getData());
            return Result.success(roleResult.getData());
        }
    }

    /**
     * 当前已授权用户是否拥有超级管理员角色
     *
     * @return 否拥有超级管理员角色
     */
    @Override
    public Result<Boolean> hasAdministrator() {
        final Result<List<Role>> result = getCurrentAuthUserRoles();
        if (!result.isSuccess()) {
            return Result.failed(result.getMsg());
        }
        final List<Role> roles = result.getData();
        final boolean exist = roles.stream().anyMatch(role -> role.getType() == RoleType.ADMINISTRATOR);
        return Result.success(exist);
    }

    /**
     * 获取用户权限列表
     *
     * @param userId 用户id
     * @return 用户权限列表 <permission, menu>
     */
    @Override
    public Map<String, Menu> getUserAuthority(String userId) {
        try {
            // 从spring容器获取用户授权信息
            return SpringUtil.getBean(SECURITY_USER_GRANTED_AUTHORITY + "_" + userId);
        } catch (NoSuchBeanDefinitionException exception) {
            // 获取用户菜单
            final Result<List<Menu>> result = getUserMenuList(userId);
            if (!result.isSuccess()) {
                log.warn(result.getMsg());
                return CollUtil.empty(String.class);
            }
            final List<Menu> menus = result.getData();
            final Map<String, Menu> permissionMap = new HashMap<>();
            menus.forEach(menu -> permissionMap.put(menu.getPermissions(), menu));
            // 将authorities注入spring
            SpringUtil.registerBean(SECURITY_USER_GRANTED_AUTHORITY + "_" + userId, permissionMap);
            return permissionMap;
        }
    }

    /**
     * 获取用户菜单权限列表
     *
     * @param userId 用户ID
     * @return 用户菜单权限
     */
    @Override
    public Result<List<Menu>> getUserMenuList(String userId) {
        List<Menu> menus = new ArrayList<>();
        // 获取roleId
        final List<String> roleIds = userRoleService.lambdaQuery()
            .select(UserRole::getRoleId)
            .eq(UserRole::getUserId, userId)
            .list()
            .stream()
            .map(UserRole::getRoleId)
            .collect(Collectors.toList());
        if (roleIds.size() == 0) {
            return Result.success(menus);
        }
        // role是否拥有超级管理员,拥有则直接返回所有菜单
        final boolean exists = roleService.lambdaQuery()
            .in(Role::getId, roleIds)
            .eq(Role::getType, RoleType.ADMINISTRATOR)
            .eq(Role::getStatus, Status.ENABLE)
            .exists();
        if (exists) {
            menus = menuService.lambdaQuery().orderByAsc(Menu::getCreateTime).list();
            return Result.success(menus);
        }
        // 获取menuId
        final List<String> menuIds = roleMenuService.lambdaQuery()
            .select(RoleMenu::getMenuId)
            .in(RoleMenu::getRoleId, roleIds)
            .list()
            .stream()
            .map(RoleMenu::getMenuId)
            .collect(Collectors.toList());
        if (menuIds.size() == 0) {
            return Result.success(menus);
        }
        // 获取menu
        menus = menuService.lambdaQuery()
            .in(Menu::getId, menuIds)
            .eq(Menu::getStatus, Status.ENABLE)
            .orderByAsc(Menu::getCreateTime)
            .list();
        return Result.success(menus);
    }

    /**
     * 获取当前登录用户菜单权限列表
     *
     * @return 用户菜单权限
     */
    @Override
    public Result<List<Menu>> getUserMenuList() {
        // 获取当前认证用户
        final Result<User> authoredUser = this.getCurrentAuthUser();
        if (!authoredUser.isSuccess()) {
            return Result.failed(authoredUser.getCode(), authoredUser.getMsg(), null);
        }
        List<Menu> menus;
        try {
            // 从spring容器获取UserMenuList
            menus = SpringUtil.getBean(SECURITY_USER_MENU_LIST);
        } catch (NoSuchBeanDefinitionException exception) {
            // 获取当前登录用户菜单权限列表
            final User authUser = authoredUser.getData();
            final Result<List<Menu>> userMenuList = getUserMenuList(authUser.getId());
            if (userMenuList.isSuccess()) {
                menus = userMenuList.getData();
                // 将UserMenuList注入spring
                SpringUtil.registerBean(SECURITY_USER_MENU_LIST, menus);
            } else {
                return Result.failed("获取用户菜单失败");
            }
        }
        return Result.success(menus);
    }

    /**
     * 获取当前登录用户菜单权限树
     *
     * @return 用户菜单权限
     */
    @Override
    public Result<List<Menu>> getUserMenuTree() {
        List<Menu> menuTree;
        try {
            // 从spring容器获取UserMenuTree
            menuTree = SpringUtil.getBean(SECURITY_USER_MENU_TREE);
        } catch (NoSuchBeanDefinitionException exception) {
            // 获取当前登录用户菜单权限列表
            final Result<List<Menu>> result = getUserMenuList();
            if (result.isSuccess()) {
                // 生成菜单树
                final Tree<Menu> tree = new Tree<>(result.getData(), null);
                menuTree = tree.getRootTree();
                // 将UserMenuTree注入spring
                SpringUtil.registerBean(SECURITY_USER_MENU_TREE, menuTree);
            } else {
                return result;
            }
        }
        return Result.success(menuTree);
    }

    /**
     * 清除用户授权信息
     *
     * @param userId 用户ID
     */
    @Override
    public Result<?> clearUserAuthorityByUserId(String userId) {
        // 清除授权用户信息
        SpringUtil.unregisterBean(SECURITY_USER_GRANTED_AUTHORITY + "_" + userId);
        // 清除授权用户角色
        SpringUtil.unregisterBean(LOGIN_USER_ROLES + "_" + userId);
        // 清除用户菜单
        this.clearUserMenuData();
        return Result.success();
    }

    /**
     * 清除用户授权信息
     *
     * @param roleId 角色ID
     */
    @Override
    public Result<?> clearUserAuthorityByRoleId(String roleId) {
        // 获取userId
        final List<String> userIds = userRoleService.lambdaQuery()
            .select(UserRole::getUserId)
            .eq(UserRole::getRoleId, roleId)
            .list()
            .stream()
            .map(UserRole::getUserId)
            .collect(Collectors.toList());
        if (userIds.size() == 0) {
            return Result.success();
        }
        // 获取username
        final List<String> usernames = userService.lambdaQuery()
            .select(User::getUsername)
            .in(User::getId, userIds)
            .list()
            .stream()
            .map(User::getUsername)
            .collect(Collectors.toList());
        // 清除用户授权信息
        usernames.forEach(this::clearUserAuthorityByUserId);
        return Result.success();
    }

    /**
     * 清除用户授权信息
     *
     * @param menuId 权限菜单ID
     */
    @Override
    public Result<?> clearUserAuthorityByMenuId(String menuId) {
        // 根据权限菜单ID获取角色ID
        final List<String> roleIds = roleMenuService.lambdaQuery()
            .select(RoleMenu::getRoleId)
            .eq(RoleMenu::getMenuId, menuId)
            .list()
            .stream()
            .map(RoleMenu::getRoleId)
            .collect(Collectors.toList());
        // 清除用户授权信息
        roleIds.forEach(this::clearUserAuthorityByRoleId);
        return Result.success();
    }

    /**
     * 清空用户菜单缓存
     *
     * @return 是否清空成功
     */
    @Override
    public Result<?> clearUserMenuData() {
        // 清除授权用户菜单列表
        SpringUtil.unregisterBean(SECURITY_USER_MENU_LIST);
        // 清除授权用户菜单树
        SpringUtil.unregisterBean(SECURITY_USER_MENU_TREE);
        return Result.success();
    }
}
