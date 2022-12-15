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

package com.cxxwl96.jfx.admin.client.component.selector;

import static com.cxxwl96.jfx.admin.client.component.selector.SelectorListView.STYLE_CLASS_SELECTED;

import com.cxxwl96.jfx.admin.client.common.JFXComponent;
import com.cxxwl96.jfx.admin.client.component.JFXTag;
import com.cxxwl96.jfx.admin.client.utils.Alert;
import com.cxxwl96.jfx.admin.client.utils.IconUtil;
import com.cxxwl96.jfx.admin.server.base.entity.BaseEntity;
import com.cxxwl96.jfx.admin.server.base.form.SearchForm;
import com.cxxwl96.jfx.admin.server.base.form.WhereItem;
import com.cxxwl96.jfx.admin.server.enums.Condition;
import com.cxxwl96.jfx.admin.server.function.PropertyFunc;
import com.cxxwl96.jfx.admin.server.lang.SearchResult;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.PostConstruct;

import cn.hutool.core.util.StrUtil;
import io.datafx.controller.ViewController;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * JFXSelector
 *
 * @author cxxwl96
 * @since 2022/11/19 23:38
 */
@Slf4j
@ViewController("/views/component/selector/JFXSelector.fxml")
public class JFXSelector<T extends BaseEntity> extends JFXComponent {
    /************************************************************************************************
     * 视图对象                                                                                       *
     ************************************************************************************************/
    @FXML
    private VBox rootPane;

    @FXML
    private FlowPane flowPane;

    @FXML
    private JFXTextField input;

    @FXML
    private Label expandBtn;

    @FXML
    private HBox popupHBox;

    private JFXPopup popup;

    private SelectorListView listView;

    private JFXSpinner listViewSpinner;

    /************************************************************************************************
     * 视图绑定对象                                                                                   *
     ************************************************************************************************/
    private SimpleBooleanProperty expand;

    private SimpleObjectProperty<SelectionMode> selectionMode;

    /************************************************************************************************
     * 类属性                                                                                        *
     ************************************************************************************************/
    // 数据api接口
    private Function<SearchForm, SearchResult<T>> api;

    // 搜索表单
    private final SearchForm searchForm = new SearchForm();

    // SelectorItem工厂
    @Getter
    @Setter
    @Accessors(chain = true)
    private SelectorItemFactory<T> selectorItemFactory;

    /**
     * 查询字段
     */
    private final List<WhereItem> whereItems = new ArrayList<>();

    /**
     * 选中的数据
     */
    private final Map<String, T> selectedItemMap = new HashMap<>();

    /**
     * id与flowItem映射
     */
    private final Map<String, Node> flowPaneItemMap = new HashMap<>();

    /************************************************************************************************
     * 初始化页面及事件                                                                                *
     ************************************************************************************************/
    @PostConstruct
    private void init() {
        expand = new SimpleBooleanProperty();
        selectionMode = new SimpleObjectProperty<>(SelectionMode.SINGLE);
        listView = new SelectorListView();
        listViewSpinner = new JFXSpinner();
        popup = new JFXPopup(listView);
        // 初始化输入框事件
        input.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> expand.set(!expand.get()));
        input.textProperty().addListener((observable, oldValue, newValue) -> toSearch(newValue));
        // 初始化展开按钮事件
        expandBtn.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            expand.set(!expand.get());
        });
        expand.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                expandBtn.setGraphic(IconUtil.getIcon("angle-up"));
                refreshListView();
                popup.show(popupHBox, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.RIGHT);
            } else {
                expandBtn.setGraphic(IconUtil.getIcon("angle-down"));
                popup.hide();
            }
        });
        popup.setOnShown(event -> expand.set(true));
        popup.setOnHidden(event -> expand.set(false));
        // 初始化ListView
        listViewSpinner.setRadius(18);
        listViewSpinner.setStartingAngle(90);
        listViewSpinner.getStyleClass().add("blue-spinner");
        listViewSpinner.visibleProperty().bind(listView.disableProperty());
        listView.getChildren().add(listViewSpinner);
        listView.prefWidthProperty().bind(rootPane.widthProperty());
        listView.setMaxHeight(285);
        listView.getScroll().setOnScrollFinished(event -> {
            // 滑动翻页
            final ScrollPane scroll = listView.getScroll();
            if (scroll.getVvalue() == 0 || scroll.getVvalue() == 1) {
                nextPage();
            }
        });
        listView.setOnSelectChanged(this::toggleFlowItem);
    }

    /**
     * 搜索事件
     *
     * @param text 搜索文本
     */
    private void toSearch(String text) {
        searchForm.getWhereItems().clear();
        if (StrUtil.isBlank(text) || whereItems.size() == 0) {
            refreshListView();
            popup.hide();
            popup.show(popupHBox, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.RIGHT);
            return;
        }
        whereItems.forEach(whereItem -> whereItem.setValue(text));
        if (api != null) {
            searchForm.getWhereItems().addAll(whereItems);
            refreshListView();
            popup.hide();
            popup.show(popupHBox, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.RIGHT);
        }
    }

    private JFXTag createFlowItem(T data) {
        JFXTag flowItem;
        if (selectorItemFactory != null) {
            flowItem = new JFXTag(selectorItemFactory.getSelectorItem(data));
        } else {
            flowItem = new JFXTag(data.toString());
        }
        flowItem.setCloseable(true);
        flowItem.setType(JFXTag.JFXTagType.PRIMARY);
        flowItem.setUserData(data);
        flowItem.setOnClosed(event -> toggleFlowItem(flowItem, false));
        return flowItem;
    }

    /**
     * 进入FlowPane开关
     *
     * @param item UserData拥有数据的节点
     * @param toggle 是否进入flowPane
     */
    private void toggleFlowItem(Node item, boolean toggle) {
        final T data = (T) item.getUserData();
        if (data == null) {
            return;
        }
        if (toggle) {
            // 进入flowPane
            if (flowPaneItemMap.containsKey(data.getId())) {
                return;
            }
            final JFXTag flowItem = createFlowItem(data);
            selectedItemMap.put(data.getId(), data);
            flowPaneItemMap.put(data.getId(), flowItem);
            flowPane.getChildren().add(flowItem);
        } else {
            if (!flowPaneItemMap.containsKey(data.getId())) {
                return;
            }
            final Node flowItem = flowPaneItemMap.get(data.getId());
            flowPane.getChildren().remove(flowItem);
            selectedItemMap.remove(data.getId());
            flowPaneItemMap.remove(data.getId());
        }
    }
    /************************************************************************************************
     * 外部方法                                                                                      *
     ************************************************************************************************/
    /**
     * 设置数据api接口
     *
     * @param api 数据api接口
     */
    public JFXSelector<T> setApi(Function<SearchForm, SearchResult<T>> api) {
        this.api = api;
        return this;
    }

    /**
     * 刷新列表
     */
    private void refreshListView() {
        listView.getItems().clear();
        searchForm.setPage(0);
        if (api != null) {
            nextPage();
        }
    }

    /**
     * 下一页
     */
    private void nextPage() {
        ProcessChain.create()
            .addRunnableInPlatformThread(() -> listView.setDisable(true))
            .addSupplierInExecutor(() -> {
                searchForm.setPage(searchForm.getPage() + 1);
                return api.apply(searchForm);
            })
            .addConsumerInPlatformThread(
                result -> result.getItems().forEach(data -> listView.getItems().add(createItem(data))))
            .onException(exception -> {
                log.error(exception.getMessage(), exception);
                Alert.error("发生未知错误", exception.getMessage());
            })
            .withFinal(() -> listView.setDisable(false))
            .run();
    }

    /**
     * 创建列表节点
     *
     * @param data 数据
     * @return 列表节点
     */
    private Label createItem(T data) {
        Label item;
        if (selectorItemFactory != null) {
            item = new Label(selectorItemFactory.getSelectorItem(data));
        } else {
            item = new Label(data.toString());
        }
        item.setMaxWidth(Double.MAX_VALUE);
        item.setUserData(data);
        // 是否选中
        if (selectedItemMap.containsKey(data.getId())) {
            item.getStyleClass().add(STYLE_CLASS_SELECTED);
            toggleFlowItem(item, true);
        }
        return item;
    }

    /**
     * 设置需要搜索的字段
     *
     * @param fieldName 需要搜索的字段
     */
    public JFXSelector<T> setSearchFieldName(Collection<PropertyFunc<T, String>> fieldName) {
        this.whereItems.clear();
        for (PropertyFunc<T, ?> propertyFunc : fieldName) {
            final WhereItem whereItem = WhereItem.whereItem(propertyFunc, Condition.LIKE, null);
            whereItem.setWhereOperator(WhereItem.WhereOperator.OR);
            this.whereItems.add(whereItem);
        }
        return this;
    }

    /**
     * 设置选中的数据
     *
     * @param data 选中的数据
     */
    public void setSelectedData(Collection<T> data) {
        selectedItemMap.clear();
        data.forEach(dataItem -> {
            selectedItemMap.put(dataItem.getId(), dataItem);
            final JFXTag flowItem = createFlowItem(dataItem);
            toggleFlowItem(flowItem, true);
        });
    }

    /**
     * 清空选择
     */
    public void clearSelection() {
        selectedItemMap.clear();
    }

    /**
     * 获取选中的数据
     *
     * @return 选中的数据, 不可更改
     */
    public List<T> getSelectedData() {
        return Collections.unmodifiableList(new ArrayList<>(selectedItemMap.values()));
    }

    /************************************************************************************************
     * setter、getter、property                                                                      *
     ************************************************************************************************/
    public boolean getExpand() {
        return expand.get();
    }

    public SimpleBooleanProperty expandProperty() {
        return expand;
    }

    public void setExpand(boolean expand) {
        this.expand.set(expand);
    }

    public SelectionMode getSelectionMode() {
        return selectionMode.get();
    }

    public SimpleObjectProperty<SelectionMode> selectionModeProperty() {
        return selectionMode;
    }

    public void setSelectionMode(SelectionMode selectionMode) {
        this.selectionMode.set(selectionMode);
    }
}
