package org.group1418.easy.escm.common.base.obj;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author yq
 * @date 2024年4月28日 15:50:09
 * @description base fo 表单对象
 * @since V1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BaseFo implements Serializable {

    private static final long serialVersionUID = 4721432549358764854L;
}
