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

package com.cxxwl96.jfx.admin.client.view.frame;

import com.cxxwl96.jfx.admin.client.common.AbstractController;
import com.cxxwl96.jfx.admin.client.common.ApplicationStore;
import com.cxxwl96.jfx.admin.client.utils.Alert;
import com.cxxwl96.jfx.admin.client.utils.Confirm;
import com.cxxwl96.jfx.admin.client.utils.IconUtil;
import com.cxxwl96.jfx.admin.client.view.login.LoginController;
import com.cxxwl96.jfx.admin.server.entity.Menu;
import com.cxxwl96.jfx.admin.server.entity.User;
import com.cxxwl96.jfx.admin.server.lang.Result;
import com.cxxwl96.jfx.admin.server.service.impl.AuthServiceImpl;
import com.cxxwl96.jfx.admin.server.service.impl.MenuServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import cn.hutool.core.util.IdUtil;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.util.VetoException;
import io.datafx.core.concurrent.ProcessChain;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

/**
 * UserListViewController
 *
 * @author cxxwl96
 * @since 2022/11/24 23:38
 */
@Slf4j
@ViewController("/views/frame/UserListView.fxml")
public class UserListViewController extends AbstractController {
    @FXML
    private Label userSetting;

    @FXML
    private Label loginExit;

    @FXML
    private Label close;

    @Autowired
    private MenuServiceImpl menuService;

    @Autowired
    private AuthServiceImpl authService;

    @PostConstruct
    private void init() {
        final String userSettingMenuId = IdUtil.simpleUUID(); // 数据库不存在UserSetting编码的菜单时使用此ID作为新菜单ID
        final Result<Menu> menuResult = menuService.getMenuByCode("UserSetting"); // 获取UserSetting编码的菜单
        this.userSetting.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            // 获取Frame的控制器
            ApplicationStore.getRegisterBean(IndexController.class).ifPresent(controller -> {
                // 数据库获取的菜单存在则使用数据库的菜单
                menuResult.successAndThen(result -> controller.addTab(result.getData())).failedAndThen((code, msg) -> {
                    // 数据库的菜单不存在则使用新菜单
                    controller.addTab(userSettingMenuId, "个人设置", IconUtil.getIcon("icon-ri-user-settings-line"),
                        new Flow(com.cxxwl96.jfx.admin.client.view.setting.user.IndexController.class));
                });
            });
        });
        loginExit.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            // 确认对话框
            Confirm.buildConfirm().okAction(okEvent -> ApplicationStore.getContextFlowHandler().ifPresent(handler -> {
                final Result<User> authUser = authService.getCurrentAuthUser();
                // 退出登录
                authService.logout().successAndThen(result -> {
                    // 跳转登录界面
                    ProcessChain.create().addRunnableInPlatformThread(() -> {
                        try {
                            handler.navigateTo(LoginController.class);
                            authUser.successAndThen(res -> log.info("{} logout success", res.getData().getUsername()))
                                .failedAndThen((code, msg) -> log.error("logout failed. {}", msg));
                        } catch (VetoException | FlowException exception) {
                            log.error(exception.getMessage(), exception);
                            Alert.error("发生未知错误", exception.getMessage());
                        }
                    }).run();
                });
            })).warn("提示", "确认退出登录？");
        });
        close.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            // 确认对话框
            Confirm.buildConfirm().okAction(okEvent -> {
                // 关闭系统
                ApplicationStore.getRootStage().ifPresent(Stage::close);
            }).warn("提示", "确认关闭程序？");
        });
    }
}
