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

package com.cxxwl96.jfx.admin.client.component.form;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cxxwl96.jfx.admin.client.common.JFXComponent;
import com.cxxwl96.jfx.admin.client.utils.Alert;
import com.cxxwl96.jfx.admin.client.utils.IconUtil;
import com.cxxwl96.jfx.admin.server.entity.User;
import com.cxxwl96.jfx.admin.server.lang.Result;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSpinner;
import com.sun.javafx.collections.TrackableObservableList;

import java.lang.reflect.Field;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import io.datafx.controller.ViewController;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.DefaultProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * JFXForm
 *
 * @param <T> 表单参数数据类型
 * @author cxxwl96
 * @since 2022/10/26 21:36
 */
@Slf4j
@ViewController("/views/component/form/JFXForm.fxml")
@DefaultProperty("formFields")
public class JFXForm<T> extends JFXComponent {
    /************************************************************************************************
     * 视图对象                                                                                       *
     ************************************************************************************************/
    @FXML
    private VBox rootPane;

    @FXML
    private Label titleLabel;

    @FXML
    private GridPane formGrid;

    @FXML
    private JFXSpinner spinner;

    @FXML
    private JFXButton resetBtn;

    @FXML
    private JFXButton submitBtn;

    @FXML
    private JFXButton cancelBtn;

    /************************************************************************************************
     * 视图绑定对象                                                                                   *
     ************************************************************************************************/

    // 表单标题
    private SimpleStringProperty title;

    // 是否显示重置按钮
    private SimpleBooleanProperty showResetButton;

    // 是否显示提交按钮
    private SimpleBooleanProperty showSubmitButton;

    // 是否显示取消按钮
    private SimpleBooleanProperty showCancelButton;

    // 表单字段
    private final ObservableList<JFXFormField> formFields = new TrackableObservableList<JFXFormField>() {
        @Override
        protected void onChanged(ListChangeListener.Change<JFXFormField> change) {
            // 取最大行最大列，此处不能使用流方式取
            int maxRow = 0;
            int maxCol = 0;
            for (JFXFormField formField : formFields) {
                maxRow = Math.max(formField.getRowIndex(), maxRow);
                maxCol = Math.max(formField.getColumnIndex(), maxCol);
            }
            // 添加行列约束
            formGrid.getRowConstraints().clear();
            formGrid.getColumnConstraints().clear();
            formGrid.getChildren().clear();
            for (int i = 0; i <= maxRow; i++) {
                final RowConstraints constraints = new RowConstraints();
                constraints.setFillHeight(false);
                constraints.setValignment(VPos.BOTTOM);
                formGrid.getRowConstraints().add(constraints);
            }
            // 添加列约束
            for (int i = 0; i <= maxCol; i++) {
                final ColumnConstraints labelConstraints = new ColumnConstraints();
                labelConstraints.setFillWidth(false);
                labelConstraints.setHalignment(HPos.RIGHT);
                labelConstraints.setHgrow(Priority.ALWAYS);
                labelConstraints.setMinWidth(50);
                labelConstraints.setPrefWidth(100);
                labelConstraints.setMaxWidth(100);
                final ColumnConstraints controlConstraints = new ColumnConstraints();
                controlConstraints.setHalignment(HPos.LEFT);
                controlConstraints.setHgrow(Priority.ALWAYS);
                controlConstraints.setMinWidth(50);
                controlConstraints.setPrefWidth(200);
                formGrid.getColumnConstraints().addAll(labelConstraints, controlConstraints);
            }
            // 添加表单label及控件
            for (JFXFormField formField : formFields) {
                if (!formField.isShow()) {
                    continue;
                }
                final HBox labelHBox = createLabelHBox(formField);
                GridPane.setRowIndex(labelHBox, formField.getRowIndex());
                GridPane.setColumnIndex(labelHBox, formField.getColumnIndex() * 2);
                GridPane.setRowSpan(labelHBox, formField.getRowSpan());
                formGrid.getChildren().add(labelHBox);
                final Node control = formField.getControl();
                GridPane.setRowIndex(control, formField.getRowIndex());
                GridPane.setColumnIndex(control, formField.getColumnIndex() * 2 + 1);
                GridPane.setRowSpan(control, formField.getRowSpan());
                GridPane.setColumnSpan(control, formField.getColumnSpan() * 2 - 1);
                formGrid.getChildren().add(control);
            }
        }
    };

    /************************************************************************************************
     * 类属性                                                                                        *
     ************************************************************************************************/
    // 重置前置事件处理
    private Consumer<ActionEvent> resetHandler;

    // 提交前置事件处理
    private Consumer<ActionEvent> submitHandler;

    // 提交后置事件处理
    private Consumer<Result<T>> submittedHandler;

    // 取消前置事件处理
    private Consumer<ActionEvent> cancelHandler;

    // 表单数据添加/编辑api接口
    private Function<T, Result<T>> api;

    // 表单数据类型
    private Class<T> formDataClass;

    // 提交表单是否清空表单数据
    @Setter
    @Getter
    private boolean resetFormOnSubmit;

    /************************************************************************************************
     * 初始化页面及事件                                                                                *
     ************************************************************************************************/

    @PostConstruct
    private void init() {
        // 初始化表单标题
        title = new SimpleStringProperty();
        titleLabel.textProperty().bindBidirectional(title);
        // 初始化表单按钮
        showResetButton = new SimpleBooleanProperty(true);
        showSubmitButton = new SimpleBooleanProperty(true);
        showCancelButton = new SimpleBooleanProperty(true);
        resetBtn.managedProperty().bind(showResetButton);
        submitBtn.managedProperty().bind(showSubmitButton);
        cancelBtn.managedProperty().bind(showCancelButton);
        resetBtn.setOnAction(this::resetHandler);
        submitBtn.setOnAction(this::submitHandler);
        cancelBtn.setOnAction(this::cancelHandler);
        // 初始化spinner
        spinner.visibleProperty().bind(rootPane.disableProperty());
    }

    /**
     * 创建表单字段labelHBox
     *
     * @param formField form field
     * @return 表单字段labelHBox
     */
    private HBox createLabelHBox(JFXFormField formField) {
        final HBox labelHBox = new HBox();
        final Text requireText = new Text("*");
        requireText.setFill(Paint.valueOf("RED"));
        requireText.setStrokeType(StrokeType.OUTSIDE);
        requireText.setStrokeWidth(0);
        requireText.managedProperty().bind(formField.requireProperty());
        final Label label = new Label(formField.getTitle(), requireText);
        if (StrUtil.isNotBlank(formField.getTitle())) {
            label.setTooltip(new Tooltip(formField.getTitle()));
        }
        labelHBox.getChildren().add(label);
        if (StrUtil.isNotBlank(formField.getHelpText())) {
            final Label help = new Label();
            help.setGraphic(IconUtil.getIcon("info-circle"));
            help.setTooltip(new Tooltip(formField.getHelpText()));
            labelHBox.getChildren().add(help);
        }
        return labelHBox;
    }

    /**
     * 重置事件处理
     *
     * @param event 事件
     */
    private void resetHandler(ActionEvent event) {
        // 重置前置事件处理
        if (resetHandler != null) {
            resetHandler.accept(event);
            if (event.isConsumed()) {
                return;
            }
        }
        resetForm();
    }

    /**
     * 提交事件处理
     *
     * @param event 事件
     */
    private void submitHandler(ActionEvent event) {
        // 提交前置事件处理
        if (submitHandler != null) {
            submitHandler.accept(event);
            if (event.isConsumed()) {
                return;
            }
        }
        if (api == null) {
            log.error("Please provide a save API");
            Alert.error("请设置保存api接口");
            return;
        }
        try {
            // 校验表单
            final Result<String> result = validateForm();
            if (!result.isSuccess()) {
                return;
            }
        } catch (ValidationException exception) {
            Alert.error(exception.getMessage());
            return;
        }
        ProcessChain.create().addRunnableInPlatformThread(() -> rootPane.setDisable(true)).addSupplierInExecutor(() -> {
            // 获取表单数据
            final T formData = getFormData(formDataClass);
            if (formData instanceof User) {
                // 日志打印去除密码
                final User user = (User) formData;
                final User tempUser = new User();
                BeanUtil.copyProperties(user, tempUser, "password");
                log.info("SubmitForm: {}", tempUser);
            } else {
                log.info("SubmitForm: {}", formData);
            }
            // 提交表单
            return api.apply(formData);
        }).addConsumerInPlatformThread(result -> {
            result.successAndThen(res -> {
                if (resetFormOnSubmit) {
                    resetForm(); // 提交成功后清空表单数据
                }
                Alert.info(result.getMsg());
            }).failedAndThen((code, msg) -> Alert.error(msg));
            // 提交后置事件处理
            if (submittedHandler != null) {
                submittedHandler.accept(result);
            }
        }).onException(exception -> {
            log.error(exception.getMessage(), exception);
            Alert.error("发生未知错误", exception.getMessage());
        }).withFinal(() -> rootPane.setDisable(false)).run();
    }

    /**
     * 取消事件处理
     *
     * @param event 事件
     */
    private void cancelHandler(ActionEvent event) {
        // 取消前置事件处理
        if (cancelHandler != null) {
            cancelHandler.accept(event);
            if (event.isConsumed()) {
                return;
            }
        }
        resetForm();
    }

    /************************************************************************************************
     * 外部方法                                                                                      *
     ************************************************************************************************/

    /**
     * 设置表单约束
     *
     * @param schemaClass 约束类
     */
    @SneakyThrows
    public void initFormSchema(@NotNull Class<?> schemaClass) {
        if (schemaClass == null) {
            throw new NullPointerException("Search form column schema is null");
        }
        final Field[] fields = schemaClass.getDeclaredFields();
        for (Field field : fields) {
            String title = StrUtil.EMPTY; // 标题
            boolean required = false; // 是否必填
            String help = StrUtil.EMPTY; // 提示
            Class<? extends AbstractFormControlFactory> controlFactoryClass = TextFieldControlFactory.class; // 控件工厂
            String name = field.getName(); // 对应字段名
            boolean show = true;
            int rowIndex = 0;
            int columnIndex = 0;
            int rowSpan = 1;
            int columnSpan = 1;
            String pattern = StrUtil.EMPTY;
            String message = StrUtil.EMPTY;
            if (field.isAnnotationPresent(com.cxxwl96.jfx.admin.client.annotation.JFXFormField.class)) {
                final com.cxxwl96.jfx.admin.client.annotation.JFXFormField annotation = field.getDeclaredAnnotation(
                    com.cxxwl96.jfx.admin.client.annotation.JFXFormField.class);
                if (!annotation.exists()) {
                    continue;
                }
                title = annotation.title();
                required = annotation.required();
                help = annotation.help();
                controlFactoryClass = annotation.controlFactory();
                if (StrUtil.isNotBlank(annotation.name())) {
                    name = annotation.name();
                }
                show = annotation.show();
                rowIndex = annotation.rowIndex();
                columnIndex = annotation.columnIndex();
                rowSpan = annotation.rowSpan();
                columnSpan = annotation.columnSpan();
                pattern = annotation.pattern();
                message = annotation.message();
            }
            // 添加到表单容器
            final JFXFormField formField = new JFXFormField(rowIndex, columnIndex);
            formField.setRowSpan(rowSpan);
            formField.setColumnSpan(columnSpan);
            formField.setRequire(required);
            formField.setTitle(title);
            formField.setHelpText(help);
            formField.setName(name);
            formField.setShow(show);
            formField.setPattern(pattern);
            formField.setMessage(message);
            // 创建控件工厂
            final AbstractFormControlFactory controlFactory = controlFactoryClass.newInstance();
            controlFactory.setSchemaClass(schemaClass); // 设置约束类
            controlFactory.setClassField(field); // 设置约束字段
            formField.setControlFactory(controlFactory);
            formFields.add(formField);
        }
    }

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
     * 提交API接口
     *
     * @param api 提交API接口
     * @param formDataClass 表单数据类型
     */
    public void setApi(@NotNull Function<T, Result<T>> api, @NotNull Class<T> formDataClass) {
        this.api = api;
        this.formDataClass = formDataClass;
    }

    /**
     * 重置前置事件处理
     *
     * @param handler 前置事件处理
     */
    public void onReset(Consumer<ActionEvent> handler) {
        resetHandler = handler;
    }

    /**
     * 提交前置事件处理
     *
     * @param handler 前置事件处理
     */
    public void onSubmit(Consumer<ActionEvent> handler) {
        submitHandler = handler;
    }

    /**
     * 提交后置事件处理
     *
     * @param handler 后置事件处理
     */
    public void onSubmitted(Consumer<Result<T>> handler) {
        submittedHandler = handler;
    }

    /**
     * 取消前置事件处理
     *
     * @param handler 前置事件处理
     */
    public void onCancel(Consumer<ActionEvent> handler) {
        cancelHandler = handler;
    }

    /**
     * 校验表单
     *
     * @return 校验结果
     * @throws ValidationException 不支持IFXValidatableControl的控件且校验失败的异常
     */
    public Result<String> validateForm() throws ValidationException {
        for (JFXFormField formField : formFields) {
            final Result<String> result = formField.validate();
            if (!result.isSuccess()) {
                return result;
            }
        }
        return Result.success();
    }

    /**
     * 获取表单数据
     *
     * @param formDataClass 表单数据类型
     * @return 表单数据
     */
    public T getFormData(@NotNull Class<T> formDataClass) {
        if (formDataClass == null) {
            throw new NullPointerException("FormDataClass is null");
        }
        // 获取表单数据
        JSONObject json = new JSONObject();
        formFields.forEach(formField -> {
            String name = formField.getName();
            String value = formField.getValue();
            json.put(name, value);
        });
        return json.toJavaObject(formDataClass);
    }

    /**
     * 设置表单数据
     *
     * @param formData 表单数据
     */
    public void setFormData(@NotNull T formData) {
        if (formData == null) {
            throw new NullPointerException("FormData is null");
        }
        // 设置表单数据前清空表单数据
        resetForm();
        // 设置表单数据
        JSONObject object = JSON.parseObject(JSON.toJSONString(formData));
        formFields.forEach(formField -> {
            String value = object.getString(formField.getName());
            formField.setValue(value);
        });
    }

    /**
     * 重置表单
     */
    public void resetForm() {
        formFields.forEach(field -> field.setValue(StrUtil.EMPTY));
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

    public ObservableList<JFXFormField> getFormFields() {
        return formFields;
    }

    public JFXButton getResetBtn() {
        return resetBtn;
    }

    public JFXButton getSubmitBtn() {
        return submitBtn;
    }

    public JFXButton getCancelBtn() {
        return cancelBtn;
    }

    public boolean isShowResetButton() {
        return showResetButton.get();
    }

    public SimpleBooleanProperty showResetButtonProperty() {
        return showResetButton;
    }

    public void setShowResetButton(boolean showResetButton) {
        this.showResetButton.set(showResetButton);
    }

    public boolean isShowSubmitButton() {
        return showSubmitButton.get();
    }

    public SimpleBooleanProperty showSubmitButtonProperty() {
        return showSubmitButton;
    }

    public void setShowSubmitButton(boolean showSubmitButton) {
        this.showSubmitButton.set(showSubmitButton);
    }

    public boolean isShowCancelButton() {
        return showCancelButton.get();
    }

    public SimpleBooleanProperty showCancelButtonProperty() {
        return showCancelButton;
    }

    public void setShowCancelButton(boolean showCancelButton) {
        this.showCancelButton.set(showCancelButton);
    }
}
