package org.group1418.easy.escm.core.system.pojo.fo;

import lombok.Data;
import org.group1418.easy.escm.common.validator.BigDecimalCheck;
import org.group1418.easy.escm.common.validator.StrCheck;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * 登录表单
 *
 * @author yq 2024/3/29 15:32
 */
@Data
public class LoginFo {
    /**
     * 客户端ID
     */
    @StrCheck(name = "客户端")
    private String clientId;
    /**
     * 授权类型
     */
    @StrCheck(name = "授权类型")
    private String grantType;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 验证码
     */
    private String code;

    /**
     * 唯一标识, 验证码id
     */
    private String uuid;

    @BigDecimalCheck(name = "测试",precision = 2,scale = 1)
    private BigDecimal a;

    @Valid
    private List<Entry> list;

    @Data
    public static class Entry{
        @StrCheck(name = "时间",type = StrCheck.StringType.DATE)
        private String c;
    }
}
