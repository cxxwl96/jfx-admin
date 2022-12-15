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

import com.cxxwl96.jfx.admin.client.common.JFXComponent;
import com.cxxwl96.jfx.admin.server.lang.Result;
import com.jfoenix.controls.base.IFXValidatableControl;
import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;

import org.kordamp.ikonli.javafx.FontIcon;

import javax.validation.ValidationException;

import cn.hutool.core.util.StrUtil;
import javafx.beans.DefaultProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * JFXFormField
 *
 * @author cxxwl96
 * @since 2022/10/26 21:37
 */
@Slf4j
@DefaultProperty("control")
public class JFXFormField extends JFXComponent {
    // 是否必填
    private final SimpleBooleanProperty require = new SimpleBooleanProperty();

    // label标题
    private final SimpleStringProperty title = new SimpleStringProperty();

    // help提示
    private final SimpleStringProperty helpText = new SimpleStringProperty();

    private final SimpleIntegerProperty rowIndex = new SimpleIntegerProperty(0);

    private final SimpleIntegerProperty columnIndex = new SimpleIntegerProperty(0);

    private final SimpleIntegerProperty rowSpan = new SimpleIntegerProperty(1);

    private final SimpleIntegerProperty columnSpan = new SimpleIntegerProperty(1);

    private final SimpleStringProperty name = new SimpleStringProperty();

    private final SimpleStringProperty value = new SimpleStringProperty();

    private final SimpleBooleanProperty show = new SimpleBooleanProperty(true);

    private final SimpleStringProperty pattern = new SimpleStringProperty();

    private final SimpleStringProperty message = new SimpleStringProperty();

    private AbstractFormControlFactory controlFactory;

    @Getter
    private Node control;

    public JFXFormField() {
        this(0, 0);
    }

    public JFXFormField(int rowIndex, int columnIndex) {
        setRowIndex(rowIndex);
        setColumnIndex(columnIndex);
    }

    /**
     * 字段校验
     *
     * @return 校验结果
     */
    public Result<String> validate() throws ValidationException {
        if (control instanceof IFXValidatableControl) {
            final IFXValidatableControl validatableControl = (IFXValidatableControl) control;
            final FontIcon icon = new FontIcon("fas-exclamation-triangle");
            if (isRequire()) {
                final RequiredFieldValidator validator = new RequiredFieldValidator();
                validator.setMessage(getTitle() + "是必须的");
                validator.setIcon(icon);
                validatableControl.getValidators().add(validator);
            }
            if (StrUtil.isNotBlank(getPattern())) {
                String message = this.getMessage();
                if (StrUtil.isBlank(message)) {
                    message = getTitle() + "格式错误, 不匹配" + getPattern();
                }
                final RegexValidator validator = new RegexValidator();
                validator.setMessage(message);
                validator.setIcon(icon);
                validator.setRegexPattern(getPattern());
                validatableControl.getValidators().add(validator);
            }
            if (!validatableControl.validate()) {
                final String message = validatableControl.getActiveValidator().getMessage();
                return Result.failed(message);
            }
        } else {
            final String value = getValue();
            if (isRequire() && StrUtil.isBlank(value)) {
                final String message = getTitle() + "是必须的";
                throw new ValidationException(message);
            }
            if (value != null && StrUtil.isNotBlank(getPattern()) && !value.matches(getPattern())) {
                String message = this.getMessage();
                if (StrUtil.isBlank(message)) {
                    message = getTitle() + "格式错误, 不匹配" + getPattern();
                }
                throw new ValidationException(message);
            }
        }
        return Result.success();
    }

    public boolean isRequire() {
        return require.get();
    }

    public SimpleBooleanProperty requireProperty() {
        return require;
    }

    public void setRequire(boolean require) {
        this.require.set(require);
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

    public String getHelpText() {
        return helpText.get();
    }

    public SimpleStringProperty helpTextProperty() {
        return helpText;
    }

    public void setHelpText(String helpText) {
        this.helpText.set(helpText);
    }

    public int getRowIndex() {
        return rowIndex.get();
    }

    public SimpleIntegerProperty rowIndexProperty() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex.set(rowIndex);
    }

    public int getColumnIndex() {
        return columnIndex.get();
    }

    public SimpleIntegerProperty columnIndexProperty() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex.set(columnIndex);
    }

    public int getRowSpan() {
        return rowSpan.get();
    }

    public SimpleIntegerProperty rowSpanProperty() {
        return rowSpan;
    }

    public void setRowSpan(int rowSpan) {
        this.rowSpan.set(rowSpan);
    }

    public int getColumnSpan() {
        return columnSpan.get();
    }

    public SimpleIntegerProperty columnSpanProperty() {
        return columnSpan;
    }

    public void setColumnSpan(int columnSpan) {
        this.columnSpan.set(columnSpan);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getValue() {
        return value.get();
    }

    public SimpleStringProperty valueProperty() {
        return value;
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    public boolean isShow() {
        return show.get();
    }

    public SimpleBooleanProperty showProperty() {
        return show;
    }

    public void setShow(boolean show) {
        this.show.set(show);
    }

    public String getPattern() {
        return pattern.get();
    }

    public SimpleStringProperty patternProperty() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern.set(pattern);
    }

    public String getMessage() {
        return message.get();
    }

    public SimpleStringProperty messageProperty() {
        return message;
    }

    public void setMessage(String message) {
        this.message.set(message);
    }

    public void setControlFactory(AbstractFormControlFactory controlFactory) {
        this.controlFactory = controlFactory;
        this.control = controlFactory.getControl(this.getTitle());
        if (this.control instanceof Region) {
            ((Region) this.control).setMaxWidth(Double.MAX_VALUE);
        }
        // 控件失去焦点事件
        this.control.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                try {
                    // 控件校验
                    validate();
                } catch (ValidationException exception) {
                    log.error("Failed to validate: {}", exception.getMessage(), exception);
                }
            }
        });
        // 字段值绑定控件工厂字段值
        this.value.bindBidirectional(this.controlFactory.valueProperty());
    }
}
