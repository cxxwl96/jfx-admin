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

import com.jfoenix.controls.base.IFXValidatableControl;
import com.jfoenix.validation.base.ValidatorBase;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;

/**
 * ValidationControl
 *
 * @author cxxwl96
 * @since 2022/10/13 00:45
 */
class ValidationControl implements IFXValidatableControl {

    private final ReadOnlyObjectWrapper<ValidatorBase> activeValidator = new ReadOnlyObjectWrapper<>();

    private final Control control;

    public ValidationControl(Control control) {
        this.control = control;
    }

    @Override
    public ValidatorBase getActiveValidator() {
        return activeValidator.get();
    }

    @Override
    public ReadOnlyObjectProperty<ValidatorBase> activeValidatorProperty() {
        return this.activeValidator.getReadOnlyProperty();
    }

    ReadOnlyObjectWrapper<ValidatorBase> activeValidatorWritableProperty() {
        return this.activeValidator;
    }

    private final ObservableList<ValidatorBase> validators = FXCollections.observableArrayList();

    @Override
    public ObservableList<ValidatorBase> getValidators() {
        return validators;
    }

    @Override
    public void setValidators(ValidatorBase... validators) {
        this.validators.addAll(validators);
    }

    @Override
    public boolean validate() {
        for (ValidatorBase validator : validators) {
            // source control must be set to allow validators re-usability
            validator.setSrcControl(control);
            validator.validate();
            if (validator.getHasErrors()) {
                activeValidator.set(validator);
                return false;
            }
        }
        activeValidator.set(null);
        return true;
    }

    @Override
    public void resetValidation() {
        control.pseudoClassStateChanged(ValidatorBase.PSEUDO_CLASS_ERROR, false);
        activeValidator.set(null);
    }
}
