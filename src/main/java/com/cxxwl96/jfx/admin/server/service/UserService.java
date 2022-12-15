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

import com.cxxwl96.jfx.admin.server.base.service.BaseService;
import com.cxxwl96.jfx.admin.server.entity.Role;
import com.cxxwl96.jfx.admin.server.entity.User;
import com.cxxwl96.jfx.admin.server.lang.Result;

import java.util.Collection;
import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * 系统用户服务类接口
 *
 * @author cxxwl96
 * @since 2021-06-18
 */
public interface UserService extends BaseService<User> {
    /**
     * 保存用户
     *
     * @param entity 用户
     * @return 用户
     */
    Result<User> saveOrUpdateUser(@NotNull User entity);

    /**
     * 删除用户
     *
     * @param ids 用户id
     * @return 是否删除成功
     */
    Result<Boolean> deleteUsers(Collection<String> ids);

    /**
     * 获取用户
     *
     * @param id 用户id
     * @return 用户
     */
    Result<User> getUser(String id);

    /**
     * 获取用户角色
     *
     * @param userId 用户ID
     * @return 用户角色
     */
    Result<List<Role>> getUserRoles(String userId);

    /**
     * 设置用户角色（分配角色）
     *
     * @param userId 用户ID
     * @param roleIds 角色ID集合
     * @return 是否设置成功
     */
    Result<Boolean> setUserRoles(String userId, List<String> roleIds);

    /**
     * 修改密码
     *
     * @param userId 用户ID
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @return 是否修改成功
     */
    Result<Boolean> updatePassword(String userId, String oldPassword, String newPassword);

    /**
     * 是否拥有超级管理员角色
     *
     * @param userId 用户ID
     * @return 是否拥有超级管理员角色
     */
    boolean hasAdministrator(String userId);
}
