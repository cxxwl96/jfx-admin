package com.cxxwl96.jfx.admin.server.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cxxwl96.jfx.admin.server.base.entity.TreeNode;
import com.cxxwl96.jfx.admin.server.enums.MenuType;
import com.cxxwl96.jfx.admin.server.enums.Status;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author cxxwl96
 * @since 2021-07-13
 */

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class Menu extends TreeNode<Menu> implements Serializable {

    private static final long serialVersionUID = 1L;

    private MenuType type = MenuType.DIRECTORY;

    private String permissions;

    private String title;

    private String icon;

    private Boolean main = false;

    private String resourceUrl;

    private Boolean hide = false;

    // 是否是http链接
    private Boolean httpUrl = false;

    // 如果是http链接，是否隐藏WebView的toolbar
    private Boolean hideToolbar = false;

    // 是否是外链
    private Boolean httpUrlBlank = false;

    private Status status = Status.ENABLE;

    @TableField(exist = false)
    private Menu parentMenu;

    /**
     * 目录校验分组
     */
    public interface DirectoryValidGroup {
    }

    /**
     * 菜单校验分组
     */
    public interface MenuValidGroup {
    }

    /**
     * 按钮校验分组
     */
    public interface ButtonValidGroup {
    }

}
