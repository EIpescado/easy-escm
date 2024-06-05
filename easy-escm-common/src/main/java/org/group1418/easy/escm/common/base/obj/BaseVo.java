package org.group1418.easy.escm.common.base.obj;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * base Vo 展示对象
 * @author yq
 * @date 2024年4月28日 15:50:09
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BaseVo implements Serializable {

    private static final long serialVersionUID = -3602840798016047720L;
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
    private Long createUserId;
    /**
     * 创建用户
     */
    private String createUser;

    /**
     * 最后修改用户ID
     */
    private Long updateUserId;
    /**
     * 最后修改用户
     */
    private String updateUser;

    /**
     * 租户ID
     */
    private Long tenantId;
}
