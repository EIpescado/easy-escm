package org.group1418.easy.escm.common.base.obj;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础Entity
 * @author yq
 * @date 2021年4月14日 10:42:01
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 6526236438185395534L;
    @TableId
    private Long id;

    /**
     * 逻辑删除标识
     */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Boolean enabled;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @ExcelProperty(value = "创建时间")
    @DateTimeFormat("yyyy-MM-dd HH")
    private LocalDateTime createTime;

    /**
     * 最后修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Version
    private Integer version;

    /**
     * 创建用户ID
     */
    @TableField(fill = FieldFill.INSERT)
    private Long creatorId;
    /**
     * 创建用户 名称
     */
    @TableField(fill = FieldFill.INSERT)
    private String creator;

    /**
     * 最后修改用户ID
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updaterId;
    /**
     * 最后修改用户 名称
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updater;

}
