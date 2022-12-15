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

package com.cxxwl96.jfx.admin.server.base.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 实体基类
 *
 * @author cxxwl96
 * @since 2021/6/18 22:07
 */

@Data
@Accessors(chain = true)
public class AllFieldBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String remark;

    @TableField(value = "create_time", insertStrategy = FieldStrategy.NOT_NULL, fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    @TableField(value = "update_time", updateStrategy = FieldStrategy.NOT_NULL, fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    @TableField(value = "update_by", fill = FieldFill.UPDATE)
    private String updateBy;

    @TableField(value = "enable", updateStrategy = FieldStrategy.NOT_NULL, fill = FieldFill.INSERT)
    @TableLogic(value = "1", delval = "0")
    private Boolean enable;

}
