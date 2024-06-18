package org.group1418.easy.escm.common.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 租户配置属性
 * @author yuqian 2024年6月17日 10:40:47
 */
@Data
@ConfigurationProperties(prefix = "easy.escm.tenant")
@Component
public class EasyEscmTenantProp {

    /**
     * 是否启用
     */
    private Boolean enable = true;

    /**
     * 排除表
     */
    private List<String> excludes;

}
