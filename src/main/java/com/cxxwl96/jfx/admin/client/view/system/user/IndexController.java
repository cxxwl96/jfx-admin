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

package com.cxxwl96.jfx.admin.client.view.system.user;

import com.cxxwl96.jfx.admin.client.common.AbstractController;
import com.cxxwl96.jfx.admin.client.component.detail.JFXDetail;
import com.cxxwl96.jfx.admin.client.component.form.JFXForm;
import com.cxxwl96.jfx.admin.client.component.selector.JFXSelector;
import com.cxxwl96.jfx.admin.client.component.tabledata.JFXTableData;
import com.cxxwl96.jfx.admin.client.utils.Alert;
import com.cxxwl96.jfx.admin.client.utils.Confirm;
import com.cxxwl96.jfx.admin.client.utils.IconUtil;
import com.cxxwl96.jfx.admin.client.utils.SecurityUtil;
import com.cxxwl96.jfx.admin.server.base.form.WhereItem;
import com.cxxwl96.jfx.admin.server.entity.Role;
import com.cxxwl96.jfx.admin.server.entity.User;
import com.cxxwl96.jfx.admin.server.enums.Condition;
import com.cxxwl96.jfx.admin.server.enums.RoleType;
import com.cxxwl96.jfx.admin.server.enums.Status;
import com.cxxwl96.jfx.admin.server.lang.Result;
import com.cxxwl96.jfx.admin.server.service.impl.AuthServiceImpl;
import com.cxxwl96.jfx.admin.server.service.impl.RoleServiceImpl;
import com.cxxwl96.jfx.admin.server.service.impl.UserServiceImpl;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Supplier4;
import cn.hutool.core.util.StrUtil;
import io.datafx.controller.ViewController;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
 * @since 2022/10/16 21:20
 */
@Slf4j
@ViewController("/views/system/user/Index.fxml")
public class IndexController extends AbstractController {
    private static final String ICON_ADD = "icon-ic-baseline-add";

    private static final String ICON_DETAIL = "icon-ic-outline-info";

    private static final String ICON_EDIT = "icon-ic-baseline-edit";

    private static final String ICON_ROLE = "group, users";

    private static final String ICON_DELETE = "icon-ic-baseline-delete-outline";

    @FXML
    private StackPane rootPane;

    @FXML
    private JFXTableData<ListSchema> tableData;

    @FXML
    private JFXDialog detailDialog;

    @FXML
    private JFXDetail<User> detail;

    @FXML
    private JFXDialog formDialog;

    @FXML
    private JFXForm<User> form;

    @FXML
    private JFXDialog roleDialog;

    @FXML
    private JFXSelector<Role> selector;

    @FXML
    private JFXButton roleSubmitBtn;

    @FXML
    private JFXButton roleCancelBtn;

    // 添加按钮
    private final JFXButton addButton = new JFXButton("添加", IconUtil.getIcon(ICON_ADD));

    // 批量删除按钮
    private final JFXButton multipleDeleteButton = new JFXButton("批量删除", IconUtil.getIcon(ICON_DELETE));

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private RoleServiceImpl roleService;

    @Autowired
    private AuthServiceImpl authService;

    // 即将分配角色的userId
    private String userId;

    @PostConstruct
    private void init() {
        // 初始化表格数据
        initTableData();
        // 初始化表单
        initForm();
        // 初始化详情
        initDetail();
        // 初始化分配角色窗口
        initRole();
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
        tableData.setApi((searchForm) -> userService.getPageList(searchForm));
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
        // 当前登录用户是否拥有超级管理员角色
        AtomicBoolean currentUserHasAdministrator = new AtomicBoolean(false);
        authService.hasAdministrator().successAndThen(result -> currentUserHasAdministrator.set(result.getData()));
        // 获取当前登录用户
        final Result<User> userResult = authService.getCurrentAuthUser();
        if (!userResult.isSuccess()) {
            Alert.error(userResult.getMsg());
            return;
        }
        final User authUser = userResult.getData();
        tableData.setOperateCallback((tableColumn, tableCell) -> {
            final JFXButton detailBtn = createBtn.get("详情", ICON_DETAIL, "jfx-button-flat", "user:detail");
            final JFXButton editBtn = createBtn.get("编辑", ICON_EDIT, "jfx-button", "user:edit");
            final JFXButton roleBtn = createBtn.get("分配角色", ICON_ROLE, "jfx-button-primary", "user:allocation:roles");
            final JFXButton deleteBtn = createBtn.get("删除", ICON_DELETE, "jfx-button-danger", "user:delete");
            detailBtn.setOnAction(event -> detailAction(tableColumn, tableCell));
            editBtn.setOnAction(event -> editAction(tableColumn, tableCell));
            roleBtn.setOnAction(event -> roleAction(tableColumn, tableCell));
            deleteBtn.setOnAction(event -> deleteAction(tableColumn, tableCell));
            final HBox node = new HBox(detailBtn, editBtn, roleBtn, deleteBtn);
            node.setSpacing(10);
            node.setAlignment(Pos.CENTER);
            // 超级管理员拥有所有有权限操作
            if (currentUserHasAdministrator.get()) {
                return node;
            }
            final ListSchema listSchema = tableCell.getTableView().getItems().get(tableCell.getIndex());
            // 1、非超级管理员不能删除自己
            if (authUser.getId().equals(listSchema.getId())) {
                deleteBtn.setManaged(false);
                deleteBtn.setVisible(false);
            }
            // 2、非超级管理员不允许删除超级管理员
            if (hasAdministrator(listSchema.getRoles())) {
                deleteBtn.setManaged(false);
                deleteBtn.setVisible(false);
            }
            return node;
        });
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
     * 初始化表单
     */
    private void initForm() {
        // 设置表单约束
        form.initFormSchema(FormSchema.class);
        // 提交API接口
        form.setApi(userService::saveOrUpdateUser, User.class);
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
     * 初始化分配角色窗口
     */
    private void initRole() {
        // 初始化selector
        selector.setSelectorItemFactory(role -> role.getName() + " " + role.getCode()) // SelectorItem工厂
            .setSearchFieldName(CollUtil.newArrayList(Role::getName, Role::getCode)) // 设置需要搜索的字段
            .setApi(searchForm -> {
                // 过滤未启用的角色
                searchForm.addWhereItem(WhereItem.whereItem(Role::getStatus, Condition.EQ, Status.ENABLE));
                return roleService.getPageList(searchForm);
            }); // 设置数据api接口
        // 提交分配角色事件
        roleSubmitBtn.setOnAction(event -> {
            if (StrUtil.isBlank(userId)) {
                Alert.error("请选择要分配角色的用户");
                return;
            }
            final List<Role> roles = selector.getSelectedData();
            Confirm.buildConfirm().okAction(okEvent -> {
                final List<String> roleIds = roles.stream().map(Role::getId).collect(Collectors.toList());
                userService.setUserRoles(userId, roleIds)
                    .successAndThen(result -> Alert.info(result.getMsg()))
                    .failedAndThen((code, msg) -> Alert.error(msg));
                selector.clearSelection(); // 清空选择
                roleDialog.close();
            }).warn("确认分配？", String.format(Locale.ROOT, "确认分配已选择的%d个角色？", roles.size()));
        });
        // 分配角色取消事件
        roleCancelBtn.setOnAction(event -> roleDialog.close());
    }

    /**
     * 初始化添加按钮
     */
    private void initAddButton() {
        SecurityUtil.manage(addButton, "user:add"); // 权限管理
        addButton.setOnAction(event -> {
            form.setTitle("添加用户", IconUtil.getIcon(ICON_ADD));
            formDialog.show(rootPane);
        });
    }

    /**
     * 初始化批量删除按钮
     */
    private void initMultipleDeleteButton() {
        SecurityUtil.manage(multipleDeleteButton, "user:delete:batch"); // 权限管理
        multipleDeleteButton.getStyleClass().add("jfx-button-danger");
        multipleDeleteButton.setOnAction(event -> {
            final List<ListSchema> selectedItems = tableData.getSelectedItems();
            if (selectedItems.size() < 1) {
                Alert.warn("请选择要删除的数据");
                return;
            }
            final List<String> ids = selectedItems.stream().map(ListSchema::getId).collect(Collectors.toList());
            Confirm.buildConfirm().okAction(okEvent -> {
                userService.deleteUsers(ids)
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
        userService.getUser(listSchema.getId()).successAndThen(result -> {
            detail.setTitle("用户详情", IconUtil.getIcon(ICON_DETAIL));
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
        userService.getUser(listSchema.getId()).successAndThen(result -> {
            form.setTitle("编辑用户", IconUtil.getIcon(ICON_EDIT));
            form.setFormData(result.getData());
            formDialog.show(rootPane);
        }).failedAndThen((code, msg) -> Alert.error(msg));
    }

    /**
     * 分配角色
     *
     * @param tableColumn table column
     * @param tableCell table cell
     */
    private void roleAction(TableColumn<ListSchema, Node> tableColumn, TableCell<ListSchema, Node> tableCell) {
        final ListSchema listSchema = tableCell.getTableView().getItems().get(tableCell.getIndex());
        userId = listSchema.getId();
        userService.getUserRoles(userId).successAndThen(result -> {
            selector.setSelectedData(result.getData()); // 设置选中数据
            roleDialog.show(rootPane);
        }).failedAndThen((code, msg) -> Alert.error(msg));
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
            userService.deleteUsers(CollUtil.newArrayList(listSchema.getId()))
                .successAndThen(result -> Alert.info(result.getMsg()))
                .failedAndThen((code, msg) -> Alert.error(msg));
            tableData.refreshTable();
        }).warn("确认删除？");
    }
}
