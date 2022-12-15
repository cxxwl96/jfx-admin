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

package com.cxxwl96.jfx.admin.client.common;

import com.cxxwl96.jfx.admin.client.component.JFXDecorator;
import com.jfoenix.assets.JFoenixResources;

import java.util.Optional;

import io.datafx.controller.context.ApplicationContext;
import io.datafx.controller.flow.FlowHandler;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.stage.Stage;

/**
 * 应用存储类
 *
 * @author cxxwl96
 * @since 2022/9/23 11:35
 */
public class ApplicationStore {
    private static final ApplicationContext JFX_APPLICATION_CONTEXT = ApplicationContext.getInstance();

    // 是否暗夜主题
    private static final SimpleBooleanProperty DARK_THEME = new SimpleBooleanProperty();

    static {
        DARK_THEME.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                ApplicationCache.getCache().setDarkTheme(newValue).flushCache();
            }
        });
    }

    private static final String APP_LIGHT = JFoenixResources.load("/assets/css/app-light.css").toExternalForm();

    private static final String LOGIN_LIGHT = JFoenixResources.load("/assets/css/login-light.css").toExternalForm();

    private static final String APP_DARK = JFoenixResources.load("/assets/css/app-dark.css").toExternalForm();

    private static final String LOGIN_DARK = JFoenixResources.load("/assets/css/login-dark.css").toExternalForm();

    /**
     * 获取注册的bean
     *
     * @param beanClazz 注册的bean
     * @param <T> 注册的bean的类型
     * @return 注册的bean
     */
    public static <T> Optional<T> getRegisterBean(Class<T> beanClazz) {
        return Optional.ofNullable(JFX_APPLICATION_CONTEXT.getRegisteredObject(beanClazz));
    }

    /**
     * 获取注册的bean
     *
     * @param beanName 注册的beanName
     * @param <T> 注册的bean的类型
     * @return 注册的bean
     */
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> getRegisterBean(String beanName) {
        return Optional.ofNullable(((T) JFX_APPLICATION_CONTEXT.getRegisteredObject(beanName)));
    }

    /**
     * 注册bean
     *
     * @param bean 需要注册的bean
     * @param <T> 注册的bean的类型
     */
    public static <T> void registerBean(T bean) {
        JFX_APPLICATION_CONTEXT.register(bean);
    }

    /**
     * 注册bean
     *
     * @param beanName beanName
     * @param bean 需要注册的bean
     * @param <T> 注册的bean的类型
     */
    public static <T> void registerBean(String beanName, T bean) {
        JFX_APPLICATION_CONTEXT.register(beanName, bean);
    }

    /**
     * 获取上下文JFXDecorator
     *
     * @return 上下文JFXDecorator
     */
    public static Optional<JFXDecorator> getDecorator() {
        final JFXDecorator decorator = (JFXDecorator) JFX_APPLICATION_CONTEXT.getRegisteredObject("Decorator");
        return Optional.ofNullable(decorator);
    }

    /**
     * 获取上下文RootStage
     *
     * @return 上下文RootStage
     */
    public static Optional<Stage> getRootStage() {
        final Stage stage = (Stage) JFX_APPLICATION_CONTEXT.getRegisteredObject("RootStage");
        return Optional.ofNullable(stage);
    }

    /**
     * 获取上下文FlowHandler
     *
     * @return 上下文FlowHandler
     */
    public static Optional<FlowHandler> getContextFlowHandler() {
        final FlowHandler contextFlowHandler = (FlowHandler) JFX_APPLICATION_CONTEXT.getRegisteredObject(
            "ContextFlowHandler");
        return Optional.ofNullable(contextFlowHandler);
    }

    public static String[] getDarkThemeResources() {
        return new String[] {APP_DARK, LOGIN_DARK};
    }

    public static String[] getLightThemeResources() {
        return new String[] {APP_LIGHT, LOGIN_LIGHT};
    }

    public static boolean getDarkTheme() {
        return DARK_THEME.get();
    }

    public static SimpleBooleanProperty darkThemeProperty() {
        return DARK_THEME;
    }

    public static void setDarkTheme(boolean darkTheme) {
        ApplicationStore.DARK_THEME.set(darkTheme);
    }
}
