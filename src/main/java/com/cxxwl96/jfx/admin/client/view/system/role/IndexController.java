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

    // 添加按钮
    private final JFXButton addButton = new JFXButton("添加", IconUtil.getIcon(ICON_ADD));

    // 批量删除按钮
    private final JFXButton multipleDeleteButton = new JFXButton("批量删除", IconUtil.getIcon(ICON_DELETE));

    @Autowired
    private RoleServiceImpl roleService;

    @Autowired
    private AuthServiceImpl authService;

    // 即将分配权限的roleId
    private String authRoleId;

    @PostConstruct
    private void init() {
        // 初始化表格数据
        initTableData();
        // 初始化表单
        initForm();
        // 初始化详情
        initDetail();
        // 初始化分配权限窗口
        initAuth();
        // 初始化添加按钮
        initAddButton();
        // 初始化批量删除按钮
        initMultipleDeleteButton();
    }

    /**
     * 初始化表格数据
     */
    private void initTableData() {
        // 设置搜索表单约束
        tableData.initSearchFormSchema(SearchFormSchema.class);
        // 设置表格列约束
        tableData.initTableColumnSchema(ListSchema.class);
        // 设置表格数据api接口
        tableData.setApi((searchForm) -> roleService.getPageList(searchForm));
        // 设置添加按钮
        tableData.getToolBar().getChildren().add(0, addButton);
        // 设置批量删除按钮
        tableData.getToolBar().getChildren().add(1, multipleDeleteButton);
        // 设置操作列宽
        tableData.setOperateWidth(160);
        // 设置操作列表
        Supplier4<JFXButton, String, String, String, String> createBtn = (title, icon, styleClass, authority) -> {
            final JFXButton button = new JFXButton(StrUtil.EMPTY, IconUtil.getIcon(icon));
            button.setTooltip(new Tooltip(title));
            button.getStyleClass().add(styleClass);
            button.setPrefSize(30, 30);
            SecurityUtil.manage(button, authority); // 权限管理
            return button;
        };
        // 获取当前用户角色最高的角色等级
        AtomicInteger maxLevel = new AtomicInteger();
        authService.getCurrentAuthUserRoles()
            .successAndThen(result -> result.getData()
                .stream()
                .max(Comparator.comparing(Role::getLevel))
                .ifPresent(role -> maxLevel.set(role.getLevel())));
        tableData.setOperateCallback((tableColumn, tableCell) -> {
            final JFXButton detailBtn = createBtn.get("详情", ICON_DETAIL, "jfx-button-flat", "role:detail");
            final JFXButton editBtn = createBtn.get("编辑", ICON_EDIT, "jfx-button", "role:edit");
            final JFXButton authBtn = createBtn.get("分配权限", ICON_AUTH, "jfx-button-primary", "role:allocation:auth");
            final JFXButton deleteBtn = createBtn.get("删除", ICON_DELETE, "jfx-button-danger", "role:delete");
            // 角色低等级用户不能删除高等级角色
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
     * 初始化表单
     */
    private void initForm() {
        // 设置表单约束
        form.initFormSchema(FormSchema.class);
        // 提交API接口
        form.setApi(roleService::saveOrUpdateRole, Role.class);
        // 设置取消按钮为关闭对话框按钮
        form.getCancelBtn().setText("关闭");
        // 点击取消关闭对话框
        form.onCancel(event -> formDialog.close());
        // 提交后置事件处理
        form.onSubmitted(result -> result.successAndThen(res -> {
            // 关闭对话框
            formDialog.close();
            // 刷新表格
            tableData.refreshTable();
        }));
        // 关闭表单对话框时重置表单
        formDialog.setOnDialogClosed(event -> form.resetForm());
        // 设置角色等级最大值
        // 1、先获取当前授权用户的所有角色
        authService.getCurrentAuthUserRoles().successAndThen(result -> {
            // 2、过滤得到角色的最大等级
            result.getData().stream().map(Role::getLevel).max(Integer::compareTo).ifPresent(maxLevel -> {
                // 3、过滤获取表单中的level字段的控件
                form.getFormFields()
                    .stream()
                    .filter(formField -> "level".equals(formField.getName()))
                    .map(JFXFormField::getControl)
                    .findFirst()
                    .ifPresent(control -> {
                        // 4、得到level字段的控件后设置控件的最大值
                        final HBox hBox = (HBox) control;
                        final JFXSlider slider = (JFXSlider) hBox.getChildren().get(0);
                        slider.setMax(maxLevel);
                    });
            });
        }).failedAndThen((code, msg) -> Alert.error(msg));

    }

    /**
     * 初始化详情
     */
    private void initDetail() {
        // 设置详情约束
        detail.initDetailSchema(DetailSchema.class);
        // 设置关闭对话框按钮
        final JFXButton closeBtn = new JFXButton("关闭");
        closeBtn.getStyleClass().add("jfx-button-flat");
        closeBtn.setOnAction(event -> detailDialog.close());
        detail.getToolBottom().getChildren().add(closeBtn);
    }

    /**
     * 初始化分配权限窗口
     */
    private void initAuth() {
        tree.setTitle("分配权限", IconUtil.getIcon(ICON_AUTH));
        tree.setTreeItemFilter(data -> data.getStatus() == Status.ENABLE) // 设置TreeItem过滤器
            .setTreeItemValueFactory(data -> new Label(data.getTitle())) // 设置TreeItemValueFactory
            .setMainFieldValueFactory(Menu::getTitle); // 设置MainFieldValueFactory
        // 提交分配权限事件
        authSubmitBtn.setOnAction(event -> {
            if (StrUtil.isBlank(authRoleId)) {
                Alert.error("请选择要分配权限的角色");
                return;
            }
            final List<String> menuIds = tree.getSelectedData().stream().map(Menu::getId).collect(Collectors.toList());
            Confirm.buildConfirm().okAction(okEvent -> {
                roleService.setRoleMenus(authRoleId, menuIds)
                    .successAndThen(result -> Alert.info(result.getMsg()))
                    .failedAndThen((code, msg) -> Alert.error(msg));
                tree.clearSelection(); // 清空选择
                authDialog.close();
            }).warn("确认分配？", String.format(Locale.ROOT, "确认分配已勾选的%d条菜单权限？", menuIds.size()));
        });
        // 分配权限取消事件
        authCancelBtn.setOnAction(event -> authDialog.close());
    }

    /**
     * 初始化添加按钮
     */
    private void initAddButton() {
        SecurityUtil.manage(addButton, "role:add"); // 权限管理
        addButton.setOnAction(event -> {
            form.setTitle("添加角色", IconUtil.getIcon(ICON_ADD));
            formDialog.show(rootPane);
        });
    }

    /**
     * 初始化批量删除按钮
     */
    private void initMultipleDeleteButton() {
        SecurityUtil.manage(multipleDeleteButton, "role:delete:batch"); // 权限管理
        multipleDeleteButton.getStyleClass().add("jfx-button-danger");
        multipleDeleteButton.setOnAction(event -> {
            final List<ListSchema> selectedItems = tableData.getSelectedItems();
            if (selectedItems.size() < 1) {
                Alert.warn("请选择要删除的数据");
                return;
            }
            final List<String> ids = selectedItems.stream().map(ListSchema::getId).collect(Collectors.toList());
            Confirm.buildConfirm().okAction(okEvent -> {
                roleService.deleteRoles(ids)
                    .successAndThen(result -> Alert.info(result.getMsg()))
                    .failedAndThen((code, msg) -> Alert.error(msg));
                tableData.refreshTable();
            }).warn(String.format(Locale.ROOT, "确认删除选中的%d条数据？", ids.size()));
        });
    }

    /**
     * 详情事件
     *
     * @param tableColumn table column
     * @param tableCell table cell
     */
    private void detailAction(TableColumn<ListSchema, Node> tableColumn, TableCell<ListSchema, Node> tableCell) {
        final ListSchema listSchema = tableCell.getTableView().getItems().get(tableCell.getIndex());
        roleService.getRole(listSchema.getId()).successAndThen(result -> {
            detail.setTitle("角色详情", IconUtil.getIcon(ICON_DETAIL));
            detail.setDetailData(result.getData());
            detailDialog.show(rootPane);
        }).failedAndThen((code, msg) -> Alert.error(msg));
    }

    /**
     * 编辑事件
     *
     * @param tableColumn table column
     * @param tableCell table cell
     */
    private void editAction(TableColumn<ListSchema, Node> tableColumn, TableCell<ListSchema, Node> tableCell) {
        final ListSchema listSchema = tableCell.getTableView().getItems().get(tableCell.getIndex());
        roleService.getRole(listSchema.getId()).successAndThen(result -> {
            form.setTitle("编辑角色", IconUtil.getIcon(ICON_EDIT));
            form.setFormData(result.getData());
            formDialog.show(rootPane);
        }).failedAndThen((code, msg) -> Alert.error(msg));
    }

    /**
     * 分配权限事件
     *
     * @param tableColumn table column
     * @param tableCell table cell
     */
    private void authAction(TableColumn<ListSchema, Node> tableColumn, TableCell<ListSchema, Node> tableCell) {
        final ListSchema listSchema = tableCell.getTableView().getItems().get(tableCell.getIndex());
        authRoleId = listSchema.getId();
        roleService.getRoleMenus(authRoleId)
            .successAndThen(roleMenusResult -> authService.getUserMenuTree().successAndThen(result -> {
                tree.setTreeData(result.getData()); // 设置显示数据
                tree.clearSelection(); // 清空选择
                tree.setSelectedData(roleMenusResult.getData()); // 设置选中数据
                authDialog.show(rootPane);
            }).failedAndThen((code, msg) -> Alert.error(msg)))
            .failedAndThen((code, msg) -> Alert.error(msg));
    }

    /**
     * 删除事件
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
        }).warn("确认删除？");
    }
}
