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
import com.cxxwl96.jfx.admin.client.component.JFXLoading;
import com.cxxwl96.jfx.admin.client.component.WebViewController;
import com.cxxwl96.jfx.admin.client.component.iconswitch.IconSwitch;
import com.cxxwl96.jfx.admin.client.utils.Alert;
import com.cxxwl96.jfx.admin.client.utils.Browser;
import com.cxxwl96.jfx.admin.client.utils.IconUtil;
import com.cxxwl96.jfx.admin.server.entity.Menu;
import com.cxxwl96.jfx.admin.server.entity.Role;
import com.cxxwl96.jfx.admin.server.entity.User;
import com.cxxwl96.jfx.admin.server.enums.MenuType;
import com.cxxwl96.jfx.admin.server.enums.RoleType;
import com.cxxwl96.jfx.admin.server.enums.Status;
import com.cxxwl96.jfx.admin.server.service.impl.AuthServiceImpl;
import com.cxxwl96.jfx.admin.server.service.impl.UserServiceImpl;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawersStack;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXTabPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.action.ActionMethod;
import io.datafx.controller.flow.action.ActionTrigger;
import io.datafx.controller.flow.container.AnimatedFlowContainer;
import io.datafx.controller.flow.container.ContainerAnimations;
import io.datafx.controller.flow.context.ActionHandler;
import io.datafx.controller.flow.context.FlowActionHandler;
import io.datafx.controller.util.VetoException;
import io.datafx.core.concurrent.ProcessChain;
import javafx.animation.Transition;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * IndexController
 *
 * @author cxxwl96
 * @since 2022/9/16 23:44
 */
@Slf4j
@ViewController("/views/frame/Index.fxml")
public class IndexController extends AbstractController {
    @ActionHandler
    private FlowActionHandler actionHandler;

    //菜单按钮
    @FXML
    private JFXHamburger navigationButton;

    //主页按钮
    @FXML
    @ActionTrigger("goHome")
    private JFXButton homeButton;

    // 日期选择器
    @FXML
    private JFXDatePicker datePicker;

    // 主题切换按钮
    @FXML
    @ActionTrigger("showSkinPane")
    private IconSwitch iconSwitch;

    // 通知按钮
    @FXML
    private JFXButton bellButton;

    @FXML
    private Label userLabel;

    @FXML
    private Label roleLabel;

    // 用户按钮
    @FXML
    private JFXButton userButton;

    private final JFXPopup userPopup = new JFXPopup();

    // 左侧抽屉
    private final JFXDrawer leftDrawer = new JFXDrawer();

    // 左侧导航菜单
    private final TreeView<Node> treeView = new TreeView<>();

    // tab内容
    @FXML
    private JFXDrawersStack drawersStack;

    //内容面板tabPane
    @FXML
    private JFXTabPane tabPane;

    // 菜单集合 <menuId, TreeItem>
    private final Map<String, TreeItem<Node>> treeItemMap = new TreeMap<>();

    // tab集合 <menuId, Tab>
    private final Map<String, Tab> tabMap = new HashMap<>();

    // loading
    private final JFXLoading loading = new JFXLoading();

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private UserServiceImpl userService;

    @PostConstruct
    private void postConstruct() throws Exception {
        // 初始化stage
        initStage();
        // 初始化header
        initHeader();
        // 初始化leftSider视图
        initLeftSider();
        // 初始化左侧菜单
        initNavigation();
        // 初始化tabPane
        initTabPane();
        // 将this注入datafx容器
        ApplicationStore.registerBean(this);
    }

    @ActionMethod("goHome")
    private void goHome() {
        treeItemMap.forEach((id, item) -> {
            final Menu menu = (Menu) item.getValue().getUserData();
            if (menu == null) {
                return;
            }
            // 如果是菜单并且是主菜单则选中
            if (menu.getType() == MenuType.MENU && menu.getMain() && tabMap.containsKey(id)) {
                final Tab tab = tabMap.get(id);
                tabPane.getSelectionModel().select(tab);
            }
        });
    }

    /**
     * 初始化装饰器
     */
    private void initStage() {
        // 设置可调整大小
        Stage stage = ApplicationStore.getRootStage().orElseThrow(() -> new IllegalStateException("window is null"));
        stage.setWidth(1000);
        stage.setHeight(750);
    }

    /**
     * 初始化header
     *
     * @throws Exception exception
     */
    private void initHeader() throws Exception {
        // 初始化按钮图标
        datePicker.setValue(LocalDate.now());
        // 初始化切换主题
        iconSwitch.selectedProperty().bindBidirectional(ApplicationStore.darkThemeProperty());
        iconSwitch.selectedProperty().addListener((observable, oldValue, newValue) -> {
            try {
                showSkinPane();
            } catch (VetoException | FlowException exception) {
                log.error(exception.getMessage(), exception);
            }
        });
        // 初始化用户信息
        authService.getCurrentAuthUser().successAndThen(authResult -> {
            final User authUser = authResult.getData();
            // 设置用户名
            userLabel.setText(StrUtil.isNotBlank(authUser.getName()) ? authUser.getName() : authUser.getUsername());
            userService.getUserRoles(authUser.getId()).successAndThen(userResult -> {
                final List<Role> roles = userResult.getData();
                // 获取角色类型最大的类型值
                final Integer typeValue = roles.stream()
                    .map(Role::getType)
                    .map(RoleType::getValue)
                    .max(Integer::compareTo)
                    .orElse(-1);
                RoleType.valueOf(typeValue).ifPresent(roleType -> {
                    // 设置角色
                    roleLabel.setText("[" + roleType.getRemark() + "]");
                });
            });
        });
        // 用户下拉菜单
        try {
            final Flow flow = new Flow(UserListViewController.class);
            final StackPane stackPane = flow.start();
            final JFXListView<?> listView = ((JFXListView<?>) stackPane.getChildren().get(0));
            listView.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
                listView.getSelectionModel().clearSelection();
                userPopup.hide();
            });
            userPopup.setPopupContent(stackPane);
        } catch (FlowException exception) {
            log.error(exception.getMessage(), exception);
        }
        userButton.setOnAction(
            event -> userPopup.show(userButton, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.RIGHT));
    }

    /**
     * 初始化leftSider视图
     */
    private void initLeftSider() {
        // 初始化左侧菜单抽屉
        leftDrawer.setDefaultDrawerSize(240);
        leftDrawer.setResizeContent(true);
        leftDrawer.setOverLayVisible(false);
        leftDrawer.setResizableOnDrag(true);
        leftDrawer.setOnDrawerOpening(event -> {
            final Transition animation = navigationButton.getAnimation();
            animation.setRate(1);
            animation.play();
        });
        leftDrawer.setOnDrawerClosing(event -> {
            final Transition animation = navigationButton.getAnimation();
            animation.setRate(-1);
            animation.play();
        });
        navigationButton.setOnMouseClicked(event -> drawersStack.toggle(leftDrawer));
    }

    /**
     * 初始化左侧菜单
     */
    private void initNavigation() {
        authService.getUserMenuTree().successAndThen(result -> {
            List<TreeItem<Node>> treeItems = getTreeItemList(result.getData());
            final TreeItem<Node> rootItem = new TreeItem<>();
            rootItem.getChildren().addAll(treeItems);
            treeView.setRoot(rootItem);
            treeView.setShowRoot(false);
            treeView.setId("sider-menu");
            // 添加菜单点击事件
            treeView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                final TreeItem<Node> item = treeView.getSelectionModel().getSelectedItem();
                if (item == null) {
                    return;
                }
                final Menu menu = (Menu) item.getValue().getUserData();
                if (menu == null || menu.getType() != MenuType.MENU) {
                    return;
                }
                // 添加tab
                addTab(menu);
            });
            leftDrawer.setSidePane(treeView);
        }).failedAndThen((code, msg) -> Alert.error("获取用户菜单失败", "Code: " + code + System.lineSeparator() + msg));
    }

    /**
     * 初始化tabPane
     */
    private void initTabPane() {
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
    }

    @ActionMethod("showSkinPane")
    private void showSkinPane() throws VetoException, FlowException {
        ProcessChain.create().addRunnableInPlatformThread(() -> {
            if (iconSwitch.isSelected()) {
                iconSwitch.getScene().getStylesheets().removeAll(ApplicationStore.getLightThemeResources());
                iconSwitch.getScene().getStylesheets().addAll(ApplicationStore.getDarkThemeResources());
            } else {
                iconSwitch.getScene().getStylesheets().removeAll(ApplicationStore.getDarkThemeResources());
                iconSwitch.getScene().getStylesheets().addAll(ApplicationStore.getLightThemeResources());
            }
        }).run();
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
            // 没有启用menu、menu隐藏、menu类型为按钮则跳过
            if (menu.getStatus() == Status.DISABLE || menu.getHide() || menu.getType() == MenuType.BUTTON) {
                return;
            }
            // 构建菜单按钮
            Node menuButton = createMenuButton(menu);
            final TreeItem<Node> item = new TreeItem<>();
            item.setValue(menuButton);
            treeItemMap.put(menu.getId(), item);
            if (menu.getType() == MenuType.DIRECTORY) {
                if (CollUtil.isNotEmpty(menu.getChildren())) {
                    final List<TreeItem<Node>> subTreeItems = getTreeItemList(menu.getChildren());
                    item.getChildren().addAll(subTreeItems);
                }
                treeItems.add(item);
                // 单击展开
                menuButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> item.setExpanded(!item.isExpanded()));
            } else if (menu.getType() == MenuType.MENU) {
                // 默认菜单添加到pane,并设置不可关闭
                if (menu.getMain()) {
                    addTab(menu).ifPresent(tab -> tab.setClosable(false));
                }
                treeItems.add(item);
            } else {
                throw new IllegalStateException("Invalid menu type: " + menu.getType() + ".");
            }
        });
        return treeItems;
    }

    /**
     * 构建菜单按钮
     *
     * @param menu menu
     * @return 菜单按钮
     */
    private Node createMenuButton(Menu menu) {
        final Label label = new Label(menu.getTitle(), IconUtil.getIcon(menu.getIcon()));
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        HBox.setHgrow(label, Priority.ALWAYS);
        label.setStyle("-fx-font-family: 'Microsoft YaHei'; -fx-font-size: 14;");
        final Pane centerPane = new Pane();
        centerPane.setMaxWidth(Double.MAX_VALUE);
        centerPane.setMaxHeight(Double.MAX_VALUE);
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        final HBox hBox = new HBox();
        hBox.setCursor(Cursor.HAND);
        hBox.getChildren().addAll(label, centerPane);
        hBox.setStyle("-fx-pref-height: 45; -fx-alignment: center; -fx-padding: 0 10 0 0;");
        hBox.setUserData(menu); // 将menu数据存入userData
        if (menu.getType() == MenuType.DIRECTORY && CollUtil.isNotEmpty(menu.getChildren())) {
            hBox.getChildren().add(IconUtil.getIcon("ellipsis-v"));
        }
        return hBox;
    }

    /**
     * 添加菜单到TabPane
     *
     * @param menu 菜单
     * @return Tab
     */
    @SneakyThrows
    public Optional<Tab> addTab(Menu menu) {
        if (StrUtil.isBlank(menu.getResourceUrl())) {
            return Optional.empty();
        }
        if (menu.getHttpUrl()) {
            if (menu.getHttpUrlBlank()) { // 是否是外链
                Browser.openHttpInBrowser(menu.getResourceUrl());
                return Optional.empty();
            }
            // WebViewController
            final Flow flow = new Flow(WebViewController.class);
            final Tab tab = addTab(menu.getId(), menu.getTitle(), IconUtil.getIcon(menu.getIcon()), flow, menu);
            return Optional.of(tab);
        }
        Tab tab = null;
        try {
            final Class<?> controllerClass = Class.forName(menu.getResourceUrl());
            tab = addTab(menu.getId(), menu.getTitle(), IconUtil.getIcon(menu.getIcon()), new Flow(controllerClass),
                menu);
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
        }
        return Optional.ofNullable(tab);
    }

    /**
     * 添加菜单到TabPane
     *
     * @param id 菜单唯一ID
     * @param title 标题
     * @param icon icon
     * @param flow flow
     * @return Tab
     */
    public Tab addTab(String id, String title, Node icon, Flow flow) {
        return addTab(id, title, icon, flow, null);
    }

    private Tab addTab(@NotNull String id, @NotNull String title, @NotNull Node icon, @NotNull Flow flow,
        @Nullable Menu menu) {
        Tab tab = tabMap.get(id);
        if (tab == null) {
            FlowHandler flowHandler = flow.createHandler();
            tab = new Tab(title);
            tab.setGraphic(icon);
            if (menu != null && menu.getId() != null) {
                flowHandler.getFlowContext().register(menu);
                tab.setClosable(!menu.getMain());
            }
            // 添加选中事件
            tab.setOnSelectionChanged(event -> {
                if (treeItemMap.get(id) != null) {
                    treeView.getSelectionModel().select(treeItemMap.get(id));
                }
            });
            // 添加关闭事件
            tab.setOnClosed(event -> {
                try {
                    flowHandler.getCurrentViewContext().destroy();
                } catch (IllegalAccessException | InvocationTargetException exception) {
                    log.error(exception.getMessage(), exception);
                }
            });
            tabMap.put(id, tab);
            // 设置tab内容
            Tab finalTab = tab;
            ProcessChain.create().addRunnableInPlatformThread(() -> {
                finalTab.setContent(loading);
            }).addSupplierInPlatformThread(() -> {
                try {
                    StackPane node = flowHandler.start(
                        new AnimatedFlowContainer(Duration.millis(320), ContainerAnimations.SWIPE_LEFT));
                    node.getStyleClass().addAll("tab-content");
                    return node;
                } catch (FlowException exception) {
                    log.error(exception.getMessage(), exception);
                }
                return null;
            }).addConsumerInPlatformThread(node -> {
                if (node != null) {
                    finalTab.setContent(node);
                }
            }).onException(exception -> Alert.error("发生未知异常", ExceptionUtil.stacktraceToString(exception))).run();
        }
        if (!tabPane.getTabs().contains(tab)) {
            tabPane.getTabs().add(tab);
        }
        tabPane.getSelectionModel().select(tab);
        return tab;
    }
}
