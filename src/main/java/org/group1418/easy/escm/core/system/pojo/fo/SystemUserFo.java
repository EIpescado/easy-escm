package org.group1418.easy.escm.core.system.pojo.fo;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * @author yq
 * @date 2020/10/13 16:17
 * @description 用户表单
 * @since V1.0.0
 */
@Data
public class SystemUserFo implements Serializable {
    private static final long serialVersionUID = 5435145935428948587L;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名必填")
    private String username;

    /**
     * 昵称
     */
    @NotBlank(message = "昵称必填")
    private String nickname;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    @Email(message = "邮箱无效")
    private String mail;

    /**
     * 角色ID集合
     */
    @NotEmpty(message = "角色必选")
    List<Long> roleIds;

}
