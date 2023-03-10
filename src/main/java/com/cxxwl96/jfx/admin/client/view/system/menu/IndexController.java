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

    // ?????????flowHandler
    private FlowHandler detailHandler;

    // ?????????flowHandler
    private FlowHandler formHandler;

    // ????????????
    private final ContextMenu contextMenu = new ContextMenu();

    @Autowired
    private MenuServiceImpl menuService;

    @Autowired
    private AuthServiceImpl authService;

    // ????????????
    private List<Menu> menus;

    // ?????????????????????
    private Menu currentSelectedMenu = null;

    // ?????????map
    private final Map<String, TreeItem<Node>> treeItemMap = new HashMap<>();

    @PostConstruct
    private void init() {
        // ?????????Dialog
        initDialog();
        // ?????????????????????
        initRightMenuItems();
        // ??????????????????
        initTreeView();
        // ????????????????????????
        initTreeSearch();
        // ?????????????????????
        initDetail();
    }

    /**
     * ?????????Dialog
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
     * ?????????????????????
     */
    private void initRightMenuItems() {
        // ??????????????????
        final Supplier4<MenuItem, String, String, String, EventHandler<ActionEvent>> item
            = (name, icon, authority, action) -> {
            final MenuItem menuItem = new MenuItem(name, IconUtil.getIcon(icon));
            menuItem.setOnAction(action);
            menuItem.visibleProperty().bind(SecurityUtil.visibleProperty(authority));
            return menuItem;
        };
        // ????????????
        List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(item.get("????????????", "icon-ic-baseline-add", "menu:add", event -> addMenuAction()));
        menuItems.add(item.get("??????", "icon-ic-baseline-edit", "menu:edit", event -> editMenuAction()));
        menuItems.add(item.get("??????", "icon-ic-baseline-delete-outline", "menu:delete", event -> deleteMenuAction()));
        if (SecurityUtil.visible("menu:add:submenu") || SecurityUtil.visible("menu:delete:submenu")) {
            menuItems.add(new SeparatorMenuItem());
        }
        menuItems.add(item.get("???????????????", "icon-ic-baseline-add-box", "menu:add:submenu", event -> addSubMenuAction()));
        menuItems.add(item.get("???????????????", "icon-ic-baseline-delete", "menu:delete:submenu", event -> delSubMenuAction()));
        contextMenu.getItems().addAll(menuItems);
        contextMenu.getStyleClass().add("backgroundPane");
    }

    /**
     * ??????????????????
     */
    private void initTreeView() {
        compressBtn.setGraphic(IconUtil.getIcon("compress"));
        compressBtn.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> compressAllAction());
        expandBtn.setGraphic(IconUtil.getIcon("expand"));
        expandBtn.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> expandAllAction());
        treeViewSpinner.visibleProperty().bind(treePane.disableProperty());
        // ??????menu??????????????????????????????
        refreshTreeView();
        // ?????????????????????
        treeView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            TreeItem<Node> item = treeView.getSelectionModel().getSelectedItem();
            final Menu menu = item == null ? null : (Menu) item.getValue().getUserData();
            // ????????????,??????????????????
            if (event.getButton() == MouseButton.SECONDARY) {
                // ???????????????????????????????????????
                if (currentSelectedMenu != menu) {
                    currentSelectedMenu = menu;
                }
                Node node = event.getPickResult().getIntersectedNode();
                contextMenu.show(node, event.getScreenX(), event.getScreenY());
                return;
            }
            // ???????????????????????????????????????????????????????????????
            if (currentSelectedMenu == menu) {
                treeView.getSelectionModel().clearSelection();
                currentSelectedMenu = null;
            } else {
                currentSelectedMenu = menu;
            }
            // ??????????????????
            if (menu == null) {
                return;
            }
            try {
                detailHandler.registerInFlowContext(menu); // ??????????????????
                detailHandler.handle(DetailController.SHOW_ACTION); // ??????????????????
            } catch (VetoException | FlowException exception) {
                log.error(exception.getMessage(), exception);
            }
        });
    }

    /**
     * ??????menu??????????????????????????????
     */
    private void refreshTreeView() {
        ProcessChain.create()
            .addRunnableInPlatformThread(() -> treePane.setDisable(true))
            .addSupplierInExecutor(() -> authService.getUserMenuTree())
            .addConsumerInPlatformThread(result -> result.successAndThen(res -> {
                this.menus = res.getData();
                initTreeData(this.menus);
            }).failedAndThen((code, msg) -> Alert.error("????????????????????????", "Code: " + code + System.lineSeparator() + msg)))
            .onException(exception -> {
                log.error(exception.getMessage(), exception);
                Alert.error("??????????????????", exception.getMessage());
            })
            .withFinal(() -> treePane.setDisable(false))
            .run();
    }

    /**
     * ????????????????????????
     */
    private void initTreeSearch() {
        treeSearchTextField.textProperty().addListener(event -> {
            final String text = treeSearchTextField.getText();
            if (StrUtil.isBlank(text)) {
                initTreeData(this.menus);
                compressAllAction(); // ??????????????????
                return;
            }
            List<Menu> menus = searchFromTree(this.menus, text);
            initTreeData(menus);
            expandAllAction(); // ??????????????????
        });
    }

    /**
     * ?????????????????????
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
     * ??????????????????
     */
    @ActionMethod("compressAll")
    private void compressAllAction() {
        expand(treeView.getRoot().getChildren(), false);
    }

    /**
     * ??????????????????
     */
    @ActionMethod("expandAll")
    private void expandAllAction() {
        expand(treeView.getRoot().getChildren(), true);
    }

    @ActionMethod("addMenu")
    private void addMenuAction() {
        try {
            formTitle.setText("????????????");
            formHandler.registerInFlowContext(Menu.class, null); // ??????????????????
            formHandler.handle(FormController.ADD_ACTION); // ??????????????????
            formDialog.show(rootPane); // ??????????????????
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
            formTitle.setText("????????????");
            formHandler.registerInFlowContext(Menu.class, currentSelectedMenu); // ??????????????????
            formHandler.handle(FormController.EDIT_ACTION); // ??????????????????
            formDialog.show(rootPane); // ??????????????????
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
        // ??????menu??????????????????????????????
        refreshTreeView();
    }

    @ActionMethod("addSubMenu")
    private void addSubMenuAction() {
        if (currentSelectedMenu == null) {
            log.warn("Please select the menu to do this again");
            return;
        }
        try {
            formTitle.setText("???????????????");
            formHandler.registerInFlowContext(Menu.class, currentSelectedMenu); // ??????????????????
            formHandler.handle(FormController.ADD_SUB_ACTION); // ??????????????????
            formDialog.show(rootPane); // ??????????????????
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
        // ??????menu??????????????????????????????
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
            // ??????menu??????????????????????????????
            refreshTreeView();
        }
    }

    @ActionMethod("formCancel")
    private void formCancelAction() {
        formDialog.close();
    }

    /**
     * ??????/??????????????????
     *
     * @param items ??????
     * @param expand ????????????
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
     * ????????????
     *
     * @param menus ??????
     * @param text ??????
     * @return ??????
     */
    private List<Menu> searchFromTree(List<Menu> menus, String text) {
        final ArrayList<Menu> list = new ArrayList<>();
        menus.forEach(menu -> {
            final Menu newMenu = new Menu();
            BeanUtil.copyProperties(menu, newMenu);
            // ???????????????????????????????????????
            if (CollUtil.isNotEmpty(newMenu.getChildren())) {
                final List<Menu> subList = searchFromTree(newMenu.getChildren(), text);
                if (CollUtil.isNotEmpty(subList)) {
                    newMenu.setChildren(subList);
                    list.add(newMenu);
                    return;
                }
            }
            // ??????????????????????????????????????????????????????????????????????????????
            if (searchFromTextByTextOrPinyin(newMenu.getTitle(), text)) {
                list.add(newMenu);
            }
        });
        return list;
    }

    /**
     * ??????????????????????????????
     *
     * @param source ?????????
     * @param searchText ???????????????
     * @return ???????????????
     */
    private boolean searchFromTextByTextOrPinyin(String source, String searchText) {
        final boolean containsTitle = source.toLowerCase(Locale.ROOT).contains(searchText.toLowerCase(Locale.ROOT));
        final boolean containsTitlePinyin = PinyinUtil.getPinyin(source)
            .toLowerCase(Locale.ROOT)
            .contains(searchText.toLowerCase(Locale.ROOT));
        return containsTitle || containsTitlePinyin;
    }

    /**
     * ?????????????????????
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
     * ?????????????????????
     *
     * @param menus ??????
     * @return ???????????????
     */
    private List<TreeItem<Node>> getTreeItemList(List<Menu> menus) {
        final ArrayList<TreeItem<Node>> treeItems = new ArrayList<>();
        if (menus == null) {
            return treeItems;
        }
        menus.forEach(menu -> {
            // ??????????????????
            final Label label = new Label();
            label.setText(menu.getTitle());
            label.setUserData(menu);
            final TreeItem<Node> item = new TreeItem<>();
            item.setValue(label);
            // ???????????????
            if (treeItemMap.containsKey(menu.getId())) {
                TreeItem<Node> oldItem = treeItemMap.get(menu.getId());
                item.setExpanded(oldItem.isExpanded());
            }
            treeItemMap.put(menu.getId(), item);
            // ????????????????????????????????????????????????
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
                // ??????????????????
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
