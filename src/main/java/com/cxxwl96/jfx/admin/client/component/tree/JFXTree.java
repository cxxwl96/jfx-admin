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
     * ????????????                                                                                       *
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
     * ??????????????????                                                                                   *
     ************************************************************************************************/
    private SimpleBooleanProperty checkable;

    // ??????
    private SimpleStringProperty title;

    /************************************************************************************************
     * ?????????                                                                                        *
     ************************************************************************************************/
    // TreeItem?????????
    @Getter
    @Setter
    @Accessors(chain = true)
    private TreeItemFilter<T> treeItemFilter;

    // TreeItem?????????
    @Getter
    @Setter
    @Accessors(chain = true)
    private TreeItemValueFactory<T> treeItemValueFactory;

    // ???????????????????????????
    @Getter
    @Setter
    @Accessors(chain = true)
    private MainFieldValueFactory<T> mainFieldValueFactory;

    // ????????????
    private List<T> data;

    // ??????????????????
    private Class<?> dataClass;

    // ?????????map
    private final Map<String, TreeItem<Node>> treeItemMap = new HashMap<>();

    /************************************************************************************************
     * ????????????????????????                                                                                *
     ************************************************************************************************/
    @PostConstruct
    private void init() {
        super.setPrefWidth(405);
        // ???????????????
        title = new SimpleStringProperty();
        titleLabel.textProperty().bindBidirectional(title);
        // ?????????????????????
        checkable = new SimpleBooleanProperty();
        selectMenuPane.visibleProperty().bind(checkable);
        selectMenuPane.managedProperty().bind(checkable);
        // ??????????????????
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (StrUtil.isBlank(newValue)) {
                selectMenuPane.setDisable(false); // ???????????????????????????????????????
                refreshTree(this.data, true);
                return;
            }
            selectMenuPane.setDisable(true); // ??????????????????????????????????????????????????????,????????????????????????????????????????????????
            // ?????????
            final List<T> data = searchTree(this.data, newValue);
            refreshTree(data, true);
            expand(true);
        });
        clear.setOnAction(event -> searchTextField.clear());
        // ???????????????
        compressBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> expand(false));
        expandBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> expand(true));
        selectAll.setOnAction(event -> selectChildTreeItem(treeView.getRoot(), true));
        selectReverse.setOnAction(event -> selectReverseTreeItem(treeView.getRoot()));
        selectNone.setOnAction(event -> selectChildTreeItem(treeView.getRoot(), false));
        // ????????????
        treeView.setShowRoot(false);
        spinner.visibleProperty().bind(treeView.disabledProperty());
    }

    /**
     * ?????????
     *
     * @param data ????????????
     * @param text ????????????
     * @return ???????????????????????????
     */
    private List<T> searchTree(List<T> data, String text) {
        final ArrayList<T> list = new ArrayList<>();
        if (mainFieldValueFactory == null || CollUtil.isEmpty(data) || dataClass == null) {
            return list;
        }
        data.forEach(item -> {
            final T treeNode = (T) ReflectUtil.newInstance(dataClass);
            BeanUtil.copyProperties(item, treeNode);
            // ???????????????????????????????????????
            if (CollUtil.isNotEmpty(treeNode.getChildren())) {
                final List<T> subList = searchTree(treeNode.getChildren(), text);
                if (CollUtil.isNotEmpty(subList)) {
                    treeNode.setChildren(subList);
                    list.add(treeNode);
                    return;
                }
            }
            // ??????????????????????????????????????????????????????????????????????????????
            if (matchByTextOrPinyin(item, text)) {
                list.add(treeNode);
            }
        });
        return list;
    }

    /**
     * ??????????????????????????????
     *
     * @param data ????????????
     * @param searchText ???????????????
     * @return ???????????????
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
     * ????????????                                                                                      *
     ************************************************************************************************/
    /**
     * ??????????????????
     *
     * @param title ??????
     * @param graphic ??????
     */
    public void setTitle(String title, Node graphic) {
        this.titleLabel.setText(title);
        this.titleLabel.setGraphic(graphic);
    }

    /**
     * ????????????
     *
     * @param data ??????
     */
    public void setTreeData(@NotNull List<T> data) {
        this.data = data;
        if (CollUtil.isNotEmpty(data)) {
            this.dataClass = data.get(0).getClass();
        }
        refreshTree(data, false);
    }

    /**
     * ?????????
     *
     * @param data ????????????
     * @param sync ????????????
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
                    Alert.error("??????????????????", exception.getMessage());
                })
                .withFinal(() -> treeView.setDisable(false))
                .run();
        } else {
            refresh.run();
        }
    }

    /**
     * ????????????TreeItems
     *
     * @param data ????????????
     * @return TreeItems
     */
    private List<TreeItem<Node>> createTreeItems(List<T> data) {
        final ArrayList<TreeItem<Node>> treeItems = new ArrayList<>();
        if (data == null) {
            return treeItems;
        }
        data.forEach(item -> {
            // TreeItem??????
            if (treeItemFilter != null && !treeItemFilter.filter(item)) {
                return;
            }
            // ??????????????????
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
     * ??????TreeItem
     *
     * @param data ????????????
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
        // ???????????????
        if (treeItemMap.containsKey(data.getId())) {
            TreeItem<Node> oldItem = treeItemMap.get(data.getId());
            treeItem.setExpanded(oldItem.isExpanded());
            getTreeItemCheckBox(oldItem).ifPresent(oldCheckBox -> checkBox.setSelected(oldCheckBox.isSelected()));
        }
        treeItemMap.put(data.getId(), treeItem);
        // ????????????????????????????????????????????????
        final String text = searchTextField.getText();
        if (matchByTextOrPinyin(data, text)) {
            treeItemValue.setStyle("-fx-text-fill: red");
        }
        return treeItem;
    }

    /**
     * ??????TreeItem???CheckBox
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
     * ??????TreeItem???????????????
     *
     * @param treeItem treeItem
     * @return ????????????
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
     * ????????????TreeItem????????????
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
     * ????????????TreeItem?????????????????????
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
     * ????????????TreeItem??????
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
     * ????????????????????????????????????
     *
     * @param treeItem treeItem
     * @return ???????????????
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
     * ????????????????????????????????????
     *
     * @return ???????????????
     */
    public List<T> getSelectedData() {
        return treeItemMap.values().stream() // ?????????
            .filter(treeItem -> getTreeItemCheckBox(treeItem).map(CheckBox::isSelected).orElse(false)) // ????????????
            .map(treeItem -> getTreeItemData(treeItem).orElse(null)) // ????????????
            .filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * ??????????????????
     *
     * @param ids ID??????
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
     * ????????????
     */
    public void clearSelection() {
        treeItemMap.values()
            .forEach(treeItem -> getTreeItemCheckBox(treeItem).ifPresent(checkBox -> checkBox.setSelected(false)));
    }

    /**
     * ????????????
     */
    public void expand(boolean expand) {
        expand(this.treeView.getRoot(), expand);
    }

    /**
     * ????????????
     *
     * @param treeItem treeItem
     * @param expand ????????????
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
     * setter???getter???property                                                                      *
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
