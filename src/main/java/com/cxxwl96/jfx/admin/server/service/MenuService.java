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

import com.cxxwl96.jfx.admin.server.base.service.TreeService;
import com.cxxwl96.jfx.admin.server.entity.Menu;
import com.cxxwl96.jfx.admin.server.lang.Result;

import javax.validation.constraints.NotNull;

/**
 * MenuService
 *
 * @author cxxwl96
 * @since 2021-06-18
 */
public interface MenuService extends TreeService<Menu> {
    /**
     * 保存菜单
     *
     * @param menu 菜单
     * @return 菜单
     */
    Result<Menu> saveMenu(@NotNull Menu menu);

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     * @param includeSelf 是否包含自己
     * @return 是否删除成功
     */
    Result<Boolean> deleteMenu(@NotNull String id, boolean includeSelf);

    /**
     * 通过编码获取菜单，菜单不存在或存在多个菜单则获取失败
     *
     * @param code 菜单编码
     * @return 菜单
     */
    Result<Menu> getMenuByCode(@NotNull String code);
}
