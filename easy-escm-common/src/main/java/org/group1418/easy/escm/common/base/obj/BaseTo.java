package org.group1418.easy.escm.common.base.obj;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * base to 列表对象
 * @author yq
 * @date 2021年4月14日 10:42:01
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BaseTo implements Serializable {

    private static final long serialVersionUID = 6526236438185395534L;
    /**
     * ID
     */
    private Long id;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    /**
     * 版本
     */
    private Integer version;

    /**
     * 创建用户ID
     */
    private Long creatorId;
    /**
     * 创建用户
     */
    private String creator;

    /**
     * 最后修改用户ID
     */
    private Long updaterId;
    /**
     * 最后修改用户
     */
    private String updater;

    /**
     * 租户ID
     */
    private String tenantId;
}
