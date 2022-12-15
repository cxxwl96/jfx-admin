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

import com.cxxwl96.jfx.admin.server.base.form.SearchForm;
import com.cxxwl96.jfx.admin.server.base.service.impl.BaseServiceImpl;
import com.cxxwl96.jfx.admin.server.entity.Role;
import com.cxxwl96.jfx.admin.server.entity.User;
import com.cxxwl96.jfx.admin.server.entity.UserRole;
import com.cxxwl96.jfx.admin.server.enums.RoleType;
import com.cxxwl96.jfx.admin.server.enums.Status;
import com.cxxwl96.jfx.admin.server.function.PropertyFunc;
import com.cxxwl96.jfx.admin.server.lang.Result;
import com.cxxwl96.jfx.admin.server.lang.SearchResult;
import com.cxxwl96.jfx.admin.server.mapper.UserMapper;
import com.cxxwl96.jfx.admin.server.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 系统用户服务类
 *
 * @author cxxwl96
 * @since 2021-06-18
 */
@Slf4j
@Service
public class UserServiceImpl extends BaseServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserSecurityImpl userSecurity;

    @Autowired
    private RoleServiceImpl roleService;

    @Autowired
    private UserRoleServiceImpl userRoleService;

    @Autowired
    private AuthServiceImpl authService;

    /**
     * 保存用户
     *
     * @param entity 用户
     * @return 用户
     */
    @Override
    public Result<User> saveOrUpdateUser(@NotNull User entity) {
        if (entity == null) {
            return Result.failed("保存用户为null");
        }
        User user = super.getById(entity.getId());
        if (user == null) {
            if (StrUtil.isBlank(entity.getPassword())) {
                return Result.failed("添加用户请设置密码");
            }
            // 不存在表示添加
            final String password = userSecurity.encodePassword(entity.getUsername(), entity.getPassword());
            entity.setPassword(password);
        } else {
            // 存在表示修改
            // 若输入密码不为空则修改密码
            if (StrUtil.isNotBlank(entity.getPassword())) {
                final String password = userSecurity.encodePassword(entity.getUsername(), entity.getPassword());
                entity.setPassword(password);
            } else {
                // 否则沿用原密码
                entity.setPassword(user.getPassword());
            }
        }
        if (!super.saveOrUpdate(entity)) {
            return Result.failed("保存失败");
        }
        return Result.success("保存成功");
    }

    /**
     * 删除用户
     *
     * @param ids 用户id
     * @return 是否删除成功
     */
    @Override
    public Result<Boolean> deleteUsers(Collection<String> ids) {
        // 鉴权当前用户是否能操作用户
        List<User> users = super.listByIds(ids);
        if (nonAuthOperate(users)) {
            return Result.failed("用户权限不足");
        }
        if (!super.removeByIds(ids)) {
            return Result.failed("删除失败");
        }
        return Result.success("删除成功");
    }

    /**
     * 分页条件查询
     *
     * @param searchForm 查询表单
     * @return 数据
     */
    @Override
    public SearchResult<User> getPageList(SearchForm searchForm) {
        final SearchResult<User> result = super.getPageList(searchForm);
        // 补充用户信息
        result.getItems().forEach(this::replenishUser);
        return result;
    }

    /**
     * 获取用户
     *
     * @param id 用户id
     * @return 用户
     */
    @Override
    public Result<User> getUser(String id) {
        User user = super.getById(id);
        if (user == null) {
            return Result.failed("找不到该用户");
        }
        // 补充用户信息
        replenishUser(user);
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 获取用户角色
     *
     * @param userId 用户ID
     * @return 用户角色
     */
    @Override
    public Result<List<Role>> getUserRoles(String userId) {
        final List<String> roleIds = userRoleService.lambdaQuery()
            .eq(UserRole::getUserId, userId)
            .list()
            .stream()
            .map(UserRole::getRoleId)
            .distinct()
            .collect(Collectors.toList());
        final List<Role> roles = roleService.lambdaQuery()
            .in(Role::getId, roleIds)
            .eq(Role::getStatus, Status.ENABLE)
            .list();
        return Result.success(roles);
    }

    /**
     * 设置用户角色（分配角色）
     *
     * @param userId 用户ID
     * @param roleIds 角色ID集合
     * @return 是否设置成功
     */
    @Override
    @Transactional
    public Result<Boolean> setUserRoles(String userId, List<String> roleIds) {
        // 先全部删除
        deleteUserRoles(userId);
        // 再添加
        final List<UserRole> userRoles = new ArrayList<>();
        roleIds.forEach(roleId -> {
            final UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoles.add(userRole);
        });
        if (userRoles.size() > 0 && !userRoleService.saveBatch(userRoles)) {
            log.error("save batch user role fail");
            return Result.failed("分配角色失败");
        }
        // 清空缓存的用户
        authService.clearUserAuthorityByUserId(userId);
        return Result.success("保存成功");
    }

    /**
     * 修改密码
     *
     * @param userId 用户ID
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @return 是否修改成功
     */
    @Override
    public Result<Boolean> updatePassword(String userId, String oldPassword, String newPassword) {
        if (StrUtil.isBlank(oldPassword)) {
            return Result.failed("原密码不能为空");
        }
        if (StrUtil.isBlank(newPassword)) {
            return Result.failed("新密码不能为空");
        }
        final User user = super.getById(userId);
        if (user == null) {
            return Result.failed("用户不存在");
        }
        final boolean matches = userSecurity.matchesPassword(user.getUsername(), oldPassword, user.getPassword());
        if (!matches) {
            return Result.failed("原密码错误");
        }
        final String encodedPassword = userSecurity.encodePassword(user.getUsername(), newPassword);
        final boolean success = super.lambdaUpdate()
            .eq(User::getId, userId)
            .set(User::getPassword, encodedPassword)
            .update();
        if (!success) {
            return Result.failed("修改失败");
        }
        return Result.success("修改成功");
    }

    /**
     * 是否拥有超级管理员角色
     *
     * @param userId 用户ID
     * @return 是否拥有超级管理员角色
     */
    @Override
    public boolean hasAdministrator(String userId) {
        final Result<List<Role>> result = getUserRoles(userId);
        if (!result.isSuccess()) {
            return false;
        }
        final List<Role> roles = result.getData();
        return roles.stream().anyMatch(role -> role.getType() == RoleType.ADMINISTRATOR);
    }

    /**
     * 鉴权当前用户是否能操作用户，非超级管理员不能操作超级管理员用户
     *
     * @param users 用户
     * @return true: 没有权限操作用户, false: 有权限操作用户
     */
    private boolean nonAuthOperate(List<User> users) {
        if (CollUtil.isEmpty(users)) {
            return false;
        }
        // 当前登录用户是否拥有超级管理员角色
        AtomicBoolean currentUserHasAdministrator = new AtomicBoolean(false);
        authService.hasAdministrator().successAndThen(result -> currentUserHasAdministrator.set(result.getData()));
        // 超级管理员拥有所有有权限操作
        if (currentUserHasAdministrator.get()) {
            return false;
        }
        // 获取当前登录用户
        final Result<User> userResult = authService.getCurrentAuthUser();
        if (!userResult.isSuccess()) {
            log.error(userResult.getMsg());
            return true; // 未了登录认证不允许操作
        }
        final User authUser = userResult.getData();
        // 非超级管理员不能做如下操作
        for (User user : users) {
            // 1、非超级管理员不能删除自己
            if (authUser.getId().equals(user.getId())) {
                log.error("Can't delete yourself. userId: {}, username: {}", authUser.getUsername(), authUser.getId());
                return true;
            }
            final Result<List<Role>> rolesResult = getUserRoles(user.getId());
            if (!rolesResult.isSuccess()) {
                continue;
            }
            final List<Role> roles = rolesResult.getData();
            // 2、非超级管理员不能操作超级管理员用户
            if (hasAdministrator(roles)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否拥有超级管理员角色
     *
     * @param roles 角色列表
     * @return 是否拥有超级管理员角色
     */
    private boolean hasAdministrator(List<Role> roles) {
        if (CollUtil.isEmpty(roles)) {
            return false;
        }
        return roles.stream().anyMatch(role -> role.getType() == RoleType.ADMINISTRATOR);
    }

    /**
     * 删除用户角色
     *
     * @param userId 用户ID
     */
    private void deleteUserRoles(String userId) {
        long count = userRoleService.lambdaQuery().eq(UserRole::getUserId, userId).count();
        if (count > 0) {
            final Map<String, Object> columnMap = new HashMap<>();
            PropertyFunc<UserRole, ?> column = UserRole::getUserId;
            columnMap.put(column.getColumnName(), userId);
            if (!userRoleService.removeByMap(columnMap)) {
                throw new RuntimeException("delete user role fail");
            }
        }
    }

    /**
     * 补充用户信息：
     * 1、补充用户角色
     *
     * @param user 用户
     */
    private void replenishUser(User user) {
        // 1、获取用户角色
        getUserRoles(user.getId()).successAndThen(userRoleResult -> {
            // 2、设置用户角色
            user.setRoles(userRoleResult.getData());
        });
    }
}
