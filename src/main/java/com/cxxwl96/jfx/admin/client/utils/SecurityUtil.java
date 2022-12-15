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

package com.cxxwl96.jfx.admin.client.utils;

import com.cxxwl96.jfx.admin.server.entity.Menu;
import com.cxxwl96.jfx.admin.server.entity.User;
import com.cxxwl96.jfx.admin.server.lang.Result;
import com.cxxwl96.jfx.admin.server.service.impl.AuthServiceImpl;

import java.util.Map;

import cn.hutool.extra.spring.SpringUtil;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import lombok.extern.slf4j.Slf4j;

/**
 * SecurityUtil
 *
 * @author cxxwl96
 * @since 2022/12/5 21:47
 */
@Slf4j
public class SecurityUtil {
    private static final AuthServiceImpl authService = SpringUtil.getBean(AuthServiceImpl.class);

    /**
     * 通过权限管理权限节点
     *
     * @param node 节点
     * @param authority 权限标识
     */
    public static void manage(Node node, String authority) {
        manage(node, authority, false);
    }

    /**
     * 通过权限管理权限节点
     *
     * @param node 节点
     * @param authority 权限标识
     * @param forceShow 没有权限时强制显示，但是disable=false
     */
    public static void manage(Node node, String authority, boolean forceShow) {
        final boolean visible = visible(authority);
        if (!visible && forceShow) {
            // 强制显示，但是disable=false
            node.setDisable(true);
        } else if (!visible) {
            // 没有权限
            node.setManaged(false);
            node.setVisible(false);
        }
    }

    /**
     * 权限是否可见属性
     *
     * @param authority 权限标识
     * @return 权限是否可见属性
     */
    public static SimpleBooleanProperty visibleProperty(String authority) {
        return new SimpleBooleanProperty(visible(authority));
    }

    /**
     * 权限是否可见。
     * 拥有权限，并未设置隐藏
     *
     * @param authority 权限标识
     * @return 是否拥有权限
     */
    public static boolean visible(String authority) {
        final Result<Boolean> hasAdministrator = authService.hasAdministrator();
        if (!hasAdministrator.isSuccess()) {
            log.error(hasAdministrator.getMsg());
            return false;
        }
        // 是否是超级管理员
        if (hasAdministrator.getData()) {
            return true;
        }
        final Result<User> currentAuthUser = authService.getCurrentAuthUser();
        if (!currentAuthUser.isSuccess()) {
            log.error(currentAuthUser.getMsg());
            return false;
        }
        final User user = currentAuthUser.getData();
        // 获取用户权限列表
        final Map<String, Menu> userAuthority = authService.getUserAuthority(user.getId());
        // 没有权限
        if (!userAuthority.containsKey(authority)) {
            return false;
        }
        // 拥有权限，并未设置隐藏
        return !userAuthority.get(authority).getHide();
    }
}
