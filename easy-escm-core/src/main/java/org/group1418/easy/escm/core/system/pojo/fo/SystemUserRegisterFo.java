package org.group1418.easy.escm.core.system.pojo.fo;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import org.group1418.easy.escm.common.validator.StrCheck;
import org.group1418.easy.escm.common.deserializer.StrFull2HalfTrimReader;

import java.io.Serializable;

/**
 * @author yq 2021/5/12 15:57
 * @description SystemUserRegisterFo 用户注册表单
 */
@Data
public class SystemUserRegisterFo implements Serializable {
    private static final long serialVersionUID = 7358494245214936807L;

    /**
     * 统一社会信用代码
     */
    private String uniformSocialCreditCode;

    /**
     * 客户名称
     */
    @JSONField(deserializeUsing = StrFull2HalfTrimReader.class)
    @StrCheck(name = "客户名称")
    private String customerName;

    /**
     * 用户名
     */
    @JSONField(deserializeUsing = StrFull2HalfTrimReader.class)
    @StrCheck(name = "用户名")
    private String username;

    /***
     * 密码
     */
    @JSONField(deserializeUsing = StrFull2HalfTrimReader.class)
    @StrCheck(name = "密码")
    private String password;

    /**
     * 确认密码
     */
    @JSONField(deserializeUsing = StrFull2HalfTrimReader.class)
    @StrCheck(name = "确认密码")
    private String confirmPassword;

    /**
     * 手机
     */
    @JSONField(deserializeUsing = StrFull2HalfTrimReader.class)
    @StrCheck(name = "手机")
    private String phone;

    /**
     * 授权邮箱
     */
    @JSONField(deserializeUsing = StrFull2HalfTrimReader.class)
    @StrCheck(name = "邮箱")
    private String mail;

    /**
     * 是否提示商务注册
     */
    private Boolean tipBusiness = true;

    /**
     * 备注
     */
    private String remark;

}
