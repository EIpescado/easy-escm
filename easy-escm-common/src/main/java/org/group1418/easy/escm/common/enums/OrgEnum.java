package org.group1418.easy.escm.common.enums;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 组织枚举
 *
 * @author yq 2021年3月29日 14:49:18
 */
@Getter
public enum OrgEnum {
    HFY_2024("HF三三四四", "2024"),
    /**
     * 1418工作室
     */
    STUDIO_1418("1418工作室", "1418"),
    ;

    final String organizationName;
    final String companyId;

    OrgEnum(String organizationName, String companyId) {
        this.organizationName = organizationName;
        this.companyId = companyId;
    }

    public static OrgEnum parse(String org) {
        if (StrUtil.isBlank(org)) {
            return null;
        }
        return Arrays.stream(OrgEnum.values())
                .filter(oe -> oe.name().equalsIgnoreCase(org)
                        || oe.getCompanyId().equals(org)
                        || oe.getOrganizationName().equals(org)
                )
                .findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return organizationName;
    }
}
