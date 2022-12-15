/*
 * Copyright (c) 2021-2021, jad (cxxwl96@sina.com).
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

package com.cxxwl96.jfx.admin.server.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cxxwl96.jfx.admin.server.base.entity.BaseEntity;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 字典数据实体
 *
 * @author cxxwl96
 * @since 2021/11/05 21:07
 */

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_data")
public class DictData extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "字典ID不能为空")
    private String dictId;

    @NotBlank(message = "名称不能为空")
    private String name;

    @NotBlank(message = "数据值不能为空")
    private String value;

    private int orderNo;

}
