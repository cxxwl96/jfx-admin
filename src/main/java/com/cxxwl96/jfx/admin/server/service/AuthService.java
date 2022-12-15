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

package com.cxxwl96.jfx.admin.server.service;

import com.cxxwl96.jfx.admin.server.entity.Menu;
import com.cxxwl96.jfx.admin.server.entity.Role;
import com.cxxwl96.jfx.admin.server.entity.User;
import com.cxxwl96.jfx.admin.server.lang.Result;

import java.util.List;
import java.util.Map;

/**
 * AuthService
 *
 * @author cxxwl96
 * @since 2022/9/16 22:44
 */
public interface AuthService {
    /**
     * 登录
     *
     * @param username username
     * @param password password
     * @return result
     */
    Result<User> login(String username, String password);

    /**
     * 退出登录
     *
     * @return result
     */
    Result<?> logout();

    /**
     * 获取已授权用户
     *
     * @return result
     */
    Result<User> getCurrentAuthUser();

    /**
     * 获取已授权用户的角色
     *
     * @return 已授权用户的角色
     */
    Result<List<Role>> getCurrentAuthUserRoles();

    /**
     * 当前已授权用户是否拥有超级管理员角色
     *
     * @return 否拥有超级管理员角色
     */
    Result<Boolean> hasAdministrator();

    /**
     * 获取用户权限列表
     *
     * @param userId 用户id
     * @return 用户权限列表 <permission, menu>
     */
    Map<String, Menu> getUserAuthority(String userId);

    /**
     * 获取用户菜单权限列表
     *
     * @param userId 用户ID
     * @return 用户菜单权限
     */
    Result<List<Menu>> getUserMenuList(String userId);

    /**
     * 获取当前登录用户菜单权限列表
     *
     * @return 用户菜单权限
     */
    Result<List<Menu>> getUserMenuList();

    /**
     * 获取当前登录用户菜单权限树
     *
     * @return 用户菜单权限
     */
    Result<List<Menu>> getUserMenuTree();

    /**
     * 清除用户授权信息
     *
     * @param userId 用户ID
     */
    Result<?> clearUserAuthorityByUserId(String userId);

    /**
     * 清除用户授权信息
     *
     * @param roleId 角色ID
     */
    Result<?> clearUserAuthorityByRoleId(String roleId);

    /**
     * 清除用户授权信息
     *
     * @param menuId 权限菜单ID
     */
    Result<?> clearUserAuthorityByMenuId(String menuId);

    /**
     * 清空用户菜单缓存
     *
     * @return 是否清空成功
     */
    Result<?> clearUserMenuData();
}
