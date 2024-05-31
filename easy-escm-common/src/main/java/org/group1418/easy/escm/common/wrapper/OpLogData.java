package org.group1418.easy.escm.common.wrapper;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.group1418.easy.escm.common.annotation.OpLog;
import org.group1418.easy.escm.common.exception.SystemCustomException;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志 数据
 * @author yq 2022/10/12 17:29
 */
@Data
public class OpLogData implements Serializable {
    private static final long serialVersionUID = -17388513313461536L;

    /**
     * 接口名称
     */
    private String name;

    /**
     * 接口耗时
     */
    private int timeCost;

    /**
     * 参数
     */
    private String params;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 租户ID
     */
    private Long tenantId;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;

    /**
     * 接口请求是否失败
     */
    private Boolean fail;

    /**
     * 用户客户端类型
     */
    private String clientType;

    /**
     * 响应内容
     */
    private String result;

    /**
     * 是否对外
     */
    private Boolean outer;

    public static OpLogData buildByAnnotation(OpLog opLog, int timeCost, boolean hadLogin, boolean fail) {
        OpLogData dto = new OpLogData();
        dto.setName(opLog.value());
        dto.setTimeCost(timeCost);
        if (hadLogin) {
            try {
//                CustomUserDetails customUserDetails = CustomUserDetails.currentDetails();
//                dto.setUserId(customUserDetails.getUserId());
//                dto.setCustomerId(customUserDetails.getCustomerId());
//                dto.setClientType(customUserDetails.getClientType().name());
            } catch (SystemCustomException customException) {
                //可能登陆过期 获取不到
            }
        }
        dto.setTime(LocalDateTime.now());
        dto.setFail(fail);
        dto.setOuter(opLog.outer());
        return dto;
    }
}
