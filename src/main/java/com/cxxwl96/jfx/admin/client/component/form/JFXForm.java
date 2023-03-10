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
 * @param <T> ????????????????????????
 * @author cxxwl96
 * @since 2022/10/26 21:36
 */
@Slf4j
@ViewController("/views/component/form/JFXForm.fxml")
@DefaultProperty("formFields")
public class JFXForm<T> extends JFXComponent {
    /************************************************************************************************
     * ????????????                                                                                       *
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
     * ??????????????????                                                                                   *
     ************************************************************************************************/

    // ????????????
    private SimpleStringProperty title;

    // ????????????????????????
    private SimpleBooleanProperty showResetButton;

    // ????????????????????????
    private SimpleBooleanProperty showSubmitButton;

    // ????????????????????????
    private SimpleBooleanProperty showCancelButton;

    // ????????????
    private final ObservableList<JFXFormField> formFields = new TrackableObservableList<JFXFormField>() {
        @Override
        protected void onChanged(ListChangeListener.Change<JFXFormField> change) {
            // ??????????????????????????????????????????????????????
            int maxRow = 0;
            int maxCol = 0;
            for (JFXFormField formField : formFields) {
                maxRow = Math.max(formField.getRowIndex(), maxRow);
                maxCol = Math.max(formField.getColumnIndex(), maxCol);
            }
            // ??????????????????
            formGrid.getRowConstraints().clear();
            formGrid.getColumnConstraints().clear();
            formGrid.getChildren().clear();
            for (int i = 0; i <= maxRow; i++) {
                final RowConstraints constraints = new RowConstraints();
                constraints.setFillHeight(false);
                constraints.setValignment(VPos.BOTTOM);
                formGrid.getRowConstraints().add(constraints);
            }
            // ???????????????
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
            // ????????????label?????????
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
     * ?????????                                                                                        *
     ************************************************************************************************/
    // ????????????????????????
    private Consumer<ActionEvent> resetHandler;

    // ????????????????????????
    private Consumer<ActionEvent> submitHandler;

    // ????????????????????????
    private Consumer<Result<T>> submittedHandler;

    // ????????????????????????
    private Consumer<ActionEvent> cancelHandler;

    // ??????????????????/??????api??????
    private Function<T, Result<T>> api;

    // ??????????????????
    private Class<T> formDataClass;

    // ????????????????????????????????????
    @Setter
    @Getter
    private boolean resetFormOnSubmit;

    /************************************************************************************************
     * ????????????????????????                                                                                *
     ************************************************************************************************/

    @PostConstruct
    private void init() {
        // ?????????????????????
        title = new SimpleStringProperty();
        titleLabel.textProperty().bindBidirectional(title);
        // ?????????????????????
        showResetButton = new SimpleBooleanProperty(true);
        showSubmitButton = new SimpleBooleanProperty(true);
        showCancelButton = new SimpleBooleanProperty(true);
        resetBtn.managedProperty().bind(showResetButton);
        submitBtn.managedProperty().bind(showSubmitButton);
        cancelBtn.managedProperty().bind(showCancelButton);
        resetBtn.setOnAction(this::resetHandler);
        submitBtn.setOnAction(this::submitHandler);
        cancelBtn.setOnAction(this::cancelHandler);
        // ?????????spinner
        spinner.visibleProperty().bind(rootPane.disableProperty());
    }

    /**
     * ??????????????????labelHBox
     *
     * @param formField form field
     * @return ????????????labelHBox
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
     * ??????????????????
     *
     * @param event ??????
     */
    private void resetHandler(ActionEvent event) {
        // ????????????????????????
        if (resetHandler != null) {
            resetHandler.accept(event);
            if (event.isConsumed()) {
                return;
            }
        }
        resetForm();
    }

    /**
     * ??????????????????
     *
     * @param event ??????
     */
    private void submitHandler(ActionEvent event) {
        // ????????????????????????
        if (submitHandler != null) {
            submitHandler.accept(event);
            if (event.isConsumed()) {
                return;
            }
        }
        if (api == null) {
            log.error("Please provide a save API");
            Alert.error("???????????????api??????");
            return;
        }
        try {
            // ????????????
            final Result<String> result = validateForm();
            if (!result.isSuccess()) {
                return;
            }
        } catch (ValidationException exception) {
            Alert.error(exception.getMessage());
            return;
        }
        ProcessChain.create().addRunnableInPlatformThread(() -> rootPane.setDisable(true)).addSupplierInExecutor(() -> {
            // ??????????????????
            final T formData = getFormData(formDataClass);
            if (formData instanceof User) {
                // ????????????????????????
                final User user = (User) formData;
                final User tempUser = new User();
                BeanUtil.copyProperties(user, tempUser, "password");
                log.info("SubmitForm: {}", tempUser);
            } else {
                log.info("SubmitForm: {}", formData);
            }
            // ????????????
            return api.apply(formData);
        }).addConsumerInPlatformThread(result -> {
            result.successAndThen(res -> {
                if (resetFormOnSubmit) {
                    resetForm(); // ?????????????????????????????????
                }
                Alert.info(result.getMsg());
            }).failedAndThen((code, msg) -> Alert.error(msg));
            // ????????????????????????
            if (submittedHandler != null) {
                submittedHandler.accept(result);
            }
        }).onException(exception -> {
            log.error(exception.getMessage(), exception);
            Alert.error("??????????????????", exception.getMessage());
        }).withFinal(() -> rootPane.setDisable(false)).run();
    }

    /**
     * ??????????????????
     *
     * @param event ??????
     */
    private void cancelHandler(ActionEvent event) {
        // ????????????????????????
        if (cancelHandler != null) {
            cancelHandler.accept(event);
            if (event.isConsumed()) {
                return;
            }
        }
        resetForm();
    }

    /************************************************************************************************
     * ????????????                                                                                      *
     ************************************************************************************************/

    /**
     * ??????????????????
     *
     * @param schemaClass ?????????
     */
    @SneakyThrows
    public void initFormSchema(@NotNull Class<?> schemaClass) {
        if (schemaClass == null) {
            throw new NullPointerException("Search form column schema is null");
        }
        final Field[] fields = schemaClass.getDeclaredFields();
        for (Field field : fields) {
            String title = StrUtil.EMPTY; // ??????
            boolean required = false; // ????????????
            String help = StrUtil.EMPTY; // ??????
            Class<? extends AbstractFormControlFactory> controlFactoryClass = TextFieldControlFactory.class; // ????????????
            String name = field.getName(); // ???????????????
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
            // ?????????????????????
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
            // ??????????????????
            final AbstractFormControlFactory controlFactory = controlFactoryClass.newInstance();
            controlFactory.setSchemaClass(schemaClass); // ???????????????
            controlFactory.setClassField(field); // ??????????????????
            formField.setControlFactory(controlFactory);
            formFields.add(formField);
        }
    }

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
     * ??????API??????
     *
     * @param api ??????API??????
     * @param formDataClass ??????????????????
     */
    public void setApi(@NotNull Function<T, Result<T>> api, @NotNull Class<T> formDataClass) {
        this.api = api;
        this.formDataClass = formDataClass;
    }

    /**
     * ????????????????????????
     *
     * @param handler ??????????????????
     */
    public void onReset(Consumer<ActionEvent> handler) {
        resetHandler = handler;
    }

    /**
     * ????????????????????????
     *
     * @param handler ??????????????????
     */
    public void onSubmit(Consumer<ActionEvent> handler) {
        submitHandler = handler;
    }

    /**
     * ????????????????????????
     *
     * @param handler ??????????????????
     */
    public void onSubmitted(Consumer<Result<T>> handler) {
        submittedHandler = handler;
    }

    /**
     * ????????????????????????
     *
     * @param handler ??????????????????
     */
    public void onCancel(Consumer<ActionEvent> handler) {
        cancelHandler = handler;
    }

    /**
     * ????????????
     *
     * @return ????????????
     * @throws ValidationException ?????????IFXValidatableControl?????????????????????????????????
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
     * ??????????????????
     *
     * @param formDataClass ??????????????????
     * @return ????????????
     */
    public T getFormData(@NotNull Class<T> formDataClass) {
        if (formDataClass == null) {
            throw new NullPointerException("FormDataClass is null");
        }
        // ??????????????????
        JSONObject json = new JSONObject();
        formFields.forEach(formField -> {
            String name = formField.getName();
            String value = formField.getValue();
            json.put(name, value);
        });
        return json.toJavaObject(formDataClass);
    }

    /**
     * ??????????????????
     *
     * @param formData ????????????
     */
    public void setFormData(@NotNull T formData) {
        if (formData == null) {
            throw new NullPointerException("FormData is null");
        }
        // ???????????????????????????????????????
        resetForm();
        // ??????????????????
        JSONObject object = JSON.parseObject(JSON.toJSONString(formData));
        formFields.forEach(formField -> {
            String value = object.getString(formField.getName());
            formField.setValue(value);
        });
    }

    /**
     * ????????????
     */
    public void resetForm() {
        formFields.forEach(field -> field.setValue(StrUtil.EMPTY));
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
