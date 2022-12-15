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

package com.cxxwl96.jfx.admin.client.view.setting.user;

import com.cxxwl96.jfx.admin.client.common.AbstractController;
import com.cxxwl96.jfx.admin.client.component.form.JFXForm;
import com.cxxwl96.jfx.admin.client.utils.Alert;
import com.cxxwl96.jfx.admin.server.entity.User;
import com.cxxwl96.jfx.admin.server.service.impl.AuthServiceImpl;
import com.cxxwl96.jfx.admin.server.service.impl.UserServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import io.datafx.controller.ViewController;
import javafx.fxml.FXML;

/**
 * BaseSettingController
 *
 * @author cxxwl96
 * @since 2022/11/26 22:03
 */
@ViewController("/views/setting/user/BaseSetting.fxml")
public class BaseSettingController extends AbstractController {
    @FXML
    private JFXForm<User> form;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private AuthServiceImpl authService;

    @PostConstruct
    private void init() {
        // 设置表单约束
        form.initFormSchema(BaseFormSchema.class);
        // 提交API接口
        form.setApi(userService::saveOrUpdateUser, User.class);
        // 提交表单不清空表单
        form.setResetFormOnSubmit(false);
        // 获取当前用户信息
        authService.getCurrentAuthUser().successAndThen(result -> {
            // 设置表单数据
            form.setFormData(result.getData());
        }).failedAndThen((code, msg) -> Alert.error(msg));
    }
}
