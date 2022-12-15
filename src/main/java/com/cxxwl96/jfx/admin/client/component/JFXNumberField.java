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

package com.cxxwl96.jfx.admin.client.component;

import com.cxxwl96.jfx.admin.client.utils.IconUtil;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.base.ValidatorBase;

import java.util.function.Consumer;
import java.util.function.Function;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Getter;

/**
 * JFXNumberField
 *
 * @author cxxwl96
 * @since 2022/10/9 23:38
 */
public class JFXNumberField extends HBox {
    @Getter
    private final JFXTextField textField = new JFXTextField();

    private Label incrementBtn;

    private Label decrementBtn;

    private final SimpleBooleanProperty showBtn = new SimpleBooleanProperty(true);

    private final SimpleStringProperty promptText = new SimpleStringProperty();

    private final SimpleObjectProperty<NumberType> type = new SimpleObjectProperty<>(NumberType.Integer);

    private final SimpleDoubleProperty max = new SimpleDoubleProperty();

    private final SimpleDoubleProperty min = new SimpleDoubleProperty();

    private final SimpleDoubleProperty step = new SimpleDoubleProperty(1);

    private final ValidationControl validationControl = new ValidationControl(textField);

    public JFXNumberField() {
        initContent();
        initProperty();
        initEvent();
    }

    public enum NumberType {
        Integer,
        Double
    }

    private void initContent() {
        // right content
        Function<String, Label> createBtn = (icon) -> {
            double width = 30;
            double height = 11.5;
            Label button = new Label();
            button.getStyleClass().add("jfx-label-button");
            button.setMinWidth(width);
            button.setMinHeight(height);
            button.setMaxHeight(height);
            button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            button.setGraphicTextGap(0);
            button.setGraphic(IconUtil.getIcon(icon));
            button.setStyle("-fx-border-radius: 0; -fx-background-radius: 0; -fx-border-width: 0");
            button.setEffect(null);
            return button;
        };
        incrementBtn = createBtn.apply("caret-up");
        decrementBtn = createBtn.apply("caret-down");
        incrementBtn.managedProperty().bind(showBtn);
        incrementBtn.visibleProperty().bind(showBtn);
        decrementBtn.managedProperty().bind(showBtn);
        decrementBtn.visibleProperty().bind(showBtn);

        VBox rightContent = new VBox();
        rightContent.setMaxHeight(Double.MAX_VALUE);
        rightContent.setAlignment(Pos.CENTER);
        rightContent.getChildren().addAll(incrementBtn, decrementBtn);
        // left content
        super.setAlignment(Pos.CENTER);
        super.getChildren().addAll(textField, rightContent);
    }

    private void initProperty() {
        HBox.setHgrow(textField, Priority.ALWAYS);
        textField.promptTextProperty().bindBidirectional(promptText);
    }

    private void initEvent() {
        // 输入事件
        textField.textProperty().addListener(this::textFieldChangeHandler);
        // 按钮事件
        final Consumer<Double> setValue = (value) -> {
            final String text = getText();
            if (StrUtil.isBlank(text)) {
                setText("0");
                return;
            }
            if (!NumberUtil.isNumber(text)) {
                return;
            }
            final double fieldValue = NumberUtil.parseDouble(text) + value;
            switch (type.get()) {
                case Integer:
                    setText(String.valueOf((int) fieldValue));
                    break;
                case Double:
                    setText(String.valueOf(fieldValue));
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported number type: " + type.get().name());
            }
        };
        incrementBtn.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> setValue.accept(step.get()));
        decrementBtn.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> setValue.accept(-1 * step.get()));
    }

    private void textFieldChangeHandler(Observable observable, String oldValue, String newValue) {
        boolean supported;
        if (StrUtil.isBlank(newValue)) {
            return;
        }
        if (newValue.endsWith("f") || newValue.endsWith("d") || newValue.endsWith("l")) {
            this.textField.setText(oldValue);
            return;
        }
        if ("-".equals(newValue)) {
            return;
        }
        switch (type.get()) {
            case Integer:
                supported = NumberUtil.isInteger(newValue);
                break;
            case Double:
                if (newValue.endsWith(".") && newValue.indexOf(".") != newValue.lastIndexOf(".")) {
                    this.textField.setText(oldValue);
                    return;
                }
                supported = NumberUtil.isNumber(newValue);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported number type: " + type.get().name());
        }
        if (!supported) {
            this.textField.setText(oldValue);
        } else {
            this.textField.setText(newValue);
        }
    }

    public String getText() {
        return textField.getText();
    }

    public StringProperty textProperty() {
        return this.textField.textProperty();
    }

    public void setText(String text) {
        this.textField.setText(text);
    }

    public boolean isShowBtn() {
        return showBtn.get();
    }

    public SimpleBooleanProperty showBtnProperty() {
        return showBtn;
    }

    public void setShowBtn(boolean showBtn) {
        this.showBtn.set(showBtn);
    }

    public String getPromptText() {
        return promptText.get();
    }

    public SimpleStringProperty promptTextProperty() {
        return promptText;
    }

    public void setPromptText(String promptText) {
        this.promptText.set(promptText);
    }

    public NumberType getType() {
        return type.get();
    }

    public SimpleObjectProperty<NumberType> typeProperty() {
        return type;
    }

    public void setType(NumberType type) {
        this.type.set(type);
    }

    public double getMax() {
        return max.get();
    }

    public SimpleDoubleProperty maxProperty() {
        return max;
    }

    public void setMax(double max) {
        this.max.set(max);
    }

    public double getMin() {
        return min.get();
    }

    public SimpleDoubleProperty minProperty() {
        return min;
    }

    public void setMin(double min) {
        this.min.set(min);
    }

    public double getStep() {
        return step.get();
    }

    public SimpleDoubleProperty stepProperty() {
        return step;
    }

    public void setStep(double step) {
        this.step.set(step);
    }

    public ValidatorBase getActiveValidator() {
        return this.validationControl.getActiveValidator();
    }

    public ReadOnlyObjectProperty<ValidatorBase> activeValidatorProperty() {
        return this.validationControl.activeValidatorProperty();
    }

    public ObservableList<ValidatorBase> getValidators() {
        return this.validationControl.getValidators();
    }

    public void setValidators(ValidatorBase... validators) {
        this.validationControl.setValidators(validators);
    }

    public boolean validate() {
        return this.validationControl.validate();
    }

    public void resetValidation() {
        this.validationControl.resetValidation();
    }
}
