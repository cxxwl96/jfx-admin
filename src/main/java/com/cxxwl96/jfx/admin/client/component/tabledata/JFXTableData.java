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

package com.cxxwl96.jfx.admin.client.component.tabledata;

import com.cxxwl96.jfx.admin.client.annotation.JFXFormField;
import com.cxxwl96.jfx.admin.client.annotation.JFXTableColumn;
import com.cxxwl96.jfx.admin.client.common.JFXComponent;
import com.cxxwl96.jfx.admin.client.component.JFXNumberField;
import com.cxxwl96.jfx.admin.client.component.form.AbstractFormControlFactory;
import com.cxxwl96.jfx.admin.client.component.form.DatePickerControlFactory;
import com.cxxwl96.jfx.admin.client.component.form.TextFieldControlFactory;
import com.cxxwl96.jfx.admin.client.utils.Alert;
import com.cxxwl96.jfx.admin.client.utils.IconUtil;
import com.cxxwl96.jfx.admin.server.base.enums.IEnum;
import com.cxxwl96.jfx.admin.server.base.form.SearchForm;
import com.cxxwl96.jfx.admin.server.base.form.WhereItem;
import com.cxxwl96.jfx.admin.server.enums.Condition;
import com.cxxwl96.jfx.admin.server.lang.SearchResult;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.base.IFXLabelFloatControl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import io.datafx.controller.ViewController;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * 表格数据管理组件，包括查询表单、数据列表、列表分页
 *
 * @param <S> 表格行数据的类型
 * @author cxxwl96
 * @since 2022/10/17 23:31
 */
@Slf4j
@ViewController("/views/component/table/JFXTableData.fxml")
public class JFXTableData<S extends TableItem> extends JFXComponent {
    /************************************************************************************************
     * 视图对象                                                                                       *
     ************************************************************************************************/
    @FXML
    private VBox rootPane;

    @FXML
    private VBox searchFormPane;

    @FXML
    private JFXSpinner searchFormSpinner;

    @FXML
    private FlowPane searchFormFieldPane;

    @FXML
    private JFXDatePicker startDatePicker;

    @FXML
    private JFXDatePicker endDatePicker;

    @FXML
    private JFXButton searchFormResetButton;

    @FXML
    private JFXButton searchFormSubmitButton;

    @FXML
    private Label searchFormExpandButton;

    @FXML
    private VBox tableViewPane;

    @FXML
    private Label tableTitle;

    @FXML
    private HBox toolBar;

    @FXML
    private Label refreshTableButton;

    @FXML
    private JFXSpinner tableViewSpinner;

    @FXML
    private TableView<S> tableView;

    @FXML
    private Label pageTotalLabel;

    @FXML
    private Pagination pagination;

    @FXML
    private JFXComboBox<Label> pageSizeComboBox;

    @FXML
    private JFXNumberField pageTextField;

    /************************************************************************************************
     * 视图绑定对象                                                                                   *
     ************************************************************************************************/
    // 标题
    private SimpleStringProperty title;

    // 是否显示搜索表单
    private SimpleBooleanProperty showSearchForm;

    // 是否收缩搜索表单
    private SimpleBooleanProperty searchFormExpand;

    // 是否显示序号列
    private SimpleBooleanProperty showNumberColumn;

    // 是否显示操作列
    private SimpleBooleanProperty showOperateColumn;

    /************************************************************************************************
     * 类属性                                                                                        *
     ************************************************************************************************/
    // 搜索表单字段名与控件工厂映射表
    private Map<String, AbstractFormControlFactory> searchFormControlFactoryMap;

    // 搜索表单字段名与控件映射表
    private Map<String, Node> searchFormControlMap;

    // 开始时间控件工厂
    private AbstractFormControlFactory startDatePickerControlFactory;

    // 结束时间控件工厂
    private AbstractFormControlFactory endDatePickerControlFactory;

    // 约束类
    private Class<S> tableSchemaClass;

    // 约束类中不存在的字段，即不与数据实体绑定的字段
    private String[] notExistFieldNames = new String[0];

    // 表格数据api接口
    private Function<SearchForm, SearchResult<?>> api;

    // 搜索表单
    private final SearchForm searchForm = new SearchForm();

    // 序号列
    private TableColumn<S, Integer> numberColumn;

    // 操作列
    private TableColumn<S, Node> operateColumn;

    /************************************************************************************************
     * 初始化页面及事件                                                                                *
     ************************************************************************************************/
    @PostConstruct
    private void init() {
        title = new SimpleStringProperty();
        tableTitle.textProperty().bindBidirectional(title);
        showSearchForm = new SimpleBooleanProperty(true);
        searchFormExpand = new SimpleBooleanProperty(false);
        showNumberColumn = new SimpleBooleanProperty(true);
        showOperateColumn = new SimpleBooleanProperty(true);
        // 初始化搜索表单
        initSearchForm();
        // 初始化表格
        initTableView();
        // 初始化分页
        initPagination();
    }

    /**
     * 初始化搜索表单
     */
    private void initSearchForm() {
        searchFormControlFactoryMap = new HashMap<>();
        searchFormControlMap = new HashMap<>();
        // 默认添加开始时间选择器、结束时间选择器
        startDatePickerControlFactory = new DatePickerControlFactory();
        endDatePickerControlFactory = new DatePickerControlFactory();
        startDatePicker = (JFXDatePicker) startDatePickerControlFactory.getControl("开始时间");
        endDatePicker = (JFXDatePicker) endDatePickerControlFactory.getControl("结束时间");
        // 是否显示搜索表单双向绑定
        searchFormPane.managedProperty().bindBidirectional(this.showSearchFormProperty());
        // 初始化spinner
        searchFormSpinner.visibleProperty().bind(searchFormPane.disableProperty());
        // 初始化重置按钮
        initSearchFormResetButton();
        // 初始化查询按钮
        initSearchFormSubmitButton();
        // 初始化收缩按钮
        initSearchFormExpandButton();
    }

    /**
     * 初始化重置按钮
     */
    private void initSearchFormResetButton() {
        searchFormResetButton.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
            searchFormControlFactoryMap.values().forEach(controlFactory -> controlFactory.setValue(null));
        });
    }

    /**
     * 初始化查询按钮
     */
    private void initSearchFormSubmitButton() {
        searchFormSubmitButton.setOnAction(event -> ProcessChain.create()
            .addRunnableInPlatformThread(() -> searchFormPane.setDisable(true))
            .addSupplierInExecutor(() -> {
                if (api == null) {
                    log.error("Please provide a table data API");
                    return new SearchResult<>();
                }
                log.info("SearchForm: {}", searchForm);
                return api.apply(searchForm);
            })
            .addConsumerInPlatformThread(result -> searchAction())
            .onException(exception -> {
                log.error(exception.getMessage(), exception);
                Alert.error("发生未知错误", exception.getMessage());
            })
            .withFinal(() -> searchFormPane.setDisable(false))
            .run());
    }

    /**
     * 初始化收缩按钮
     */
    private void initSearchFormExpandButton() {
        // 收缩/展开方法
        final Consumer<Boolean> expandSearchFormFieldPane = (expand) -> {
            searchFormFieldPane.getChildren().clear();
            searchFormFieldPane.getChildren().addAll(startDatePicker, endDatePicker);
            if (expand) {
                // 展开
                searchFormFieldPane.getChildren().addAll(searchFormControlMap.values());
                searchFormExpandButton.setGraphic(IconUtil.getIcon("angle-up"));
                searchFormExpandButton.setText("收起");
            } else {
                // 收缩
                searchFormExpandButton.setGraphic(IconUtil.getIcon("angle-down"));
                searchFormExpandButton.setText("展开");
            }
        };
        // 默认收缩
        expandSearchFormFieldPane.accept(false);
        // 收缩事件
        this.searchFormExpandProperty()
            .addListener((observable, oldValue, newValue) -> expandSearchFormFieldPane.accept(newValue));
        searchFormExpandButton.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            final boolean expand = this.searchFormExpandProperty().get();
            this.searchFormExpandProperty().set(!expand);
        });
    }

    /**
     * 初始化表格
     */
    private void initTableView() {
        // 刷新按钮事件
        refreshTableButton.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> this.refreshTable());
        // table spinner绑定
        tableViewSpinner.visibleProperty().bind(tableView.disableProperty());
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // 初始化序号列
        numberColumn = new TableColumn<>("序号");
        numberColumn.setMinWidth(40);
        numberColumn.setPrefWidth(40);
        numberColumn.setMaxWidth(40);
        numberColumn.setSortable(false);
        numberColumn.setCellFactory(new NumberCellFactory<>());
        numberColumn.visibleProperty().bindBidirectional(showNumberColumn);
        // 初始化操作列
        operateColumn = new TableColumn<S, Node>("操作");
        operateColumn.setMinWidth(120);
        operateColumn.setMaxWidth(120);
        operateColumn.setSortable(false);
        operateColumn.visibleProperty().bindBidirectional(showOperateColumn);
    }

    /**
     * 初始化分页
     */
    private void initPagination() {
        pagination.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> {
            this.searchForm.setPage(newValue.longValue() + 1);
            this.refreshTable();
        });
        pageSizeComboBox.getSelectionModel().selectFirst();
        pageSizeComboBox.setOnAction(event -> {
            final String userData = (String) pageSizeComboBox.getSelectionModel().getSelectedItem().getUserData();
            final int pageSize = Integer.parseInt(userData);
            searchForm.setPageSize(pageSize);
            this.refreshTable();
        });
        pageTextField.getTextField().setAlignment(Pos.CENTER);
        final Runnable toPage = () -> {
            final String text = pageTextField.getText();
            if (StrUtil.isBlank(text)) {
                searchForm.setPage(1);
            }
            if (!NumberUtil.isInteger(text)) {
                return;
            }
            final int page = Integer.parseInt(text);
            searchForm.setPage(page);
            this.refreshTable();
        };
        pageTextField.getTextField().setOnAction(event -> toPage.run());
        pageTextField.getTextField().textProperty().addListener(event -> toPage.run());
    }

    /**
     * 搜索按钮事件
     */
    private void searchAction() {
        searchForm.getWhereItems().clear();
        searchForm.getOrderItems().clear();
        searchFormControlMap.forEach((fieldName, control) -> {
            if (StrUtil.isBlank(fieldName)) {
                return;
            }
            final AbstractFormControlFactory controlFactory = searchFormControlFactoryMap.get(fieldName);
            String value = controlFactory.getValue();
            if (StrUtil.isBlank(value)) {
                return;
            }
            // 若字段是继承与IEnum的枚举类才支持，则需要枚举转换
            if (controlFactory.getClassField().getType().isEnum() && IEnum.class.isAssignableFrom(
                controlFactory.getClassField().getType())) {
                final Field enumField = ReflectUtil.getField(controlFactory.getClassField().getType(), value);
                try {
                    Object enumObject = enumField.get(enumField.getName());
                    value = ((IEnum<?>) enumObject).getValue().toString();
                } catch (IllegalAccessException exception) {
                    log.error(exception.getMessage(), exception);
                    return;
                }
                final WhereItem whereItem = new WhereItem();
                whereItem.setColumn(fieldName);
                whereItem.setValue(value);
                whereItem.setCondition(Condition.EQ);
                searchForm.addWhereItem(whereItem);
            }
            final WhereItem whereItem = new WhereItem();
            whereItem.setColumn(fieldName);
            whereItem.setValue(value);
            whereItem.setCondition(Condition.LIKE);
            searchForm.addWhereItem(whereItem);
        });
        if (startDatePicker.getValue() != null && endDatePicker.getValue() != null) {
            final WhereItem whereItem = new WhereItem();
            whereItem.setColumn("createTime");
            final ArrayList<String> value = new ArrayList<>();
            value.add(startDatePicker.getValue().toString());
            value.add(endDatePicker.getValue().toString());
            whereItem.setValue(value);
            whereItem.setCondition(Condition.RANGE_TIME);
            searchForm.addWhereItem(whereItem);
        }
        this.refreshTable();
    }

    /************************************************************************************************
     * 外部方法                                                                                      *
     ************************************************************************************************/
    /**
     * 设置搜索表单约束
     *
     * @param schemaClass 约束类
     */
    @SneakyThrows
    public void initSearchFormSchema(@NotNull Class<?> schemaClass) {
        if (schemaClass == null) {
            throw new NullPointerException("Search form column schema is null");
        }
        final Field[] fields = schemaClass.getDeclaredFields();
        for (Field field : fields) {
            String title = StrUtil.EMPTY; // 标题
            Class<? extends AbstractFormControlFactory> controlFactoryClass = TextFieldControlFactory.class; // 控件工厂
            String fieldName = field.getName(); // 对应字段名
            boolean show = true;
            if (field.isAnnotationPresent(JFXFormField.class)) {
                final JFXFormField annotation = field.getDeclaredAnnotation(JFXFormField.class);
                if (!annotation.exists()) {
                    continue;
                }
                title = annotation.title();
                controlFactoryClass = annotation.controlFactory();
                if (StrUtil.isNotBlank(annotation.name())) {
                    fieldName = annotation.name();
                }
                show = annotation.show();
            }
            final AbstractFormControlFactory controlFactory = controlFactoryClass.newInstance();
            controlFactory.setSchemaClass(schemaClass);
            controlFactory.setClassField(field);
            Node control = controlFactory.getControl(title);
            if (control instanceof IFXLabelFloatControl) {
                ((IFXLabelFloatControl) control).setLabelFloat(true);
            }
            if (!show) {
                control.setManaged(false);
                control.setVisible(false);
            }
            this.searchFormControlFactoryMap.put(fieldName, controlFactory);
            this.searchFormControlMap.put(fieldName, control);
            // 添加到表单容器
            if (searchFormExpandProperty().get()) {
                this.searchFormFieldPane.getChildren().add(control);
            }
        }
    }

    /**
     * 设置表格列约束
     *
     * @param schemaClass 约束类
     */
    @SneakyThrows
    public void initTableColumnSchema(@NotNull Class<S> schemaClass) {
        if (schemaClass == null) {
            throw new NullPointerException("Table schema is null");
        }
        this.tableSchemaClass = schemaClass;
        final List<String> notExistFieldNames = new ArrayList<>();
        final Field[] fields = schemaClass.getDeclaredFields();
        final ObservableList<TableColumn<S, ?>> schemaColumns = FXCollections.observableArrayList();
        for (Field field : fields) {
            String title = field.getName(); // 列标题
            double min = 0; // 最小宽度
            double pref = 120; // 显示宽度
            double max = Double.MAX_VALUE; // 最大宽度
            boolean sortable = true; // 是否允许排序
            String fieldName = field.getName(); // 对应字段名
            AbstractTableCellFactory cellFactory = null; // 单元格工厂
            boolean exists = true;
            if (field.isAnnotationPresent(JFXTableColumn.class)) {
                final JFXTableColumn annotation = field.getDeclaredAnnotation(JFXTableColumn.class);
                if (!annotation.show()) {
                    continue;
                }
                title = annotation.title();
                min = annotation.min();
                pref = annotation.pref();
                max = annotation.max();
                sortable = annotation.sortable();
                if (StrUtil.isNotBlank(annotation.fieldName())) {
                    fieldName = annotation.fieldName();
                }
                if (annotation.cellFactory().length > 0) {
                    final Class<? extends AbstractTableCellFactory<? extends TableItem, ?>> cellFactoryClazz
                        = annotation.cellFactory()[0];
                    cellFactory = cellFactoryClazz.newInstance();
                }
                exists = annotation.exists();
            }
            // 如果没有设置cellFactory，且类型继承与IEnum的枚举类，则默认使用EnumTableCellFactory
            if (cellFactory == null && field.getType().isEnum() && IEnum.class.isAssignableFrom(field.getType())) {
                cellFactory = new EnumTableCellFactory();
            }
            final TableColumn<S, Object> column = new TableColumn<>(title);
            column.setMinWidth(min);
            column.setPrefWidth(pref);
            if (max != Double.MAX_VALUE) {
                column.setMaxWidth(max);
            }
            column.setSortable(sortable);
            if (exists) {
                column.setCellValueFactory(new PropertyValueFactory<>(fieldName));
            } else {
                notExistFieldNames.add(fieldName);
            }
            if (cellFactory != null) {
                column.setCellFactory(cellFactory);
            }
            schemaColumns.add(column);
        }
        this.notExistFieldNames = notExistFieldNames.toArray(new String[0]);
        tableView.getColumns().add(numberColumn);
        tableView.getColumns().addAll(schemaColumns);
        tableView.getColumns().add(operateColumn);
    }

    /**
     * 操作列回调
     *
     * @param operateCallback 操作列回调
     */
    public void setOperateCallback(BiFunction<TableColumn<S, Node>, TableCell<S, Node>, Node> operateCallback) {
        final OperateCellFactory<S> factory = new OperateCellFactory<>(operateCallback);
        operateColumn.setCellFactory(factory);
    }

    /**
     * 设置表格数据api接口
     *
     * @param api 表格数据api接口
     */
    public void setApi(Function<SearchForm, SearchResult<?>> api) {
        this.api = api;
        refreshTable();
    }

    /**
     * 刷新表格数据
     */
    public void refreshTable() {
        if (api == null) {
            tableView.getItems().clear();
            log.error("Please provide a table data API");
            Alert.error("请设置表格数据api接口");
            return;
        }
        ProcessChain.create()
            .addRunnableInPlatformThread(() -> tableView.setDisable(true))
            .addSupplierInExecutor(() -> {
                log.info("SearchForm: {}", searchForm);
                return api.apply(searchForm);
            })
            .addConsumerInPlatformThread(result -> {
                tableView.getItems().clear();
                // 实体数据转换
                for (Object item : result.getItems()) {
                    final S tableItem = BeanUtil.copyProperties(item, this.tableSchemaClass, this.notExistFieldNames);
                    tableView.getItems().add(tableItem);
                }
                // 刷新分页
                pageTotalLabel.setText("共" + result.getTotal() + "条数据");
                pagination.setCurrentPageIndex((int) (result.getPage() > 0 ? result.getPage() - 1 : result.getPage()));
                pagination.setPageCount((int) result.getPageCount());
            })
            .onException(exception -> {
                log.error(exception.getMessage(), exception);
                Alert.error("发生未知错误", exception.getMessage());
            })
            .withFinal(() -> tableView.setDisable(false))
            .run();
    }

    /**
     * 设置表格标题
     *
     * @param title 标题
     */
    public void setTableTitle(String title) {
        this.setTableTitle(title, null);
    }

    /**
     * 设置表格标题
     *
     * @param title 标题
     * @param graphic 图标
     */
    public void setTableTitle(String title, Node graphic) {
        this.tableTitle.setText(title);
        this.tableTitle.setGraphic(graphic);
    }

    /**
     * 设置操作列宽
     *
     * @param width 列宽
     */
    public void setOperateWidth(double width) {
        this.operateColumn.setMinWidth(width);
        this.operateColumn.setMaxWidth(width);
    }

    /**
     * 工具栏
     *
     * @return 工具栏
     */
    public HBox getToolBar() {
        return this.toolBar;
    }

    /**
     * 获取选中的行
     *
     * @return 选中的行
     */
    public List<S> getSelectedItems() {
        return tableView.getSelectionModel().getSelectedItems();
    }

    /************************************************************************************************
     * setter、getter、property                                                                      *
     ************************************************************************************************/
    public String getTitle() {
        return title.get();
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public boolean isShowSearchForm() {
        return showSearchForm.get();
    }

    public SimpleBooleanProperty showSearchFormProperty() {
        return showSearchForm;
    }

    public void setShowSearchForm(boolean showSearchForm) {
        this.showSearchForm.set(showSearchForm);
    }

    public boolean isSearchFormExpand() {
        return searchFormExpand.get();
    }

    public SimpleBooleanProperty searchFormExpandProperty() {
        return searchFormExpand;
    }

    public void setSearchFormExpand(boolean searchFormExpand) {
        this.searchFormExpand.set(searchFormExpand);
    }

    public boolean isShowNumberColumn() {
        return showNumberColumn.get();
    }

    public SimpleBooleanProperty showNumberColumnProperty() {
        return showNumberColumn;
    }

    public void setShowNumberColumn(boolean showNumberColumn) {
        this.showNumberColumn.set(showNumberColumn);
    }

    public boolean isShowOperateColumn() {
        return showOperateColumn.get();
    }

    public SimpleBooleanProperty showOperateColumnProperty() {
        return showOperateColumn;
    }

    public void setShowOperateColumn(boolean showOperateColumn) {
        this.showOperateColumn.set(showOperateColumn);
    }
}
