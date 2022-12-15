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
import com.cxxwl96.jfx.admin.client.component.JFXIconPicker;
import com.cxxwl96.jfx.admin.client.component.JFXNumberField;
import com.cxxwl96.jfx.admin.client.utils.Alert;
import com.cxxwl96.jfx.admin.client.utils.IconUtil;
import com.cxxwl96.jfx.admin.server.entity.Menu;
import com.cxxwl96.jfx.admin.server.enums.MenuType;
import com.cxxwl96.jfx.admin.server.enums.Status;
import com.cxxwl96.jfx.admin.server.exception.ExecutionException;
import com.cxxwl96.jfx.admin.server.lang.Result;
import com.cxxwl96.jfx.admin.server.service.impl.MenuServiceImpl;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;

import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.action.ActionMethod;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import lombok.extern.slf4j.Slf4j;

/**
 * FormController
 *
 * @author cxxwl96
 * @since 2022/10/6 14:03
 */
@Slf4j
@ViewController("/views/system/menu/Form.fxml")
public class FormController extends AbstractController {
    public static final String ADD_ACTION = "addAction";

    public static final String EDIT_ACTION = "editAction";

    public static final String ADD_SUB_ACTION = "addSubAction";

    public static final String SUBMIT_ACTION = "submitAction";

    public static final String CANCEL_ACTION = "cancelAction";

    @FXMLViewFlowContext
    private ViewFlowContext context;

    @FXML
    private GridPane root;

    @FXML
    private ToggleGroup menuType;

    @FXML
    private JFXTextField titleText;

    @FXML
    private Label parentLabel;

    @FXML
    private JFXIconPicker iconPicker;

    @FXML
    private JFXTextField iconText;

    @FXML
    private JFXNumberField orderText;

    @FXML
    private JFXToggleButton mainSwitch;

    @FXML
    private JFXToggleButton hideSwitch;

    @FXML
    private JFXToggleButton httpUrlSwitch;

    @FXML
    private Label resourceUrlLabel;

    @FXML
    private JFXTextField resourceUrlText;

    @FXML
    private JFXToggleButton hideToolbarSwitch;

    @FXML
    private JFXToggleButton httpUrlBlankSwitch;

    @FXML
    private JFXTextField permissionsText;

    @FXML
    private JFXToggleButton statusSwitch;

    @FXML
    private JFXTextField codeText;

    @FXML
    private JFXTextArea remarkText;

    @Autowired
    private MenuServiceImpl menuService;

    private Menu formMenu;

    @PostConstruct
    private void init() {
        resetForm();
        // icon变更监听
        iconPicker.onChange((observable, oldValue, newValue) -> iconText.setText(newValue));
        iconText.textProperty().addListener((observable, oldValue, newValue) -> {
            Node icon;
            if (StrUtil.isBlank(newValue)) {
                icon = iconPicker.getDefaultGraphic();
            } else {
                icon = IconUtil.getIcon(newValue);
            }
            iconPicker.graphicProperty().set(icon);
        });
        // 显示目录类型字段
        showField(MenuType.DIRECTORY);
        // 菜单类型切换监听
        menuType.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            // 显示对应的字段
            showField(MenuType.valueOf(((String) newValue.getUserData())));
        });
        // http链接切换监听
        httpUrlSwitch.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                root.getChildren()
                    .stream()
                    .filter(hbox -> hbox.getStyleClass().contains("http-show"))
                    .forEach(this::showNode);
            } else {
                root.getChildren()
                    .stream()
                    .filter(hbox -> hbox.getStyleClass().contains("http-show"))
                    .forEach(this::hideNode);
            }
        });
    }

    /**
     * 显示对应的字段
     *
     * @param menuType menuType
     */
    private void showField(MenuType menuType) {
        // 隐藏所有字段
        root.getChildren()
            .stream()
            .filter(
                node -> !node.getStyleClass().contains("always-show") && !node.getStyleClass().contains("http-show"))
            .forEach(this::hideNode);
        // 默认显示字段
        if (menuType == MenuType.MENU) {
            root.getChildren()
                .stream()
                .filter(hbox -> hbox.getStyleClass().contains("menu-show"))
                .forEach(this::showNode);
        }
        if (menuType == MenuType.BUTTON) {
            httpUrlSwitch.setSelected(false);
            root.getChildren()
                .stream()
                .filter(hbox -> hbox.getStyleClass().contains("button-show"))
                .forEach(this::showNode);
        }
    }

    private void showNode(Node node) {
        if (node == null) {
            return;
        }
        node.setManaged(true);
        node.setVisible(true);
        if (node instanceof Labeled) {
            final Node graphic = ((Labeled) node).getGraphic();
            if (graphic != null) {
                graphic.setManaged(true);
            }
        }
    }

    private void hideNode(Node node) {
        if (node == null) {
            return;
        }
        node.setManaged(false);
        node.setVisible(false);
        if (node instanceof Labeled) {
            final Node graphic = ((Labeled) node).getGraphic();
            if (graphic != null) {
                graphic.setManaged(false);
            }
        }
    }

    /**
     * 添加菜单事件
     */
    @ActionMethod(ADD_ACTION)
    private void addAction() {
        formMenu = new Menu();
        resetForm();
    }

    /**
     * 编辑菜单事件
     */
    @ActionMethod(EDIT_ACTION)
    private void editAction() {
        final Menu menu = context.getRegisteredObject(Menu.class);
        if (menu == null) {
            resetForm();
            log.error("edit menu error: menu is null");
            Alert.error("编辑菜单失败", "找不到菜单");
            return;
        }
        formMenu = menu;
        transformToForm();
    }

    /**
     * 添加子菜单事件
     */
    @ActionMethod(ADD_SUB_ACTION)
    private void addSubAction() {
        final Menu menu = context.getRegisteredObject(Menu.class);
        if (menu == null) {
            resetForm();
            log.error("add submenu error:  parent menu is null");
            Alert.error("添加子菜单失败", "找不到父级菜单");
            return;
        }
        formMenu = new Menu();
        formMenu.setPId(menu.getId());
        formMenu.setParentMenu(menu);
        transformToForm();
    }

    /**
     * 表单提交事件
     *
     * @throws ExecutionException 执行失败异常
     */
    @ActionMethod(SUBMIT_ACTION)
    private void submitAction() throws ExecutionException {
        try {
            // 校验表单
            validateForm();
            // 表单数据转换为menu对象
            transformToMenu();
            final Result<Menu> result = menuService.saveMenu(formMenu);
            resetForm();
            if (!result.isSuccess()) {
                Alert.error(result.getMsg());
                throw new ExecutionException(result.getMsg());
            }
            Alert.info(result.getMsg());
        } catch (ExecutionException exception) {
            log.error(exception.getMessage());
            throw exception;
        }
    }

    /**
     * 取消事件
     */
    @ActionMethod(CANCEL_ACTION)
    private void cancelAction() {
        resetForm();
    }

    /**
     * 表单校验
     *
     * @throws ExecutionException 执行失败异常
     */
    private void validateForm() throws ExecutionException {
        if (!titleText.validate()) {
            throw new ExecutionException("Invalid title");
        }
        if (!NumberUtil.isInteger(orderText.getText())) {
            Alert.error("无效的排序序号");
            throw new ExecutionException("Invalid http resource url");
        }
        // 如果是http链接
        if (httpUrlSwitch.isSelected()) {
            // 如果资源/链接未填写则不保存
            if (!resourceUrlText.validate() || StrUtil.isBlank(resourceUrlText.getText())) {
                throw new ExecutionException("Invalid resource url");
            }
            // 校验是否是http链接
            if (!HttpUtil.isHttp(resourceUrlText.getText()) && !HttpUtil.isHttps(resourceUrlText.getText())) {
                Alert.error("无效的Http链接");
                throw new ExecutionException("Invalid http resource url");
            }
            return;
        }
        // 如果菜单类型为菜单或按钮
        if (MenuType.MENU.name().equals(menuType.getSelectedToggle().getUserData())) {
            // 如果资源/链接未填写则不保存
            if (!resourceUrlText.validate() || StrUtil.isBlank(resourceUrlText.getText())) {
                throw new ExecutionException("Invalid resource url");
            }
            // 校验controller资源是否存在
            try {
                Class.forName(resourceUrlText.getText());
            } catch (ClassNotFoundException exception) {
                log.error("{}: {}", exception.getClass().getName(), exception.getMessage());
                Alert.error("无效的资源");
                throw new ExecutionException("Invalid controller resource url");
            }
        }
    }

    /**
     * 重置表单
     */
    private void resetForm() {
        menuType.getToggles()
            .stream()
            .filter(toggle -> toggle.getUserData().equals(MenuType.DIRECTORY.name()))
            .findFirst()
            .ifPresent(toggle -> menuType.selectToggle(toggle));

        parentLabel.setText(StrUtil.EMPTY);
        parentLabel.setGraphic(null);

        titleText.setText(StrUtil.EMPTY);
        iconText.setText(StrUtil.EMPTY);
        orderText.setText("0");
        mainSwitch.setSelected(false);
        hideSwitch.setSelected(false);
        httpUrlSwitch.setSelected(false);
        resourceUrlText.setText(StrUtil.EMPTY);
        hideToolbarSwitch.setSelected(false);
        httpUrlBlankSwitch.setSelected(false);
        permissionsText.setText(StrUtil.EMPTY);
        statusSwitch.setSelected(true);
        codeText.setText(StrUtil.EMPTY);
        remarkText.setText(StrUtil.EMPTY);
    }

    /**
     * menu对象数据填充到表单
     */
    private void transformToForm() {
        menuType.getToggles()
            .stream()
            .filter(toggle -> toggle.getUserData().equals(formMenu.getType().name()))
            .findFirst()
            .ifPresent(toggle -> menuType.selectToggle(toggle));
        final Menu parentMenu = formMenu.getParentMenu();
        if (parentMenu != null) {
            parentLabel.setText(parentMenu.getTitle());
            parentLabel.setGraphic(IconUtil.getIcon(parentMenu.getIcon()));
        }
        titleText.setText(formMenu.getTitle());
        iconText.setText(formMenu.getIcon());
        orderText.setText(String.valueOf(formMenu.getOrderNo()));
        mainSwitch.setSelected(formMenu.getMain());
        hideSwitch.setSelected(formMenu.getHide());
        httpUrlSwitch.setSelected(formMenu.getHttpUrl());
        resourceUrlText.setText(formMenu.getResourceUrl());
        hideToolbarSwitch.setSelected(formMenu.getHideToolbar());
        httpUrlBlankSwitch.setSelected(formMenu.getHttpUrlBlank());
        permissionsText.setText(formMenu.getPermissions());
        statusSwitch.setSelected(formMenu.getStatus() == Status.ENABLE);
        codeText.setText(formMenu.getCode());
        remarkText.setText(formMenu.getRemark());
    }

    /**
     * 表单数据填充到menu对象
     */
    private void transformToMenu() {
        formMenu.setType(MenuType.valueOf(menuType.getSelectedToggle().getUserData().toString()));
        formMenu.setTitle(titleText.getText());
        formMenu.setIcon(iconText.getText());
        formMenu.setOrderNo(Integer.parseInt(orderText.getText()));
        formMenu.setMain(mainSwitch.isSelected());
        formMenu.setHide(hideSwitch.isSelected());
        formMenu.setHttpUrl(httpUrlSwitch.isSelected());
        formMenu.setResourceUrl(resourceUrlText.getText());
        formMenu.setHideToolbar(hideToolbarSwitch.isSelected());
        formMenu.setHttpUrlBlank(httpUrlBlankSwitch.isSelected());
        formMenu.setPermissions(permissionsText.getText());
        formMenu.setStatus(statusSwitch.isSelected() ? Status.ENABLE : Status.DISABLE);
        formMenu.setCode(codeText.getText());
        formMenu.setRemark(remarkText.getText());
    }

}
