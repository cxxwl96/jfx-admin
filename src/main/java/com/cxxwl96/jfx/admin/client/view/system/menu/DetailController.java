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

package com.cxxwl96.jfx.admin.client.view.system.menu;

import com.cxxwl96.jfx.admin.client.common.AbstractController;
import com.cxxwl96.jfx.admin.client.utils.IconUtil;
import com.cxxwl96.jfx.admin.server.entity.Menu;

import java.util.Arrays;

import cn.hutool.core.util.StrUtil;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.action.ActionMethod;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.extern.slf4j.Slf4j;

/**
 * DetailController
 *
 * @author cxxwl96
 * @since 2022/9/23 21:35
 */
@Slf4j
@ViewController("/views/system/menu/Detail.fxml")
public class DetailController extends AbstractController {
    public static final String SHOW_ACTION = "showAction";

    private static final String STRING_TRUE = "是";

    private static final String STRING_FALSE = "否";

    @FXMLViewFlowContext
    private ViewFlowContext context;

    @FXML
    private Label typeLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private Label iconLabel;

    @FXML
    private Label orderLabel;

    @FXML
    private Label mainLabel;

    @FXML
    private Label hideLabel;

    @FXML
    private Label httpUrlLabel;

    @FXML
    private Label resourceUrlLabel;

    @FXML
    private Label hideToolbarLabel;

    @FXML
    private Label httpUrlBlankLabel;

    @FXML
    private Label permissionsLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label codeLabel;

    @FXML
    private Label remarkLabel;

    @ActionMethod(SHOW_ACTION)
    private void showAction() {
        Menu menu = context.getRegisteredObject(Menu.class);
        if (menu == null) {
            // 清空所有菜单详情字段
            clearMenuLabel();
            return;
        }
        if (menu.getType() != null) {
            switch (menu.getType()) {
                case DIRECTORY:
                    typeLabel.setGraphic(IconUtil.getIcon("folder"));
                    break;
                case MENU:
                    typeLabel.setGraphic(IconUtil.getIcon("file-empty"));
                    break;
                case BUTTON:
                    typeLabel.setGraphic(IconUtil.getIcon("toggle-off"));
                    break;
            }
            typeLabel.setText(menu.getType().getRemark());
        }
        titleLabel.setText(menu.getTitle());
        iconLabel.setGraphic(IconUtil.getIcon(menu.getIcon()));
        orderLabel.setText(String.valueOf(menu.getOrderNo()));
        mainLabel.setText(menu.getMain() ? STRING_TRUE : STRING_FALSE);
        hideLabel.setText(menu.getHide() ? STRING_TRUE : STRING_FALSE);
        httpUrlLabel.setText(menu.getHttpUrl() ? STRING_TRUE : STRING_FALSE);
        resourceUrlLabel.setText(menu.getResourceUrl());
        hideToolbarLabel.setText(menu.getHideToolbar() ? STRING_TRUE : STRING_FALSE);
        httpUrlBlankLabel.setText(menu.getHttpUrlBlank() ? STRING_TRUE : STRING_FALSE);
        permissionsLabel.setText(menu.getPermissions());
        statusLabel.setText(menu.getStatus() != null ? menu.getStatus().getRemark() : StrUtil.EMPTY);
        codeLabel.setText(menu.getCode());
        remarkLabel.setText(menu.getRemark());
    }

    private void clearMenuLabel() {
        Arrays.stream(DetailController.class.getDeclaredFields())
            .filter(field -> field.isAnnotationPresent(FXML.class) && field.getType()
                .getName()
                .equals(Label.class.getName()))
            .forEach(field -> {
                try {
                    boolean accessible = field.isAccessible();
                    field.setAccessible(true);
                    Label label = (Label) field.get(this);
                    label.setText(StrUtil.EMPTY);
                    field.setAccessible(accessible);
                } catch (IllegalAccessException exception) {
                    log.error(exception.getMessage(), exception);
                }
            });
    }
}
