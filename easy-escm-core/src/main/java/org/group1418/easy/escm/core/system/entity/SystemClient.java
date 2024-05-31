package org.group1418.easy.escm.core.system.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.group1418.easy.escm.common.base.obj.BaseEntity;
import org.group1418.easy.escm.common.enums.system.AbleStateEnum;
import org.group1418.easy.escm.common.enums.system.UserStateEnum;
import org.group1418.easy.escm.common.validator.annotation.IntCheck;

import java.time.LocalDate;

/**
 * SystemClient 系统客户端,即本身系统授权的客户端
 * @author yq 2021/4/22 15:03
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SystemClient extends BaseEntity {

    private static final long serialVersionUID = -6236575072187759890L;
    /**
     * 客户端标识
     */
    @ExcelProperty(value = "clientId")
    private String clientId;
    /**
     * 客户端密钥
     */
    @ExcelProperty(value = "clientSecret")
    private String clientSecret;
    /**
     * 支持的授权类型,多个逗号相隔
     */
    private String grantType;
    /**
     * 设备类型
     */
    private String deviceType;
    /**
     * token 最低活跃频率（单位：秒），如果 token 超过此时间没有访问系统就会被冻结，默认-1 代表不限制，永不冻结
     */
    private Integer activeTimeout;
    /**
     * token过期时间
     */
    private Integer timeout;
    /**
     * 状态
     */
    private AbleStateEnum state;
    /**
     * 备注
     */
    private String remark;

    @TableField(exist = false)
    private UserStateEnum userState;

    @TableField(exist = false)
    private LocalDate hh = LocalDate.now();
}
