package org.group1418.easy.escm.core.system.pojo.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.group1418.easy.escm.common.base.obj.BaseFo;
import org.group1418.easy.escm.common.enums.system.AbleStateEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
* 租户 Vo
* @author yq 2024-06-07
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class SystemTenantVo extends BaseFo {

    private static final long serialVersionUID = 1L;

    /**
    * 套餐ID
    */
    private Long packageId;

    /**
    * 租户状态
    */
    private AbleStateEnum state;

    /**
    * 租户备注
    */
    private String remark;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 用户数量（-1不限制）
     */
    private Long userLimit;
}
