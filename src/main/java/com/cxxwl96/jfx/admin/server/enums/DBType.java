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

package com.cxxwl96.jfx.admin.server.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 数据库类型 枚举
 *
 * @author cxxwl96
 * @since 2021/6/30 00:44
 */
public enum DBType {
    Oracle(0, "oracle.jdbc.driver.OracleDriver"),

    MySQL(1, "com.mysql.cj.jdbc.Driver"),

    DB2(2, "com.ibm.db2.app.DB2Driver"),

    PostgreSQL(3, "org.postgresql.Driver"),

    H2(4, "org.h2.Driver");

    private final String driver;

    @EnumValue
    @JsonValue
    private final int index;

    DBType(int index, String driver) {
        this.index = index;
        this.driver = driver;
    }

    public int getIndex() {
        return index;
    }

    public String getDriver() {
        return driver;
    }
}
