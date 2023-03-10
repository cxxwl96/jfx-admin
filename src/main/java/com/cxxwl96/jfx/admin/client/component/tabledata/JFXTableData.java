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
 * ???????????????????????????????????????????????????????????????????????????
 *
 * @param <S> ????????????????????????
 * @author cxxwl96
 * @since 2022/10/17 23:31
 */
@Slf4j
@ViewController("/views/component/table/JFXTableData.fxml")
public class JFXTableData<S extends TableItem> extends JFXComponent {
    /************************************************************************************************
     * ????????????                                                                                       *
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
     * ??????????????????                                                                                   *
     ************************************************************************************************/
    // ??????
    private SimpleStringProperty title;

    // ????????????????????????
    private SimpleBooleanProperty showSearchForm;

    // ????????????????????????
    private SimpleBooleanProperty searchFormExpand;

    // ?????????????????????
    private SimpleBooleanProperty showNumberColumn;

    // ?????????????????????
    private SimpleBooleanProperty showOperateColumn;

    /************************************************************************************************
     * ?????????                                                                                        *
     ************************************************************************************************/
    // ?????????????????????????????????????????????
    private Map<String, AbstractFormControlFactory> searchFormControlFactoryMap;

    // ???????????????????????????????????????
    private Map<String, Node> searchFormControlMap;

    // ????????????????????????
    private AbstractFormControlFactory startDatePickerControlFactory;

    // ????????????????????????
    private AbstractFormControlFactory endDatePickerControlFactory;

    // ?????????
    private Class<S> tableSchemaClass;

    // ?????????????????????????????????????????????????????????????????????
    private String[] notExistFieldNames = new String[0];

    // ????????????api??????
    private Function<SearchForm, SearchResult<?>> api;

    // ????????????
    private final SearchForm searchForm = new SearchForm();

    // ?????????
    private TableColumn<S, Integer> numberColumn;

    // ?????????
    private TableColumn<S, Node> operateColumn;

    /************************************************************************************************
     * ????????????????????????                                                                                *
     ************************************************************************************************/
    @PostConstruct
    private void init() {
        title = new SimpleStringProperty();
        tableTitle.textProperty().bindBidirectional(title);
        showSearchForm = new SimpleBooleanProperty(true);
        searchFormExpand = new SimpleBooleanProperty(false);
        showNumberColumn = new SimpleBooleanProperty(true);
        showOperateColumn = new SimpleBooleanProperty(true);
        // ?????????????????????
        initSearchForm();
        // ???????????????
        initTableView();
        // ???????????????
        initPagination();
    }

    /**
     * ?????????????????????
     */
    private void initSearchForm() {
        searchFormControlFactoryMap = new HashMap<>();
        searchFormControlMap = new HashMap<>();
        // ?????????????????????????????????????????????????????????
        startDatePickerControlFactory = new DatePickerControlFactory();
        endDatePickerControlFactory = new DatePickerControlFactory();
        startDatePicker = (JFXDatePicker) startDatePickerControlFactory.getControl("????????????");
        endDatePicker = (JFXDatePicker) endDatePickerControlFactory.getControl("????????????");
        // ????????????????????????????????????
        searchFormPane.managedProperty().bindBidirectional(this.showSearchFormProperty());
        // ?????????spinner
        searchFormSpinner.visibleProperty().bind(searchFormPane.disableProperty());
        // ?????????????????????
        initSearchFormResetButton();
        // ?????????????????????
        initSearchFormSubmitButton();
        // ?????????????????????
        initSearchFormExpandButton();
    }

    /**
     * ?????????????????????
     */
    private void initSearchFormResetButton() {
        searchFormResetButton.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
            searchFormControlFactoryMap.values().forEach(controlFactory -> controlFactory.setValue(null));
        });
    }

    /**
     * ?????????????????????
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
                Alert.error("??????????????????", exception.getMessage());
            })
            .withFinal(() -> searchFormPane.setDisable(false))
            .run());
    }

    /**
     * ?????????????????????
     */
    private void initSearchFormExpandButton() {
        // ??????/????????????
        final Consumer<Boolean> expandSearchFormFieldPane = (expand) -> {
            searchFormFieldPane.getChildren().clear();
            searchFormFieldPane.getChildren().addAll(startDatePicker, endDatePicker);
            if (expand) {
                // ??????
                searchFormFieldPane.getChildren().addAll(searchFormControlMap.values());
                searchFormExpandButton.setGraphic(IconUtil.getIcon("angle-up"));
                searchFormExpandButton.setText("??????");
            } else {
                // ??????
                searchFormExpandButton.setGraphic(IconUtil.getIcon("angle-down"));
                searchFormExpandButton.setText("??????");
            }
        };
        // ????????????
        expandSearchFormFieldPane.accept(false);
        // ????????????
        this.searchFormExpandProperty()
            .addListener((observable, oldValue, newValue) -> expandSearchFormFieldPane.accept(newValue));
        searchFormExpandButton.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            final boolean expand = this.searchFormExpandProperty().get();
            this.searchFormExpandProperty().set(!expand);
        });
    }

    /**
     * ???????????????
     */
    private void initTableView() {
        // ??????????????????
        refreshTableButton.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> this.refreshTable());
        // table spinner??????
        tableViewSpinner.visibleProperty().bind(tableView.disableProperty());
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // ??????????????????
        numberColumn = new TableColumn<>("??????");
        numberColumn.setMinWidth(40);
        numberColumn.setPrefWidth(40);
        numberColumn.setMaxWidth(40);
        numberColumn.setSortable(false);
        numberColumn.setCellFactory(new NumberCellFactory<>());
        numberColumn.visibleProperty().bindBidirectional(showNumberColumn);
        // ??????????????????
        operateColumn = new TableColumn<S, Node>("??????");
        operateColumn.setMinWidth(120);
        operateColumn.setMaxWidth(120);
        operateColumn.setSortable(false);
        operateColumn.visibleProperty().bindBidirectional(showOperateColumn);
    }

    /**
     * ???????????????
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
     * ??????????????????
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
            // ?????????????????????IEnum?????????????????????????????????????????????
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
     * ????????????                                                                                      *
     ************************************************************************************************/
    /**
     * ????????????????????????
     *
     * @param schemaClass ?????????
     */
    @SneakyThrows
    public void initSearchFormSchema(@NotNull Class<?> schemaClass) {
        if (schemaClass == null) {
            throw new NullPointerException("Search form column schema is null");
        }
        final Field[] fields = schemaClass.getDeclaredFields();
        for (Field field : fields) {
            String title = StrUtil.EMPTY; // ??????
            Class<? extends AbstractFormControlFactory> controlFactoryClass = TextFieldControlFactory.class; // ????????????
            String fieldName = field.getName(); // ???????????????
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
            // ?????????????????????
            if (searchFormExpandProperty().get()) {
                this.searchFormFieldPane.getChildren().add(control);
            }
        }
    }

    /**
     * ?????????????????????
     *
     * @param schemaClass ?????????
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
            String title = field.getName(); // ?????????
            double min = 0; // ????????????
            double pref = 120; // ????????????
            double max = Double.MAX_VALUE; // ????????????
            boolean sortable = true; // ??????????????????
            String fieldName = field.getName(); // ???????????????
            AbstractTableCellFactory cellFactory = null; // ???????????????
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
            // ??????????????????cellFactory?????????????????????IEnum??????????????????????????????EnumTableCellFactory
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
     * ???????????????
     *
     * @param operateCallback ???????????????
     */
    public void setOperateCallback(BiFunction<TableColumn<S, Node>, TableCell<S, Node>, Node> operateCallback) {
        final OperateCellFactory<S> factory = new OperateCellFactory<>(operateCallback);
        operateColumn.setCellFactory(factory);
    }

    /**
     * ??????????????????api??????
     *
     * @param api ????????????api??????
     */
    public void setApi(Function<SearchForm, SearchResult<?>> api) {
        this.api = api;
        refreshTable();
    }

    /**
     * ??????????????????
     */
    public void refreshTable() {
        if (api == null) {
            tableView.getItems().clear();
            log.error("Please provide a table data API");
            Alert.error("?????????????????????api??????");
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
                // ??????????????????
                for (Object item : result.getItems()) {
                    final S tableItem = BeanUtil.copyProperties(item, this.tableSchemaClass, this.notExistFieldNames);
                    tableView.getItems().add(tableItem);
                }
                // ????????????
                pageTotalLabel.setText("???" + result.getTotal() + "?????????");
                pagination.setCurrentPageIndex((int) (result.getPage() > 0 ? result.getPage() - 1 : result.getPage()));
                pagination.setPageCount((int) result.getPageCount());
            })
            .onException(exception -> {
                log.error(exception.getMessage(), exception);
                Alert.error("??????????????????", exception.getMessage());
            })
            .withFinal(() -> tableView.setDisable(false))
            .run();
    }

    /**
     * ??????????????????
     *
     * @param title ??????
     */
    public void setTableTitle(String title) {
        this.setTableTitle(title, null);
    }

    /**
     * ??????????????????
     *
     * @param title ??????
     * @param graphic ??????
     */
    public void setTableTitle(String title, Node graphic) {
        this.tableTitle.setText(title);
        this.tableTitle.setGraphic(graphic);
    }

    /**
     * ??????????????????
     *
     * @param width ??????
     */
    public void setOperateWidth(double width) {
        this.operateColumn.setMinWidth(width);
        this.operateColumn.setMaxWidth(width);
    }

    /**
     * ?????????
     *
     * @return ?????????
     */
    public HBox getToolBar() {
        return this.toolBar;
    }

    /**
     * ??????????????????
     *
     * @return ????????????
     */
    public List<S> getSelectedItems() {
        return tableView.getSelectionModel().getSelectedItems();
    }

    /************************************************************************************************
     * setter???getter???property                                                                      *
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
