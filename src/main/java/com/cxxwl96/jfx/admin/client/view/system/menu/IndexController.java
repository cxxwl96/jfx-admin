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
import com.cxxwl96.jfx.admin.client.utils.Alert;
import com.cxxwl96.jfx.admin.client.utils.IconUtil;
import com.cxxwl96.jfx.admin.client.utils.SecurityUtil;
import com.cxxwl96.jfx.admin.server.entity.Menu;
import com.cxxwl96.jfx.admin.server.enums.MenuType;
import com.cxxwl96.jfx.admin.server.lang.Result;
import com.cxxwl96.jfx.admin.server.service.impl.AuthServiceImpl;
import com.cxxwl96.jfx.admin.server.service.impl.MenuServiceImpl;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Supplier4;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.action.ActionMethod;
import io.datafx.controller.flow.action.ActionTrigger;
import io.datafx.controller.util.VetoException;
import io.datafx.core.concurrent.ProcessChain;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;

/**
 * MenuController
 *
 * @author cxxwl96
 * @since 2022/9/23 21:35
 */
@Slf4j
@ViewController("/views/system/menu/Index.fxml")
public class IndexController extends AbstractController {
    @FXML
    private StackPane rootPane;

    @FXML
    private VBox treePane;

    @FXML
    private JFXTextField treeSearchTextField;

    @FXML
    @ActionTrigger("compressAll")
    private Label compressBtn;

    @FXML
    @ActionTrigger("expandAll")
    private Label expandBtn;

    @FXML
    private TreeView<Node> treeView;

    @FXML
    private JFXSpinner treeViewSpinner;

    @FXML
    private StackPane detailPane;

    @FXML
    private JFXDialog formDialog;

    @FXML
    private JFXDialogLayout formDialogLayout;

    @FXML
    private Label formTitle;

    @FXML
    private JFXProgressBar JFXProgressBar;

    @FXML
    @ActionTrigger("formSave")
    private JFXButton saveBtn;

    @FXML
    @ActionTrigger("formCancel")
    private JFXButton cancelBtn;

    // 详情页flowHandler
    private FlowHandler detailHandler;

    // 表单页flowHandler
    private FlowHandler formHandler;

    // 右键菜单
    private final ContextMenu contextMenu = new ContextMenu();

    @Autowired
    private MenuServiceImpl menuService;

    @Autowired
    private AuthServiceImpl authService;

    // 所有菜单
    private List<Menu> menus;

    // 当前选中的菜单
    private Menu currentSelectedMenu = null;

    // 菜单项map
    private final Map<String, TreeItem<Node>> treeItemMap = new HashMap<>();

    @PostConstruct
    private void init() {
        // 初始化Dialog
        initDialog();
        // 初始化右键菜单
        initRightMenuItems();
        // 初始化菜单树
        initTreeView();
        // 初始化菜单树搜索
        initTreeSearch();
        // 初始化菜单详情
        initDetail();
    }

    /**
     * 初始化Dialog
     */
    private void initDialog() {
        try {
            formHandler = new Flow(FormController.class).createHandler();
            final StackPane stackPane = formHandler.start();
            formDialogLayout.getBody().add(stackPane);
        } catch (FlowException exception) {
            log.error(exception.getMessage(), exception);
        }
    }

    /**
     * 初始化右键菜单
     */
    private void initRightMenuItems() {
        // 创建菜单方法
        final Supplier4<MenuItem, String, String, String, EventHandler<ActionEvent>> item
            = (name, icon, authority, action) -> {
            final MenuItem menuItem = new MenuItem(name, IconUtil.getIcon(icon));
            menuItem.setOnAction(action);
            menuItem.visibleProperty().bind(SecurityUtil.visibleProperty(authority));
            return menuItem;
        };
        // 右键菜单
        List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(item.get("添加菜单", "icon-ic-baseline-add", "menu:add", event -> addMenuAction()));
        menuItems.add(item.get("编辑", "icon-ic-baseline-edit", "menu:edit", event -> editMenuAction()));
        menuItems.add(item.get("删除", "icon-ic-baseline-delete-outline", "menu:delete", event -> deleteMenuAction()));
        if (SecurityUtil.visible("menu:add:submenu") || SecurityUtil.visible("menu:delete:submenu")) {
            menuItems.add(new SeparatorMenuItem());
        }
        menuItems.add(item.get("添加子菜单", "icon-ic-baseline-add-box", "menu:add:submenu", event -> addSubMenuAction()));
        menuItems.add(item.get("删除子菜单", "icon-ic-baseline-delete", "menu:delete:submenu", event -> delSubMenuAction()));
        contextMenu.getItems().addAll(menuItems);
        contextMenu.getStyleClass().add("backgroundPane");
    }

    /**
     * 初始化菜单树
     */
    private void initTreeView() {
        compressBtn.setGraphic(IconUtil.getIcon("compress"));
        compressBtn.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> compressAllAction());
        expandBtn.setGraphic(IconUtil.getIcon("expand"));
        expandBtn.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> expandAllAction());
        treeViewSpinner.visibleProperty().bind(treePane.disableProperty());
        // 请求menu菜单列表并刷新菜单树
        refreshTreeView();
        // 菜单树点击事件
        treeView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            TreeItem<Node> item = treeView.getSelectionModel().getSelectedItem();
            final Menu menu = item == null ? null : (Menu) item.getValue().getUserData();
            // 鼠标右键,显示右键菜单
            if (event.getButton() == MouseButton.SECONDARY) {
                // 不同菜单保存当前点击的菜单
                if (currentSelectedMenu != menu) {
                    currentSelectedMenu = menu;
                }
                Node node = event.getPickResult().getIntersectedNode();
                contextMenu.show(node, event.getScreenX(), event.getScreenY());
                return;
            }
            // 相同的菜单清空选中，否则保存当前点击的菜单
            if (currentSelectedMenu == menu) {
                treeView.getSelectionModel().clearSelection();
                currentSelectedMenu = null;
            } else {
                currentSelectedMenu = menu;
            }
            // 显示菜单详情
            if (menu == null) {
                return;
            }
            try {
                detailHandler.registerInFlowContext(menu); // 注入菜单数据
                detailHandler.handle(DetailController.SHOW_ACTION); // 调用显示方法
            } catch (VetoException | FlowException exception) {
                log.error(exception.getMessage(), exception);
            }
        });
    }

    /**
     * 请求menu菜单列表并刷新菜单树
     */
    private void refreshTreeView() {
        ProcessChain.create()
            .addRunnableInPlatformThread(() -> treePane.setDisable(true))
            .addSupplierInExecutor(() -> authService.getUserMenuTree())
            .addConsumerInPlatformThread(result -> result.successAndThen(res -> {
                this.menus = res.getData();
                initTreeData(this.menus);
            }).failedAndThen((code, msg) -> Alert.error("获取用户菜单失败", "Code: " + code + System.lineSeparator() + msg)))
            .onException(exception -> {
                log.error(exception.getMessage(), exception);
                Alert.error("发生未知错误", exception.getMessage());
            })
            .withFinal(() -> treePane.setDisable(false))
            .run();
    }

    /**
     * 初始化菜单树搜索
     */
    private void initTreeSearch() {
        treeSearchTextField.textProperty().addListener(event -> {
            final String text = treeSearchTextField.getText();
            if (StrUtil.isBlank(text)) {
                initTreeData(this.menus);
                compressAllAction(); // 收缩所有菜单
                return;
            }
            List<Menu> menus = searchFromTree(this.menus, text);
            initTreeData(menus);
            expandAllAction(); // 展开所有菜单
        });
    }

    /**
     * 初始化菜单详情
     */
    private void initDetail() {
        try {
            Flow detailFlow = new Flow(DetailController.class);
            detailHandler = detailFlow.createHandler();
            detailHandler.startInPane(detailPane);
        } catch (FlowException exception) {
            log.error(exception.getMessage(), exception);
        }
    }

    /**
     * 收缩所有菜单
     */
    @ActionMethod("compressAll")
    private void compressAllAction() {
        expand(treeView.getRoot().getChildren(), false);
    }

    /**
     * 展开所有菜单
     */
    @ActionMethod("expandAll")
    private void expandAllAction() {
        expand(treeView.getRoot().getChildren(), true);
    }

    @ActionMethod("addMenu")
    private void addMenuAction() {
        try {
            formTitle.setText("添加菜单");
            formHandler.registerInFlowContext(Menu.class, null); // 注入菜单数据
            formHandler.handle(FormController.ADD_ACTION); // 调用显示方法
            formDialog.show(rootPane); // 显示表单窗口
        } catch (VetoException | FlowException exception) {
            log.error(exception.getMessage(), exception);
        }
    }

    @ActionMethod("editMenu")
    private void editMenuAction() {
        if (currentSelectedMenu == null) {
            log.warn("Please select the menu to do this again");
            return;
        }
        try {
            formTitle.setText("编辑菜单");
            formHandler.registerInFlowContext(Menu.class, currentSelectedMenu); // 注入菜单数据
            formHandler.handle(FormController.EDIT_ACTION); // 调用显示方法
            formDialog.show(rootPane); // 显示表单窗口
        } catch (VetoException | FlowException exception) {
            log.error(exception.getMessage(), exception);
        }
    }

    @ActionMethod("deleteMenu")
    private void deleteMenuAction() {
        if (currentSelectedMenu == null) {
            log.warn("Please select the menu to do this again");
            return;
        }
        final Result<Boolean> result = menuService.deleteMenu(currentSelectedMenu.getId(), true);
        if (result.isSuccess()) {
            Alert.info(result.getMsg());
        } else {
            Alert.error(result.getMsg());
        }
        // 请求menu菜单列表并刷新菜单树
        refreshTreeView();
    }

    @ActionMethod("addSubMenu")
    private void addSubMenuAction() {
        if (currentSelectedMenu == null) {
            log.warn("Please select the menu to do this again");
            return;
        }
        try {
            formTitle.setText("添加子菜单");
            formHandler.registerInFlowContext(Menu.class, currentSelectedMenu); // 注入菜单数据
            formHandler.handle(FormController.ADD_SUB_ACTION); // 调用显示方法
            formDialog.show(rootPane); // 显示表单窗口
        } catch (VetoException | FlowException exception) {
            log.error(exception.getMessage(), exception);
        }
    }

    @ActionMethod("deleteSubMenu")
    private void delSubMenuAction() {
        if (currentSelectedMenu == null) {
            log.warn("Please select the menu to do this again");
            return;
        }
        final Result<Boolean> result = menuService.deleteMenu(currentSelectedMenu.getId(), false);
        if (result.isSuccess()) {
            Alert.info(result.getMsg());
        } else {
            Alert.error(result.getMsg());
        }
        // 请求menu菜单列表并刷新菜单树
        refreshTreeView();
    }

    @ActionMethod("formSave")
    private void formSaveAction() {
        try {
            formHandler.handle(FormController.SUBMIT_ACTION);
            formDialog.close();
        } catch (VetoException | FlowException exception) {
            log.error(exception.getMessage());
        } finally {
            // 请求menu菜单列表并刷新菜单树
            refreshTreeView();
        }
    }

    @ActionMethod("formCancel")
    private void formCancelAction() {
        formDialog.close();
    }

    /**
     * 展开/收缩所有菜单
     *
     * @param items 菜单
     * @param expand 是否展开
     */
    private void expand(List<TreeItem<Node>> items, boolean expand) {
        items.forEach(item -> {
            if (!item.isLeaf()) {
                item.setExpanded(expand);
                expand(item.getChildren(), expand);
            }
        });
    }

    /**
     * 搜索菜单
     *
     * @param menus 菜单
     * @param text 文本
     * @return 菜单
     */
    private List<Menu> searchFromTree(List<Menu> menus, String text) {
        final ArrayList<Menu> list = new ArrayList<>();
        menus.forEach(menu -> {
            final Menu newMenu = new Menu();
            BeanUtil.copyProperties(menu, newMenu);
            // 如果有子菜单则继续向下搜索
            if (CollUtil.isNotEmpty(newMenu.getChildren())) {
                final List<Menu> subList = searchFromTree(newMenu.getChildren(), text);
                if (CollUtil.isNotEmpty(subList)) {
                    newMenu.setChildren(subList);
                    list.add(newMenu);
                    return;
                }
            }
            // 没有子菜单则判断当前菜单是否满足文本包含以及拼音包含
            if (searchFromTextByTextOrPinyin(newMenu.getTitle(), text)) {
                list.add(newMenu);
            }
        });
        return list;
    }

    /**
     * 按文本或拼音搜索文本
     *
     * @param source 源文本
     * @param searchText 搜索关键字
     * @return 是否搜索到
     */
    private boolean searchFromTextByTextOrPinyin(String source, String searchText) {
        final boolean containsTitle = source.toLowerCase(Locale.ROOT).contains(searchText.toLowerCase(Locale.ROOT));
        final boolean containsTitlePinyin = PinyinUtil.getPinyin(source)
            .toLowerCase(Locale.ROOT)
            .contains(searchText.toLowerCase(Locale.ROOT));
        return containsTitle || containsTitlePinyin;
    }

    /**
     * 初始化菜单数据
     */
    private void initTreeData(List<Menu> menus) {
        currentSelectedMenu = null;
        List<TreeItem<Node>> treeItems = getTreeItemList(menus);
        final TreeItem<Node> rootItem = new TreeItem<>();
        rootItem.getChildren().addAll(treeItems);
        treeView.setRoot(rootItem);
        treeView.setShowRoot(false);
    }

    /**
     * 创建菜单树视图
     *
     * @param menus 菜单
     * @return 菜单树视图
     */
    private List<TreeItem<Node>> getTreeItemList(List<Menu> menus) {
        final ArrayList<TreeItem<Node>> treeItems = new ArrayList<>();
        if (menus == null) {
            return treeItems;
        }
        menus.forEach(menu -> {
            // 构建菜单按钮
            final Label label = new Label();
            label.setText(menu.getTitle());
            label.setUserData(menu);
            final TreeItem<Node> item = new TreeItem<>();
            item.setValue(label);
            // 保存菜单项
            if (treeItemMap.containsKey(menu.getId())) {
                TreeItem<Node> oldItem = treeItemMap.get(menu.getId());
                item.setExpanded(oldItem.isExpanded());
            }
            treeItemMap.put(menu.getId(), item);
            // 若搜索框包含文本，则使文本呈红色
            if (StrUtil.isNotBlank(treeSearchTextField.getText()) && searchFromTextByTextOrPinyin(menu.getTitle(),
                treeSearchTextField.getText())) {
                label.setStyle("-fx-text-fill: red");
            }
            if (menu.getType() == MenuType.DIRECTORY) {
                label.setGraphic(IconUtil.getIcon("folder"));
            } else if (menu.getType() == MenuType.MENU) {
                label.setGraphic(IconUtil.getIcon("file-empty"));
            } else if (menu.getType() == MenuType.BUTTON) {
                label.setGraphic(IconUtil.getIcon("toggle-off"));
            } else {
                throw new IllegalStateException("Invalid menu type: " + menu.getType() + ".");
            }
            if (CollUtil.isNotEmpty(menu.getChildren())) {
                final List<TreeItem<Node>> subTreeItems = getTreeItemList(menu.getChildren());
                item.getChildren().addAll(subTreeItems);
                // 设置父级菜单
                subTreeItems.forEach(subTreeItem -> {
                    final Menu subMenu = (Menu) subTreeItem.getValue().getUserData();
                    if (subMenu != null) {
                        subMenu.setParentMenu(menu);
                    }
                });
            }
            treeItems.add(item);
        });
        return treeItems;
    }
}
