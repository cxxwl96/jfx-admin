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

    // ????????????
    private final JFXButton addButton = new JFXButton("??????", IconUtil.getIcon(ICON_ADD));

    // ??????????????????
    private final JFXButton multipleDeleteButton = new JFXButton("????????????", IconUtil.getIcon(ICON_DELETE));

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private RoleServiceImpl roleService;

    @Autowired
    private AuthServiceImpl authService;

    // ?????????????????????userId
    private String userId;

    @PostConstruct
    private void init() {
        // ?????????????????????
        initTableData();
        // ???????????????
        initForm();
        // ???????????????
        initDetail();
        // ???????????????????????????
        initRole();
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
        tableData.setApi((searchForm) -> userService.getPageList(searchForm));
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
        // ???????????????????????????????????????????????????
        AtomicBoolean currentUserHasAdministrator = new AtomicBoolean(false);
        authService.hasAdministrator().successAndThen(result -> currentUserHasAdministrator.set(result.getData()));
        // ????????????????????????
        final Result<User> userResult = authService.getCurrentAuthUser();
        if (!userResult.isSuccess()) {
            Alert.error(userResult.getMsg());
            return;
        }
        final User authUser = userResult.getData();
        tableData.setOperateCallback((tableColumn, tableCell) -> {
            final JFXButton detailBtn = createBtn.get("??????", ICON_DETAIL, "jfx-button-flat", "user:detail");
            final JFXButton editBtn = createBtn.get("??????", ICON_EDIT, "jfx-button", "user:edit");
            final JFXButton roleBtn = createBtn.get("????????????", ICON_ROLE, "jfx-button-primary", "user:allocation:roles");
            final JFXButton deleteBtn = createBtn.get("??????", ICON_DELETE, "jfx-button-danger", "user:delete");
            detailBtn.setOnAction(event -> detailAction(tableColumn, tableCell));
            editBtn.setOnAction(event -> editAction(tableColumn, tableCell));
            roleBtn.setOnAction(event -> roleAction(tableColumn, tableCell));
            deleteBtn.setOnAction(event -> deleteAction(tableColumn, tableCell));
            final HBox node = new HBox(detailBtn, editBtn, roleBtn, deleteBtn);
            node.setSpacing(10);
            node.setAlignment(Pos.CENTER);
            // ??????????????????????????????????????????
            if (currentUserHasAdministrator.get()) {
                return node;
            }
            final ListSchema listSchema = tableCell.getTableView().getItems().get(tableCell.getIndex());
            // 1???????????????????????????????????????
            if (authUser.getId().equals(listSchema.getId())) {
                deleteBtn.setManaged(false);
                deleteBtn.setVisible(false);
            }
            // 2???????????????????????????????????????????????????
            if (hasAdministrator(listSchema.getRoles())) {
                deleteBtn.setManaged(false);
                deleteBtn.setVisible(false);
            }
            return node;
        });
    }

    /**
     * ?????????????????????????????????
     *
     * @param roles ????????????
     * @return ?????????????????????????????????
     */
    private boolean hasAdministrator(List<Role> roles) {
        if (CollUtil.isEmpty(roles)) {
            return false;
        }
        return roles.stream().anyMatch(role -> role.getType() == RoleType.ADMINISTRATOR);
    }

    /**
     * ???????????????
     */
    private void initForm() {
        // ??????????????????
        form.initFormSchema(FormSchema.class);
        // ??????API??????
        form.setApi(userService::saveOrUpdateUser, User.class);
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
    private void initRole() {
        // ?????????selector
        selector.setSelectorItemFactory(role -> role.getName() + " " + role.getCode()) // SelectorItem??????
            .setSearchFieldName(CollUtil.newArrayList(Role::getName, Role::getCode)) // ???????????????????????????
            .setApi(searchForm -> {
                // ????????????????????????
                searchForm.addWhereItem(WhereItem.whereItem(Role::getStatus, Condition.EQ, Status.ENABLE));
                return roleService.getPageList(searchForm);
            }); // ????????????api??????
        // ????????????????????????
        roleSubmitBtn.setOnAction(event -> {
            if (StrUtil.isBlank(userId)) {
                Alert.error("?????????????????????????????????");
                return;
            }
            final List<Role> roles = selector.getSelectedData();
            Confirm.buildConfirm().okAction(okEvent -> {
                final List<String> roleIds = roles.stream().map(Role::getId).collect(Collectors.toList());
                userService.setUserRoles(userId, roleIds)
                    .successAndThen(result -> Alert.info(result.getMsg()))
                    .failedAndThen((code, msg) -> Alert.error(msg));
                selector.clearSelection(); // ????????????
                roleDialog.close();
            }).warn("???????????????", String.format(Locale.ROOT, "????????????????????????%d????????????", roles.size()));
        });
        // ????????????????????????
        roleCancelBtn.setOnAction(event -> roleDialog.close());
    }

    /**
     * ?????????????????????
     */
    private void initAddButton() {
        SecurityUtil.manage(addButton, "user:add"); // ????????????
        addButton.setOnAction(event -> {
            form.setTitle("????????????", IconUtil.getIcon(ICON_ADD));
            formDialog.show(rootPane);
        });
    }

    /**
     * ???????????????????????????
     */
    private void initMultipleDeleteButton() {
        SecurityUtil.manage(multipleDeleteButton, "user:delete:batch"); // ????????????
        multipleDeleteButton.getStyleClass().add("jfx-button-danger");
        multipleDeleteButton.setOnAction(event -> {
            final List<ListSchema> selectedItems = tableData.getSelectedItems();
            if (selectedItems.size() < 1) {
                Alert.warn("???????????????????????????");
                return;
            }
            final List<String> ids = selectedItems.stream().map(ListSchema::getId).collect(Collectors.toList());
            Confirm.buildConfirm().okAction(okEvent -> {
                userService.deleteUsers(ids)
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
        userService.getUser(listSchema.getId()).successAndThen(result -> {
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
        userService.getUser(listSchema.getId()).successAndThen(result -> {
            form.setTitle("????????????", IconUtil.getIcon(ICON_EDIT));
            form.setFormData(result.getData());
            formDialog.show(rootPane);
        }).failedAndThen((code, msg) -> Alert.error(msg));
    }

    /**
     * ????????????
     *
     * @param tableColumn table column
     * @param tableCell table cell
     */
    private void roleAction(TableColumn<ListSchema, Node> tableColumn, TableCell<ListSchema, Node> tableCell) {
        final ListSchema listSchema = tableCell.getTableView().getItems().get(tableCell.getIndex());
        userId = listSchema.getId();
        userService.getUserRoles(userId).successAndThen(result -> {
            selector.setSelectedData(result.getData()); // ??????????????????
            roleDialog.show(rootPane);
        }).failedAndThen((code, msg) -> Alert.error(msg));
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
            userService.deleteUsers(CollUtil.newArrayList(listSchema.getId()))
                .successAndThen(result -> Alert.info(result.getMsg()))
                .failedAndThen((code, msg) -> Alert.error(msg));
            tableData.refreshTable();
        }).warn("???????????????");
    }
}
