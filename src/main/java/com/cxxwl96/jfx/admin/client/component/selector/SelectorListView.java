/*
 * Copyright (change) 2021-2022, jad (cxxwl96@sina.com).
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

package com.cxxwl96.jfx.admin.client.component.selector;

import com.cxxwl96.jfx.admin.client.common.JFXComponent;
import com.sun.javafx.collections.TrackableObservableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.annotation.PostConstruct;

import io.datafx.controller.ViewController;
import javafx.beans.DefaultProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * SelectorListView
 *
 * @author cxxwl96
 * @since 2022/11/21 00:12
 */
@Slf4j
@DefaultProperty("items")
@ViewController("/views/component/selector/SelectorListView.fxml")
class SelectorListView extends JFXComponent {
    static final String STYLE_CLASS_SELECTED = "selected";

    public static final String STYLE_CLASS_UNSELECTED = "unselected";

    @FXML
    @Getter
    private ScrollPane scroll;

    @FXML
    private VBox listView;

    /**
     * 点击变更事件
     */
    @Setter
    private BiConsumer<Node, Boolean> onSelectChanged;

    /**
     * 列表与包装列表映射
     */
    private final Map<Node, Node> nodeMap = new HashMap<>();

    /**
     * 列表
     */
    private ObservableList<Node> items = new TrackableObservableList<Node>() {
        @Override
        protected void onChanged(ListChangeListener.Change<Node> change) {
            change.reset();
            while (change.next()) {
                if (change.wasAdded()) {
                    change.getAddedSubList().forEach(node -> {
                        final HBox item = createItem(node);
                        nodeMap.put(node, item);
                        listView.getChildren().add(item);
                    });
                } else if (change.wasRemoved()) {
                    change.getRemoved().forEach(node -> {
                        final Node item = nodeMap.get(node);
                        listView.getChildren().remove(item);
                        nodeMap.remove(node);
                    });
                }
            }
        }
    };

    @PostConstruct
    private void init() {

    }

    private HBox createItem(Node node) {
        final boolean selected = node.getStyleClass().contains(STYLE_CLASS_SELECTED);
        final String styleClass = selected ? STYLE_CLASS_SELECTED : STYLE_CLASS_UNSELECTED;
        if (selected) {
            node.getStyleClass().remove(STYLE_CLASS_SELECTED);
        }
        final HBox hBox = new HBox(node);
        hBox.setUserData(selected);
        hBox.getStyleClass().addAll("jfx-selector-list-view-item", styleClass);
        hBox.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            final boolean isSelected = !(Boolean) hBox.getUserData();
            selectItem(hBox, isSelected);
            if (onSelectChanged != null) {
                onSelectChanged.accept(node, isSelected);
            }
        });
        return hBox;
    }

    private void selectItem(Node hBox, boolean selected) {
        hBox.setUserData(selected);
        if (selected) {
            hBox.getStyleClass().add(STYLE_CLASS_SELECTED);
            hBox.getStyleClass().remove(STYLE_CLASS_UNSELECTED);
        } else {
            hBox.getStyleClass().remove(STYLE_CLASS_SELECTED);
            hBox.getStyleClass().add(STYLE_CLASS_UNSELECTED);
        }
    }

    private Node getItemNode(Node hBox) {
        if (hBox instanceof HBox) {
            return ((HBox) hBox).getChildren().get(0);
        }
        throw new IllegalStateException("Invalid hBox: " + hBox);
    }

    /**
     * 获取选中的列表
     *
     * @return 选中的列表
     */
    public List<Node> getSelectedItems() {
        final ArrayList<Node> selectedItems = new ArrayList<>();
        listView.getChildren().forEach(item -> {
            if (((Boolean) item.getUserData())) {
                selectedItems.add(getItemNode(item));
            }
        });
        return selectedItems;
    }

    public ObservableList<Node> getItems() {
        return items;
    }

    public void setItems(ObservableList<Node> items) {
        this.items = items;
    }
}
