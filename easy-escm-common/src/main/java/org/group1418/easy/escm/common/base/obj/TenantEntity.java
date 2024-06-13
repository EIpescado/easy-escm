package org.group1418.easy.escm.common.base.obj;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 基础租户Entity
 *
 * @author yq
 * @date 2024年3月8日 14:36:47
 * @since V1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TenantEntity extends BaseEntity {

    private static final long serialVersionUID = -288721751309015311L;
    /**
     * 租户ID
     */
    private Long tenantId;

}
