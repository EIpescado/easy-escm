package org.group1418.easy.escm.common.handlers;

import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.group1418.easy.escm.common.enums.OrgEnum;

import java.lang.reflect.Field;

/**
 * @author yq 2023/6/13 17:43
 * @description OrgTypeHandler 组织 字段转化 type-handlers-package: com.hfy.cloud.common.handlers 自动转化
 */
@MappedTypes({OrgEnum.class})
@MappedJdbcTypes({JdbcType.VARCHAR})
public class OrgTypeHandler extends AbstractJsonTypeHandler<OrgEnum> {

    public OrgTypeHandler(Class<?> type) {
        super(type);
    }

    public OrgTypeHandler(Class<?> type, Field field) {
        super(type, field);
    }

    @Override
    public OrgEnum parse(String json) {
        return OrgEnum.parse(json);
    }

    @Override
    public String toJson(Object obj) {
        return obj != null ? ((OrgEnum) obj).name() : null;
    }

}
