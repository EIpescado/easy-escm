package org.group1418.easy.escm.core.system.pojo.to;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.group1418.easy.escm.common.base.obj.BaseTo;
import org.group1418.easy.escm.common.enums.system.UserStateEnum;

import java.time.LocalDateTime;

/**
 * @author yq
 * @date 2020/10/08 20:11
 * @description 用户To
 * @since V1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SystemUserTo extends BaseTo {

    private static final long serialVersionUID = -115023961674668038L;
    private Long id;

    private String username;

    private String nickname;

    private String phone;

    private String mail;

    @JSONField(format = "yyyy-MM-dd")
    private LocalDateTime dateCreated;

    private UserStateEnum state;

    /**
     * 是否为E链用户
     */
    private Boolean beEscmUser;

    /**
     * 是否为客户E链管理者
     */
    private Boolean beEscmMaster;

    /**
     * 是否接口用户
     */
    private Boolean beInterfaceUser;

    /**
     * 是否内部用户
     */
    private Boolean internalUser;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 是否授权邮箱
     */
    private Boolean whetherAuth;

    /**
     * 抄送邮箱
     */
    private String ccMail;
}
