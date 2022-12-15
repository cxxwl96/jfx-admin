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
import com.cxxwl96.jfx.admin.server.lang.Result;

import java.util.Collection;
import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * 角色服务接口类
 *
 * @author cxxwl96
 * @since 2021/8/22 14:34
 */
public interface RoleService extends BaseService<Role> {
    /**
     * 保存角色
     *
     * @param entity 角色
     * @return 角色
     */
    Result<Role> saveOrUpdateRole(@NotNull Role entity);

    /**
     * 删除角色
     *
     * @param ids 角色id
     * @return 是否删除成功
     */
    Result<Boolean> deleteRoles(Collection<String> ids);

    /**
     * 获取角色
     *
     * @param id 角色id
     * @return 角色
     */
    Result<Role> getRole(String id);

    /**
     * 获取角色的菜单权限（获取角色权限）
     *
     * @param roleId 角色ID
     * @return 菜单权限ID集合
     */
    Result<List<String>> getRoleMenus(String roleId);

    /**
     * 设置角色的菜单权限（分配权限）
     *
     * @param roleId 角色ID
     * @param menuIds 菜单权限ID集合
     * @return 是否设置成功
     */
    Result<Boolean> setRoleMenus(String roleId, List<String> menuIds);
}
