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

package com.cxxwl96.jfx.admin.client.view.system.role;

import com.cxxwl96.jfx.admin.client.common.AbstractController;
import com.cxxwl96.jfx.admin.client.component.detail.JFXDetail;
import com.cxxwl96.jfx.admin.client.component.form.JFXForm;
import com.cxxwl96.jfx.admin.client.component.form.JFXFormField;
import com.cxxwl96.jfx.admin.client.component.tabledata.JFXTableData;
import com.cxxwl96.jfx.admin.client.component.tree.JFXTree;
import com.cxxwl96.jfx.admin.client.utils.Alert;
import com.cxxwl96.jfx.admin.client.utils.Confirm;
import com.cxxwl96.jfx.admin.client.utils.IconUtil;
import com.cxxwl96.jfx.admin.client.utils.SecurityUtil;
import com.cxxwl96.jfx.admin.server.entity.Menu;
import com.cxxwl96.jfx.admin.server.entity.Role;
import com.cxxwl96.jfx.admin.server.enums.Status;
import com.cxxwl96.jfx.admin.server.service.impl.AuthServiceImpl;
import com.cxxwl96.jfx.admin.server.service.impl.RoleServiceImpl;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXSlider;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Supplier4;
import cn.hutool.core.util.StrUtil;
import io.datafx.controller.ViewController;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import lombok.extern.slf4j.Slf4j;

/**
 * IndexController
 *
 * @author cxxwl96
 * @since 2022/11/7 22:57
 */
@Slf4j
@ViewController("/views/system/role/Index.fxml")
public class IndexController extends AbstractController {
    private static final String ICON_ADD = "icon-ic-baseline-add";

    private static final String ICON_DETAIL = "icon-ic-outline-info";

    private static final String ICON_EDIT = "icon-ic-baseline-edit";

    private static final String ICON_AUTH = "icon-ic-outline-security";

    private static final String ICON_DELETE = "icon-ic-baseline-delete-outline";

    @FXML
    private StackPane rootPane;

    @FXML
    private JFXTableData<ListSchema> tableData;

    @FXML
    private JFXDialog detailDialog;

    @FXML
    private JFXDetail<Role> detail;

    @FXML
    private JFXDialog formDialog;

    @FXML
    private JFXForm<Role> form;

    @FXML
    private JFXDialog authDialog;

    @FXML
    private JFXTree<Menu> tree;

    @FXML
    private JFXButton authSubmitBtn;

    @FXML
    private JFXButton authCancelBtn;

    // ????????????
    private final JFXButton addButton = new JFXButton("??????", IconUtil.getIcon(ICON_ADD));

    // ??????????????????
    private final JFXButton multipleDeleteButton = new JFXButton("????????????", IconUtil.getIcon(ICON_DELETE));

    @Autowired
    private RoleServiceImpl roleService;

    @Autowired
    private AuthServiceImpl authService;

    // ?????????????????????roleId
    private String authRoleId;

    @PostConstruct
    private void init() {
        // ?????????????????????
        initTableData();
        // ???????????????
        initForm();
        // ???????????????
        initDetail();
        // ???????????????????????????
        initAuth();
        // ?????????????????????
        initAddButton();
        // ???????????????????????????
        initMultipleDeleteButton();
    }

    /**
     * ?????????????????????
     */
    private void initTableData() {
        // ????????????????????????
        tableData.initSearchFormSchema(SearchFormSchema.class);
        // ?????????????????????
        tableData.initTableColumnSchema(ListSchema.class);
        // ??????????????????api??????
        tableData.setApi((searchForm) -> roleService.getPageList(searchForm));
        // ??????????????????
        tableData.getToolBar().getChildren().add(0, addButton);
        // ????????????????????????
        tableData.getToolBar().getChildren().add(1, multipleDeleteButton);
        // ??????????????????
        tableData.setOperateWidth(160);
        // ??????????????????
        Supplier4<JFXButton, String, String, String, String> createBtn = (title, icon, styleClass, authority) -> {
            final JFXButton button = new JFXButton(StrUtil.EMPTY, IconUtil.getIcon(icon));
            button.setTooltip(new Tooltip(title));
            button.getStyleClass().add(styleClass);
            button.setPrefSize(30, 30);
            SecurityUtil.manage(button, authority); // ????????????
            return button;
        };
        // ?????????????????????????????????????????????
        AtomicInteger maxLevel = new AtomicInteger();
        authService.getCurrentAuthUserRoles()
            .successAndThen(result -> result.getData()
                .stream()
                .max(Comparator.comparing(Role::getLevel))
                .ifPresent(role -> maxLevel.set(role.getLevel())));
        tableData.setOperateCallback((tableColumn, tableCell) -> {
            final JFXButton detailBtn = createBtn.get("??????", ICON_DETAIL, "jfx-button-flat", "role:detail");
            final JFXButton editBtn = createBtn.get("??????", ICON_EDIT, "jfx-button", "role:edit");
            final JFXButton authBtn = createBtn.get("????????????", ICON_AUTH, "jfx-button-primary", "role:allocation:auth");
            final JFXButton deleteBtn = createBtn.get("??????", ICON_DELETE, "jfx-button-danger", "role:delete");
            // ????????????????????????????????????????????????
            final ListSchema listSchema = tableCell.getTableView().getItems().get(tableCell.getIndex());
            if (listSchema.getLevel() >= maxLevel.get()) {
                deleteBtn.setManaged(false);
                deleteBtn.setVisible(false);
            }
            detailBtn.setOnAction(event -> detailAction(tableColumn, tableCell));
            editBtn.setOnAction(event -> editAction(tableColumn, tableCell));
            authBtn.setOnAction(event -> authAction(tableColumn, tableCell));
            deleteBtn.setOnAction(event -> deleteAction(tableColumn, tableCell));
            final HBox node = new HBox(detailBtn, editBtn, authBtn, deleteBtn);
            node.setSpacing(10);
            node.setAlignment(Pos.CENTER);
            return node;
        });
    }

    /**
     * ???????????????
     */
    private void initForm() {
        // ??????????????????
        form.initFormSchema(FormSchema.class);
        // ??????API??????
        form.setApi(roleService::saveOrUpdateRole, Role.class);
        // ??????????????????????????????????????????
        form.getCancelBtn().setText("??????");
        // ???????????????????????????
        form.onCancel(event -> formDialog.close());
        // ????????????????????????
        form.onSubmitted(result -> result.successAndThen(res -> {
            // ???????????????
            formDialog.close();
            // ????????????
            tableData.refreshTable();
        }));
        // ????????????????????????????????????
        formDialog.setOnDialogClosed(event -> form.resetForm());
        // ???????????????????????????
        // 1?????????????????????????????????????????????
        authService.getCurrentAuthUserRoles().successAndThen(result -> {
            // 2????????????????????????????????????
            result.getData().stream().map(Role::getLevel).max(Integer::compareTo).ifPresent(maxLevel -> {
                // 3???????????????????????????level???????????????
                form.getFormFields()
                    .stream()
                    .filter(formField -> "level".equals(formField.getName()))
                    .map(JFXFormField::getControl)
                    .findFirst()
                    .ifPresent(control -> {
                        // 4?????????level??????????????????????????????????????????
                        final HBox hBox = (HBox) control;
                        final JFXSlider slider = (JFXSlider) hBox.getChildren().get(0);
                        slider.setMax(maxLevel);
                    });
            });
        }).failedAndThen((code, msg) -> Alert.error(msg));

    }

    /**
     * ???????????????
     */
    private void initDetail() {
        // ??????????????????
        detail.initDetailSchema(DetailSchema.class);
        // ???????????????????????????
        final JFXButton closeBtn = new JFXButton("??????");
        closeBtn.getStyleClass().add("jfx-button-flat");
        closeBtn.setOnAction(event -> detailDialog.close());
        detail.getToolBottom().getChildren().add(closeBtn);
    }

    /**
     * ???????????????????????????
     */
    private void initAuth() {
        tree.setTitle("????????????", IconUtil.getIcon(ICON_AUTH));
        tree.setTreeItemFilter(data -> data.getStatus() == Status.ENABLE) // ??????TreeItem?????????
            .setTreeItemValueFactory(data -> new Label(data.getTitle())) // ??????TreeItemValueFactory
            .setMainFieldValueFactory(Menu::getTitle); // ??????MainFieldValueFactory
        // ????????????????????????
        authSubmitBtn.setOnAction(event -> {
            if (StrUtil.isBlank(authRoleId)) {
                Alert.error("?????????????????????????????????");
                return;
            }
            final List<String> menuIds = tree.getSelectedData().stream().map(Menu::getId).collect(Collectors.toList());
            Confirm.buildConfirm().okAction(okEvent -> {
                roleService.setRoleMenus(authRoleId, menuIds)
                    .successAndThen(result -> Alert.info(result.getMsg()))
                    .failedAndThen((code, msg) -> Alert.error(msg));
                tree.clearSelection(); // ????????????
                authDialog.close();
            }).warn("???????????????", String.format(Locale.ROOT, "????????????????????????%d??????????????????", menuIds.size()));
        });
        // ????????????????????????
        authCancelBtn.setOnAction(event -> authDialog.close());
    }

    /**
     * ?????????????????????
     */
    private void initAddButton() {
        SecurityUtil.manage(addButton, "role:add"); // ????????????
        addButton.setOnAction(event -> {
            form.setTitle("????????????", IconUtil.getIcon(ICON_ADD));
            formDialog.show(rootPane);
        });
    }

    /**
     * ???????????????????????????
     */
    private void initMultipleDeleteButton() {
        SecurityUtil.manage(multipleDeleteButton, "role:delete:batch"); // ????????????
        multipleDeleteButton.getStyleClass().add("jfx-button-danger");
        multipleDeleteButton.setOnAction(event -> {
            final List<ListSchema> selectedItems = tableData.getSelectedItems();
            if (selectedItems.size() < 1) {
                Alert.warn("???????????????????????????");
                return;
            }
            final List<String> ids = selectedItems.stream().map(ListSchema::getId).collect(Collectors.toList());
            Confirm.buildConfirm().okAction(okEvent -> {
                roleService.deleteRoles(ids)
                    .successAndThen(result -> Alert.info(result.getMsg()))
                    .failedAndThen((code, msg) -> Alert.error(msg));
                tableData.refreshTable();
            }).warn(String.format(Locale.ROOT, "?????????????????????%d????????????", ids.size()));
        });
    }

    /**
     * ????????????
     *
     * @param tableColumn table column
     * @param tableCell table cell
     */
    private void detailAction(TableColumn<ListSchema, Node> tableColumn, TableCell<ListSchema, Node> tableCell) {
        final ListSchema listSchema = tableCell.getTableView().getItems().get(tableCell.getIndex());
        roleService.getRole(listSchema.getId()).successAndThen(result -> {
            detail.setTitle("????????????", IconUtil.getIcon(ICON_DETAIL));
            detail.setDetailData(result.getData());
            detailDialog.show(rootPane);
        }).failedAndThen((code, msg) -> Alert.error(msg));
    }

    /**
     * ????????????
     *
     * @param tableColumn table column
     * @param tableCell table cell
     */
    private void editAction(TableColumn<ListSchema, Node> tableColumn, TableCell<ListSchema, Node> tableCell) {
        final ListSchema listSchema = tableCell.getTableView().getItems().get(tableCell.getIndex());
        roleService.getRole(listSchema.getId()).successAndThen(result -> {
            form.setTitle("????????????", IconUtil.getIcon(ICON_EDIT));
            form.setFormData(result.getData());
            formDialog.show(rootPane);
        }).failedAndThen((code, msg) -> Alert.error(msg));
    }

    /**
     * ??????????????????
     *
     * @param tableColumn table column
     * @param tableCell table cell
     */
    private void authAction(TableColumn<ListSchema, Node> tableColumn, TableCell<ListSchema, Node> tableCell) {
        final ListSchema listSchema = tableCell.getTableView().getItems().get(tableCell.getIndex());
        authRoleId = listSchema.getId();
        roleService.getRoleMenus(authRoleId)
            .successAndThen(roleMenusResult -> authService.getUserMenuTree().successAndThen(result -> {
                tree.setTreeData(result.getData()); // ??????????????????
                tree.clearSelection(); // ????????????
                tree.setSelectedData(roleMenusResult.getData()); // ??????????????????
                authDialog.show(rootPane);
            }).failedAndThen((code, msg) -> Alert.error(msg)))
            .failedAndThen((code, msg) -> Alert.error(msg));
    }

    /**
     * ????????????
     *
     * @param tableColumn table column
     * @param tableCell table cell
     */
    private void deleteAction(TableColumn<ListSchema, Node> tableColumn, TableCell<ListSchema, Node> tableCell) {
        final ListSchema listSchema = tableCell.getTableView().getItems().get(tableCell.getIndex());
        Confirm.buildConfirm().okAction(event -> {
            roleService.deleteRoles(CollUtil.newArrayList(listSchema.getId()))
                .successAndThen(result -> Alert.info(result.getMsg()))
                .failedAndThen((code, msg) -> Alert.error(msg));
            tableData.refreshTable();
        }).warn("???????????????");
    }
}
