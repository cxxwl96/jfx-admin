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

package com.cxxwl96.jfx.admin.client.component.tree;

import com.cxxwl96.jfx.admin.client.common.JFXComponent;
import com.cxxwl96.jfx.admin.client.utils.Alert;
import com.cxxwl96.jfx.admin.server.base.entity.TreeNode;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import io.datafx.controller.ViewController;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * JFXTree
 *
 * @author cxxwl96
 * @since 2022/11/12 21:37
 */
@Slf4j
@ViewController("/views/component/tree/JFXTree.fxml")
public class JFXTree<T extends TreeNode<T>> extends JFXComponent {
    /************************************************************************************************
     * 视图对象                                                                                       *
     ************************************************************************************************/
    @FXML
    private Label titleLabel;

    @FXML
    private JFXTextField searchTextField;

    @FXML
    private Hyperlink clear;

    @FXML
    private Label compressBtn;

    @FXML
    private Label expandBtn;

    @FXML
    private HBox selectMenuPane;

    @FXML
    private Hyperlink selectAll;

    @FXML
    private Hyperlink selectReverse;

    @FXML
    private Hyperlink selectNone;

    @FXML
    private JFXTreeView<Node> treeView;

    @FXML
    private JFXSpinner spinner;

    /************************************************************************************************
     * 视图绑定对象                                                                                   *
     ************************************************************************************************/
    private SimpleBooleanProperty checkable;

    // 标题
    private SimpleStringProperty title;

    /************************************************************************************************
     * 类属性                                                                                        *
     ************************************************************************************************/
    // TreeItem过滤器
    @Getter
    @Setter
    @Accessors(chain = true)
    private TreeItemFilter<T> treeItemFilter;

    // TreeItem值工厂
    @Getter
    @Setter
    @Accessors(chain = true)
    private TreeItemValueFactory<T> treeItemValueFactory;

    // 树节点主属性值工厂
    @Getter
    @Setter
    @Accessors(chain = true)
    private MainFieldValueFactory<T> mainFieldValueFactory;

    // 树形数据
    private List<T> data;

    // 树形数据类型
    private Class<?> dataClass;

    // 菜单项map
    private final Map<String, TreeItem<Node>> treeItemMap = new HashMap<>();

    /************************************************************************************************
     * 初始化页面及事件                                                                                *
     ************************************************************************************************/
    @PostConstruct
    private void init() {
        super.setPrefWidth(405);
        // 初始化标题
        title = new SimpleStringProperty();
        titleLabel.textProperty().bindBidirectional(title);
        // 初始化能否多选
        checkable = new SimpleBooleanProperty();
        selectMenuPane.visibleProperty().bind(checkable);
        selectMenuPane.managedProperty().bind(checkable);
        // 初始化搜索框
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (StrUtil.isBlank(newValue)) {
                selectMenuPane.setDisable(false); // 恢复全选、反选、全不选按钮
                refreshTree(this.data, true);
                return;
            }
            selectMenuPane.setDisable(true); // 搜索时不能点击全选、反选、全不选按钮,不然获取选中数据时可能缺失父节点
            // 搜索树
            final List<T> data = searchTree(this.data, newValue);
            refreshTree(data, true);
            expand(true);
        });
        clear.setOnAction(event -> searchTextField.clear());
        // 初始化按钮
        compressBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> expand(false));
        expandBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> expand(true));
        selectAll.setOnAction(event -> selectChildTreeItem(treeView.getRoot(), true));
        selectReverse.setOnAction(event -> selectReverseTreeItem(treeView.getRoot()));
        selectNone.setOnAction(event -> selectChildTreeItem(treeView.getRoot(), false));
        // 初始化树
        treeView.setShowRoot(false);
        spinner.visibleProperty().bind(treeView.disabledProperty());
    }

    /**
     * 搜索树
     *
     * @param data 树形数据
     * @param text 搜索文本
     * @return 搜索到的树节点数据
     */
    private List<T> searchTree(List<T> data, String text) {
        final ArrayList<T> list = new ArrayList<>();
        if (mainFieldValueFactory == null || CollUtil.isEmpty(data) || dataClass == null) {
            return list;
        }
        data.forEach(item -> {
            final T treeNode = (T) ReflectUtil.newInstance(dataClass);
            BeanUtil.copyProperties(item, treeNode);
            // 如果有子节点则继续向下搜索
            if (CollUtil.isNotEmpty(treeNode.getChildren())) {
                final List<T> subList = searchTree(treeNode.getChildren(), text);
                if (CollUtil.isNotEmpty(subList)) {
                    treeNode.setChildren(subList);
                    list.add(treeNode);
                    return;
                }
            }
            // 没有子节点则判断当前节点是否满足文本包含以及拼音包含
            if (matchByTextOrPinyin(item, text)) {
                list.add(treeNode);
            }
        });
        return list;
    }

    /**
     * 按文本或拼音搜索文本
     *
     * @param data 树形数据
     * @param searchText 搜索关键字
     * @return 是否搜索到
     */
    private boolean matchByTextOrPinyin(T data, String searchText) {
        if (mainFieldValueFactory == null || StrUtil.isBlank(searchText)) {
            return false;
        }
        final String mainFieldValue = mainFieldValueFactory.getMainFieldValue(data);
        if (StrUtil.isBlank(mainFieldValue)) {
            return false;
        }
        final boolean containsTitle = mainFieldValue.toLowerCase(Locale.ROOT)
            .contains(searchText.toLowerCase(Locale.ROOT));
        final boolean containsTitlePinyin = PinyinUtil.getPinyin(mainFieldValue)
            .replaceAll("\\s", StrUtil.EMPTY)
            .toLowerCase(Locale.ROOT)
            .contains(searchText.toLowerCase(Locale.ROOT));
        return containsTitle || containsTitlePinyin;
    }
    /************************************************************************************************
     * 外部方法                                                                                      *
     ************************************************************************************************/
    /**
     * 设置表单标题
     *
     * @param title 标题
     * @param graphic 图标
     */
    public void setTitle(String title, Node graphic) {
        this.titleLabel.setText(title);
        this.titleLabel.setGraphic(graphic);
    }

    /**
     * 设置数据
     *
     * @param data 数据
     */
    public void setTreeData(@NotNull List<T> data) {
        this.data = data;
        if (CollUtil.isNotEmpty(data)) {
            this.dataClass = data.get(0).getClass();
        }
        refreshTree(data, false);
    }

    /**
     * 刷新树
     *
     * @param data 树形数据
     * @param sync 是否异步
     */
    private void refreshTree(List<T> data, boolean sync) {
        final Runnable refresh = () -> {
            List<TreeItem<Node>> treeItems = createTreeItems(data);
            final TreeItem<Node> rootItem = new TreeItem<>();
            rootItem.getChildren().addAll(treeItems);
            treeView.setRoot(rootItem);
        };
        if (sync) {
            ProcessChain.create()
                .addRunnableInPlatformThread(() -> treeView.setDisable(true))
                .addSupplierInExecutor(() -> data)
                .addConsumerInPlatformThread(result -> refresh.run())
                .onException(exception -> {
                    log.error(exception.getMessage(), exception);
                    Alert.error("发生未知错误", exception.getMessage());
                })
                .withFinal(() -> treeView.setDisable(false))
                .run();
        } else {
            refresh.run();
        }
    }

    /**
     * 递归创建TreeItems
     *
     * @param data 树形数据
     * @return TreeItems
     */
    private List<TreeItem<Node>> createTreeItems(List<T> data) {
        final ArrayList<TreeItem<Node>> treeItems = new ArrayList<>();
        if (data == null) {
            return treeItems;
        }
        data.forEach(item -> {
            // TreeItem过滤
            if (treeItemFilter != null && !treeItemFilter.filter(item)) {
                return;
            }
            // 构建菜单按钮
            final TreeItem<Node> treeItem = createTreeItem(item);
            if (CollUtil.isNotEmpty(item.getChildren())) {
                final List<TreeItem<Node>> subTreeItems = createTreeItems(item.getChildren());
                treeItem.getChildren().addAll(subTreeItems);
            }
            treeItems.add(treeItem);
        });
        return treeItems;
    }

    /**
     * 创建TreeItem
     *
     * @param data 树形数据
     * @return TreeItem
     */
    private TreeItem<Node> createTreeItem(T data) {
        Node treeItemValue;
        if (treeItemValueFactory != null) {
            treeItemValue = treeItemValueFactory.getTreeItemValue(data);
        } else {
            treeItemValue = new Label(data.toString());
        }
        final TreeItem<Node> treeItem = new TreeItem<>();
        final HBox box = new HBox();
        final JFXCheckBox checkBox = new JFXCheckBox();
        checkBox.visibleProperty().bind(checkable);
        checkBox.managedProperty().bind(checkable);
        checkBox.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            final boolean selected = checkBox.isSelected();
            selectChildTreeItem(treeItem, selected);
            selectParentTreeItem(treeItem, selected);
        });
        box.getChildren().addAll(checkBox, treeItemValue);
        box.setUserData(data);
        treeItem.setValue(box);
        // 保存菜单项
        if (treeItemMap.containsKey(data.getId())) {
            TreeItem<Node> oldItem = treeItemMap.get(data.getId());
            treeItem.setExpanded(oldItem.isExpanded());
            getTreeItemCheckBox(oldItem).ifPresent(oldCheckBox -> checkBox.setSelected(oldCheckBox.isSelected()));
        }
        treeItemMap.put(data.getId(), treeItem);
        // 若搜索框包含文本，则使文本呈红色
        final String text = searchTextField.getText();
        if (matchByTextOrPinyin(data, text)) {
            treeItemValue.setStyle("-fx-text-fill: red");
        }
        return treeItem;
    }

    /**
     * 获取TreeItem的CheckBox
     *
     * @param treeItem treeItem
     * @return CheckBox
     */
    private Optional<JFXCheckBox> getTreeItemCheckBox(TreeItem<Node> treeItem) {
        if (treeItem == null) {
            return Optional.empty();
        }
        final HBox treeItemValue = (HBox) treeItem.getValue();
        if (treeItemValue == null) {
            return Optional.empty();
        }
        final ObservableList<Node> children = treeItemValue.getChildren();
        if (children.size() == 0) {
            return Optional.empty();
        }
        final JFXCheckBox checkBox = (JFXCheckBox) children.get(0);
        return Optional.ofNullable(checkBox);
    }

    /**
     * 获取TreeItem的树形数据
     *
     * @param treeItem treeItem
     * @return 树形数据
     */
    private Optional<T> getTreeItemData(TreeItem<Node> treeItem) {
        if (treeItem == null) {
            return Optional.empty();
        }
        final HBox hBox = (HBox) treeItem.getValue();
        if (hBox == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(((T) hBox.getUserData()));
    }

    /**
     * 递归设置TreeItem是否选中
     *
     * @param treeItem treeItem
     * @param select select
     */
    private void selectChildTreeItem(TreeItem<Node> treeItem, boolean select) {
        if (treeItem == null) {
            return;
        }
        getTreeItemCheckBox(treeItem).ifPresent(checkBox -> checkBox.setSelected(select));
        treeItem.getChildren().forEach(item -> selectChildTreeItem(item, select));
    }

    /**
     * 递归设置TreeItem的父级是否选中
     *
     * @param treeItem treeItem
     * @param select select
     */
    private void selectParentTreeItem(TreeItem<Node> treeItem, boolean select) {
        if (treeItem == null) {
            return;
        }
        final TreeItem<Node> parentTreeItem = treeItem.getParent();
        getTreeItemCheckBox(parentTreeItem).ifPresent(checkBox -> checkBox.setSelected(select));
        selectParentTreeItem(parentTreeItem, select);
        final int size = selectedData(parentTreeItem).size();
        if (size > 0) {
            getTreeItemCheckBox(parentTreeItem).ifPresent(checkBox -> checkBox.setSelected(true));
        }
    }

    /**
     * 递归设置TreeItem反选
     *
     * @param treeItem treeItem
     */
    private void selectReverseTreeItem(TreeItem<Node> treeItem) {
        if (treeItem == null) {
            return;
        }
        getTreeItemCheckBox(treeItem).ifPresent(checkBox -> checkBox.setSelected(!checkBox.isSelected()));
        treeItem.getChildren().forEach(this::selectReverseTreeItem);
        final int size = selectedData(treeItem).size();
        if (size > 0) {
            getTreeItemCheckBox(treeItem).ifPresent(checkBox -> checkBox.setSelected(true));
        }
    }

    /**
     * 递归获取当前树选中的数据
     *
     * @param treeItem treeItem
     * @return 选中的数据
     */
    private List<T> selectedData(TreeItem<Node> treeItem) {
        final ArrayList<T> list = new ArrayList<>();
        if (treeItem == null) {
            return list;
        }
        treeItem.getChildren().forEach(item -> {
            getTreeItemCheckBox(item).ifPresent(checkBox -> {
                if (checkBox.isSelected()) {
                    getTreeItemData(item).ifPresent(list::add);
                }
            });
            list.addAll(selectedData(item));
        });
        return list;
    }

    /**
     * 获取所有树节点选中的数据
     *
     * @return 选中的数据
     */
    public List<T> getSelectedData() {
        return treeItemMap.values().stream() // 流过滤
            .filter(treeItem -> getTreeItemCheckBox(treeItem).map(CheckBox::isSelected).orElse(false)) // 过滤选中
            .map(treeItem -> getTreeItemData(treeItem).orElse(null)) // 获取数据
            .filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 设置选中数据
     *
     * @param ids ID集合
     */
    public void setSelectedData(List<String> ids) {
        ids.forEach(id -> {
            final TreeItem<Node> treeItem = treeItemMap.get(id);
            if (treeItem != null) {
                getTreeItemCheckBox(treeItem).ifPresent(checkBox -> checkBox.setSelected(true));
            }
        });
    }

    /**
     * 清空选择
     */
    public void clearSelection() {
        treeItemMap.values()
            .forEach(treeItem -> getTreeItemCheckBox(treeItem).ifPresent(checkBox -> checkBox.setSelected(false)));
    }

    /**
     * 展开所有
     */
    public void expand(boolean expand) {
        expand(this.treeView.getRoot(), expand);
    }

    /**
     * 展开所有
     *
     * @param treeItem treeItem
     * @param expand 是否展开
     */
    private void expand(TreeItem<Node> treeItem, boolean expand) {
        if (treeItem == null) {
            return;
        }
        treeItem.getChildren().forEach(item -> {
            item.setExpanded(expand);
            expand(item, expand);
        });
    }

    /************************************************************************************************
     * setter、getter、property                                                                      *
     ************************************************************************************************/
    public boolean isCheckable() {
        return checkable.get();
    }

    public SimpleBooleanProperty checkableProperty() {
        return checkable;
    }

    public void setCheckable(boolean checkable) {
        this.checkable.set(checkable);
    }

    public String getTitle() {
        return title.get();
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }
}
