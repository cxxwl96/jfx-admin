package com.cxxwl96.jfx.admin.server.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cxxwl96.jfx.admin.server.base.entity.TreeNode;
import com.cxxwl96.jfx.admin.server.enums.Status;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
public class Dept extends TreeNode<Dept> implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "部门名称不能为空")
    private String name;

    private Status status = Status.ENABLE;

}
