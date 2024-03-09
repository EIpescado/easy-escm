package org.group1418.easy.escm.core.system.pojo.fo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.group1418.easy.escm.common.enums.system.AbleStateEnum;

import java.io.Serializable;

/**
 * @author yq
 * @date 2021-04-22 15:09:29
 * @description system client Fo
 * @since V1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SystemClientFo implements Serializable {
    /**
     * 客户端标识
     */
    private String clientId;
    /**
     * 客户端密钥
     */
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
     * 备注
     */
    private String remark;
}
