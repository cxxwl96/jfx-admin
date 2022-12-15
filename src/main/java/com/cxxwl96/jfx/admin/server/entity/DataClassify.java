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
import com.cxxwl96.jfx.admin.server.base.entity.TreeNode;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 数据分类实体
 *
 * @author cxxwl96
 * @since 2021/11/12 23:21
 */

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("sys_data_classify")
public class DataClassify extends TreeNode<DataClassify> implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "标题不能为空")
    private String title;

}
