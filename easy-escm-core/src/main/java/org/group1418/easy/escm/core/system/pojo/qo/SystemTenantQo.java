package org.group1418.easy.escm.core.system.pojo.qo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.group1418.easy.escm.common.base.obj.BasePageQo;
/**
* 租户 Qo
* @author yq  2024-06-07
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class SystemTenantQo extends BasePageQo {

    private static final long serialVersionUID = 1L;

    /**
    * 租户编码
    */
    private String code;

    /**
    * 套餐ID
    */
    private Long packageId;

    /**
    * 租户状态
    */
    private String state;

    /**
    * 租户备注
    */
    private String remark;
}
