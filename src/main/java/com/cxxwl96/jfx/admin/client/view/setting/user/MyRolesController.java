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

package com.cxxwl96.jfx.admin.client.view.setting.user;

import com.cxxwl96.jfx.admin.client.common.AbstractController;
import com.cxxwl96.jfx.admin.client.component.JFXTag;
import com.cxxwl96.jfx.admin.client.utils.Alert;
import com.cxxwl96.jfx.admin.server.service.impl.AuthServiceImpl;
import com.cxxwl96.jfx.admin.server.service.impl.UserServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import cn.hutool.core.util.StrUtil;
import io.datafx.controller.ViewController;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.layout.FlowPane;

/**
 * SecuritySettingController
 *
 * @author cxxwl96
 * @since 2022/11/26 22:03
 */
@ViewController("/views/setting/user/MyRoles.fxml")
public class MyRolesController extends AbstractController {
    @FXML
    private FlowPane rolesPane;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private AuthServiceImpl authService;

    @PostConstruct
    private void init() {
        // 获取当前用户信息
        authService.getCurrentAuthUser().successAndThen(userResult -> {
            // 获取当前用户角色信息
            userService.getUserRoles(userResult.getData().getId()).successAndThen(rolesResult -> {
                // 将角色信息刷新到视图
                rolesResult.getData().forEach(role -> {
                    final String tagText;
                    if (StrUtil.isNotBlank(role.getCode())) {
                        tagText = role.getName() + " " + role.getCode();
                    } else {
                        tagText = role.getName();
                    }
                    final JFXTag tag = new JFXTag(tagText);
                    tag.setType(JFXTag.JFXTagType.PRIMARY);
                    tag.setPadding(new Insets(20, 30, 20, 30));
                    rolesPane.getChildren().add(tag);
                });
            }).failedAndThen((code, msg) -> Alert.error(msg));
        }).failedAndThen((code, msg) -> Alert.error(msg));
    }
}
